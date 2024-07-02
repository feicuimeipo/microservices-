package com.nx.elasticsearch.utils;

import com.google.gson.Gson;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class EsUtils {
	//json转化对象
	public static final Gson GSON = new Gson();
	private static final Class<String> CLASS_STRING = String.class;

	//罗马数字转化为英文字母
	private static final HashMap<String, String> ROMAN_REPLACE_DICT = new HashMap<String, String>() {{
		put("ⅰ", "i");
		put("ⅱ", "ii");
		put("ⅲ", "iii");
		put("ⅳ", "iv");
		put("ⅴ", "v");
		put("ⅵ", "vi");
		put("ⅶ", "vii");
		put("ⅷ", "viii");
		put("ⅸ", "ix");
		put("ⅹ", "x");
		put("ⅺ", "xi");
		put("ⅻ", "xii");
	}};
	//罗马数字转化为英文字母
	private static final HashMap<String, String> ROMAN_REPLACE_DICT_UPPER = new HashMap<String, String>() {{
		put("Ⅰ", "I");
		put("Ⅱ", "II");
		put("Ⅲ", "III");
		put("Ⅳ", "IV");
		put("Ⅴ", "V");
		put("Ⅵ", "VI");
		put("Ⅶ", "VII");
		put("Ⅷ", "VIII");
		put("Ⅸ", "IX");
		put("Ⅹ", "X");
		put("Ⅺ", "XI");
		put("Ⅻ", "XII");
	}};
	//希腊字母转化为英文字母
	private static final HashMap<String, String> GREEK_REPLACE_DICT = new HashMap<String, String>() {{
		put("α", "a");
		put("β", "b");
		put("γ", "g");
		put("δ", "d");
		put("ε", "e");
		put("ζ", "z");
		put("η", "h");
		put("θ", "q");
		put("ι", "i");
		put("κ", "k");
		put("λ", "l");
		put("μ", "m");
		put("ν", "n");
		put("ξ", "x");
		put("ο", "o");
		put("π", "p");
		put("ρ", "r");
		put("σ", "s");
		put("τ", "t");
		put("υ", "u");
		put("φ", "f");
		put("χ", "c");
		put("ψ", "y");
		put("ω", "w");
	}};
	//希腊字母转化为英文字母
	private static final HashMap<String, String> GREEK_REPLACE_DICT_UPPER = new HashMap<String, String>() {{
		put("Α", "A");
		put("Β", "B");
		put("Γ", "G");
		put("Δ", "D");
		put("Ε", "E");
		put("Ζ", "Z");
		put("Η", "H");
		put("Θ", "Q");
		put("Ι", "I");
		put("Κ", "K");
		put("Λ", "L");
		put("Μ", "M");
		put("Ν", "N");
		put("Ξ", "X");
		put("Ο", "O");
		put("Π", "P");
		put("Ρ", "R");
		put("Σ", "S");
		put("Τ", "T");
		put("Υ", "U");
		put("Φ", "F");
		put("Χ", "C");
		put("Ψ", "Y");
		put("Ω", "W");
	}};
	//全角小写英文替换英文字母
	private static final HashMap<String, String> CASE_DICT = new HashMap<String, String>() {{
		put("ａ", "a");
		put("ｂ", "b");
		put("ｃ", "c");
		put("ｄ", "d");
		put("ｅ", "e");
		put("ｆ", "f");
		put("ｇ", "g");
		put("ｈ", "h");
		put("ｉ", "i");
		put("ｊ", "j");
		put("ｋ", "k");
		put("ｌ", "l");
		put("ｍ", "m");
		put("ｎ", "n");
		put("ｏ", "o");
		put("ｐ", "p");
		put("ｑ", "q");
		put("ｒ", "r");
		put("ｓ", "s");
		put("ｔ", "t");
		put("ｕ", "u");
		put("ｖ", "v");
		put("ｗ", "w");
		put("ｘ", "x");
		put("ｙ", "y");
		put("ｚ", "z");
	}};
	//全角小写英文替换英文字母
	private static final HashMap<String, String> CASE_DICT_UPPER = new HashMap<String, String>() {{
		put("Ａ", "A");
		put("Ｂ", "B");
		put("Ｃ", "C");
		put("Ｄ", "D");
		put("Ｅ", "E");
		put("Ｆ", "F");
		put("Ｇ", "G");
		put("Ｈ", "H");
		put("Ｉ", "I");
		put("Ｊ", "J");
		put("Ｋ", "K");
		put("Ｌ", "L");
		put("Ｍ", "M");
		put("Ｎ", "N");
		put("Ｏ", "O");
		put("Ｐ", "P");
		put("Ｑ", "Q");
		put("Ｒ", "R");
		put("Ｓ", "S");
		put("Ｔ", "T");
		put("Ｕ", "U");
		put("Ｖ", "V");
		put("Ｗ", "W");
		put("Ｘ", "X");
		put("Ｙ", "Y");
		put("Ｚ", "Z");
	}};
	//大小写其它语言字母和中文拼音替换英文字母
	private static final HashMap<String, String> OTHER_REPLACE_DICT = new HashMap<String, String>() {{
		put("ä", "a");
		put("ö", "o");
		put("ê", "e");
		put("ü", "v");
		put("ā", "a");
		put("á", "a");
		put("ǎ", "a");
		put("à", "a");
		put("ō", "o");
		put("ó", "o");
		put("ǒ", "o");
		put("ò", "o");
		put("ē", "e");
		put("é", "e");
		put("ě", "e");
		put("è", "e");
		put("ī", "i");
		put("í", "i");
		put("ǐ", "i");
		put("ì", "i");
		put("ū", "u");
		put("ú", "u");
		put("ǔ", "u");
		put("ù", "u");
		put("ǖ", "v");
		put("ǘ", "v");
		put("ǚ", "v");
		put("ǜ", "v");
	}};
	//大小写其它语言字母和中文拼音替换英文字母
	private static final HashMap<String, String> OTHER_REPLACE_DICT_UPPER = new HashMap<String, String>() {{
		put("Ä", "A");
		put("Ö", "O");
		put("Ê", "E");
		put("Ü", "V");
		put("Ā", "A");
		put("Á", "A");
		put("Ǎ", "A");
		put("À", "A");
		put("Ō", "O");
		put("Ó", "O");
		put("Ǒ", "O");
		put("Ò", "O");
		put("Ē", "E");
		put("É", "E");
		put("Ě", "E");
		put("È", "E");
		put("Ī", "I");
		put("Í", "I");
		put("Ǐ", "I");
		put("Ì", "I");
		put("Ū", "U");
		put("Ú", "U");
		put("Ǔ", "U");
		put("Ù", "U");
		put("Ǖ", "V");
		put("Ǘ", "V");
		put("Ǚ", "V");
		put("Ǜ", "V");
	}};
	//中文符号转化为英文符号
	private static final HashMap<String, String> CHARS_DICT = new LinkedHashMap<String, String>() {{
		put("（", "(");
		put("）", ")");
		put("【", "[");
		put("】", "]");
		put("｛", "{");
		put("｝", "}");
		put("《", "<");
		put("》", ">");
		put("‘", "'");
		put("’", "'");
		put("“", "\"");
		put("”", "\"");
		put("、", ",");
		put("；", ";");
		put("，", ",");
		put("。", ".");
		put("？", "?");
		put("！", "!");
		put("：", ":");
		put("￥", "$");
		put("……", "^");
		put("——", "_");
		put("［", "[");
		put("］", "]");
		put("〔", "[");
		put("〕", "]");
		put("〖", "[");
		put("〗", "]");
		put("『", "[");
		put("』", "]");
		put("「", "[");
		put("」", "]");
		put("〈", "<");
		put("〉", ">");
		put("＜", "<");
		put("＞", ">");
		put("〃", "\"");
		put("＂", "\"");
		put("｀", "'");
		put("＇", "'");
		put("＃", "#");
		put("＆", "&");
		put("＠", "@");
		put("％", "%");
		put("＼", "\\");
		put("／", "/");
		put("︿", "^");
		put("＿", "_");
		put("￣", "_");
		put("ˉ", "_");
		put("―", "-");
		put("－", "-");
		put("＋", "+");
		put("．", ".");
		put("｜", "|");
		put("∶", ":");
		put("～", "~");
		put("·", ".");
		put("＝", "=");
		put("…", "^");
		put("—", "_");
	}};
	//全角数字替换半角数字
	private static final HashMap<String, String> NUMBER_DICT = new HashMap<String, String>() {{
		put("０", "0");
		put("１", "1");
		put("２", "2");
		put("３", "3");
		put("４", "4");
		put("５", "5");
		put("６", "6");
		put("７", "7");
		put("８", "8");
		put("９", "9");
	}};

	private static final Map<String, String>[] _tuple_replace_upper = new Map[]{ROMAN_REPLACE_DICT_UPPER, GREEK_REPLACE_DICT_UPPER, CASE_DICT_UPPER, OTHER_REPLACE_DICT_UPPER};
	private static final Map<String, String>[] _tuple_replace_all = new Map[]{ROMAN_REPLACE_DICT, GREEK_REPLACE_DICT, CASE_DICT, OTHER_REPLACE_DICT, NUMBER_DICT, CHARS_DICT};


	/**
	 * 将json字符串转化为对象
	 *
	 * @param json
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T jsonToObject(String json, Class<T> clazz) {
		return GSON.fromJson(json, clazz);
	}

	/**
	 * 将对象转化为json字符串
	 *
	 * @param obj
	 * @param <T>
	 * @return
	 */
	public static <T> String objectToJson(T obj) {
		return GSON.toJson(obj);
	}


	public static Map<String, Object> initHashMapWithStringObject(Object... arr) {
		return initHashMapWithKV(arr);
	}

	public static <K, V> Map<K, V> initHashMapWithKV() {
		return (new EsMapBuilder()).builder();
	}

	public static <K, V> Map<K, V> initHashMapWithKV(Object... arr) {
		EsMapBuilder<K, V> builder = new EsMapBuilder();
		int i = 0;

		for(int len = arr.length; i < len; i += 2) {
			builder.append((K) arr[i], (V) arr[i + 1]);
		}

		return builder.builder();
	}


	/**
	 * 获取新的uuid
	 *
	 * @return
	 */
	public static UUID getUUID() {
		return UUID.randomUUID();
	}

	/**
	 * 获取总页数
	 *
	 * @param count 总条数
	 * @param size  每页条数
	 * @return 总页数
	 */
	public static int getPageTotal(long count, int size) {
		//return (int) Math.ceil(count * 1.0 / size);
		return (int) (count + size - 1) / size;
	}

	/**
	 * 如果集合中元素个数<2或者元素中含有null,则抛出异常
	 *
	 * @param coll
	 * @param <E>
	 */
	public static <E> void judgeIterLen2AndENotNull(String name, Collection<E> coll) {
		judgeIterLenNAndENotNull(name, coll, 2);
	}

	/**
	 * 如果集合中元素个数<2或者元素中含有null,则抛出异常
	 *
	 * @param coll
	 * @param <E>
	 */
	public static <E> void judgeIterLenNAndENotNull(String name, Collection<E> coll, int n) {
		judgeNameNotNull(name);
		if (CollectionUtils.isEmpty(coll) || coll.size() < n) {
			throwIllegalArgumentException(String.format("%s不可以为null,且长度不小于%s.", name, n));
		}
		judgeIterENotNull(name, coll);
	}

	/**
	 * 如果集合中元素个数<2或者元素中含有null,则抛出异常
	 *
	 * @param arr
	 */
	public static void judgeIterLen2AndENotNull(String name, Object... arr) {
		judgeNameNotNull(name);
		if (ArrayUtils.isEmpty(arr) || arr.length < 2) {
			throwIllegalArgumentException(String.format("%s不可以为null,且长度>1.", name));
		}
		judgeIterENotNull(name, arr);
	}

	/**
	 * 如果集合中元素个数<1,则抛出异常
	 *
	 * @param coll
	 * @param name
	 * @param <E>
	 */
	public static <E> void judgeIterNotNull(String name, Collection<E> coll) {
		judgeNameNotNull(name);
		judgeIterNotNullCore(name, coll);
	}

	/**
	 * 如果集合中元素个数<1,则抛出异常
	 *
	 * @param map
	 * @param name
	 * @param <K>
	 * @param <V>
	 */
	public static <K, V> void judgeIterNotNull(String name, Map<K, V> map) {
		judgeNameNotNull(name);
		judgeIterNotNullCore(name, map);
	}

	/**
	 * 如果集合中元素个数<1,则抛出异常
	 *
	 * @param arr
	 * @param name
	 */
	public static void judgeIterNotNull(String name, Object... arr) {
		judgeNameNotNull(name);
		judgeIterNotNullCore(name, arr);
	}

	/**
	 * 如果集合中元素个数<1或者元素中含有null,则抛出异常
	 *
	 * @param coll
	 * @param name
	 * @param <E>
	 */
	public static <E> void judgeIterNotNullAndENotNull(String name, Collection<E> coll) {
		judgeNameNotNull(name);
		judgeIterNotNullCore(name, coll);
		judgeIterENotNull(name, coll);
	}

	/**
	 * 如果集合中元素个数<1或者元素中含有null,则抛出异常
	 *
	 * @param arr
	 */
	public static void judgeIterNotNullAndENotNull(String name, Object... arr) {
		judgeNameNotNull(name);
		judgeIterNotNullCore(name, arr);
		judgeIterENotNull(name, arr);
	}

	/**
	 * 如果字典中元素个数<1或者字典中key含有null,则抛出异常
	 *
	 * @param map
	 * @param name
	 * @param <K>
	 * @param <V>
	 */
	public static <K, V> void judgeIterNotNullAndKNotNull(String name, Map<K, V> map) {
		judgeNameNotNull(name);
		judgeIterNotNullCore(name, map);
		judgeIterENotNull(name, map.keySet());
	}

	/**
	 * 如果字典中元素个数<1或者字典中key含有null,则抛出异常
	 *
	 * @param map
	 * @param name
	 * @param <K>
	 * @param <V>
	 */
	public static <K, V> void judgeIterNotNullAndVNotNull(String name, Map<K, V> map) {
		judgeNameNotNull(name);
		judgeIterNotNullCore(name, map);
		judgeIterENotNull(name, map.values());
	}

	/**
	 * 对象为空,则抛出异常
	 *
	 * @param o    对象
	 * @param name 名称
	 * @param <T>
	 */
	public static <T> void judgeObjectNotNull(String name, T o) {
		judgeNameNotNull(name);
		if (o == null) {
			throwIllegalArgumentException(String.format("%s禁止为空.", name));
		}
	}

	/**
	 * 字符串为空,则抛出异常
	 *
	 * @param name
	 * @param s
	 */
	public static void judgeStringNotNull(String name, String s) {
		judgeNameNotNull(name);
		if (StringUtils.isEmpty(s)) {
			throwIllegalArgumentException(String.format("%s禁止为空.", name));
		}
	}

	/**
	 * 多个字符串都为空,则抛出异常
	 *
	 * @param arr
	 */
	public static void judgeStringsNotAllNull(String... arr) {
		int length = arr.length;
		if (length > 0 && length % 2 != 0) {
			throwIllegalArgumentException("arr长度必须是偶数");
		}
		int len2 = length / 2;
		String[] names = Arrays.copyOfRange(arr, 0, len2);
		Arrays.stream(names).forEach(o -> judgeNameNotNull(o));
		String[] vals = Arrays.copyOfRange(arr, len2, length);
		if (Arrays.stream(vals).allMatch(StringUtils::isEmpty)) {
			throwIllegalArgumentException(String.format("%s禁止都为空.", Arrays.toString(names)));
		}
	}

	public static IllegalArgumentException getIllegalArgumentException(Object... objs) {
		return new IllegalArgumentException(String.format("参数类型错误:%s", StringUtils.join(objs, ',')));
	}

	public static void throwIllegalArgumentException(Object... objs) {
		throw getIllegalArgumentException(objs);
	}

	public static RuntimeException getRuntimeException(String message, Throwable cause) {
		if (StringUtils.isEmpty(message) && cause == null) {
			throwIllegalArgumentException("message", "cause", "至少存在1个值");
		}
		if (cause == null) {
			return new RuntimeException(message);
		} else if (StringUtils.isEmpty(message)) {
			return new RuntimeException(cause);
		}
		return new RuntimeException(message, cause);
	}

	public static void throwRuntimeException(String message, Throwable cause) {
		throw getRuntimeException(message, cause);
	}

	public static void throwRuntimeException(Throwable cause) {
		throw getRuntimeException(null, cause);
	}


	public static void throwRuntimeException(String message) {
		throw getRuntimeException(message, null);
	}

	public static String lower_join(String key, boolean lower, boolean join, String split_char, Function<String, String> join_func, String defaults) {
		try {
			if (lower) {
				key = StringUtils.replace(key.toLowerCase(), "\ufeff", "");
			}
			if (join) {
				if (join_func != null) {
					if (StringUtils.isNotEmpty(split_char)) {
						return Arrays.stream(StringUtils.split(key, split_char)).filter(o -> StringUtils.isNotEmpty(join_func.apply(o))).collect(Collectors.joining(split_char));
					} else {
						return Arrays.stream(StringUtils.split(key, " ")).filter(o -> StringUtils.isNotEmpty(join_func.apply(o))).collect(Collectors.joining(" "));
					}
				} else {
					if (StringUtils.isNotEmpty(split_char)) {
						return Arrays.stream(StringUtils.split(key, split_char)).filter(o -> StringUtils.isNotEmpty(o)).collect(Collectors.joining(split_char));
					} else {
						return Arrays.stream(StringUtils.split(key, " ")).filter(o -> StringUtils.isNotEmpty(o)).collect(Collectors.joining(" "));
					}
				}
			}
			return key.trim();
		} catch (Exception e) {
			return defaults;
		}
	}

	public static String _replace_core(String txt, Map<String, String> replace_dict) {
		for (Map.Entry<String, String> entry : replace_dict.entrySet()) {
			txt = StringUtils.replace(txt, entry.getKey(), entry.getValue());
		}
		return txt;
	}

	public static String replace_key(String txt, Map<String, String>... replace_dicts) {
		if (StringUtils.isNotEmpty(txt)) {
			for (Map<String, String> d : replace_dicts) {
				txt = _replace_core(txt, d);
			}
		}
		return txt;
	}

	public static String replace_all(String txt, boolean lower) {
		if (!lower) {
			//替换含有大写的特殊字符
			txt = replace_key(txt, _tuple_replace_upper);
		}
		return replace_key(txt, _tuple_replace_all);
	}

	public static String regex_replace(Pattern p, String s, String repl) {
		return p.matcher(s).replaceAll(repl);
	}

	public static void clean(String s, boolean lower, boolean join, boolean replace_default_dict, Object replace_dict, Object replace_regex, boolean remove_special_chars, boolean replace_special_chars, Object extra_func, Object defaults, Object deal_special_chars) {
		if (lower || join) {
			s = lower_join(s, lower, join, null, null, null);
		} else {
			s = s.trim();
		}
		if (replace_default_dict) {
			s = replace_all(s, lower);
		}
		if (replace_dict != null) {
			if (replace_dict instanceof Map) {
				s = replace_key(s, (Map<String, String>) replace_dict);
			} else if (replace_dict.getClass().isArray()) {
				s = replace_key(s, (Map<String, String>[]) replace_dict);
			}
		}
		if (StringUtils.isNotEmpty(s)) {

		}


	}

	private static <E> void judgeIterENotNull(String name, Collection<E> coll) {
		Class<?> clazz = null;
		for (E e : coll) {
			clazz = e.getClass();
			break;
		}
		judgeIterENotNull(name, coll.stream(), clazz);
	}

	private static void judgeIterENotNull(String name, Object... arr) {
		Class<?> clazz = arr[0].getClass();
		judgeIterENotNull(name, Arrays.stream(arr), clazz);
	}

	private static <T> void judgeIterENotNull(String name, Stream<T> stream, Class<?> clazz) {
		if (CLASS_STRING.equals(clazz)) {
			if (((Stream<String>) stream).anyMatch(StringUtils::isEmpty)) {
				throwIllegalArgumentException(String.format("%s中元素禁止为null.", name));
			}
		} else {
			if (stream.anyMatch(Objects::isNull)) {
				throwIllegalArgumentException(String.format("%s中元素禁止为null.", name));
			}
		}
	}

	private static <E> void judgeIterNotNullCore(String name, Collection<E> coll) {
		if (CollectionUtils.isEmpty(coll)) {
			throwIllegalArgumentException(String.format("%s不可以为null,且长度>0.", name));
		}
	}

	private static <K, V> void judgeIterNotNullCore(String name, Map<K, V> map) {
		if (MapUtils.isEmpty(map)) {
			throwIllegalArgumentException(String.format("%s字典不可以为null,且长度>0.", name));
		}
	}

	private static void judgeIterNotNullCore(String name, Object[] arr) {
		if (ArrayUtils.isEmpty(arr)) {
			throwIllegalArgumentException(String.format("%s不可以为null,且长度>0.", name));
		}
	}

	private static void judgeNameNotNull(String name) {
		if (StringUtils.isEmpty(name)) {
			throwIllegalArgumentException("name参数名称禁止为空!");
		}
	}

}
