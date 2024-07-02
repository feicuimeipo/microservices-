/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.feign.dto.form;


import lombok.Getter;

public enum FormStoreType implements java.io.Serializable{

    boObject("boObject"),database("database"),
    mongodb("mongodb") //todo:
    ;

    @Getter
    private String oriType; //旧系统存储的标志

    /*
        public static final String SAVE_TYPE_FOLDER = "folder";
        public static final String SAVE_TYPE_FTP = "ftp";
        public static final String SAVE_TYPE_DTABASE = "database";
    }*/

    FormStoreType(String oriType) {
        this.oriType = oriType;
    }
}

