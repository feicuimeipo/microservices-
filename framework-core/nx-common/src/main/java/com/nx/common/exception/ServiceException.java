package com.nx.common.exception;

import com.nx.common.model.constant.ResultCode;
import lombok.EqualsAndHashCode;

/**
 * 业务逻辑异常 Exception
 */
@EqualsAndHashCode(callSuper = true)
public final class ServiceException extends BaseException {


    public ServiceException(int code) {
        super(code);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(int code, String message) {
        super(code, message);
    }

    public ServiceException(int code, String message, Throwable e) {
        super(code, message, e);
    }

    public ServiceException(ResultCode resultCode) {
        super(resultCode);
    }


    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
