/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.util;
//
//import com.cronutils.builder.CronBuilder;
//import com.cronutils.model.Cron;
//import com.cronutils.model.definition.CronConstraintsFactory;
//import com.cronutils.model.definition.CronDefinition;
//import com.cronutils.model.definition.CronDefinitionBuilder;
//import com.cronutils.model.field.expression.FieldExpression;
//import com.cronutils.model.field.expression.Weekdays;
//import com.cronutils.model.field.value.SpecialChar;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import org.nianxi.boot.support.AppUtil;
//import org.nianxi.utils.BeanUtils;
//import org.nianxi.utils.JsonUtil;
//import org.nianxi.utils.StringUtil;
//import com.hotent.uc.dao.PropertiesDao;
//import com.hotent.uc.model.Org;
//import com.hotent.uc.model.Properties;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.cronutils.model.field.expression.FieldExpressionFactory.always;
//import static com.cronutils.model.field.expression.FieldExpressionFactory.and;
//import static com.cronutils.model.field.expression.FieldExpressionFactory.every;
//import static com.cronutils.model.field.expression.FieldExpressionFactory.on;
//import static com.cronutils.model.field.expression.FieldExpressionFactory.questionMark;
//public class CronUtils {
//
//    public static String getScheduledCron(String jsonStr) throws IOException{
//		String cronStr = "";
//		if(StringUtil.isNotEmpty(jsonStr)){
//			ObjectNode json = (ObjectNode) JsonUtil.toJsonNode(jsonStr);
//			//每N分钟执行
//			String time =  json.get("time").toString();
//			//日期
//			String date = json.findValue("date")!=null?json.get("date").toString():"0";
//			//秒
//			String second = json.findValue("second")!=null?json.get("second").toString():"0";
//			//分钟
//			String minute = json.findValue("minute") != null?json.get("minute").toString():"0";
//			//小时
//			String hour = json.findValue("hour") != null?json.get("hour").toString():"0";
//			//天
//			String day = json.findValue("day") != null?json.get("day").toString():"0";
//			//周
//			String week = json.findValue("week") != null?json.get("week").toString():"0";
//			//计划类型
//			String type = json.get("triggerType").toString();
//
//			CronDefinition cronDefinition = CronDefinitionBuilder.defineCron()
//	        .withSeconds().and()
//	        .withMinutes().and()
//	        .withHours().and()
//	        .withDayOfMonth().supportsL().supportsW().supportsLW().supportsQuestionMark().and()
//	        .withMonth().and()
//	        .withDayOfWeek().withValidRange(1, 7).withMondayDoWValue(2).supportsHash().supportsL().supportsQuestionMark().and()
//	        .withCronValidation(CronConstraintsFactory.ensureEitherDayOfWeekOrDayOfMonth())
//	        .instance();
//
//			CronBuilder builder = null;
//
//	    	switch (type) {
//			//执行一次
//			case "cron":
//				cronStr = json.get("cron").toString();
//				break;
//			//每N分钟执行一次
//			case "time":
//				 builder = CronBuilder.cron(cronDefinition)
//	            .withMonth(always())
//	            .withDoW(questionMark())
//	            .withDoM(always())
//	            .withHour(always())
//	            .withMinute(every(on(0), Integer.valueOf(time)))
//	            .withSecond(on(0));
//				break;
//			//每天N时执行
//			case "day":
//				builder = CronBuilder.cron(cronDefinition)
//	            .withMonth(always())
//	            .withDoW(questionMark())
//	            .withDoM(always())
//	            .withHour(on(Integer.valueOf(hour)))
//	            .withMinute(on(Integer.valueOf(minute)))
//	            .withSecond(on(0));
//				break;
//			//每周每周N执行
//			case "week":
//				builder = CronBuilder.cron(cronDefinition)
//	            .withMonth(always())
//	            .withDoW(and(buildWeekdays(week)))
//	            .withDoM(questionMark())
//	            .withHour(on(Integer.valueOf(hour)))
//	            .withMinute(on(Integer.valueOf(minute)))
//	            .withSecond(on(0));
//				break;
//			//每月的某些天执行
//			case "month":
//				 builder = CronBuilder.cron(cronDefinition)
//	            .withMonth(always())
//	            .withDoW(questionMark())
//	            .withDoM(and(builddays(day)))
//	            .withHour(on(Integer.valueOf(hour)))
//	            .withMinute(on(Integer.valueOf(minute)))
//	            .withSecond(on(0));
//				break;
//			default:
//				break;
//			}
//
//	    	if(BeanUtils.isNotEmpty(builder)){
//	    		Cron cron = builder.instance();
//		        cronStr = cron.asString();
//		        if("week".equals(type)){
//		        	cronStr = transCronWeek(cronStr);
//		        }
//	    	}
//		}
//		return cronStr;
//	}
//
//    private static String transCronWeek(String cronStr){
//        String[] strs = cronStr.split(" ");
//        String weekStr = strs[strs.length-1];
//        String[] weeks = weekStr.split(",");
//        StringBuilder weekBuilder = new StringBuilder();
//        for (String week : weeks) {
//            switch (week) {
//                case "1":
//                    weekBuilder.append(",MON");
//                    break;
//                case "2":
//                    weekBuilder.append(",TUE");
//                    break;
//                case "3":
//                    weekBuilder.append(",WED");
//                    break;
//                case "4":
//                    weekBuilder.append(",THU");
//                    break;
//                case "5":
//                    weekBuilder.append(",FRI");
//                    break;
//                case "6":
//                    weekBuilder.append(",SAT");
//                    break;
//                case "7":
//                    weekBuilder.append(",SUN");
//                    break;
//                default:
//                    break;
//            }
//        }
//        String weekBuilderStr = weekBuilder.toString();
//        if(weekBuilderStr.startsWith(",")){
//            weekBuilderStr = weekBuilderStr.replaceFirst(",", "");
//            cronStr = cronStr.replace(weekStr, weekBuilderStr);
//        }
//        return cronStr;
//    }
//
//	private static List<FieldExpression> buildWeekdays(String str) throws IOException{
//		List<FieldExpression> list = new ArrayList<FieldExpression>();
//		ArrayNode split = (ArrayNode) JsonUtil.toJsonNode(str);
//		for (Object object : split) {
//			switch (object.toString()) {
//			case "MON":
//				list.add(on(Weekdays.MONDAY.getWeekday()));
//				break;
//			case "TUE":
//				list.add(on(Weekdays.TUESDAY.getWeekday()));
//				break;
//			case "WED":
//				list.add(on(Weekdays.WEDNESDAY.getWeekday()));
//				break;
//			case "THU":
//				list.add(on(Weekdays.THURSDAY.getWeekday()));
//				break;
//			case "FRI":
//				list.add(on(Weekdays.FRIDAY.getWeekday()));
//				break;
//			case "SAT":
//				list.add(on(Weekdays.SATURDAY.getWeekday()));
//				break;
//			case "SUN":
//				list.add(on(Weekdays.SUNDAY.getWeekday()));
//				break;
//			default:
//				break;
//			}
//		}
//		return list;
//	}
//
//	private static List<FieldExpression> builddays(String str) throws IOException{
//		List<FieldExpression> list = new ArrayList<FieldExpression>();
//		ArrayNode split = (ArrayNode) JsonUtil.toJsonNode(str);
//		for (Object object : split) {
//			if(!"L".equals(object.toString())){
//				list.add(on(Integer.valueOf(object.toString())));
//			}else{
//				list.add(on(SpecialChar.L));
//			}
//		}
//		return list;
//	}
//
//
//    public static String getScheduledCronLog(String jsonStr) throws IOException {
//        String cronStr = "";
//        if(StringUtil.isNotEmpty(jsonStr)){
//            ObjectNode json = (ObjectNode) JsonUtil.toJsonNode(jsonStr);
//            //每N分钟执行
//            String time =  json.get("time").toString();
//            //日期
//            String date = json.findValue("date")!=null?json.get("date").toString():"0";
//            //秒
//            String second = json.findValue("second")!=null?json.get("second").toString():"0";
//            //分钟
//            String minute = json.findValue("minute") != null?json.get("minute").toString():"0";
//            //小时
//            String hour = json.findValue("hour") != null?json.get("hour").toString():"0";
//            //天
//            String day = json.findValue("day") != null?json.get("day").toString():"0";
//            //周
//            String week = json.findValue("week") != null?json.get("week").toString():"0";
//            //计划类型
//            String type = json.get("triggerType").toString();
//
//            switch (type) {
//                //执行一次
//                case "cron":
//                    cronStr = "Cron表达式【"+json.get("cron")+"】";
//                    break;
//                //每N分钟执行一次
//                case "time":
//                    cronStr = "每"+time+"分钟执行一次";
//                    break;
//                //每天N时执行
//                case "day":
//                    cronStr = "每天"+hour+"时"+minute+"分执行";
//                    break;
//                //每周每周N执行
//                case "week":
//                    cronStr = "每"+buildWeekdaysLog(week)+"的"+hour+"时"+minute+"分执行";
//                    break;
//                //每月的某些天执行
//                case "month":
//                    cronStr = "每月"+builddaysLog(day)+"的"+hour+"时"+minute+"分执行";
//                    break;
//                default:
//                    break;
//            }
//        }
//        return cronStr;
//    }
//
//    private static String buildWeekdaysLog(String str) throws IOException{
//        StringBuilder weeks = new StringBuilder();
//        ArrayNode split = (ArrayNode) JsonUtil.toJsonNode(str);
//        for (Object object : split) {
//            switch (object.toString()) {
//                case "MON":
//                    weeks.append("周一，");
//                    break;
//                case "TUE":
//                    weeks.append("周二，");
//                    break;
//                case "WED":
//                    weeks.append("周三，");
//                    break;
//                case "THU":
//                    weeks.append("周四，");
//                    break;
//                case "FRI":
//                    weeks.append("周五，");
//                    break;
//                case "SAT":
//                    weeks.append("周六，");
//                    break;
//                case "SUN":
//                    weeks.append("周日，");
//                    break;
//                default:
//                    break;
//            }
//        }
//        return weeks.toString();
//    }
//
//    private static String builddaysLog(String str) throws IOException{
//        StringBuilder days = new StringBuilder();
//        ArrayNode split = (ArrayNode) JsonUtil.toJsonNode(str);
//        for (Object object : split) {
//            if(!"L".equals(object.toString())){
//                days.append(object.toString()+"日，");
//            }else{
//                days.append("最后一天，");
//            }
//        }
//        return days.toString();
//    }
//
//
//
//
//    public static String getDbCronByCode(String code) throws IOException{
//        String cron = "";
//        PropertiesDao propertiesDao = AppUtil.getBean(PropertiesDao.class);
//        Properties properties = propertiesDao.getByCode(code);
//        if(BeanUtils.isNotEmpty(properties)){
//            cron = getScheduledCron(properties.getValue());
//        }
//        return cron;
//    }
//
//
//}

