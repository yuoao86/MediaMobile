package com.drawshirt.mediamobile.utils;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */

import android.content.Context;

import com.drawshirt.mediamobile.R;

import java.text.SimpleDateFormat;

/**
 * 格式化工具
 * @author Wang Chenxi
 *
 */
public class FormatUtil {
    private static final String TAG = "FormatUtil";

    public static final class TimeFormat {
        public static final long SECOND = 1000;
        public static final long MINUTE = 60 * 1000;
        public static final long HOUR = 60 * MINUTE;
        public static final long DAY = 24 * HOUR;

        public static String absolute(Long time) {
            SimpleDateFormat df = new SimpleDateFormat("MM-dd");
            return df.format(time);
        }


        /**
         *
         * add by zhangshuo
         * 2013-2-22上午10:55:38
         * @param context
         * @param time
         * @return
         */
        public static String relative(Context context, Long time) {
            StringBuffer playHistoryBuffer = new StringBuffer();
            if (time < MINUTE) {
                playHistoryBuffer.append(time/SECOND);
                playHistoryBuffer.append( context.getString(R.string.secondbefore));
            } else if (time < HOUR) {
                playHistoryBuffer.append((time/MINUTE));
                playHistoryBuffer.append(context.getString(R.string.minutebefore));
            } else if (time < DAY) {
                playHistoryBuffer.append(time/HOUR);
                playHistoryBuffer.append(context.getString(R.string.hoursbefore));
            } else {
                LogUtil.e("暂只支持处理一天以内的相对时间！'time' =  "+ time);
                absolute(time);
            }
            return playHistoryBuffer.toString();
        }
    }
}