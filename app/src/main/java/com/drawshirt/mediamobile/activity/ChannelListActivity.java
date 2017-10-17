package com.drawshirt.mediamobile.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.adapter.ChannelMovieAdapter;
import com.drawshirt.mediamobile.bean.BaiDuChannelSearch;
import com.drawshirt.mediamobile.bean.BaiDuChannelVideo;
import com.drawshirt.mediamobile.bean.BaiDuRecommend;
import com.drawshirt.mediamobile.bean.ChannelInfo;
import com.drawshirt.mediamobile.bean.Conds;
import com.drawshirt.mediamobile.bean.CurrentConds;
import com.drawshirt.mediamobile.bean.Orders;
import com.drawshirt.mediamobile.bean.ValuesSearch;
import com.drawshirt.mediamobile.common.AppNetConfig;
import com.drawshirt.mediamobile.common.BaseActivity;
import com.drawshirt.mediamobile.utils.UIUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.drawshirt.mediamobile.R.id.btn_search;
import static com.drawshirt.mediamobile.R.id.progressLinear;


public class ChannelListActivity extends BaseActivity implements View.OnClickListener {


    private static final int VISIBLE_THRESHOLD = 2;
    @Bind(R.id.btn_back)
    ImageButton mBtnBack;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.searchText)
    TextView mSearchText;
    @Bind(btn_search)
    ImageButton mBtnSearch;
    @Bind(R.id.rv_channle_list)
    RecyclerView mRvChannleList;
    @Bind(R.id.horizontalSc)
    LinearLayout mHorizontalSc;
    @Bind(R.id.progressbar_loading)
    ProgressBar mProgressbarLoading;
    @Bind(R.id.load_more_textview)
    TextView mLoadMoreTextview;
    @Bind(progressLinear)
    LinearLayout mProgressLinear;
    private BaiDuChannelVideo channelVideo;
    private ChannelInfo channelInfo;
    private ChannelMovieAdapter recommendMovieHot;
    private boolean isGetData=false;
    private LayoutInflater mInflater;
    private BaiDuChannelSearch channelSearch;


    /**
     * 拼凑请求路径
     * 检索也是这个接口
     */
    private String urlRequest(String type, String area, String start, String beg, String end) {
        //?type=&area=&start=&beg=18&end=36&version=3.5.2
        String url = "?type=" + type + "&area=" + area + "&start=" + start + "&beg=" + beg + "&end=" + end + "&version=" + AppNetConfig.version;
        return url;
    }

    @Override
    protected String getUrl() {

        String path = null;
        Intent i = getIntent();

        if (i.getSerializableExtra("channelVideoInfo") != null) {
            channelVideo = (BaiDuChannelVideo) i.getSerializableExtra("channelVideoInfo");
            if (channelVideo != null) {
                try {
                    String base_url = channelVideo.getBase_url();
                    //title.setText(channelVideo.getName()+"");
                    if (base_url != null && !base_url.equals("") && !base_url.equals("null") && UIUtils.hasNetwork()) {
                        path = channelVideo.getBase_url() + urlRequest("", "", "", 0 + "", 18 + "");

                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }

        return path;
    }

    @Override
    protected RequestParams getParams() {
        return null;
    }


    @Override
    protected void initData(String content) {
        this.mInflater = LayoutInflater.from(ChannelListActivity.this);
        mTvTitle.setText(channelVideo.getName()+"");
        initPopwindow();
        channelInfo = parserChannelData(content);

        if (channelInfo != null) {
            //					pagesize = channelInfo.getEnd();
            if (channelVideo.getFilter() != null) {//获取检索数据

                client.get(channelVideo.getFilter(),new AsyncHttpResponseHandler(){
                    @Override
                    public void onSuccess(String content) {
                        super.onSuccess(content);
                        BaiDuChannelSearch object = parserChannelSearch(content);
                        if(object!=null){
                            channelSearch = object;
                            if(channelSearch!=null){
                                ArrayList<Conds> arrConds = channelSearch.getCondsArr();
                                for(int i=0;i<arrConds.size();i++){
                                    Conds conds = arrConds.get(i);
                                    String field = conds.getField();
                                    if(field.equals("type")){
                                        arrValueSearchType = conds.getValuesArr();
                                        continue;
                                    }else if(field.equals("area")){
                                        arrValueSearchArea = conds.getValuesArr();
                                        continue;
                                    }else if(field.equals("start")){
                                        arrValueSearchStart = conds.getValuesArr();
                                        continue;
                                    }
                                }
                                showAsDropDown() ;
                            }


                        }

                    }


                    @Override
                    public void onFailure(Throwable error, String content) {
                        super.onFailure(error, content);
                        UIUtils.toast("获取网络数据失败！",false);
                    }
                });


                //new NetWorkTask().execute(ChannelListActivity.this,UIUtils.Channel_Search,
                //channelVideo.getFilter(),mainHandler);
            }

            refleshView(channelInfo.getVideosArr());
        }

        mRvChannleList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

                int totalItemCount = layoutManager.getItemCount();

                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                //比如一共15个Item，当前到达第13个， Constant.VISIBLE_THRESHOLD设为2
                //总数小于最后一个+阈值，就加载更多数据，同时把loading标记为 true 。
                if (!isGetData && totalItemCount < (lastVisibleItem+VISIBLE_THRESHOLD) ) {

                    if(channelInfo.getVideo_num() == totalItemCount) {
                        mLoadMoreTextview.setText("全部加载完了");
                        mLoadMoreTextview.setVisibility(View.GONE);
                        mProgressbarLoading.setVisibility(View.GONE);
                        mProgressLinear.setVisibility(View.GONE);
                    }else{
                        isGetData = true;
                        try {
                            mProgressbarLoading.setVisibility(View.VISIBLE);
                            mProgressLinear.setVisibility(View.VISIBLE);
                            mLoadMoreTextview.setVisibility(View.VISIBLE);
                            mLoadMoreTextview.setText("正在加载数据");
                            int end = channelInfo.getEnd()+18;
                            String path =channelVideo.getBase_url()+ urlRequest(channelInfo.getCurCondsArr().get(0).getValue(),channelInfo.getCurCondsArr().get(1).getValue(),channelInfo.getCurCondsArr().get(2).getValue(),channelInfo.getEnd()+"",end+"");
                            client.get(path,new AsyncHttpResponseHandler(){
                                @Override
                                public void onSuccess(String content) {
                                    super.onSuccess(content);
                                    channelInfo = parserChannelData(content);
                                    //Log.d("TAG","info==="+info.toString());

                                    refleshView(channelInfo.getVideosArr());

                                    isGetData = false;
                                    //					Utils.closeWaitingDialog();
                                    mProgressbarLoading.setVisibility(View.GONE);
                                    mProgressLinear.setVisibility(View.GONE);
                                    mLoadMoreTextview.setVisibility(View.GONE);
                                }

                                @Override
                                public void onFailure(Throwable error, String content) {
                                    super.onFailure(error, content);
                                    UIUtils.toast("获取网络数据失败！",false);
                                }
                            });

                            //new NetWorkTask().execute(ChannelListActivity.this,UIUtils.Channel_ScrollStateChanged,
                                    //path,channelInfo,mainHandler);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }


                }
            }
        });

        mBtnBack.setOnClickListener(this);
        mBtnSearch.setOnClickListener(this);
        mSearchText.setOnClickListener(this);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_channel_list;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                removeCurrent();
                break;
            case R.id.btn_search:
            case R.id.searchText:
                if(channelSearch!=null){
                    //					ArrayList<Conds> arrConds = channelSearch.getCondsArr();
                    showAsDropDown() ;
                }
                break;
        }
    }


    private BaiDuChannelSearch parserChannelSearch(String js){
        BaiDuChannelSearch channelSearch = new BaiDuChannelSearch();
        try {
            JSONObject json = new JSONObject(js);
            if(json!=null){
                JSONArray condsArr = json.optJSONArray("conds");
                ArrayList<Conds> arrConds = new ArrayList<Conds>();
                for(int i=0;i<condsArr.length();i++){
                    JSONObject jsoConds = condsArr.optJSONObject(i);
                    Conds conds = new Conds();
                    conds.setField(jsoConds.optString("field"));
                    conds.setName(jsoConds.optString("name"));
                    JSONArray valuesArray = jsoConds.optJSONArray("values");
                    ArrayList<ValuesSearch> arrValuesSearch = new ArrayList<ValuesSearch>();
                    for(int j= 0;j<valuesArray.length();j++){
                        JSONObject valuesObject = valuesArray.optJSONObject(j);

                        if(j == 0){//表示到最后一个
                            ValuesSearch serOne = new ValuesSearch();
                            serOne.setTitle("全部");
                            serOne.setTerm("");
                            arrValuesSearch.add(serOne);
                        }


                        ValuesSearch ser = new ValuesSearch();
                        ser.setTitle(valuesObject.optString("title"));
                        ser.setTerm(valuesObject.optString("term"));
                        arrValuesSearch.add(ser);

                    }
                    conds.setValuesArr(arrValuesSearch);
                    arrConds.add(conds);
                }
                channelSearch.setCondsArr(arrConds);
                if(json.has("orders")){
                    JSONArray  ordersArr = json.optJSONArray("orders");

                    ArrayList<Orders> ordersList = new ArrayList<Orders>();
                    for(int i=0;i<ordersArr.length();i++){
                        JSONObject jsonOrder = ordersArr.optJSONObject(i);
                        Orders orders = new Orders();
                        orders.setField(jsonOrder.optString("field"));
                        orders.setName(jsonOrder.optString("name"));
                        ordersList.add(orders);
                    }
                    channelSearch.setOrdersArra(ordersList);
                }


            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return channelSearch;
    }


    private LinearLayout myGalleryType;
    private LinearLayout myGalleryArea;
    private LinearLayout myGalleryActor;
    ArrayList<ValuesSearch> arrValueSearchType = null;
    ArrayList<ValuesSearch> arrValueSearchArea = null;
    ArrayList<ValuesSearch> arrValueSearchStart = null;
    public PopupWindow popupWindow;
    private HorizontalScrollView horizontalScrollViewType;
    private HorizontalScrollView horizontalScrollViewArea;
    private HorizontalScrollView horizontalScrollViewActor;
    private Boolean pop_flag = false;

    private void initPopwindow(){
        View view = LayoutInflater.from(ChannelListActivity.this)
                .inflate(R.layout.popmenu, null);
        //			bagpanelpage = (LinearLayout)view.findViewById(R.id.bagpanelpage);
        myGalleryType = (LinearLayout)view.findViewById(R.id.myGalleryType);
        myGalleryArea = (LinearLayout)view.findViewById(R.id.myGalleryArea);
        myGalleryActor = (LinearLayout)view.findViewById(R.id.myGalleryActor);
        horizontalScrollViewType = (HorizontalScrollView)view.findViewById(R.id.horizontalScrollViewType);
        horizontalScrollViewArea = (HorizontalScrollView)view.findViewById(R.id.horizontalScrollViewArea);
        horizontalScrollViewActor = (HorizontalScrollView)view.findViewById(R.id.horizontalScrollViewActor);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                // TODO Auto-generated method stub
                mBtnSearch.setBackgroundResource(R.drawable.titlebar_filter_expanded_normal);;
            }
        });

        mBtnSearch.setBackgroundResource(R.drawable.titlebar_filter_shrinked_normal);
    }


    public void showAsDropDown() {
        try {
            myGalleryType.removeAllViews();
            myGalleryArea.removeAllViews();
            myGalleryActor.removeAllViews();

            mBtnSearch.setBackgroundResource(R.drawable.titlebar_filter_shrinked_normal);
            if(arrValueSearchType!=null&&arrValueSearchType.size()>0){
                horizontalScrollViewType.setVisibility(View.VISIBLE);
                for(int i=0;i<arrValueSearchType.size();i++){
                    myGalleryType.addView(insertType(arrValueSearchType.get(i),i));

                }
            }else{
                horizontalScrollViewType.setVisibility(View.GONE);
            }
            if(arrValueSearchArea!=null&&arrValueSearchArea.size()>0){
                horizontalScrollViewArea.setVisibility(View.VISIBLE);
                for(int i=0;i<arrValueSearchArea.size();i++){
                    myGalleryArea.addView(insertArea(arrValueSearchArea.get(i),i));
                    //						myGalleryArea.setBackgroundResource(R.drawable.bg_home_title);
                }
            }else{
                horizontalScrollViewArea.setVisibility(View.GONE);
            }
            if(arrValueSearchStart!=null&&arrValueSearchStart.size()>0){
                horizontalScrollViewActor.setVisibility(View.VISIBLE);
                for(int i=0;i<arrValueSearchStart.size();i++){
                    myGalleryActor.addView(insertStart(arrValueSearchStart.get(i),i));
                    //						myGalleryActor.setBackgroundResource(R.drawable.bg_home_title);
                }
            }else{
                horizontalScrollViewActor.setVisibility(View.GONE);
            }
            popupWindow.showAsDropDown(mTvTitle, 10, 5);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.update();
        } catch (Exception e) {
        }

    }

    private int selectorPostionType = 0;
    private View insertType(final ValuesSearch valueSearch,final int postion) {
        View  v =  mInflater.inflate(R.layout.pomenu_item, null);
        Button textview = (Button)v.findViewById(R.id.button);
        textview.setText(valueSearch.getTitle()+"");
        if(selectorPostionType == postion){
            textview.setBackgroundResource(R.drawable.ads_accountime_bg);
        }else{
            textview.setBackgroundResource(R.drawable.channel_gallery_item);
        }
        textview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    selectorPostionType = postion;
                    ChannelInfo chan = new ChannelInfo();
                    requestSearch(valueSearch.getTerm(), channelInfo.getCurCondsArr().get(1).getValue(), channelInfo.getCurCondsArr().get(2).getValue(), 0, 18,chan);
                    myGalleryType.removeAllViews();
                    for(int i=0;i<arrValueSearchType.size();i++){
                        myGalleryType.addView(insertType(arrValueSearchType.get(i),i));
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        });

        return v;
    }

    private int selectorPostionArea = 0;
    private View insertArea(final ValuesSearch valueSearch,final int postion) {
        View  v =  mInflater.inflate(R.layout.pomenu_item, null);
        Button textview = (Button)v.findViewById(R.id.button);
        textview.setText(valueSearch.getTitle()+"");
        if(selectorPostionArea== postion){
            textview.setBackgroundResource(R.drawable.ads_accountime_bg);
        }else{
            textview.setBackgroundResource(R.drawable.channel_gallery_item);
        }

        textview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    selectorPostionArea = postion;
                    ChannelInfo chan = new ChannelInfo();
                    requestSearch( channelInfo.getCurCondsArr().get(0).getValue(),valueSearch.getTerm(), channelInfo.getCurCondsArr().get(2).getValue(), 0, 18,chan);
                    myGalleryArea.removeAllViews();
                    for(int i=0;i<arrValueSearchArea.size();i++){
                        myGalleryArea.addView(insertArea(arrValueSearchArea.get(i),i));
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        });

        return v;
    }
    private int selectorPostionStart= 0;
    private View insertStart(final ValuesSearch valueSearch,final int postion) {

        View  v =  mInflater.inflate(R.layout.pomenu_item, null);
        Button textview = (Button)v.findViewById(R.id.button);
        textview.setText(valueSearch.getTitle()+"");

        if(selectorPostionStart== postion){
            textview.setBackgroundResource(R.drawable.ads_accountime_bg);
        }else{
            textview.setBackgroundResource(R.drawable.channel_gallery_item);
        }
        textview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    selectorPostionStart = postion;
                    ChannelInfo chan = new ChannelInfo();
                    requestSearch( channelInfo.getCurCondsArr().get(0).getValue(),channelInfo.getCurCondsArr().get(1).getValue(), valueSearch.getTerm(), 0, 18,chan);

                    myGalleryActor.removeAllViews();
                    for(int i=0;i<arrValueSearchStart.size();i++){
                        myGalleryActor.addView(insertStart(arrValueSearchStart.get(i),i));
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        });
        return v;
    }


    private void requestSearch(String type,String area,String start,int beg,int end, ChannelInfo chan ){
        //Utils.startWaitingDialog(ChannelListActivity.this);
        try {
            String typeEncoder  = "";
            if(type!=null && !type.equals("")&&!type.equals("全部"))
                typeEncoder = URLEncoder.encode(type, "utf-8");
            String areaEncoder  = "";
            if(area!=null && !area.equals("") &&!type.equals("全部"))
                areaEncoder = URLEncoder.encode(area, "utf-8");
            String startEncoder  = "";
            if(start!=null && !start.equals("")&&!type.equals("全部"))
                startEncoder = URLEncoder.encode(start, "utf-8");
            String path =channelVideo.getBase_url()+ urlRequest(typeEncoder,areaEncoder,startEncoder,beg+"",end+"");
            client.get(path,new AsyncHttpResponseHandler(){
                @Override
                public void onSuccess(String content) {
                    super.onSuccess(content);
                    ChannelInfo object = parserChannelData(content);
                    if(object!=null){
                        channelInfo =object;
                        //Log.d("TAG","channelInfo==="+channelInfo.toString());
                        //refleshView(channelInfo.getVideosArr());
                        recommendMovieHot.setData(channelInfo.getVideosArr());
                        recommendMovieHot.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    super.onFailure(error, content);
                    UIUtils.toast("获取网络数据失败！",false);
                }
            });
           // new NetWorkTask().execute(ChannelListActivity.this,UIUtils.Channel_Search_Video,
           //         path,chan,mainHandler);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void refleshView(List<BaiDuRecommend> arrayList) {
        if (recommendMovieHot == null) {

            recommendMovieHot = new ChannelMovieAdapter(arrayList);
            mRvChannleList.setLayoutManager(new GridLayoutManager(this, 3));
            mRvChannleList.setAdapter(recommendMovieHot);
            //mViewMainTabChannleGrid.setAdapter(recommendMovieHot);
        } else {
            recommendMovieHot.addData(arrayList);
            recommendMovieHot.notifyDataSetChanged();
        }
    }




    private ChannelInfo parserChannelData(String js) {
        ChannelInfo info = new ChannelInfo();
        try {
            JSONObject jsonObject = new JSONObject(js);
            if (jsonObject.has("video_list")) {
                JSONObject json = jsonObject.optJSONObject("video_list");

                if (json != null) {
                    info.setVideo_num(json.optInt("video_num"));
                    info.setBeg(json.optInt("beg"));
                    info.setEnd(json.getInt("end"));
                    JSONArray jsonVideoArr = json.optJSONArray("videos");
                    List<BaiDuRecommend> videosArr = info.getVideosArr();
                    for (int i = 0; i < jsonVideoArr.length(); i++) {
                        BaiDuRecommend video = new BaiDuRecommend();
                        JSONObject jsonVideo = jsonVideoArr.optJSONObject(i);
                        video.setDuration(jsonVideo.optString("duration"));
                        video.setImg_url(jsonVideo.optString("img_url"));
                        video.setPlay_filter(jsonVideo.optString("play_filter"));
                        video.setTitle(jsonVideo.optString("title"));
                        video.setUpdate(jsonVideo.optString("update"));
                        video.setUrl(jsonVideo.optString("url"));
                        video.setWorks_id(jsonVideo.optString("works_id"));
                        video.setWorks_type(jsonVideo.optString("works_type"));
                        video.setRating(jsonVideo.optString("rating"));
                        videosArr.add(video);
                    }
                    info.setVideosArr(videosArr);
                }
                JSONArray curCondsJson = jsonObject.optJSONArray("cur_conds");
                if (curCondsJson != null) {
                    ArrayList<CurrentConds> arr = new ArrayList<CurrentConds>();
                    for (int i = 0; i < curCondsJson.length(); i++) {
                        JSONObject jsonO = curCondsJson.optJSONObject(i);
                        CurrentConds currentC = new CurrentConds();
                        currentC.setKey(jsonO.optString("key"));
                        currentC.setValue(jsonO.optString("value"));
                        arr.add(currentC);
                    }
                    info.setCurCondsArr(arr);
                }

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return info;
    }


}
