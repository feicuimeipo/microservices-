package com.nx.logger.model.api;

import com.nx.logger.model.api.dto.ActionLogDTO;

import javax.validation.Valid;

//TODO
public interface ActionLogApi {
    /**
     * 创建 API 访问日志
     *
     * @param createDTO 创建信息
     */
    void createActionLog(@Valid ActionLogDTO createDTO);
}
