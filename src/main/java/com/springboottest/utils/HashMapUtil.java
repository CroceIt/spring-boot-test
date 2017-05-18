package com.springboottest.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 工具类,组装Map集合数据
 *

 * @since 2016年10月30日 下午5:38:03
 */
public class HashMapUtil {

    private final Map<String, Object> data;

    private HashMapUtil() {
        data = new HashMap<String, Object>();
    }

    public static com.springboottest.utils.HashMapUtil build() {
        return new com.springboottest.utils.HashMapUtil();
    }

    public com.springboottest.utils.HashMapUtil append(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public com.springboottest.utils.HashMapUtil appendAll(Map<String, Object> in) {
        data.putAll(in);
        return this;
    }

    public Map<String, Object> fetchAll() {
        return data;
    }
}
