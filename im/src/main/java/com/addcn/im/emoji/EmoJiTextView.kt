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

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.widget.TextView

/**
 * Author:WangYongQi
 * emoJi textView class
 */

@SuppressLint("AppCompatCustomView")
class EmoJiTextView : TextView {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        text = text
    }

    override fun setText(text: CharSequence, type: BufferType) {
        var text = text
        if (!TextUtils.isEmpty(text)) {
            val builder = SpannableStringBuilder(text)
            replaceEmoJi(context, builder)
            text = builder
        }
        super.setText(text, type)
    }

    private fun replaceEmoJi(context: Context, text: Spannable) {
        val length = text.length
        val oldSpans = text.getSpans(0, length, DynamicDrawableSpan::class.java)
        for (i in oldSpans.indices) {
            text.removeSpan(oldSpans[i])
        }
        var i = 0
        var skip: Int
        while (i < length) {
            var icon = 0
            val unicode = Character.codePointAt(text, i)
            skip = Character.charCount(unicode)
            if (unicode > 0xff) {
                icon = EmoJiItem.instance.emoJiArray.get(unicode)
            }
            if (icon > 0) {
                val imageSpan = ImageSpan(context, icon)
                text.setSpan(imageSpan, i, i + skip, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            i += skip
        }
    }

}