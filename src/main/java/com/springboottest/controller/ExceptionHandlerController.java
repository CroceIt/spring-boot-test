package com.springboottest.controller;

import com.springboottest.exception.ValidationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**

 */

/**
 * Controller 全局异常处理
 */
@ControllerAdvice
public class ExceptionHandlerController {

    //    @ExceptionHandler//处理所有异常
    //    @ExceptionHandler(value={RuntimeException.class,MyRuntimeException.class}) 处理指定异常
    @ExceptionHandler(Exception.class)
    @ResponseBody //在返回自定义相应类的情况下必须有，这是@ControllerAdvice注解的规定
    public Object handle(Exception ex) {
        Map<String, Object> result = com.springboottest.utils.LinkedHashMapUtil.build().fetchAll();
        if (ex instanceof ValidationException) {
            ex.printStackTrace();
            ValidationException ve = (ValidationException) ex;
            result.put("code", ve.getCode());
            result.put("message", ve.getMessage());
        } else if (ex instanceof com.springboottest.exception.BaseException) {
            ex.printStackTrace();
            com.springboottest.exception.BaseException ve = (com.springboottest.exception.BaseException) ex;
            result.put("code", ve.getCode());
            result.put("message", ve.getMessage());
        } else {
            ex.printStackTrace();
            result.put("code", 9999);
            result.put("message", ex.getMessage());
        }
        return result;

    }
}
