package com.springboottest.response.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**

 *
 * @Title: CityStoreHistoryDataBO.java
 * @Prject: sensors-data
 * @Package: com.springboottest.response.bo
 * @Description: TODO
 * @author: hujunzheng
 * @date: 2017年4月26日 上午10:13:51
 * @version: V1.0
 */
public class CityStoreHistoryDataBO implements Serializable {

    /**
     * @fieldName: serialVersionUID
     * @fieldType: long
     * @Description: TODO
     */
    private static final long serialVersionUID = -7041933147330067028L;

    private BigDecimal orderTotalAmount;
    private BigInteger orderTotalNum;
    private BigInteger orderCancelNum;
    private BigInteger orderRefundNum;
    private String appOrderRate;
    private BigInteger storeRegisterNum;
    private String orderRefundRate;
    private String addTime;
    private Long storeId;
    private String storeName;
    private String orderRepeatRate;
    private Long beforeTop;
    private Long nowadaysTop;

    private BigInteger selfOrderSubmitTotalAmout;

    public BigInteger getSelfOrderSubmitTotalAmout() {
        return selfOrderSubmitTotalAmout;
    }

    public void setSelfOrderSubmitTotalAmout(BigInteger selfOrderSubmitTotalAmout) {
        this.selfOrderSubmitTotalAmout = selfOrderSubmitTotalAmout;
    }

    public BigDecimal getOrderTotalAmount() {
        return orderTotalAmount;
    }

    public void setOrderTotalAmount(BigDecimal orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }

    public BigInteger getOrderTotalNum() {
        return orderTotalNum;
    }

    public void setOrderTotalNum(BigInteger orderTotalNum) {
        this.orderTotalNum = orderTotalNum;
    }

    public BigInteger getOrderCancelNum() {
        return orderCancelNum;
    }

    public void setOrderCancelNum(BigInteger orderCancelNum) {
        this.orderCancelNum = orderCancelNum;
    }

    public BigInteger getOrderRefundNum() {
        return orderRefundNum;
    }

    public void setOrderRefundNum(BigInteger orderRefundNum) {
        this.orderRefundNum = orderRefundNum;
    }

    public String getAppOrderRate() {
        return appOrderRate;
    }

    public void setAppOrderRate(String appOrderRate) {
        this.appOrderRate = appOrderRate;
    }

    public BigInteger getStoreRegisterNum() {
        return storeRegisterNum;
    }

    public void setStoreRegisterNum(BigInteger storeRegisterNum) {
        this.storeRegisterNum = storeRegisterNum;
    }

    public String getOrderRefundRate() {
        return orderRefundRate;
    }

    public void setOrderRefundRate(String orderRefundRate) {
        this.orderRefundRate = orderRefundRate;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
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

    public String getOrderRepeatRate() {
        return orderRepeatRate;
    }

    public void setOrderRepeatRate(String orderRepeatRate) {
        this.orderRepeatRate = orderRepeatRate;
    }

    public Long getBeforeTop() {
        return beforeTop;
    }

    public void setBeforeTop(Long beforeTop) {
        this.beforeTop = beforeTop;
    }

    public Long getNowadaysTop() {
        return nowadaysTop;
    }

    public void setNowadaysTop(Long nowadaysTop) {
        this.nowadaysTop = nowadaysTop;
    }
}
