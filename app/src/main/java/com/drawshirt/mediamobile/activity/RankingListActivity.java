package com.drawshirt.mediamobile.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.adapter.RankListAdapter;
import com.drawshirt.mediamobile.bean.BaiDuRecommend;
import com.drawshirt.mediamobile.common.BaseActivity;
import com.drawshirt.mediamobile.utils.UIUtils;
import com.drawshirt.mediamobile.utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;

import static com.drawshirt.mediamobile.R.id.btn_search;
import static com.drawshirt.mediamobile.R.id.progressLinear;

public class RankingListActivity extends BaseActivity {


    private static final int VISIBLE_THRESHOLD = 2;
    @Bind(R.id.btn_logo)
    ImageButton mBtnLogo;
    @Bind(btn_search)
    ImageButton mBtnSearch;
    @Bind(R.id.rank_tv)
    TextView mRankTv;
    @Bind(R.id.rank_movie)
    TextView mRankMovie;
    @Bind(R.id.rank_tvshow)
    TextView mRankTvshow;
    @Bind(R.id.rank_comic)
    TextView mRankComic;
    @Bind(R.id.listview)
    RecyclerView mListview;
    @Bind(R.id.horizontalSc)
    RelativeLayout mHorizontalSc;
    @Bind(R.id.progressbar_loading)
    ProgressBar mProgressbarLoading;
    @Bind(R.id.load_more_textview)
    TextView mLoadMoreTextview;
    @Bind(progressLinear)
    LinearLayout mProgressLinear;
    private String req;
    private ArrayList<BaiDuRecommend> arrRankListMovie, arrRankListTV, arrRankListTVShow, arrRankListComic;
    private RankListAdapter myRankListAdapter;
    private int pageMovie = 1, pageTV = 1, pageTVShow = 1, pageComic = 1;

    private boolean isGetData = false;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArrayList<BaiDuRecommend> baiDuRecommends = parserRankList((String) msg.obj);
            switch (req) {
                case "movie":
                    arrRankListMovie.addAll(baiDuRecommends);
                    refleshView(arrRankListMovie);
                    break;
                case "tvplay":
                    arrRankListTV.addAll(baiDuRecommends);
                    refleshView(arrRankListTV);
                    break;
                case "tvshow":
                    arrRankListTVShow.addAll(baiDuRecommends);
                    refleshView(arrRankListTVShow);
                    break;
                case "comic":
                    arrRankListComic.addAll(baiDuRecommends);
                    refleshView(arrRankListComic);
                    break;
            }
            isGetData = false;
            Utils.closeWaitingDialog();
        }
    };
    private boolean isScrollUpdate=false;

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
        arrRankListMovie = new ArrayList<>();
        arrRankListTV = new ArrayList<>();
        arrRankListTVShow = new ArrayList<>();
        arrRankListComic = new ArrayList<>();
        setTitleButton(0);


        mListview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                int totalItemCount = layoutManager.getItemCount();

                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                //比如一共15个Item，当前到达第13个， Constant.VISIBLE_THRESHOLD设为2
                //总数小于最后一个+阈值，就加载更多数据，同时把loading标记为 true 。
                if (!isGetData && totalItemCount < (lastVisibleItem + VISIBLE_THRESHOLD)) {

                    isGetData = true;
                    try {
                        mProgressbarLoading.setVisibility(View.VISIBLE);
                        mProgressLinear.setVisibility(View.VISIBLE);
                        mLoadMoreTextview.setVisibility(View.VISIBLE);
                        mLoadMoreTextview.setText("正在加载数据");

                        if(req.equals("movie")){
                            pageMovie++;
                            setRequestData(pageMovie);
                        }else if(req.equals("tvplay")){
                            pageTV ++;
                            setRequestData(pageTV);
                        }else if(req.equals("tvshow")){
                            pageTVShow++;
                            setRequestData(pageTVShow);
                        }else if(req.equals("comic")){
                            pageComic++;
                            setRequestData(pageComic);
                        }
                        isScrollUpdate=true;
                        //new NetWorkTask().execute(ChannelListActivity.this,UIUtils.Channel_ScrollStateChanged,
                        //path,channelInfo,mainHandler);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        });

        mRankMovie.setOnClickListener(myOnClick);
        mRankTv.setOnClickListener(myOnClick);
        mRankTvshow.setOnClickListener(myOnClick);
        mRankComic.setOnClickListener(myOnClick);
        mBtnLogo.setOnClickListener(myOnClick);
        mBtnSearch.setOnClickListener(myOnClick);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_ranking_list;
    }

    View.OnClickListener myOnClick = new View.OnClickListener() {

        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_logo:
                    removeCurrent();
                    break;
                case R.id.rank_movie:
                    setTitleButton(1);
                    break;
                case R.id.rank_tv:
                    setTitleButton(0);
                    break;
                case R.id.rank_tvshow:
                    setTitleButton(2);
                    break;
                case R.id.rank_comic:
                    setTitleButton(3);
                    break;

                default:
                    break;
            }
        }
    };

    private void setTitleButton(int i) {
        mRankMovie.setBackgroundResource(R.color.common_transparent_color);
        mRankTv.setBackgroundResource(R.color.common_transparent_color);
        mRankTvshow.setBackgroundResource(R.color.common_transparent_color);
        mRankComic.setBackgroundResource(R.color.common_transparent_color);

        switch (i) {
            case 0:
                req = "tvplay";
                pageTV = 1;

                mRankTv.setBackgroundResource(R.drawable.filter_text_bg_selected);

                if (arrRankListTV.size() > 0) {
                    refleshView(arrRankListTV);
                } else {
                    setRequestData(pageTV);
                }
                break;
            case 1:
                req = "movie";
                pageMovie = 1;

                mRankMovie.setBackgroundResource(R.drawable.filter_text_bg_selected);


                if (arrRankListMovie.size() > 0) {
                    refleshView(arrRankListMovie);
                } else {
                    setRequestData(pageMovie);
                }

                break;
            case 2:
                req = "tvshow";
                pageTVShow = 1;

                mRankTvshow.setBackgroundResource(R.drawable.filter_text_bg_selected);

                if (arrRankListTVShow.size() > 0) {
                    refleshView(arrRankListTVShow);
                } else {
                    setRequestData(pageTVShow);
                }
                break;
            case 3:
                req = "comic";
                pageComic = 1;

                mRankComic.setBackgroundResource(R.drawable.filter_text_bg_selected);
                if (arrRankListComic.size() > 0) {
                    refleshView(arrRankListComic);
                } else {
                    setRequestData(pageComic);
                }

                break;
            default:
                break;
        }

    }

    private void refleshView(ArrayList<BaiDuRecommend> items) {
        if (myRankListAdapter == null) {
            myRankListAdapter = new RankListAdapter(items);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RankingListActivity.this, LinearLayoutManager.VERTICAL, false);
            mListview.setLayoutManager(linearLayoutManager);
            mListview.setAdapter(myRankListAdapter);
        } else {
            myRankListAdapter.setData(items);
            myRankListAdapter.notifyDataSetChanged();
        }

        mProgressbarLoading.setVisibility(View.GONE);
        mProgressLinear.setVisibility(View.GONE);
        mLoadMoreTextview.setVisibility(View.GONE);
        mLoadMoreTextview.setText(" ");

    }

    private void setRequestData(int page) {
        try {
            String base_url = getUrl(req, page);
            if (base_url != null && !base_url.equals("") && !base_url.equals("null") && UIUtils.hasNetwork()) {
                if (!isScrollUpdate){
                    isScrollUpdate=false;
                    Utils.startWaitingDialog(RankingListActivity.this);
                }

                client.get(base_url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(String content) {
                        super.onSuccess(content);
                        Message message = mHandler.obtainMessage();
                        //message.what=req;
                        //Log.d("TAG","content==="+content);
                        message.obj = content;
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                        UIUtils.toast("获取网络数据失败！", false);
                    }
                });
                //                new NetWorkTask().execute(RankingListActivity.this,UIUtils.BaiDuInfoFlag,
                //                        base_url,arrRankList );
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private String getUrl(String req, int page) {
        String url = "http://app.video.baidu.com/adnativelist/?req=" + req + "&page=" + page;
        return url;
    }

    private ArrayList<BaiDuRecommend> parserRankList(String js) {
        ArrayList<BaiDuRecommend> templist = new ArrayList<>();
        JSONArray jsonArr;
        try {
            jsonArr = new JSONArray(js);
            if (jsonArr != null) {
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObject = jsonArr.optJSONObject(i);
                    BaiDuRecommend baiduRe = new BaiDuRecommend();
                    baiduRe.setTitle(jsonObject.optString("title"));
                    baiduRe.setImg_url(jsonObject.optString("imgv_url"));
                    baiduRe.setRating(jsonObject.optString("rating"));
                    baiduRe.setTerminal_type(jsonObject.optString("terminal_type"));
                    baiduRe.setWorks_id(jsonObject.optString("works_id"));
                    baiduRe.setWorks_type(jsonObject.optString("works_type"));
                    baiduRe.setActor(jsonObject.optString("actor"));
                    baiduRe.setUpdate(jsonObject.optString("update"));
                    baiduRe.setType(jsonObject.optString("type"));
                    baiduRe.setStatus_day(jsonObject.optString("status_day"));
                    templist.add(baiduRe);
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return templist;
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
