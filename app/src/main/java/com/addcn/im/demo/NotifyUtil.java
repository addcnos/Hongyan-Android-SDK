/**
 * Copyright (c) 2019 addcn
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.addcn.im.demo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.Random;

import androidx.core.app.NotificationCompat;

/**
 * Author:WangYongQi
 */
public class NotifyUtil {

    /**
     * 通知聊天消息
     *
     * @param title
     * @param message
     * @param extras
     */
    public static void notifyChatMessage(String title, String message, Bundle extras) {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(BaseApplication.Companion.getApplication(), "chat");
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setLargeIcon(BitmapFactory.decodeResource(BaseApplication.Companion.getApplication().getResources(), R.mipmap.ic_launcher));
            builder.setColor(Color.argb(255, 255, 128, 0));
            builder.setAutoCancel(true);
            if (TextUtils.isEmpty(title)) {
                builder.setContentTitle("遊客");
            } else {
                builder.setContentTitle(title);
            }

            builder.setContentText("" + message);
            Intent intent = new Intent(BaseApplication.Companion.getApplication(), ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            if (extras != null) {
                intent.putExtras(extras);
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(BaseApplication.Companion.getApplication(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            builder.setContentIntent(pendingIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setGroupSummary(false).setGroup("group");
            }
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            // 遵從系統的設置，如系統完全靜音，軟件靜音
            AudioManager volMgr = (AudioManager) BaseApplication.Companion.getApplication().getSystemService(Context.AUDIO_SERVICE);
            switch (volMgr.getRingerMode()) {// 获取系统设置的铃声模式
                case AudioManager.RINGER_MODE_SILENT:// 静音模式，值为0，这时候不震动，不响铃
                    notification.sound = null;
                    notification.vibrate = null;
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:// 震动模式，值为1，这时候震动，不响铃
                    notification.sound = null;
                    notification.defaults |= Notification.DEFAULT_VIBRATE;
                    break;
                case AudioManager.RINGER_MODE_NORMAL:// 常规模式，值为2，分两种情况：1_响铃但不震动，2_响铃+震动
                    notification.defaults |= Notification.DEFAULT_VIBRATE;
                    notification.defaults |= Notification.DEFAULT_SOUND;
                    break;
                default:
                    break;
            }
            // 通知ID
            final int notifyId = new Random().nextInt(1000);
            NotificationManager notificationManager = (NotificationManager) BaseApplication.Companion.getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notifyId, notification);
        } catch (Exception ex) {
        }
    }

}
