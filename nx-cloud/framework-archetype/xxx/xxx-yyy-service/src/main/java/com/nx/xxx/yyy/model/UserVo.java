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
@NoArgsConstructor
@ApiModel(value = "UserVo",description = "用户查询返回结果")
public class UserVo implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="帐号t",name = "accoun")
    protected String account;

    @ApiModelProperty(value="地址",name = "address")
    protected String address;

    @ApiModelProperty(value="邮箱",name = "email")
    protected String email;
}
