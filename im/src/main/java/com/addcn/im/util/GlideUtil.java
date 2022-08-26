/**
 * Copyright (c) 2019 addcn
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.addcn.im.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.addcn.im.GlideApp;
import com.addcn.im.app.IMApp;
import com.addcn.im.glide.GlideCircleTransform;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

/**
 * Author: WangYongQi
 */

public class GlideUtil {

    private static GlideUtil instance;

    public static GlideUtil getInstance() {
        if (instance == null) {
            instance = new GlideUtil();
        }
        return instance;
    }

    private GlideUtil() {
    }

    /**
     * 加载图片
     *
     * @param url
     * @param imageView
     */
    public void loadImage(String url, ImageView imageView) {
        if (imageView != null) {
            if (!TextUtils.isEmpty(url)) {
                // 加载图片
                GlideApp.with(IMApp.INSTANCE.getContext())
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            } else {
                // 暂无图片
            }
        }
    }

    /**
     * 加载列表图片
     *
     * @param url
     * @param imageView
     */
    public void loadListImage(String url, ImageView imageView) {
        if (imageView != null) {
            if (!TextUtils.isEmpty(url)) {
                // 加载图片
                GlideApp.with(IMApp.INSTANCE.getContext())
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            } else {
                // 暂无图片
            }
        }
    }

    /**
     * 加载圆形图片
     *
     * @param url
     * @param imageView
     * @param borderWidth
     * @param borderColor
     */
    public void loadRoundImage(String url, ImageView imageView, int borderWidth, int borderColor) {
        if (imageView != null) {
            if (!TextUtils.isEmpty(url)) {
                // 加载图片
                GlideApp.with(IMApp.INSTANCE.getContext())
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new GlideCircleTransform(borderWidth, borderColor))
                        .into(imageView);
            } else {
                // 暂无图片
            }
        }
    }

    public void loadCircleImage(Context context, String imgUrl, int loading, ImageView view) {
        if (view != null) {
            GlideApp.with(context)
                    .load(imgUrl)
                    .apply(RequestOptions
                            .bitmapTransform(new CircleCrop())
                            .placeholder(loading)
                            .error(loading))
                    .into(view);
        }
    }

}
