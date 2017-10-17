package com.drawshirt.mediamobile.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import com.drawshirt.mediamobile.bean.BaiduResolution;
import com.drawshirt.mediamobile.bean.BaoFengPlayUrl;
import com.drawshirt.mediamobile.bean.PlayHistoryInfo;
import com.drawshirt.mediamobile.bean.SearchData;
import com.drawshirt.mediamobile.bean.SearchResult;
import com.drawshirt.mediamobile.common.AppNetConfig;
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

import static com.drawshirt.mediamobile.R.id.area;
import static com.drawshirt.mediamobile.R.id.changePlayView;
import static com.drawshirt.mediamobile.R.id.craw;
import static com.drawshirt.mediamobile.R.id.describe;
import static com.drawshirt.mediamobile.R.id.describeTextview;
import static com.drawshirt.mediamobile.R.id.director;
import static com.drawshirt.mediamobile.R.id.lin;

public class MediaSearchActivity extends BaseActivity {


    @Bind(R.id.btn_back)
    ImageButton mBtnBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.about_video_image)
    ImageView mAboutVideoImage;
    @Bind(lin)
    RelativeLayout mLin;
    @Bind(director)
    TextView mDirector;
    @Bind(craw)
    TextView mCraw;
    @Bind(area)
    TextView mArea;
    @Bind(R.id.videoPlayButton_history_text)
    TextView mVideoPlayButtonHistoryText;
    @Bind(R.id.videoPlayButton)
    ImageView mVideoPlayButton;
    @Bind(R.id.bagpanelpage)
    LinearLayout mBagpanelpage;
    @Bind(R.id.gridview)
    RelativeLayout mGridview;
    @Bind(changePlayView)
    LinearLayout mChangePlayView;
    @Bind(describeTextview)
    TextView mDescribeTextview;
    @Bind(describe)
    TextView mDescribe;
    @Bind(R.id.layout_tv)
    LinearLayout mLayoutTv;
    private Intent mIntent;
    private SearchData voide;
    private SearchResult search;
    private String mMediaName;
    public final int APP_PAGE_SIZE = 20;//一页最多20
    private int mPosition;
    private PlayHistoryDao dao = null;
    private String mItem;

    @Override
    protected String getUrl() {
        String url = null;
        mIntent = getIntent();
        if (mIntent != null) {
            voide = (SearchData) mIntent.getSerializableExtra("BaiDuRecommend");
            if (UIUtils.hasNetwork()) {
                Utils.startWaitingDialog(MediaSearchActivity.this);
                url = "http://search.shouji.baofeng.com/mdetail.php?aid=" + voide.getId() + "&mtype=normal&ver=" + AppNetConfig.versionBaoFeng;
            } else {
                UIUtils.toast(MediaSearchActivity.this.getText(R.string.tip_network).toString(), false);
                url = null;
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
        dao=new PlayHistoryDao(MediaSearchActivity.this);
        SearchResult object = parserSearchResult(content);
        if (object != null) {
            try {
                search = object;
                mMediaName = voide.getTitle();
                mTvTitle.setText("" + mMediaName);

                if (voide.getDirectors_name() != null && voide.getDirectors_name().size() > 0) {
                    String s = "";
                    for (int i = 0; i < voide.getDirectors_name().size(); i++) {
                        String director = voide.getDirectors_name().get(i);
                        s += director + "  ";
                    }
                    mDirector.setText("导演：" + s + "");
                }
                if (voide.getActors_name() != null && voide.getActors_name().size() > 0) {
                    String s = "";
                    for (int i = 0; i < voide.getActors_name().size(); i++) {
                        String actor = voide.getActors_name().get(i);
                        s += actor + "   ";
                    }
                    mCraw.setText("演员：" + s + "");
                }
                //					    describeTextview.setText("简介");
                if (search.getDesc() != null && !search.getDesc().equals("") && !search.getDesc().equals("null")) {
                    mDescribeTextview.setBackgroundResource(R.drawable.bg_video_detail_website);
                    mDescribeTextview.setText("简介");
                    mDescribe.setText(search.getDesc() + "");
                    mDescribeTextview.setVisibility(View.VISIBLE);
                }

                //					    String seqCount = voide.getTotal();

                if (voide.getArea_l() != null && !voide.getArea_l().equals("") && !voide.getArea_l().equals("null")) {
                    mArea.setText("地区：" + voide.getArea_l() + "");
                }

                if (voide.getCover_url() != null && !voide.getCover_url().equals("")) {
                    mLin.setBackgroundResource(R.drawable.video_pic_bg);

                    Glide.with(MediaSearchActivity.this).load(voide.getCover_url())
                            .placeholder(R.drawable.bg_list_default)
                            .into(mAboutVideoImage);
                }

                if (voide.getMax_site() != null) {
                    mChangePlayView.setVisibility(View.VISIBLE);
                    TextView t = new TextView(MediaSearchActivity.this);
                    t.setTextColor(Color.WHITE);
                    t.setText("视频来源：" + voide.getMax_site() + "");
                    mChangePlayView.addView(t);
                }

                //默认写0；这里表示有很多家视频可以播放，等以后视频切换的时候在打开

                mBagpanelpage.setVisibility(View.VISIBLE);
                mVideoPlayButton.setVisibility(View.VISIBLE);

                if (search.getHas() != null && search.getHas().size() > 0) {
                    setViewPageSize();
                    setPageView(0);
                }
                Utils.closeWaitingDialog();
            } catch (Exception e) {
                Utils.closeWaitingDialog();
            }
        } else {
            Utils.closeWaitingDialog();
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
                if (search != null && search.getHas() != null && search.getHas().size() > 0)
                    getPlayUrl(search.getHas().get(0));
            }
        });


    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_media;
    }


    private void setViewPageSize() {
        if (search.getHas() != null && search.getHas().size() > 0) {
            mBagpanelpage.removeAllViews();
            int viewpage = (int) search.getHas().size() % APP_PAGE_SIZE == 0 ?
                    search.getHas().size() / APP_PAGE_SIZE : search.getHas().size() / APP_PAGE_SIZE + 1;
            if (viewpage >= 1) {
                if (viewpage >= 5) {
                    GridView appPage = new GridView(MediaSearchActivity.this);
                    appPage.setNumColumns(5);
                    appPage.setHorizontalSpacing(5);
                    appPage.setVerticalSpacing(5);
                    TextViewAdapter myAppAdapter = new TextViewAdapter(viewpage);
                    appPage.setAdapter(myAppAdapter);
                    appPage.setSelector(new ColorDrawable(Color.TRANSPARENT));
                    mBagpanelpage.addView(appPage);
                    mBagpanelpage.setBackgroundResource(R.drawable.bg_videodetail_serise_mul);
                } else {

                    GridView appPage = new GridView(MediaSearchActivity.this);
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
            this.mInflater = LayoutInflater.from(MediaSearchActivity.this);
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

    private void initPopwindow(View parent, int viewpage) {
        View view = LayoutInflater.from(MediaSearchActivity.this)
                .inflate(R.layout.pagepopwindow, null);
        gridview = (GridView) view.findViewById(R.id.view_select_grid);
        popupWindow = new PopupWindow(view, UIUtils.dp2px(260),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(parent, UIUtils.dp2px(3), UIUtils.dp2px(5));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        TextViewAdapterPoP myAppAdapter = new TextViewAdapterPoP(viewpage);
        gridview.setAdapter(myAppAdapter);
    }

    public void dismiss() {
        popupWindow.dismiss();
    }

    private int imagePostionPop = 0;

    class TextViewAdapterPoP extends BaseAdapter {
        private LayoutInflater mInflater;
        private int viewpage;

        public TextViewAdapterPoP(int viewpage) {
            this.viewpage = viewpage;
            this.mInflater = LayoutInflater.from(MediaSearchActivity.this);
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
        if (search.getHas() != null && search.getHas().size() > 0) {
            mGridview.removeAllViews();
            GridView appPage = new GridView(MediaSearchActivity.this);
            appPage.setNumColumns(5);
            appPage.setHorizontalSpacing(5);
            appPage.setVerticalSpacing(5);

            EpisodeAdapter myAppAdapter = new EpisodeAdapter(search.getHas(), i);
            appPage.setAdapter(myAppAdapter);
            appPage.setSelector(new ColorDrawable(Color.TRANSPARENT));
            mGridview.addView(appPage);
            mGridview.setVisibility(View.VISIBLE);
        }

    }

    class EpisodeAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        //	int episode;
        ArrayList<String> arrCurrent;
        ArrayList<String> arr;

        public EpisodeAdapter(ArrayList<String> arrCurrent, int page) {
            this.mInflater = LayoutInflater.from(MediaSearchActivity.this);
            this.arrCurrent = arrCurrent;
            int i = page * APP_PAGE_SIZE;
            int iEnd = i + APP_PAGE_SIZE;
            arr = new ArrayList<String>();
            while ((i < arrCurrent.size()) && (i < iEnd)) {
                arr.add(arrCurrent.get(i));
                i++;
            }
        }

        @Override
        public int getCount() {
            if (arr != null)
                return arr.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (arr != null) {
                return arr.get(position);
            }
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
                v = mInflater.inflate(R.layout.playbutton_item, null);
            } else {
                v = convertView;
            }
            try {
                if (arr != null && arr.size() > position) {
                    Button button = (Button) v.findViewById(R.id.button);

                    button.setText(arr.get(position) + "");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getPlayUrl(arr.get(position));
                            mPosition = position;
                        }
                    });
                }

            } catch (Exception e) {
            }
            return v;
        }
    }

    private void getPlayUrl(String seq) {
        if (UIUtils.hasNetwork()) {
            Utils.startWaitingDialog(MediaSearchActivity.this);
            String url = "http://search.shouji.baofeng.com/minfo.php?aid=" + voide.getId() + "&seq=" + seq + "&mtype=normal&ver=" + AppNetConfig.versionBaoFeng;

            client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String content) {
                    super.onSuccess(content);
                    BaoFengPlayUrl object = parserPlayUrl(content);
                    try {
                        if (object != null) {
                            BaoFengPlayUrl playUrl = object;
                            mItem = playUrl.getTitle();
                            playView(playUrl.getPage_url());
                        } else {
                            Utils.closeWaitingDialog();
                        }
                        //				Utils.closeWaitingDialog();
                    } catch (Exception e) {
                        // TODO: handle exception
                        Utils.closeWaitingDialog();
                    }
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                }
            });

        } else {
            UIUtils.toast(MediaSearchActivity.this.getText(R.string.tip_network).toString(), false);
        }
    }

    private void playView(String videoUrl) {
        if (!Utils.isEmpty(videoUrl)) {

            if (videoUrl.contains(".m3u8") || videoUrl.contains(".mp4")) {
                //					MediaItem  mediaItem = (MediaItem)object;
                //					if(mediaItem != null){
                String video_source_url = videoUrl;
                if (!Utils.isEmpty(video_source_url)) {
                    UIUtils.toast("用播放器播放！", false);

                } else {
                    String sourceUri = videoUrl;
                    if (!Utils.isEmpty(sourceUri)) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(sourceUri));
                        startActivity(intent);
                    } else {
                        UIUtils.toast("该视频无法播放", false);
                    }

                }
            } else {
                try {
                    final BaiduResolution playDatas = new BaiduResolution();
                    playDatas.setSourceUrl(videoUrl);
                    String baiduService = "http://gate.baidu.com/tc?m=8&video_app=1&ajax=1&src=" + videoUrl;
                    if (!Utils.isEmpty(baiduService)) {
                        if (UIUtils.hasNetwork()) {
                            client.get(baiduService, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(String content) {
                                    super.onSuccess(content);
                                    BaiduResolution object = parserMediaItem(content, playDatas);
                                    if (object != null) {
                                        try {
                                            BaiduResolution mediaItem = object;
                                            if (mediaItem != null) {
                                                String video_source_url = mediaItem.getVideo_source_url();
                                                if (!Utils.isEmpty(video_source_url)) {
                                                    UIUtils.toast("用播放器播放！", false);
                                                } else {
                                                    String sourceUri = mediaItem.getSourceUrl();
                                                    if (!Utils.isEmpty(sourceUri)) {
                                                        Intent intent = new Intent();
                                                        intent.setAction(Intent.ACTION_VIEW);
                                                        intent.setData(Uri.parse(sourceUri));

                                                        final PlayHistoryInfo playHistoryInfo = new PlayHistoryInfo();
                                                        playHistoryInfo.setTitle(voide.getTitle());
                                                        playHistoryInfo.setImage_url(voide.getCover_url());
                                                        int i ;
                                                        if (mPosition >= 0) {
                                                            i = mPosition + 1;
                                                            playHistoryInfo.setPosition("第" + i + "集");
                                                        }
                                                        playHistoryInfo.setSource_url(sourceUri);
                                                        playHistoryInfo.setPlay_time(System.currentTimeMillis());
                                                        if(dao!=null ) {
                                                            new Thread() {
                                                                @Override
                                                                public void run() {
                                                                    dao.insert(playHistoryInfo);
                                                                }
                                                            }.start();
                                                        }
                                                        startActivity(intent);
                                                    } else {
                                                        UIUtils.toast("该视频无法播放", false);
                                                    }

                                                }

                                            }
                                        } catch (Exception e) {
                                            // TODO: handle exception
                                        }
                                        Utils.closeWaitingDialog();
                                    } else {
                                        Utils.closeWaitingDialog();
                                    }
                                }

                                @Override
                                public void onFailure(Throwable error, String content) {
                                    super.onFailure(error, content);
                                }
                            });
                        } else {
                            UIUtils.toast(MediaSearchActivity.this.getText(R.string.tip_network).toString(), false);
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }

        }
    }

    public BaiduResolution parserMediaItem(String js, BaiduResolution homeRes) {

        try {
            JSONObject jsonOb = new JSONObject(js);
            if (jsonOb != null) {
                String logo = jsonOb.optString("logo");
                homeRes.setLogo(logo);

                String STRUCT_PAGE_TYPE = jsonOb.optString("STRUCT_PAGE_TYPE");
                homeRes.setSTRUCT_PAGE_TYPE(STRUCT_PAGE_TYPE);

                String video_source_url = jsonOb.optString("video_source_url");
                homeRes.setVideo_source_url(video_source_url);

                String video_source_type = jsonOb.optString("video_source_type");
                homeRes.setVideo_source_type(video_source_type);

                String video_trans_url = jsonOb.optString("video_trans_url");
                homeRes.setVideo_trans_url(video_trans_url);


                String content = jsonOb.optString("content");
                homeRes.setContent(content);


                String src_url_processed = jsonOb.optString("src_url_processed");
                homeRes.setSrc_url_processed(src_url_processed);

                String page_title = jsonOb.optString("page_title");
                homeRes.setPage_title(page_title);

                String src_url = jsonOb.optString("src_url");
                homeRes.setSrc_url(src_url);

                return homeRes;

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return homeRes;
    }

    private BaoFengPlayUrl parserPlayUrl(String js) {
        //HashMap<String, String> map  = new HashMap<String, String>();
        BaoFengPlayUrl playUrl = new BaoFengPlayUrl();

        try {
            JSONArray jsarr = new JSONArray(js);
            for (int i = 0; i < jsarr.length(); i++) {
                JSONObject json = jsarr.optJSONObject(i);
                playUrl.setCover_url(json.optString("cover_url"));
                playUrl.setId(json.optString("id"));
                playUrl.setPage_url(json.optString("page_url"));
                playUrl.setSeq(json.optString("seq"));
                playUrl.setSite(json.optString("site"));
                playUrl.setSubseq(json.optString("subseq"));
                playUrl.setTitle(json.optString("title"));

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return playUrl;
    }

    private SearchResult parserSearchResult(String s) {
        SearchResult res = new SearchResult();
        try {
            JSONObject jsonobject = new JSONObject(s);
            res.setId(jsonobject.optString("id"));
            res.setDesc(jsonobject.optString("desc"));

            if (jsonobject.has("has")) {

                String hasJson = jsonobject.optString("has");
                if (hasJson != null) {
                    JSONArray hasArr = new JSONArray(hasJson);
                    ArrayList<String> arr = new ArrayList<String>();
                    if (hasArr != null)
                        for (int i = 0; i < hasArr.length(); i++) {
                            arr.add(hasArr.optString(i));
                        }
                    res.setHas(arr);
                }

            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return res;
    }


}
