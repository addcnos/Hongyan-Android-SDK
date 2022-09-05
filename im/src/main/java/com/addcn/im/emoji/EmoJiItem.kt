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

import android.util.SparseIntArray
import com.addcn.im.R
import com.addcn.im.app.IMApp

/**
 * Author:WangYongQi
 * EmoJiItem class
 */

class EmoJiItem private constructor() {

    val emoJiArray = SparseIntArray()

    private val list1 = mutableListOf<EmoJi>()
    private val list2 = mutableListOf<EmoJi>()
    private val list3 = mutableListOf<EmoJi>()
    private val list4 = mutableListOf<EmoJi>()
    private val list5 = mutableListOf<EmoJi>()
    private val list6 = mutableListOf<EmoJi>()
    private val list7 = mutableListOf<EmoJi>()

    val item1: MutableList<EmoJi>
        get() = list1

    val item2: MutableList<EmoJi>
        get() = list2

    val item3: MutableList<EmoJi>
        get() = list3

    val item4: MutableList<EmoJi>
        get() = list4

    val item5: MutableList<EmoJi>
        get() = list5

    val item6: MutableList<EmoJi>
        get() = list6

    val item7: MutableList<EmoJi>
        get() = list7

    init {
        val resources = IMApp.getContext().resources
        val resArray = resources.obtainTypedArray(R.array.im_emoji_res)
        val emoJiCode = resources.getIntArray(R.array.im_emoji_code)
        for (i in 0 until resArray.length()) {
            val emoJi = EmoJi()
            val code = emoJiCode[i]
            val resId = resArray.getResourceId(i, 0)
            emoJi.resId = resId
            emoJi.code = code
            emoJi.type = "EmoJi"
            when {
                i < 20 -> list1.add(emoJi)
                i < 40 -> list2.add(emoJi)
                i < 60 -> list3.add(emoJi)
                i < 80 -> list4.add(emoJi)
                i < 100 -> list5.add(emoJi)
                i < 120 -> list6.add(emoJi)
                i < 140 -> list7.add(emoJi)
            }
            emoJiArray.put(code, resId)
        }
        val emoJi = EmoJi()
        emoJi.type = "DEL"
        emoJi.resId = R.drawable.im_icon_emoji_delete
        list1.add(emoJi)
        list2.add(emoJi)
        list3.add(emoJi)
        list4.add(emoJi)
        list5.add(emoJi)
        list6.add(emoJi)
        list7.add(emoJi)
    }

    companion object {

        private var mInstance: EmoJiItem? = null

        val instance: EmoJiItem
            get() {
                if (mInstance == null) {
                    mInstance = EmoJiItem()
                }
                return mInstance!!
            }
    }

}
