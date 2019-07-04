package com.july.interceptor;

import com.july.anno.AccessLimitAnno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collections;

public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    DefaultRedisScript defaultRedisScript;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) o;
        Method method = handlerMethod.getMethod();
        if (! method.isAnnotationPresent(AccessLimitAnno.class)){
            return true;
        }
        AccessLimitAnno accessLimit = method.getAnnotation(AccessLimitAnno.class);
        int limit = accessLimit.limit();
        int sec = accessLimit.sec();
        String limitKey = accessLimit.limitKey();


        /*
        //限制时间窗最大请求数方案
        Long result = (Long)redisTemplate.execute(defaultRedisScript, Collections.singletonList(limitKey), limit, sec);
        System.out.println(result);

        if(result ==0){//被限流了，改变方法的处理结果
            System.out.println("被分布式限流了");
            output(httpServletResponse,"请求太频繁，请稍后重试");
            return false;
        }
        //返回为1，不为0的话，正常调用原有的方法
        return  true;
*/



        //令牌桶方
        Long currMillSecond = (Long) redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.time();
            }
        });


        /**
         * 初始化
         * max_permits 桶大小
         * rate  向桶里添加令牌的速率
         * apps  可以使用令牌桶的应用列表，应用之前用逗号分隔

        Long max_permits = 3l;
        int rate = 1;
        String apps = "pay,aaa,bbb";
        String init = "init";

        Long result2 = (Long)redisTemplate.execute(defaultRedisScript, Collections.singletonList(limitKey),
                init,max_permits,rate, apps);
        System.out.println("init: "+ result2);
         */


        /**
         * 获取令牌
         *  key 令牌的唯一标识
         *  max_permits 桶大小
         *  rate  向桶里添加令牌的速率  每秒添加几个
         *  permits  请求令牌数量
         *  curr_mill_second 当前毫秒数
         */

        int max_permits = 1;
        int rate = 1;
        int permits = 1;
        Long result1 = (Long)redisTemplate.execute(defaultRedisScript, Collections.singletonList(limitKey),permits,currMillSecond,max_permits,rate);
        System.out.println("require: "+ result1);

        if(result1 ==0){//被限流了，改变方法的处理结果
            System.out.println("被分布式限流了");
            output(httpServletResponse,"请求太频繁，请稍后重试");
            return false;
        }
        //返回为1，不为0的话，正常调用原有的方法
        return  true;


    }


    public void output(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(msg.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
