package com.nx.gateway.conf.hystrix;

import com.nx.api.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName SelfHystrixController
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/12 19:18
 * @Version 1.0
 **/
@RestController
@Slf4j
public class DefaultHystrixController {

    @RequestMapping("/defaultfallback")
    public R<String> defaultfallback(){
       log.info("请求熔断");
       return R.FAIL(601,"网络繁忙服务降级请稍后再访问");
    }

    /**
     * 发起Get 请求 ：localhost:8000/gateway/timeout
     * 会进行熔断
     * @return
     */
    @RequestMapping("/gateway/test/timeout")
    public String timeout(){
        try {
            Thread.sleep(15000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Hello, I am timeout return. If normal, you can not find me.";
    }



}
