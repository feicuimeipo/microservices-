/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.index;

import com.pharmcube.mybatis.support.query.PageList;

/**
 * 首页tab
 * 
 * @author hugh zhuang
 * 
 */
public class IndexTab {

	// 标题
	protected String title;
	// key
	protected String key;

	// 徽章
	protected String badge;

	protected boolean active = false;

	protected PageList<?> list;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getBadge() {
		return badge;
	}

	public void setBadge(String badge) {
		this.badge = badge;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public PageList<?> getList() {
		return list;
	}

	public void setList(PageList<?> list) {
		this.list = list;
	}

}
