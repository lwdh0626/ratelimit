package com.july;

import redis.clients.jedis.Jedis;

/**
 */
public class Demo {
    public static void main(String[] args) {
        //拿到redis的连接
        Jedis jedis =  new Jedis("127.0.0.1",6379);
        //限流的key
        String key = "limit-demo";

        for(int i=0;i<10;i++){
            boolean result =true; //判断是否限流(true代表没有限制，false代表被限制了)
            long aferValue = jedis.incr(key);//对key的value进行+1操作
            if(aferValue==1){//第一次 有效时间 TTL :-1  ->60
                System.out.println("第一次!");
                jedis.expire(key,60);//设置key的失效时间
            }else{//不是第一次
                if(aferValue>5){//判断是否超过限制(10次)
                    result =false;
                }
            }
            System.out.println("限流结果:"+result);
        }

    }
}