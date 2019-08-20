package com.example.springboot_batch_sample_07.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.springboot_batch_sample_07.batch.xyz.BatchXyzConfig;
import com.example.springboot_batch_sample_07.web.form.HelloForm;

@Controller
@RequestMapping("/hello")
public class HelloController {

    @Autowired
    private BatchXyzConfig batchXyz;

    @RequestMapping
    public String index(HelloForm helloForm) {
        return "hello";
    }

    @RequestMapping(params = { "action=button1" })
    public String button1(HelloForm helloForm) {
        batchXyz.exec();
        return "hello";
    }
}
