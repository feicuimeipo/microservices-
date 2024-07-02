package com.nx.common.model.bo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@ToString
@EqualsAndHashCode
public abstract class BaseModel implements java.io.Serializable {

    protected String tenantId;

    protected String version;
}
