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

import android.content.Intent
import android.os.Bundle
import com.addcn.im.config.Config
import com.addcn.im.entity.ChatMessage
import com.addcn.im.interfaces.OnReceiveMessageListener
import com.wyq.fast.utils.ObjectValueUtil

/**
 * Author:WangYongQi
 * Instant messaging accepted
 */

class IMReceiveNotify : OnReceiveMessageListener {

    override fun onReceived(message: ChatMessage?) {
        if (null != message) {
            if (message.messageType == Config.msgTypeTxt || message.messageType == Config.msgTypeCustomize || message.messageType == Config.msgTypeImg) {
                var text: String
                text = when {
                    message.messageType == Config.msgTypeTxt -> {
                        val jsonObject = ObjectValueUtil.getJSONObject(message.messageContent)
                        ObjectValueUtil.getString(jsonObject, "content")
                    }
                    message.messageType == Config.msgTypeImg -> {
                        "[圖片]"
                    }
                    message.messageType == Config.msgTypeCustomize -> {
                        "[物件]"
                    }
                    else -> {
                        "您有一條新消息"
                    }
                }
                val bundle = Bundle()
                bundle.putString("target_name", "" + message.targetName)
                bundle.putString("target_uid", "" + message.targetId)
                bundle.putString("token", "9976e7b0820a0cbb9fbc5e4786ba358069b27566")
                NotifyUtil.notifyChatMessage(message.targetName, text, bundle)
            }
        }
        val i = Intent(Config.updateimmessageAction)
        try{
            if(BaseApplication.application!=null|| ("").equals(BaseApplication.application)) {
                BaseApplication.application?.sendBroadcast(i)
            }
        }catch (e: Exception  ){

        }
    }

    override fun onOpen(httpStatus: Short?, httpStatusMessage: String?) {
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
    }

    override fun onError(ex: Exception) {
    }

}
