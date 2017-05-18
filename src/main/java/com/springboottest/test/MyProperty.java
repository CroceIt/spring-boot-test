package com.springboottest.test;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties
class MyProperty {
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