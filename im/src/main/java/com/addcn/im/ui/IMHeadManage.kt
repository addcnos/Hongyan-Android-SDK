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
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.addcn.im.R

/**
 * Author: YangBin
 * Head Layout
 */

class IMHeadManage {

    private var mActivity: Activity? = null
    //  Head layout
    var ryHeadLayout: RelativeLayout? = null
    // back
    var ivBack: ImageView? = null
    // Right button
    var ivRight: ImageView? = null
    // title
    var tvTitle: TextView? = null

    constructor(activity: Activity) {
        mActivity = activity
        initView()
    }

    /**
     * initView
     */
    private fun initView() {
        ryHeadLayout = mActivity?.findViewById<View>(R.id.ry_im_head) as RelativeLayout
        ivBack = mActivity?.findViewById<View>(R.id.iv_im_back) as ImageView
        ivRight = mActivity?.findViewById<View>(R.id.iv_im_right) as ImageView
        tvTitle = mActivity?.findViewById<View>(R.id.iv_im_title) as TextView
    }

    /**
     * setHeadLayoutColor
     */
    fun setBackgroundColor(backgroundColor: Int) {
        ryHeadLayout?.setBackgroundColor(backgroundColor)
    }

    /**
     * set HeadTitle
     */
    fun setTitle(title: String?) {
        if (title != null) {
            tvTitle?.visibility = View.INVISIBLE
            tvTitle?.text = title
        }
    }

    /**
     * set HeadTitle bgcolor
     */
    fun setTitleBgColor(backgroundColor: Int) {
        tvTitle?.setBackgroundColor(backgroundColor)
    }

    /**
     * set HeadTitle  textview size
     */
    fun setTitleSize(unit: Int ,size: Float) {
        tvTitle?.setTextSize(unit,size)
    }



    /**
     * setRightImagview show
     */
    fun setShowRightImg(resId1: Int) {
        ivRight?.visibility = View.VISIBLE
        ivRight?.setImageResource(resId1)
    }

    /**
     *   setRightImagview show
     */
    fun setHideRightImgShow() {
        ivRight?.visibility = View.VISIBLE
    }

    /**
     *  setRightImagview gone
     */
    fun setHideRightImgGone() {
        ivRight?.visibility = View.GONE
    }
}
