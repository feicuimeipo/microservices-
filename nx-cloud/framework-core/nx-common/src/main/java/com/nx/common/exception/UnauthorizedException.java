package com.nx.common.exception;




public class UnauthorizedException extends BaseException {

    private static final long serialVersionUID = 1L;

    private static final String MSG = "401 Unauthorized";

    public UnauthorizedException() {
        super(401, MSG);
    }

    public UnauthorizedException(int code,String msg) {
        super(code, msg);
    }

    public UnauthorizedException(int code,Throwable e) {
        super(e);
        this.code = code;
        this.msg = e.getMessage();
    }

    public UnauthorizedException(Throwable e) {
        super(e);
        this.code = 401;
        this.msg = e.getMessage();
    }

}
