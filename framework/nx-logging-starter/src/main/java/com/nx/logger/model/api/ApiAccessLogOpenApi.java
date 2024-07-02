package com.nx.logger.model.api;


import com.nx.logger.model.api.dto.AccessLogOpenApiDTO;

import javax.validation.Valid;

/**
 * API 访问日志的 API 接口
 *
 * @author 芋道源码
 */
public interface ApiAccessLogOpenApi {

    /**
     * 创建 API 访问日志
     *
     * @param createDTO 创建信息
     */
    void createOpenApiAccessLog(@Valid AccessLogOpenApiDTO createDTO);


}
