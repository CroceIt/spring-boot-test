package com.springboottest.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.springboottest.interceptor.ValidateInterceptorHandler;

/**

 */
@Configuration
@Import(com.springboottest.utils.RedisUtils.class)
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private com.springboottest.utils.RedisUtils redisUtils;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(validateInterceptorHandler()).addPathPatterns("/**");
        registry.addInterceptor(resubmitInterceptorHandler()).addPathPatterns("/**");
    }

    @Bean
    public com.springboottest.interceptor.ResubmitInterceptorHandler resubmitInterceptorHandler() {
        return new com.springboottest.interceptor.ResubmitInterceptorHandler(redisUtils);
    }

    @Bean
    public ValidateInterceptorHandler validateInterceptorHandler() {
        return new ValidateInterceptorHandler();
    }
}
