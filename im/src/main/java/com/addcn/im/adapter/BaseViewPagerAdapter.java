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
package com.addcn.im.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.PagerAdapter;

/**
 * Author: WangYongQi
 */

public class BaseViewPagerAdapter extends PagerAdapter {

    private List<View> mList;

    public BaseViewPagerAdapter() {
        this.mList = new ArrayList<>();
    }

    public void setList(List<View> list) {
        this.mList = list;
    }

    public void addList(List<View> list) {
        if (null == mList) {
            mList = new ArrayList<>();
        }
        if (!mList.containsAll(list)) {
            mList.addAll(list);
        }
    }

    public void addView(View view) {
        if (null != mList && null != view) {
            mList.add(view);
        }
    }

    public void addView(View view, int position) {
        if (null != mList && null != view) {
            mList.add(position, view);
        }
    }

    public void removeView(View view) {
        if (null != mList) {
            mList.remove(view);
        }
    }

    public void removeView(int position) {
        if (null != mList && mList.size() > position) {
            mList.remove(position);
        }
    }

    public void clearList() {
        if (null != mList) {
            mList.clear();
        }
    }

    @Override
    public int getCount() {
        if (null != mList) {
            return mList.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (null != mList && mList.size() > position) {
            View v = mList.get(position);
            if (v != null) {
                ViewGroup parent = (ViewGroup) v.getParent();
                if (parent != null) {
                    parent.removeAllViews();
                }
                container.addView(v);
                return v;
            }
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mList != null && mList.size() > position) {
            View v = mList.get(position);
            if (v != null) {
                container.removeView(v);
            }
        }
    }

}
