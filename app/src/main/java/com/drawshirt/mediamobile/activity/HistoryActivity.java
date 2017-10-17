package com.drawshirt.mediamobile.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.adapter.PlayHistoryAdapter;
import com.drawshirt.mediamobile.bean.PlayHistoryInfo;
import com.drawshirt.mediamobile.common.BaseActivity;
import com.drawshirt.mediamobile.dao.PlayHistoryDao;
import com.loopj.android.http.RequestParams;

import java.util.List;

import butterknife.Bind;

public class HistoryActivity extends BaseActivity {

    @Bind(R.id.btn_back)
    ImageButton mBtnBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.tv_record_clear)
    TextView mTvRecordClear;
    @Bind(R.id.rv_record)
    RecyclerView mRvRecord;
    @Bind(R.id.ll_no_record)
    LinearLayout mLlNoRecord;

    private PlayHistoryDao mPlayHistoryDao;
    private List<PlayHistoryInfo> mHistoryList;
    private PlayHistoryAdapter mHistoryListAdapter;
    private AlertDialog mCleanDataDialog;

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
        mTvTitle.setText("播放历史");

        mPlayHistoryDao = new PlayHistoryDao(this);

        initPlayHistoryData();
        initCleanDataDialog();

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCurrent();
            }
        });

        mTvRecordClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPlayHistory()) {
                    mCleanDataDialog.show();
                }
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_record;
    }

    private void initCleanDataDialog() {
        AlertDialog.Builder customBuilder = new AlertDialog.Builder(HistoryActivity.this);
        customBuilder
                .setTitle(R.string.tip)
                .setMessage(R.string.deleteallhistory)
                .setPositiveButton(R.string.queren,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mPlayHistoryDao.deleteAllData();
                                mHistoryList.clear();
                                mHistoryListAdapter.notifyDataSetChanged();
                                mLlNoRecord.setVisibility(View.VISIBLE);
                                dialog.dismiss();
                                //								setRightButtonHideOrShow(false);
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });

        mCleanDataDialog = customBuilder.create();
    }

    private void initPlayHistoryData() {
        try {
            mPlayHistoryDao.autoDelete(50);
            mHistoryList = mPlayHistoryDao.findByOrder("desc");
            if (hasPlayHistory()) {
                mLlNoRecord.setVisibility(View.GONE);
                mHistoryListAdapter = new PlayHistoryAdapter(mHistoryList);
                mRvRecord.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
                mRvRecord.setAdapter(mHistoryListAdapter);
            } else {
                //				setRightButtonHideOrShow(false);
                mLlNoRecord.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean hasPlayHistory() {
        return mHistoryList != null && mHistoryList.size() > 0;
    }

    @Override
    protected void onDestroy() {
        if (mPlayHistoryDao != null) {
            mPlayHistoryDao = null;
        }

        super.onDestroy();
    }
}
