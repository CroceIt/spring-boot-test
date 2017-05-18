package com.springboottest.response.bo;

import com.alibaba.fastjson.JSONArray;
import com.springboottest.utils.SensorsUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**

 *
 * @Title: CityDataPandect.java
 * @Prject: sensors-data
 * @Package: com.springboottest.response.bo
 * @Description: TODO
 * @author: hujunzheng
 * @date: 2017年4月25日 下午2:47:22
 * @version: V1.0
 */
public class CityDataPandectBo implements Serializable {
    /**
     * @fieldName: serialVersionUID
     * @fieldType: long
     * @Description: TODO
     */
    private static final long serialVersionUID = -4189766992407301238L;

    public CityDataPandectBo() {
        cityPandect = new CityPandect();
        cityTrend = new CityTrend();
    }

    private CityPandect cityPandect;

    private CityTrend cityTrend;

    public CityPandect getCityPandect() {
        return cityPandect;
    }

    public void setCityPandect(CityPandect cityPandect) {
        this.cityPandect = cityPandect;
    }

    public CityTrend getCityTrend() {
        return cityTrend;
    }

    public void setCityTrend(CityTrend cityTrend) {
        this.cityTrend = cityTrend;
    }


    public void setShopsTotalNum(BigInteger shopsTotalNum) {
        this.cityPandect.setShopsTotalNum(shopsTotalNum);
    }

    public void setYesterdayDau(BigInteger yesterdayDau) {
        this.cityPandect.setYesterdayDau(yesterdayDau);
    }

    public void setNewRegisterNum(BigInteger newRegisterNum) {
        this.cityPandect.setNewRegisterNum(newRegisterNum);
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.cityPandect.setTotalAmount(totalAmount);
    }

    public void setTotalTotal(BigInteger totalTotal) {
        this.cityPandect.setTotalTotal(totalTotal);
    }

    public void setAvgOrderAmount() {
        this.cityPandect.setAvgOrderAmount(new BigDecimal(SensorsUtils.bigDivision(this.cityPandect.getTotalAmount().toString(), this.cityPandect.getTotalTotal().toString(), 2, 1)));
    }

    public void setOrderCR(String orderCR) {
        this.cityPandect.setOrderCR(orderCR);
    }

    public void setWeekAgaginBuyerRate(String weekAgaginBuyerRate) {
        this.cityPandect.setWeekAgaginBuyerRate(weekAgaginBuyerRate);
    }

    public void setAppOrderAmtRate(String appOrderAmtRate) {
        this.cityPandect.setAppOrderAmtRate(appOrderAmtRate);
    }

    public void setAppOrderNumRate(String appOrderNumRate) {
        this.cityPandect.setAppOrderNumRate(appOrderNumRate);
    }

    public void setTrendDauArr(JSONArray trendDauArr) {
        this.cityTrend.setTrendDauArr(trendDauArr);
    }

    public void setTrendOrderNumArr(JSONArray trendOrderNumArr) {
        this.cityTrend.setTrendOrderNumArr(trendOrderNumArr);
    }

    public void setTrendOrderAmountArr(JSONArray trendOrderAmountArr) {
        this.cityTrend.setTrendOrderAmountArr(trendOrderAmountArr);
    }

    public void setTrendOrderAvgAmountArr(JSONArray trendOrderAvgAmountArr) {
        this.cityTrend.setTrendOrderAvgAmountArr(trendOrderAvgAmountArr);
    }
}

class CityTrend implements Serializable {
    /**
     * @fieldName: serialVersionUID
     * @fieldType: long
     * @Description: TODO
     */
    private static final long serialVersionUID = -3056725280232946630L;
    private JSONArray trendDauArr;
    private JSONArray trendOrderNumArr;
    private JSONArray trendOrderAmountArr;
    private JSONArray trendOrderAvgAmountArr;

    public JSONArray getTrendDauArr() {
        return trendDauArr;
    }

    public void setTrendDauArr(JSONArray trendDauArr) {
        this.trendDauArr = trendDauArr;
    }

    public JSONArray getTrendOrderNumArr() {
        return trendOrderNumArr;
    }

    public void setTrendOrderNumArr(JSONArray trendOrderNumArr) {
        this.trendOrderNumArr = trendOrderNumArr;
    }

    public JSONArray getTrendOrderAmountArr() {
        return trendOrderAmountArr;
    }

    public void setTrendOrderAmountArr(JSONArray trendOrderAmountArr) {
        this.trendOrderAmountArr = trendOrderAmountArr;
    }

    public JSONArray getTrendOrderAvgAmountArr() {
        return trendOrderAvgAmountArr;
    }

    public void setTrendOrderAvgAmountArr(JSONArray trendOrderAvgAmountArr) {
        this.trendOrderAvgAmountArr = trendOrderAvgAmountArr;
    }
}

class CityPandect implements Serializable {
    /**
     * @fieldName: serialVersionUID
     * @fieldType: long
     * @Description: TODO
     */
    private static final long serialVersionUID = 5608414687548063572L;
    private BigInteger shopsTotalNum;
    private BigInteger yesterdayDau;
    private BigInteger newRegisterNum;
    private BigDecimal totalAmount;
    private BigInteger totalTotal;
    private BigDecimal avgOrderAmount;
    private String orderCR;
    private String weekAgaginBuyerRate;
    private String appOrderAmtRate;
    private String appOrderNumRate;

    public BigInteger getShopsTotalNum() {
        return shopsTotalNum;
    }

    public void setShopsTotalNum(BigInteger shopsTotalNum) {
        this.shopsTotalNum = shopsTotalNum;
    }

    public BigInteger getYesterdayDau() {
        return yesterdayDau;
    }

    public void setYesterdayDau(BigInteger yesterdayDau) {
        this.yesterdayDau = yesterdayDau;
    }

    public BigInteger getNewRegisterNum() {
        return newRegisterNum;
    }

    public void setNewRegisterNum(BigInteger newRegisterNum) {
        this.newRegisterNum = newRegisterNum;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigInteger getTotalTotal() {
        return totalTotal;
    }

    public void setTotalTotal(BigInteger totalTotal) {
        this.totalTotal = totalTotal;
    }

    public BigDecimal getAvgOrderAmount() {
        return avgOrderAmount;
    }

    public void setAvgOrderAmount(BigDecimal avgOrderAmount) {
        this.avgOrderAmount = avgOrderAmount;
    }

    public String getOrderCR() {
        return orderCR;
    }

    public void setOrderCR(String orderCR) {
        this.orderCR = orderCR;
    }

    public String getWeekAgaginBuyerRate() {
        return weekAgaginBuyerRate;
    }

    public void setWeekAgaginBuyerRate(String weekAgaginBuyerRate) {
        this.weekAgaginBuyerRate = weekAgaginBuyerRate;
    }

    public String getAppOrderAmtRate() {
        return appOrderAmtRate;
    }

    public void setAppOrderAmtRate(String appOrderAmtRate) {
        this.appOrderAmtRate = appOrderAmtRate;
    }

    public String getAppOrderNumRate() {
        return appOrderNumRate;
    }

    public void setAppOrderNumRate(String appOrderNumRate) {
        this.appOrderNumRate = appOrderNumRate;
    }
}
