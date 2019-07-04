package com.demo2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * 1 使用redis+lua脚本实现接口限流
 *   基于Redis的令牌桶算法限流策略实现  测试通过
 * @author lmc
 * 2018年12月21日
 */
@Service
public class RateLimitClient {

    @Autowired
    StringRedisTemplate redisTemplate;

    //这里需要到redis配置文件下配置相关bean
    @Qualifier("ratelimitInitLua")
    @Resource
    RedisScript<Long> ratelimitInitLua;


    public boolean initToken(String key){
        boolean token;
        //此序列化redis会把数字类字符串存为数字类型
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        Long currMillSecond = redisTemplate.execute(
                (RedisCallback<Long>) redisConnection -> redisConnection.time()
        );
        /** 初始化接收到的参数
         * redis.pcall("HMSET",KEYS[1],  "last_mill_second",ARGV[1],  "curr_permits",ARGV[2],
         * "max_burst",ARGV[3], "rate",ARGV[4], "app",ARGV[5])
         */
        StringBuffer ratelimitStr= new StringBuffer();
        ratelimitStr.append(" local result=1 ");
        ratelimitStr.append(" redis.pcall('HMSET',KEYS[1], ");
        ratelimitStr.append(" 'last_mill_second',ARGV[1], ");
        ratelimitStr.append(" 'curr_permits',ARGV[2], ");
        ratelimitStr.append(" 'max_burst',ARGV[3], ");
        ratelimitStr.append(" 'rate',ARGV[4], ");
        ratelimitStr.append(" 'app',ARGV[5]) ");
        ratelimitStr.append(" return result ");
        DefaultRedisScript<Long> ratelimitLua = new DefaultRedisScript<>(ratelimitStr.toString(), Long.class);

        String last_mill_second =  String.valueOf(currMillSecond);//上一次添加令牌的毫秒数
        String curr_permits = "3";//令牌桶的最少令牌数
        String max_permits = "200";//令牌桶的最大令牌数
        String rate = "100";//向令牌桶中添加令牌的速率  , 令牌消耗速率
        String app = "skynet";//定义标记，比如哪些是被限流的
        Long accquire = redisTemplate.execute(ratelimitLua,Collections.singletonList("ratelimit:"+key),
                currMillSecond.toString(), curr_permits, max_permits, rate, app);
        if (accquire == 1) {
            token = true;
        } else if (accquire == 0) {
            token = true;
        } else {
            token = false;
        }
        return token;
    }

    /*
     *  last_mill_second 最后时间毫秒
        curr_permits 当前可用的令牌
        max_burst 令牌桶最大值
        rate 每秒生成几个令牌
        app 应用
        令牌桶内令牌生成借鉴Guava-RateLimiter类的设计
        每次根据时间戳生成token，不超过最大值
        permits 每次请求令牌数
     */
    public boolean accquireToken(String key, Integer permits) {
        boolean token;
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        Long currMillSecond = redisTemplate.execute(
                (RedisCallback<Long>) redisConnection -> redisConnection.time()
        );

        Long accquire = redisTemplate.execute(ratelimitInitLua,
                Collections.singletonList("ratelimit:"+key), permits.toString(), currMillSecond.toString());
        if (accquire == 1) {
            token = true;
        } else {
            token = false;
        }
        return token;
    }




//        @RequestMapping(value = "/getRatelimitlua", method = RequestMethod.POST)
//        @ResponseBody
    public Object RatelimitRedisLua() {//@RequestParam(value="uid") String uid

        String key="lmc168";

        initToken(key);//初如化开始

        if (!accquireToken(key, 1)) {
            System.out.println("触发限流API:调用太忙了,请休息下");
            //throw new Exception();
        }else{
            System.out.println("没有触发限流策略");
        }
        return key;
    }


}


