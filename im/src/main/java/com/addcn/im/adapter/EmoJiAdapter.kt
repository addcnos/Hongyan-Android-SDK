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
package com.addcn.im.adapter

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.addcn.im.R
import com.addcn.im.emoji.EmoJi

/**
 * Author:WangYongQi
 * emoJi adapter
 */

class EmoJiAdapter : BaseAdapter {

    private val mContext: Context
    private val mList: MutableList<EmoJi> = mutableListOf()

    constructor(context: Context) {
        mContext = context
    }

    override fun getCount(): Int {
        return mList.size
    }

    fun restList(list: MutableList<EmoJi>) {
        mList?.clear()
        mList?.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): EmoJi? {
        return if (mList.size > position) {
            mList[position]
        } else {
            null
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view: View
        val holder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_im_emoji_facial, parent, false)
            holder = ViewHolder(view!!)
            view?.tag = holder
        } else {
            view = convertView
            holder = convertView?.tag as ViewHolder
        }
        if (mList.size > position) {
            val emoJi = mList[position]
            if (emoJi.type == "DEL") {
                val imageSpan = ImageSpan(mContext, emoJi.resId)
                val builder = SpannableStringBuilder("DEL")
                builder.setSpan(imageSpan, 0, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                holder.textView.text = builder
            } else {
                holder.textView.text = (String(Character.toChars(emoJi.code)))
            }
        }
        return view
    }

    internal inner class ViewHolder(v: View) {

        val textView: TextView = v.findViewById(R.id.textView) as TextView

    }

}