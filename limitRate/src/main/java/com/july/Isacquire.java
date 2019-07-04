package com.july;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import redis.clients.jedis.Jedis;

/**
 * 执行Lua脚本判断是否限流的类
 */

public class Isacquire {
    //引入redis执行lua的脚本支持
    private  DefaultRedisScript<Long> getRedsiScript;

    //判断lua脚本是否成功，成功--没有限制 ，false--限制
    public  boolean acquire() throws Exception{
        //拿到redis的连接 redis的客户端
        Jedis jedis =  new Jedis("127.0.0.1",6379);
        getRedsiScript = new DefaultRedisScript<>();
        //设置Lua脚本的返回值类型 Long
        getRedsiScript.setResultType(Long.class);
        //加载我们自己写的lua脚本
        getRedsiScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("rateLimiter.lua")));
        String limitKey = "limit-key"; //key (limit-key)
        //执行lua脚本(这里可以保障执行脚本的原子性) 10秒限制3次
        Long result = (Long)jedis.eval(getRedsiScript.getScriptAsString(),1,limitKey,"3","10");
        //key的数量1， key ，param1， param2
        if(result ==0){//被限流了，改变方法的处理结果
            System.out.println("被分布式限流了");
            return false;
        }
        //返回为1，不为0的话，正常调用原有的方法
        return  true;
    }
}