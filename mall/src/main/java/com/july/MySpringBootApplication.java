package com.july;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//这个注解等同于
@SpringBootApplication
public class MySpringBootApplication {
    //相当于启动了Tomcat,端口默认为8080
    public static void main(String[] args) {

        SpringApplication.run(MySpringBootApplication.class, args);
    }
}
