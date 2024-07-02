/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.model.bo;

/**
 * 文章。
 * <pre>
 * {
 * "title": "Title",
   "description": "Description",
   "url": "URL",
   "picurl": "PIC_URL"
   }
   </pre>
 * @author ray
 *
 */
public class WxArticle {
	
	
	/**
	 * 标题。
	 */
	private String title="";
	/**
	 * 描述。
	 */
	private String description="";
	/**
	 * 地址。
	 */
	private String url="";
	
	/**
	 * 地址。
	 */
	private String picurl="";
	
	public WxArticle(){}
	
	public WxArticle(String title,String desc,String url,String picurl){
		this.title=title;
		this.description=desc;
		this.url=url;
		this.picurl=picurl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}
	
	
	

}
