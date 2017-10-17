package com.drawshirt.mediamobile.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;

import static com.drawshirt.mediamobile.R.id.describeTextview;
import static com.drawshirt.mediamobile.R.id.playListText;

public class MediaActivity extends BaseActivity {

    private static final int APP_PAGE_SIZE = 20;
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
    @Bind(R.id.bagpanelpage)
    LinearLayout mBagpanelpage;
    @Bind(R.id.gridview)
    RelativeLayout mGridview;
    @Bind(R.id.describe)
    TextView mDescribe;
    @Bind(R.id.layout_tv)
    LinearLayout mLayoutTv;
    @Bind(R.id.changePlayView)
    LinearLayout mChangePlayView;
    @Bind(describeTextview)
    TextView mDescribeTextview;
    private Media mMedia;
    private BaiDuRecommend baiDuRecommend;
    private String mMediaName;
    private Sites movieSites;
    private BaiDuMediaInfo baiduMediaInfo;
    private ArrayList<MediaItem> arrVideo;
    private int PageCount;
    private PlayHistoryInfo mPlayHistory;
    private PlayHistoryDao dao;

    @Override
    protected String getUrl() {
        Intent intent = getIntent();
        String url;
        if (intent != null) {
            baiDuRecommend = (BaiDuRecommend) intent.getSerializableExtra("BaiDuRecommend");
            url = AppNetConfig.XiangQingInfo + "?worktype=adnative" + baiDuRecommend.getWorks_type() + "&id=" + baiDuRecommend.getWorks_id() + "&site=";//voide.getUrl();
        } else {
            url = null;
            Log.e("TAG", "没有intent数据");
        }

        return url;
    }


    @Override
    protected RequestParams getParams() {
        return null;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_media;
    }

    @Override
    protected void initData(String content) {
        dao = new PlayHistoryDao(this);

        Media object = parserVideoData(content);

        if (object != null) {
            try {
                mMedia = object;

                if (mMedia != null) {
                    mMedia.setMediaType(baiDuRecommend.getWorks_type());
                    mMediaName = mMedia.getTitle();

                    mTvTitle.setText("" + mMediaName);

                    if (mMedia.getDirectorArr() != null) {
                        String s = "";
                        for (int i = 0; i < mMedia.getDirectorArr().size(); i++) {
                            String director = mMedia.getDirectorArr().get(i);
                            s += director + "  ";
                        }
                        mDirector.setText("导演：" + s + "");
                    }
                    if (mMedia.getActorArr() != null) {
                        String s = "";
                        for (int i = 0; i < mMedia.getActorArr().size(); i++) {
                            String actor = mMedia.getActorArr().get(i);
                            s += actor + "   ";
                        }
                        mCraw.setText("演员：" + s + "");
                    }


                    if (mMedia.getIntro() != null && !mMedia.getIntro().equals("") && !mMedia.getIntro().equals("null")) {
                        mDescribeTextview.setBackgroundResource(R.drawable.bg_video_detail_website);
                        mDescribeTextview.setText("简介");
                        mDescribe.setText(mMedia.getIntro() + "");
                    }


                    if (mMedia.getAreaArr() != null) {
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
                                .into(mAboutVideoImage);
                    }

                    if (mMedia.getSitesArr() != null && mMedia.getSitesArr().size() > 0) {
                        //电影就不用再次请求，电影里面有getSitesArr有播放地址 site_url: "http://www.iqiyi.com/dianying/20111013/7d2c41e9e79c7def.html",

                        movieSites = mMedia.getSitesArr().get(0);
                        mBagpanelpage.setVisibility(View.GONE);
                        mGridview.setVisibility(View.GONE);
                        mChangePlayView.setVisibility(View.VISIBLE);
                        mVideoPlayButton.setVisibility(View.VISIBLE);
                        mDescribeTextview.setVisibility(View.VISIBLE);


                        //setChangePlayUrlButton(mMedia.getSitesArr());

                    } else {
                        //如果是电视或者其他则
                        //再次请求获取播放地址

                        String getPlayUrl = AppNetConfig.XiangQingSingle + "?worktype=adnative" + baiDuRecommend.getWorks_type()
                                + "&id=" + mMedia.getId() + "&site=";

                        getDataFromNet(getPlayUrl);

                    }

                }


            } catch (Exception e) {
            }


        }

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCurrent();
            }
        });

        mVideoPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMedia != null) {
                    mPlayHistory=new PlayHistoryInfo();

                    if (baiDuRecommend.getWorks_type().equals("movie")) {
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


                    }else{
                        if (baiduMediaInfo!=null){
                            if (baiduMediaInfo.getVideosArray()!=null&&baiduMediaInfo.getVideosArray().size()>0){
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


            }
        });



    }


    private void getDataFromNet(String url) {
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                baiduMediaInfo = parserBaiDuMediaInfo(content);

                if (baiduMediaInfo != null) {
                    try {
                        arrVideo = baiduMediaInfo.getVideosArray();
                        mMedia.setMediaItemArrayList(arrVideo);
                        if (arrVideo != null && arrVideo.size() >= 1) {
                            //默认写0；这里表示有很多家视频可以播放，等以后视频切换的时候在打开

                            mDescribeTextview.setVisibility(View.VISIBLE);
                            mBagpanelpage.setVisibility(View.VISIBLE);
                            mVideoPlayButton.setVisibility(View.VISIBLE);

                            if (arrVideo != null && !arrVideo.equals("") && !arrVideo.equals("null")) {
                                setViewPageSize();
                                setPageView(0);

                            }
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
                UIUtils.toast("获取网络数据失败！",false);
            }
        });
    }

    private void setChangePlayUrlButton(ArrayList<Sites> arrSites) {

        if (arrSites != null && arrSites.size() > 0) {
            //			int viewpage = (int)arrSites.size()%PAGE_SIZE==0?
            //					arrSites.size()/PAGE_SIZE:arrSites.size()/PAGE_SIZE+1;
            mChangePlayView.removeAllViews();
            GridView appPage = new GridView(MediaActivity.this);
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

        //		private
        ChangePlayAdapter(ArrayList<Sites> mVide) {
            this.mVideosPlay = mVide;
            this.mInflater = LayoutInflater.from(MediaActivity.this);


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
                Glide.with(MediaActivity.this).load(vid.getSite_logo())
                        .placeholder(R.drawable.bg_list_default)
                        .into(VideoSource);

                txt.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        changeButtonIndex = position;
                        notifyDataSetChanged();
                        if (baiDuRecommend.getWorks_type().equals("movie")) {
                            if (mMedia.getSitesArr() != null && mMedia.getSitesArr().size() > 0) {
                                setMediaItemMovie(vid);
                            }
                        }
                        refleshPlaySite(vid.getSite_url());
                        imagePostion = 0;
                    }
                });

            }

            return v;
        }

    }


    /**
     * 刷新界面，更换播放源地址
     */
    private void refleshPlaySite(String site) {
        //如果是电影就直接播放了
        if (baiDuRecommend.getWorks_type() != null && baiDuRecommend.getWorks_type().equals("movie") && movieSites != null) {
            setPlayerUrl();
        } else {
            //再次请求
            String getPlayUrl = AppNetConfig.XiangQingSingle + "?worktype=adnative" + baiDuRecommend.getWorks_type()
                    + "&id=" + mMedia.getId() + "&site=" + site;
            getDataFromNet(getPlayUrl);
        }

    }


    private void setPlayerUrl() {
        try {
            if (mMedia != null) {
                mPlayHistory=new PlayHistoryInfo();
                ArrayList<MediaItem> mediaItemArrayList = mMedia.getMediaItemArrayList();
                MediaItem mediaItem = mediaItemArrayList.get(mMedia.getPosition());
                String sourceUrl = mediaItem.getSourceUrl();
                //Log.e("TAG", "sourceUrl===" + sourceUrl);
                if (!TextUtils.isEmpty(sourceUrl)){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(sourceUrl));

                    int i = mMedia.getPosition() + 1;
                    if (!TextUtils.isEmpty(mMedia.getTitle())){
                        mPlayHistory.setTitle(mMedia.getTitle());
                    }else{
                        mPlayHistory.setTitle(baiDuRecommend.getTitle());
                    }
                    mPlayHistory.setTitle(mMedia.getTitle());
                    mPlayHistory.setImage_url(mMedia.getImg_url());
                    mPlayHistory.setSource_url(sourceUrl);
                    mPlayHistory.setPosition("第"+i+"集");
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
                }else{
                    UIUtils.toast("抱谦了，该视频不存在了！",false);
                }



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void setViewPageSize() {
        int viewpage = (int) arrVideo.size() % APP_PAGE_SIZE == 0 ?
                arrVideo.size() / APP_PAGE_SIZE : arrVideo.size() / APP_PAGE_SIZE + 1;
        mBagpanelpage.removeAllViews();
        if (viewpage >= 1) {
            if (viewpage >= 5) {
                GridView appPage = new GridView(MediaActivity.this);
                appPage.setNumColumns(5);
                appPage.setHorizontalSpacing(5);
                appPage.setVerticalSpacing(5);
                TextViewAdapter myAppAdapter = new TextViewAdapter(viewpage);
                appPage.setAdapter(myAppAdapter);
                appPage.setSelector(new ColorDrawable(Color.TRANSPARENT));
                mBagpanelpage.addView(appPage);
                mBagpanelpage.setBackgroundResource(R.drawable.bg_videodetail_serise_mul);
            } else {

                if (arrVideo != null && arrVideo.size() > 0) {
                    GridView appPage = new GridView(MediaActivity.this);
                    appPage.setNumColumns(5);
                    appPage.setHorizontalSpacing(5);
                    appPage.setVerticalSpacing(5);
                    TextViewAdapter myAppAdapter = new TextViewAdapter(viewpage);
                    appPage.setAdapter(myAppAdapter);
                    appPage.setSelector(new ColorDrawable(Color.TRANSPARENT));
                    mBagpanelpage.addView(appPage);
                    mBagpanelpage.setBackgroundResource(R.drawable.bg_video_detail_website);
                }

            }

        }

    }

    private int imagePostion = 0;

    class TextViewAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private int viewpage;

        public TextViewAdapter(int viewpage) {
            this.viewpage = viewpage;
            this.mInflater = LayoutInflater.from(MediaActivity.this);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return viewpage;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
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
            if (imagePostion == position) {
                imageView.setBackgroundResource(R.drawable.bg_oriange_line_long);
            } else {
                imageView.setBackgroundResource(R.drawable.bg_tab_my);
            }
            int page = position + 1;
            String text = "第" + page + "页";
            txt.setText(text);
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (position == 4) {
                        initPopwindow(v, viewpage);
                    } else {
                        imagePostion = position;
                        notifyDataSetChanged();
                        setPageView(position);
                    }
                }
            });
            return v;
        }

    }

    public PopupWindow popupWindow;
    private GridView gridview;
    private void initPopwindow(View parent, int viewpage ){
        View view = LayoutInflater.from(MediaActivity.this)
                .inflate(R.layout.pagepopwindow, null);
        gridview = (GridView)view.findViewById(R.id.view_select_grid);
        popupWindow = new PopupWindow(view, UIUtils.dp2px(260),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(parent,UIUtils.dp2px(3), UIUtils.dp2px(5));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        TextViewAdapterPoP myAppAdapter = new TextViewAdapterPoP(viewpage);
        gridview.setAdapter(myAppAdapter);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    private int imagePostionPop=0;
    class TextViewAdapterPoP extends BaseAdapter{
        private LayoutInflater mInflater;
        private int viewpage;

        public TextViewAdapterPoP(int viewpage ){
            this.viewpage = viewpage;
            this.mInflater = LayoutInflater.from(MediaActivity.this);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return viewpage;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v;
            if(convertView == null ){
                v =  mInflater.inflate(R.layout.tvactivity_item, null);
            }else{
                v = convertView;
            }
            TextView txt = (TextView)v.findViewById(R.id.txt_id);
            ImageView imageView = (ImageView)v.findViewById(R.id.imageView1);
            if(imagePostion == position){
                imageView.setBackgroundResource(R.drawable.bg_oriange_line_long);
            } else{
                imageView.setBackgroundResource(R.drawable.bg_tab_my);
            }
            int page = position+1;
            String text = "第"+page+"页";
            txt.setText(text);
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    imagePostionPop = position;
                    notifyDataSetChanged();
                    setPageView(position);
                    dismiss();
                }
            });
            return v;
        }

    }

    private void setPageView(int i) {
        PageCount = (int) arrVideo.size() % APP_PAGE_SIZE == 0 ?
                arrVideo.size() / APP_PAGE_SIZE : arrVideo.size() / APP_PAGE_SIZE + 1;
        mGridview.removeAllViews();
        if (PageCount > 0) {
            GridView appPage = new GridView(MediaActivity.this);
            appPage.setNumColumns(5);
            appPage.setHorizontalSpacing(5);
            appPage.setVerticalSpacing(5);

            EpisodeAdapter myAppAdapter = new EpisodeAdapter(arrVideo, i);
            appPage.setAdapter(myAppAdapter);
            appPage.setSelector(new ColorDrawable(Color.TRANSPARENT));
            mGridview.addView(appPage);
            mGridview.setVisibility(View.VISIBLE);
        }

    }


    class EpisodeAdapter extends BaseAdapter {
        //		ArrayList<Videos> videosArr;
        private int mPage;
        private LayoutInflater mInflater;
        private ArrayList<MediaItem> bagItemList;

        public EpisodeAdapter(ArrayList<MediaItem> episode, int page) {
            this.mInflater = LayoutInflater.from(MediaActivity.this);
            //			this.videosArr = episode ;
            mPage = page;
            bagItemList = new ArrayList<MediaItem>();
            int i = page * APP_PAGE_SIZE;
            int iEnd = i + APP_PAGE_SIZE;

            while ((i < episode.size()) && (i < iEnd)) {
                bagItemList.add(episode.get(i));
                i++;
            }

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (bagItemList != null && bagItemList.size() > 0) {
                return bagItemList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (bagItemList != null && bagItemList.size() > 0) {
                return bagItemList.get(position);
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
                v = mInflater.inflate(R.layout.playbutton_item, null);
            } else {
                v = convertView;
            }


            try {
                if (bagItemList != null && bagItemList.size() > position) {
                    Button button = (Button) v.findViewById(R.id.button);
                    final MediaItem video = bagItemList.get(position);

                    String seq = video.getEpisode();
                    button.setText(seq + "");
                    button.setOnClickListener(new View.OnClickListener() {
                        //
                        @Override
                        public void onClick(View v) {
                            try {
                                int myPosition = (mPage * APP_PAGE_SIZE) + position;
                                mMedia.setPosition(myPosition);

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

}
