package com.addcn.im.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;


import com.addcn.im.R;
import com.addcn.im.util.CalendarUtils;
import com.addcn.im.util.GlideUtil;
import com.addcn.im.view.MyTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Author:YangBin
 * ChatList Adapter
 */
public class ImListAdapter extends BaseQuickAdapter<HashMap<String, String>, BaseViewHolder> implements LoadMoreModule {
    protected Context mContext;

    public ImListAdapter(int layoutResId, List<HashMap<String, String>> datas) {
        super(layoutResId, datas);

    }

    public ImListAdapter(int layoutResId, List<HashMap<String, String>> data, Context context) {
        super(layoutResId, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, HashMap<String, String> map) {

        MyTextView number = helper.getView(R.id.tv_content_number);
        number.setText(map.get("new_msg_count"));
        if (map.get("new_msg_count").equals("0")) {
            number.setVisibility(View.GONE);
        } else {
            number.setVisibility(View.VISIBLE);
        }
        helper.setText(R.id.tv_name, map.get("nickname"));
        try {


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            Date date1 = calendar.getTime();
            String nowDate = sdf.format(date1);
            String subYear1 = nowDate.substring(0, 4);
            String subMonth1 = nowDate.substring(5, 7);
            String subDay1 = nowDate.substring(8, 10);



            String sendTimeDate =  map.get("last_time")  ;
            String subYear2 = sendTimeDate.substring(0, 4);
            String subMonth2 = sendTimeDate.substring(5, 7);
            String subDay2 = sendTimeDate.substring(8, 10);
            String subTime2 = sendTimeDate.substring(11,16);


            Date date = sdf2.parse(map.get("last_time"));
            //日期转时间戳（毫秒）
            long time = date.getTime();


            //如果是今天发的就只显示时间
            if (subYear1.equals(subYear2) && subMonth1.equals(subMonth2) && subDay1.equals(subDay2)) {
                helper.setText(R.id.tv_time, subTime2);
            } else if (isThisWeek(time)) {//如果是本周发的 就显示是周几
                String weekday = CalendarUtils.getWhatDay(time);//得到今天是周几
                helper.setText(R.id.tv_time, weekday);
            } else {
                helper.setText(R.id.tv_time, map.get("last_time"));
            }
        } catch (Exception e) {

        }


        ImageView iv_avatar = helper.getView(R.id.iv_avatar);//图片
        String avatar = map.get("avatar");//
        if ( isNotNull(avatar)) {
            try {
                GlideUtil.getInstance().loadCircleImage(mContext.getApplicationContext(), avatar, R.drawable.ic_msg_avatar, iv_avatar);
            } catch (Throwable t) {
            }
        } else {
            iv_avatar.setImageResource(R.drawable.no_photo_80x60);
        }

        String type = map.get("type");
        if (type.equals("Msg:Txt")) {
            helper.setText(R.id.tv_content, map.get("last_content"));
        } else if (type.equals("Msg:Img")) {
            helper.setText(R.id.tv_content, "[圖片]");
        }else if(type.equals("Msg:Customize")){

            helper.setText(R.id.tv_content, "物件:"+map.get("title"));
        }


    }


    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (paramWeek == currentWeek) {
            return true;
        }
        return false;
    }



    /**
     * 方法描述：是否不为空<br>
     */
    public static boolean isNotNull(String text) {
        return (!TextUtils.isEmpty(text) && !text.equals("null") && !text
                .equals(""));
    }



    //回调接口
    private interface ItemOnClickInterface {
        void onItemClick(View view, int position);
    }





}