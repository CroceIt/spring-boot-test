package com.springboottest.interceptor;

import com.springboottest.exception.DefaultException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**

 *
 * @Title: ValidateInterceptorHandler.java
 * @Prject: sensors-data
 * @Package: com.springboottest.interceptor
 * @Description: TODO
 * @author: hujunzheng
 * @date: 2017年4月27日 下午12:49:17
 * @version: V1.0
 */
@Component("validateInterceptorHandler")
public class ValidateInterceptorHandler extends HandlerInterceptorAdapter {

    private static final String STATIC_TOKEN = "xxxxx=";
    private static final String TOKEN_NAME = "token";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (StringUtils.contains(request.getRequestURI(), "api/sensors/data") &&
                (StringUtils.isBlank(request.getParameter(TOKEN_NAME)) ||
                        !STATIC_TOKEN.equals(request.getParameter(TOKEN_NAME)))) {
            throw new DefaultException("999", TOKEN_NAME + " 无效");
        }

        return true;
    }

}
