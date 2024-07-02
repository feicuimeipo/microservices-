package com.nx.xxx.yyy.api.dto;

import com.nx.api.model.BaseModel;
import lombok.Data;

/**
 * @ClassName SayHello
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/7 2:02
 * @Version 1.0
 **/
@Data
public class SayHelloReq extends BaseModel {
    private String hello;
}
