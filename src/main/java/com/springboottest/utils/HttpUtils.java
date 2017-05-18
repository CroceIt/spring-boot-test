package com.springboottest.utils;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**

 */
public class HttpUtils {

    private static Logger logger = LoggerFactory.getLogger(com.springboottest.utils.HttpUtils.class);

    /**
     * 空值
     */
    private static Object EMPTY = "";

    /**
     * & 连接符
     */
    private static String CONJ = "&";

    /**
     * = 连接符
     */
    private static String EQUAL = "=";

    /**
     * ? 连接符
     */
    public static String _CONJ = "?";

    /**
     * 默认超时时间
     */
    private static Long DEFAULT_TIMEOUT = 100000L;

    /**
     * 默认编码为UTF8
     */
    private static Charset UTF8 = Charset.forName("UTF-8");

    /**
     * GBK编码
     */
    private static Charset GBK = Charset.forName("GBK");

    /**
     * ISO8859-1编码
     */
    private static Charset ISO8859_1 = Charset.forName("ISO8859-1");

    /**
     * SSL 连接上下文
     */

    private static SSLConnectionSocketFactory ssf;

    /**
     * 加载自定义信任证书, 可以使用https请求
     */
    static {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, new TrustManager[]{new MyX509TrustManager()}, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        ssf = new SSLConnectionSocketFactory(sslContext);
    }

    /**
     * http get请求
     *
     * @param url          请求url
     * @param requestParam 请求参数
     * @param charset      字符编码
     * @return
     */
    public static String httpGet(String url, Map<String, Object> requestParam, String charset) {
        return doGet(null, url, requestParam, charset);
    }

    /**
     * http post请求
     *
     * @param url          请求Url
     * @param requestParam 请求参数
     * @param charset      字符编码
     * @return
     */
    public static String httpPost(String url, Map<String, Object> requestParam, String charset) {
        return doPost(null, url, requestParam, charset);
    }

    /**
     * Https get 请求
     *
     * @param url          请求url
     * @param requestParam 请求参数
     * @param charset      字符编码集
     * @return 响应Json字符串
     */
    public static String httpsGet(String url, Map<String, Object> requestParam, String charset) {
        try {
            CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(ssf).build();
            return doGet(client, url, requestParam, charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Https post 请求
     *
     * @param url          请求url
     * @param requestParam 请求参数
     * @param charset      字符编码集
     * @return 响应Json字符串
     */
    public static String httpsPost(String url, Map<String, Object> requestParam, String charset) {
        try {
            CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(ssf).build();
            return doPost(client, url, requestParam, charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void upload() {
        // 上传文件到AliYun的对象存储服务器
    }

    /**
     * Https 下载文件
     *
     * @param url
     * @return 返回字节数组
     */
    public static byte[] download(String url) {
        Assert.notNull(url, "url is null");
        InputStream input = null;
        ByteArrayOutputStream output = null;
        byte[] bytes = null;
        // 创建SSL隧道链接的HttpClient
        // CloseableHttpClient client =
        // HttpClients.custom().setSSLSocketFactory(ssf).build();
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建post请求
        HttpGet httpGet = new HttpGet(url);
        // 请求数据获取响应
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null) {
            // 获取响应体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try {
                    // 获取输入流
                    input = entity.getContent();
                    output = new ByteArrayOutputStream();
                    // 将字节写入到字节输入流中
                    int len = 4096;
                    byte[] bs = new byte[len];
                    while ((len = input.read(bs)) != -1) {
                        output.write(bs, 0, len);
                    }
                    bytes = output.toByteArray();
                    return bytes;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (input != null) {
                        try {
                            input.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            logger.error("http get request response is null");
        }
        return bytes;
    }

    /**
     * 下载文件, 输出到对应目录
     *
     * @param url
     * @param path
     * @param fileName
     */
    public static void downloadFile(String url, String path, String fileName) {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url is empty");
        }
        if (StringUtils.isEmpty(path)) {
            throw new IllegalArgumentException("path is empty");
        }
        if (StringUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("file name is empty");
        }
        byte[] download = download(url);
        ByteArrayInputStream in = new ByteArrayInputStream(download);
        FileOutputStream fout = null;
        try {
            File file = new File(path, fileName);
            fout = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fout);
            int len = 4096;
            byte[] tmp = new byte[len];
            while ((len = in.read(tmp)) != -1) {
                bufferedOutputStream.write(tmp, 0, len);
                // 注意这里如果用OutputStream.write(buff)的话，图片会失真，大家可以试试
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * http/https get请求
     *
     * @param client       如果是https请求, 则需要传入client
     * @param url          请求url
     * @param requestParam 请求参数
     * @param charset      字符编码
     * @return json数据
     */
    private static String doGet(CloseableHttpClient client, String url, Map<String, Object> requestParam,
                                String charset) {
        Charset defaultCharset = !StringUtils.isEmpty(charset) ? Charset.forName(charset) : UTF8;
        if (requestParam != null) {
            url = getMethodParamHandle(url, requestParam, defaultCharset);
        }
        Assert.notNull(url, "url is null");
        // httpGet请求
        HttpGet httpGet = new HttpGet(url);
        if (client == null) {
            client = HttpClients.createDefault();
        }
        String result = null;
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            response = client.execute(httpGet);
            if (response != null) {
                entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, defaultCharset);
                }
            } else {
                logger.error("http get request response is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关流操作
            close(response, entity);
        }
        logger.info("\nGET请求 URL:{}, charset:{} \n 请求结果:\n{}", url, defaultCharset.toString(), result);
        return result;
    }

    /**
     * http/https post请求
     *
     * @param client       如果是https请求, 则需要传入client
     * @param url          请求url
     * @param requestParam 请求参数
     * @param charset      字符编码
     * @return json数据
     */
    private static String doPost(CloseableHttpClient client, String url, Map<String, Object> requestParam,
                                 String charset) {
        Charset defaultCharset = !StringUtils.isEmpty(charset) ? Charset.forName(charset) : UTF8;
        Assert.notNull(url, "url is null");
        // 将请求参数转换为HttpClient post请求参数列表
        HttpPost post = new HttpPost(url);
        if (requestParam != null) {
            UrlEncodedFormEntity entity = postMethodParamHandle(requestParam, defaultCharset);
            post.setEntity(entity);
        }
        if (client == null) {
            client = HttpClients.createDefault();
        }
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String result = null;
        try {
            response = client.execute(post);
            if (response != null) {
                // 获取响应体
                entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity);
                }
            } else {
                logger.error("http get request response is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关流操作
            close(response, entity);
        }
        logger.info("\nPOST请求 URL:{}, charset:{} \n 请求参数:{} 请求结果:\n{}", url, defaultCharset.toString(), requestParam,
                result);
        return result;
    }

    /**
     * 方法执行完毕后的关流操作
     *
     * @param response
     * @param entity
     */
    private static void close(CloseableHttpResponse response, HttpEntity entity) {
        // entity是后开的IO流所以先关entity
        try {
            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("entity close failure");
        }
        // 再关response
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("response close failure");
            }
        }
    }

    /**
     * get 方法的请求参数处理器
     *
     * @param url          请求url
     * @param requestParam get请求参数
     * @return 完整请求url
     */
    private static String getMethodParamHandle(String url, Map<String, Object> requestParam, Charset charset) {
        // 断言请求参数不为空
        Assert.notNull(requestParam, "request param is null");
        Set<Map.Entry<String, Object>> entries = requestParam.entrySet();
        StringBuffer paramBuilder = new StringBuffer();
        // 遍历请求参数, 拼接成&key=value的字符串
        for (Map.Entry<String, Object> entry : entries) {
            Object value = entry.getValue();
            if (value == null) {
                value = EMPTY;
            }
            // 判断如果是String类型, 需要将String进行Encode
            if (value instanceof String) {
                try {
                    value = URLEncoder.encode(value.toString(), charset.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    logger.error("doGet 请求参数处理编码异常, 请求参数:{}, 字符编码集", requestParam, charset);
                }
            }
            // 拼接get请求参数
            paramBuilder.append(CONJ).append(entry.getKey()).append(EQUAL).append(value);
        }
        String param = paramBuilder.length() > 1 ? paramBuilder.substring(1) : CONJ;
        // 将拼接好的参数拼接到url后面
        String conjUrl = null;
        if (!StringUtils.isEmpty(url) && !StringUtils.isEmpty(param)) {
            conjUrl = url.contains(_CONJ) ? url + CONJ + param : url + _CONJ + param;
        }
        return conjUrl;
    }

    /**
     * post 请求参数处理器
     *
     * @param requestParam post请求参数
     * @param charset      字符编码集
     * @return 返回HttpClient post请求所需参数
     */
    private static UrlEncodedFormEntity postMethodParamHandle(Map<String, Object> requestParam, Charset charset) {
        Assert.notNull(requestParam, "request param is null");
        List<NameValuePair> nameValuePairs = new LinkedList<>();
        // 遍历请求参数集合, 把键值对都放入 nameValuePairs 中
        Set<Map.Entry<String, Object>> entries = requestParam.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue() == null ? null : entry.getValue().toString();
            Assert.notNull(key, "there is null key in request param");
            Assert.notNull(value, "there is null value in request param");
            BasicNameValuePair nameValuePair = new BasicNameValuePair(key, value);
            nameValuePairs.add(nameValuePair);
        }
        // 将请求参数format一下
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs, charset);
        return entity;
    }

    /**
     * 自定义证书（信任所有） 从云采项目复制过来的
     *
     * @author xice
     */
    public static class MyX509TrustManager implements X509TrustManager {

        public static SSLSocketFactory getSslSocketFactory() throws Exception {
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, new TrustManager[]{new MyX509TrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        }

        /*
         * (non-Javadoc) 这里由于archetype不支持这种方式, 将所有.替换为_
         *
         * @see javax_net_ssl_X509TrustManager#checkClientTrusted(
         * java_security_cert_X509Certificate[], java_lang_String)
         */
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         *
         * @see javax_net_ssl_X509TrustManager#checkServerTrusted(
         * java_security_cert_X509Certificate[], java_lang_String)
         */
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         *
         * @see javax_net_ssl_X509TrustManager#getAcceptedIssuers()
         */
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
