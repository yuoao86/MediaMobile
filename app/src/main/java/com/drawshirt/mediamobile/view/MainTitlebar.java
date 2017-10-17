package com.drawshirt.mediamobile.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.activity.SearchActivity;
import com.drawshirt.mediamobile.utils.UIUtils;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class MainTitlebar extends LinearLayout implements View.OnClickListener {
    private final Context context;
    private ImageView mIvLogo;
    private TextView mTvTitle;
    private ImageView mIvTitleSearch;

    public MainTitlebar(Context context) {
        this(context,null);
    }

    public MainTitlebar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MainTitlebar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View localView = LayoutInflater.from(context).inflate(R.layout.title_bar, this);
        this.context = context;
        mIvLogo = (ImageView) localView.findViewById(R.id.iv_logo);
        mTvTitle = (TextView) localView.findViewById(R.id.tv_title);
        mIvTitleSearch = (ImageView) localView.findViewById(R.id.iv_title_search);

        mIvLogo.setOnClickListener(this);
        mIvTitleSearch.setOnClickListener(this);
    }

    public void show(String paramString)
    {
        this.mTvTitle.setText(paramString);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_logo:
                UIUtils.toast("Logo click!",false);
                break;
            case R.id.iv_title_search:
                Intent localIntent = new Intent(context, SearchActivity.class);
                context.startActivity(localIntent);
                break;
        }
    }
}
