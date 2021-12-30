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
package com.addcn.im.app

import android.app.Application
import android.text.TextUtils
import android.view.Gravity
import com.addcn.im.config.Config
import com.wyq.fast.app.FastApp
import com.wyq.fast.utils.SPUtil

/**
 * Author:WangYongQi
 * Initialize the entry
 */

object IMApp {

    private var logEnabled: Boolean = false
    private lateinit var application: Application

    fun init(app: Application) {
        this.application = app
        // 初始化
        FastApp.init(app)
        // 设置全局Toast弹窗重力位置
        FastApp.setToastGravity(Gravity.CENTER, 0, 0)
    }

    fun setWSSHost(host: String) {
        SPUtil.getInstance(Config.cacheNameIM).put(Config.cacheKeyWSSHost, host)
    }

    fun setWSSHostDebug(host: String) {
        SPUtil.getInstance(Config.cacheNameIM).put(Config.cacheKeyWSSHostDebug, host)
    }

    fun setIMHost(host: String) {
        SPUtil.getInstance(Config.cacheNameIM).put(Config.cacheKeyIMHost, host)
    }

    fun setIMHostDebug(host: String) {
        SPUtil.getInstance(Config.cacheNameIM).put(Config.cacheKeyIMHostDebug, host)
    }

    fun setLogEnabled(boolean: Boolean) {
        this.logEnabled = boolean
    }

    fun setDebugApi(boolean: Boolean) {
        SPUtil.getInstance(Config.cacheNameIM).put(Config.cacheKeyDebug, boolean)
    }

    fun getWSSHost(): String {
        val host = SPUtil.getInstance(Config.cacheNameIM).getString(Config.cacheKeyWSSHost)
        return if (!TextUtils.isEmpty(host)) {
            host
        } else {
            Config.hostWSS
        }
    }

    fun getWSSHostDebug(): String {
        val host = SPUtil.getInstance(Config.cacheNameIM).getString(Config.cacheKeyWSSHostDebug)
        return if (!TextUtils.isEmpty(host)) {
            host
        } else {
            Config.hostWSSDebug
        }
    }

    fun getIMHost(): String {
        val host = SPUtil.getInstance(Config.cacheNameIM).getString(Config.cacheKeyIMHost)
        return if (!TextUtils.isEmpty(host)) {
            host
        } else {
            Config.hostIM
        }
    }

    fun getIMHostDebug(): String {
        val host = SPUtil.getInstance(Config.cacheNameIM).getString(Config.cacheKeyIMHostDebug)
        return if (!TextUtils.isEmpty(host)) {
            host
        } else {
            Config.hostIMDebug
        }
    }

    fun isDebugLog(): Boolean {
        return logEnabled
    }

    fun isDebugApi(): Boolean {
        return SPUtil.getInstance(Config.cacheNameIM).getBoolean(Config.cacheKeyDebug, false)
    }

    fun getContext(): Application {
        return application
    }

}
