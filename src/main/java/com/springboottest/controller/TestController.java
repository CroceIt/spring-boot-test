package com.springboottest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**

 */
@RequestMapping("/base")
@RestController
public class TestController {

    @RequestMapping("/hello")
    public Object hello() {
        return "hello";
    }
}
