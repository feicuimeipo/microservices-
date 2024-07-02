package com.nx.storage.core.enums;

import lombok.Getter;

public enum StoProviderEnum {
    aliyun("aliyun"),huawei("huawei"),minio("minio"),aws("aws"),
    qcloud("qcloud"),qiniu("qiniu")
    ;

    @Getter
    private String code;
    StoProviderEnum(String code) {
        this.code = code;
    }

    public static StoProviderEnum valueByCode(String code){
        for (StoProviderEnum value : StoProviderEnum.values()) {
            if (value.code.equalsIgnoreCase(code)){
                return value;
            }
        }
        return null;
    }
    
}
