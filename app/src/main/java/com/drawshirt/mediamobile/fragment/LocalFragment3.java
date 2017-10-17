package com.drawshirt.mediamobile.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.activity.AudioPlayerActivity;
import com.drawshirt.mediamobile.activity.SystemVideoPlayerActivity;
import com.drawshirt.mediamobile.adapter.VideoPageAdapter;
import com.drawshirt.mediamobile.bean.LocalMediaItem;
import com.drawshirt.mediamobile.common.BaseFragment;
import com.drawshirt.mediamobile.view.MainTitlebar;
import com.loopj.android.http.RequestParams;

import java.util.ArrayList;

import butterknife.Bind;


/**
 * @author Admin
 * @version $Rev$
 * @des ${TODO}
 * @updateAuthor $Author$
 * @updateDes ${TODO}
 */
public class LocalFragment3 extends BaseFragment {

    private static final int AUDIO_MEDIA = 1;
    private static final int VIDEO_MEDIA = 0;
    @Bind(R.id.main_title)
    MainTitlebar mMainTitle;
    @Bind(R.id.channel_video)
    TextView mChannelVideo;
    @Bind(R.id.channel_audio)
    TextView mChannelAudio;
    @Bind(R.id.nodata_img)
    ImageView mNodataImg;
    @Bind(R.id.playListView)
    TextView mPlayListView;
    @Bind(R.id.havanodata_layout)
    LinearLayout mHavanodataLayout;
    @Bind(R.id.list)
    ListView mList;
    @Bind(R.id.audioList)
    ListView mAudioList;
    @Bind(R.id.icon_nocard)
    ImageView mIconNocard;
    @Bind(R.id.layout)
    LinearLayout mLayout;
    private ArrayList<Object> mediaItems;
    private Handler handler;
    private ArrayList<Object> audioMediaItems;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

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
        mMainTitle.show("本地影音");
    }

    @Override
    public void initData(String content) {
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case VIDEO_MEDIA:
                        if (mediaItems!=null&&mediaItems.size()>0){
                            mList.setAdapter(new VideoPageAdapter(getActivity(),mediaItems,true));
                            mHavanodataLayout.setVisibility(View.GONE);
                        }else {
                            mHavanodataLayout.setVisibility(View.VISIBLE);
                        }
                        mList.setVisibility(View.VISIBLE);
                        mAudioList.setVisibility(View.INVISIBLE);
                        break;
                    case AUDIO_MEDIA:
                        if (audioMediaItems!=null&&audioMediaItems.size()>0){
                            mAudioList.setAdapter(new VideoPageAdapter(getActivity(),audioMediaItems,false));
                            mHavanodataLayout.setVisibility(View.GONE);
                        }else {
                            mHavanodataLayout.setVisibility(View.VISIBLE);
                        }
                        mList.setVisibility(View.INVISIBLE);
                        mAudioList.setVisibility(View.VISIBLE);
                        break;
                }



                //ProgressBar隐藏
                //pb_loading.setVisibility(View.GONE);

            }
        };
        mList.setOnItemClickListener(new VideoOnItemClickListener());
        mAudioList.setOnItemClickListener(new AudioOnItemClickListener());
        getDataFromLocal();

        mChannelVideo.setOnClickListener(myOnClick);
        mChannelAudio.setOnClickListener(myOnClick);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_local;
    }


    private boolean isVideoData=true;
    View.OnClickListener myOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.channel_audio://音乐
                    try {
                        isVideoData = false;
                        mChannelVideo.setBackgroundResource(R.drawable.ad_title_left_new);
                        mChannelAudio.setBackgroundResource(R.drawable.ad_title_right_new_press);
                        if (audioMediaItems==null){
                            getAudioDataFromLocal();
                        }else{
                            mList.setVisibility(View.INVISIBLE);
                            mAudioList.setVisibility(View.VISIBLE);
                            if (audioMediaItems.size()<=0){
                                mHavanodataLayout.setVisibility(View.VISIBLE);
                            }else{
                                mHavanodataLayout.setVisibility(View.GONE);
                            }

                        }


                    } catch (Exception e) {
                        // TODO: handle exception
                    }


                    break;
                case R.id.channel_video://视频
                    try {
                        isVideoData = true;
                        mChannelVideo.setBackgroundResource(R.drawable.ad_title_left_new_press);
                        mChannelAudio.setBackgroundResource(R.drawable.ad_title_right_new);
                        if (mediaItems==null){
                            getDataFromLocal();
                        }else{
                            mList.setVisibility(View.VISIBLE);
                            mAudioList.setVisibility(View.INVISIBLE);
                            if (mediaItems.size()<=0){
                                mHavanodataLayout.setVisibility(View.VISIBLE);
                            }else{
                                mHavanodataLayout.setVisibility(View.GONE);
                            }
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

    private void getDataFromLocal() {
        mediaItems=new ArrayList<>();
        new Thread(){
            @Override
            public void run() {
                super.run();
                ContentResolver resolver = getActivity().getContentResolver();
                Uri uri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs={
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.ARTIST
                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor!=null){
                    while (cursor.moveToNext()){
                        LocalMediaItem mediaItem = new LocalMediaItem();
                        mediaItems.add(mediaItem);
                        mediaItem.setName(cursor.getString(0));
                        mediaItem.setDuration(cursor.getLong(1));
                        mediaItem.setData(cursor.getString(2));
                        mediaItem.setSize(cursor.getLong(3));
                        mediaItem.setArtist(cursor.getString(4));
                    }

                    Message message = handler.obtainMessage();
                    message.what=VIDEO_MEDIA;
                    handler.sendMessage(message);

                }
                cursor.close();
            }
        }.start();
    }
    private void getAudioDataFromLocal() {
        audioMediaItems=new ArrayList<>();
        new Thread(){
            @Override
            public void run() {
                super.run();
                ContentResolver resolver = getActivity().getContentResolver();
                Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs={
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.ARTIST
                };

                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor!=null){
                    while (cursor.moveToNext()){
                        //筛选1MB以上的音频文件
                        if (cursor.getLong(3)>1048576) {
                            LocalMediaItem mediaItem = new LocalMediaItem();
                            audioMediaItems.add(mediaItem);
                            mediaItem.setName(cursor.getString(0));
                            mediaItem.setDuration(cursor.getLong(1));
                            mediaItem.setData(cursor.getString(2));
                            mediaItem.setSize(cursor.getLong(3));
                            mediaItem.setArtist(cursor.getString(4));
                        }
                    }

                    Message message = handler.obtainMessage();
                    message.what=AUDIO_MEDIA;
                    handler.sendMessage(message);

                }
                cursor.close();
            }
        }.start();
    }


    private class VideoOnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            Intent intent = new Intent(getActivity(), SystemVideoPlayerActivity.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("videolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",i);
            getActivity().startActivity(intent);
        }
    }

    private class AudioOnItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            Intent intent = new Intent(getActivity(), AudioPlayerActivity.class);
            intent.putExtra("position",i);
            getActivity().startActivity(intent);
        }
    }



}
