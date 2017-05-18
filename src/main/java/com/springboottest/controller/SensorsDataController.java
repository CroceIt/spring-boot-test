package com.springboottest.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboottest.response.ObjectResponse;
import com.springboottest.response.ReturnCode;
import com.springboottest.response.SimpleResponse;
import com.springboottest.service.SensorsDataService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**

 *
 * @Title: SensorsDataController.java
 * @Prject: sensors-data
 * @Package: com.springboottest.controller
 * @Description: TODO
 * @author: hujunzheng
 * @date: 2017年4月21日 上午11:46:48
 * @version: V1.0
 */

@RestController
@RequestMapping("api/sensors/data")
public class SensorsDataController {

    @Autowired
    private SensorsDataService sensorsDataService;

    @ApiOperation(value = "昨日数据总览+七天核心数据走势", notes = "根据城市和日期展示数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "city", value = "城市名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "date", value = "日期", required = true, paramType = "query"),
            @ApiImplicitParam(name = "token", value = "静态token", required = true, paramType = "query")
    })
    @RequestMapping(value = "index", method = RequestMethod.GET)
    public ObjectResponse<JSONObject> index(@RequestParam String city, @RequestParam String date) {
        ObjectResponse<JSONObject> result = new ObjectResponse<>();
        SimpleResponse response = sensorsDataService.cityDataPandect(city, date);
        if (response.getCode() == ReturnCode.SUCCESS.getValue()) {
            result.setCode(ReturnCode.SUCCESS);
            result.setMsg("查询成功");
            result.setData(JSONObject.parseObject(response.getMsg()));
        } else {
            result.setCode(ReturnCode.FAILURE);
            result.setMsg(response.getMsg());
        }
        return result;
    }

    @ApiOperation(value = "各店单日数据", notes = "根据城市和日期展示数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "city", value = "城市名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "date", value = "日期", required = true, paramType = "query"),
            @ApiImplicitParam(name = "token", value = "静态token", required = true, paramType = "query")
    })
    @RequestMapping(value = "oneday", method = RequestMethod.GET)
    public ObjectResponse<JSONArray> oneday(@RequestParam String city, @RequestParam String date) {
        ObjectResponse<JSONArray> result = new ObjectResponse<>();
        SimpleResponse response = sensorsDataService.oneDay(city, date);
        if (response.getCode() == ReturnCode.SUCCESS.getValue()) {
            result.setCode(ReturnCode.SUCCESS);
            result.setMsg("查询成功");
            result.setData(JSON.parseArray(response.getMsg()));
        } else {
            result.setCode(ReturnCode.FAILURE);
            result.setMsg(response.getMsg());
        }
        return result;
    }

    @ApiOperation(value = "各店铺历史累积综合数据", notes = "根据城市和日期展示数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "city", value = "城市名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "date", value = "日期", required = true, paramType = "query"),
            @ApiImplicitParam(name = "token", value = "静态token", required = true, paramType = "query")
    })
    @RequestMapping(value = "grandtotal", method = RequestMethod.GET)
    public ObjectResponse<JSONArray> grandtotal(@RequestParam String city, @RequestParam String date) {
        ObjectResponse<JSONArray> result = new ObjectResponse<>();
        SimpleResponse response = sensorsDataService.cityStoreHistoryData(city, date);
        if (response.getCode() == ReturnCode.SUCCESS.getValue()) {
            result.setCode(ReturnCode.SUCCESS);
            result.setMsg("查询成功");
            result.setData(JSON.parseArray(response.getMsg()));
        } else {
            result.setCode(ReturnCode.FAILURE);
            result.setMsg(response.getMsg());
        }
        return result;
    }

    @ApiOperation(value = "各店铺异常数据", notes = "根据城市和日期展示数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "city", value = "城市名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "date", value = "日期", required = true, paramType = "query"),
            @ApiImplicitParam(name = "token", value = "静态token", required = true, paramType = "query")
    })
    @RequestMapping(value = "abnormal", method = RequestMethod.GET)
    public ObjectResponse<JSONObject> abnormal(@RequestParam String city, @RequestParam String date) {
        ObjectResponse<JSONObject> result = new ObjectResponse<>();

        SimpleResponse response = sensorsDataService.cityStoreNotAbnormal(city, date);
        if (response.getCode() == ReturnCode.SUCCESS.getValue()) {
            result.setCode(ReturnCode.SUCCESS);
            result.setMsg("查询成功");
            result.setData(JSON.parseObject(response.getMsg()));
        } else {
            result.setCode(ReturnCode.FAILURE);
            result.setMsg(response.getMsg());
        }

        return result;
    }
}
