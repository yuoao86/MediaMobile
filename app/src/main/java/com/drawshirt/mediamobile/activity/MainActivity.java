package com.drawshirt.mediamobile.activity;

import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.common.BaseActivity;
import com.drawshirt.mediamobile.fragment.ChannelFragment;
import com.drawshirt.mediamobile.fragment.HomeFragment;
import com.drawshirt.mediamobile.fragment.LocalFragment3;
import com.drawshirt.mediamobile.fragment.MeFragment;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.waps.AppConnect;

public class MainActivity extends BaseActivity {


    @Bind(R.id.fl_main)
    FrameLayout mFlMain;
    @Bind(R.id.rb_main_home)
    RadioButton mRbMainHome;
    @Bind(R.id.rb_main_channel)
    RadioButton mRbMainChannel;
    @Bind(R.id.rb_main_local)
    RadioButton mRbMainLocal;
    @Bind(R.id.rb_main_me)
    RadioButton mRbMainMe;
    @Bind(R.id.rg_main_bottom)
    RadioGroup mRgMainBottom;
    private List<Fragment> fragments = new ArrayList<>();
    private int position = 0;
    private Fragment currentFragment;


    @Override
    protected String getUrl() {
        return null;
    }

    @Override
    protected RequestParams getParams() {
        return null;
    }

    @Override
    protected void initData(String content) {

        AppConnect.getInstance("03dae6f7698e2cbb6438692a7f0764cd", "360", this);



        initFragment();
        //侦听radiobutton的切换
        mRgMainBottom.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.rb_main_home:
                        position = 0;
                        break;
                    case R.id.rb_main_channel:
                        position = 1;
                        break;
                    case R.id.rb_main_local:
                        position = 2;
                        break;
                    case R.id.rb_main_me:
                        position = 3;
                        break;

                }
                switchFragment(currentFragment, getFragment(position));
            }
        });


        //默认显示首页
        mRgMainBottom.check(R.id.rb_main_home);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    private void initFragment() {
        fragments.add(new HomeFragment());
        fragments.add(new ChannelFragment());
        fragments.add(new LocalFragment3());
        fragments.add(new MeFragment());
    }


    //根据position获取fragment
    private Fragment getFragment(int position) {
        if (fragments != null && fragments.size() > 0) {
            return fragments.get(position);
        }
        return null;
    }


    private void switchFragment(Fragment fromFragment, Fragment nextFragment) {
        if (currentFragment != nextFragment) {
            currentFragment = nextFragment;
            if (nextFragment != null) {
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                if (!nextFragment.isAdded()) {
                    transaction.add(R.id.fl_main, nextFragment);
                    if (fromFragment != null) {
                        transaction.hide(fromFragment);
                    }
                } else {
                    transaction.show(nextFragment);
                    if (fromFragment != null) {
                        transaction.hide(fromFragment);
                    }
                }
                transaction.commit();
            }
        }


    }

    @Override
    protected void onDestroy() {
        AppConnect.getInstance(this).close();
        super.onDestroy();
    }

    /**
     * 是否已经退出
     */
    private boolean isExit = false;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode ==KeyEvent.KEYCODE_BACK){
            if(position != 0){//不是第一页面
                position = 0;
                mRgMainBottom.check(R.id.rb_main_home);//首页
                return true;
            }else  if(!isExit){
                isExit = true;
                Toast.makeText(MainActivity.this,"再按一次推出",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit  = false;
                    }
                },2000);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
