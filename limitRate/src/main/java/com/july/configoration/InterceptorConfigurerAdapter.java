package com.july.configoration;

import com.july.interceptor.AccessLimitInterceptor;
import com.july.interceptor.InterStatInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfigurerAdapter extends WebMvcConfigurerAdapter {

    @Override
    public void  addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(accessLimitInterceptor()).addPathPatterns("/**");
//        registry.addInterceptor(interStatInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    @Bean
    public AccessLimitInterceptor accessLimitInterceptor(){
        return new AccessLimitInterceptor();
    }

    @Bean
    public InterStatInterceptor interStatInterceptor(){
        return new InterStatInterceptor();
    }

}
