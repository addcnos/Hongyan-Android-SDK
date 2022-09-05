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
package com.addcn.im.ui

import android.app.Activity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.addcn.im.R

/**
 * Author: YangBin
 * Edit box
 */

class IMEditText {
    var mActivity: Activity? = null
    // Head layout
    var lyHeadLayout: LinearLayout? = null
    // Search box
    var etSearch: EditText? = null
    // Select expression
    var ivSelectEmo: ImageView? = null
    // select  pic
    var ivSelectPic: ImageView? = null
    //     SendMessage
    var btSendMessage: Button? = null


    constructor(activity: Activity) {
        mActivity = activity
        initView()
    }

    /**
     * Initialize the header layout view
     */
    private fun initView() {
        etSearch = mActivity?.findViewById<View>(R.id.et_content) as EditText
        ivSelectEmo = mActivity?.findViewById<View>(R.id.iv_im_select_emo) as ImageView
        ivSelectPic = mActivity?.findViewById<View>(R.id.iv_im_select_pic) as ImageView
        lyHeadLayout = mActivity?.findViewById<View>(R.id.ly_head_layout) as LinearLayout
        btSendMessage = mActivity?.findViewById<View>(R.id.bt_im_send_message) as Button


    }


    //setSelectPicShow
    fun setSelectPicShow() {
        ivSelectPic?.visibility = View.VISIBLE
    }

    // setSelectPicGone
    fun setSelectPicGone() {
        ivSelectPic?.visibility = View.GONE
    }

    //setSelectPicShow
    fun setSelectEmojShow() {
        ivSelectEmo?.visibility = View.VISIBLE
    }

    // setSelectPicGone
    fun setSelectEmojGone() {
        ivSelectEmo?.visibility = View.GONE
    }


    //setButtonSendShow
    fun setButtonSendShow() {
        btSendMessage?.visibility = View.VISIBLE
    }

    // setButtonSendGone
    fun setButtonSendGone() {
        btSendMessage?.visibility = View.GONE
    }


    /**
     * setButtonSendColor
     */
    fun setButtonSendColor(backgroundColor: Int) {
        btSendMessage?.setBackgroundColor(backgroundColor)
    }

    /**
     * setButtonSendTitle
     */
    fun setButtonSendTitle(title: String?) {
        if (title != null) {
            btSendMessage?.visibility = View.INVISIBLE
            btSendMessage?.text = title
        }
    }

    /**
     * set setButtonSendSize  textview size
     */
    fun setButtonSendSize(unit: Int, size: Float) {
        btSendMessage?.setTextSize(unit, size)
    }


}
