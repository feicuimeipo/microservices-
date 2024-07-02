package com.nx.gateway.controller;


import com.nx.api.model.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 *  * @author nianxiaioling
 *  * @version 1.0.0
 *  * @since 2020-03-06 9:34
 */
@RestController
@RequestMapping("/")
@Slf4j
public class HomeController {
    @GetMapping("/")
    public Mono<R<String>> home(){
        return Mono.just(R.OK("Your are welcome to gateway!"));
    }

}
