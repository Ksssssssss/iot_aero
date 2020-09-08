//package com.aero.ops.utils;
//
//import okhttp3.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
///**
// *
// * @author: wangzhi
// * Copyright @2018 Tima Networks Inc. All Rights Reserved. 
// * 需引用相关依赖 /**OKHttp3*/  compile group: 'com.squareup.okhttp3', name: 'okhttp', version: '4.7.2'
// */
//public class OkHttpUtil {
//
//    private static int CONNECT_TIMEOUT = 10;
//    private static int READ_TIMEOUT = 10;
//    private static int WRITE_TIMEOUT = 10;
//
//    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//    private static OkHttpClient client;
//
//    private OkHttpUtil() {
//    }
//
//    /**
//     * 参数初始化
//     *
//     * @throws Exception
//     */
//    static  {
////        logger.info("\n init OkHttpUtil start");
//
//        client = new OkHttpClient().newBuilder()
//                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
//                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
//                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
//                .build();
//    }
//
//    /**
//     * post请求
//     *
//     * @param url
//     * @param content
//     * @return
//     * @throws IOException
//     */
//    public static String doPost(String url, String content) throws IOException {
//        RequestBody body = RequestBody.create(JSON, content);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//        return invoke(request);
//    }
//
//    /**
//     * post请求
//     *
//     * @param url
//     * @return
//     * @throws IOException
//     */
//    public static String doPost(String url, String content, Map<String, String> headers) throws IOException {
//        RequestBody body = RequestBody.create(JSON, content);
//
//        Request.Builder builder = new Request.Builder().url(url);
//        if (headers != null) {
//            headers.forEach((k, v) -> builder.addHeader(k, v));
//        }
//
//        builder.url(url)
//                .post(body)
//                .build();
//        return invoke(builder.build());
//    }
//
//    private static String invoke(Request request) throws IOException {
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful()) {
//                return response.body().string();
//            } else {
//                throw new IOException("Unexpected code " + response);
//            }
//        }
//    }
//}
