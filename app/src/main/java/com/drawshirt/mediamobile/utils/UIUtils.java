package com.drawshirt.mediamobile.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.view.View;
import android.widget.Toast;

import com.drawshirt.mediamobile.common.MyApplication;

import java.util.Formatter;
import java.util.Locale;

import static com.drawshirt.mediamobile.common.MyApplication.context;


/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class UIUtils {

    public static final int Get_Slices = 10131;
    public static final int GEG_MEDIA_PLAY_URL = 1020;
    public static final int BaiDuInfoFlag = 1018;
    public static final int BaiDuRecommend = 1019;
    public static final int Channel_Video_Info = 1021;
    public static final int Channel_Search = 1022;//检索接口
    public static final int Channel_Search_Video = 1023;
    public static final int Channel_ScrollStateChanged = 1024;
    public static final int Channel_Video_View = 1025;
    public static final int Channel_Video_View_hot = 1026;
    public static final int Research_Activity = 1027;
    public static final int BaoFeng_Detail = 1028;
    public static final int BaoFeng_Play = 1029;




    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {

        return MyApplication.handler;
    }

    //返回指定color对应的颜色值
    public static int getColor(int colorId) {
        int color = getContext().getResources().getColor(colorId);
        return color;
    }

    //加载指定viewId视图并返回
    public static View getView(int viewId) {
        View view = View.inflate(getContext(), viewId, null);
        return view;
    }


    public static String[] getStringArr(int arrId) {
        String[] stringArray = getContext().getResources().getStringArray(arrId);

        return stringArray;
    }


    public static int dp2px(int dp) {
        //获取手机密度
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (density * dp + 0.5);//实现四舍五入
    }

    public static int px2dp(int px) {
        //获取手机密度
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5);//实现四舍五入
    }

    public static void runOnUiThread(Runnable runnable) {
        if (isInMainThread()) {
            runnable.run();
        } else {
            getHandler().post(runnable);
        }
    }

    public static boolean isInMainThread() {
        int currentThreadId = Process.myTid();
        return currentThreadId == MyApplication.mainThreadId;
    }

    public static boolean hasNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            return true;
        }
        return false;
    }

    public static void toast(String message,boolean isLengthLong){
        Toast.makeText(UIUtils.getContext(), message,isLengthLong? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    /**
     * 判断是否有存储卡，有返回TRUE，否则FALSE
     *
     * @return
     */
    public static boolean isSDcardExist() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 把毫秒转换成：1:20:30这里形式
     *
     * @param timeMs
     * @return
     */
    public static String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;

        int minutes = (totalSeconds / 60) % 60;

        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 判断是否是网络资源
     *
     * @param uri
     * @return
     */
    public static Boolean isNetUri(String uri) {
        Boolean result = false;
        if (uri != null) {
            if (uri.toLowerCase().startsWith("http") || uri.toLowerCase().startsWith("rtsp") || uri.toLowerCase().startsWith("mms")) {
                result = true;
            }
        }
        return result;
    }



}
