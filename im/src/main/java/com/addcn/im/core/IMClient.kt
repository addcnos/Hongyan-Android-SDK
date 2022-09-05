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

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.addcn.im.app.IMApp
import com.addcn.im.config.Config
import com.addcn.im.entity.ChatMessage
import com.addcn.im.interfaces.OnReceiveMessageListener
import com.addcn.im.util.LogUtil
import com.wyq.fast.utils.ObjectValueUtil
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

/**
 * Author:WangYongQi
 * im client
 */

class IMClient private constructor() {

    private var mToken = ""
    private var webSocketClient: WebSocketClient? = null
    private var messageReceiveRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())
    private var onReceiveChatListener: OnReceiveMessageListener? = null
    private var onReceiveNotifyListener: OnReceiveMessageListener? = null

    /**
     * heartbeat thread detection
     */
    private val heartBeatRunnable = object : Runnable {
        override fun run() {
            try {
                if (null != webSocketClient) {
                    if (webSocketClient!!.isOpen) {
                        webSocketClient?.send("mode: heartbeat")
                        LogUtil.logDebug("IMClient/heartBeatRunnable/mode: heartbeat")
                    } else {
                        connect(mToken, true)
                    }
                } else {
                    connect(mToken, true)
                }
            } catch (ex: Exception) {
            }
            handler.removeCallbacks(this)
            handler.postDelayed(this, heartbeatTime)
        }
    }

    fun setOnReceiveChatListener(listener: OnReceiveMessageListener) {
        this.onReceiveChatListener = listener
    }

    fun setOnReceiveNotifyListener(listener: OnReceiveMessageListener) {
        this.onReceiveNotifyListener = listener
    }

    /**
     * initialization
     */
    fun connect(token: String, isReconnect: Boolean) {
        if (isReconnect) {
            disconnect()
        }
        if (null != webSocketClient && webSocketClient!!.isOpen) {
            return
        }
        handler?.removeCallbacks(heartBeatRunnable)
        handler?.postDelayed(heartBeatRunnable, heartbeatTime)
        if (!TextUtils.isEmpty(token)) {
            mToken = token
            val host = if (IMApp.isDebugApi()) {
                IMApp.getWSSHostDebug()
            } else {
                IMApp.getWSSHost()
            }
            val url = "$host/wss?token=$token"
            val uri = URI.create(url)
            LogUtil.logDebug("IMClient/connect/url: $url")
            webSocketClient = object : WebSocketClient(uri) {
                override fun onOpen(handshake: ServerHandshake) {
                    // connection success
                    onReceiveChatListener?.onOpen(handshake?.httpStatus, handshake?.httpStatusMessage)
                    onReceiveNotifyListener?.onOpen(handshake?.httpStatus, handshake?.httpStatusMessage)
                    LogUtil.logDebug("IMClient/WebSocketClient-onOpen/connection success")
                }

                override fun onMessage(strJson: String) {
                    // Receive messages from the server
                    val message = ChatMessage()
                    if (!TextUtils.isEmpty(strJson)) {
                        try {
                            val jsonObject = ObjectValueUtil.getJSONObject(strJson)
                            if (jsonObject != null) {
                                val status = ObjectValueUtil.getJSONValue(jsonObject, "status")
                                val msgId = ObjectValueUtil.getJSONValue(jsonObject, "msg_id")
                                val arrivalsCallback = ObjectValueUtil.getJSONValue(jsonObject, "arrivals_callback")
                                if (arrivalsCallback == "2") {
                                    webSocketClient?.send("{\"msg_id\":\"${msgId}\",\"receiving_time\":\"${System.currentTimeMillis()}\"}")
                                }
                                if (status == "1") {
                                    val messageType = ObjectValueUtil.getJSONValue(jsonObject, "type")
                                    val contentObject = ObjectValueUtil.getJSONObject(jsonObject, "content")
                                    if (null != contentObject) {
                                        if (messageType == Config.msgTypeTxt || messageType == Config.msgTypeCustomize || messageType == Config.msgTypeImg) {
                                            val fromUid = ObjectValueUtil.getJSONValue(jsonObject, "from_uid")
                                            val targetUid = ObjectValueUtil.getJSONValue(jsonObject, "target_uid")
                                            val nickname = ObjectValueUtil.getJSONValue(jsonObject, "nickname")
                                            val sendTime = ObjectValueUtil.getJSONValue(jsonObject, "send_time")
                                            val messageDirection = ObjectValueUtil.getJSONValue(jsonObject, "message_direction")
                                            val mStatus = ObjectValueUtil.getJSONValue(jsonObject, "status")
                                            message.messageContent = contentObject.toString()
                                            message.messageId = msgId
                                            if (messageDirection == "1") {
                                                message.sentId = fromUid
                                                message.targetId = targetUid
                                            } else {
                                                message.sentId = targetUid
                                                message.targetId = fromUid
                                            }
                                            message.targetName = nickname
                                            message.messageType = messageType
                                            message.sendTime = sendTime
                                            message.messageDirection = messageDirection
                                            message.status = mStatus
                                            message.arrivalsCallback = arrivalsCallback
                                            sendMessage(message)
                                        }
                                    }
                                }
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            LogUtil.logDebug("IMClient/WebSocketClient-onMessage/Exception：$ex")
                        }
                    }
                    LogUtil.logDebug("IMClient/WebSocketClient-onMessage/receive messages from the server：$strJson")
                }

                override fun onClose(code: Int, reason: String, remote: Boolean) {
                    onReceiveChatListener?.onClose(code, reason, remote)
                    onReceiveNotifyListener?.onClose(code, reason, remote)
                    LogUtil.logDebug("IMClient/WebSocketClient-onClose/code:$code  reason:$reason  remote:$remote")
                }

                override fun onError(ex: Exception) {
                    onReceiveChatListener?.onError(ex)
                    onReceiveNotifyListener?.onError(ex)
                    LogUtil.logDebug("IMClient/WebSocketClient-onError/Exception:$ex")
                }
            }
            try {
                webSocketClient?.connect()
            } catch (ex: Exception) {
                ex.printStackTrace()
                LogUtil.logDebug("IMClient/connect/Exception:$ex")
            }

        } else {
            LogUtil.logDebug("IMClient/connect/token is null")
        }

    }

    /**
     * send message
     *
     * @param message
     */
    private fun sendMessage(message: ChatMessage) {
        when {
            Looper.myLooper() == Looper.getMainLooper() -> onReceiveMessage(message)
            null != handler -> {
                if (null != messageReceiveRunnable) {
                    handler.removeCallbacks(messageReceiveRunnable)
                }
                messageReceiveRunnable = Runnable { onReceiveMessage(message) }
                handler.post(messageReceiveRunnable)
            }
            else -> onReceiveMessage(message)
        }
    }

    private fun onReceiveMessage(message: ChatMessage) {
        onReceiveChatListener?.onReceived(message)
        onReceiveNotifyListener?.onReceived(message)
    }

    /**
     * close connect
     */
    fun disconnect() {
        try {
            webSocketClient?.let {
                if (it.isOpen) {
                    it.close()
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            LogUtil.logDebug("IMClient/disconnect/Exception:$ex")
        } finally {
            webSocketClient = null
        }
    }

    companion object {

        private const val heartbeatTime = (50 * 1000).toLong()

        @Volatile
        private var instance: IMClient? = null

        fun getInstance(): IMClient {
            if (null == instance) {
                synchronized(IMClient::class.java) {
                    if (null == instance) {
                        instance = IMClient()
                    }
                }
            }
            return instance!!
        }
    }

}
