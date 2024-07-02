/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.index;

import java.util.List;

import com.pharmcube.mybatis.support.query.PageBean;


public class IndexTabList {

	protected String curTab;

	private PageBean pageBean = new PageBean(0, 10);

	protected List<IndexTab> indexTabList;

	public List<IndexTab> getIndexTabList() {
		return indexTabList;
	}

	public void setIndexTabList(List<IndexTab> indexTabList) {
		this.indexTabList = indexTabList;
	}

	public String getCurTab() {
		return curTab;
	}

	public void setCurTab(String curTab) {
		this.curTab = curTab;
	}

	public PageBean getPageBean() {
		return pageBean;
	}

	public void setPageBean(PageBean pageBean) {
		this.pageBean = pageBean;
	}

}
