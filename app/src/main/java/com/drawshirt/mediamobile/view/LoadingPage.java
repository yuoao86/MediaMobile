package com.drawshirt.mediamobile.view;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.utils.UIUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public abstract class LoadingPage extends FrameLayout {
    private static final int STATE_LOADING = 1;
    private static final int STATE_ERROR = 2;
    private static final int STATE_EMPTY = 3;
    private static final int STATE_SUCCESS = 4;
    private final Context context;

    private int current_state = STATE_LOADING;

    private View view_loading;
    private View view_error;
    private View view_empty;
    private View view_success;
    private LayoutParams layoutParams;


    public LoadingPage(@NonNull Context context) {
        this(context, null);
    }

    public LoadingPage(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingPage(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (view_loading == null) {
            view_loading = UIUtils.getView(R.layout.page_loading);
            addView(view_loading, layoutParams);
        }

        if (view_error == null) {
            view_error = UIUtils.getView(R.layout.page_error);
            addView(view_error, layoutParams);
        }

        if (view_empty == null) {
            view_empty = UIUtils.getView(R.layout.page_empty);
            addView(view_empty, layoutParams);
        }

        showSafePage();
    }

    private void showSafePage() {
        UIUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showPage();
            }
        });
    }

    private void showPage() {
        view_loading.setVisibility(current_state == STATE_LOADING ? VISIBLE : INVISIBLE);
        view_empty.setVisibility(current_state == STATE_EMPTY ? VISIBLE : INVISIBLE);
        view_error.setVisibility(current_state == STATE_ERROR ? VISIBLE : INVISIBLE);

        if (view_success==null) {
            view_success = View.inflate(context, layoutId(), null);
            addView(view_success, layoutParams);

        }
        view_success.setVisibility(current_state == STATE_SUCCESS ? VISIBLE : INVISIBLE);



    }

    public abstract int layoutId();

    ResultState resultState;

    public void show() {

        if (TextUtils.isEmpty(url())){
            resultState= ResultState.SUCCESS;
            resultState.setContent("");
            showImage();
            return;
        }

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url(), params(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                if (!TextUtils.isEmpty(content)){
                    resultState= ResultState.SUCCESS;
                    resultState.setContent(content);
                }else{
                    resultState= ResultState.EMPTY;
                    resultState.setContent("");
                }
                showImage();
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                resultState= ResultState.ERROR;
                resultState.setContent("");

                showImage();
            }
        });
    }

    private void showImage() {
        switch (resultState){
            case ERROR:
                current_state=STATE_ERROR;
                break;
            case EMPTY:
                current_state=STATE_EMPTY;
                break;
            case SUCCESS:
                current_state=STATE_SUCCESS;
                break;
        }

        showSafePage();

        if (current_state==STATE_SUCCESS){
            onSuccessed(resultState,view_success);
        }
    }

    protected abstract void onSuccessed(ResultState resultState,View view_success);

    public abstract RequestParams params();

    public abstract String url();


    public enum ResultState {
        ERROR(2), EMPTY(3), SUCCESS(4);

        int state;

        ResultState(int state) {
            this.state = state;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        private String content;

    }

}
