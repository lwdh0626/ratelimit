package com.july.controller;

import com.july.Isacquire;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;



@RestController
//http请求控制类  Contoller
public class PayController {
//    @Autowired
//    private UserserviceImpl userservice;

    //订单支付接口 url:/pay
    @RequestMapping("/pay")
    public String sendPayment(@RequestParam(required = true) String name) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss:SSS");
        Isacquire isacquire = new Isacquire();//分布式限流类实例化
        if(isacquire.acquire()){//分布式限流判断
            try {
                //简化业务处理，保存记录信息
//                User use = new User(name);
//                userservice.save(use);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("业务处理时间:" + formatter.format(new Date()) + ",业务处理-------" + name);
            return "支付成功";
        }else{
            System.out.println("被限流了！");
            return "被限流了！";
        }

    }

}

