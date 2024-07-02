package com.nx.cloud.protection.resilience4j.exception;


import com.nx.common.exception.BaseException;

public class Resilience4jException extends BaseException {

    public Resilience4jException(){
        super("500");
    }

    public Resilience4jException(int code) {
        super(code);
    }

    public Resilience4jException(String message) {
        super(message);
    }

    public Resilience4jException(int code, String message) {
        super(code, message);
    }

    public Resilience4jException(int code, String message, Throwable e) {
        super(code, message, e);
    }

    public Resilience4jException(Throwable cause) {
        super(cause);
    }

    public Resilience4jException(String message, Throwable cause) {
        super(message, cause);
    }
}
