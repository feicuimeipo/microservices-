package com.nx.auth.api.dto;

import com.nx.api.model.BaseModel;
import lombok.Data;

import java.util.Set;

@Data
public class TenantHandlerDTO extends BaseModel<TenantManageDTO> {
    private Set<String> ignoreTablePrefix;
    private Set<String> ignoreTables;
    private Set<String> ignoreMenu;
    private String tenantCode;
    private String tenantId;
    
}
