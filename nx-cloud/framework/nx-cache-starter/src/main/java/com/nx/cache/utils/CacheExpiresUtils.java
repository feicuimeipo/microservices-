package com.nx.cache.utils;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * 
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2015年4月28日
 */
public class CacheExpiresUtils {
	/**
	 * 毫秒为单位
	 */
	public final static long IN_1MIN = 60;
	
	public final static long IN_3MINS = 60 * 3; 
	
	public final static long IN_5MINS = 60 * 5;

	public final static long IN_1HOUR = 60 * 60;
	
	public final static long IN_HALF_HOUR = 60 * 30;
	
	public final static long IN_1DAY = IN_1HOUR * 24;
	
	public final static long IN_1WEEK = IN_1DAY * 7;
	
	public final static long IN_1MONTH = IN_1DAY * 30;
	
	/**
	 * 当前时间到今天结束相隔的秒
	 * @return
	 */
	public static long todayEndSeconds(){
		Date curTime = new Date();
		return getDiffSeconds(getDayEnd(curTime), curTime);
	}


	private static final String TIME_23_59_59 = " 23:59:59";
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	/**
	 * 获取一天结束时间<br>
	 * generate by: carmen nianxi at 2011-12-23
	 *
	 * @param date
	 * @return
	 */
	public static Date getDayEnd(Date date) {
		String format = DateFormatUtils.format(date, DATE_PATTERN);
		return parseDate(format.concat(TIME_23_59_59));
	}

	/**
	 * 比较两个时间相差多少秒
	 * */
	public static long getDiffSeconds(Date d1, Date d2) {
		return Math.abs((d2.getTime() - d1.getTime()) / 1000);
	}

	/**
	 * 解析日期<br>
	 * 支持格式：<br>
	 * generate by: carmen nian at 2012-3-1
	 *
	 * @param dateStr
	 * @return
	 */
	public static Date parseDate(String dateStr) {
		SimpleDateFormat format = null;
		if (StringUtils.isBlank(dateStr))
			return null;
		String _dateStr = dateStr.trim();
		try {
			if (_dateStr.matches("\\d{1,2}[A-Z]{3}")) {
				_dateStr = _dateStr
						+ (Calendar.getInstance().get(Calendar.YEAR) - 2000);
			}
			// 01OCT12
			if (_dateStr.matches("\\d{1,2}[A-Z]{3}\\d{2}")) {
				format = new SimpleDateFormat("ddMMMyy", Locale.ENGLISH);
			} else if (_dateStr.matches("\\d{1,2}[A-Z]{3}\\d{4}.*")) {// 01OCT2012
				// ,01OCT2012
				// 1224,01OCT2012
				// 12:24
				_dateStr = _dateStr.replaceAll("[^0-9A-Z]", "")
						.concat("000000").substring(0, 15);
				format = new SimpleDateFormat("ddMMMyyyyHHmmss", Locale.ENGLISH);
			} else {
				StringBuffer sb = new StringBuffer(_dateStr);
				String[] tempArr = _dateStr.split("\\s+");
				tempArr = tempArr[0].split("-|\\/");
				if (tempArr.length == 3) {
					if (tempArr[1].length() == 1) {
						sb.insert(5, "0");
					}
					if (tempArr[2].length() == 1) {
						sb.insert(8, "0");
					}
				}
				_dateStr = sb.append("000000").toString().replaceAll("[^0-9]",
						"").substring(0, 14);
				if (_dateStr.matches("\\d{14}")) {
					format = new SimpleDateFormat("yyyyMMddHHmmss");
				}
			}
			Date date = format.parse(_dateStr);
			return date;
		} catch (Exception e) {
			throw new RuntimeException("无法解析日期字符[" + dateStr + "]");
		}
	}

}
