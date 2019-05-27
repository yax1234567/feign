package com.yax.feign;

import com.yax.feign.feignAssembly.FeignScanner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeignApplicationTests {

    @Test
    public void contextLoads() {
        String BASE_PACKAGE = "com.yax.feign";
        GenericApplicationContext context = new GenericApplicationContext();
        FeignScanner feignScanner = new FeignScanner(context);
         // 注册过滤器
        feignScanner.registerDefaultFilters();
        int beanCount = feignScanner.scan(BASE_PACKAGE);
        context.refresh();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        System.out.println(beanCount);
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }

}
