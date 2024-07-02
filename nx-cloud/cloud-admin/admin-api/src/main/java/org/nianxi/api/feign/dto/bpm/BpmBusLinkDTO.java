/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.feign.dto.bpm;


import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

/**
 * 对象功能:业务数据关联 entity对象 开发公司:广州宏天软件有限公司 开发人员:zyg 创建时间:2014-06-05 16:55:41
 */
@Data
@ToString
public class BpmBusLinkDTO implements java.io.Serializable{

	/**
	 * 主键 
	 */
	protected String id;
	/**
	 *流程定义ID 
	 */
	protected String defId;
	/**
	 *  流程实例ID 
	 */
	protected String procInstId;
	/**
	 *业务键 
	 */
	protected Long businesskey;
	/**
	 * 业务键字符型
	 */
	protected String businesskeyStr;
	/**
	 * 业务系统编码
	 */
	protected String sysCode;
	/**
	 * 表单标识
	 */
	protected String formIdentify;
	/**
	 * 发起人 
	 */
	protected String startId;
	/**
	 * 发起人 
	 */
	protected String startor;
	/**
	 * 创建时间 
	 */
	protected LocalDateTime createDate;
	/**
	 * 发起组织ID 
	 */
	protected String startGroupId;
	/**
	 * 发起组织 
	 */
	protected String startGroup;
	/**
	 * 是否主表记录
	 */
	protected int isMain = 1;

	/**
	 * 暂时存放bodefcode数据。
	 */
	protected  String boDefCode = "";
	
	/**
	 * 保存模式 (boObject,database);
	 */
	protected  String saveMode = "";
	



	@SuppressWarnings("unused")
	private static Boolean supportPartition = null;

	/** 判断是否支持分区表 目前支持 oracle mysql5.5 以上版本 */
	public static boolean isSupportPartition()
	{
		return false;
//		String isSupportPartition = PropertyUtil.getProperty("supportPartition", "true");
//		if (isSupportPartition.equals("false"))
//			return false;
//		if (supportPartition == null)
//		{
//			String dataType = PropertyUtil.getJdbcType();
//			if (dataType.equals("oracle"))
//			{
//				supportPartition = true;
//			} else if (dataType.equals("mysql"))
//			{
//				BpmBusLinkDao dao = AppUtil.getBean(BpmBusLinkDao.class);
//				String mysqlVersion = dao.getMysqlVersion();
//				double varsion = Double.parseDouble(mysqlVersion.substring(0, 3));
//				if (varsion >= 5.5)
//					supportPartition = true;
//				else
//					supportPartition = false;
//			} else
//			{
//				supportPartition = false;
//			}
//		}
//		return supportPartition;
	}


}