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
package com.addcn.im.interfaces

import com.addcn.im.entity.ChatMessage

/**
 * Author:WangYongQi
 * chat action monitoring
 */
interface OnChatActionListener {

    fun onSendTextMessage(result: String): Boolean

    fun onSendImageMessage(list: MutableList<String>): Boolean

    fun onClickMessage(message: ChatMessage)

    fun onLongClickMessage(message: ChatMessage)

    fun onSendUrl(): String?

    fun onSendParam(): HashMap<String, String>?

    fun onIMRequestCode(requestType:String,code: String)

}