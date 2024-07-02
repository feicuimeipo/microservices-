package com.nx.logger.model.api;


import com.nx.logger.model.api.dto.OperateLogDTO;

import javax.validation.Valid;

/**
 * 操作日志 API 接口
 *
 * @author 芋道源码
 */
public interface OperateLogApi {

    /**
     * 创建操作日志
     *
     * @param createReqDTO 请求
     */
    void createOperateLog(@Valid OperateLogDTO createReqDTO);

}
