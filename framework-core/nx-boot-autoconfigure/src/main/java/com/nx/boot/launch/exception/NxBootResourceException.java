package com.nx.boot.launch.exception;

import com.nx.common.exception.BaseException;
import com.nx.common.model.constant.ResultCode;

public class NxBootResourceException extends BaseException {
    public NxBootResourceException(int code) {
        super(code);
    }

    public NxBootResourceException(String message) {
        super(message);
    }

    public NxBootResourceException(int code, String message) {
        super(code, message);
    }

    public NxBootResourceException(int code, String message, Throwable e) {
        super(code, message, e);
    }

    public NxBootResourceException(ResultCode resultCode) {
        super(resultCode);
    }

    public NxBootResourceException(Throwable cause) {
        super(cause);
    }

    public NxBootResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
