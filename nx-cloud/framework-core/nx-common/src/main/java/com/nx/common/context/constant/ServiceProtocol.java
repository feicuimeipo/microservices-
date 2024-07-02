package com.nx.common.context.constant;

public enum ServiceProtocol {
    feign("feign"),
    dubbo("dubbo"),
    ;
    private String code;

    ServiceProtocol(String code) {
        this.code = code;
    }
    public String code(){
        return code;
    }

    public String getCode() {
        return getCode();
    }
}
