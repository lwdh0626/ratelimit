package com.july.anno;

import java.lang.annotation.*;

@Inherited
@Documented
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimitAnno {

    String limitKey();

    //标识 指定sec 时间内 被访问次数
    int limit() default 5;

    //时间段
    int sec() default 1;
}
