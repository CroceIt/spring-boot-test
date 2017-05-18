package com.springboottest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

/**

 */
// SpringBoot标准注解, 一般写在App入口处
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
// 开启Servlet扫描
@ServletComponentScan

public class App {
    public static void main(String[] args) {

        SpringApplication.run(App.class, args);

    }
    
}