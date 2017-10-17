package com.drawshirt.mediamobile.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.bean.BaiDuMediaInfo;
import com.drawshirt.mediamobile.bean.BaiDuRecommend;
import com.drawshirt.mediamobile.bean.Media;
import com.drawshirt.mediamobile.bean.MediaItem;
import com.drawshirt.mediamobile.bean.PlayHistoryInfo;
import com.drawshirt.mediamobile.bean.Sites;
import com.drawshirt.mediamobile.common.AppNetConfig;
import com.drawshirt.mediamobile.common.BaseActivity;
import com.drawshirt.mediamobile.dao.PlayHistoryDao;
import com.drawshirt.mediamobile.utils.UIUtils;
import com.drawshirt.mediamobile.view.MainListview;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;


public class MediaShowActivity extends BaseActivity {


    @Bind(R.id.btn_back)
    ImageButton mBtnBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.about_video_image)
    ImageView mAboutVideoImage;
    @Bind(R.id.lin)
    RelativeLayout mLin;
    @Bind(R.id.director)
    TextView mDirector;
    @Bind(R.id.craw)
    TextView mCraw;
    @Bind(R.id.area)
    TextView mArea;
    @Bind(R.id.videoPlayButton_history_text)
    TextView mVideoPlayButtonHistoryText;
    @Bind(R.id.videoPlayButton)
    ImageView mVideoPlayButton;
    @Bind(R.id.changePlayView)
    LinearLayout mChangePlayView;
    @Bind(R.id.describeTextview)
    TextView mDescribeTextview;
    @Bind(R.id.playListText)
    TextView mPlayListText;
    @Bind(R.id.linear)
    LinearLayout mLinear;
    @Bind(R.id.changeView)
    LinearLayout mChangeView;
    @Bind(R.id.layout_tv)
    LinearLayout mLayoutTv;
    private Intent mIntent;
    private BaiDuRecommend video;
    private Media mMedia;
    private String mMediaName;
    private Sites movieSites;
    private ArrayList<MediaItem> arrVideo;
    private String videoMovieUrl;
    private BaiDuMediaInfo baiduMediaInfo;
    private PlayHistoryDao dao = null;
    private PlayHistoryInfo mPlayHistory=null;
    private MediaItem mVideoInfo;


    @Override
    protected String getUrl() {
        String url;
        mIntent = getIntent();
        if (mIntent != null) {
            video = (BaiDuRecommend) mIntent.getSerializableExtra("BaiDuRecommend");
            url = AppNetConfig.XiangQingInfo + "?worktype=adnative" + video.getWorks_type() + "&id=" + video.getWorks_id() + "&site=";//voide.getUrl();

        } else {
            url = null;
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

        Media object = parserVideoData(content);

        if (object != null) {
            try {
                mMedia = object;

                if (mMedia != null) {
                    mMediaName = mMedia.getTitle();
                    mMedia.setMediaType(video.getWorks_type());
                    mTvTitle.setText("" + mMediaName);

                    if (mMedia.getDirectorArr() != null && mMedia.getDirectorArr().size() > 0) {
                        String s = "";
                        for (int i = 0; i < mMedia.getDirectorArr().size(); i++) {
                            String director = mMedia.getDirectorArr().get(i);
                            s += director + "  ";
                        }
                        mDirector.setText("导演：" + s + "");
                    }
                    if (mMedia.getActorArr() != null && mMedia.getActorArr().size() > 0) {
                        String s = "";
                        for (int i = 0; i < mMedia.getActorArr().size(); i++) {
                            String actor = mMedia.getActorArr().get(i);
                            s += actor + "   ";
                        }
                        mCraw.setText("演员：" + s + "");
                    }
                    mDescribeTextview.setText("简介");
                    mPlayListText.setText("播放列表");
                    changeView(false);

                    if (mMedia.getAreaArr() != null && mMedia.getAreaArr().size() > 0) {
                        String s = "";
                        for (int i = 0; i < mMedia.getAreaArr().size(); i++) {
                            String actor = mMedia.getAreaArr().get(i);
                            s += actor + "   ";
                        }
                        mArea.setText("地区：" + s + "");
                    }

                    if (mMedia.getImg_url() != null && !mMedia.getImg_url().equals("")) {
                        mLin.setBackgroundResource(R.drawable.video_pic_bg);
                        Glide.with(this).load(mMedia.getImg_url())
                                .placeholder(R.drawable.bg_list_default)
                                .error(R.drawable.bg_list_default)
                                .into(mAboutVideoImage);
                    }

                    if (mMedia.getSitesArr() != null && mMedia.getSitesArr().size() > 0) {
                        movieSites = mMedia.getSitesArr().get(0);
                        mChangePlayView.setVisibility(View.VISIBLE);
                        mVideoPlayButton.setVisibility(View.VISIBLE);
                        mDescribeTextview.setVisibility(View.VISIBLE);
                        setChangePlayUrlButton(mMedia.getSitesArr());

                    } else {
                        //再次请求
                        String getPlayUrl = AppNetConfig.XiangQingSingle + "?worktype=adnative" + video.getWorks_type()
                                + "&id=" + mMedia.getId() + "&site=";

                        getDataFromNet(getPlayUrl);
                    }

                }

                mLinear.setVisibility(View.VISIBLE);
            } catch (Exception e) {
            }
        }
        mVideoPlayButton.setOnClickListener(myOnClick);
        mBtnBack.setOnClickListener(myOnClick);
        mDescribeTextview.setOnClickListener(myOnClick);
        mPlayListText.setOnClickListener(myOnClick);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_media_show;
    }


    View.OnClickListener myOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_back:
                    removeCurrent();
                    break;
                case R.id.videoPlayButton:
                    if (mMedia != null) {
                        mPlayHistory=new PlayHistoryInfo();

                        if (video.getWorks_type().equals("movie")) {
                            if (mMedia.getSitesArr() != null && mMedia.getSitesArr().size() > 0) {

                                Sites vid = mMedia.getSitesArr().get(0);
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(vid.getSite_url()));
                                mPlayHistory.setTitle(mMedia.getTitle());
                                mPlayHistory.setImage_url(mMedia.getImg_url());
                                mPlayHistory.setSource_url(vid.getSite_url());
                                mPlayHistory.setPlay_time(System.currentTimeMillis());

                                if(dao!=null ) {
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            dao.insert(mPlayHistory);
                                        }
                                    }.start();
                                }

                                startActivity(intent);

                            } else {
                                UIUtils.toast("抱谦！该视频已经不存在了！", false);
                            }


                        } else {
                            if (baiduMediaInfo != null) {
                                if (baiduMediaInfo.getVideosArray() != null && baiduMediaInfo.getVideosArray().size() > 0) {
                                    String sourceUrl = baiduMediaInfo.getVideosArray().get(0).getSourceUrl();
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(sourceUrl));

                                    mPlayHistory.setTitle(mMedia.getTitle());
                                    mPlayHistory.setImage_url(mMedia.getImg_url());
                                    mPlayHistory.setSource_url(sourceUrl);
                                    mPlayHistory.setPlay_time(System.currentTimeMillis());

                                    if(dao!=null ) {
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                dao.insert(mPlayHistory);
                                            }
                                        }.start();
                                    }

                                    startActivity(intent);
                                }
                            }
                        }
                    }

                    break;
                case R.id.describeTextview:
                    changeView(true);
                    break;
                case R.id.playListText:
                    changeView(false);
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * false表示显示列表
     */
    private void changeView(boolean isDescribe) {
        mChangeView.removeAllViews();
        if (isDescribe) {//简介
            mDescribeTextview.setBackgroundResource(R.drawable.collect_edit_ico);
            mPlayListText.setBackgroundResource(R.drawable.collect_edit_press_ico);
            TextView textview = new TextView(MediaShowActivity.this);
            textview.setTextColor(Color.WHITE);
            if (mMedia != null)
                textview.setText(mMedia.getIntro() + "");
            mChangeView.addView(textview);
        } else {
            mDescribeTextview.setBackgroundResource(R.drawable.collect_edit_press_ico);
            mPlayListText.setBackgroundResource(R.drawable.collect_edit_ico);

            MainListview appPage = new MainListview(MediaShowActivity.this);
            EpisodeAdapter myAppAdapter = new EpisodeAdapter(arrVideo);
            appPage.setAdapter(myAppAdapter);
            appPage.setSelector(new ColorDrawable(Color.TRANSPARENT));
            mChangeView.addView(appPage);
        }
    }



    class EpisodeAdapter extends BaseAdapter {
        ArrayList<MediaItem> videosArr;
        private LayoutInflater mInflater;

        public EpisodeAdapter(ArrayList<MediaItem> episode) {
            this.mInflater = LayoutInflater.from(MediaShowActivity.this);
            this.videosArr = episode;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (videosArr != null && videosArr.size() > 0) {
                return videosArr.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (videosArr != null && videosArr.size() > 0) {
                return videosArr.get(position);
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v;
            if (convertView == null) {
                v = mInflater.inflate(R.layout.listview_button, null);
            } else {
                v = convertView;
            }
            try {
                if (videosArr != null && videosArr.size() > position) {
                    Button button = (Button) v.findViewById(R.id.button);
                    mVideoInfo = videosArr.get(position);

                    if (mMedia != null) {
                        button.setText("  " + mVideoInfo.getEpisode() + ":" + mVideoInfo.getTitle());
                    }

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                mMedia.setPosition(position);
                                setPlayerUrl();

                            } catch (Exception e) {
                            }

                        }
                    });
                }

            } catch (Exception e) {
            }
            return v;
        }
    }

    private void setChangePlayUrlButton(ArrayList<Sites> arrSites) {

        if (arrSites != null && arrSites.size() > 0) {
            mChangePlayView.removeAllViews();
            GridView appPage = new GridView(MediaShowActivity.this);
            appPage.setNumColumns(5);
            appPage.setHorizontalSpacing(5);
            appPage.setVerticalSpacing(5);
            ChangePlayAdapter myAppAdapter = new ChangePlayAdapter(arrSites);
            appPage.setAdapter(myAppAdapter);
            appPage.setSelector(new ColorDrawable(Color.TRANSPARENT));
            mChangePlayView.addView(appPage);
        }

    }

    private int changeButtonIndex = 0;

    class ChangePlayAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<Sites> mVideosPlay;

        //private ImageLoader changeImageLoader;
        ChangePlayAdapter(ArrayList<Sites> mVide) {
            this.mVideosPlay = mVide;
            this.mInflater = LayoutInflater.from(MediaShowActivity.this);
            //changeImageLoader = new ImageLoader(MediaShowActivity.this.getApplicationContext());

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mVideosPlay.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mVideosPlay.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v;
            if (convertView == null) {
                v = mInflater.inflate(R.layout.tvactivity_item, null);
            } else {
                v = convertView;
            }
            TextView txt = (TextView) v.findViewById(R.id.txt_id);
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView1);
            if (changeButtonIndex == position) {
                imageView.setBackgroundResource(R.drawable.bg_oriange_line_long);
            } else {
                imageView.setBackgroundResource(R.drawable.bg_tab_my);
            }
            ImageView VideoSource = (ImageView) v.findViewById(R.id.VideoSource);
            VideoSource.setVisibility(View.VISIBLE);

            if (mVideosPlay != null && mVideosPlay.size() > position) {

                final Sites vid = mVideosPlay.get(position);
                String name = vid.getSite_name();
                txt.setText(name + "");

                Glide.with(MediaShowActivity.this).load(vid.getSite_logo())
                        .placeholder(R.drawable.bg_list_default)
                        .into(VideoSource);
                //changeImageLoader.DisplayImage(vid.getSite_logo(), VideoSource);
                txt.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        changeButtonIndex = position;
                        notifyDataSetChanged();
                        if (video.getWorks_type().equals("movie")) {
                            if (mMedia.getSitesArr() != null && mMedia.getSitesArr().size() > 0) {
                                setMediaItemMovie(vid);
                            }
                        }
                        refleshPlaySite(vid.getSite_url());
                        //							imagePostion = 0;
                    }
                });

            }

            return v;
        }

    }


    private void setPlayerUrl() {
        try {
            if (mMedia != null) {
                //Log.e("TAG", "mMedia===" + mMedia.toString());
                mPlayHistory=new PlayHistoryInfo();
                ArrayList<MediaItem> mediaItemArrayList = mMedia.getMediaItemArrayList();
                MediaItem mediaItem = mediaItemArrayList.get(mMedia.getPosition());
                String sourceUrl = mediaItem.getSourceUrl();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(sourceUrl));
                int i = mMedia.getPosition() + 1;
                if (!TextUtils.isEmpty(mMedia.getTitle())){
                    mPlayHistory.setTitle(mMedia.getTitle());
                }else{
                    mPlayHistory.setTitle(video.getTitle());
                }

                mPlayHistory.setImage_url(mMedia.getImg_url());
                mPlayHistory.setSource_url(sourceUrl);
                if (video.getWorks_type().equals("tvshow")){
                    mPlayHistory.setPosition(i+"");
                    mPlayHistory.setPosition("  " + mVideoInfo.getEpisode() + ":" + mVideoInfo.getTitle());
                }else{
                    mPlayHistory.setPosition("第"+i+"集");
                }

                mPlayHistory.setPlay_time(System.currentTimeMillis());

                if(dao!=null ) {
                    new Thread() {
                        @Override
                        public void run() {
                            dao.insert(mPlayHistory);
                        }
                    }.start();
                }

                startActivity(intent);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 刷新界面，更换播放源地址
     */
    private void refleshPlaySite(String site) {
        //如果是电影就直接播放了
        if (video.getWorks_type() != null && video.getWorks_type().equals("movie") && movieSites != null) {
            videoMovieUrl = site;
        } else {
            //再次请求
            String getPlayUrl = AppNetConfig.XiangQingSingle + "?worktype=adnative" + video.getWorks_type()
                    + "&id=" + mMedia.getId() + "&site=" + site;

            getDataFromNet(getPlayUrl);
            // new NetWorkTask().execute(MediaShowActivity.this, UIUtils.GEG_MEDIA_PLAY_URL,
            //getPlayUrl,mainHandler);
        }

    }

    private void getDataFromNet(String url) {

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                //Log.d("TAG","content==="+content);
                baiduMediaInfo = parserBaiDuMediaInfo(content);

                if (baiduMediaInfo != null) {
                    try {
                        arrVideo = baiduMediaInfo.getVideosArray();
                        mMedia.setMediaItemArrayList(arrVideo);
                        if (arrVideo != null && arrVideo.size() >= 1) {
                            //默认写0；这里表示有很多家视频可以播放，等以后视频切换的时候在打开

                            mDescribeTextview.setVisibility(View.VISIBLE);

                            mVideoPlayButton.setVisibility(View.VISIBLE);
                            changeView(false);

                            if (baiduMediaInfo.getSitesArray() != null) {
                                setChangePlayUrlButton(baiduMediaInfo.getSitesArray());
                            }
                        } else {
                            mVideoPlayButton.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
            }
        });
    }


    private void setMediaItemMovie(Sites vids) {

        Sites vid = vids;
        ArrayList<MediaItem> mMediaItemArrayList = new ArrayList<MediaItem>();
        MediaItem mMediaItem = new MediaItem();
        mMediaItem.setTitle(mMedia.getTitle());
        mMediaItem.setSourceUrl(vid.getSite_url());
        mMediaItem.setImage(vid.getSite_logo());
        mMediaItemArrayList.add(mMediaItem);
        mMedia.setMediaItemArrayList(mMediaItemArrayList);
    }

    private BaiDuMediaInfo parserBaiDuMediaInfo(String json) {
        BaiDuMediaInfo media = new BaiDuMediaInfo();

        try {
            JSONObject jsonObject = new JSONObject(json);
            media.setId(jsonObject.optString("id"));
            media.setSite(jsonObject.optString("site"));
            media.setTotal_num(jsonObject.optInt("total_num"));
            if (jsonObject.has("sites")) {
                ArrayList<Sites> sitesArrayList = new ArrayList<Sites>();
                JSONArray sitesArr = jsonObject.optJSONArray("sites");
                for (int i = 0; i < sitesArr.length(); i++) {
                    Sites sites = new Sites();
                    JSONObject jso = sitesArr.optJSONObject(i);
                    sites.setSite_name(jso.optString("site_name"));
                    sites.setMax_episode(jso.optInt("max_episode"));
                    sites.setSite_logo(jso.getString("site_logo"));
                    sites.setSite_no(jso.optInt("site_no"));
                    sites.setSite_url(jso.optString("site_url"));
                    sitesArrayList.add(sites);
                }
                media.setSitesArray(sitesArrayList);
            }

            if (jsonObject.has("videos")) {
                ArrayList<MediaItem> videosArrayList = new ArrayList<MediaItem>();
                JSONArray videosArr = jsonObject.optJSONArray("videos");
                for (int i = 0; i < videosArr.length(); i++) {
                    MediaItem videos = new MediaItem();
                    JSONObject jso = videosArr.optJSONObject(i);
                    videos.setTitle(jso.optString("title"));
                    videos.setSourceUrl(jso.optString("url"));
                    videos.setIs_play(jso.optString("is_play"));
                    videos.setEpisode(jso.optString("episode"));
                    videos.setImage(jso.optString("img_url"));
                    //					videos.setTvid(jso.optString("tvid"));
                    //					videos.setDownload(jso.optString("download"));
                    //					videos.setSec(jso.optInt("sec"));
                    //					videos.setDi(jso.optString("di"));
                    videosArrayList.add(videos);
                }
                media.setVideosArray(videosArrayList);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return media;
    }

    private Media parserVideoData(String jso) {
        Media videoData = new Media();
        try {
            JSONObject jsonOb = new JSONObject(jso);
            videoData.setId(jsonOb.optString("id"));
            videoData.setTitle(jsonOb.optString("title"));
            videoData.setImg_url(jsonOb.optString("img_url"));
            videoData.setIntro(jsonOb.optString("intro"));
            videoData.setIs_finish(jsonOb.optString("is_finish"));
            videoData.setPubtime(jsonOb.optString("pubtime"));
            videoData.setCur_episode(jsonOb.optInt("cur_episode"));
            videoData.setMax_episode(jsonOb.optString("max_episode"));

            if (jsonOb.has("director")) {
                JSONArray jsonArray = jsonOb.optJSONArray("director");
                ArrayList<String> arr = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    arr.add(jsonArray.optString(i));
                }
                videoData.setDirectorArr(arr);
            }

            if (jsonOb.has("actor")) {
                JSONArray jsonArray = jsonOb.optJSONArray("actor");
                ArrayList<String> arr = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    arr.add(jsonArray.optString(i));
                }
                videoData.setActorArr(arr);
            }


            if (jsonOb.has("area")) {
                JSONArray jsonArray = jsonOb.optJSONArray("area");
                ArrayList<String> arr = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    arr.add(jsonArray.optString(i));
                }
                videoData.setAreaArr(arr);
            }
            videoData.setSeason_num(jsonOb.optInt("season_num"));

            if (jsonOb.has("type")) {
                JSONArray jsonArray = jsonOb.optJSONArray("type");
                ArrayList<String> arr = new ArrayList<String>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    arr.add(jsonArray.optString(i));
                }
                videoData.setTypeArr(arr);
            }
            videoData.setRating(jsonOb.optString("rating"));
            videoData.setPlay_filter(jsonOb.optString("play_filter"));
            videoData.setForeign_ip(jsonOb.optString("foreign_ip"));
            if (jsonOb.has("sites")) {
                JSONArray sitesJsonArr = jsonOb.optJSONArray("sites");
                ArrayList<Sites> sitesArr = new ArrayList<Sites>();
                for (int i = 0; i < sitesJsonArr.length(); i++) {
                    Sites sites = new Sites();
                    JSONObject jo = sitesJsonArr.optJSONObject(i);
                    //					sites.setMax_episode(jo.optInt("max_episode"));
                    sites.setSite_logo(jo.optString("site_logo"));
                    sites.setSite_name(jo.optString("site_name"));
                    //					sites.setSite_no(site_no)
                    sites.setSite_url(jo.optString("site_url"));
                    sitesArr.add(sites);
                }
                videoData.setSitesArr(sitesArr);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return videoData;
    }


}
