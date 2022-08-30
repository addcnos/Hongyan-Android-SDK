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
package com.addcn.im.demo

import android.text.TextUtils
import com.addcn.im.config.Config
import com.addcn.im.core.IMClient
import com.addcn.im.interfaces.OnResultCallbackListener
import com.addcn.im.request.ApiRequestHelper
import com.wyq.fast.utils.ObjectValueUtil
import com.wyq.fast.utils.SPUtil

/**
 * Author:WangYongQi
 * Request token, connect IM
 */

object IMConnect {

    /**
     * connect
     */
    fun connect(isReconnect: Boolean) {
        requestToken(object : OnResultCallbackListener {
            override fun onCallbackListener(token: String?) {
                if (null != token && !TextUtils.isEmpty(token)) {
                    // connect im
                    IMClient.getInstance().connect(token, isReconnect)
                }
            }
        })
    }

    /**
     * request token
     */
    fun requestToken(listener: OnResultCallbackListener) {
        val curToken = SPUtil.getInstance(Config.cacheNameIM).getString(Config.cacheKeyToken)
        if (TextUtils.isEmpty(curToken)) {
            ApiRequestHelper.instance.doGet("https://www.example.com/api/Im/getToken", object : OnResultCallbackListener {
                override fun onCallbackListener(result: String?) {
                    if (null != result) {
                        val jsonObject = ObjectValueUtil.getJSONObject(result)
                        val status = ObjectValueUtil.getJSONValue(jsonObject, "status")
                        if (status == "1") {
                            val data = ObjectValueUtil.getJSONObject(jsonObject, "data")
                            val token = ObjectValueUtil.getJSONValue(data, "token")
                            if (!TextUtils.isEmpty(token)) {
                                SPUtil.getInstance(Config.cacheNameIM).put(Config.cacheKeyToken, token)
                                listener.onCallbackListener(token)
                            }
                        }
                    }
                }
            })
        } else {
            listener.onCallbackListener(curToken)
        }
    }

    /**
     * clear token
     * sendRequest: whether to request the server clear token
     */
    fun clearToken(sendRequest: Boolean) {
        SPUtil.getInstance(Config.cacheNameIM).put(Config.cacheKeyToken, "")
        SPUtil.getInstance(Config.cacheNameIM).remove(Config.cacheKeyToken)
        if (sendRequest) {
            ApiRequestHelper.instance.doGet("业务层接口，这里填写自己需要清空服务端token的url", object : OnResultCallbackListener {
                override fun onCallbackListener(result: String?) {
                    // 请求接口>返回数据>若有需要，可自行实现自己的业务逻辑
                }
            })
        }
    }

}
