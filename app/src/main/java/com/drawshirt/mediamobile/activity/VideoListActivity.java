package com.drawshirt.mediamobile.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.adapter.ChannelOtherAdapter;
import com.drawshirt.mediamobile.bean.BaiDuChannelVideo;
import com.drawshirt.mediamobile.bean.ChannelOtherVideos;
import com.drawshirt.mediamobile.bean.Media;
import com.drawshirt.mediamobile.bean.MediaItem;
import com.drawshirt.mediamobile.bean.PlayHistoryInfo;
import com.drawshirt.mediamobile.common.BaseActivity;
import com.drawshirt.mediamobile.dao.PlayHistoryDao;
import com.drawshirt.mediamobile.utils.UIUtils;
import com.drawshirt.mediamobile.utils.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;

import static com.drawshirt.mediamobile.R.id.progressLinear;
import static com.drawshirt.mediamobile.utils.Utils.isGetData;

public class VideoListActivity extends BaseActivity {


    private static final int VISIBLE_THRESHOLD = 2;
    @Bind(R.id.btn_logo)
    ImageButton mBtnLogo;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.btn_search)
    ImageButton mBtnSearch;
    @Bind(R.id.channel_video_new)
    TextView mChannelVideoNew;
    @Bind(R.id.channel_video_hot)
    TextView mChannelVideoHot;
    @Bind(R.id.rv_video_list)
    RecyclerView mRvVideoList;
    @Bind(R.id.horizontalSc)
    RelativeLayout mHorizontalSc;
    @Bind(R.id.progressbar_loading)
    ProgressBar mProgressbarLoading;
    @Bind(R.id.load_more_textview)
    TextView mLoadMoreTextview;
    @Bind(progressLinear)
    LinearLayout mProgressLinear;
    private BaiDuChannelVideo channelVideo;
    private String tag;
    private ArrayList<ChannelOtherVideos> arrayList;
    private ArrayList<ChannelOtherVideos> arrayListHot;
    private ChannelOtherAdapter adapter;
    private boolean isHostData = false;
    private int indexHost = 1;
    private int indexNew = 1;
    private PlayHistoryDao dao = null;
    private PlayHistoryInfo mPlayHistory;

    @Override
    protected String getUrl() {
        String url = null;
        Intent i = getIntent();
        if (i.getSerializableExtra("channelVideoInfo") != null) {
            try {
                channelVideo = (BaiDuChannelVideo) i.getSerializableExtra("channelVideoInfo");
                if (channelVideo != null) {
                    String base_url = channelVideo.getBase_url() + "1.js";
                    tag = channelVideo.getTag();
                    mTvTitle.setText(channelVideo.getName() + "");
                    if (base_url != null && !base_url.equals("") && !base_url.equals("null") && UIUtils.hasNetwork()) {
                        url = base_url;
                    }

                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
        return url;
    }

    @Override
    protected RequestParams getParams() {
        return null;
    }

    @Override
    protected void initData(String content) {
        dao = new PlayHistoryDao(this);
        arrayList = new ArrayList<ChannelOtherVideos>();
        arrayListHot = new ArrayList<ChannelOtherVideos>();
        ArrayList<ChannelOtherVideos> object = parserChannelView(content);
        if (object != null) {
            arrayList = object;
            refleshView(arrayList);
            Utils.closeWaitingDialog();
        }
        mProgressbarLoading.setVisibility(View.GONE);
        mProgressLinear.setVisibility(View.GONE);
        mLoadMoreTextview.setVisibility(View.GONE);


        mChannelVideoNew.setOnClickListener(myOnClick);
        mChannelVideoHot.setOnClickListener(myOnClick);

        mRvVideoList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

                int totalItemCount = layoutManager.getItemCount();

                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                //比如一共15个Item，当前到达第13个， Constant.VISIBLE_THRESHOLD设为2
                //总数小于最后一个+阈值，就加载更多数据，同时把loading标记为 true 。
                if (!isGetData && totalItemCount < (lastVisibleItem + VISIBLE_THRESHOLD)) {
                    try {
                        if (isHostData) {
                            if (arrayListHot != null && arrayListHot.get(0).getVideo_num() != null) {
                                int count = Integer.parseInt(arrayListHot.get(0).getVideo_num());
                                if (count == totalItemCount) {
                                    mProgressbarLoading.setVisibility(View.GONE);
                                    mLoadMoreTextview.setText("全部加载完了");
                                    mLoadMoreTextview.setVisibility(View.VISIBLE);
                                    mProgressLinear.setVisibility(View.VISIBLE);
                                } else {
                                    indexHost = indexHost + 1;
                                    mProgressbarLoading.setVisibility(View.VISIBLE);
                                    mProgressLinear.setVisibility(View.VISIBLE);
                                    mLoadMoreTextview.setText("正在加载数据");
                                    mLoadMoreTextview.setVisibility(View.VISIBLE);
                                    String hotUrl = channelVideo.getBase_url() + "hot/" + indexHost + ".js";

                                    if (hotUrl != null && !hotUrl.equals("") && !hotUrl.equals("null") && UIUtils.hasNetwork()) {
                                        client.get(hotUrl, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(String content) {
                                                super.onSuccess(content);
                                                ArrayList<ChannelOtherVideos> object = parserChannelView(content);
                                                if (object != null) {
                                                    adapter.addData(object);
                                                    adapter.notifyDataSetChanged();
                                                }
                                                mProgressbarLoading.setVisibility(View.GONE);
                                                mProgressLinear.setVisibility(View.GONE);
                                                mLoadMoreTextview.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onFailure(Throwable error, String content) {
                                                super.onFailure(error, content);
                                                UIUtils.toast("获取网络数据失败！", false);
                                            }
                                        });

                                    }
                                }
                            }
                        } else {
                            if (arrayList != null && arrayList.get(0).getVideo_num() != null) {
                                int count = Integer.parseInt(arrayList.get(0).getVideo_num());
                                if (count == totalItemCount) {
                                    mProgressbarLoading.setVisibility(View.GONE);
                                    mLoadMoreTextview.setText("全部加载完了");
                                    mLoadMoreTextview.setVisibility(View.VISIBLE);
                                    mProgressLinear.setVisibility(View.VISIBLE);
                                } else {
                                    indexNew = indexNew + 1;
                                    mProgressbarLoading.setVisibility(View.VISIBLE);
                                    mProgressLinear.setVisibility(View.VISIBLE);
                                    mLoadMoreTextview.setText("正在加载数据");
                                    mLoadMoreTextview.setVisibility(View.VISIBLE);
                                    String base_url = channelVideo.getBase_url() + indexNew + ".js";
                                    if (base_url != null && !base_url.equals("") && !base_url.equals("null") && UIUtils.hasNetwork()) {
                                        client.get(base_url, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(String content) {
                                                super.onSuccess(content);
                                                ArrayList<ChannelOtherVideos> object = parserChannelView(content);
                                                if (object != null) {
                                                    adapter.addData(object);
                                                    adapter.notifyDataSetChanged();
                                                }
                                                mProgressbarLoading.setVisibility(View.GONE);
                                                mProgressLinear.setVisibility(View.GONE);
                                                mLoadMoreTextview.setVisibility(View.GONE);

                                            }

                                            @Override
                                            public void onFailure(Throwable error, String content) {
                                                super.onFailure(error, content);
                                                UIUtils.toast("获取网络数据失败！", false);
                                            }
                                        });
                                    }
                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        adapter.setOnItemClickListener(new ChannelOtherAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (UIUtils.hasNetwork()) {
                    try {

                        ChannelOtherVideos video=null;
                        if (isHostData) {

                            if (arrayListHot != null && arrayListHot.size() > position) {
                                video = arrayListHot.get(position);
                            }
                        } else {

                            if (arrayList != null && arrayList.size() > position) {
                                video = arrayList.get(position);
                            }

                        }

                        if (video != null) {
                            mPlayHistory = new PlayHistoryInfo();
                            mPlayHistory.setTitle(video.getTitle());
                            mPlayHistory.setImage_url(video.getImgv_url());
                            mPlayHistory.setSource_url(video.getUrl());
                            mPlayHistory.setPlay_time(System.currentTimeMillis());

                            if(dao!=null ) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        dao.insert(mPlayHistory);
                                    }
                                }.start();
                            }
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(video.getUrl()));
                            VideoListActivity.this.startActivity(intent);
                        }

                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                } else {
                    UIUtils.toast(VideoListActivity.this.getText(R.string.tip_network).toString(), false);
                }
            }
        });


        mBtnLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCurrent();
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_list;
    }

    private Media setMedia(ChannelOtherVideos video) {
        Media mMedia = new Media();
        ArrayList<MediaItem> mMediaItemArrayList = new ArrayList<MediaItem>();
        MediaItem mMediaItem = new MediaItem();
        mMediaItem.setLive(false);
        mMediaItem.setTitle(video.getTitle());
        mMediaItem.setSourceUrl(video.getUrl());
        mMediaItem.setImage(video.getImgh_url());
        mMediaItemArrayList.add(mMediaItem);
        mMedia.setMediaItemArrayList(mMediaItemArrayList);
        mMedia.setPosition(0);
        mMedia.setMediaType(channelVideo.getTag());
        return mMedia;
    }

    private void refleshView(ArrayList<ChannelOtherVideos> array) {
        if (adapter == null) {
            adapter = new ChannelOtherAdapter(array);
            mRvVideoList.setLayoutManager(new GridLayoutManager(this, 2));
            mRvVideoList.setAdapter(adapter);
        } else {
            adapter.setData(array);
            adapter.notifyDataSetChanged();
        }
    }


    View.OnClickListener myOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_logo:
                    removeCurrent();
                    break;
                case R.id.channel_video_hot://最热
                    try {
                        isHostData = true;
                        mChannelVideoHot.setBackgroundResource(R.drawable.ad_title_right_new_press);
                        mChannelVideoNew.setBackgroundResource(R.drawable.ad_title_left_new);
                        if (arrayListHot == null || (arrayListHot != null && arrayListHot.size() == 0)) {
                            if (channelVideo != null) {
                                String hotUrl = channelVideo.getBase_url() + "hot/1.js";

                                if (hotUrl != null && !hotUrl.equals("") && !hotUrl.equals("null") && UIUtils.hasNetwork()) {
                                    Utils.startWaitingDialog(VideoListActivity.this);

                                    client.get(hotUrl, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(String content) {
                                            super.onSuccess(content);
                                            ArrayList<ChannelOtherVideos> object = parserChannelView(content);
                                            if (object != null) {
                                                arrayListHot = object;
                                                refleshView(arrayListHot);
                                                Utils.closeWaitingDialog();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Throwable error, String content) {
                                            super.onFailure(error, content);
                                            UIUtils.toast("获取网络数据失败！", false);
                                        }
                                    });

                                }

                            }
                        } else {
                            refleshView(arrayListHot);
                        }

                    } catch (Exception e) {
                        // TODO: handle exception
                    }


                    break;
                case R.id.channel_video_new://最新
                    try {
                        isHostData = false;
                        mChannelVideoHot.setBackgroundResource(R.drawable.ad_title_right_new);
                        mChannelVideoNew.setBackgroundResource(R.drawable.ad_title_left_new_press);
                        if (arrayList == null || (arrayList != null && arrayList.size() == 0)) {
                            if (channelVideo != null) {
                                String base_url = channelVideo.getBase_url() + "1.js";

                                if (base_url != null && !base_url.equals("") && !base_url.equals("null") && UIUtils.hasNetwork()) {
                                    Utils.startWaitingDialog(VideoListActivity.this);
                                    arrayList = new ArrayList<ChannelOtherVideos>();
                                    client.get(base_url, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(String content) {
                                            super.onSuccess(content);
                                            ArrayList<ChannelOtherVideos> object = parserChannelView(content);
                                            if (object != null) {
                                                arrayList = object;
                                                refleshView(arrayList);
                                                Utils.closeWaitingDialog();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Throwable error, String content) {
                                            super.onFailure(error, content);
                                            UIUtils.toast("获取网络数据失败！", false);
                                        }
                                    });
                                }

                            }
                        } else {
                            refleshView(arrayList);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                    break;
                default:
                    break;
            }
        }
    };

    private ArrayList<ChannelOtherVideos> parserChannelView(String js) {
        ArrayList<ChannelOtherVideos> arr = new ArrayList<ChannelOtherVideos>();

        try {

            String key = null;
            if (js != null) {
                String s = js;
                if (s.contains(":{")) {
                    int index = s.indexOf(":{");
                    key = s.substring(2, index - 1);
                }
            }
            if (key != null) {
                JSONObject json = new JSONObject(js);
                JSONObject channel_info_object = null;
                ;
                channel_info_object = json.optJSONObject(key);//生活

                JSONArray videosArr = channel_info_object.optJSONArray("videos");
                for (int i = 0; i < videosArr.length(); i++) {
                    JSONObject jsonO = videosArr.optJSONObject(i);
                    ChannelOtherVideos video = new ChannelOtherVideos();
                    video.setDomain(jsonO.optString("domain"));
                    video.setDuration(jsonO.optString("duration"));
                    video.setImgh_url(jsonO.optString("imgh_url"));
                    video.setImgv_url(jsonO.optString("imgv_url"));
                    video.setIs_play(jsonO.optString("is_play"));
                    video.setTitle(jsonO.optString("title"));
                    video.setUrl(jsonO.optString("url"));

                    video.setVideo_num(channel_info_object.optString("video_num"));
                    video.setEnd(channel_info_object.optString("end"));
                    video.setBeg(channel_info_object.optString("beg"));


                    arr.add(video);
                }
            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return arr;
    }

}
