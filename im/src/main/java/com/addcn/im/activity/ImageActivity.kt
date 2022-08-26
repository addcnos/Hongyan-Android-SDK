/**
 * Copyright (c) 2019 addcn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.addcn.im.activity

import android.os.Bundle
import android.widget.ImageView
import com.addcn.im.R
import com.addcn.im.util.GlideUtil
import com.github.chrisbanes.photoview.PhotoView
import com.wyq.fast.activity.FastBaseAppCompatActivity
import com.wyq.fast.utils.ObjectValueUtil

/**
 * Author:WangYongQi
 */

class ImageActivity : FastBaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_im_image)
        findViewById<ImageView>(R.id.iv_left).setOnClickListener {
            finish()
        }
        val ivFull = findViewById<PhotoView>(R.id.iv_full)
        val imgUrl = ObjectValueUtil.getString(intent.extras, "img_url")
        GlideUtil.getInstance().loadImage(imgUrl, ivFull)
    }

}
