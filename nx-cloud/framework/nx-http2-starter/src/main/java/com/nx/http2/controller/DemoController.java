package com.nx.http2.controller;

import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.PushBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * @ClassName DemoController
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/23 11:49
 * @Version 1.0
 **/
@RestController("/http2/")
public class DemoController {

    @GetMapping("/demo")
    public void http2ServerPush(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PushBuilder pushBuilder = request.newPushBuilder();
        pushBuilder
                .path("/demo.png")
                .addHeader("content-type", "image/png")
                .push();
        try(PrintWriter respWriter = response.getWriter()){
            respWriter.write("<html>" +
                    "<img src='/demo.png'>" +
                    "</html>");
        }
    }

    @GetMapping(value = "/demo.png")
    public void download(HttpServletResponse response) throws IOException {
        InputStream data = getClass().getClassLoader().getResourceAsStream("demo.png");
        response.setHeader("content-type", "image/png");
        FileCopyUtils.copy(data,response.getOutputStream());
    }
}
