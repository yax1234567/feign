package com.yax.feign.annotation;

import java.lang.annotation.*;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-07 16:55
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignClient {
    String url() ;
    Class<?> fallback() default void.class;
}
