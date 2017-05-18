package com.springboottest.service;
/**

 *
 * @Title: SensorsDataService.java
 * @Prject: sensors-data
 * @Package: com.springboottest.service
 * @Description: TODO
 * @author: hujunzheng
 * @date: 2017年4月21日 下午4:45:40
 * @version: V1.0
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboottest.response.ReturnCode;
import com.springboottest.response.SimpleResponse;
import com.springboottest.response.bo.CityDataPandectBo;
import com.springboottest.response.bo.CityStoreHistoryDataBO;
import com.springboottest.response.bo.CityStoreNotAbnormalDataBo;
import com.springboottest.response.bo.OneDayBO;
import com.springboottest.utils.DateUtils;
import com.springboottest.utils.SensorsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Service("sensorsDataService")
public class SensorsDataService {

    @Autowired
    private BaseSensorsDataService baseSensorsDataService;

    /**
     *
     * @Title: oneDay2
     *
     * @author: hujunzheng
     *
     * @param city
     * @param date
     * @return
     *
     * @return: SimpleResponse
     *
     */
    public SimpleResponse oneDay(String city, String date) {
        SimpleResponse response = new SimpleResponse();
        Map<Long, OneDayBO> map = new HashMap<>();
        List<OneDayBO> data = new ArrayList<>();

        try {
            SimpleResponse successOrderResponse = this.baseSensorsDataService.successOrderContent(city, date, date);
            if (successOrderResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject successOrderResponseJb = JSONObject.parseObject(successOrderResponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(successOrderResponseJb);
                rows.forEach(object -> {
                    JSONObject jsonObject = (JSONObject) object;
                    JSONArray values = SensorsUtils.getValuesOfRows(jsonObject);
                    JSONArray byValues = SensorsUtils.getByvaluesOfRows(jsonObject);
                    OneDayBO bo = new OneDayBO();

                    bo.setStoreId(byValues.getLong(0));
                    bo.setStoreName(byValues.getString(1));

                    map.put(bo.getStoreId(), bo);

                    // 将 各个 数组 对应的列 相加 合并成 一个数组（如果是一天，可以不用）
                    Optional<Object> sumRowValues = SensorsUtils.sumRowValues(values);

                    JSONArray jsonArray = (JSONArray) sumRowValues.get();
                    bo.setOrderTotalNum(String.valueOf(jsonArray.get(0)));
                    bo.setOrderTotalAmount(String.valueOf(jsonArray.get(1)));
                    bo.setAvgOrderAmount(
                            SensorsUtils.bigDivision(bo.getOrderTotalAmount(), bo.getOrderTotalNum(), 2, 1));

                    data.add(bo);
                });

                rows.sort((oa, ob) -> {
                    JSONObject joa = (JSONObject) oa;
                    JSONObject job = (JSONObject) ob;
                    return SensorsUtils.getValuesOfRows(job).getJSONArray(0).getBigDecimal(2)
                            .compareTo(SensorsUtils.getValuesOfRows(joa).getJSONArray(0).getBigDecimal(2));
                });
                for (int i = 0; i < rows.size(); ++i) {
                    JSONArray byValues = SensorsUtils.getByvaluesOfRows(rows.getJSONObject(i));
                    map.get(byValues.getLong(0)).setNowadaysTop(String.valueOf(i + 1));
                }

            } else {
                throw new RuntimeException("支付订单获取异常!");
            }

            SimpleResponse sibmitOrderResponse = this.baseSensorsDataService.submitOrderContent(city, date, date);
            if (sibmitOrderResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject submitOrderResponseJb = JSONObject.parseObject(sibmitOrderResponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(submitOrderResponseJb);
                rows.forEach(object -> {
                    JSONObject jsonObject = (JSONObject) object;
                    JSONArray values = SensorsUtils.getValuesOfRows(jsonObject);
                    JSONArray byValues = SensorsUtils.getByvaluesOfRows(jsonObject);

                    // 将 各个 数组 对应的列 相加 合并成 一个数组(如果是一天，可以不用)
                    Optional<Object> sumRowValues = SensorsUtils.sumRowValues(values);

                    JSONArray jsonArray = (JSONArray) sumRowValues.get();
                    if (map.containsKey(byValues.getLong(0))) {
                        map.get(byValues.getLong(0)).setSelfOrderSubmitTotalAmount(String.valueOf(jsonArray.get(0)));
                        map.get(byValues.getLong(0)).setOrderPersonNum(String.valueOf(jsonArray.get(1)));// 用户触发次数
                    } else {
                        OneDayBO bo = new OneDayBO();
                        bo.setStoreId(byValues.getLong(0));
                        bo.setStoreName(byValues.getString(1));
                        bo.setSelfOrderSubmitTotalAmount(String.valueOf(jsonArray.get(0)));
                        bo.setOrderPersonNum(String.valueOf(jsonArray.get(1)));
                        data.add(bo);
                        map.put(bo.getStoreId(), bo);
                    }
                });
            } else {
                throw new RuntimeException("提交订单获取异常!");
            }

            SimpleResponse cancelOrderResponse = this.baseSensorsDataService.cancelOrderContent(city, date, date, null);
            if (cancelOrderResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject cancelOrderResponseJb = JSONObject.parseObject(cancelOrderResponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(cancelOrderResponseJb);
                rows.forEach(object -> {
                    JSONObject jsonObject = (JSONObject) object;
                    JSONArray values = SensorsUtils.getValuesOfRows(jsonObject);
                    JSONArray byValues = SensorsUtils.getByvaluesOfRows(jsonObject);

                    // 将 各个 数组 对应的列 相加 合并成 一个数组(如果是一天，可以不用)
                    Optional<Object> sumRowValues = SensorsUtils.sumRowValues(values);

                    JSONArray jsonArray = (JSONArray) sumRowValues.get();
                    if (map.containsKey(byValues.getLong(0))) {
                        OneDayBO bo = map.get(byValues.getLong(0));
                        bo.setOrderCancelNum(String.valueOf(jsonArray.get(0)));
                        bo.setCancelRate(SensorsUtils.bigDivision(bo.getOrderCancelNum(),
                                bo.getSelfOrderSubmitTotalAmount(), 2, 100) + "%");
                    } else {
                        OneDayBO bo = new OneDayBO();
                        bo.setStoreId(byValues.getLong(0));
                        bo.setStoreName(byValues.getString(1));
                        bo.setOrderCancelNum(String.valueOf(jsonArray.get(0)));
                        bo.setCancelRate("100%");
                        data.add(bo);
                        map.put(bo.getStoreId(), bo);
                    }
                });

                data.stream().forEach(dt -> {
                    if (StringUtils.isEmpty(dt.getOrderCancelNum())) {
                        dt.setOrderCancelNum(String.valueOf(0));
                        dt.setCancelRate("0%");
                    }
                });
            } else {
                throw new RuntimeException("取消订单获取异常!");
            }

            // 昨日排行
            String yesterdayDate = DateUtils.getDateAfter(date, -1);
            SimpleResponse rankReponse = this.baseSensorsDataService.rankContent(city, yesterdayDate, yesterdayDate);
            if (rankReponse.getCode() == ReturnCode.SUCCESS.getValue()) {

                JSONObject rankJson = JSONObject.parseObject(rankReponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(rankJson);
                rows.sort((oa, ob) -> {
                    JSONObject joa = (JSONObject) oa;
                    JSONObject job = (JSONObject) ob;
                    return SensorsUtils.getValuesOfRows(job).getJSONArray(0).getBigDecimal(0)
                            .compareTo(SensorsUtils.getValuesOfRows(joa).getJSONArray(0).getBigDecimal(0));
                });
                for (int i = 0; i < rows.size(); ++i) {
                    JSONArray byValues = SensorsUtils.getByvaluesOfRows(rows.getJSONObject(i));
                    if (map.containsKey(byValues.getInteger(0))) {
                        map.get(byValues.getLong(0)).setBeforeTop(String.valueOf(i + 1));
                    } else {
                        OneDayBO nbo = new OneDayBO();
                        nbo.setBeforeTop(String.valueOf(i + 1));
                        nbo.setStoreId(byValues.getLong(0));
                        nbo.setStoreName(byValues.getString(1));
                        data.add(nbo);
                    }
                }
            } else {
                throw new RuntimeException("获取昨日排行失败。");
            }

            for (OneDayBO bo : data) {
                if (StringUtils.isEmpty(bo.getNowadaysTop())) {
                    bo.setNowadaysTop(String.valueOf("-"));
                }
                if (StringUtils.isEmpty(bo.getBeforeTop())) {
                    bo.setBeforeTop("-");
                }
            }
            response.setMsg(SensorsUtils.sensorsDataToJsonWithFilter(data));
            response.setCode(ReturnCode.SUCCESS);
            System.out.println("one day 结果->" + response.getMsg());
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            response.setCode(ReturnCode.FAILURE);
            e.printStackTrace();
        }
        return response;
    }

    /**
     *
     * @Title: cityDataPandect
     *
     * @author: hujunzheng
     *
     * @param city
     * @param date
     * @return
     *
     * @return: SimpleResponse
     *
     */
    public SimpleResponse cityDataPandect(String city, String date) {
        SimpleResponse response = new SimpleResponse();
        CityDataPandectBo bo = new CityDataPandectBo();
        try {
            SimpleResponse successOrderResponse = this.baseSensorsDataService.successOrderContent(city, date, date);
            if (successOrderResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject successOrderResponseJb = JSONObject.parseObject(successOrderResponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(successOrderResponseJb);
                if (rows != null && rows.size() > 0) {
                    JSONObject sumRows = (JSONObject) SensorsUtils.sumRows(rows).get();

                    bo.setTotalTotal(SensorsUtils.getValuesOfRows(sumRows).getJSONArray(0).getBigInteger(0));
                    bo.setTotalAmount(SensorsUtils.getValuesOfRows(sumRows).getJSONArray(0).getBigDecimal(1));
                    bo.setAvgOrderAmount();
                }
            } else {
                throw new RuntimeException("成交订单获取异常!");
            }

            SimpleResponse weekAgaginBuyerRateResponse = this.baseSensorsDataService.weekAgaginBuyerRate(city, null,
                    DateUtils.getDateAfter(date, -6), date);
            if (weekAgaginBuyerRateResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                bo.setWeekAgaginBuyerRate(weekAgaginBuyerRateResponse.getMsg());
            } else {
                throw new RuntimeException("周复购率获取异常!");
            }

            SimpleResponse appAndWapResponse = this.baseSensorsDataService.appAndWap(city, date, date, null);
            if (appAndWapResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject appAndWapResponseJb = JSONObject.parseObject(appAndWapResponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(appAndWapResponseJb);
                JSONArray appValue = null, wapValue = null;
                for (Object obj : rows) {
                    JSONArray rowValues = SensorsUtils.getValuesOfRows((JSONObject) obj);
                    JSONArray rowByValues = SensorsUtils.getByvaluesOfRows((JSONObject) obj);
                    if (rowByValues != null) {
                        JSONArray tmpValue = (rowValues != null && rowValues.size() > 0) ? rowValues.getJSONArray(0)
                                : null;
                        if (rowByValues.getString(0).equals("app")) {
                            appValue = tmpValue;
                        } else if (rowByValues.getString(0).equals("wap")) {
                            wapValue = tmpValue;
                        }
                    }
                }
                BigDecimal totalAmount = new BigDecimal(0), totalTotal = new BigDecimal(0);

                if (appValue != null) {
                    totalAmount = totalAmount.add(appValue.getBigDecimal(0));
                    totalTotal = totalTotal.add(appValue.getBigDecimal(1));
                }

                if (wapValue != null) {
                    totalAmount = totalAmount.add(wapValue.getBigDecimal(0));
                    totalTotal = totalTotal.add(wapValue.getBigDecimal(1));
                }

                if (appValue != null) {
                    bo.setAppOrderAmtRate(
                            SensorsUtils.bigDivision(appValue.getString(0), totalAmount.toString(), 2, 100) + "%");
                    bo.setAppOrderNumRate(
                            SensorsUtils.bigDivision(appValue.getString(1), totalTotal.toString(), 2, 100) + "%");
                } else {
                    bo.setAppOrderAmtRate("0%");
                    bo.setAppOrderNumRate("0%");
                }
            } else {
                throw new RuntimeException("订单-终端内容获取异常!");
            }

            successOrderResponse = this.baseSensorsDataService.successOrderContent(city, DateUtils.getDateAfter(date, -6), date);
            if (successOrderResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject successOrderResponseJb = JSONObject.parseObject(successOrderResponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(successOrderResponseJb);
                JSONArray series = SensorsUtils.getSeries(successOrderResponseJb);

                JSONArray trendOrderAmountArr = new JSONArray(), trendOrderAvgAmountArr = new JSONArray(),
                        trendOrderNumArr = new JSONArray();
                final int WEEK_DAYS = 7;
                for (int i = 0; i < WEEK_DAYS; ++i) {
                    trendOrderAmountArr.add(0.0);
                    trendOrderAvgAmountArr.add(0.00);
                    trendOrderNumArr.add(0);
                }

                Map<Integer, Integer> positonMap = new HashMap<>();
                if (series != null && series.size() > 0) {
                    String firstDate = DateUtils.getDateAfter(date, -6);
                    for (int i = 0, j = 0; i < WEEK_DAYS; ++i) {
                        if (j < series.size()) {
                            if (series.getString(j).split(" ")[0].equals(firstDate)) {
                                positonMap.put(j, i);
                                ++j;
                            }
                        } else {
                            break;
                        }
                        firstDate = DateUtils.getDateAfter(firstDate, 1);
                    }
                }

                if (rows != null && rows.size() > 0) {
                    Optional<Object> optionalRow = SensorsUtils.sumRows(rows);
                    JSONArray valuesArray = SensorsUtils.getValuesOfRows((JSONObject) optionalRow.get());
                    for (int i = 0; i < valuesArray.size(); ++i) {
                        JSONArray valueArray = valuesArray.getJSONArray(i);
                        int index = positonMap.get(i);
                        trendOrderNumArr.set(index, valueArray.getBigDecimal(0));
                        trendOrderAmountArr.set(index, valueArray.getBigDecimal(1));
                        trendOrderAvgAmountArr.set(index,
                                SensorsUtils.bigDivision(
                                        trendOrderAmountArr.get(index).toString(),
                                        trendOrderNumArr.get(index).toString(), 2, 1));
                    }
                }
                bo.setTrendOrderAmountArr(trendOrderAmountArr);
                bo.setTrendOrderNumArr(trendOrderNumArr);
                bo.setTrendOrderAvgAmountArr(trendOrderAvgAmountArr);
            } else {
                throw new RuntimeException("支付订单获取异常!");
            }

            response.setMsg(SensorsUtils.sensorsDataToJsonNoFilter(bo));
            response.setCode(ReturnCode.SUCCESS);
            System.out.println("城市数据总览->" + response.getMsg());
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            response.setCode(ReturnCode.FAILURE);
            e.printStackTrace();
        }
        return response;
    }

    /**
     *
     * @Title: cityStoreHistoryData
     *
     * @author: hujunzheng
     *
     * @param city
     * @param date
     * @return
     *
     * @return: SimpleResponse
     *
     */
    public SimpleResponse cityStoreHistoryData(String city, String date) {
        SimpleResponse response = new SimpleResponse();
        Map<Long, CityStoreHistoryDataBO> map = new HashMap<>();
        List<CityStoreHistoryDataBO> data = new ArrayList<>();
        try {
            SimpleResponse successOrderResponse = this.baseSensorsDataService.successOrderContent(city,
                    SensorsUtils.THE_EARLY_DATE, date);
            if (successOrderResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject successOrderResponseJb = JSONObject.parseObject(successOrderResponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(successOrderResponseJb);
                rows.forEach(object -> {
                    JSONObject jsonObject = (JSONObject) object;
                    JSONArray values = SensorsUtils.getValuesOfRows(jsonObject);
                    JSONArray byValues = SensorsUtils.getByvaluesOfRows(jsonObject);
                    CityStoreHistoryDataBO bo = new CityStoreHistoryDataBO();

                    bo.setStoreId(byValues.getLong(0));
                    bo.setStoreName(byValues.getString(1));

                    map.put(bo.getStoreId(), bo);

                    // 将 各个 数组 对应的列 相加 合并成 一个数组（如果是一天，可以不用）
                    Optional<Object> sumRowValues = SensorsUtils.sumRowValues(values);

                    JSONArray jsonArray = (JSONArray) sumRowValues.get();
                    bo.setOrderTotalNum(jsonArray.getBigInteger(0));
                    bo.setOrderTotalAmount(jsonArray.getBigDecimal(1));
                    data.add(bo);
                });
            } else {
                throw new RuntimeException("支付订单获取异常!");
            }

            // 失败订单
            SimpleResponse failOrderResponse = this.baseSensorsDataService.failOrderContent(city,
                    SensorsUtils.THE_EARLY_DATE, date);
            if (failOrderResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject cancelOrderResponseJb = JSONObject.parseObject(failOrderResponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(cancelOrderResponseJb);
                rows.forEach(object -> {
                    JSONObject jsonObject = (JSONObject) object;
                    JSONArray values = SensorsUtils.getValuesOfRows(jsonObject);
                    JSONArray byValues = SensorsUtils.getByvaluesOfRows(jsonObject);

                    // 将 各个 数组 对应的列 相加 合并成 一个数组(如果是一天，可以不用)
                    Optional<Object> sumRowValues = SensorsUtils.sumRowValues(values);

                    JSONArray jsonArray = (JSONArray) sumRowValues.get();
                    if (map.containsKey(byValues.getLong(0))) {
                        CityStoreHistoryDataBO bo = map.get(byValues.getLong(0));
                        bo.setOrderRefundNum(jsonArray.getBigInteger(0));
                        bo.setOrderRefundRate(SensorsUtils.bigDivision(
                                bo.getOrderRefundNum() != null ? bo.getOrderRefundNum().toString() : null,
                                bo.getOrderTotalAmount() != null ? bo.getOrderTotalAmount().toString()
                                        : null,
                                2, 100) + "%");
                    } else {
                        CityStoreHistoryDataBO bo = new CityStoreHistoryDataBO();
                        bo.setStoreId(byValues.getLong(0));
                        bo.setStoreName(byValues.getString(1));
                        bo.setOrderRefundNum(jsonArray.getBigInteger(0));
                        bo.setOrderRefundRate("100%");
                        data.add(bo);
                        map.put(bo.getStoreId(), bo);
                    }
                });
            } else {
                throw new RuntimeException("取消订单获取异常!");
            }

            // 各店铺今日排行
            SimpleResponse rankNowadaysReponse = this.baseSensorsDataService.rankContent(city, date, date);
            if (rankNowadaysReponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject rankJson = JSONObject.parseObject(rankNowadaysReponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(rankJson);
                rows.sort((oa, ob) -> {
                    JSONObject joa = (JSONObject) oa;
                    JSONObject job = (JSONObject) ob;
                    return SensorsUtils.getValuesOfRows(job).getJSONArray(0).getBigDecimal(0)
                            .compareTo(SensorsUtils.getValuesOfRows(joa).getJSONArray(0).getBigDecimal(0));
                });
                for (int i = 0; i < rows.size(); ++i) {
                    JSONArray byValues = SensorsUtils.getByvaluesOfRows(rows.getJSONObject(i));
                    if (map.containsKey(byValues.getInteger(0))) {
                        map.get(byValues.getLong(0)).setNowadaysTop(new Long(i + 1));
                    }
                }

            } else {
                throw new RuntimeException("获取上周排行失败。");
            }

            // 各店铺上周排行
            String befoeDate = DateUtils.getDateAfter(date, -6);
            SimpleResponse rankBeforeReponse = this.baseSensorsDataService.rankContent(city, befoeDate, date);
            if (rankBeforeReponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject rankJson = JSONObject.parseObject(rankBeforeReponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(rankJson);
                rows.sort((oa, ob) -> {
                    JSONObject joa = (JSONObject) oa;
                    JSONObject job = (JSONObject) ob;
                    return SensorsUtils.getValuesOfRows(job).getJSONArray(0).getBigDecimal(0)
                            .compareTo(SensorsUtils.getValuesOfRows(joa).getJSONArray(0).getBigDecimal(0));
                });
                for (int i = 0; i < rows.size(); ++i) {
                    JSONArray byValues = SensorsUtils.getByvaluesOfRows(rows.getJSONObject(i));
                    if (map.containsKey(byValues.getInteger(0))) {
                        map.get(byValues.getLong(0)).setBeforeTop(new Long(i + 1));
                    }
                }

            } else {
                throw new RuntimeException("获取上周排行失败。");
            }

            // 各店铺 周复购率
            SimpleResponse weekRepateResponse = this.baseSensorsDataService.weekAgaginBuyerRate2(city,
                    DateUtils.getDateAfter(date, -6), date);
            if (weekRepateResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONArray weekRepateJson = JSON.parseArray(weekRepateResponse.getMsg());
                for (int i = 0; i < weekRepateJson.size(); ++i) {
                    JSONObject repeatContent = weekRepateJson.getJSONObject(i);
                    Long storeId = repeatContent.getLong("storeId");
                    String repeat = repeatContent.getString("repeatRate");
                    if (map.get(storeId) != null) {
                        map.get(storeId).setOrderRepeatRate(repeat);
                    }
                }
            } else {
                throw new RuntimeException("获取周复购率异常");
            }

            // app终端 下单率
            SimpleResponse appAndWapResponse = this.baseSensorsDataService.appAndWap(city, SensorsUtils.THE_EARLY_DATE,
                    date, Arrays.asList("event.$Anything.store_id"));
            if (appAndWapResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject appAndWapResponseJb = JSONObject.parseObject(appAndWapResponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(appAndWapResponseJb);
                Map<Long, BigDecimal> storeAppAmount = new HashMap<>(), storeWapAmount = new HashMap<>();
                rows.forEach(obj -> {
                    JSONObject jo = (JSONObject) obj;
                    JSONArray byValues = SensorsUtils.getByvaluesOfRows(jo);
                    Optional<Object> valuesSum = SensorsUtils.sumRowValues(SensorsUtils.getValuesOfRows(jo));
                    try {
                        if (valuesSum != null && valuesSum.get() != null) {
                            JSONArray finalSumArray = (JSONArray) valuesSum.get();
                            Long storeId = byValues.getLong(1);
                            if ("app".equals(byValues.getString(0))) {
                                if (!storeAppAmount.containsKey(storeId))
                                    storeAppAmount.put(storeId, finalSumArray.getBigDecimal(0));
                                storeAppAmount.put(storeId,
                                        storeAppAmount.get(storeId).add(new BigDecimal(finalSumArray.getString(0))));
                            } else if ("wap".equals(byValues.getString(0))) {
                                if (!storeWapAmount.containsKey(storeId))
                                    storeWapAmount.put(storeId, finalSumArray.getBigDecimal(0));
                                storeWapAmount.put(storeId,
                                        storeWapAmount.get(storeId).add(new BigDecimal(finalSumArray.getString(0))));
                            }
                        }
                    } catch (NumberFormatException e) {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                data.forEach(bo -> {
                    Long storeId = bo.getStoreId();
                    String appAmount = storeAppAmount.containsKey(storeId) ? storeAppAmount.get(storeId).toString()
                            : "0";
                    String wapAmount = storeWapAmount.containsKey(storeId) ? storeWapAmount.get(storeId).toString()
                            : "0";
                    String totalAmount = new BigDecimal(appAmount).add(new BigDecimal(wapAmount)).toString();
                    bo.setAppOrderRate(SensorsUtils.bigDivision(appAmount, totalAmount, 2, 100) + "%");
                });
            } else {
                throw new RuntimeException("订单-终端内容获取异常!");
            }

            response.setMsg(SensorsUtils.sensorsDataToJsonWithFilter(data));
            response.setCode(ReturnCode.SUCCESS);
            System.out.println("总提交订单数->" + response.getMsg());
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            response.setCode(ReturnCode.FAILURE);
            e.printStackTrace();
        }
        return response;
    }

    public SimpleResponse cityStoreNotAbnormal(String city, String date) {
        SimpleResponse response = new SimpleResponse();
        CityStoreNotAbnormalDataBo data = new CityStoreNotAbnormalDataBo();
        try {
            // 多日取消订单
            SimpleResponse moreDaysCancelOrderResponse = this.baseSensorsDataService.cancelOrderContent(city,
                    SensorsUtils.THE_EARLY_DATE, date, null);
            Map<Long, BigInteger> storeCancelOrderCount = new HashMap<>();
            BigInteger cityCancelOrderCount = new BigInteger("0");

            if (moreDaysCancelOrderResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject cancelOrderResponseJb = JSONObject.parseObject(moreDaysCancelOrderResponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(cancelOrderResponseJb);
                rows.forEach(object -> {
                    JSONObject jsonObject = (JSONObject) object;
                    JSONArray values = SensorsUtils.getValuesOfRows(jsonObject);
                    JSONArray byValues = SensorsUtils.getByvaluesOfRows(jsonObject);

                    // 将 各个 数组 对应的列 相加 合并成 一个数组(如果是一天，可以不用)
                    Optional<Object> sumRowValues = SensorsUtils.sumRowValues(values);

                    JSONArray jsonArray = (JSONArray) sumRowValues.get();
                    if (jsonArray != null) {
                        storeCancelOrderCount.put(byValues.getLong(0), jsonArray.getBigInteger(0));
                        cityCancelOrderCount.add(jsonArray.getBigInteger(0));
                    }
                });
            } else {
                throw new RuntimeException("多日取消订单获取异常!");
            }

            Map<Long, CityStoreNotAbnormalDataBo.CancelOrderTopBO> mapCancelOrderData = new HashMap<>();
            // 当日取消订单
            SimpleResponse nowadaysCancelOrderResponse = this.baseSensorsDataService.cancelOrderContent(city, date,
                    date, null);
            if (nowadaysCancelOrderResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject cancelOrderResponseJb = JSONObject.parseObject(nowadaysCancelOrderResponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(cancelOrderResponseJb);
                rows.forEach(object -> {
                    JSONObject jsonObject = (JSONObject) object;
                    JSONArray values = SensorsUtils.getValuesOfRows(jsonObject);
                    JSONArray byValues = SensorsUtils.getByvaluesOfRows(jsonObject);
                    Long storeId = byValues.getLong(0);
                    CityStoreNotAbnormalDataBo.CancelOrderTopBO bo = data.new CancelOrderTopBO();
                    bo.setStoreId(storeId);
                    bo.setStoreName(byValues.getString(1));
                    bo.setOrderCancelNum(values.getJSONArray(0).getBigInteger(0));

                    bo.setStoreCancelRate(
                            SensorsUtils
                                    .bigDivision(bo.getOrderCancelNum().toString(),
                                            storeCancelOrderCount.containsKey(storeId)
                                                    ? storeCancelOrderCount.get(storeId).toString() : null,
                                            2, 100)
                                    + "%");
                    bo.setCityCancelRate(SensorsUtils.bigDivision(bo.getOrderCancelNum().toString(),
                            cityCancelOrderCount.toString(), 2, 100) + "%");

                    data.getCancelOrderTopList().add(bo);
                    mapCancelOrderData.put(storeId, bo);
                });
            } else {
                throw new RuntimeException("当日取消订单获取异常!");
            }

            // 上周同期取消订单
            SimpleResponse beforeCancelOrderResponse = this.baseSensorsDataService.cancelOrderContent(city,
                    DateUtils.getDateAfter(date, -6), DateUtils.getDateAfter(date, -6), null);
            if (beforeCancelOrderResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject cancelOrderResponseJb = JSONObject.parseObject(beforeCancelOrderResponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(cancelOrderResponseJb);
                rows.forEach(object -> {
                    JSONObject jsonObject = (JSONObject) object;
                    JSONArray values = SensorsUtils.getValuesOfRows(jsonObject);
                    JSONArray byValues = SensorsUtils.getByvaluesOfRows(jsonObject);
                    Long storeId = byValues.getLong(0);
                    if (mapCancelOrderData.containsKey(storeId)) {
                        BigInteger first = mapCancelOrderData.get(storeId).getOrderCancelNum();
                        BigInteger second = values.getJSONArray(0).getBigInteger(0);
                        String arrow = first.compareTo(second) > 0 ? "↑ " : "↓ ";
                        mapCancelOrderData.get(storeId).setWeekCancelRate(arrow + SensorsUtils
                                .bigDivision(first.subtract(second).abs().toString(), second.toString(), 2, 100) + "%");
                    }
                });
            } else {
                throw new RuntimeException("当日取消订单获取异常!");
            }

            // 前一日取消订单
            SimpleResponse yesterdayCancelOrderResponse = this.baseSensorsDataService.cancelOrderContent(city,
                    DateUtils.getDateAfter(date, -1), DateUtils.getDateAfter(date, -1), null);
            if (yesterdayCancelOrderResponse.getCode() == ReturnCode.SUCCESS.getValue()) {
                JSONObject cancelOrderResponseJb = JSONObject.parseObject(yesterdayCancelOrderResponse.getMsg());
                JSONArray rows = SensorsUtils.getRows(cancelOrderResponseJb);
                rows.sort((oa, ob) -> {//按照取消订单数目排序
                    JSONObject joa = (JSONObject) oa;
                    JSONObject job = (JSONObject) ob;
                    return SensorsUtils.getValuesOfRows(job).getJSONArray(0).getBigInteger(0)
                            .compareTo(SensorsUtils.getValuesOfRows(joa).getJSONArray(0).getBigInteger(0));
                });

                for (int i = 0; i < rows.size(); ++i) {
                    JSONObject jo = rows.getJSONObject(i);
                    JSONArray byValues = SensorsUtils.getByvaluesOfRows(jo);
                    Long storeId = byValues.getLong(0);
                    if (mapCancelOrderData.containsKey(storeId)) {
                        mapCancelOrderData.get(storeId).setBeforeTop(String.valueOf(i + 1));
                    }
                }

                for (CityStoreNotAbnormalDataBo.CancelOrderTopBO bo : data.getCancelOrderTopList()) {
                    if (StringUtils.isEmpty(bo.getBeforeTop())) bo.setBeforeTop("-");
                    if (StringUtils.isEmpty(bo.getWeekCancelRate())) bo.setWeekCancelRate("-");
                }
            } else {
                throw new RuntimeException("当日取消订单获取异常!");
            }

            response.setMsg(SensorsUtils.sensorsDataToJsonWithFilter(data));
            response.setCode(ReturnCode.SUCCESS);
            System.out.println("总取消订单数->" + response.getMsg());
        } catch (Exception e) {
            response.setMsg(e.getMessage());
            response.setCode(ReturnCode.FAILURE);
            e.printStackTrace();
        }
        return response;
    }
}
