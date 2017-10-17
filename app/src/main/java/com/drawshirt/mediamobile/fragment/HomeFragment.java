package com.drawshirt.mediamobile.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.activity.ChannelListActivity;
import com.drawshirt.mediamobile.adapter.BaiDuRecommondAdapter;
import com.drawshirt.mediamobile.adapter.NetVideoPagerAdapter;
import com.drawshirt.mediamobile.bean.BaiDuChannelVideo;
import com.drawshirt.mediamobile.bean.BaiDuMediaList;
import com.drawshirt.mediamobile.bean.BaiDuRecommend;
import com.drawshirt.mediamobile.bean.LocalMediaItem;
import com.drawshirt.mediamobile.common.AppNetConfig;
import com.drawshirt.mediamobile.common.BaseFragment;
import com.drawshirt.mediamobile.utils.LogUtil;
import com.drawshirt.mediamobile.view.MainTitlebar;
import com.drawshirt.mediamobile.view.MyGridLayoutManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class HomeFragment extends BaseFragment {

    @Bind(R.id.scrollView)
    ScrollView mScrollView;
    @Bind(R.id.banner_home)
    Banner mBannerHome;
    @Bind(R.id.recommendMoreMovie)
    TextView mRecommendMoreMovie;
    @Bind(R.id.rv_movie)
    RecyclerView mRvMovie;
    @Bind(R.id.recommendMoreTv)
    TextView mRecommendMoreTv;
    @Bind(R.id.rv_tvplay)
    RecyclerView mRvTvplay;
    @Bind(R.id.recommendMoreZy)
    TextView mRecommendMoreZy;
    @Bind(R.id.gridviewZy)
    RecyclerView mGridviewZy;
    @Bind(R.id.recommendMoreDm)
    TextView mRecommendMoreDm;
    @Bind(R.id.gridviewDm)
    RecyclerView mGridviewDm;
    @Bind(R.id.main_title)
    MainTitlebar mMainTitle;
    @Bind(R.id.rv_net_movie)
    RecyclerView mRvNetMovie;

    private List<BaiDuRecommend> arrayListMovieHot;
    private List<BaiDuRecommend> arrayListTvplayHot;
    private List<BaiDuRecommend> arrayListTvshowHot;
    private List<BaiDuRecommend> arrayListComicHot;
    private List<BaiDuRecommend> arrayListIndexFlash;

    /**
     * 装数据集合
     */
    private ArrayList<LocalMediaItem> mediaItems;
    private NetVideoPagerAdapter adapter;


    @Override
    protected String getUrl() {
        return AppNetConfig.BaiDuUrlRecommend + "?version=" + AppNetConfig.version;
    }

    @Override
    protected RequestParams getParams() {
        return null;
    }

    @Override
    public void initTitle() {
        mMainTitle.show("首页");
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }


    @Override
    public void initData(String content) {


        getDataFromNet();

        JSONObject jsonObject = JSON.parseObject(content);
        String slices = jsonObject.getString("slices");
        List<BaiDuMediaList> baiDuMediaLists = JSON.parseArray(slices, BaiDuMediaList.class);
        arrayListIndexFlash=baiDuMediaLists.get(0).getHot();
        arrayListMovieHot = baiDuMediaLists.get(1).getHot();
        arrayListTvplayHot = baiDuMediaLists.get(2).getHot();
        arrayListTvshowHot = baiDuMediaLists.get(4).getHot();
        arrayListComicHot = baiDuMediaLists.get(5).getHot();

        List<String> imagesPath = new ArrayList<String>();
        List<String> titles = new ArrayList<String>();

        for (int i = 0; i < arrayListIndexFlash.size(); i++) {
            String imgurl = arrayListIndexFlash.get(i).getImg_url();
            String title=arrayListIndexFlash.get(i).getTitle();
            imagesPath.add(imgurl);
            titles.add(title);
        }

        //保证只有一张图片时也能轮播
        if (arrayListIndexFlash.size()==1){
            imagesPath.add(arrayListIndexFlash.get(0).getImg_url());
            imagesPath.add(arrayListIndexFlash.get(0).getImg_url());
            titles.add(arrayListIndexFlash.get(0).getTitle());
            titles.add(arrayListIndexFlash.get(0).getTitle());
        }

        mBannerHome.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(context).load((String) path)
                        .placeholder(R.drawable.bg_list_default)
                        .error(R.drawable.bg_list_default)
                        .into(imageView);
            }
        });
        mBannerHome.setImages(imagesPath);
        mBannerHome.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        mBannerHome.setBannerTitles(titles);
        mBannerHome.isAutoPlay(true);
        mBannerHome.setDelayTime(2000);
        mBannerHome.setBannerAnimation(Transformer.DepthPage);
        mBannerHome.setIndicatorGravity(BannerConfig.CENTER);
        mBannerHome.start();



        mRvMovie.setLayoutManager(new MyGridLayoutManager(getActivity(), 3));
        mRvMovie.setAdapter(new BaiDuRecommondAdapter(arrayListMovieHot));

        mRvTvplay.setLayoutManager(new MyGridLayoutManager(getActivity(), 3));
        mRvTvplay.setAdapter(new BaiDuRecommondAdapter(arrayListTvplayHot));

        mGridviewZy.setLayoutManager(new MyGridLayoutManager(getActivity(), 3));
        mGridviewZy.setAdapter(new BaiDuRecommondAdapter(arrayListTvshowHot));

        mGridviewDm.setLayoutManager(new MyGridLayoutManager(getActivity(), 3));
        mGridviewDm.setAdapter(new BaiDuRecommondAdapter(arrayListComicHot));


        MyOnClick myOnClick = new MyOnClick();

        mRecommendMoreMovie.setOnClickListener(myOnClick);
        mRecommendMoreTv.setOnClickListener(myOnClick);
        mRecommendMoreZy.setOnClickListener(myOnClick);
        mRecommendMoreDm.setOnClickListener(myOnClick);


        //mScrollView.scrollTo(0,0);
    }

    private void getDataFromNet() {

        new AsyncHttpClient().get(AppNetConfig.NET_URL,new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(String content) {
                super.onSuccess(content);
                processData(content);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                super.onFailure(error, content);
                LogUtil.e("请求数据失败==" + error);
            }

        });

    }

    /**
     * 解析json数据和显示数据
     * 解析数据：1.GsonFormat生成bean对象；2.用gson解析数据
     * @param json
     */
    private void processData(String json) {
        mediaItems = parseJson(json);
        showData();

    }

    private void showData() {
        //设置适配器
        if(mediaItems != null && mediaItems.size() >0){
            //有数据
            //设置适配器
            adapter = new NetVideoPagerAdapter(getActivity(),mediaItems);
            mRvNetMovie.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
            mRvNetMovie.setAdapter(adapter);

        }
    }


    /**
     * 解决json数据：
     * 1.用系统接口解析json数据
     * 2.使用第三方解决工具（Gson,fastjson）
     * @param json
     * @return
     */
    private ArrayList<LocalMediaItem> parseJson(String json) {
        ArrayList<LocalMediaItem> mediaItems = new ArrayList<>();
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            String trailers = jsonObject.getString("trailers");
            JSONArray jsonArray = JSON.parseArray(trailers);
            if(jsonArray!= null && jsonArray.size() >0){

                for (int i=0;i<jsonArray.size();i++){

                    JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);

                    if(jsonObjectItem != null){

                        LocalMediaItem mediaItem = new LocalMediaItem();


                        String movieName = jsonObjectItem.getString("movieName");//name
                        mediaItem.setName(movieName);

                        String videoTitle = jsonObjectItem.getString("videoTitle");//desc
                        mediaItem.setDesc(videoTitle);

                        String imageUrl = jsonObjectItem.getString("coverImg");//imageUrl
                        mediaItem.setImageUrl(imageUrl);

                        String hightUrl = jsonObjectItem.getString("hightUrl");//data
                        mediaItem.setData(hightUrl);

                        //把数据添加到集合
                        mediaItems.add(mediaItem);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mediaItems;
    }

    private class MyOnClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent i = null;
            switch (view.getId()) {
                case R.id.recommendMoreMovie:
                    i = new Intent(getActivity(), ChannelListActivity.class);
                    BaiDuChannelVideo infoMovie = new BaiDuChannelVideo();
                    infoMovie.setBase_url("http://app.video.baidu.com/adnativemovie/");
                    infoMovie.setExtra("");
                    infoMovie.setFilter("http://app.video.baidu.com/conds/?worktype=adnativemovie");
                    infoMovie.setMask(3);
                    infoMovie.setName("电影");
                    infoMovie.setTag("movie");
                    infoMovie.setType("channel_video");
                    i.putExtra("channelVideoInfo", infoMovie);

                    break;
                case R.id.recommendMoreTv:
                    i = new Intent(getActivity(), ChannelListActivity.class);
                    BaiDuChannelVideo infoTv = new BaiDuChannelVideo();
                    infoTv.setBase_url("http://app.video.baidu.com/adnativetvplay/");
                    infoTv.setExtra("");
                    infoTv.setFilter("http://app.video.baidu.com/conds/?worktype=adnativetvplay");
                    infoTv.setMask(3);
                    infoTv.setName("电视剧");
                    infoTv.setTag("tvplay");
                    infoTv.setType("channel_video");
                    i.putExtra("channelVideoInfo", infoTv);
                    break;
                case R.id.recommendMoreZy:
                    i = new Intent(getActivity(), ChannelListActivity.class);
                    BaiDuChannelVideo infoZy = new BaiDuChannelVideo();
                    infoZy.setBase_url("http://app.video.baidu.com/adnativetvshow/");
                    infoZy.setExtra("");
                    infoZy.setFilter("http://app.video.baidu.com/conds/?worktype=adnativetvshow");
                    infoZy.setMask(3);
                    infoZy.setName("综艺");
                    infoZy.setTag("tvshow");
                    infoZy.setType("channel_video");
                    i.putExtra("channelVideoInfo", infoZy);
                    break;
                case R.id.recommendMoreDm:
                    i = new Intent(getActivity(), ChannelListActivity.class);
                    BaiDuChannelVideo infoComic = new BaiDuChannelVideo();
                    infoComic.setBase_url("http://app.video.baidu.com/adnativecomic/");
                    infoComic.setExtra("");
                    infoComic.setFilter("http://app.video.baidu.com/conds/?worktype=adnativecomic");
                    infoComic.setMask(3);
                    infoComic.setName("动漫");
                    infoComic.setTag("comic");
                    infoComic.setType("channel_video");
                    i.putExtra("channelVideoInfo", infoComic);
                    break;
            }
            startActivity(i);
        }
    }
}
