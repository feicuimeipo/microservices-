package com.nx.xxx.yyy.api.dto;

import com.nx.api.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName OrderInfo
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/7 1:58
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloDTO extends BaseModel{
    private String uid;
    private String name;

}
