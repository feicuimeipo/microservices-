package com.nx.auth.api.dto;

import com.nx.common.model.base.BaseModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
public class ApiResourceDTO extends BaseModel<ApiResourceDTO> {
    private String appId="basic";
    private Set<String> resources = new HashSet(){{add("/**");}}; //可访问的接口范围
}
