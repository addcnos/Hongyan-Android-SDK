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
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.addcn.im.R
import com.addcn.im.config.Config
import com.addcn.im.entity.ChatMessage
import com.addcn.im.util.GlideUtil
import com.wyq.fast.interfaces.OnItemClickListener
import com.wyq.fast.utils.ObjectValueUtil
import com.wyq.fast.view.TypingView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Author: WangYongQi
 * Chat list adapter
 */

class ChatListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private var mContext: Context
    private var mList: MutableList<ChatMessage>
    private val viewTypeRight = 1
    private val viewTypeLeft = 2
    private var clickFailureListener: OnItemClickListener<ChatMessage>? = null
    private var clickMessageListener: OnItemClickListener<ChatMessage>? = null
    private var longClickMessageListener: OnItemClickListener<ChatMessage>? = null
    var lastId = ""
    var userPortrait = ""
    var targetPortrait = ""
    var curSize = 0
    var totalRecords = 0

    constructor(context: Context) {
        mContext = context
        mList = mutableListOf()
    }

    fun addList(position: Int, list: MutableList<ChatMessage>) {
        mList?.addAll(position, list)
    }

    fun add(message: ChatMessage) {
        mList?.add(message)
    }

    fun remove(message: ChatMessage) {
        mList?.remove(message)
    }

    fun clear() {
        mList?.clear()
        curSize = 0
        lastId = ""
    }

    fun isComplete(): Boolean {
        return curSize >= totalRecords
    }

    fun setOnClickFailureListener(listener: OnItemClickListener<ChatMessage>) {
        clickFailureListener = listener
    }

    fun setOnClickMessageListener(listener: OnItemClickListener<ChatMessage>) {
        clickMessageListener = listener
    }

    fun setOnLongClickMessageListener(listener: OnItemClickListener<ChatMessage>) {
        longClickMessageListener = listener
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mList?.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = mList[position]
        return when {
            message.messageDirection == "1" -> viewTypeRight
            else -> viewTypeLeft
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            viewTypeLeft -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.item_im_chat_left, parent, false)
                val holder = SentHolder(view)
                // change
                holder.typingView.setPaintColor(Color.parseColor("#cccccc"))
                holder.typingView.setDelayMilliseconds(300)
                holder.typingView.setViewSize(mContext.resources.getDimension(R.dimen.width254px), mContext.resources.getDimension(R.dimen.width116px))
                holder.typingView.setSmallRadius(mContext.resources.getDimension(R.dimen.width8px))
                holder.typingView.setBigRadius(mContext.resources.getDimension(R.dimen.width12px))
                holder.typingView.setPaddingLeft(mContext.resources.getDimension(R.dimen.width12px))
                holder.typingView.start()
                holder.typingView.postDelayed({
                    holder.typingView.stop()
                    holder.typingView.visibility = View.GONE
                    holder.tvTypingText.visibility = View.VISIBLE
                }, 1500)
                holder
            }
            viewTypeRight -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.item_im_chat_right, parent, false)
                ReceivedHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(mContext).inflate(R.layout.item_im_default, parent, false)
                return DefaultHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (mList.size > position) {
            val message = mList[position]
            if (holder is ReceivedHolder) {
                when {
                    message.messageType == Config.msgTypeTxt -> {
                        setContentText(holder.tvRightContent, message, position)
                        holder.tvRightContent.visibility = View.VISIBLE
                        holder.llRightHouseContent.visibility = View.GONE
                        holder.rlRightImage.visibility = View.GONE
                    }
                    message.messageType == Config.msgTypeCustomize -> {
                        setContentHouseLayout(holder, message.messageContent)
                        holder.tvRightContent.visibility = View.GONE
                        holder.llRightHouseContent.visibility = View.VISIBLE
                        holder.rlRightImage.visibility = View.GONE
                        holder.llRightHouseContent.setOnClickListener {
                            clickMessageListener?.onClick(it, message, position)
                        }
                        holder.llRightHouseContent.setOnLongClickListener {
                            longClickMessageListener?.onClick(it, message, position)
                            true
                        }
                    }
                    message.messageType == Config.msgTypeImg -> {
                        holder.tvRightContent.visibility = View.GONE
                        holder.llRightHouseContent.visibility = View.GONE
                        holder.rlRightImage.visibility = View.VISIBLE
                        setContentImage(holder.ivRightImage, holder.tvRightProgress, message, position)
                    }
                }
                when {
                    message.messageSentState == "0" -> {
                        holder.ivFlag.visibility = View.VISIBLE
                        holder.ivFlag.setImageResource(R.drawable.im_loading)
                    }
                    message.messageSentState == "-1" -> {
                        holder.ivFlag.visibility = View.VISIBLE
                        holder.ivFlag.setImageResource(R.drawable.im_conversation_list_msg_send_failure)
                        holder.ivFlag.setOnClickListener {
                            clickFailureListener?.onClick(it, message, position)
                        }
                    }
                    message.messageSentState == "1" -> {
                        holder.ivFlag.visibility = View.GONE
                    }
                    else -> {
                        holder.ivFlag.visibility = View.GONE
                    }
                }
                if (!TextUtils.isEmpty(message.sendTime)) {
                    holder.tvRightTime.text = message.sendTime
                } else if (message.sendTimeLong > 0) {
                    holder.tvRightTime.text = conversionTime(message.sendTimeLong)
                } else {
                    holder.tvRightTime.text = ""
                }
                val obj = holder.ivRightPortrait.getTag(holder.ivRightPortrait.id)
                if (null == obj || obj != userPortrait) {
                    holder.ivRightPortrait.setTag(holder.ivRightPortrait.id, userPortrait)
                    GlideUtil.getInstance().loadRoundImage(userPortrait, holder.ivRightPortrait, 1, Color.parseColor("#ffffff"))
                }
            } else if (holder is SentHolder) {
                when {
                    message.messageType == Config.msgTypeTxt -> {
                        setContentText(holder.tvLeftContent, message, position)
                        holder.tvLeftContent.visibility = View.VISIBLE
                        holder.llLeftHouseContent.visibility = View.GONE
                        holder.llTyping.visibility = View.GONE
                        holder.rlLeftImage.visibility = View.GONE
                    }
                    message.messageType == Config.msgTypeCustomize -> {
                        setContentHouseLayout(holder, message.messageContent)
                        holder.tvLeftContent.visibility = View.GONE
                        holder.llLeftHouseContent.visibility = View.VISIBLE
                        holder.llTyping.visibility = View.GONE
                        holder.rlLeftImage.visibility = View.GONE
                        holder.llLeftHouseContent.setOnClickListener {
                            clickMessageListener?.onClick(it, message, position)
                        }
                        holder.llLeftHouseContent.setOnLongClickListener {
                            longClickMessageListener?.onClick(it, message, position)
                            true
                        }
                        holder.llTyping.visibility = View.GONE
                    }
                    message.messageType == Config.msgTypeTyping -> {
                        holder.tvLeftContent.visibility = View.GONE
                        holder.llLeftHouseContent.visibility = View.GONE
                        holder.llTyping.visibility = View.VISIBLE
                        holder.rlLeftImage.visibility = View.GONE
                        holder.typingView.setBackgroundResource(R.drawable.im_ic_bubble_left)
                        holder.tvTypingText.setBackgroundResource(R.drawable.im_ic_bubble_left)
                        setTypingText(holder, message.messageContent)
                    }
                    message.messageType == Config.msgTypeHint -> {
                        holder.tvLeftContent.visibility = View.GONE
                        holder.llLeftHouseContent.visibility = View.GONE
                        holder.llTyping.visibility = View.GONE
                        holder.rlLeftImage.visibility = View.GONE
                    }
                    message.messageType == Config.msgTypeImg -> {
                        holder.tvLeftContent.visibility = View.GONE
                        holder.llLeftHouseContent.visibility = View.GONE
                        holder.llTyping.visibility = View.GONE
                        holder.rlLeftImage.visibility = View.VISIBLE
                        setContentImage(holder.ivLeftImage, holder.tvLeftProgress, message, position)
                    }
                }
                if (!TextUtils.isEmpty(message.sendTime)) {
                    holder.tvLeftTime.text = message.sendTime
                } else if (message.sendTimeLong > 0) {
                    holder.tvLeftTime.text = conversionTime(message.sendTimeLong)
                } else {
                    holder.tvLeftTime.text = ""
                }
                val obj = holder.ivLeftPortrait.getTag(holder.ivLeftPortrait.id)
                if (null == obj || obj != targetPortrait) {
                    holder.ivLeftPortrait.setTag(holder.ivLeftPortrait.id, targetPortrait)
                    GlideUtil.getInstance().loadRoundImage(targetPortrait, holder.ivLeftPortrait, 1, Color.parseColor("#ffffff"))
                }
            }
        }
    }

    private fun setContentText(textView: TextView, message: ChatMessage, position: Int) {
        val jsonObject = ObjectValueUtil.getJSONObject(message.messageContent)
        val content = ObjectValueUtil.getString(jsonObject, "content")
        textView.text = content
        textView.setOnClickListener { v ->
            clickMessageListener?.onClick(v, message, position)
            false
        }
        textView.setOnLongClickListener {
            longClickMessageListener?.onClick(it, message, position)
            true
        }
    }

    private fun setContentImage(imageView: ImageView, tvProgress: TextView, message: ChatMessage, position: Int) {
        val jsonObject = ObjectValueUtil.getJSONObject(message.messageContent)
        val thumbnailUrl = ObjectValueUtil.getString(jsonObject, "thumbnail_url")
        GlideUtil.getInstance().loadListImage(thumbnailUrl, imageView)
        tvProgress.text = message.imageUploadProgressText
        if (!TextUtils.isEmpty(message.imageUploadProgressText)) {
            tvProgress.visibility = View.VISIBLE
        } else {
            tvProgress.visibility = View.GONE
        }
        imageView.setOnClickListener { v -> clickMessageListener?.onClick(v, message, position) }
        imageView.setOnLongClickListener {
            longClickMessageListener?.onClick(it, message, position)
            true
        }
    }

    private fun setContentHouseLayout(holder: RecyclerView.ViewHolder, messageContent: String) {
        val houseInfo = ObjectValueUtil.getJSONObject(messageContent)
        val title = ObjectValueUtil.getJSONValue(houseInfo, "title")
        val price = ObjectValueUtil.getJSONValue(houseInfo, "price")
        val info = ObjectValueUtil.getJSONValue(houseInfo, "infos")
        val imagePath = ObjectValueUtil.getJSONValue(houseInfo, "imagePath")
        if (holder is SentHolder) {
            holder.tvHouseLine1.text = price
            holder.tvHouseLine2.text = title
            holder.tvHouseLine3.text = info
            GlideUtil.getInstance().loadListImage(imagePath, holder.ivHousePhoto)
        } else if (holder is ReceivedHolder) {
            holder.tvHouseLine1.text = price
            holder.tvHouseLine2.text = title
            holder.tvHouseLine3.text = info
            GlideUtil.getInstance().loadListImage(imagePath, holder.ivHousePhoto)
        }
    }

    private fun setTypingText(holder: SentHolder, messageContent: String) {
        val jsonObject = ObjectValueUtil.getJSONObject(messageContent)
        val summaryType = ObjectValueUtil.getString(jsonObject, "summary_type")
        val name = ObjectValueUtil.getString(jsonObject, "name")
        val whatsAppUrl = ObjectValueUtil.getString(jsonObject, "whats_app_url")
        val mobile = ObjectValueUtil.getString(jsonObject, "mobile")
        val text1 = when (summaryType) {
            "1" -> "您好，我是$name，很高興為閣下服務，關於這個樓盤的任何問題，我都可以為閣下解答。"
            else -> "您好，我是$name，關於這套房子的任何問題，我都可以為閣下解答。"
        }
        var text2 = ""
        var text2LineLength = 0
        if (!TextUtils.isEmpty(whatsAppUrl)) {
            text2LineLength = "\n".length
            text2 = "\nwhatsapp聯繫"
        }
        var text3 = ""
        var text3LineLength = 0
        if (!TextUtils.isEmpty(mobile)) {
            text3LineLength = "\n".length
            text3 = "\n打電話聯繫"
        }
        val text = text1 + text2 + text3
        val builder = SpannableStringBuilder(text)
        try {
            builder.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    if (!TextUtils.isEmpty(whatsAppUrl)) {
                        val intent = Intent()
                        intent.action = "android.intent.action.VIEW"
                        val contentUrl = Uri.parse(whatsAppUrl)
                        intent.data = contentUrl
                        mContext.startActivity(intent)
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = Color.parseColor("#0000ff")
                    ds.isUnderlineText = true
                    holder.tvTypingText.postInvalidate()
                }
            }, text1.length + text2LineLength, text1.length + text2.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            builder.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    if (!TextUtils.isEmpty(mobile)) {
                        var tel = mobile.replace("-", "")
                        val len1 = tel.length
                        tel = tel.replace("轉", "")
                        val len2 = tel.length
                        if (len1 != len2 && len2 > 10) {
                            tel = tel.substring(0, 10)
                        }
                        tel = "tel:$tel"
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(tel))
                        mContext.startActivity(intent)
                    }
                }

                override fun updateDrawState(ds: TextPaint) {
                    ds.color = Color.parseColor("#0000ff")
                    ds.isUnderlineText = true
                    holder.tvTypingText.postInvalidate()
                }
            }, text1.length + text2.length + text3LineLength, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        } catch (ex: Exception) {
        }
        holder.tvTypingText.text = builder
        holder.tvTypingText.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * conversion time
     */
    private fun conversionTime(time: Long): String {
        try {
            val curTime = System.currentTimeMillis()
            if (curTime > time) {
                when {
                    curTime - time > 1000 * 60 * 60 * 24 * 2 -> {
                        // 超過兩天，顯示日期
                        val sdr = SimpleDateFormat("MM-dd hh:mm")
                        return sdr.format(Date(time))
                    }
                    curTime - time > 1000 * 60 * 60 * 24 -> {
                        // 超過一天，顯示文字+時間
                        val sdr = SimpleDateFormat("HH:mm")
                        return "昨天 " + sdr.format(Date(time))
                    }
                    curTime - time < 1000 * 60 -> {
                        // 一分鐘之內
                        return "剛剛"
                    }
                    else -> {
                        val sdr = SimpleDateFormat("HH:mm")
                        val temp = sdr.format(Date(time))
                        val mCalendar = Calendar.getInstance()
                        mCalendar.timeInMillis = time
                        val pm = mCalendar.get(Calendar.AM_PM)
                        return if (pm == 0) {
                            "上午 $temp"
                        } else {
                            "下午 $temp"
                        }
                    }
                }
            }
        } catch (ex: Exception) {
        }
        val sdr = SimpleDateFormat("HH:mm")
        return sdr.format(Date(System.currentTimeMillis()))
    }

    inner class ReceivedHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal val ivFlag = view.findViewById<ImageView>(R.id.ivFlag)
        internal val tvRightTime = view.findViewById<TextView>(R.id.tvRightTime)
        internal val ivRightPortrait = view.findViewById<ImageView>(R.id.ivRightPortrait)
        internal val tvRightContent = view.findViewById<TextView>(R.id.tvRightContent)
        internal val llRightHouseContent = view.findViewById<LinearLayout>(R.id.llRightHouseContent)
        internal val ivHousePhoto = view.findViewById<ImageView>(R.id.ivHousePhoto)
        internal val tvHouseLine1 = view.findViewById<TextView>(R.id.tvLine1)
        internal val tvHouseLine2 = view.findViewById<TextView>(R.id.tvLine2)
        internal val tvHouseLine3 = view.findViewById<TextView>(R.id.tvLine3)
        internal val ivRightImage = view.findViewById<ImageView>(R.id.ivRightImage)
        internal val rlRightImage = view.findViewById<RelativeLayout>(R.id.rlRightImage)
        internal val tvRightProgress = view.findViewById<TextView>(R.id.tvRightProgress)
    }

    inner class SentHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal val tvLeftTime = view.findViewById<TextView>(R.id.tvLeftTime)
        internal val ivLeftPortrait = view.findViewById<ImageView>(R.id.ivLeftPortrait)
        internal val tvLeftContent = view.findViewById<TextView>(R.id.tvLeftContent)
        internal val llLeftHouseContent = view.findViewById<LinearLayout>(R.id.llLeftHouseContent)
        internal val ivHousePhoto = view.findViewById<ImageView>(R.id.ivHousePhoto)
        internal val tvHouseLine1 = view.findViewById<TextView>(R.id.tvLine1)
        internal val tvHouseLine2 = view.findViewById<TextView>(R.id.tvLine2)
        internal val tvHouseLine3 = view.findViewById<TextView>(R.id.tvLine3)
        internal val llTyping = view.findViewById<LinearLayout>(R.id.llTyping)
        internal val typingView = view.findViewById<TypingView>(R.id.typingView)
        internal val tvTypingText = view.findViewById<TextView>(R.id.tvTypingText)
        internal val ivLeftImage = view.findViewById<ImageView>(R.id.ivLeftImage)
        internal val rlLeftImage = view.findViewById<RelativeLayout>(R.id.rlLeftImage)
        internal val tvLeftProgress = view.findViewById<TextView>(R.id.tvLeftProgress)
    }

    inner class DefaultHolder(view: View) : RecyclerView.ViewHolder(view)

}
