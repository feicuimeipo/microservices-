package com.nx.xxx.yyy.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName UcUserBo
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/10 11:33
 * @Version 1.0
 **/
@Data
@ApiModel(value = "UserBo",description = "用户查询参数")
@NoArgsConstructor
public class UserBo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="account",name = "帐号")
    protected String account;

}
