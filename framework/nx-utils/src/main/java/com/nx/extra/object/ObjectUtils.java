package com.nx.extra.object;

//import cn.hutool.core.util.ArrayUtil;
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.core.util.ReflectUtil;
//import com.nx.utils.ReflectUtils;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;
import java.util.Arrays;
//import java.util.Objects;
import java.util.function.Consumer;

/**
 * Object 工具类
 *
 * @author 芋道源码
 */
@Slf4j
public class ObjectUtils {

    /**
     * 复制对象，并忽略 Id 编号
     *
     * @param object 被复制对象
     * @param consumer 消费者，可以二次编辑被复制对象
     * @return 复制后的对象
     */
    public static <T> T cloneIgnoreId(T object, Consumer<T> consumer)  {
        T result = org.apache.commons.lang3.ObjectUtils.clone(object);
        // 忽略 id 编号
        Field field = null;
        try {
            field = object.getClass().getField("id");
            //Field field = ReflectUtil.getField(object.getClass(), "id");
            if (field != null) {
                field.setAccessible(true);
                //Field field = ReflectUtil.getField(object.getClass(), "id");
                field.set(object,null);
                //setFieldValue(result, field, null);
            }
        } catch (NoSuchFieldException e) {
            log.warn(e.getMessage());
            //throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            log.warn(e.getMessage());
            //throw new RuntimeException(e);
        }

        // 二次编辑
        if (result != null) {
            consumer.accept(result);
        }
        return result;
    }

    public static <T extends Comparable<T>> T max(T obj1, T obj2) {
        if (obj1 == null) {
            return obj2;
        }
        if (obj2 == null) {
            return obj1;
        }
        return obj1.compareTo(obj2) > 0 ? obj1 : obj2;
    }

    public static <T> T defaultIfNull(T... array) {
        for (T item : array) {
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    public static <T> boolean equalsAny(T obj, T... array) {
        return Arrays.asList(array).contains(obj);
    }

}
