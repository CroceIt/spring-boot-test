package com.springboottest.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;

/**

 *
 * @Title: RestTemplateUtils.java
 * @Prject: sensorsdata
 * @Package: com.springboottest.sensorsdata.utils
 * @Description: TODO
 * @author: hujunzheng
 * @date: 2017年4月20日 下午2:07:18
 * @version: V1.0
 */
public class RestTemplateUtils {

    /**
     * @ClassName: DefaultResponseErrorHandler
     * @Description: TODO
     * @author: hujunzheng
     * @date: 2017年4月20日 下午2:15:27
     */
    private static class DefaultResponseErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().value() != HttpServletResponse.SC_OK;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(response.getBody()));
            StringBuilder sb = new StringBuilder();
            String str = null;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            try {
                throw new Exception(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param url
     * @param params
     * @return
     * @Title: get
     * @author: hujunzheng
     * @Description: TODO
     * @return: String
     */
    public static String get(String url, JSONObject params) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        String response = restTemplate.getForObject(expandURL(url, params.keySet()), String.class, params);
        return response;
    }

    /**
     * @param url
     * @param params
     * @param mediaType
     * @return
     * @Title: post
     * @author: hujunzheng
     * @Description: TODO
     * @return: String
     */
    public static String post(String url, JSONObject params, MediaType mediaType) {
        RestTemplate restTemplate = new RestTemplate();
        // 拿到header信息
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(mediaType);
        HttpEntity<JSONObject> requestEntity = (mediaType == MediaType.APPLICATION_JSON
                || mediaType == MediaType.APPLICATION_JSON_UTF8) ? new HttpEntity<JSONObject>(params, requestHeaders)
                : new HttpEntity<JSONObject>(null, requestHeaders);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        String result = (mediaType == MediaType.APPLICATION_JSON || mediaType == MediaType.APPLICATION_JSON_UTF8)
                ? restTemplate.postForObject(url, requestEntity, String.class)
                : restTemplate.postForObject(expandURL(url, params.keySet()), requestEntity, String.class, params);
        return result;
    }

    /**
     * @param url
     * @param params
     * @return
     * @Title: expandURL
     * @author: hujunzheng
     * @Description: TODO
     * @return: String
     */
    private static String expandURL(String url, Set<?> keys) {
        final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");
        Matcher mc = QUERY_PARAM_PATTERN.matcher(url);
        StringBuilder sb = new StringBuilder(url);
        if (mc.find()) {
            sb.append("&");
        } else {
            sb.append("?");
        }

        for (Object key : keys) {
            sb.append(key).append("=").append("{").append(key).append("}").append("&");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
