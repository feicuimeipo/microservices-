package com.nx.logger.model.api;


import com.nx.logger.model.api.dto.AccessLogRpcDTO;

import javax.validation.Valid;

/**
 * rpc接口访问日志
 */
public interface AccessLogRpcApi {

    /**
     * 创建 API 访问日志
     *
     * @param createDTO 创建信息
     */
    void createRpcAccessLog(@Valid AccessLogRpcDTO createDTO);


}
