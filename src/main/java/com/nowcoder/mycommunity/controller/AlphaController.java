package com.nowcoder.mycommunity.controller;

import com.nowcoder.mycommunity.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @ResponseBody
    @RequestMapping("/hello")
    public String sayHello(){
        return "hello world";
    }

    @ResponseBody
    @RequestMapping("/data")
    public String getData(){
        return alphaService.find();
    }
}
