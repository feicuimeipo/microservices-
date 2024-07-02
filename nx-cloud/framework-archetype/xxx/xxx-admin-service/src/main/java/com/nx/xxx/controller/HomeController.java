package com.nx.xxx.controller;


import com.nx.api.model.R;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@Schema(description = "首页")
public class HomeController {

    @Value("${spring.application.name:''}")
    private String applicationName;

    //@ActionLogger(bizId = {"id"}, action = "bbb",detail = "dd",buryingPointCode = "1001")
    @GetMapping("/")
    @Schema(name = "主页面",  description = "根据用户开始时间和结束时间，获取这段时间的有效工时")
    public R<String> home(){
        return R.OK("you are welcome to "+ applicationName +"!");
    }


}
