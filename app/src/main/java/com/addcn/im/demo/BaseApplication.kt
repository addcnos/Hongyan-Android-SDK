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

import android.app.Application
import com.addcn.im.app.IMApp
import com.addcn.im.core.IMClient

/**
 * Author:WangYongQi
 * Application entry
 */

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this

        // 初始化SDK
        IMApp.init(this)
        // 设置域名 (以HK591为例)
        IMApp.setWSSHost("wss://im.591.com.hk")
        // IMApp.setWSSHost("wss://im.591.com.tw:1216")
        IMApp.setWSSHostDebug("wss://im.debug.591.com.hk")
        IMApp.setIMHost("https://im.591.com.hk")
        IMApp.setIMHostDebug("https://im.debug.591.com.hk")
        // 开启日志输出
        IMApp.setLogEnabled(true)
        // 接口切换成debug
        IMApp.setDebugApi(true)
        // 进行连接，参数true代表重新连接
        IMConnect.connect(true)
        // 设置通知栏消息接收监听
        IMClient.getInstance().setOnReceiveNotifyListener(IMReceiveNotify())

    }

    companion object {

        var application: Application? = null

    }

}