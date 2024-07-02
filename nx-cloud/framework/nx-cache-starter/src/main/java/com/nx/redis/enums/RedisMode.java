package com.nx.redis.enums;


import lombok.Getter;

@Getter
public enum RedisMode {
    standalone("standalone"),sentinel("sentinel"),cluster("cluster");

    private String code;
    RedisMode(String code) {
        this.code = code;
    }

    public static RedisMode valueOfCode(String code){
        for (RedisMode value : RedisMode.values()) {
            if (code.equals(value.code)){
                return value;
            }
        }
        return null;
    }
}
