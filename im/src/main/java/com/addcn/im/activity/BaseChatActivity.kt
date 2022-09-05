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
package com.addcn.im.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.addcn.im.R
import com.addcn.im.adapter.BaseViewPagerAdapter
import com.addcn.im.adapter.ChatListAdapter
import com.addcn.im.adapter.EmoJiAdapter
import com.addcn.im.app.IMApp
import com.addcn.im.config.Config
import com.addcn.im.core.IMClient
import com.addcn.im.core.IMRequest
import com.addcn.im.emoji.EmoJi
import com.addcn.im.emoji.EmoJiFilter
import com.addcn.im.emoji.EmoJiItem
import com.addcn.im.entity.ChatMessage
import com.addcn.im.glide.Glide4Engine
import com.addcn.im.interfaces.*
import com.addcn.im.request.ApiRequestHelper
import com.addcn.im.request.HttpClientHelper
import com.addcn.im.request.UploadPhoto
import com.addcn.im.ui.FixedGridView
import com.wyq.fast.activity.FastBaseAppCompatActivity
import com.wyq.fast.interfaces.OnItemClickListener
import com.wyq.fast.interfaces.permission.OnPermissionListener
import com.wyq.fast.utils.*
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.filter.Filter
import com.zhihu.matisse.internal.entity.CaptureStrategy
import com.zhihu.matisse.internal.entity.IncapableCause
import com.zhihu.matisse.internal.entity.Item
import kotlinx.android.synthetic.main.activity_im_chat.*
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.*
import kotlin.math.roundToInt

/**
 * Author:WangYongQi
 * Chat Activity
 */

abstract class BaseChatActivity : FastBaseAppCompatActivity(), View.OnClickListener, OnReceiveMessageListener, OnChatActionListener {

    private lateinit var mContext: Context
    private var mTargetUid = ""
    private var mTargetName = ""
    private lateinit var mAdapter: ChatListAdapter
    private var zoneAllowLoadingMore = false
    private val requestCodeSelectPhoto = 100
    private val whatInput = 1000
    private val whatImageProgress = 1
    private val whatImageDone = 2
    private var currentItem = 0
    private var mSlidDistance = 0
    private var mCurToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_im_chat)
        mContext = this
        initData()
        // initialization
        initialization()
        // load initial data
        loadInitialData()
        // load history data
        loadHistoryData("")
        // The message has been read
        readMsg()
        // register
        register()
        // No need to repeat connections
        IMClient.getInstance().connect(mCurToken, false)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        initData()
        tvTitle.text = mTargetName
        mAdapter.clear()
        mAdapter.notifyDataSetChanged()
        // load history data
        loadHistoryData("")
        // The message has been read
        readMsg()
    }

    private fun initData() {
        mCurToken = ObjectValueUtil.getString(intent?.extras, "token")
        mTargetUid = ObjectValueUtil.getString(intent?.extras, "target_uid")
        mTargetName = ObjectValueUtil.getString(intent?.extras, "target_name")
    }

    /**
     * initialization
     */
    private fun initialization() {
        tvTitle.text = mTargetName
        ivLeft.setOnClickListener(this)
        tvSent.setOnClickListener(this)
        ivPlugin.setOnClickListener(this)
        llAddPhoto.setOnClickListener(this)
        etInputBox.setOnClickListener(this)
        ivEmoJi.setOnClickListener(this)
        etInputBox.setOnTouchListener { _, event ->
            if (null != event) {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    etInputBox.requestFocus()
                    showSoftInput(etInputBox)
                    hideEmoJi()
                    hidePlugin()
                    sendEmptyMessageDelayed(whatInput, 250)
                }
            }
            false
        }

        // Chat content display list
        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        if (recyclerView.itemAnimator is DefaultItemAnimator) {
            (recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
        }
        // set adapter
        mAdapter = ChatListAdapter(this)
        mAdapter.setOnClickFailureListener(OnItemClickListener { _, message, _ ->
            if (null !== message) {
                message.messageSentState = "0"
                mAdapter.notifyDataSetChanged()
                if (message.messageType == Config.msgTypeImg) {
                    if (message.imageUploadState == "200") {
                        sendChatMessage(message)
                    } else if (null != message.imagePath && !TextUtils.isEmpty(message.imagePath)) {
                        mAdapter.remove(message)
                        compress(message.imagePath)
                    }
                } else {
                    sendChatMessage(message)
                }
            }
        })
        mAdapter.setOnClickMessageListener(OnItemClickListener { _, message, _ ->
            onClickMessage(message)
        })
        mAdapter.setOnLongClickMessageListener(OnItemClickListener { _, message, _ ->
            onLongClickMessage(message)
        })
        recyclerView.adapter = mAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                if (NetWorkUtil.isNetworkConnected()) {
                    if (!rv.canScrollVertically(-1) && zoneAllowLoadingMore && !mAdapter.isComplete()) {
                        zoneAllowLoadingMore = false
                        loadHistoryData(mAdapter.lastId)
                    }
                }
            }
        })
        recyclerView.setOnTouchListener { _, _ ->
            hideSoftInput()
            hideEmoJi()
            hidePlugin()
            false
        }
        // input text listener
        etInputBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (null !== s && !TextUtils.isEmpty(s.toString())) {
                    switchInputVisible(true)
                } else {
                    switchInputVisible(false)
                }
            }
        })
        // emoJi
        viewPagerEmoJi.isCanScroll = true
        viewPagerEmoJi.setOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(position: Int) {
                currentItem = position
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                val pixel = (mSlidDistance * positionOffset).roundToInt() + position * mSlidDistance
                val params = ivPointEmoJi.layoutParams as RelativeLayout.LayoutParams
                params.leftMargin = pixel
                ivPointEmoJi.layoutParams = params
            }

            override fun onPageSelected(position: Int) {
            }
        })
        val views: MutableList<View> = mutableListOf()
        views.add(getEmoJiItemView(EmoJiItem.instance.item1))
        views.add(getEmoJiItemView(EmoJiItem.instance.item2))
        views.add(getEmoJiItemView(EmoJiItem.instance.item3))
        views.add(getEmoJiItemView(EmoJiItem.instance.item4))
        views.add(getEmoJiItemView(EmoJiItem.instance.item5))
        views.add(getEmoJiItemView(EmoJiItem.instance.item6))
        views.add(getEmoJiItemView(EmoJiItem.instance.item7))
        if (views.size > 0) {
            val count = views.size
            val vSize = ScreenUtil.dpToPx(9f)
            llDotEmoJi.removeAllViews()
            for (i in 0 until count) {
                val image = ImageView(mContext)
                image.setImageResource(R.drawable.ic_lens_grey_18dp)
                val params = LinearLayout.LayoutParams(vSize, vSize)
                if (i != 0) {
                    params.leftMargin = vSize
                }
                image.layoutParams = params
                llDotEmoJi.addView(image)
            }
            mSlidDistance = vSize * 2
            rlSlidPointEmoJi.visibility = (if (count > 1) View.VISIBLE else View.GONE)
            try {
                val viewPagerAdapter = BaseViewPagerAdapter()
                viewPagerAdapter.addList(views)
                viewPagerEmoJi?.adapter = viewPagerAdapter
                viewPagerEmoJi?.currentItem = currentItem
            } catch (ex: Exception) {
            }
        }

        // add receive message listener
        IMClient.getInstance().setOnReceiveChatListener(this)
    }

    private fun switchInputVisible(boolean: Boolean) {
        if (boolean) {
            ivPlugin.visibility = View.GONE
            tvSent.visibility = View.VISIBLE
        } else {
            tvSent.visibility = View.GONE
            ivPlugin.visibility = View.VISIBLE
        }
    }

    /**
     * check json
     */
    private fun jsonCheckHouseInfo(json: String?): Boolean {
        var flag = false
        if (!TextUtils.isEmpty(json)) {
            val houseInfo = ObjectValueUtil.getJSONObject(json)
            val houseTitle = ObjectValueUtil.getJSONValue(houseInfo, "title")
            val houseAddress = ObjectValueUtil.getJSONValue(houseInfo, "address")
            val housePrice = ObjectValueUtil.getJSONValue(houseInfo, "price")
            val housePriceUnit = ObjectValueUtil.getJSONValue(houseInfo, "price_unit")
            val houseIcon = ObjectValueUtil.getJSONValue(houseInfo, "icon")
            val houseId = ObjectValueUtil.getJSONValue(houseInfo, "houseId")
            val linkUrl = ObjectValueUtil.getJSONValue(houseInfo, "linkUrl")
            if (!TextUtils.isEmpty(houseId) || !TextUtils.isEmpty(linkUrl) ||
                    !TextUtils.isEmpty(houseIcon) || !TextUtils.isEmpty(houseTitle) ||
                    !TextUtils.isEmpty(houseAddress) || !TextUtils.isEmpty(housePrice) || !TextUtils.isEmpty(housePriceUnit)) {
                flag = true
            }
        }
        return flag
    }

    /**
     * Message settings read
     */
    private fun readMsg() {
        if (null != mCurToken && !TextUtils.isEmpty(mCurToken)) {
            IMRequest.doReadMsg(mCurToken, mTargetUid, object : OnAsyncTaskCallbackListener {
                override fun doInBackground(url: String, hashMap: HashMap<String, String>?): String? {
                    return if (null != hashMap) {
                        HttpClientHelper.getInstance().doPost(url, hashMap)
                    } else {
                        ""
                    }
                }

                override fun onPostExecute(result: String?) {
                }
            })
        }
    }

    /**
     * register
     */
    private fun register() {
        // 注册权限监听
        registerPermissionListener(object : OnPermissionListener {
            override fun onPermissionAllow(requestCode: Int) {
                selectPhoto()
            }

            override fun onPermissionTempReject(tempRequestCode: Int, vararg permissions: String) {}

            override fun onPermissionAlwaysReject(alwaysRequestCode: Int, vararg permissions: String) {
                AlertDialog.Builder(this@BaseChatActivity)
                        .setMessage("獲取權限組失敗，您可以在設置中重新啟用“相機”和“存儲裝置”權限，是否前往應用程式設定頁面？")
                        .setPositiveButton("前往設置") { _, _ ->
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                        .setNegativeButton("取消") { _, _ -> }
                        .create()
                        .show()
            }
        })

        registerHandlerListener { msg ->
            if (null != msg) {
                when (msg.what) {
                    whatImageProgress -> {
                        mAdapter?.notifyDataSetChanged()
                    }
                    whatImageDone -> {
                        mAdapter?.notifyDataSetChanged()
                    }
                    whatInput -> {
                        listScrollToBottom()
                    }
                }
            }
        }
    }

    /**
     * load initial data
     */
    private fun loadInitialData() {
        IMRequest.getUserInfo(mCurToken, mTargetUid, object : OnResultCallbackListener {
            override fun onCallbackListener(string: String?) {
                try {
                    if (!TextUtils.isEmpty(string)) {
                        val jsonObject = ObjectValueUtil.getJSONObject(string)
                        val status = ObjectValueUtil.getString(jsonObject, "status")
                        if (status == "1") {
                            val data = ObjectValueUtil.getJSONObject(jsonObject, "data")
                            val user = ObjectValueUtil.getJSONObject(data, "user")
                            val userPortrait = ObjectValueUtil.getJSONValue(user, "portrait")
                            val target = ObjectValueUtil.getJSONObject(data, "target")
                            val targetName = ObjectValueUtil.getJSONValue(target, "name")
                            val targetPortrait = ObjectValueUtil.getJSONValue(target, "portrait")
                            // update portrait
                            mAdapter.userPortrait = userPortrait
                            mAdapter.targetPortrait = targetPortrait
                            mAdapter.notifyDataSetChanged()
                            listScrollToBottom()
                            // update name
                            if (TextUtils.isEmpty(mTargetName) || mTargetName == ("undefined")) {
                                tvTitle.text = targetName
                            }
                        }
                    }
                } catch (ex: Exception) {
                }
            }
        })
    }

    /**
     * load history data
     */
    private fun loadHistoryData(lastId: String) {
        try {
            if (null != mCurToken && !TextUtils.isEmpty(mCurToken)) {
                // load history data
                IMRequest.doLoadHistoryData(mCurToken, mTargetUid, lastId, object : OnAsyncTaskCallbackListener {
                    override fun doInBackground(url: String, hashMap: HashMap<String, String>?): String? {
                        return HttpClientHelper.getInstance().doGet(url)
                    }

                    override fun onPostExecute(result: String?) {
                        if (!TextUtils.isEmpty(result)) {
                            val jsonObject = ObjectValueUtil.getJSONObject(result)
                            val code = ObjectValueUtil.getJSONValue(jsonObject, "code")
                            onIMRequestCode(Config.requestTypeHistoricalMessage, code)
                            if (code == "200") {
                                val data = ObjectValueUtil.getJSONObject(jsonObject, "data")
                                var total = ObjectValueUtil.getJSONValue(data, "total")
                                if (!TextUtils.isEmpty(total)) {
                                    total = total.replace(",", "")
                                    try {
                                        val totalRecords = Integer.parseInt(total)
                                        mAdapter.totalRecords = totalRecords
                                    } catch (ex: Exception) {
                                    }
                                }
                                val dataArray = ObjectValueUtil.getJSONArray(data, "data")
                                if (null != dataArray && dataArray.length() > 0) {
                                    val list: MutableList<ChatMessage> = mutableListOf()
                                    var tempId = lastId
                                    for (i in 0 until dataArray.length()) {
                                        val item = ObjectValueUtil.getJSONObject(dataArray, i)
                                        val msgId = ObjectValueUtil.getJSONValue(item, "msg_id")
                                        val fromUid = ObjectValueUtil.getJSONValue(item, "from_uid")
                                        val targetUid = ObjectValueUtil.getJSONValue(item, "target_uid")
                                        val type = ObjectValueUtil.getJSONValue(item, "type")
                                        val sendTime = ObjectValueUtil.getJSONValue(item, "send_time")
                                        val status = ObjectValueUtil.getJSONValue(item, "status")
                                        val arrivalsCallback = ObjectValueUtil.getJSONValue(item, "arrivals_callback")
                                        val messageDirection = ObjectValueUtil.getJSONValue(item, "message_direction")
                                        val id = ObjectValueUtil.getJSONValue(item, "id")
                                        val createdAt = ObjectValueUtil.getJSONValue(item, "created_at")
                                        val contentObject = ObjectValueUtil.getJSONObject(item, "content")
                                        val chatMessage = ChatMessage()
                                        chatMessage.messageId = msgId
                                        if (messageDirection == "1") {
                                            chatMessage.targetId = targetUid
                                            chatMessage.sentId = fromUid
                                        } else {
                                            chatMessage.targetId = fromUid
                                            chatMessage.sentId = targetUid
                                        }
                                        chatMessage.messageType = type
                                        chatMessage.sendTime = sendTime
                                        chatMessage.status = status
                                        chatMessage.arrivalsCallback = arrivalsCallback
                                        chatMessage.messageDirection = messageDirection
                                        chatMessage.id = id
                                        chatMessage.createdAt = createdAt
                                        chatMessage.messageSentState = "1"
                                        if (null != contentObject) {
                                            chatMessage.messageContent = contentObject.toString()
                                            if (!TextUtils.isEmpty(chatMessage.messageContent)) {
                                                if (type == Config.msgTypeTxt || (type == Config.msgTypeCustomize && jsonCheckHouseInfo(chatMessage.messageContent)) || type == Config.msgTypeImg) {
                                                    list.add(chatMessage)
                                                    tempId = id
                                                }
                                            }
                                        }
                                    }
                                    mAdapter.lastId = tempId
                                    list.reverse()
                                    mAdapter.curSize = mAdapter.curSize + list.size
                                    mAdapter.addList(0, list)
                                    mAdapter.notifyDataSetChanged()
                                    if (lastId.isEmpty()) {
                                        listScrollToBottom()
                                    } else {
                                        recyclerView.scrollToPosition(list.size)
                                    }
                                }
                            } else if (code == "4006") {
                            }
                        }
                    }
                })
            }
            zoneAllowLoadingMore = true
        } catch (ex: Exception) {
        }
    }

    /**
     * received message
     */
    override fun onReceived(chatMessage: ChatMessage?) {
        if (!isFinishing) {
            if (null != chatMessage) {
                if (!TextUtils.isEmpty(chatMessage.messageContent)) {
                    if (chatMessage.messageType == Config.msgTypeTxt || (chatMessage.messageType == Config.msgTypeCustomize && jsonCheckHouseInfo(chatMessage.messageContent)) || chatMessage.messageType == Config.msgTypeImg) {
                        if (chatMessage.targetId == mTargetUid) {
                            mAdapter.add(chatMessage)
                            mAdapter.notifyDataSetChanged()
                            listScrollToBottom()
                            readMsg()
                        } else {
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
                        }
                    }
                }
            }
        }
    }

    /**
     * onOpen
     */
    override fun onOpen(httpStatus: Short?, httpStatusMessage: String?) {
    }

    /**
     * onError
     */
    override fun onError(ex: Exception) {
    }

    /**
     * onClose
     */
    override fun onClose(code: Int, reason: String, remote: Boolean) {
    }

    /**
     * Returns the url of the sending api
     */
    override fun onSendUrl(): String? {
        return if (IMApp.isDebugApi()) {
            IMApp.getIMHostDebug() + "/messages/send"
        } else {
            IMApp.getIMHost() + "/messages/send"
        }
    }

    /**
     * Returns the param of the sending api
     */
    override fun onSendParam(): HashMap<String, String>? {
        return null
    }

    /**
     * send text message
     */
    override fun onSendTextMessage(result: String): Boolean {
        return false
    }

    /**
     * send image message
     */
    override fun onSendImageMessage(list: MutableList<String>): Boolean {
        return false
    }

    /**
     * im request code
     **/
    override fun onIMRequestCode(requestType: String, code: String) {
    }

    /**
     * on click message
     */
    override fun onClickMessage(message: ChatMessage) {
        if (message.messageType == Config.msgTypeImg) {
            val jsonObject = ObjectValueUtil.getJSONObject(message.messageContent)
            val imgUrl = ObjectValueUtil.getString(jsonObject, "img_url")
            val intent = Intent()
            intent.setClass(mContext, ImageActivity::class.java)
            val bundle = Bundle()
            bundle.putString("img_url", imgUrl)
            intent.putExtras(bundle)
            mContext.startActivity(intent)
        }
    }

    /**
     * on long click message
     */
    override fun onLongClickMessage(message: ChatMessage) {
        if (message.messageType == Config.msgTypeTxt) {
            val jsonObject = ObjectValueUtil.getJSONObject(message.messageContent)
            val content = ObjectValueUtil.getString(jsonObject, "content")
            if (!TextUtils.isEmpty(content)) {
                if (content.startsWith("http")) {
                    val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newRawUri("Label", Uri.parse(content))
                    cm.primaryClip = clipData
                } else {
                    val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("Label", content)
                    cm.primaryClip = clipData
                }
                ToastUtil.showShort("複製消息成功")
            }
        }
    }

    /**
     * send message
     */
    fun sendChatMessage(message: ChatMessage) {
        try {
            if (null != mCurToken && !TextUtils.isEmpty(mCurToken)) {
                if (TextUtils.isEmpty(message.messageContent)) {
                    return
                }
                /**
                 * type: Msg:Txt / Msg:Img / Msg:Ware / Msg:Customize
                 * push：是否推送:0否1是 (默认值: 1)
                 */
                var hashMap = onSendParam()
                if (null == hashMap) {
                    hashMap = HashMap()
                    hashMap["token"] = mCurToken
                    hashMap["type"] = message.messageType
                    hashMap["target_uid"] = mTargetUid
                    hashMap["content"] = message.messageContent
                    hashMap["push"] = message.push
                    hashMap["mark"] = "1"
                    hashMap["client_time"] = "" + System.currentTimeMillis()
                }
                ApiRequestHelper.instance.doPost("${onSendUrl()}", hashMap, object : OnResultCallbackListener {
                    override fun onCallbackListener(string: String?) {
                        onSendMessageResult(message, string)
                    }
                })
            }
        } catch (ex: Exception) {
        }
    }

    /**
     * Send message result processing
     */
    private fun onSendMessageResult(message: ChatMessage, result: String?) {
        if (!TextUtils.isEmpty(result)) {
            val jsonObject = ObjectValueUtil.getJSONObject(result)
            val code = ObjectValueUtil.getJSONValue(jsonObject, "code")
            onIMRequestCode(Config.requestTypeMessageSend, code)
            when (code) {
                "200" -> {
                    message.messageSentState = "1"
                    if (message.messageType == Config.msgTypeCustomize) {
                        val jsonObject = ObjectValueUtil.getJSONObject(message.messageContent)
                        val houseId = ObjectValueUtil.getJSONValue(jsonObject, "houseId")
                        SPUtil.getInstance(Config.cacheNameIM).put("targetId=$mTargetUid&houseId=$houseId", true)
                    }
                }
                "4006" -> {
                    message.messageSentState = "-1"
                }
                else -> {
                    message.messageSentState = "-1"
                }
            }
            message.imageUploadProgressText = ""
            mAdapter.notifyDataSetChanged()
        } else {
            message.messageSentState = "-1"
            message.imageUploadProgressText = ""
            mAdapter.notifyDataSetChanged()
        }
    }

    /**
     * select photo
     */
    private fun selectPhoto() {
        Matisse.from(this)
                .choose(MimeType.ofImage(), true)
                .showSingleMediaType(true)
                .capture(true)
                .captureStrategy(CaptureStrategy(true, "$packageName.FileProvider"))
                .countable(true)
                .maxSelectable(9)
                .theme(R.style.Matisse_app)
                .addFilter(object : Filter() {
                    override fun constraintTypes(): Set<MimeType> {
                        return object : HashSet<MimeType>() {
                            init {
                                add(MimeType.GIF)
                            }
                        }
                    }

                    override fun filter(context: Context, item: Item): IncapableCause? {
                        return if (!needFiltering(context, item)) {
                            null
                        } else {
                            IncapableCause(IncapableCause.DIALOG, "暫不支持您選擇的圖片類型")
                        }
                    }
                })
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.95f)
                .imageEngine(Glide4Engine())
                .forResult(requestCodeSelectPhoto)
    }

    /**
     * 获取临时图片存储路径
     */
    private fun getPhotoPath(): String {
        val sdExist = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
        if (!sdExist) {
            return ""
        }
        val absolutePath = Environment.getExternalStorageDirectory().absolutePath + "/" + "hk591" + "/" + "photo"
        val dirFile = File(absolutePath)
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                return ""
            }
        }
        return absolutePath
    }

    /**
     * 这里传入的路径必须是原图路径
     */
    private fun compress(path: String) {
        val photoPath = getPhotoPath()
        if (!TextUtils.isEmpty(path)) {
            /**
             * load     	       传入原图
             * filter         	   设置开启压缩条件
             * ignoreBy	           不压缩的阈值，单位为K
             * setFocusAlpha	   设置是否保留透明通道
             * setTargetDir	       缓存压缩图片路径
             * setCompressListener 压缩回调接口
             * setRenameListener   压缩前重命名接口
             */
            Luban.with(this)
                    .load(path)
                    .ignoreBy(100)
                    .setTargetDir(photoPath)
                    .filter { path ->
                        if (null != path) {
                            !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"))
                        } else {
                            false
                        }
                    }
                    .setRenameListener { "${System.currentTimeMillis()}.jpg" }
                    .setCompressListener(object : OnCompressListener {
                        override fun onError(e: Throwable?) {
                        }

                        override fun onStart() {
                        }

                        override fun onSuccess(file: File?) {
                            if (null != file) {
                                uploadImage(path, file)
                            }
                        }
                    }).launch()
        }
    }

    /**
     * upload image
     */
    private fun uploadImage(path: String, file: File) {
        val message = ChatMessage()
        message.messageDirection = "1"
        message.sendTime = ""
        message.sendTimeLong = System.currentTimeMillis()
        message.messageContent = getImageMessageContent(path, path, "")
        message.messageSentState = "0"
        message.targetId = mTargetUid
        message.messageType = Config.msgTypeImg
        message.imagePath = path
        mAdapter.add(message)
        mAdapter.notifyDataSetChanged()
        if (mAdapter.itemCount > 0) {
            recyclerView.scrollToPosition(mAdapter.itemCount - 1)
        }
        if (null != mCurToken && !TextUtils.isEmpty(mCurToken)) {
            try {
                UploadPhoto.getInstance().uploadPhoto("${Config.hostIM}/messages/pictureUpload", mCurToken, file, object : OnUploadPhotoProgressListener {
                    override fun onProgress(current: Long, total: Long, done: Boolean) {
                        if (done) {
                            message.imageUploadProgressText = "99%"
                        } else {
                            val text = (current * 100 / total)
                            if (text >= 100) {
                                message.imageUploadProgressText = "99%"
                            } else {
                                message.imageUploadProgressText = "$text%"
                            }
                        }
                        sendEmptyMessage(whatImageProgress)
                    }

                    override fun onResult(result: String) {
                        try {
                            val jsonObject = ObjectValueUtil.getJSONObject(result)
                            val code = ObjectValueUtil.getJSONValue(jsonObject, "code")
                            val msg = ObjectValueUtil.getJSONValue(jsonObject, "message")
                            if (code == "200") {
                                val data = ObjectValueUtil.getJSONObject(jsonObject, "data")
                                val imgUrl = ObjectValueUtil.getJSONValue(data, "img_url")
                                val thumbnailUrl = ObjectValueUtil.getJSONValue(data, "thumbnail_url")
                                val extra = ObjectValueUtil.getJSONValue(data, "extra")
                                message.imageUploadState = "200"
                                message.messageContent = getImageMessageContent(imgUrl, thumbnailUrl, extra)
                                sendChatMessage(message)
                            } else {
                                message.messageSentState = "-1"
                                sendEmptyMessage(whatImageDone)
                            }
                        } catch (ex: Exception) {
                        } finally {
                            try {
                                // 删除临时图片文件
                                file.delete()
                            } catch (ex: Exception) {
                            }
                        }
                    }
                })
            } catch (ex: Exception) {
            }
        }
    }

    /**
     * return text message content
     */
    private fun getTextMessageContent(content: String?, extra: String?): String {
        return "{\"content\":\"$content\",\"extra\":\"$extra\"}"
    }

    /**
     * return image message content
     */
    private fun getImageMessageContent(imgUrl: String?, thumbnailUrl: String?, extra: String?): String {
        return "{\"img_url\":\"$imgUrl\",\"thumbnail_url\":\"$thumbnailUrl\",\"extra\":\"$extra\"}"
    }

    /**
     * list scroll to bottom
     */
    private fun listScrollToBottom() {
        if (mAdapter.itemCount > 0) {
            recyclerView.scrollToPosition(mAdapter.itemCount - 1)
        }
    }

    /**
     * hide emoJi
     */
    private fun hideEmoJi() {
        rlEmoJi?.visibility = View.GONE
    }

    /**
     * hide plugin
     */
    private fun hidePlugin() {
        rlPlugin?.visibility = View.GONE
    }

    /**
     * show soft input
     */
    private fun showSoftInput(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    /**
     * hide soft input
     */
    private fun hideSoftInput() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    /**
     * go back
     */
    private fun goBack() {
        finish()
        hideSoftInput()
    }

    /**
     * return emoJi view
     */
    private fun getEmoJiItemView(list: MutableList<EmoJi>): View {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_im_emoji, null)
        val gridView = view?.findViewById(R.id.fixedGridView) as FixedGridView
        val adapter = EmoJiAdapter(mContext)
        adapter.restList(list)
        gridView.adapter = adapter
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            try {
                val emoJi = adapter.getItem(position)
                if (null != emoJi) {
                    if (emoJi.type == "DEL") {
                        // delete
                        val index = etInputBox.selectionStart
                        if (index > 0) {
                            val text = etInputBox.text.substring(index - 1)
                            if (EmoJiFilter.containsEmoJi(text)) {
                                if (index >= 2) {
                                    etInputBox.text.delete(index - 2, index)
                                }
                            } else {
                                etInputBox.text.delete(index - 1, index)
                            }
                        }
                    } else {
//                        val string = String(Character.toChars(emoJi.code))
//                        val spannableString = SpannableString(string)
//                        val imageSpan = ImageSpan(this, emoJi.resId)
//                        spannableString.setSpan(imageSpan, 0, string.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//                        etInputBox.text.insert(etInputBox.selectionStart, spannableString)
                        etInputBox.text.insert(etInputBox.selectionStart, String(Character.toChars(emoJi.code)))
                    }
                }
            } catch (ex: Exception) {
            }
        }
        return view
    }

    /**
     * 获取当前token
     */
    fun getToken(): String {
        return mCurToken
    }

    /**
     * 获取当前聊天目标ID
     */
    fun getTargetUid(): String {
        return mTargetUid
    }

    /**
     * 获取当前聊天目标昵称
     */
    fun getTargetName(): String {
        return mTargetName
    }

    /**
     * 返回输入框的文本内容
     */
    fun getInputContent(): String {
        return etInputBox?.text.toString() + ""
    }

    /**
     * Click event
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivLeft -> {
                goBack()
            }
            R.id.tvSent -> {
                val content = etInputBox.text.toString()
                val flag = onSendTextMessage(content)
                if (!flag) {
                    if (TextUtils.isEmpty(content) || content.trim() == "") {
                        ToastUtil.showLong("聊天内容不能為空")
                        return
                    }
                    val message = ChatMessage()
                    message.messageDirection = "1"
                    message.sendTime = ""
                    message.sendTimeLong = System.currentTimeMillis()
                    message.messageContent = getTextMessageContent(content, "")
                    message.messageSentState = "0"
                    message.targetId = mTargetUid
                    message.messageType = Config.msgTypeTxt
                    mAdapter.add(message)
                    mAdapter.notifyDataSetChanged()
                    if (mAdapter.itemCount > 0) {
                        recyclerView.scrollToPosition(mAdapter.itemCount - 1)
                    }
                    etInputBox.setText("")
                    sendChatMessage(message)
                }
            }
            R.id.ivPlugin -> {
                if (rlPlugin.visibility == View.VISIBLE) {
                    rlPlugin.visibility = View.GONE
                } else {
                    hideEmoJi()
                    hideSoftInput()
                    rlPlugin.visibility = View.VISIBLE
                    listScrollToBottom()
                }
            }
            R.id.llAddPhoto -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !PermissionUtil.isHasPermission(this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    setPermissionReason("您需要先允許“相機”和“存儲裝置”的授權才能上傳圖片文件")
                    requestPermission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else {
                    selectPhoto()
                }
            }
            R.id.ivEmoJi -> {
                if (rlEmoJi.visibility == View.VISIBLE) {
                    rlEmoJi.visibility = View.GONE
                } else {
                    hidePlugin()
                    hideSoftInput()
                    rlEmoJi.visibility = View.VISIBLE
                    listScrollToBottom()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestCodeSelectPhoto) {
                if (null != data) {
                    val list = Matisse.obtainPathResult(data)
                    if (null != list && list.size > 0) {
                        if (!onSendImageMessage(list)) {
                            for (index in 0 until list.size) {
                                compress(list[index])
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Back button listener
     */
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                goBack()
            }
            return true
        }
        return super.dispatchKeyEvent(event)
    }

}