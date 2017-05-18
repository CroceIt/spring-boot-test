package com.springboottest.test;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @Prject: my-spring-boot-test
 * @Package: com.springboottest.test
 * @Description: TODO
 * @author: hujunzheng
 * @Date: 2017/5/16 2017/5/16
 * @version: V1.0
 */
@Configuration
@ConfigurationProperties
public class TestConfigure {
    private String userName;
    private String password;

    private String env;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    @Override
    public String toString() {
        return "TestConfigure{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", env='" + env + '\'' +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
