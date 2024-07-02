package com.nx.redis.serializer;

import lombok.Getter;

public enum SerializerTypeEnum {
    Kryo("kryo"),Json("json");

    @Getter
    private String code;
    SerializerTypeEnum(String code) {
        this.code = code;
    }

    public static SerializerTypeEnum valueOfByCode(String code){
        for (SerializerTypeEnum value : SerializerTypeEnum.values()) {
            if (code.equalsIgnoreCase(value.code)){
                return value;
            }
        }
        return null;
    }
}
