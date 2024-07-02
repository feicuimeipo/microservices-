package com.nx.utils;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.beanutils.*;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import com.nx.utils.time.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ReflectionUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BeanUtils的等价类，只是将check exception改为uncheck exception
 * 
 * @company 念熹科技
 * @author nianxl
 * @email xlnian@163.com
 * @date 2018年4月19日
 */
public class BeanUtils {

	private static final String CLASS_PROP_NAME = "class";

	private static Map<String, Map<String, PropertyDescriptor>> cache = new ConcurrentHashMap<>();

	private static Map<String, List<String>> fieldCache = new HashMap<>();

	private static Logger logger = LoggerFactory.getLogger(BeanUtils.class);
	
	/**
	 * BeanUtil类型转换器
	 */
	public static ConvertUtilsBean convertUtilsBean = new ConvertUtilsBean();

	private static BeanUtilsBean beanUtilsBean = new BeanUtilsBean(
			convertUtilsBean, new PropertyUtilsBean());

	static {
		convertUtilsBean.register(new DateConverter(), Date.class);
		convertUtilsBean.register(new LongConverter(null), Long.class);
	}

	/**
	 * 可以用于判断 Map,Collection,String,Array,Long是否为空
	 *
	 * @param o
	 *            java.lang.Object.
	 * @return boolean.
	 */
	public static boolean isEmpty(Object o) {
		if (o == null)
			return true;
		if (o instanceof String) {
			if (((String) o).trim().length() == 0)
				return true;
		} else if (o instanceof Collection) {
			if (((Collection<?>) o).size() == 0)
				return true;
		} else if (o.getClass().isArray()) {
			if (((Object[]) o).length == 0)
				return true;
		} else if (o instanceof Map) {
			if (((Map<?, ?>) o).size() == 0)
				return true;
		}
		else if(o instanceof Serializable) {
			return ((Serializable)o).toString().trim().length()==0;
		}
		else if(o instanceof ArrayNode) {
			ArrayNode an = (ArrayNode)o;
			return an.isEmpty(null);
		}
		return false;
	}

	/**
	 * 可以用于判断 Map,Collection,String,Array是否不为空
	 * 
	 * @param o
	 * @return
	 */
	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

	/**
	 * 判断对象是否为数字
	 * 
	 * @param o
	 * @return
	 */
	public static boolean isNumber(Object o) {
		if (o == null)
			return false;
		if (o instanceof Number)
			return true;
		if (o instanceof String) {
			try {
				Double.parseDouble((String) o);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return false;
	}


	/**
	 * 根据指定的类名判定指定的类是否存在。
	 * 
	 * @param className
	 * @return
	 */
	public static boolean validClass(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static Map<String, Object> beanToMap(Object bean) {
		return beanToMap(bean, false);
	}
	public static Map<String, Object> beanToMap(Object bean,boolean recursive) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			Map<String, PropertyDescriptor> descriptors = getCachePropertyDescriptors(bean.getClass());
			for (PropertyDescriptor descriptor : descriptors.values()) {
				String propertyName = descriptor.getName();
				if(CLASS_PROP_NAME.equalsIgnoreCase(propertyName))continue;
				Method readMethod = descriptor.getReadMethod();
				Object result = readMethod.invoke(bean, new Object[0]);
				if (result != null) {
					if(isSimpleDataType(result) || result instanceof Iterable){
						returnMap.put(propertyName, result);
					}else {
						if(recursive){
							returnMap.put(propertyName, beanToMap(result,recursive));
						}else{
							returnMap.put(propertyName,result);
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}


		return returnMap;

	}

	/**
	 * 判断是否基本类型
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public static boolean isSimpleDataType(Object o) {
		return isSimpleDataType(o.getClass());
	}

	public static boolean isSimpleDataType(Class<?> clazz) {
		return
				(
						clazz.isPrimitive() ||
								clazz.equals(String.class) ||
								clazz.equals(Integer.class)||
								clazz.equals(Byte.class) ||
								clazz.equals(Long.class) ||
								clazz.equals(Double.class) ||
								clazz.equals(Float.class) ||
								clazz.equals(Character.class) ||
								clazz.equals(Short.class) ||
								clazz.equals(BigDecimal.class) ||
								clazz.equals(Boolean.class) ||
								clazz.equals(Date.class)
				);
	}

	private static  Map<String, PropertyDescriptor> getCachePropertyDescriptors(Class<?> clazz) throws IntrospectionException {
		String canonicalName = clazz.getCanonicalName();
		Map<String, PropertyDescriptor> map = cache.get(canonicalName);

		if (map == null) {
			map = doCacheClass(clazz, canonicalName);
		}

		return map;
	}

	/**
	 * @param clazz
	 * @param canonicalName
	 * @return
	 * @throws IntrospectionException
	 */
	private synchronized static Map<String, PropertyDescriptor> doCacheClass(Class<?> clazz, String canonicalName)
			throws IntrospectionException {
		if(cache.containsKey(canonicalName))return cache.get(canonicalName);

		Map<String, PropertyDescriptor> map = new ConcurrentHashMap<>();

		List<String> fieldNames = new ArrayList<>();

		BeanInfo srcBeanInfo = Introspector.getBeanInfo(clazz);

		PropertyDescriptor[] descriptors = srcBeanInfo.getPropertyDescriptors();
		for (PropertyDescriptor descriptor : descriptors) {

			if("class".equals(descriptor.getName()))continue;
			if("serialVersionUID".equals(descriptor.getName()))continue;

			fieldNames.add(descriptor.getName());

			Method readMethod = descriptor.getReadMethod();
			Method writeMethod = descriptor.getWriteMethod();

			String name = descriptor.getName();

			if (readMethod == null)
				try {
					readMethod = clazz.getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));

					descriptor.setReadMethod(readMethod);
				} catch (NoSuchMethodException | SecurityException e) {
				}

			if (writeMethod == null)
				try {
					writeMethod = clazz.getMethod("set" + name.substring(0, 1).toUpperCase() + name.substring(1), descriptor.getPropertyType());

					descriptor.setWriteMethod(writeMethod);
				} catch (NoSuchMethodException | SecurityException e) {
				}

			if (readMethod != null && writeMethod != null) {
				map.put(descriptor.getName(), descriptor);
			}
		}

		cache.put(canonicalName, map);
		fieldCache.put(canonicalName, fieldNames);
		return map;
	}

	/**
	 * 判定类是否继承自父类
	 * 
	 * @param cls
	 *            子类
	 * @param parentClass
	 *            父类
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean isInherit(Class cls, Class parentClass) {
		return parentClass.isAssignableFrom(cls);
	}

	/**
	 * 输入基类包名，扫描其下的类，返回类的全路径
	 * 
	 * @param basePackages
	 *            如：com.hotent
	 * @return
	 * @throws IllegalArgumentException
	 */
	@SuppressWarnings("all")
	public static List<String> scanPackages(String basePackages)
			throws IllegalArgumentException {

		ResourcePatternResolver rl = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
				rl);
		List result = new ArrayList();
		String[] arrayPackages = basePackages.split(",");
		try {
			for (int j = 0; j < arrayPackages.length; j++) {
				String packageToScan = arrayPackages[j];
				String packagePart = packageToScan.replace('.', '/');
				String classPattern = "classpath*:/" + packagePart
						+ "/**/*.class";
				Resource[] resources = rl.getResources(classPattern);
				for (int i = 0; i < resources.length; i++) {
					Resource resource = resources[i];
					MetadataReader metadataReader = metadataReaderFactory
							.getMetadataReader(resource);
					String className = metadataReader.getClassMetadata()
							.getClassName();
					result.add(className);
				}
			}
		} catch (Exception e) {
			new IllegalArgumentException("scan pakcage class error,pakcages:"
					+ basePackages);
		}

		return result;
	}

	/**
	 * java反射访问私有成员变量的值。
	 * 
	 * @param instance
	 * @param fieldName
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 *             Object
	 */
	public static Object getValue(Object instance, String fieldName)
			throws IllegalAccessException, NoSuchFieldException {

		Field field = getField(instance.getClass(), fieldName);
		// 参数值为true，禁用访问控制检查
		field.setAccessible(true);
		return field.get(instance);
	}

	/**
	 * 将字符串数据按照指定的类型进行转换。
	 * 
	 * @param typeName
	 *            实际的数据类型
	 * @param value
	 *            字符串值。
	 * @return Object
	 */
	public static Object convertByActType(String typeName, String value) {
		Object o = null;
		if (typeName.equals("int")) {
			o = Integer.parseInt(value);
		} else if (typeName.equals("short")) {
			o = Short.parseShort(value);
		} else if (typeName.equals("long")) {
			o = Long.parseLong(value);
		} else if (typeName.equals("float")) {
			o = Float.parseFloat(value);
		} else if (typeName.equals("double")) {
			o = Double.parseDouble(value);
		} else if (typeName.equals("boolean")) {
			o = Boolean.parseBoolean(value);
		} else if (typeName.equals("java.lang.String")) {
			o = value;
		} else {
			o = value;
		}
		return o;
	}

	/**
	 * 根据类和成员变量名称获取成员变量。
	 * 
	 * @param thisClass
	 * @param fieldName
	 * @return
	 * @throws NoSuchFieldException
	 *             Field
	 */
	public static Field getField(Class<?> thisClass, String fieldName)
			throws NoSuchFieldException {

		if (fieldName == null) {
			throw new NoSuchFieldException("Error field !");
		}

		Field field = thisClass.getDeclaredField(fieldName);
		return field;
	}

	/**
	 * 合并两个对象。
	 * 
	 * @param srcObj
	 * @param desObj
	 *            void
	 */
	public static void mergeObject(Object srcObj, Object desObj) {
		if (srcObj == null || desObj == null)
			return;

		Field[] fs1 = srcObj.getClass().getDeclaredFields();
		Field[] fs2 = desObj.getClass().getDeclaredFields();
		for (int i = 0; i < fs1.length; i++) {
			try {
				fs1[i].setAccessible(true);
				Object value = fs1[i].get(srcObj);
				fs1[i].setAccessible(false);
				if (null != value) {
					fs2[i].setAccessible(true);
					fs2[i].set(desObj, value);
					fs2[i].setAccessible(false);
				}
			} catch (Exception e) {
				logger.error("mergeObject" + e.getMessage());
			}
		}
	}

	/**
	 * 数据列表去重
	 * @param list
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void removeDuplicate(List list)  {
	    HashSet h = new HashSet(list);
	    list.clear();
	    list.addAll(h);
	}


	
	/**
	 * 拷贝一个bean中的非空属性于另一个bean中
	 * 
	 * @param dest
	 *            目标对象
	 * @param orig
	 *            源对象
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void copyNotNullProperties(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {

		// Validate existence of the specified beans
		if (dest == null) {
			logger.error("No destination bean specified");
			return;
		}
		if (orig == null) {
			logger.error("No origin bean specified");
			return;
		}

			// Copy the properties, converting as necessary
		if (orig instanceof DynaBean) {
			DynaProperty[] origDescriptors = ((DynaBean) orig)
					.getDynaClass().getDynaProperties();
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				if (beanUtilsBean.getPropertyUtils().isReadable(orig, name)
						&& beanUtilsBean.getPropertyUtils().isWriteable(
								dest, name)) {
					Object value = ((DynaBean) orig).get(name);
					beanUtilsBean.copyProperty(dest, name, value);
				}
			}
		} else if (orig instanceof Map) {
			Iterator<?> entries = ((Map<?, ?>) orig).entrySet().iterator();
			while (entries.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry) entries.next();
				String name = (String) entry.getKey();
				if (beanUtilsBean.getPropertyUtils()
						.isWriteable(dest, name)) {
					beanUtilsBean
							.copyProperty(dest, name, entry.getValue());
				}
			}
		} else /* if (orig is a standard JavaBean) */{
			PropertyDescriptor[] origDescriptors = beanUtilsBean
					.getPropertyUtils().getPropertyDescriptors(orig);
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				if ("class".equals(name)) {
					continue; // No point in trying to set an object's class
				}
				if (beanUtilsBean.getPropertyUtils().isReadable(orig, name)
						&& beanUtilsBean.getPropertyUtils().isWriteable(
								dest, name)) {
					try {
						Object value = beanUtilsBean.getPropertyUtils()
								.getSimpleProperty(orig, name);
						if (value != null) {
							beanUtilsBean.copyProperty(dest, name, value);
						}
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}
	
	/**
	 * 克隆对象
	 * 
	 * @param bean
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public static Object cloneBean(Object bean) throws Exception {
			return beanUtilsBean.cloneBean(bean);
	}
	
	/**
	 * 通过反射设置对象的值。
	 * 
	 * @param bean
	 * @param name
	 * @param value
	 *            void
	 */
	public static void setProperty(Object bean, String name, Object value) {
		try {
			beanUtilsBean.setProperty(bean, name, value);
		} catch (Exception e) {
			handleReflectionException(e);
		}
	}
	
	private static void handleReflectionException(Exception e) {
		ReflectionUtils.handleReflectionException(e);
	}
	public static boolean isEquals(Object  newVal, Object oldVal)  {
		if (newVal == oldVal || (isEmpty(newVal) && isEmpty(oldVal))) {
			return true;
		}else if ((isEmpty(newVal) && isNotEmpty(oldVal)) || (isNotEmpty(newVal) && isEmpty(oldVal))) {
			return false;
		}else if (!ObjectToString(newVal).equals(ObjectToString(oldVal))) {
			return false;
		}
	    return true;
	}
	

	
	public static String ObjectToString(Object  val)  {
		if (BeanUtils.isEmpty(val)) {
			return "";
		}else if (val instanceof String) {
			return (String)val; 
		}else if (isNumber(val)) {
			DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
			return decimalFormat.format(val); 
		}else if ((val instanceof LocalDateTime)) {
			return TimeUtil.getDateTimeString((LocalDateTime)val);
		}else if ((val instanceof LocalDate)) {
			LocalDate date =(LocalDate)val;
			return TimeUtil.getDateString(date.atTime(0, 0, 0));
		}else {
			return JsonUtil.toJson(val);
		}
	}
}
