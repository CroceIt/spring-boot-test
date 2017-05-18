package com.springboottest.response;

/**
 * @param <T>
 * @ClassName: ObjectResponse
 * @Description: TODO
 * @author: hujunzheng
 * @date: 2017年4月21日 上午11:56:53
 */
public class ObjectResponse<T> extends SimpleResponse {

    private static final long serialVersionUID = 4386424983471523514L;

    // 返回的简单数据对象
    private T data;

    public T getData() {
        return data;
    }

    public ObjectResponse() {
    }

    ;

    public ObjectResponse(boolean flag) {
        new SimpleResponse(flag);
    }

    /**
     * 设置返回的数据对象
     *
     * @param value
     */
    public void setData(T data) {
        this.data = data;
    }

}