package com.nx.cache.enums;

import lombok.Getter;

public enum CacheModeEnum {
    Both("both"),Use1LevelCache("use1LevelCache"),Use2LevelCache("use2LevelCache");

    @Getter
    private String code;
    CacheModeEnum(String code) {
        this.code = code;
    }

    private CacheModeEnum valueOfByCode(String code){
        for (CacheModeEnum value : CacheModeEnum.values()) {
            if (value.code.equalsIgnoreCase(code)){
                return value;
            }
        }
        return null;
    }
}
