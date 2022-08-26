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
 * Author: WangYongQi
 * Message reception monitoring
 */

interface OnReceiveMessageListener {

    fun onReceived(message: ChatMessage?)

    fun onOpen(httpStatus: Short?, httpStatusMessage: String?)

    fun onClose(code: Int, reason: String, remote: Boolean)

    fun onError(ex: Exception)

}
