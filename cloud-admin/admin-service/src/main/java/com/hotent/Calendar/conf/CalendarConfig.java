/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.Calendar.conf;

import com.hotent.Calendar.calc.ICalendarCalc;
import com.hotent.Calendar.calc.impl.AbsenceCalc;
import com.hotent.Calendar.calc.impl.OverCalc;
import com.hotent.Calendar.calc.impl.ShiftCalc;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TreeMap;

@Configuration
public class CalendarConfig {
	
	@Bean(name="shiftCalc")
	public ShiftCalc shiftCalc(){
		return  new ShiftCalc();
	}
	
	@Bean(name="absenceCalc")
	public AbsenceCalc absenceCalc(){
		return  new AbsenceCalc();
	}
	
	@Bean(name="overCalc")
	public OverCalc overCalc(){
		return  new OverCalc();
	}
	
	@Bean(name="calendarCalcMap")
	public TreeMap<Integer, ICalendarCalc> pluginExecutionCommand(
			@Qualifier("shiftCalc") ShiftCalc shiftCalc,
			@Qualifier("absenceCalc") AbsenceCalc absenceCalc,
			@Qualifier("overCalc") OverCalc overCalc){
		TreeMap map = new TreeMap<Integer, ICalendarCalc>();
		map.put(1, shiftCalc);
		map.put(2, absenceCalc);
		map.put(3, overCalc);
		return  map;
	}
	
	
}
