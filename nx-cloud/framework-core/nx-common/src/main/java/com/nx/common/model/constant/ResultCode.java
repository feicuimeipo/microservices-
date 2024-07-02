package com.nx.common.model.constant;

import lombok.Data;

@Data
public class ResultCode implements java.io.Serializable{

    private int code;
    private String msg;

    public ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public static final ResultCode SUCCESS_0 = new ResultCode(0, "");
    public static final ResultCode SUCCESS = new ResultCode(HttpStatus.OK, "");
    // ========== 客户端错误段 ==========
    public static final ResultCode   PARAM_TYPE_ERROR = new ResultCode(HttpStatus.BAD_REQUEST, "请求参数不正确");
    public static final ResultCode   UN_AUTHORIZED = new ResultCode(HttpStatus.UNAUTHORIZED, "请求未授权");
    public static final ResultCode   FORBIDDEN = new ResultCode(HttpStatus.FORBIDDEN, "请求被拒绝");
    public static final  ResultCode  NOT_FOUND = new ResultCode(HttpStatus.NOT_FOUND, "404 没找到请求");
    public static final  ResultCode  METHOD_NOT_ALLOWED = new ResultCode(HttpStatus.METHOD_NOT_ALLOWED, "不支持当前请求方法");
    public static final ResultCode   LOCKED = new ResultCode(423, "不支持当前请求方法");
    public static final ResultCode   LOCKED_TOO_MANY_REQUESTS = new ResultCode(429, "请求过于频繁，请稍后重试");
    public static final ResultCode   MEDIA_TYPE_NOT_SUPPORTED = new ResultCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "不支持当前媒体类型");
    public static final ResultCode   REPEATED_REQUESTS = new ResultCode(900,"重复请求，请稍后重试");
    //网络层
    public static final ResultCode   SYSTEM_ERROR = new ResultCode(-001, "系统异常");
    public static final ResultCode   CONNECTION_ERROR = new ResultCode(-004, "网络连接请求失败");
    //服务
    public static final ResultCode   FEIGN_EMPTY_ERROR = new ResultCode(29,"Feign未找到对应微服务");
    //服务端
    public static final ResultCode   INTERNAL_SERVER_ERROR = new ResultCode(HttpStatus.INTERNAL_SERVER_ERROR, "服务器异常");
    public static final ResultCode   BAD_REQUEST = new ResultCode(HttpStatus.INTERNAL_SERVER_ERROR, "请求异常");
    public static final ResultCode  DEMO_DENY = new ResultCode(901, "演示模式，禁止写操作");

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 是否为服务端错误，参考 HTTP 5XX 错误码段
     *
     * @param code 错误码
     * @return 是否
     */
    public static boolean isServerErrorCode(Integer code) {
        return code != null
                && code >= INTERNAL_SERVER_ERROR.getCode() && code <= INTERNAL_SERVER_ERROR.getCode() + 99;
    }
}
