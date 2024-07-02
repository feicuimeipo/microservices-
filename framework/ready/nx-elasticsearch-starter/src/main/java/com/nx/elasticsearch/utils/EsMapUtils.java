package com.nx.elasticsearch.utils;


import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created by yymofang on 18/5/10.
 */
public class EsMapUtils {

    public static void copyTo(Map from, Map to, String... val) {
        for (String str : val) {
            to.put(str, from.get(str));
        }
    }

    public static boolean isEmpty(Map map) {
        return (map == null || map.isEmpty());
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }


    public static void put(Map map, Object... val) {
        for (int i = 0; i < val.length; i = i + 2) {
            map.put(val[i], val[i + 1]);
        }
    }

     public static void putOrAdd(Map map, Number key, Long val) {
        Long nub = (Long) map.getOrDefault(key, 0L);
        map.put(key, nub + val);
    }

    public static void putOrAdd(Map map, Number key, String val) {
        String str = (String) map.getOrDefault(key, "");
        map.put(key, str + val);
    }

    public static void putOrAdd(Map map, String key, Long val) {
        Long nub = (Long) map.getOrDefault(key, 0L);
        map.put(key, nub + val);
    }

    public static void putOrAdd(Map map, String key, String val) {
        String str = (String) map.getOrDefault(key, "");
        map.put(key, str + val);
    }

    public static void putOrAdd(Map map, String key, Integer val) {
        Integer nub = (Integer) map.getOrDefault(key, 0);
        map.put(key, nub + val);
    }

    public static void putOrAdd(Map map, String key, Double val) {
        Double nub = toDouble(map.get(key));
        map.put(key, nub + val);
    }

    public static void putOrAdd(Map map, String key, BigDecimal val) {
        BigDecimal nub = toBigDecimal(map.get(key));
        map.put(key, val.add(nub));
    }

    public static <T> void putOrAdd(Map map, String key, List<T> val) {
        List<T> list = (List) map.get(key);
        if (list == null) {
            list = new ArrayList<T>();
        }
        list.addAll(Optional.ofNullable(val).orElse(new ArrayList<>(0)));
        map.put(key, list);
    }

    public static <T> void putOrAdd(Map map, String key, Set<T> val) {
        Set<T> set = (Set) map.get(key);
        if (set == null) {
            set = new HashSet<>();
        }
        set.addAll(Optional.ofNullable(val).orElse(new HashSet<>(0)));
        map.put(key, set);
    }

    public static void putSet(Map map, String key, String val) {
        Set<String> nub = getSetString(map, key);
        if (StringUtils.isNotEmpty(val)) {
            nub.add(val);
        }
        map.put(key, nub);
    }

    public static void putIntoSet(Set<String> set, Map map, String... key) {
        for (String s : key) {
            String val = getString(map, s);
            if (StringUtils.isNotEmpty(val)) {
                set.add(val);
            }
        }
    }

    public static void putListIntoSet(Set<String> set, Map map, String... key) {
        for (String s : key) {
            List<String> val = getListString(map, s);
            if (CollectionUtils.isNotEmpty(val)) {
                set.addAll(val);
            }
        }
    }
//
//    public static void putSet(Map map, String key, List<String> val) {
//        Set<String> nub = getSetString(map, key);
//        if (val != null && val.size() > 0) {
//            nub.addAll(val);
//        }
//        map.put(key, nub);
//    }

    public static Boolean contains(Map map, String content, String... key) {
        for (String s : key) {
            String val = (String) map.get(s);
            if (StringUtils.isEmpty(val)) {
                continue;
            }
            if (val.contains(content)) {
                return true;
            }
        }
        return false;
    }

//    public static void putIfNotNull(Map<String, Object> source, Map<String, Object> target, String... excludeField) {
//        for (Map.Entry<String, Object> item : source.entrySet()) {
//            if (excludeField != null) {
//                if (ArrayUtils.contains(excludeField, item.getKey())) {
//                    continue;
//                }
//            }
//            if (item.getValue() != null && StringUtils.isNotEmpty(item.getValue().toString())) {
//                target.put(item.getKey(), item.getValue());
//            }
//
//        }
//    }
//
//    public static void putIfNotNull(Map<String, Object> map, String key, Object obj) {
//        if (map == null) {
//            map = new HashMap<>();
//        }
//        if (obj != null) {
//            map.put(key, obj);
//        }
//    }
//
//    public static Boolean valEquals(Map<String, Object> source, Map<String, Object> target, String... excludeField) {
//        for (Map.Entry<String, Object> item : source.entrySet()) {
//            if (excludeField != null) {
//                if (ArrayUtils.contains(excludeField, item.getKey())) {
//                    continue;
//                }
//            }
//            if (!item.getValue().equals(target.get(item.getKey()))) {
//                return false;
//            }
//        }
//        return true;
//    }

//    public static <K, V> Boolean valEquals(Map<K, V> source, Map<K, V> target, K... excludeField) {
//        for (Map.Entry<K, V> item : source.entrySet()) {
//            if (excludeField != null) {
//                if (ArrayUtils.contains(excludeField, item.getKey())) {
//                    continue;
//                }
//            }
//            if (item.getValue() == null && target.get(item.getKey()) == null) {
//                return true;
//            }
//            if (!item.getValue().equals(target.get(item.getKey()))) {
//                return false;
//            }
//        }
//        return true;
//    }

    public static BigDecimal toBigDecimal(Object val) {
        return BigDecimal.valueOf(toDouble(val));
    }
    public static Double toDouble(Object val) {
        if (val == null) {
            return 0D;
        }
        try {
            return Double.valueOf(trim(val.toString()));
        } catch (Exception e) {
            return 0D;
        }
    }
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * @Description: 判断两个map是否相等，忽略数组和Collection的元素顺序,忽略数值型类型
     * @Param: source
     * @Param: target
     * @Param: includeField
     * @Return: java.lang.Boolean
     * @Date: 2021/03/10
     **/
    @SafeVarargs
    public static <K, V> Boolean valEqualsInclude(Map<K, V> source, Map<K, V> target, K... includeField) {
        if (ArrayUtils.isNotEmpty(includeField)) {
            if (isEmpty(source) || isEmpty(target)) {
                return false;
            }
            for (K k : includeField) {
                if (source.containsKey(k) && target.containsKey(k)) {
                    if (source.get(k) instanceof Number) {
                        if (toBigDecimal(source.get(k)).compareTo(toBigDecimal(target.get(k))) != 0) {
                            return false;
                        }
                    } else if (source.get(k) != null && target.get(k) != null && source.get(k).getClass() != target.get(k).getClass()) {
                        return false;
                    } else if (source.get(k) instanceof Collection) {
                        Collection collection1 = Optional.ofNullable((Collection) source.get(k)).orElse(new ArrayList<>(0));
                        Collection collection2 = Optional.ofNullable((Collection) target.get(k)).orElse(new ArrayList<>(0));
                        return CollectionUtils.isEqualCollection(collection1, collection2);
                    } else if (source.get(k) != null && source.get(k).getClass().isArray()) {
                        Object[] array1 = Optional.ofNullable((Object[]) source.get(k)).orElse(new Object[0]);
                        Object[] array2 = Optional.ofNullable((Object[]) target.get(k)).orElse(new Object[0]);
                        Arrays.sort(array1);
                        Arrays.sort(array2);
                        return ArrayUtils.isEquals(array1, array2);
                    } else if (!Objects.equals(source.get(k), target.get(k))) {
                        return false;
                    }
                } else if (!source.containsKey(k) || !target.containsKey(k)) {
                    if (!Objects.equals(source.get(k), target.get(k))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

//    public static Boolean compareFields(Map<String, Object> source, Map<String, Object> target, String... fields) {
//        if (ArrayUtils.isNotEmpty(fields)) {
//            for (String k : fields) {
//                if (source.get(k) == null && target.get(k) == null) {
//                    return true;
//                } else if (source.get(k) == null || target.get(k) == null) {
//                    return false;
//                } else if (source.get(k) instanceof List) {
//                    return CollectionUtils.compareList((List) source.get(k), (List) target.get(k));
//                } else if (!source.get(k).equals(target.get(k))) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }

    /**
     * @Description: 获取两个map不同的key，忽略数组和Collection的元素顺序，忽略数值型类型
     * @Param: map1
     * @Param: map2
     * @Return: java.util.List<K> 不同的key的集合
     * @Date: 2021/03/10
     **/
    @SafeVarargs
    public static <K, V> List<K> getDifferences(Map<K, V> map1, Map<K, V> map2, K... ignore) {
        List<K> result = new ArrayList<>();
        if (isNotEmpty(map1) || isNotEmpty(map2)) {
            Map<K, V> m1 = Optional.ofNullable(deepCopy(map1)).orElse(new HashMap<>(0));
            Map<K, V> m2 = Optional.ofNullable(deepCopy(map2)).orElse(new HashMap<>(0));
            m1.forEach((k, v) -> {
                try {
                    if (!valEqualsInclude(m1, m2, k)) {
                        result.add(k);
                    }
                } catch (Exception e) {
                    result.add(k);
                }
                m2.remove(k);
            });
            m2.forEach((k, v) -> result.add(k));
        }
        if (ArrayUtils.isNotEmpty(ignore)) {
            result.removeAll(Arrays.asList(ignore));
        }
        return result;
    }

    @SafeVarargs
    public static <K, V> Set<V> getValues(K key, Map<K, V>... maps) {
        Set<V> result = new HashSet<>();
        if (ArrayUtils.isNotEmpty(maps)) {
            Arrays.stream(maps).forEach(map -> {
                V value = map.get(key);
                if (value != null) {
                    if (value instanceof Collection) {
                        result.addAll((Collection) value);
                    } else {
                        result.add(value);
                    }
                }
            });
        }
        return result;
    }

    private static Number getNumber(final Map map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Number) {
                    return (Number) answer;

                } else if (answer instanceof String) {
                    try {
                        String text = (String) answer;
                        return NumberFormat.getInstance().parse(text);
                    } catch (ParseException ignored) {

                    }
                }
            }
        }
        return null;
    }

    public static Long getLong(final Map map, final Object key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else if (answer instanceof Long) {
            return (Long) answer;
        }
        return answer.longValue();
    }

//    public static Integer getInteger(final Map map, final Object key) {
//        Number answer = getNumber(map, key);
//        if (answer == null) {
//            return null;
//        } else if (answer instanceof Integer) {
//            return (Integer) answer;
//        }
//        return answer.intValue();
//    }
//
//    public static long getLong(final Map map, final Object key, long defaultValue) {
//        Long longObject = getLong(map, key);
//        if (longObject == null) {
//            return defaultValue;
//        }
//        return longObject;
//    }
//
//    public static Double getDouble(final Map map, final Object key) {
//        Number answer = getNumber(map, key);
//        if (answer == null) {
//            return null;
//        } else if (answer instanceof Double) {
//            return (Double) answer;
//        }
//        return answer.doubleValue();
//    }

//    public static Double getDouble(Map map, Object key, Double defaultValue) {
//        Double answer = getDouble(map, key);
//        if (answer == null) {
//            answer = defaultValue;
//        }
//        return answer;
//    }
//
//    public static Boolean getBoolean(Map map, Object key) {
//        if (map != null) {
//            Object answer = map.get(key);
//            if (answer != null) {
//                if (answer instanceof Boolean) {
//                    return (Boolean) answer;
//                }
//                if (answer instanceof String) {
//                    return Boolean.valueOf((String) answer);
//                }
//                if (answer instanceof Number) {
//                    Number n = (Number) answer;
//                    return n.intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
//                }
//            }
//        }
//
//        return null;
//    }
//
//    public static BigDecimal getBigDecimal(final Map map, final Object key) {
//        Double answer = getDouble(map, key);
//        return answer != null ? new BigDecimal(answer) : null;
//    }

    public static String getString(final Map map, final Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                return answer.toString();
            }
        }
        return null;
    }

    public static String getString(Map map, Object key, String defaultValue) {
        String answer = getString(map, key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

    public static List<String> getListString(final Map map, final String key) {
        if (map != null) {
            List<String> answer = null;
            try {
                Object val = map.get(key);
                if (val instanceof List) {
                    answer = (List<String>) map.get(key);
                }
                if (val instanceof Set) {
                    answer = new ArrayList<>((Set<String>) map.get(key));
                } else if (val instanceof String) {
                    answer = Lists.newArrayList(((String) val).split("\\|"));
                } else if (val instanceof String[]) {
                    answer = Lists.newArrayList((String[]) val);
                } else if (val instanceof Object[]) {
                    answer = Arrays.asList(Arrays.stream((Object[]) val).toArray(String[]::new));
                } else if (val instanceof Integer) {
                    answer = Lists.newArrayList(((Integer) val).toString());
                }
            } catch (Exception ignored) {

            }
            if (answer == null) {
                answer = new ArrayList<>();
            }
            if (CollectionUtils.isNotEmpty(answer) && answer.size() == 1 && StringUtils.isEmpty(answer.get(0))) {
                answer = new ArrayList<>();
            }
            return answer;
        }
        return new ArrayList<>();
    }

//    public static List<Long> getListLong(final Map map, final String key) {
//        if (map != null) {
//            List<Long> answer = null;
//            try {
//                Object val = map.get(key);
//                if (val instanceof List) {
//                    answer = (List<Long>) map.get(key);
//                } else if (val instanceof Long) {
//                    answer = Lists.newArrayList((Long) val);
//                } else if (val instanceof Long[]) {
//                    answer = Lists.newArrayList((Long[]) val);
//                }
//            } catch (Exception ignored) {
//
//            }
//            if (answer == null) {
//                answer = new ArrayList<>();
//            }
//            return answer;
//        }
//        return new ArrayList<>();
//    }

//    public static List<Integer> getListInteger(final Map map, final String key) {
//        if (map != null) {
//            List<Integer> answer = null;
//            try {
//                Object val = map.get(key);
//                if (val instanceof List) {
//                    answer = (List<Integer>) map.get(key);
//                } else if (val instanceof Long) {
//                    answer = Lists.newArrayList((Integer) val);
//                } else if (val instanceof Long[]) {
//                    answer = Lists.newArrayList((Integer[]) val);
//                }
//            } catch (Exception ignored) {
//
//            }
//            if (answer == null) {
//                answer = new ArrayList<>();
//            }
//            return answer;
//        }
//        return new ArrayList<>();
//    }
//
//    public static List<Map> getListMap(final Map map, final String key) {
//        List<Map> val = (List<Map>) map.get(key);
//        if (val == null) {
//            val = new ArrayList<>();
//        }
//        return val;
//    }

//    /*
//     * @Description: 获取嵌套列表
//     * @Param: map
//     * @Param: key
//     * @Return: List<Map<String,Object>>
//     **/
//    public static List<Map<String, Object>> getNestedListMap(final Map<String, Object> map, final String key) {
//        if (map == null) {
//            return new ArrayList<>(0);
//        }
//        List<Map<String, Object>> val = (List<Map<String, Object>>) map.get(key);
//        if (val == null) {
//            val = new ArrayList<>(0);
//        }
//        return val;
//    }
//
//    public static Set<String> getMapKeys(List<Map> val, String mapKey) {
//        Set<String> sts = new HashSet<>();
//        val.forEach(e -> sts.add((String) e.get(mapKey)));
//        return sts;
//    }
//
    public static Set<String> getSetString(final Map map, final String key) {
        if (map != null) {
            Set<String> answer;
            Object val = map.get(key);
            if (val instanceof Set) {
                answer = (Set<String>) val;
            } else if (val instanceof List) {
                answer = new HashSet<>((List<String>) map.get(key));
            } else if (val instanceof String) {
                answer = new HashSet<>();
                answer.add((String) val);
            } else if (val instanceof String[]) {
                List<String> temp = Lists.newArrayList((String[]) val);
                answer = new HashSet<>();
                temp.forEach(e -> answer.add((String) e));
            } else {
                answer = new HashSet<>();
            }
            return answer;
        }
        return new HashSet<>();
    }

//    public static <K, V> Map<String, Object> getJsonMap(Map<K, V> map, K key) {
//        if (isNotEmpty(map) && StringUtils.isNotBlank(key) && map.get(key) != null) {
//            try {
//                if (map.get(key) instanceof String) {
//                    return JSONObject.parseObject(getString(map, key), Map.class);
//                } else if (map.get(key) instanceof Map) {
//                    return (Map<String, Object>) map.get(key);
//                }
//            } catch (JSONException e) {
//                logger.error("转换JSON异常", e);
//            }
//        }
//        return new HashMap<>(0);
//    }
//
//    public static <K, V> List<Map> getJsonList(Map<K, V> map, K key) {
//        if (isNotEmpty(map) && StringUtils.isNotBlank(key) && map.get(key) != null) {
//            try {
//                if (map.get(key) instanceof String) {
//                    return JSONArray.parseArray(getString(map, key)).toList(Map.class);
//                } else if (map.get(key) instanceof List) {
//                    return (List<Map>) map.get(key);
//                }
//            } catch (JSONException e) {
//                logger.error("转换JSON异常", e);
//            }
//        }
//        return new ArrayList<>(0);
//    }

//
//    /**
//     * java List<Map<String,Object>> 集合转换集合 Map<String,Set<String>>
//     *
//     * @param dataList
//     * @return
//     */
//    public static Map<String, Set<String>> listConvertMap(List<Map<String, Object>> dataList) {
//        //声明一个Map用于返回
//        Map<String, Set<String>> dataMap = new HashMap<>();
//        //循环List
//        for (Map<String, Object> map : dataList) {
//            for (String key : map.keySet()) {
//                Object val = map.get(key);
//                if (val == null) {
//                    continue;
//                }
//                List<String> valList = new ArrayList<>();
//                if (val instanceof ArrayList) {
//                    valList.addAll((ArrayList) val);
//                } else {
//                    valList.add(StringUtils.getValue(val));
//                }
//                Set<String> l = dataMap.get(key);
//                if (l != null) {
//                    l.addAll(valList);
//                } else {
//                    Set<String> newL = new HashSet<>(valList);
//                    dataMap.put(key, newL);
//                }
//            }
//        }
//        return dataMap;
//    }

//    public static <K, V> Map.Entry<K, V> getHead(LinkedHashMap<K, V> map) {
//        return map.entrySet().iterator().next();
//    }
//
//    public static <K, V> Map.Entry<K, V> getTail(LinkedHashMap<K, V> map) {
//        Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
//        Map.Entry<K, V> tail = null;
//        while (iterator.hasNext()) {
//            tail = iterator.next();
//        }
//        return tail;
//    }
//
//    public static <K, V> LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
//        LinkedHashMap<K, V> linkedHashMap = new LinkedHashMap<>();
//        if (isEmpty(map)) {
//            return linkedHashMap;
//        }
//        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
//        if (list.get(0).getValue() instanceof Number || list.get(0).getValue() == null || StringUtils.hasDigit(StringUtils.getValue(list.get(0).getValue()))) {
//            list.sort((a, b) -> (StringUtils.toDouble(StringUtils.getValue(b.getValue())).compareTo(StringUtils.toDouble(StringUtils.getValue(a.getValue())))));
//        } else {
//            list.sort((a, b) -> (StringUtils.getValue(b.getValue()).compareTo(StringUtils.getValue(a.getValue()))));
//        }
//        list.forEach(i -> linkedHashMap.put(i.getKey(), i.getValue()));
//        return linkedHashMap;
//    }

//    public static <K, V> LinkedHashMap<K, V> sortByKey(Map<K, V> map) {
//        LinkedHashMap<K, V> linkedHashMap = new LinkedHashMap<>();
//        if (isEmpty(map)) {
//            return linkedHashMap;
//        }
//        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
//        if (list.get(0).getKey() instanceof Number || StringUtils.hasDigit(StringUtils.getValue(list.get(0).getKey()))) {
//            list.sort((a, b) -> (StringUtils.getNumbersValue(StringUtils.getValue(b.getKey())).compareTo(StringUtils.getNumbersValue(StringUtils.getValue(a.getKey())))));
//        } else {
//            list.sort((a, b) -> (StringUtils.getValue(b.getKey()).compareTo(StringUtils.getValue(a.getKey()))));
//        }
//        list.forEach(i -> linkedHashMap.put(i.getKey(), i.getValue()));
//        return linkedHashMap;
//    }

//    public static <K, V> Map<K, V> subMap(Map<K, V> map, Integer start, Integer end) {
//        Map<K, V> subMap = new LinkedHashMap<>();
//        int count = 0;
//        for (Map.Entry<K, V> entry : map.entrySet()) {
//            if (count >= start) {
//                if (count < end) {
//                    subMap.put(entry.getKey(), entry.getValue());
//                } else {
//                    break;
//                }
//            }
//            count++;
//        }
//        return subMap;
//    }
//
//    public static <K, V> LinkedHashMap<K, V> reverse(LinkedHashMap<K, V> map) {
//        LinkedHashMap<K, V> linkResult = new LinkedHashMap<>();
//        ListIterator<Map.Entry<K, V>> iterator =
//                new ArrayList<>(map.entrySet()).listIterator(map.size());
//        while (iterator.hasPrevious()) {
//            Map.Entry<K, V> previous = iterator.previous();
//            K key = previous.getKey();
//            V value = previous.getValue();
//            linkResult.put(key, value);
//        }
//        map.clear();
//        map.putAll(linkResult);
//        return map;
//    }

//    /**
//     * @Description: 计算比率
//     * @Param:
//     * @return:
//     * @Author: Li Hao
//     * @Date: 2019/4/9
//     */
//    public static <T extends Number> Map<String, BigDecimal> calculatedRatio(Map<String, T> map, int scale, RoundingMode roundingMode) {
//        BigDecimal total = BigDecimal.valueOf(map.values().stream().mapToDouble(StringUtils::toDouble).sum());
//        Map<String, BigDecimal> result = new LinkedHashMap<>();
//        map.forEach((key, value) -> result.put(key, StringUtils.toBigDecimal(value).divide(total, scale, roundingMode)));
//        return result;
//    }

//    /**
//     * @Description: 筛选置顶的keys
//     * @Param:
//     * @return:
//     * @Author: Li Hao
//     * @Date: 2019/4/19
//     */
//    public static <K, V> Map<K, V> filterKeys(Map<K, V> map, String... keys) {
//        if (isNotEmpty(map)) {
//            Map<K, V> result = new HashMap<>();
//            map.forEach((key, value) -> {
//                if (ArrayUtils.contains(keys, StringUtils.getValue(key))) {
//                    result.put(key, value);
//                }
//            });
//            return result;
//        }
//        return map;
//    }

    /**
     * @Description: 比较并替换map中的值
     * @Param: isMin：true时为需要较小值使用
     * @return:
     * @Author: Li Hao
     * @Date: 2019/5/27
     */
//    public static <K, V> void compareAndReplaceValue(Map<K, V> sourceMap, V value, K key, boolean isMin) {
//        if (value != null) {
//            if (isEmpty(sourceMap) || sourceMap.get(key) == null) {
//                sourceMap.put(key, value);
//            } else {
//                if (sourceMap.get(key) instanceof Number) {
//                    if (isMin) {
//                        if (StringUtils.toDouble(sourceMap.get(key)) > StringUtils.toDouble(value)) {
//                            sourceMap.put(key, value);
//                        }
//                    } else {
//                        if (StringUtils.toDouble(sourceMap.get(key)) < StringUtils.toDouble(value)) {
//                            sourceMap.put(key, value);
//                        }
//                    }
//                }
//            }
//        }
//    }

//    public static <V> V getValueByLikeKey(Map<String, V> map, String key) {
//        V result = null;
//        if (isNotEmpty(map)) {
//            for (Map.Entry<String, V> entry : map.entrySet()) {
//                if (entry.getKey().contains(key) || key.contains(entry.getKey())) {
//                    result = entry.getValue();
//                    break;
//                }
//            }
//        }
//        return result;
//    }
//
//    public static String getSerialVal(Map<String, Object> val, String fields) {
//        String[] field = StringUtils.split(fields, ".");
//        for (int i = 0, j = field.length - 1; i <= j; i++) {
//            if (i == j) {
//                return getString(val, field[i], "");
//            } else {
//                if (val == null) {
//                    return "";
//                }
//                val = (Map<String, Object>) val.get(field[i]);
//            }
//        }
//        return "";
//    }

//    public static <K, V> void removeEmptyKey(Map<K, V> map) {
//        if (isNotEmpty(map)) {
//            Iterator<Map.Entry<K, V>> iterator = map.entrySet().iterator();
//            while (iterator.hasNext()) {
//                V value = iterator.next().getValue();
//                if (value == null
//                        || (value instanceof String && StringUtils.isBlank((String) value))
//                        || (value instanceof Collection && CollectionUtils.isEmpty((Collection) value))
//                        || (value instanceof Map && EsMapUtils.isEmpty((Map) value))
//                        || StringUtils.isBlank(value)) {
//                    iterator.remove();
//                }
//            }
//        }
//    }

//    public static <K, V> void removeSpecialKey(Map<K, V> map, K key) {
//        if (isNotEmpty(map)) {
//            map.entrySet().removeIf(kvEntry -> kvEntry.getKey().equals(key));
//        }
//    }

//    public static <K, V> void removeSpecialValue(Map<K, V> map, V value) {
//        if (isNotEmpty(map)) {
//            map.entrySet().removeIf(kvEntry -> kvEntry.getValue().equals(value));
//        }
//    }

//    public static <K, V> void putEmptyKeyToNull(Map<K, V> map) {
//        if (isNotEmpty(map)) {
//            map.entrySet().forEach(i -> {
//                if (StringUtils.isBlank(i.getValue())) {
//                    i.setValue(null);
//                }
//            });
//        }
//    }

//    public static <K, V> Map<K, V> moveToHead(Map<K, V> map, K key) {
//        Map<K, V> result = new LinkedHashMap<>();
//        if (isNotEmpty(map)) {
//            if (map.containsKey(key)) {
//                result.put(key, map.remove(key));
//            }
//            result.putAll(map);
//        }
//        return result;
//    }

//    public static <K, V> Map<K, V> moveToTail(Map<K, V> map, K key) {
//        Map<K, V> result = new LinkedHashMap<>();
//        if (isNotEmpty(map)) {
//            if (map.containsKey(key)) {
//                V value = map.remove(key);
//                result.putAll(map);
//                result.put(key, value);
//            } else {
//                result.putAll(map);
//            }
//        }
//        return result;
//    }

//    public static <K, V> V getFirstKey(Map<K, V> map) {
//        V result = null;
//        if (isNotEmpty(map)) {
//            result = map.entrySet().iterator().next().getValue();
//        }
//        return result;
//    }

//    public static <K, V> LinkedHashMap<K, V> sortKeyBySortList(Map<K, V> map, List<K> sortList) {
//        LinkedHashMap<K, V> linkedHashMap = new LinkedHashMap<>();
//        if (isEmpty(map) || CollectionUtils.isEmpty(sortList)) {
//            return new LinkedHashMap<>(Optional.ofNullable(map).orElse(new HashMap<>()));
//        }
//        sortList.forEach(i -> linkedHashMap.put(i, map.get(i)));
//        return linkedHashMap;
//    }

    //Map<K, V> 深度复制
    public static <K, V> Map<K, V> deepCopy(Map<K, V> map) {
        Map<K, V> dest = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(map);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            dest = (Map<K, V>) in.readObject();
        } catch (Exception e) {
            System.out.println("Collection<T> 深度复制" + e.getMessage());
        }
        return dest;
    }

//    public static <T> Map<String, T> toHungarianCase(Map<String, T> map) {
//        if (isNotEmpty(map)) {
//            Map<String, T> result = new HashMap<>(map.size());
//            map.forEach((k, v) -> result.put(toHungarianCase(k), v));
//            return result;
//        }
//        return map;
//    }




//    public static List<String> getKeyListFromListMap(List<Map> list, String key) {
//        List<String> arr = new ArrayList<>();
//        if (CollectionUtils.isNotEmpty(list)) {
//            for (Map map_item : list) {
//                List<String> ids_list = EsMapUtils.getListString(map_item, key);
//                if (ids_list != null) {
//                    ids_list.forEach(e -> {
//                        if (StringUtils.isNotEmpty(e)) {
//                            arr.add(e);
//                        }
//                    });
//                }
//            }
//        }
//        return arr;
//    }
//
//    public static <K, V extends Number> Map.Entry<K, V> getMax(Map<K, V> map) {
//        if (isNotEmpty(map)) {
//            return EsMapUtils.getHead(EsMapUtils.sortByValue(map));
//        }
//        return null;
//    }
}
