package com.springboottest.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

public class ExceptionUtil {

    static final Logger logger = LoggerFactory.getLogger(com.springboottest.utils.ExceptionUtil.class);

    public static ModelAndView printErrorInfo(Exception ex) {
        ModelAndView mav = new ModelAndView();

        logger.error("Error : ", ex);
        mav.addObject("code", "9999");
        mav.addObject("message", "网络异常");

        mav.setView(new MappingJackson2JsonView());
        return mav;
    }
}
