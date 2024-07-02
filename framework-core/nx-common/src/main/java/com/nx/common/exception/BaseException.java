package com.nx.common.exception;

import com.nx.common.model.constant.ResultCode;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 内部逻辑发生错误抛出的异常 (自定义此异常方便开发者在做全局异常处理时分辨异常类型)
 */
@Data
public class BaseException extends RuntimeException {

    public static final int CODE_UNDEFINED = -1;
    protected static final long serialVersionUID = 6806129545290130132L;
    protected int code = CODE_UNDEFINED;
    protected String msg;

    public BaseException(int code) {
        super();
        this.code = code;
    }

    public BaseException(String message) {
        super(message);
        this.msg = message;
    }

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
        this.msg = message;
    }


    public BaseException(int code, String message, Throwable e) {
        super(message, e);
        this.code = code;
        this.msg = message;
    }

    public BaseException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }


    
    public BaseException(Throwable cause) {
        super(cause);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getCode() {
        return code;
    }

    public BaseException setCode(int code) {
        this.code = code;
        return this;
    }

    public static void throwBy(boolean flag, String message) {
        if (flag) {
            throw new BaseException(message);
        }
    }

    public static void throwByNull(Object value, String message) {
        if (StringUtils.isEmpty(value)) {
            throw new BaseException(message);
        }
    }

}
