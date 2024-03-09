package com.pp.common;

import com.pp.expection.PpExpection;
import lombok.AllArgsConstructor;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.pp.common.constants.CommonError.HTTP_NOT_SUPPORT;

/**
 * http请求
 */
public class OkhttpClient {
    private final static MediaType DEFAULT_MEDIATYPE = MediaType.parse("application/json");

    private Map<String, String> headers = new HashMap<>();

    private final okhttp3.OkHttpClient okHttpClient = new OkHttpClient();


    public static OkhttpClient newsInstance() {
        return new OkhttpClient();
    }


    private OkhttpClient() {

    }

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public String get(String url) {
        return execute(call(requestBuilder(HttpMethod.GET, url, "null")));
    }

    /**
     * post
     *
     * @param url
     * @param body
     * @return
     */
    public String post(String url, String body) {
        return execute(call(requestBuilder(HttpMethod.POST, url, body)));
    }

    /**
     * put
     *
     * @param url
     * @param body
     * @return
     */
    public String put(String url, String body) {
        return execute(call(requestBuilder(HttpMethod.PUT, url, body)));
    }


    /**
     * delete
     *
     * @param url
     * @param body
     * @return
     */
    public String delete(String url, String body) {
        return execute(call(requestBuilder(HttpMethod.DELETE, url, body)));
    }


    private String execute(Call call) {
        try {
            return call.execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Call call(Request request) {
        return okHttpClient.newCall(request);
    }

    private Request requestBuilder(HttpMethod method, String url, String body) {
        RequestBody requestBody = requestBodyBuild(body);
        Request.Builder builder = new Request.Builder()
                .url(url);
        switch (method) {
            case GET:
                builder.get();
                break;
            case POST:
                builder.post(requestBody);
                break;
            case PUT:
                builder.put(requestBody);
                break;
            case DELETE:
                builder.delete(requestBody);
                break;
            default:
                throw PpExpection.newsPpExpection(HTTP_NOT_SUPPORT);
        }
        if (headers != null && headers.size() > 1) {
            headers.forEach(builder::addHeader);
        }
        return builder.build();
    }


    /**
     * 添加头
     *
     * @param key
     * @param value
     */
    private void addHeader(String key, String value) {
        if (key == null) {
            return;
        }
        headers.put(key, value);
    }

    /**
     * 构建请求体
     *
     * @param body
     * @return
     */
    private RequestBody requestBodyBuild(String body) {
        return RequestBody.create(DEFAULT_MEDIATYPE, body);
    }


    @AllArgsConstructor
    private enum HttpMethod {
        GET(1),
        POST(2),
        PUT(3),
        DELETE(4);

        private final Integer code;

        public static HttpMethod of(Integer code) {
            if (code == null) return null;
            for (HttpMethod value : values()) {
                if (value.code.equals(code)) {
                    return value;
                }
            }
            return null;
        }
    }
}
