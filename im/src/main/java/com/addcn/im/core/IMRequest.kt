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
package com.addcn.im.core

import com.addcn.im.app.IMApp
import com.addcn.im.interfaces.OnAsyncTaskCallbackListener
import com.addcn.im.interfaces.OnProgressListener
import com.addcn.im.interfaces.OnResultCallbackListener
import com.addcn.im.request.ApiRequestHelper
import com.addcn.im.request.UploadImage
import java.io.File

/**
 * Author: WangYongQi
 * Instant messaging request
 */

object IMRequest {

    /**
     * send message
     * type: Msg:Txt / Msg:Img / Msg:Ware / Msg:Customize
     */
    fun sendMessage(token: String, type: String, targetUid: String, content: String, push: String, mark: String, listener: OnResultCallbackListener?) {
        val map = HashMap<String, String>()
        map["token"] = token
        map["type"] = type
        map["target_uid"] = targetUid
        map["content"] = content
        map["push"] = push
        map["mark"] = mark
        map["client_time"] = "" + System.currentTimeMillis()
        if (IMApp.isDebugApi()) {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHostDebug()}/messages/send", map, listener)
        } else {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHost()}/messages/send", map, listener)
        }
    }

    /**
     * send message
     * type: Msg:Txt / Msg:Img / Msg:Ware / Msg:Customize
     */
    fun doSendMessage(token: String, type: String, targetUid: String, content: String, push: String, mark: String, listener: OnAsyncTaskCallbackListener?) {
        val map = HashMap<String, String>()
        map["token"] = token
        map["type"] = type
        map["target_uid"] = targetUid
        map["content"] = content
        map["push"] = push
        map["mark"] = mark
        map["client_time"] = "" + System.currentTimeMillis()
        if (IMApp.isDebugApi()) {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHostDebug()}/messages/send", map, listener)
        } else {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHost()}/messages/send", map, listener)
        }
    }

    /**
     * load history data
     */
    fun loadHistoryData(token: String, targetUid: String, lastId: String, listener: OnResultCallbackListener?) {
        if (IMApp.isDebugApi()) {
            ApiRequestHelper.instance.doGet("${IMApp.getIMHostDebug()}/messages/getHistoricalMessage?token=$token&link_user=$targetUid&node_marker=$lastId", listener)
        } else {
            ApiRequestHelper.instance.doGet("${IMApp.getIMHost()}/messages/getHistoricalMessage?token=$token&link_user=$targetUid&node_marker=$lastId", listener)
        }
    }

    /**
     * load history data
     */
    fun doLoadHistoryData(token: String, targetUid: String, lastId: String, listener: OnAsyncTaskCallbackListener?) {
        if (IMApp.isDebugApi()) {
            ApiRequestHelper.instance.doGet("${IMApp.getIMHostDebug()}/messages/getHistoricalMessage?token=$token&link_user=$targetUid&node_marker=$lastId", listener)
        } else {
            ApiRequestHelper.instance.doGet("${IMApp.getIMHost()}/messages/getHistoricalMessage?token=$token&link_user=$targetUid&node_marker=$lastId", listener)
        }
    }

    /**
     * Message settings read
     */
    fun readMsg(token: String, targetUid: String, listener: OnResultCallbackListener?) {
        val map = HashMap<String, String>()
        map["token"] = token
        map["target_uid"] = targetUid
        if (IMApp.isDebugApi()) {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHostDebug()}/chat/readMsg", map, listener)
        } else {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHost()}/chat/readMsg", map, listener)
        }
    }

    /**
     * Message settings read
     */
    fun doReadMsg(token: String, targetUid: String, listener: OnAsyncTaskCallbackListener?) {
        val map = HashMap<String, String>()
        map["token"] = token
        map["target_uid"] = targetUid
        if (IMApp.isDebugApi()) {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHostDebug()}/chat/readMsg", map, listener)
        } else {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHost()}/chat/readMsg", map, listener)
        }
    }

    /**
     * get all new message
     */
    fun getAllNewMessage(token: String, listener: OnResultCallbackListener?) {
        if (IMApp.isDebugApi()) {
            ApiRequestHelper.instance.doGet("${IMApp.getIMHostDebug()}/chat/getAllNewMessage?token=$token", listener)
        } else {
            ApiRequestHelper.instance.doGet("${IMApp.getIMHost()}/chat/getAllNewMessage?token=$token", listener)
        }
    }

    /**
     * get all new message
     */
    fun doGetAllNewMessage(token: String, listener: OnAsyncTaskCallbackListener?) {
        if (IMApp.isDebugApi()) {
            ApiRequestHelper.instance.doGet("${IMApp.getIMHostDebug()}/chat/getAllNewMessage?token=$token", listener)
        } else {
            ApiRequestHelper.instance.doGet("${IMApp.getIMHost()}/chat/getAllNewMessage?token=$token", listener)
        }
    }

    /**
     * getUserInfo
     */
    fun getUserInfo(token: String, targetUid: String, listener: OnResultCallbackListener?) {
        val map = HashMap<String, String>()
        map["token"] = token
        map["target_uid"] = targetUid
        if (IMApp.isDebugApi()) {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHostDebug()}/chat/getConversationInfo", map, listener)
        } else {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHost()}/chat/getConversationInfo", map, listener)
        }
    }

    /**
     * doGetUserInfo
     */
    fun doGetUserInfo(token: String, targetUid: String, listener: OnAsyncTaskCallbackListener?) {
        val map = HashMap<String, String>()
        map["token"] = token
        map["target_uid"] = targetUid
        if (IMApp.isDebugApi()) {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHostDebug()}/chat/getConversationInfo", map, listener)
        } else {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHost()}/chat/getConversationInfo", map, listener)
        }
    }

    /**
     * get upload image url
     */
    fun getUploadImageUrl(): String {
        return if (IMApp.isDebugApi()) {
            "${IMApp.getIMHostDebug()}/messages/pictureUpload"
        } else {
            "${IMApp.getIMHost()}/messages/pictureUpload"
        }
    }

    /**
     * upload image
     */
    fun uploadImage(token: String, file: File, listener: OnProgressListener?) {
        try {
            if (IMApp.isDebugApi()) {
                UploadImage.uploadImage("${IMApp.getIMHostDebug()}/messages/pictureUpload", token, file, listener)
            } else {
                UploadImage.uploadImage("${IMApp.getIMHost()}/messages/pictureUpload", token, file, listener)
            }
        } catch (ex: Exception) {
        }
    }


    /**
     * delete conversation
     */
    fun deleteConversation(token: String, targetUid: String, listener: OnResultCallbackListener?) {
        val map = HashMap<String, String>()
        map["token"] = token
        map["target_uid"] = targetUid
        if (IMApp.isDebugApi()) {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHostDebug()}/messages/delLiaisonPerson", map, listener)
        } else {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHost()}/messages/delLiaisonPerson", map, listener)
        }
    }

    /**
     * delete conversation
     */
    fun doDeleteConversation(token: String, targetUid: String, listener: OnAsyncTaskCallbackListener?) {
        val map = HashMap<String, String>()
        map["token"] = token
        map["target_uid"] = targetUid
        if (IMApp.isDebugApi()) {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHostDebug()}/messages/delLiaisonPerson", map, listener)
        } else {
            ApiRequestHelper.instance.doPost("${IMApp.getIMHost()}/messages/delLiaisonPerson", map, listener)
        }
    }

}
