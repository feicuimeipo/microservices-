package com.nx.cache.enums;

import lombok.Getter;

public enum LocalCacheTypeEnum {
    caffeine("caffeine"),guava("guava");

    @Getter
    private String code;
    LocalCacheTypeEnum(String code) {
        this.code = code;
    }

    public static LocalCacheTypeEnum valueOfByCode(String code){
        for (LocalCacheTypeEnum value : LocalCacheTypeEnum.values()) {
            if (value.code.equalsIgnoreCase(code)){
                return value;
            }
        }
        return null;
    }
}
