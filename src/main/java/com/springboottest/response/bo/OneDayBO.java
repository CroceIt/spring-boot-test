package com.springboottest.response.bo;

import java.io.Serializable;

/**

 *
 * @Title: OneDayBO.java
 * @Prject: sensors-data
 * @Package: com.springboottest.response.bo
 * @Description: TODO
 * @author: hujunzheng
 * @date: 2017年4月24日 上午10:23:10
 * @version: V1.0
 */
public class OneDayBO implements Serializable {
    /**
     * @fieldName: serialVersionUID
     * @fieldType: long
     * @Description: TODO
     */
    private static final long serialVersionUID = -7462228914826190602L;

    private Long storeId;
    private String storeName;
    private String orderTotalAmount;
    private String orderTotalNum;
    private String orderCancelNum;
    private String agent;
    private String client;
    private String regionCityId;
    private String dau;
    private String avgOrderAmount;
    private String orderPersonNum;
    private String orderRate;
    private String cancelRate;
    private String beforeTop;
    private String nowadaysTop;
    private String addTime;

    private String selfOrderSubmitTotalAmount;

    public String getSelfOrderSubmitTotalAmount() {
        return selfOrderSubmitTotalAmount;
    }

    public void setSelfOrderSubmitTotalAmount(String selfOrderSubmitTotalAmount) {
        this.selfOrderSubmitTotalAmount = selfOrderSubmitTotalAmount;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getOrderTotalAmount() {
        return orderTotalAmount;
    }

    public void setOrderTotalAmount(String orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }

    public String getOrderTotalNum() {
        return orderTotalNum;
    }

    public void setOrderTotalNum(String orderTotalNum) {
        this.orderTotalNum = orderTotalNum;
    }

    public String getOrderCancelNum() {
        return orderCancelNum;
    }

    public void setOrderCancelNum(String orderCancelNum) {
        this.orderCancelNum = orderCancelNum;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getRegionCityId() {
        return regionCityId;
    }

    public void setRegionCityId(String regionCityId) {
        this.regionCityId = regionCityId;
    }

    public String getDau() {
        return dau;
    }

    public void setDau(String dau) {
        this.dau = dau;
    }

    public String getAvgOrderAmount() {
        return avgOrderAmount;
    }

    public void setAvgOrderAmount(String avgOrderAmount) {
        this.avgOrderAmount = avgOrderAmount;
    }

    public String getOrderPersonNum() {
        return orderPersonNum;
    }

    public void setOrderPersonNum(String orderPersonNum) {
        this.orderPersonNum = orderPersonNum;
    }

    public String getOrderRate() {
        return orderRate;
    }

    public void setOrderRate(String orderRate) {
        this.orderRate = orderRate;
    }

    public String getCancelRate() {
        return cancelRate;
    }

    public void setCancelRate(String cancelRate) {
        this.cancelRate = cancelRate;
    }

    public String getBeforeTop() {
        return beforeTop;
    }

    public void setBeforeTop(String beforeTop) {
        this.beforeTop = beforeTop;
    }

    public String getNowadaysTop() {
        return nowadaysTop;
    }

    public void setNowadaysTop(String nowadaysTop) {
        this.nowadaysTop = nowadaysTop;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }
}
