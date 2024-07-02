package com.nx.xxx.yyy.apimpl;


import com.nx.api.model.R;
import com.nx.api.model.query.PageList;
import com.nx.api.model.query.PageInfo;
import com.nx.xxx.yyy.api.HelloService;
import com.nx.xxx.yyy.api.dto.HelloDTO;
import com.nx.xxx.yyy.api.dto.SayHelloReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName HelloServiceImpl
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/7 2:04
 * @Version 1.0
 **/
@Slf4j
@DubboService(group = "${spring.profiles.active}",version = "${dubbo.version.default}")
public class HelloServiceImpl implements HelloService {
    @Override
    public R<List<HelloDTO>> listHello(String[] ids) {
        log.info("ids="+ids.toString());
        List<HelloDTO> list = new ArrayList<>();
        list.add(new HelloDTO("hello1","hello-name1"));
        list.add(new HelloDTO("hello2","hello-name2"));
        list.add(new HelloDTO("hello3","hello-name3"));
        return R.OK(list);
    }

    @Override
    public R<HelloDTO> getHelloById(String uid) {
        return R.FAIL(500,"错误！");
    }

    @Override
    public R<PageList<HelloDTO>> listHelloByPage(PageInfo pageInfo) {
        return null;
    }

    @Override
    public R<String> sayHello(SayHelloReq req) {
        return null;
    }
}
