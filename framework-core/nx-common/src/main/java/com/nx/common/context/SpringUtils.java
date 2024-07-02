package com.nx.common.context;

import com.nx.common.exception.BaseException;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import java.io.File;
import java.util.*;


/**
 *
 * 梳适用于初始化之后的Spring环境
 * org.nianxi.boot.support.AppUtil
 * 获取上下文bean
 * 
 * @作者：nianxiaoling
 * @邮箱：xlnian@163.com
 * @创建时间：2018年4月3日
 */
@Configuration
@Primary
public class SpringUtils implements ApplicationContextAware {
	protected static final Logger LOGGER = LoggerFactory.getLogger(SpringUtils.class);

	private  static ApplicationContext context;

	public static ApplicationContext getContext() {
		return SpringUtils.context;
	}

	public static ApplicationContext getContextAndNullCheck() {
		if (SpringUtils.context==null || SpringUtils.getContext().getEnvironment()==null){
			throw new BaseException("ApplicationContext为空或Environment为空！");
		}
		return SpringUtils.context;
	}

    public static void setContext(ApplicationContext _context) {
		context = _context;
		if (context!=null && context.getEnvironment()!=null){
			Env.environment=context.getEnvironment();
		}
    }

    @Override
	public void setApplicationContext(ApplicationContext _context) throws BeansException {
		if (context==null) {
			context = _context;
		}
		if (context != null && context.getEnvironment() != null) {
			Env.environment = context.getEnvironment();
		}
	}

	public static void mergeProperties(Properties props) {
		if (props==null) return ;
		ResourceUtils.getAllProperties().putAll(props);
	}


	public static <T> Map<String, T>  getBeansOfType(Class<T> clazz) {
		if (context==null){
			//LOGGER.warn("context is null!");
			return null;
		}
		return context.getBeansOfType(clazz);
	}

	/**
	 * 根据beanId获取配置在系统中的对象实例。
	 *
	 * @param beanId
	 * @return Object
	 * @exception
	 * @since 1.0.0
	 */
	public static Object getBean(String beanId) {

		if (context==null){
			//LOGGER.warn("context is null!");
			return null;
		}
		try{
			return context.getBean(beanId);
		}
		catch(Exception ex){
			LOGGER.debug("getBean:" + beanId +"," + ex.getMessage());
		}
		return null;
	}

	/**
	 * 获取Spring容器的Bean
	 *
	 * @param beanClass
	 * @return T
	 * @exception
	 * @since 1.0.0
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> beanClass) {
		if (context==null){
			//LOGGER.warn("context is null!");
			return null;
		}

		 T  bean = null;
		try{
			  bean= context.getBean(beanClass);
		}
		catch(Exception ex){
            try {
                if ( beanClass !=null) {
                    String beanName= beanClass.getSimpleName();
                    String beanId = beanName.substring(0, 1).toLowerCase()+beanName.substring(1);
                    bean = (T) context.getBean(beanId);
                }
            } catch (BeansException e) {
            	LOGGER.debug("getBean:" + beanClass +"," + ex.getMessage());
            }
        }
		return bean;
	}

	/**
	 * 根据指定的接口或基类获取实现类列表。
	 * @param clazz
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Class> getImplClass(Class clazz) throws ClassNotFoundException{
		if (context==null){
			//LOGGER.warn("context is null!");
			return null;
		}

		List<Class> list=new ArrayList<Class>();

		Map map= context.getBeansOfType(clazz);
		for(Object obj : map.values()){
			String name=obj.getClass().getName();
			int pos=name.indexOf("$$");
			if(pos>0){
				name=name.substring(0,name.indexOf("$$")) ;
			}
			Class cls= Class.forName(name);

			list.add(cls);
		}
		return list;
	}



	/**
	 * 获取接口的实现类实例。
	 * @param clazz
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String,Object> getImplInstance(Class clazz) throws ClassNotFoundException{
		if (context==null){
			//LOGGER.warn("context is null!");
			return null;
		}

		Map map= context.getBeansOfType(clazz);
		return map;
	}

	/**
	 * 发布事件。
	 * @param event
	 * void
	 */
	public static void publishEvent(ApplicationEvent event){

		if(context!=null){
			context.publishEvent(event);
		}
	}

	/**
	 * 发布事件
	 * @param var
	 */
	public static void publishEvent(Object var){
		if(context!=null){
			context.publishEvent(var);
		}
	}

	/**
	 * 获取Classpath物理路径
	 * @return
	 */
	public static String getClasspath(){
		 String classPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		 String rootPath  = "";
		  //windows下
		  if("\\".equals(File.separator)){
		   rootPath  = classPath.substring(1);
		   rootPath = rootPath.replace("/", "\\");
		  }
		  //linux下
		  if("/".equals(File.separator)){
		   rootPath  = classPath.substring(1);
		   rootPath = rootPath.replace("\\", "/");
		  }
		  return rootPath;
	}





	public static class Env {
		//spring完成初始化后在环境变量
		@Getter
		@Setter
		private static Environment environment;

		private static <T> T getPropertiesValue(String key,Class<T> tClass,T defaultValue){
			if (getEnvironment()!=null){
				T v = environment.getProperty(key,tClass);
				return v==null? defaultValue:v;
			}else if(ResourceUtils.getAllProperties()!=null){
				if (tClass.getSimpleName().equals(Map.class.getSimpleName())){
					Map map = ResourceUtils.getMappingValues(key);
					return map!=null? (T) map :defaultValue;
				}else if(tClass.getSimpleName().equals(List.class.getSimpleName())){
					List list = ResourceUtils.getList(key);
					return list!=null? (T) list :defaultValue;
				}else{
					Object obj = ResourceUtils.getAllProperties().getOrDefault(key,defaultValue);
					return (T) obj;
				}
			}
			boolean isNull = getEnvironment()==null && ResourceUtils.getAllProperties()==null;
			if (isNull && defaultValue==null){
				throw new RuntimeException("environment 或 bootstrapProperties为空！请先初如化！");
			}
			return defaultValue;
		}


		public static String getProperty(String key){
			return getPropertiesValue(key,String.class,null);
		}

		public static String getProperty(String key,String defaultValue){
			return getPropertiesValue(key,String.class,defaultValue);
		}

		public static <T> T getProperty(String key,Class<T> tClass, T defaultValue){
			return getPropertiesValue(key,tClass,defaultValue);

		}

		public static int getInt(String key,int defaultValue){
			String value = getPropertiesValue(key,String.class,null);	;
			return !isBlank(value)?Integer.parseInt(value):defaultValue;
		}

		public static boolean getBoolean(String key,boolean defaultValue){
			String value = getPropertiesValue(key,String.class,null);	;
			return !isBlank(value)?Boolean.valueOf(value):defaultValue;
		}

		public static long getLong(String key,long defaultValue){
			String value = getPropertiesValue(key,String.class,null);	;
			return !isBlank(value)?Long.parseLong(value):defaultValue;
		}

		public static boolean containsProperty(String key){
			if (getEnvironment()!=null){
				return environment.containsProperty(key);
			}else if(ResourceUtils.getAllProperties()!=null){
				return ResourceUtils.getAllProperties().containsKey(key);
			}
			boolean isNull = getEnvironment()==null && ResourceUtils.getAllProperties()==null;
			if (isNull){
				throw new RuntimeException("environment 或 bootstrapProperties为空！请先初如化！");
			}
			return false;
		}


		public static boolean containsAnyProperty(String... keys){
			for (String key : keys) {
				if (containsProperty(key)){
					return true;
				}
			}
			return false;
		}

		public static Map<String, String> getMappingValues(String prefix){
			Map<String,String> result = getPropertiesValue(prefix,Map.class,null);
			return result;
		}

		public static Properties getAllProperties() {
			return getAllProperties(null);
		}

		/**
		 * 按前缀匹配配置列表
		 * @param prefix
		 * @return
		 */
		public static Properties getAllProperties(String prefix) {
			Map<String, String> map = getMappingValues(prefix);
			Properties properties = new Properties();

			for (String key : map.keySet()) {
				properties.setProperty(key,map.get(key));
			}
			return properties;
		}


		public static String getAnyProperty(String... keys) {
			String value;
			for (String key : keys) {
				value = getProperty(key);
				if(value != null)return value;
			}
			return null;
		}


		public static List<String> getList(String key){
			String value = getProperty(key);
			if(isBlank(value))return new ArrayList<>(0);
			StringTokenizer st = new StringTokenizer(value, ResourceUtils.CONFIG_DELIMITERS);
			List<String> tokens = new ArrayList<>();
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if(!isBlank(token)) {
					tokens.add(token);
				}
			}
			return tokens;
		}

		private static boolean isBlank(String traceId){
			return traceId ==null || traceId.trim().length()==0;
		}
	}



}
