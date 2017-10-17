package com.drawshirt.mediamobile.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.drawshirt.mediamobile.view.LoadingPage;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import butterknife.ButterKnife;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public abstract class BaseActivity extends FragmentActivity {

    private LoadingPage loadingPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().addActivity(this);

        loadingPage=new LoadingPage(this) {
            @Override
            public int layoutId() {
                return getLayoutId();
            }

            @Override
            protected void onSuccessed(ResultState resultState, View view_success) {

                initData(resultState.getContent());
            }

            @Override
            public RequestParams params() {
                return getParams();
            }

            @Override
            public String url() {
                return getUrl();
            }
        };

        setContentView(loadingPage);
        ButterKnife.bind(this);
        loadingPage.show();
    }

    public AsyncHttpClient client =new AsyncHttpClient();

    protected abstract String getUrl();

    protected abstract RequestParams getParams();

    protected abstract void initData(String content);

    public abstract int getLayoutId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void goToActivity(Class activity, Bundle bundle){
        Intent intent = new Intent(this, activity);
        if (bundle!=null&&bundle.size()!=0){
            intent.putExtra("data",bundle);
        }
        this.startActivity(intent);
    }

    public void removeAll(){
        ActivityManager.getInstance().removeAll();
    }

    public void removeCurrent(){
        ActivityManager.getInstance().removeCurrent();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        removeCurrent();
    }
}
