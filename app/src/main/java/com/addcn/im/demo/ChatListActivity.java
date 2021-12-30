package com.addcn.im.demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.addcn.im.activity.BaseChatListActivity;


/**
 * Author:YangBin
 * chatList activity
 */
public class ChatListActivity extends BaseChatListActivity {
    private String setDeleteIm ="https://im.debug.591.com.hk/messages/delLiaisonPerson";
    private String setListData ="https://im.debug.591.com.hk/chat/users?token=";

    @Override
    protected void setAdapterItemClick(String target_uid, String target_name, String token, int position) {

        Toast.makeText(ChatListActivity.this, "点击item是：" + position, Toast.LENGTH_LONG).show();

        Bundle bundle = new Bundle();
        bundle.putString("target_uid", target_uid);
        bundle.putString("target_name", target_name);
        bundle.putString("tag_detail", "0");
        bundle.putString("token", token);
        Intent intent = new Intent();
        intent.setClass(ChatListActivity.this, ChatActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    /**
     * 长按删除的url传入进去
     * @return
     */
    @Override
    protected String initSetDeleteIm() {
        return setDeleteIm;
    }

    @Override
    protected String initListData() {
        return setListData;
    }
}
