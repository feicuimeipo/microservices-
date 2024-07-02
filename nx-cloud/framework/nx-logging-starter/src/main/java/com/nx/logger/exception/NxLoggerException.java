package com.nx.logger.exception;


import com.nx.common.exception.BaseException;


public class NxLoggerException extends BaseException {

    public NxLoggerException(int code) {
        super(code);
    }

    public NxLoggerException(String message) {
        super(message);
    }

    public NxLoggerException(int code, String message) {
        super(code, message);
    }

    public NxLoggerException(int code, String message, Throwable e) {
        super(code, message, e);
    }


    public NxLoggerException(Throwable cause) {
        super(cause);
    }

    public NxLoggerException(String message, Throwable cause) {
        super(message, cause);
    }
}
