/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.model;


import com.pharmcube.api.context.HttpStatus;

/**
 * 请求错误枚举
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2020年4月4日
 */

public enum ResultCode {

	SYSTEM_ERROR("-001","系统异常"),
	BAD_REQUEST("-002","错误的请求参数"),
	NOT_FOUND_PATH("-003","找不到请求路径"),
	CONNECTION_ERROR("-004","网络连接请求失败"),
	METHOD_NOT_ALLOWED("-005","不合法的请求方式"),
	DATABASE_ERROR("-004","数据库异常"),
	BOUND_STATEMENT_NOT_FOUNT("-006","找不到方法"),
	REPEAT_REGISTER("001","重复注册"),
	NO_USER_EXIST("002","用户不存在"),
	INVALID_PASSWORD("003","密码错误"),
	NO_PERMISSION("004","非法请求"),
	SUCCESS_OPTION("005","操作成功"),
	NOT_MATCH("-007","用户名和密码不匹配"),
	FAIL_GETDATA("-008","获取信息失败"),
	BAD_REQUEST_TYPE("-009","错误的请求类型"),
	REQUIRED_ERROR("-010","有必填的参数未传入"),
	FAIL_OPTION("-011","操作失败"),
	REPEAT_MOBILE("014","已存在此手机号"),
	REPEAT_EMAIL("015","已存在此邮箱地址"),
	NO_RECORD("016","没有查到相关记录"),
	LOGIN_SUCCESS("017","登陆成功"),
	LOGOUT_SUCCESS("018","已退出登录"),
	SENDEMAIL_SUCCESS("019","邮件已发送，请注意查收"),
	EDITPWD_SUCCESS("020","修改密码成功"),
	No_FileSELECT("021","未选择文件"),
	FILEUPLOAD_SUCCESS("022","上传成功"),
	NOLOGIN("023","未登陆"),
	ILLEGAL_ARGUMENT("024","参数不合法"),
	ERROR_IDCODE("025","验证码不正确"),
	CERT_ERROR("026","系统授权异常"),
	WORKFLOW_ERROR("027","流程异常"),
	LOG_SAVE_ERROR("028","日志记录错误"),
	FEIGN_EMPTY_ERROR("029","Feign未找到对应微服务"),
	DATASOURCE_ERROR("030","数据源异常"),
	SERVICE_INVOKE_ERROR("031","服务接口调用异常"),
	WEBSERVICE_PARSE_ERROR("032","Webservice接口解析异常"),
	BPM_PROCESS("040","流程定义异常，不允许有多个process"),


	/**
	 * 操作成功
	 */
	SUCCESS_0("0",""),

	/**
	 * 操作成功
	 */
	SUCCESS(Integer.valueOf(HttpStatus.SUCCESS).toString(),""),


	/**
	 * 业务异常
	 */
	FAILURE(Integer.valueOf(HttpStatus.ERROR).toString(),"业务异常"),

	/**
	 * 请求未授权
	 */
	UN_AUTHORIZED(Integer.valueOf(HttpStatus.UNAUTHORIZED).toString(),"请求未授权"),


	/**
	 * 404 没找到请求
	 */
	NOT_FOUND(Integer.valueOf(HttpStatus.NOT_FOUND).toString(),"404 没找到请求"),

	/**
	 * 消息不能读取
	 */
	MSG_NOT_READABLE(Integer.valueOf(HttpStatus.BAD_REQUEST).toString(),"输入非法字符"),

	/**
	 * 不支持当前请求方法
	 */
	METHOD_NOT_SUPPORTED(Integer.valueOf(HttpStatus.BAD_METHOD).toString(),"不支持当前请求方法"),

	/**
	 * 不支持当前媒体类型
	 */
	MEDIA_TYPE_NOT_SUPPORTED(Integer.valueOf(HttpStatus.UNSUPPORTED_TYPE).toString(),"不支持当前媒体类型"),

	/**
	 * 请求被拒绝
	 */
	REQ_REJECT(Integer.valueOf(HttpStatus.FORBIDDEN).toString(),"请求被拒绝"),

	/**
	 * 服务器异常
	 */
	INTERNAL_SERVER_ERROR(Integer.valueOf(HttpStatus.INTERNAL_SERVER_ERROR).toString(),"服务器异常"),

	/**
	 * 缺少必要的请求参数
	 */
	PARAM_MISS(Integer.valueOf(HttpStatus.ERROR).toString(),"缺少必要的请求参数"),

	/**
	 * 请求参数类型错误
	 */
	PARAM_TYPE_ERROR(Integer.valueOf(HttpStatus.BAD_REQUEST).toString(),"请求参数类型错误"),

	/**
	 * 请求参数绑定错误
	 */
	PARAM_BIND_ERROR(Integer.valueOf(HttpStatus.BAD_REQUEST).toString(),"请求参数绑定错误"),

	/**
	 * 参数校验失败
	 */
	PARAM_VALID_ERROR(Integer.valueOf(HttpStatus.BAD_REQUEST).toString(),"参数校验失败"),


	/**
	 * 参数校验失败
	 */
	LOG_CONTTEXT_NOT_FOUND(Integer.valueOf(HttpStatus.INTERNAL_SERVER_ERROR).toString(),"LogPublisherContext需要实例化并注册为spring组件！")
	;





	private String code;
	private String message;

	private ResultCode(String code, String message) {
		this.code = code;
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
