package com.nx.extra.collection;

import org.apache.commons.collections4.CollectionUtils;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.nx.extra.collection.CollectionUtils.convertList;
/**
 * Array 工具类
 *
 * @author 芋道源码
 */
public class ArrayUtils {

    /**
     * 将 object 和 newElements 合并成一个数组
     *
     * @param object 对象
     * @param newElements 数组
     * @param <T> 泛型
     * @return 结果数组
     */
    @SafeVarargs
    public static <T> Consumer<T>[] append(Consumer<T> object, Consumer<T>... newElements) {
        if (object == null) {
            return newElements;
        }

        Consumer<T>[] result = new Consumer[newElements.length+1];
        //Consumer<T>[] result =  ArrayUtil.newArray(Consumer.class, 1 + newElements.length);
        result[0] = object;
        System.arraycopy(newElements, 0, result, 1, newElements.length);
        return result;
    }

    public static <T, V> V[] toArray(Collection<T> from, Function<T, V> mapper) {
        return toArray(convertList(from, mapper));
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<T> from) {
        if (CollectionUtils.isEmpty(from)) {
            return (T[]) (new Object[0]);
        }

        T[] t = (T[]) (new Object[from.size()]);

        t = from.toArray(t);
        return t;

        //return org.apache.commons.lang3.ArrayUtils.toArray(from, (Class<T>) com.nx.extra.collection.CollectionUtils.containsAny(from.iterator()));
    }

    public static <T> T get(T[] array, int index) {
        if (null == array || index >= array.length) {
            return null;
        }
        return array[index];
    }

}
