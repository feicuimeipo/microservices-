package com.nx.common.exception;

/**
 * 一个异常：代表 功能已废弃 已被禁用
 *
 * @author kong
 */
public class DisabledException extends BaseException {

    //序列化版本号
    private static final long serialVersionUID = 6806129545290130133L;

    //异常提示语
    public static final String BE_MESSAGE = "this api is disabled";

    //一个异常：代表 API 已被禁用
    public DisabledException() {
        super(BE_MESSAGE);
    }

    //一个异常：代表 API 已被禁用
    public DisabledException(String message) {
        super(message);
    }

}
