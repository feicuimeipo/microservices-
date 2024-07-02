package com.nx.gateway.route.model;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author nxl
 * 过滤器定义模型
 */
@Slf4j
@Data
public class GatewayFilterDefinition {

    @Valid
    @NotEmpty
    private String name;

    /**
     * 对应的路由规则
     */
    private Map<String, String> args = new LinkedHashMap<String, String>();


}
