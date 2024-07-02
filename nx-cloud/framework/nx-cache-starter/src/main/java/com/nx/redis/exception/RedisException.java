package com.nx.redis.exception;

import com.nx.common.exception.BaseException;

public class RedisException extends BaseException {
    private String groupName;
    public RedisException(String groupName) {
        super(500);
        this.msg = groupName + "为空或不存在！";

    }
}
