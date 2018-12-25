package com.github.clairechen.utils;

import groovy.util.logging.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
public class HttpUtils {
    public static final Object CONNECTION_TIMEOUT = 5000;
    public static final Object SO_TIMEOUT = 5000;

    /**
     * get
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @return
     * @throws Exception
     */
    public static HttpResponse doGet(String host, String path, String method,
                                     Map<String, String> headers,
                                     Map<String, String> querys)
            throws Exception {
        HttpClient httpClient = wrapClient(host);

        HttpGet request = new HttpGet(buildUrl(host, path, querys)); 
		//添加请求超时处理，连接超时CONNECTION_TIMEOUT设置为150ms,数据等待时间SO_TIMEOUT超时设置为100ms
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT,SO_TIMEOUT);
        request.setParams(params);
        if (headers!=null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        return httpClient.execute(request);
    }

    /**
     * post form
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param bodys
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path, String method,
                                      Map<String, String> headers,
                                      Map<String, String> querys,
                                      Map<String, String> bodys)
            throws Exception {
        HttpClient httpClient = wrapClient(host);

        HttpPost request = new HttpPost(buildUrl(host, path, querys));
		//添加请求超时处理，连接超时CONNECTION_TIMEOUT设置为150ms,数据等待时间SO_TIMEOUT超时设置为100ms
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT,SO_TIMEOUT);
        request.setParams(params);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (bodys != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

            for (String key : bodys.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, bodys.get(key)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
            formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
            request.setEntity(formEntity);
        }

        return httpClient.execute(request);
    }

    public static HttpResponse getWithCookie(String host, String path,
                                             Map<String, String> headers,
                                             Map<String, String> querys, Cookie cookie)
            throws Exception {
        HttpClient httpClient = wrapClient(host);
        DefaultHttpClient defaultHttpClient = (DefaultHttpClient) httpClient;
        defaultHttpClient.getCookieStore().addCookie(cookie);
        HttpGet request = new HttpGet(buildUrl(host, path, querys));
        //添加请求超时处理，连接超时CONNECTION_TIMEOUT设置为150ms,数据等待时间SO_TIMEOUT超时设置为100ms
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT,SO_TIMEOUT);
        request.setParams(params);
        if (headers!=null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        return httpClient.execute(request);
    }

    /**
     *
     * @param host
     * @param path
     * @param headers
     * @param querys
     * @param bodys
     * @param cookie
     * @return
     * @throws Exception
     */
    public static HttpResponse postWithCookie(String host, String path,
                                              Map<String, String> headers,
                                              Map<String, String> querys,
                                              Map<String, String> bodys,Cookie cookie)
            throws Exception {
        HttpClient httpClient = wrapClient(host);
        DefaultHttpClient defaultHttpClient = (DefaultHttpClient) httpClient;
        defaultHttpClient.getCookieStore().addCookie(cookie);
        HttpPost request = new HttpPost(buildUrl(host, path, querys));
        //添加请求超时处理，连接超时CONNECTION_TIMEOUT设置为150ms,数据等待时间SO_TIMEOUT超时设置为100ms
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT,SO_TIMEOUT);
        request.setParams(params);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }
        if (bodys != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

            for (String key : bodys.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, bodys.get(key)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
            formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
            request.setEntity(formEntity);
        }
        HttpResponse response = httpClient.execute(request);
        return  response;
    }
    /**
     * Post String
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path, String method,
                                      Map<String, String> headers,
                                      Map<String, String> querys,
                                      String body)
            throws Exception {
        HttpClient httpClient = wrapClient(host);


        HttpPost request = new HttpPost(buildUrl(host, path, querys));
        //添加请求超时处理，连接超时CONNECTION_TIMEOUT设置为150ms,数据等待时间SO_TIMEOUT超时设置为100ms
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT,SO_TIMEOUT);
        request.setParams(params);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, "utf-8"));
        }

        return httpClient.execute(request);
    }

    /**
     * 带json类型的请求
     * @param host
     * @param path
     * @param headers
     * @param querys
     * @param json
     * @param cookie
     * @return
     * @throws Exception
     */
    public static HttpResponse postJson(String host, String path,
                                      Map<String, String> headers,
                                      Map<String, String> querys,
                                      String json,Cookie cookie)
            throws Exception {
        HttpClient httpClient = wrapClient(host);
        DefaultHttpClient defaultHttpClient = (DefaultHttpClient) httpClient;
        defaultHttpClient.getCookieStore().addCookie(cookie);
        HttpPost request = new HttpPost(buildUrl(host, path, querys));
		//添加请求超时处理，连接超时CONNECTION_TIMEOUT设置为150ms,数据等待时间SO_TIMEOUT超时设置为100ms
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT,SO_TIMEOUT);
        request.setParams(params);
        if(headers != null && !(headers.isEmpty())) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        request.addHeader("Content-Type","application/json");
        StringEntity s = new StringEntity(json.toString(),"UTF-8");
        request.setEntity(s);

        return httpClient.execute(request);
    }


    public static HttpResponse postJson2Ali(String host, String path, String json) throws Exception {
        HttpClient httpClient = wrapClient(host);
        DefaultHttpClient defaultHttpClient = (DefaultHttpClient) httpClient;
        HttpPost request = new HttpPost(buildUrl(host, path, null));
        request.addHeader("Content-Type","application/json");
        StringEntity s = new StringEntity(json.toString(),"UTF-8");
        request.setEntity(s);
        return httpClient.execute(request);
    }

    /**
     * 文件上传
     * @param host
     * @param path
     * @param headers
     * @param querys
     * @param bodys
     * @param cookie
     * @return
     * @throws Exception
     */
    public static HttpResponse uploadPost(String host, String path,
                                        Map<String, String> headers,
                                        Map<String, String> querys,
                                          Map<String, String> bodys,Cookie cookie,File file)
            throws Exception {
        HttpClient httpClient = wrapClient(host);
        DefaultHttpClient defaultHttpClient = (DefaultHttpClient) httpClient;
        defaultHttpClient.getCookieStore().addCookie(cookie);
        HttpPost request = new HttpPost(buildUrl(host, path, querys));
        //添加请求超时处理，连接超时CONNECTION_TIMEOUT设置为200ms,数据等待时间SO_TIMEOUT超时设置为100ms
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT,SO_TIMEOUT);
        request.setParams(params);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }
//        ClassPathResource classPathResource = new ClassPathResource("Template-blank.xlsx");
//        System.out.println("上传文件文件名："+classPathResource.getFilename());
        MultipartEntityBuilder mEntityBuilder = MultipartEntityBuilder.create();
        mEntityBuilder.addBinaryBody("filedata", file);
        request.setEntity(mEntityBuilder.build());
        return httpClient.execute(request);
    }


    /**
     * Post stream
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPost(String host, String path, String method,
                                      Map<String, String> headers,
                                      Map<String, String> querys,
                                      byte[] body)
            throws Exception {
        HttpClient httpClient = wrapClient(host);

        HttpPost request = new HttpPost(buildUrl(host, path, querys));
       //添加请求超时处理，连接超时CONNECTION_TIMEOUT设置为150ms,数据等待时间SO_TIMEOUT超时设置为100ms
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT,SO_TIMEOUT);
        request.setParams(params);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (body != null) {
            request.setEntity(new ByteArrayEntity(body));
        }

        return httpClient.execute(request);
    }

    /**
     * Put String
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPut(String host, String path, String method,
                                     Map<String, String> headers,
                                     Map<String, String> querys,
                                     String body,Cookie cookie)
            throws Exception {
        HttpClient httpClient = wrapClient(host);
        DefaultHttpClient defaultHttpClient = (DefaultHttpClient) httpClient;
        defaultHttpClient.getCookieStore().addCookie(cookie);
        HttpPut request = new HttpPut(buildUrl(host, path, querys));
        //添加请求超时处理，连接超时CONNECTION_TIMEOUT设置为150ms,数据等待时间SO_TIMEOUT超时设置为100ms
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT,SO_TIMEOUT);
        request.setParams(params);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, "utf-8"));
        }

        return httpClient.execute(request);
    }


    /**
     * Put FormData
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param bodys
     * @return
     * @throws Exception
     */
    public static HttpResponse putWithCookie(String host, String path, String method,
                                     Map<String, String> headers,
                                     Map<String, String> querys,
                                             Map<String, String>  bodys,
                                             Cookie cookie)
        throws Exception {
        HttpClient httpClient = wrapClient(host);
        DefaultHttpClient defaultHttpClient = (DefaultHttpClient) httpClient;
        defaultHttpClient.getCookieStore().addCookie(cookie);
        HttpPut request = new HttpPut(buildUrl(host, path, querys));
        //添加请求超时处理，连接超时CONNECTION_TIMEOUT设置为150ms,数据等待时间SO_TIMEOUT超时设置为100ms
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT,SO_TIMEOUT);
        request.setParams(params);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (bodys != null) {
            List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

            for (String key : bodys.keySet()) {
                nameValuePairList.add(new BasicNameValuePair(key, bodys.get(key)));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nameValuePairList, "utf-8");
            formEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
            request.setEntity(formEntity);
        }

        return httpClient.execute(request);
    }

    /**
     * Put stream
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @param body
     * @return
     * @throws Exception
     */
    public static HttpResponse doPut(String host, String path, String method,
                                     Map<String, String> headers,
                                     Map<String, String> querys,
                                     byte[] body)
            throws Exception {
        HttpClient httpClient = wrapClient(host);

        HttpPut request = new HttpPut(buildUrl(host, path, querys));
        //添加请求超时处理，连接超时CONNECTION_TIMEOUT设置为150ms,数据等待时间SO_TIMEOUT超时设置为100ms
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT,SO_TIMEOUT);
        request.setParams(params);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (body != null) {
            request.setEntity(new ByteArrayEntity(body));
        }

        return httpClient.execute(request);
    }




    /**
     * Delete
     *
     * @param host
     * @param path
     * @param method
     * @param headers
     * @param querys
     * @return
     * @throws Exception
     */
    public static HttpResponse doDelete(String host, String path, String method,
                                        Map<String, String> headers,
                                        Map<String, String> querys,Cookie cookie)
            throws Exception {
        HttpClient httpClient = wrapClient(host);
        DefaultHttpClient defaultHttpClient = (DefaultHttpClient) httpClient;
        defaultHttpClient.getCookieStore().addCookie(cookie);
        HttpDelete request = new HttpDelete(buildUrl(host, path, querys));
        //添加请求超时处理，连接超时CONNECTION_TIMEOUT设置为150ms,数据等待时间SO_TIMEOUT超时设置为100ms
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,CONNECTION_TIMEOUT);
        params.setParameter(CoreConnectionPNames.SO_TIMEOUT,SO_TIMEOUT);
		request.setParams(params);
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        return httpClient.execute(request);
    }

    public static String buildUrl(String host, String path, Map<String, String> querys) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(host);
        if (!StringUtils.isBlank(path)) {
            sbUrl.append(path);
        }
        if (null != querys) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append("&");
                }
                if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!StringUtils.isBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!StringUtils.isBlank(query.getValue())) {
                        sbQuery.append("=");
                        sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append("?").append(sbQuery);
            }
        }

        return sbUrl.toString();
    }

    public  static HttpClient wrapClient(String host) {
        HttpClient httpClient = new DefaultHttpClient();
        if (host.startsWith("https://")) {
            sslClient(httpClient);
        }

        return httpClient;
    }

    private static void sslClient(HttpClient httpClient) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                @Override
                public void checkClientTrusted(X509Certificate[] xcs, String str) {

                }
                @Override
                public void checkServerTrusted(X509Certificate[] xcs, String str) {

                }
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = httpClient.getConnectionManager();
            SchemeRegistry registry = ccm.getSchemeRegistry();
            registry.register(new Scheme("https", 443, ssf));
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
}