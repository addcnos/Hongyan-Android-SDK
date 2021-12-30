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

import com.addcn.im.request.store.CookieJarImpl;
import com.addcn.im.request.store.PersistentCookieStore;

import java.net.Proxy;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author: WangYongQi
 */

public class HttpClientHelper {

    // Cookie
    private CookieJarImpl cookieJarImpl;

    private OkHttpClient.Builder mBuilder = null;

    private final static int READ_TIMEOUT = 300;
    private final static int WRITE_TIMEOUT = 300;
    private final static int CONNECT_TIMEOUT = 300;

    private volatile static HttpClientHelper instance;

    private HttpClientHelper() {
        cookieJarImpl = new CookieJarImpl(PersistentCookieStore.getInstance());
    }

    public static HttpClientHelper getInstance() {
        if (instance == null) {
            synchronized (HttpClientHelper.class) {
                if (instance == null) {
                    instance = new HttpClientHelper();
                }
            }
        }
        return instance;
    }

    private OkHttpClient getHttpClient() {
        if (null == mBuilder) {
            mBuilder = new OkHttpClient.Builder();
            mBuilder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);//设置读取超时时间
            mBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);//设置写的超时时间
            mBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);//设置连接超时时间
            if (cookieJarImpl != null) {
                mBuilder.cookieJar(cookieJarImpl);
            }
            // 设置连接使用的HTTP代理。该方法优先于proxySelector，默认代理为空，完全禁用代理使用NO_PROXY
            mBuilder.proxy(Proxy.NO_PROXY);
        }
        return mBuilder.build();
    }

    public String doGet(String url) {
        String result = "";
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("content-type", "application/json;charset:utf-8")
                    .build();
            Response response = getHttpClient().newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String doPost(String url, Map<String, String> params) {
        String result = "";
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        try {
            if (params != null) {
                Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    String key = entry.getKey() + "";
                    String value = entry.getValue() + "";
                    if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                        bodyBuilder.add(key, value);
                    }
                }
            }
        } catch (Exception e) {
        }
        try {
            Request request = new Request.Builder().url(url)
                    .addHeader("content-type", "application/json;charset:utf-8")
                    .post(bodyBuilder.build())
                    .build();
            Response response = getHttpClient().newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
