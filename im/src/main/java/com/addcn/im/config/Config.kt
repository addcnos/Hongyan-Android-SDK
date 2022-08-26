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
package com.addcn.im.config

/**
 * Author:WangYongQi
 * IM Config
 */

object Config {

    const val updateimmessageAction = "com.addcn.im.activity.updateImMessage";
    const val cacheNameIM = "CacheIM"
    const val cacheKeyDebug = "keySwitch"
    const val cacheKeyToken = "keyToken"
    const val cacheKeyWSSHost = "keyWSSHost"
    const val cacheKeyWSSHostDebug = "keyWSSHostDebug"
    const val cacheKeyIMHost = "keyIMHost"
    const val cacheKeyIMHostDebug = "keyIMHostDebug"

    const val hostWSS = "wss://im.591.com.hk"
    const val hostWSSDebug = "wss://im.debug.591.com.hk"
    const val hostIM = "https://im.591.com.hk"
    const val hostIMDebug = "https://im.debug.591.com.hk"

    // Message type: text
    const val msgTypeTxt = "Msg:Txt"
    // Message type: image
    const val msgTypeImg = "Msg:Img"
    // Message type: Custom
    const val msgTypeCustomize = "Msg:Customize"
    // Message type: Local typing
    const val msgTypeTyping = "Msg:LocalTyping"
    // Message type: Local hint
    const val msgTypeHint = "Msg:LocalHint"
    // Message type: Information
    const val msgTypeInformationReminder = "Msg:Information"

    const val requestTypeMessageSend = "MessagesSend"
    const val requestTypeHistoricalMessage = "HistoricalMessage"

}