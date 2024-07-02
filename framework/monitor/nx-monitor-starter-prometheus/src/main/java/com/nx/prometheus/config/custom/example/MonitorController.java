package com.nx.prometheus.config.custom.example;


import com.nx.prometheus.config.custom.annotation.Monitor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 * @ClassName MonitorController
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/21 19:04
 * @Version 1.0
 **/

@RestController
@RequestMapping("/")
public class MonitorController {

    @Monitor(description = "/monitor/test")
    @GetMapping("/test")
    public String monitorTest(@RequestParam("name") String name) {
       //monitorServiceImpl.monitorTest(name);
        return "监控示范工程测试接口返回->OK!";
    }
}