package com.springboottest.druid;

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebServlet;

/**

 */
@WebServlet(urlPatterns = "/druid/*")
public class DruidStatViewServlet extends StatViewServlet {

}
