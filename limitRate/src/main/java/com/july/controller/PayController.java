package com.july.controller;

import com.july.anno.AccessLimitAnno;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;


@RestController
public class PayController {

    @RequestMapping("/pay")
//    @AccessLimitAnno(limitKey = "pay",limit = 3,sec = 5)
    public String sendPayment(@RequestParam(required = true) String name) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss:SSS");

        String x = "业务处理时间:" + formatter.format(new Date()) + ",业务处理-------" + name;
        System.out.println(x);

//        int a = 1/0;

        return "支付成功 no anno:" + x;


    }

    @RequestMapping("/pay1")
    @AccessLimitAnno(limitKey = "pay",limit = 3,sec = 5)
    public String sendPayment1(@RequestParam(required = true) String name) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss:SSS");

        String x = "业务处理时间:" + formatter.format(new Date()) + ",业务处理-------" + name;
        System.out.println(x);


        return "支付成功 :" + x;


    }
}

