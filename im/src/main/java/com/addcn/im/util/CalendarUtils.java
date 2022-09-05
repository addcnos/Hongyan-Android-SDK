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
package com.addcn.im.util;

import java.util.Calendar;

/**
 * Author: WangYongQi
 */

public final class CalendarUtils {

    /**
     * 将时间戳转换成当天零点的时间戳
     *
     * @param milliseconds
     * @return
     */
    private static Calendar zeroFromHour(long milliseconds) {
        Calendar calendar = Calendar.getInstance(); // 获得一个日历

        calendar.setTimeInMillis(completMilliseconds(milliseconds));
        zeroFromHour(calendar);
        return calendar;
    }
    /**
     * 将时，分，秒，以及毫秒值设置为0
     *
     * @param calendar
     */
    private static void zeroFromHour(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
    /**
     * 由于服务器返回的是10位，手机端使用需要补全3位
     *
     * @param milliseconds
     * @return
     */
    private static long completMilliseconds(long milliseconds) {
        String milStr = Long.toString(milliseconds);
        if (milStr.length() == 10) {
            milliseconds = milliseconds * 1000;
        }
        return milliseconds;
    }


    /**
     * 最终调用方法
     * @param timeStamp
     * @return
     */

    public static String getWhatDay (long timeStamp) {
        Calendar cal = CalendarUtils.zeroFromHour(timeStamp);
        String whatDay="";
        if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
            whatDay="星期六";
        }
        if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
            whatDay="星期日";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
            whatDay = "星期一";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY){
            whatDay = "星期二";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY){
            whatDay = "星期三";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY){
            whatDay = "星期四";
        }
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
            whatDay = "星期五";
        }
        return whatDay;
    }
}
