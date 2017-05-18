package com.springboottest.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *
 * @Prject: my-spring-boot-test
 * @Package: com.springboottest.test
 * @Description: TODO
 * @author: hujunzheng
 * @Date: 2017/5/17 2017/5/17
 * @version: V1.0
 */

@Configuration
@Import(MyProperty.class)
public class TestConfigure3 {
    @Autowired
    private MyProperty myProperty;

    public void function() {
        System.out.println(myProperty);
    }
}


