package com.springboottest.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 工具类,组装Map集合数据
 *

 * @since 2016年10月30日 下午5:38:03
 */
public class LinkedHashMapUtil {

    private final Map<String, Object> data;

    private LinkedHashMapUtil() {
        data = new LinkedHashMap<>();
    }

    public static com.springboottest.utils.LinkedHashMapUtil build() {
        return new com.springboottest.utils.LinkedHashMapUtil();
    }

    public com.springboottest.utils.LinkedHashMapUtil append(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public com.springboottest.utils.LinkedHashMapUtil appendAll(Map<String, Object> in) {
        data.putAll(in);
        return this;
    }

    public Map<String, Object> fetchAll() {
        return data;
    }
}
