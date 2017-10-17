package com.drawshirt.mediamobile.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.drawshirt.mediamobile.R;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {

    private Handler handler = new Handler();
    private RelativeLayout rl_splash_bg;

    //所需要申请的权限数组
    private static final String[] permissionsArray = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE
    };
    //还需申请的权限列表
    private List<String> permissionsList = new ArrayList<String>();
    //申请权限后的返回码
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private int permissionCount=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && SplashActivity.this.checkSelfPermission(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//            SplashActivity.this.requestPermissions(new String[]{
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_PHONE_STATE
//            }, 1);
//
//        }
        checkRequiredPermission(SplashActivity.this);


    }

    private void checkRequiredPermission(final Activity activity){
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }else{
                permissionCount++;
                if(permissionCount>=permissionsArray.length){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //两秒后才执行到这里
                            //执行在主线程中
                            startMainActivity();
                        }
                    }, 2000);

                    return;
                }
            }
        }
        ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSIONS);
    }


    private boolean isStartMain = false;
    /**
     * 跳转到主页面，并且把当前页面关闭掉
     */
    private void startMainActivity() {
        if (!isStartMain) {
            isStartMain = true;

            AlphaAnimation aa = new AlphaAnimation(1, 0);
            aa.setDuration(500);
            aa.setInterpolator(new AccelerateInterpolator());
            aa.setFillAfter(true);
            rl_splash_bg = (RelativeLayout) findViewById(R.id.rl_splash_bg);
            aa.setAnimationListener(new MyAnimationListener());
            rl_splash_bg.startAnimation(aa);

        }

    }

    private class MyAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);

            startActivity(intent);
            SplashActivity.this.overridePendingTransition(0, 0);
            //关闭当前页面
            finish();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    //用户授权数，当permissionCount==permissionsList.size()时，进入主界面
    //private int permissionCount=0;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int i=0; i<permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //Toast.makeText(SplashActivity.this, "亲亲，要同意所有限权，软件才能正常运行哦！", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //两秒后才执行到这里
                        //执行在主线程中
                        startMainActivity();
                    }
                }, 2000);

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        startMainActivity();
//        return super.onTouchEvent(event);
//    }

    @Override
    protected void onDestroy() {
        //把所有的消息和回调移除
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

//    @Override
//    public void onBackPressed() {
//        AppConnect.getInstance(this).close();
//        super.onBackPressed();
//    }
}
