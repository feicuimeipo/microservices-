package com.nx.gateway.route.model;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.LinkedHashMap;
import java.util.Map;




/**
 * 路由断言定义模型
 * @Author nxl
 */
@Slf4j
@Data
@Validated
public class GatewayPredicateDefinition{
    //断言对应的Name
    @Valid
    @NotEmpty
    private String name;
    //配置的断言规则
    private Map<String,String> args = new LinkedHashMap<String,String>();
    //此处省略Get和Set方法
}
