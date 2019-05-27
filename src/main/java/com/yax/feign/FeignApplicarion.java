package com.yax.feign;

import com.yax.feign.annotation.EnableFeignClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ${DESCRIPTION}
 *
 * @author yax
 * @create 2019-03-08 13:53
 **/
@SpringBootApplication
@EnableFeignClients
public class FeignApplicarion {
    public static void main(String[] args) {
        SpringApplication.run(FeignApplicarion.class, args);
    }
}
