/**
 * Copyright (c) 2019 addcn
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.addcn.im.demo

import android.os.Bundle
import com.addcn.im.activity.BaseChatActivity
import com.addcn.im.config.Config
import com.addcn.im.util.LogUtil

/**
 * Author:WangYongQi
 * chat activity
 */

class ChatActivity : BaseChatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtil.logDebug("进入聊天界面，可自行实现埋点统计")
    }

    /**
     * 无需强制实现该方法，按照自己需求来即可，调用者可以通过重写onClickMessage方法来实现消息点击的事件
     */
//    override fun onClickMessage(message: ChatMessage) {
//        // 可以在此处实现点击消息的业务逻辑
//        // super.onClickMessage(message)
//        ToastUtil.showShort("点击了消息：" + message.messageContent)
//        // message.messageType：消息类型
//        when {
//            message.messageType == Config.msgTypeTxt -> {
//                // msgTypeTxt：文本、表情消息
//            }
//            message.messageType == Config.msgTypeCustomize -> {
//                // msgTypeCustomize：自定义物件消息
//            }
//            message.messageType == Config.msgTypeTyping -> {
//                // msgTypeTyping：自定义本地输入消息
//            }
//            message.messageType == Config.msgTypeHint -> {
//                // msgTypeHint：自定义本地提醒消息
//            }
//            message.messageType == Config.msgTypeImg -> {
//                // msgTypeHint：图片消息
//            }
//        }
//    }

    /**
     * 无需强制实现该方法，按照自己需求来即可，调用者可以通过重写onLongClickMessage方法来实现消息长按的事件
     */
//    override fun onLongClickMessage(message: ChatMessage) {
//        // 可以在此处实现长按点击消息的业务逻辑
//        // super.onLongClickMessage(message)
//        ToastUtil.showShort("长按点击了消息：" + message.messageContent)
//    }

    /**
     * 无需强制实现该方法，按照自己需求来即可，调用者可以通过重写onSendUrl方法来实现自定义发送接口
     */
//    override fun onSendUrl(): String? {
//        return "https://www.xxx.com"
//    }

    /**
     * 无需强制实现该方法，按照自己需求来即可，调用者可以通过重写onSendParam方法来实现自定义发送接口的参数
     */
//    override fun onSendParam(): HashMap<String, String>? {
//        val hashMap = HashMap<String, String>()
//        hashMap["type"] = "1"
//        hashMap["app_id"] = "123456"
//        hashMap["target_uid"] = "65974"
//        hashMap["token"] = "9ed54245be56b9977e979f378ea723ffe329e97b"
//        hashMap["content"] = getInputContent()
//        return hashMap
//    }

    /**
     * 无需强制实现该方法，按照自己需求来即可,调用者可以通过重写onSendTextMessage方法来实现触发文本消息的发送(也可以用于接收文本框内容)
     */
//    override fun onSendTextMessage(result: String): Boolean {
//        // 返回true则自己实现发送逻辑，返回false则会走SDK发送逻辑。如果需要埋点统计消息的发送次数，则不论返回false或者true，都可以在此处进行埋点
//        return super.onSendTextMessage(result)
//    }

    /**
     * 无需强制实现该方法，按照自己需求来即可，调用者可以通过重写onSendImageMessage方法来实现触发图片消息的发送(也可以用于接收图片路径集合)
     */
//    override fun onSendImageMessage(list: MutableList<String>): Boolean {
//        return super.onSendImageMessage(list)
//    }

    /**
     * IM-WebSocket消息接收
     * 无需强制实现该方法，按照自己需求来决定是否需要重写该方法
     */
//    override fun onReceived(chatMessage: ChatMessage?) {
//        super.onReceived(chatMessage)
//        // 可以在此处实现消息接收的其他业务逻辑
//        if (!isFinishing) {
//            if (null != chatMessage) {
//                if (!TextUtils.isEmpty(chatMessage.messageContent)) {
//                    if (chatMessage.messageType == Config.msgTypeTxt || chatMessage.messageType == Config.msgTypeCustomize || chatMessage.messageType == Config.msgTypeImg) {
//                        if (chatMessage.targetId != getTargetUid()) {
//                            var text: String
//                            text = when {
//                                chatMessage.messageType == Config.msgTypeTxt -> {
//                                    val jsonObject = ObjectValueUtil.getJSONObject(chatMessage.messageContent)
//                                    ObjectValueUtil.getString(jsonObject, "content")
//                                }
//                                chatMessage.messageType == Config.msgTypeImg -> {
//                                    "[圖片]"
//                                }
//                                chatMessage.messageType == Config.msgTypeCustomize -> {
//                                    "[物件]"
//                                }
//                                else -> {
//                                    "您有一條新消息"
//                                }
//                            }
//                            // 项目中建议用通知栏形式实现，这里demo使用toast弹窗演示
//                            ToastUtil.showShort(text)
//                        }
//                    }
//                }
//            }
//        }
//    }

    /**
     * IM-WebSocket连接成功
     * 无需强制实现该方法，按照自己需求来决定是否需要重写该方法
     */
//    override fun onOpen(httpStatus: Short?, httpStatusMessage: String?) {
//        super.onOpen(httpStatus, httpStatusMessage)
//        // 可以在此处实现连接成功的其他业务逻辑
//    }

    /**
     * IM-WebSocket连接错误
     * 无需强制实现该方法，按照自己需求来决定是否需要重写该方法
     */
//    override fun onError(ex: Exception) {
//        super.onError(ex)
//        // 可以在此处实现连接错误的其他业务逻辑
//    }

    /**
     * 获取IM接口请求类型和请求码
     * 无需强制实现该方法，按照自己需求来决定是否需要重写该方法
     */
//    override fun onIMRequestCode(requestType: String, code: String) {
//        super.onIMRequestCode(requestType, code)
//        when (requestType) {
//            Config.requestTypeMessageSend -> {
//                // 来自消息发送的接口请求
//            }
//            Config.requestTypeHistoricalMessage -> {
//                // 来自拉取历史消息的接口请求
//            }
//        }
//        when (code) {
//            "4006" -> {
//                // 可自行针对4006做清除token进行重连处理
//            }
//        }
//    }

}

