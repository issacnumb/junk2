package com.bot.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Component
@Slf4j
public class HttpClientUtils {
    /**
     * 方法描述: 发送get请求
     *
     * @param url
     * @param params
     * @param header
     * @throws
     * @Return {@link String}
     * @date 2020年07月27日 09:10:10
     */
    public static String sendGet(String url, Map<String, String> params, Map<String, String> header) throws Exception {
        HttpGet httpGet = null;
        String body = "";
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            List<String> mapList = new ArrayList<>();
            if (params != null) {
                for (Entry<String, String> entry : params.entrySet()) {
                    mapList.add(entry.getKey() + "=" + entry.getValue());
                }
            }
            if (CollectionUtils.isNotEmpty(mapList)) {
                url = url + "?";
                String paramsStr = StringUtils.join(mapList, "&");
                url = url + paramsStr;
            }

            httpGet = new HttpGet(url);

            httpGet.setProtocolVersion(HttpVersion.HTTP_1_0);
            httpGet.addHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_CLOSE);

            httpGet.setHeader("Content-type", "application/json; charset=utf-8");
            httpGet.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            if (header != null) {
                for (Entry<String, String> entry : header.entrySet()) {
                    httpGet.setHeader(entry.getKey(), entry.getValue());
                }
            }
            log.info(String.format("HttpClientUtils.sendGet() 请求地址:%s", url));
            log.info(String.format("HttpClientUtils.sendGet() 请求头:%s", header));

            HttpResponse response = httpClient.execute(httpGet);

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                log.info(String.format("HttpClientUtils.sendGet() 出错:%s", "请求失败"));
                throw new RuntimeException("请求失败");
            } else {
                body = EntityUtils.toString(response.getEntity(), "UTF-8");
                log.info(String.format("HttpClientUtils.sendGet() 请求返回:%s", body));
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }
        return body;
    }

    /**
     * 方法描述: 发送post请求-json数据
     *
     * @param url
     * @param json
     * @param header
     * @throws
     * @Return {@link String}
     * @date 2020年07月27日 09:10:54
     */
    public static OkHttpClient client = new OkHttpClient().newBuilder().build();
    public static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static String sendPostJson(String url, String json, Map<String, String> header) throws Exception {

        RequestBody body = RequestBody.create(JSON, json);

        Request.Builder builder = new Request.Builder().url(url).post(body);

        for (Map.Entry<String, String> entry : header.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        Request request = builder.build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request get = new Request.Builder().url("http://www.baidu.com").method("GET", null).build();
        Response response = client.newCall(get).execute();
        System.out.println(response.body().string());
    }
}
