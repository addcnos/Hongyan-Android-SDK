/**
 * Copyright (c) 2019 addcn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.addcn.im.request;

import android.text.TextUtils;

import com.addcn.im.app.IMApp;
import com.addcn.im.config.Config;
import com.addcn.im.interfaces.OnUploadPhotoProgressListener;
import com.addcn.im.request.store.CookieJarImpl;
import com.addcn.im.request.store.PersistentCookieStore;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Author: WangYongQi
 */

public class UploadPhoto {

    /**
     * OkHttpClient
     **/
    private OkHttpClient okHttpClient;

    private final static int READ_TIMEOUT = 300;
    private final static int WRITE_TIMEOUT = 300;
    private final static int CONNECT_TIMEOUT = 300;

    // 声明一个上传辅助类对象
    private static UploadPhoto mInstance;

    public static UploadPhoto getInstance() {
        if (mInstance == null) {
            mInstance = new UploadPhoto();
        }
        return mInstance;
    }

    private UploadPhoto() {
    }

    private OkHttpClient getHttpClient() {
        if (null == okHttpClient) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);//设置读取超时时间
            builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);//设置写的超时时间
            builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);//设置连接超时时间
            CookieJarImpl cookieJarImpl = new CookieJarImpl(PersistentCookieStore.getInstance());
            builder.cookieJar(cookieJarImpl);
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    /**
     * 上传图片
     *
     * @param baseUrl
     * @param token
     * @param progressListener
     */
    public void uploadPhoto(final String baseUrl, final String token, final File file, final OnUploadPhotoProgressListener progressListener) {
        if (TextUtils.isEmpty(baseUrl)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                try {
                    final String path = file.getPath();
                    //添加图片信息
                    builder.addFormDataPart("picture", path, RequestBody.create(MediaType.parse("image/*"), file));
                    //添加其它信息
                    builder.addFormDataPart("token", token);
                    builder.addFormDataPart("picture", path);
                    // 构建请求
                    MultipartBody requestBody = builder.build();
                    String url = baseUrl;
                    if (IMApp.INSTANCE.isDebugApi()) {
                        url = baseUrl.replace(Config.INSTANCE.hostIM, Config.INSTANCE.hostIMDebug);
                    }
                    Request request = new Request.Builder()
                            .url(url)
                            .post(new CmlRequestBody(requestBody) {
                                @Override
                                public void loading(long current, long total, boolean done) {
                                    if (null != progressListener) {
                                        progressListener.onProgress(current, total, done);
                                    }
                                }
                            })
                            .build();
                    getHttpClient().newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // 上传失败
                            if (null != progressListener) {
                                progressListener.onResult("");
                            }
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // 返回结果
                            if (null != progressListener) {
                                if (null != response) {
                                    if (response.isSuccessful()) {
                                        ResponseBody body = response.body();
                                        if (null != body) {
                                            String result = body.string();
                                            progressListener.onResult(result);
                                        } else {
                                            progressListener.onResult("");
                                        }
                                    } else {
                                        progressListener.onResult("");
                                    }
                                    response.close();
                                } else {
                                    progressListener.onResult("");
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    if (null != progressListener) {
                        progressListener.onResult("");
                    }
                }
            }
        }).start();
    }

}
