package com.springboottest.response.bo;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**

 *
 * @Title: CityStoreNotAbnormalDataBo.java
 * @Prject: sensors-data
 * @Package: com.springboottest.response.bo
 * @Description: TODO
 * @author: hujunzheng
 * @date: 2017年4月26日 下午5:01:34
 * @version: V1.0
 */
public class CityStoreNotAbnormalDataBo implements Serializable {

    /**
     * @fieldName: serialVersionUID
     * @fieldType: long
     * @Description: TODO
     */
    private static final long serialVersionUID = 3503463128232622356L;

    private List<RefundOrderTopBO> refundOrderTopList;

    private List<CancelOrderTopBO> cancelOrderTopList;

    public List<RefundOrderTopBO> getRefundOrderTopList() {
        return refundOrderTopList;
    }

    public void setRefundOrderTopList(List<RefundOrderTopBO> refundOrderTopList) {
        this.refundOrderTopList = refundOrderTopList;
    }

    public List<CancelOrderTopBO> getCancelOrderTopList() {
        return cancelOrderTopList;
    }

    public void setCancelOrderTopList(List<CancelOrderTopBO> cancelOrderTopList) {
        this.cancelOrderTopList = cancelOrderTopList;
    }

    public CityStoreNotAbnormalDataBo() {
        refundOrderTopList = new ArrayList<>();
        cancelOrderTopList = new ArrayList<>();
    }


    public class RefundOrderTopBO implements Serializable {

        /**
         * @fieldName: serialVersionUID
         * @fieldType: long
         * @Description: TODO
         */
        private static final long serialVersionUID = -6248812466214336769L;

        private Long storeId;
        private String storeName;
        private String count;
        private String storeWaitRate;
        private String cityWaitRate;
        private String weekWaitRate;
        private String weekWaitNum;
        private String beforeTop;
        private String nowadaysTop;

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

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getStoreWaitRate() {
            return storeWaitRate;
        }

        public void setStoreWaitRate(String storeWaitRate) {
            this.storeWaitRate = storeWaitRate;
        }

        public String getCityWaitRate() {
            return cityWaitRate;
        }

        public void setCityWaitRate(String cityWaitRate) {
            this.cityWaitRate = cityWaitRate;
        }

        public String getWeekWaitRate() {
            return weekWaitRate;
        }

        public void setWeekWaitRate(String weekWaitRate) {
            this.weekWaitRate = weekWaitRate;
        }

        public String getWeekWaitNum() {
            return weekWaitNum;
        }

        public void setWeekWaitNum(String weekWaitNum) {
            this.weekWaitNum = weekWaitNum;
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
    }

    public class CancelOrderTopBO implements Serializable {
        /**
         * @fieldName: serialVersionUID
         * @fieldType: long
         * @Description: TODO
         */
        private static final long serialVersionUID = 5225263496613200839L;
        private Long storeId;
        private String storeName;
        private BigInteger orderCancelNum;
        private String storeCancelRate;
        private String cityCancelRate;
        private String weekCancelRate;
        private String beforeTop;
        private String nowadaysTop;

        public BigInteger getOrderCancelNum() {
            return orderCancelNum;
        }

        public void setOrderCancelNum(BigInteger orderCancelNum) {
            this.orderCancelNum = orderCancelNum;
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

        public String getStoreCancelRate() {
            return storeCancelRate;
        }

        public void setStoreCancelRate(String storeCancelRate) {
            this.storeCancelRate = storeCancelRate;
        }

        public String getCityCancelRate() {
            return cityCancelRate;
        }

        public void setCityCancelRate(String cityCancelRate) {
            this.cityCancelRate = cityCancelRate;
        }

        public String getWeekCancelRate() {
            return weekCancelRate;
        }

        public void setWeekCancelRate(String weekCancelRate) {
            this.weekCancelRate = weekCancelRate;
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
    }
}

