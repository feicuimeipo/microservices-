package com.nx.common.exception;


import com.nx.common.model.constant.ResultCode;
import lombok.EqualsAndHashCode;

/**
 * 服务器异常 Exception
 */
@EqualsAndHashCode(callSuper = true)
public final class ServerException extends BaseException {



    public ServerException(int code) {
        super(code);
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(int code, String message) {
        super(code, message);
    }

    public ServerException(int code, String message, Throwable e) {
        super(code, message, e);
    }

    public ServerException(ResultCode resultCode) {
        super(resultCode);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }


    public ServerException setCode(Integer code) {
        this.code = code;
        return this;
    }

}
