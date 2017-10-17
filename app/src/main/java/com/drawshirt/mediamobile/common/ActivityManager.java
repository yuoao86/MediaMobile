package com.drawshirt.mediamobile.common;

import android.app.Activity;

import java.util.Stack;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class ActivityManager {

    private ActivityManager() {

    }

    private static ActivityManager mManager = new ActivityManager();

    public static ActivityManager getInstance() {
        return mManager;
    }

    private Stack<Activity> mStack = new Stack<>();

    //添加一个activity
    public void addActivity(Activity activity) {
        if (activity != null) {
            mStack.add(activity);
        }

    }

    //删除指定的activity
    public void remove(Activity activity) {
        if (activity != null) {
            for (int i = mStack.size() - 1; i >= 0; i--) {
                Activity currentActivity = mStack.get(i);
                if (currentActivity.getClass().equals(activity.getClass())) {
                    activity.finish();
                    mStack.remove(i);

                }
            }
        }
    }

    //删除当前的activity
    public void removeCurrent() {
        Activity activity = mStack.lastElement();
        activity.finish();
        mStack.remove(activity);
    }

    //删除所有activity
    public void removeAll() {
        for (int i = mStack.size() - 1; i >= 0; i--) {
            Activity currentActivity = mStack.get(i);
            currentActivity.finish();
            mStack.remove(i);

        }

    }

    public int size() {
        return mStack.size();

    }
}
