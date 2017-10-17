package com.drawshirt.mediamobile.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.activity.ChannelListActivity;
import com.drawshirt.mediamobile.activity.RadiaActivity;
import com.drawshirt.mediamobile.activity.RankingListActivity;
import com.drawshirt.mediamobile.activity.TVActivity;
import com.drawshirt.mediamobile.activity.VideoListActivity;
import com.drawshirt.mediamobile.bean.BaiDuChannelVideo;
import com.drawshirt.mediamobile.common.BaseFragment;
import com.drawshirt.mediamobile.utils.UIUtils;
import com.drawshirt.mediamobile.view.MainTitlebar;
import com.loopj.android.http.RequestParams;

import butterknife.Bind;

/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class ChannelFragment extends BaseFragment {


    @Bind(R.id.main_title)
    MainTitlebar mMainTitle;
    @Bind(R.id.view_main_tab_channle_grid)
    GridView localGridView1;

    int screenWidth;
    private int size;
    int imageArray[] = {
            R.drawable.channel_gridview_ranking,
            R.drawable.channel_gridview_tv,
            R.drawable.channel_gridview_movie,
            R.drawable.channel_gridview_comic,
            R.drawable.channle_gridview_variety,
            R.drawable.channel_gridview_life,
            R.drawable.channel_griditem_funny,
            R.drawable.channel_gridview_music,
            R.drawable.channel_gridview_sport,
            R.drawable.channel_gridview_trailer,
            R.drawable.channle_gridview_hot/*, R.drawable.channle_gridview_ent,
            R.drawable.channel_gridview_radiate*/};


    @Override
    protected String getUrl() {
        return null;
    }

    @Override
    protected RequestParams getParams() {
        return null;
    }

    @Override
    public void initTitle() {
        mMainTitle.show("频道");

        ItemAdapter adapter = new ItemAdapter();
        localGridView1.setAdapter(adapter);
        GridItemOnClick onIten = new GridItemOnClick();
        localGridView1.setOnItemClickListener(onIten);
    }

    @Override
    public void initData(String content) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_channel;
    }

    public class ItemAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ItemAdapter() {
            this.mInflater = LayoutInflater.from(getActivity());
        }

        public int getCount() {
            return imageArray.length;
        }

        public Object getItem(int paramInt) {
            return imageArray[paramInt];
        }

        public long getItemId(int paramInt) {
            return paramInt;
        }

        public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {

            View convertView = mInflater.inflate(R.layout.channel_gridview_item, null);
            ImageView imageview = (ImageView) convertView.findViewById(R.id.imageView1);
            imageview.setBackgroundResource(imageArray[paramInt]);

            return convertView;
        }
    }

    class GridItemOnClick implements AdapterView.OnItemClickListener {
        private GridItemOnClick() {
        }

        public void onItemClick(AdapterView<?> paramAdapterView,
                                View paramView, int paramInt, long paramLong) {

            if (UIUtils.hasNetwork()) {
                Intent i = null;// new Intent(ChannelActivity.this,
                // ChannelListActivity.class);
                switch (paramInt) {
                    case 1:// 电影
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
                        startActivity(i);


                        break;
                    case 2:// 电视
                        i = new Intent(getActivity(), ChannelListActivity.class);
                        BaiDuChannelVideo infoMovie = new BaiDuChannelVideo();
                        infoMovie
                                .setBase_url("http://app.video.baidu.com/adnativemovie/");
                        infoMovie.setExtra("");
                        infoMovie
                                .setFilter("http://app.video.baidu.com/conds/?worktype=adnativemovie");
                        infoMovie.setMask(3);
                        infoMovie.setName("电影");
                        infoMovie.setTag("movie");
                        infoMovie.setType("channel_video");
                        i.putExtra("channelVideoInfo", infoMovie);
                        startActivity(i);

                        break;
                    case 3:// 动漫
                        i = new Intent(getActivity(),ChannelListActivity.class);
                        BaiDuChannelVideo infoComic = new BaiDuChannelVideo();
                        infoComic
                                .setBase_url("http://app.video.baidu.com/adnativecomic/");
                        infoComic.setExtra("");
                        infoComic
                                .setFilter("http://app.video.baidu.com/conds/?worktype=adnativecomic");
                        infoComic.setMask(3);
                        infoComic.setName("动漫");
                        infoComic.setTag("comic");
                        infoComic.setType("channel_video");
                        i.putExtra("channelVideoInfo", infoComic);
                        startActivity(i);

                        break;
                    case 4:// 搞笑
                        i = new Intent(getActivity(),ChannelListActivity.class);
                        BaiDuChannelVideo infoZy = new BaiDuChannelVideo();
                        infoZy.setBase_url("http://app.video.baidu.com/adnativetvshow/");
                        infoZy.setExtra("");
                        infoZy.setFilter("http://app.video.baidu.com/conds/?worktype=adnativetvshow");
                        infoZy.setMask(3);
                        infoZy.setName("综艺");
                        infoZy.setTag("tvshow");
                        infoZy.setType("channel_video");
                        i.putExtra("channelVideoInfo", infoZy);
                        startActivity(i);

                        break;
                    case 5:// 新闻
                        i = new Intent(getActivity(),VideoListActivity.class);
                        BaiDuChannelVideo infoNew = new BaiDuChannelVideo();
                        infoNew.setBase_url("http://m.baidu.com/video?static=utf8_data/android_channel/json/info/");
                        // infoNew.setHotUrl("http://m.baidu.com/video?static=utf8_data/android_channel/json/info/hot/1.js");
                        infoNew.setExtra("");
                        infoNew.setFilter("");
                        infoNew.setMask(2);
                        infoNew.setName("新闻");
                        infoNew.setTag("info");
                        infoNew.setType("short_video");
                        i.putExtra("channelVideoInfo", infoNew);
                        startActivity(i);

                        break;
                    case 6:// 综艺
                        i = new Intent(getActivity(),VideoListActivity.class);
                        BaiDuChannelVideo infoFunny = new BaiDuChannelVideo();
                        infoFunny
                                .setBase_url("http://m.baidu.com/video?static=utf8_data/android_channel/json/amuse/");
                        // infoFunny.setHotUrl("http://m.baidu.com/video?static=utf8_data/android_channel/json/amuse/");//hot/1.js
                        infoFunny.setExtra("");
                        infoFunny.setFilter("");
                        infoFunny.setMask(2);
                        infoFunny.setName("搞笑");
                        infoFunny.setTag("amuse");
                        infoFunny.setType("short_video");
                        i.putExtra("channelVideoInfo", infoFunny);
                        startActivity(i);


                        break;
                    case 7:// 音乐
                        i = new Intent(getActivity(),VideoListActivity.class);
                        BaiDuChannelVideo infoMusic = new BaiDuChannelVideo();
                        infoMusic
                                .setBase_url("http://m.baidu.com/video?static=utf8_data/android_channel/json/music/");
                        // infoMusic.setHotUrl("http://m.baidu.com/video?static=utf8_data/android_channel/json/music/hot/1.js");
                        infoMusic.setExtra("");
                        infoMusic.setFilter("");
                        infoMusic.setMask(2);
                        infoMusic.setName("音乐");
                        infoMusic.setTag("music");
                        infoMusic.setType("short_video");
                        i.putExtra("channelVideoInfo", infoMusic);
                        startActivity(i);

                        break;
                    case 8:// 体育
                        i = new Intent(getActivity(),VideoListActivity.class);
                        BaiDuChannelVideo infoSport = new BaiDuChannelVideo();
                        infoSport
                                .setBase_url("http://m.baidu.com/video?static=utf8_data/android_channel/json/sport/");
                        // infoSport.setHotUrl("http://m.baidu.com/video?static=utf8_data/android_channel/json/sport/hot/1.js");
                        infoSport.setExtra("");
                        infoSport.setFilter("");
                        infoSport.setMask(2);
                        infoSport.setName("体育");
                        infoSport.setTag("sport");
                        infoSport.setType("short_video");
                        i.putExtra("channelVideoInfo", infoSport);
                        startActivity(i);

                        break;
                    case 9:// 花片 这里是 美女
                        i = new Intent(getActivity(),VideoListActivity.class);
                        BaiDuChannelVideo infowWoman = new BaiDuChannelVideo();
                        infowWoman
                                .setBase_url("http://m.baidu.com/video?static=utf8_data/android_channel/json/woman/");
                        // infowWoman.setHotUrl("http://m.baidu.com/video?static=utf8_data/android_channel/json/woman/hot/1.js");
                        infowWoman.setExtra("");
                        infowWoman.setFilter("");
                        infowWoman.setMask(2);
                        infowWoman.setName("美女");
                        infowWoman.setTag("woman");
                        infowWoman.setType("short_video");
                        i.putExtra("channelVideoInfo", infowWoman);
                        startActivity(i);

                        break;
                    case 11:// 娱乐 这里放福利
                        i = new Intent(getActivity(), TVActivity.class);
                        startActivity(i);
                        //
                        break;
                    case 12:
                        i = new Intent(getActivity(), RadiaActivity.class);
                        startActivity(i);

                        break;
                    case 10:
                        i = new Intent(getActivity(),VideoListActivity.class);
                        BaiDuChannelVideo hot = new BaiDuChannelVideo();
                        hot.setBase_url("http://m.baidu.com/video?static=utf8_data/android_channel/json/boshidun/");
                        hot.setExtra("");
                        hot.setFilter("");
                        hot.setMask(2);
                        hot.setName("热点");
                        hot.setTag("boshidun");
                        hot.setType("channel_short");
                        i.putExtra("channelVideoInfo", hot);
                        startActivity(i);
                        break;
                    case 0:
                        i = new Intent(getActivity(),RankingListActivity.class);
                        startActivity(i);

                        break;

                    default:
                        break;
                }

            } else {
                UIUtils.toast( getActivity().getText(R.string.tip_network).toString(),false);

            }
        }
    }


}
