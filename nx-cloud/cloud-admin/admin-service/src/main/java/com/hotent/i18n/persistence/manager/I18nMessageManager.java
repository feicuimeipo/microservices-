/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.i18n.persistence.manager;

import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nianxi.mybatis.support.query.PageList;
import org.nianxi.mybatis.support.query.QueryFilter;
import org.springframework.web.multipart.MultipartFile;
import org.nianxi.mybatis.support.manager.BaseManager;
//import org.nianxi.mybatis.support.query.PageList;
//import org.nianxi.mybatis.support.query.QueryFilter;
import com.hotent.i18n.persistence.model.I18nMessage;

/**
 * 
 * <pre> 
 * 描述：国际化资源 处理接口
 * @author zhaoxy
 * @company 广州宏天软件股份有限公司
 * @email zhxy@jee-soft.cn
 * @date 2018-06-06 14:20
 * </pre>
 */
public interface I18nMessageManager extends BaseManager<I18nMessage> {
	/**
	 * 获取国际化资源列表数据
	 * @param filter
	 * @return
	 */
	PageList<Map<String,String>> getList(QueryFilter filter);
	
	/**
	 * 根据key获取国际化资源（各种类型的查询集合，不是单纯的单条记录）
	 * @param key
	 * @param dbType
	 * @return
	 */
	Map<String,Object> getByMesKey(String key,String dbType);
	
	/**
	 * 根据国际化资源key删除资源
	 * @param key
	 */
	void delByKey(String key);
	
	/**
	 * 根据key和type删除国际化资源
	 * @param key
	 * @param type
	 */
	void delByKeyAndType(String key, String type);
	
	void delByKeys(String... keys);
	
	/**
	 * 保存国际化资源
	 * @param key
	 * @param mesTypeInfo
	 * @param oldKey
	 */
	Map<String,Object> saveI18nMessage(String key, List<Map<String, String>> mesTypeInfo, String oldKey);
	
	/**
	 * 从excel导入国际化资源
	 * @param file
	 * @return
	 */
	Map<String, Object> importMessage(MultipartFile file) throws Exception;
	
	/**
	 * 导出国际化资源
	 */
	HSSFWorkbook exportExcel() throws Exception ;
	
	/**
	 * 国际化资源
	 * @param val
	 * @return
	 */
	List<Map<String,String>> getSearchList(String val);
}
