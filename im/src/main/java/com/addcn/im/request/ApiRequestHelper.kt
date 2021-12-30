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
package com.addcn.im.request

import android.os.AsyncTask
import android.os.Build
import com.addcn.im.interfaces.OnAsyncTaskCallbackListener

import com.addcn.im.interfaces.OnResultCallbackListener

/**
 * Author:WangYongQi
 * Api request helper class
 */

class ApiRequestHelper {

    fun doGet(url: String, listener: OnResultCallbackListener?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ApiRequest(url, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } else {
            ApiRequest(url, listener).execute()
        }
    }

    fun doPost(url: String, map: HashMap<String, String>, listener: OnResultCallbackListener?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ApiRequest(url, map, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } else {
            ApiRequest(url, map, listener).execute()
        }
    }

    fun doGet(url: String, listener: OnAsyncTaskCallbackListener?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ApiRequest(url, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } else {
            ApiRequest(url, listener).execute()
        }
    }

    fun doPost(url: String, map: HashMap<String, String>, listener: OnAsyncTaskCallbackListener?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ApiRequest(url, map, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        } else {
            ApiRequest(url, map, listener).execute()
        }
    }

    companion object {

        private var mInstance: ApiRequestHelper? = null

        val instance: ApiRequestHelper
            get() {
                if (mInstance == null) {
                    mInstance = ApiRequestHelper()
                }
                return mInstance!!
            }
    }

}
