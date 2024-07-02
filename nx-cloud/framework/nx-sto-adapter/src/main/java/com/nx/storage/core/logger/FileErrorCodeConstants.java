package com.nx.storage.core.logger;

import com.nx.common.model.constant.ResultCode;

public interface FileErrorCodeConstants {
    // ========= 文件相关 1001003000=================
    ResultCode FILE_PATH_EXISTS = new ResultCode(1001003000, "文件路径已存在");
    ResultCode FILE_NOT_EXISTS = new ResultCode(1001003001, "文件不存在");
    ResultCode FILE_IS_EMPTY = new ResultCode(1001003002, "文件为空");
}
