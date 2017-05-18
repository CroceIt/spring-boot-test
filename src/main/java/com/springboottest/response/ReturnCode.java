package com.springboottest.response;

/**
 * @ClassName: ReturnCode
 * @Description: TODO
 * @author: hujunzheng
 * @date: 2017年4月21日 上午11:57:42
 */
public enum ReturnCode {

    /**
     * 返回成功
     */
    SUCCESS(0),
    /**
     * 返回失败
     */
    FAILURE(1);

    Integer value;

    ReturnCode(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}