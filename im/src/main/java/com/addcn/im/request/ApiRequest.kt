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
import android.text.TextUtils
import com.addcn.im.interfaces.OnAsyncTaskCallbackListener
import com.addcn.im.interfaces.OnResultCallbackListener

/**
 * Author:WangYongQi
 * Api request asynchronous task
 */

class ApiRequest : AsyncTask<String, String, String> {

    private var mUrl: String = ""
    private var mMap = HashMap<String, String>()
    private var mRequestWay: RequestWay? = null

    private var onResultListener: OnResultCallbackListener? = null
    private var onAsyncTaskListener: OnAsyncTaskCallbackListener? = null

    private enum class RequestWay {
        GET, POST, OTHER
    }

    constructor(url: String, listener: OnResultCallbackListener?) {
        this.mUrl = url
        this.mRequestWay = RequestWay.GET
        this.onResultListener = listener
    }

    constructor(url: String, map: HashMap<String, String>, listener: OnResultCallbackListener?) {
        this.mUrl = url
        this.mMap = map
        this.mRequestWay = RequestWay.POST
        this.onResultListener = listener
    }

    constructor(url: String, listener: OnAsyncTaskCallbackListener?) {
        this.mUrl = url
        this.mRequestWay = RequestWay.OTHER
        this.onAsyncTaskListener = listener
    }

    constructor(url: String, map: HashMap<String, String>, listener: OnAsyncTaskCallbackListener?) {
        this.mUrl = url
        this.mMap = map
        this.mRequestWay = RequestWay.OTHER
        this.onAsyncTaskListener = listener
    }

    override fun doInBackground(vararg params: String?): String? {
        var result = ""
        if (!TextUtils.isEmpty(mUrl)) {
            when (mRequestWay) {
                RequestWay.GET -> result = HttpClientHelper.getInstance().doGet(mUrl)
                RequestWay.POST -> result = HttpClientHelper.getInstance().doPost(mUrl, mMap)
                RequestWay.OTHER -> onAsyncTaskListener?.let {
                    result = it.doInBackground(mUrl, mMap) + ""
                }
            }
        }
        return result
    }

    override fun onPostExecute(s: String?) {
        onResultListener?.onCallbackListener(s)
        onAsyncTaskListener?.onPostExecute(s)
    }

}
