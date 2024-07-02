package com.nx.http2.controller;


import com.nx.common.model.Result;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName HomeController
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/23 11:44
 * @Version 1.0
 **/
public class HomeController {


    /**
     * curl -Ik --http2 https://localhost:8443/hello
     * @return
     */
    @GetMapping("/hello")
    public Result<String> index(){
        return Result.OK("hello");
    }

}
