package org.xiaowu.wpywebframework.core.utils;

import lombok.Getter;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtils {

    // 创建OkHttpClient对象
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // 连接超时时间
            .readTimeout(30, TimeUnit.SECONDS) // 读取超时时间
            .writeTimeout(30, TimeUnit.SECONDS) // 写入超时时间
            .build();

    /**
     * GET请求
     * @param url 请求URL
     * @return 响应体字符串
     * @throws IOException 请求或响应过程中发生的错误
     */
    public static String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * POST请求
     * @param url 请求URL
     * @param requestBody 请求体
     * @param headers 请求头
     * @return 响应体字符串
     * @throws IOException 请求或响应过程中发生的错误
     */
    public static String post(String url, RequestBody requestBody, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 构造JSON请求体
     * @param jsonStr JSON字符串
     * @return JSON请求体
     */
    public static RequestBody buildJsonRequestBody(String jsonStr) {
        return RequestBody.create(MediaType.parse("application/json"), jsonStr);
    }

    /**
     * 构造表单请求体
     * @param formParams 表单参数
     * @return 表单请求体
     */
    public static RequestBody buildFormRequestBody(Map<String, String> formParams) {
        FormBody.Builder builder = new FormBody.Builder();
        if (formParams != null && formParams.size() > 0) {
            for (Map.Entry<String, String> entry : formParams.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

        /**
         * 构造Multipart请求体
         * @param multipartParams Multipart参数
         * @return Multipart请求体
         */
        public static RequestBody buildMultipartRequestBody(Map<String, Object> multipartParams) {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            if (multipartParams != null && multipartParams.size() > 0) {
                for (Map.Entry<String, Object>         entry : multipartParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    builder.addFormDataPart(key, (String) value);
                } else if (value instanceof byte[]) {
                    builder.addFormDataPart(key, null,
                            RequestBody.create(MediaType.parse("application/octet-stream"), (byte[]) value));
                } else if (value instanceof RequestBody) {
                    builder.addFormDataPart(key, null, (RequestBody) value);
                }
            }
        }
        return builder.build();
    }

    /**
     * 构造Multipart请求体，支持上传文件
     * @param multipartParams Multipart参数
     * @return Multipart请求体
     */
    public static RequestBody buildMultipartRequestBodyWithFiles(Map<String, Object> multipartParams) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (multipartParams != null && multipartParams.size() > 0) {
            for (Map.Entry<String, Object> entry : multipartParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String) {
                    builder.addFormDataPart(key, (String) value);
                } else if (value instanceof byte[]) {
                    builder.addFormDataPart(key, null,
                            RequestBody.create(MediaType.parse("application/octet-stream"), (byte[]) value));
                } else if (value instanceof RequestBody) {
                    builder.addFormDataPart(key, null, (RequestBody) value);
                } else if (value instanceof UploadFile) { // 支持上传文件
                    UploadFile file = (UploadFile) value;
                    builder.addFormDataPart(key, file.getName(),
                            RequestBody.create(MediaType.parse(file.getMimeType()), file.getFile()));
                }
            }
        }
        return builder.build();
    }

    /**
     * 上传文件
     * @param url 请求URL
     * @param file 上传的文件
     * @param headers 请求头
     * @return 响应体字符串
     * @throws IOException 请求或响应过程中发生的错误
     */
    public static String uploadFile(String url, UploadFile file, Map<String, String> headers) throws IOException {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse(file.getMimeType()), file.getFile()))
                .build();
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * 封装文件上传参数
     */
    @Getter
    public static class UploadFile {
        private final String name;
        private final String mimeType;
        private final byte[] file;

        public UploadFile(String name, String mimeType, byte[] file) {
            this.name = name;
            this.mimeType = mimeType;
            this.file = file;
        }

    }
}