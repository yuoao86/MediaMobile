package com.drawshirt.mediamobile.common;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class MyApplication extends Application {
    //在整个应用执行中，需要提供的变量
    //应用中的上下文
    public static Context context;
    //应用中的Handler
    public static Handler handler;
    //提供主线程对象
    public static Thread mainThread;
    //提供主线程对象Id
    public static int mainThreadId;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        handler = new Handler();
        mainThread = Thread.currentThread();//实例化当前app的主线即为主线程
        mainThreadId = android.os.Process.myTid();//获取当前线程的Id

    }

}
