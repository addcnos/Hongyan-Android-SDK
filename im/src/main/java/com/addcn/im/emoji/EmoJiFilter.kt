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
package com.addcn.im.emoji

/**
 * Author:WangYongQi
 * EmoJiFilter
 */

object EmoJiFilter {

    fun containsEmoJi(source: String): Boolean {
        val len = source.length
        for (i in 0 until len) {
            val codePoint = source[i]
            if (!isEmoJiCharacter(codePoint)) {
                // If it doesn't match, the character is an EmoJi
                return true
            }
        }
        return false
    }

    private fun isEmoJiCharacter(codePoint: Char): Boolean {
        return codePoint.toInt() == 0x0 ||
                codePoint.toInt() == 0x9 ||
                codePoint.toInt() == 0xA ||
                codePoint.toInt() == 0xD ||
                (codePoint.toInt() in 0x20..0xD7FF) ||
                (codePoint.toInt() in 0xE000..0xFFFD) ||
                (codePoint.toInt() in 0x10000..0x10FFFF)
    }

}
