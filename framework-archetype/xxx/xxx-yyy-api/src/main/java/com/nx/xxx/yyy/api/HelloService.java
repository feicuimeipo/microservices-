package com.nx.xxx.yyy.api;

import com.nx.api.model.R;
import com.nx.api.model.query.PageInfo;
import com.nx.api.model.query.PageList;
import com.nx.xxx.yyy.api.dto.HelloDTO;
import com.nx.xxx.yyy.api.dto.SayHelloReq;

import java.util.List;

/**
 * @ClassName OrderService
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/7 1:58
 * @Version 1.0
 **/
public interface HelloService {
    public R<List<HelloDTO>> listHello(String[] ids);

    public R<HelloDTO> getHelloById(String uid);

    public R<PageList<HelloDTO>> listHelloByPage(PageInfo pageInfo);

    public R<String> sayHello(SayHelloReq req);

}
