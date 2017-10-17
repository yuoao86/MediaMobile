package com.drawshirt.mediamobile.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drawshirt.mediamobile.view.LoadingPage;
import com.loopj.android.http.RequestParams;

import butterknife.ButterKnife;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public abstract class BaseFragment extends Fragment {

    private LoadingPage loadingPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        loadingPage = new LoadingPage(container.getContext()) {


            @Override
            public int layoutId() {
                return getLayoutId();
            }

            @Override
            protected void onSuccessed(ResultState resultState,View view_success) {
                ButterKnife.bind(BaseFragment.this, view_success);
                initTitle();
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

        return loadingPage;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadingPage.show();
    }

    protected abstract String getUrl();

    protected abstract RequestParams getParams();

    public abstract void initTitle();

    public abstract void initData(String content);

    //public abstract String getData();


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public abstract int getLayoutId();
}
