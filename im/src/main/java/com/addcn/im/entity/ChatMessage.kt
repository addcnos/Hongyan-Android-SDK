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
package com.addcn.im.entity

import java.io.Serializable

/**
 * Author: WangYongQi
 * Chat message entity class
 */

class ChatMessage : Serializable {

    var id: String = ""
    var messageId: String = ""
    /**
     * type:Msg:Txt / Msg:Img / Msg:Ware / Msg:Customize
     */
    var messageType: String = ""
    var targetId: String = ""
    var targetName: String = ""
    var sentId: String = ""
    var messageContent: String = ""
    /**
     * orientation 1:sent(right)  2:Received(left)
     */
    var messageDirection: String = ""
    /**
     * 0 to be pushed;
     * 1 has been pushed;
     * 2 has been delivered
     */
    var status: String = ""
    /**
     * 0：The message is being sent
     * -1：Message failed to be sent
     * 1：The message was sent successfully
     */
    var messageSentState: String = ""
    /**
     * Message generation time (received by the server)
     */
    var sendTime: String = ""
    var sendTimeLong: Long = 0
    var createdAt: String = ""
    /**
     * Whether it needs to be returned.
     * 0 : no
     * 1 : yes
     */
    var arrivalsCallback: String = ""

    /**
     * picture local path
     */
    var imagePath: String = ""
    /**
     * 200：sent successfully
     * other sent failure
     */
    var imageUploadState = ""
    /**
     * 上传进度文案
     */
    var imageUploadProgressText = ""

    /**
     * 是否推送:0否1是
     * 默认值: 1
     */
    var push: String = "1"

    override fun toString(): String {
        return "ChatMessage(id='$id', messageId='$messageId', messageType='$messageType', targetId='$targetId', sentId='$sentId', messageContent='$messageContent', messageDirection='$messageDirection', status='$status', messageSentState='$messageSentState', sendTime='$sendTime', createdAt='$createdAt', arrivalsCallback='$arrivalsCallback')"
    }

}
