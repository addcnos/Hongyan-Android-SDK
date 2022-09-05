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
package com.addcn.im.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Author:WangYongQi
 * main activity of this sample
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnChatA.setOnClickListener(this)
        btnChatB.setOnClickListener(this)
        btnChatC.setOnClickListener(this)
        btnChatList.setOnClickListener {
            // 跳转聊天列表
            val intent = Intent(this@MainActivity, ChatListActivity::class.java)
            val bundle = Bundle()
            bundle.putString("token", "example")
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // 进行连接，参数false代表不用重复连接
        IMConnect.connect(false)
    }

    override fun onClick(v: View?) {
        if (null != v) {
            val intent = Intent(this@MainActivity, ChatActivity::class.java)
            val bundle = Bundle()
            // 跳转聊天界面
            when (v.id) {
                R.id.btnChatA -> {
                    // 这里传自己的IM - token
                    bundle.putString("token", "example")
                    // 这里传对方的聊天ID，你要跟谁聊天，就传谁的ID
                    bundle.putString("target_uid", "example")
                    // 这里传对方的昵称，你要跟谁聊天，就传谁的昵称
                    bundle.putString("target_name", "A用户")
                }
                R.id.btnChatB -> {
                    bundle.putString("token", "example")
                    bundle.putString("target_uid", "example")
                    bundle.putString("target_name", "B用户")
                }
                R.id.btnChatC -> {
                    bundle.putString("token", "example")
                    bundle.putString("target_uid", "example")
                    bundle.putString("target_name", "C用户")
                }
            }
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

}
