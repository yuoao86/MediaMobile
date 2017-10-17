package com.drawshirt.mediamobile.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.drawshirt.mediamobile.IMusicPlayerService;
import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.bean.LocalMediaItem;
import com.drawshirt.mediamobile.service.MusicPlayerService;
import com.drawshirt.mediamobile.utils.LyricUtils;
import com.drawshirt.mediamobile.utils.UIUtils;
import com.drawshirt.mediamobile.utils.Utils;
import com.drawshirt.mediamobile.view.ShowLyricView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;


public class AudioPlayerActivity extends Activity implements View.OnClickListener {


    private static final int PROGRESS = 1;
    private static final int SHOW_LYRIC = 2;
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private ImageButton btSetMode;
    private ImageButton btPre;
    private ImageButton btVideoStartPause;
    private ImageButton btNext;
    private ImageButton btnLyrc;
//    private BaseVisualizerView baseVisualizerView;

    private IMusicPlayerService service;

    private Utils utils;
    private boolean notificatoin;
    private ShowLyricView showLyricView;

    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service= IMusicPlayerService.Stub.asInterface(iBinder);
            if (service!=null){
                try {

                    if (!notificatoin){
                        service.openAudio(position);
                    }else {
                        showData(null);
                    }



                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            if (service!=null){
                try {
                    service.stop();
                    service=null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }


            }
        }
    };
    private int position;
    private BroadcastReceiver receiver;
    private Handler handler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW_LYRIC://显示歌词

                    //1.得到当前的进度
                    try {
                        int currentPosition = service.getCurrentPosition();


                        //2.把进度传入ShowLyricView控件，并且计算该高亮哪一句

                        showLyricView.setshowNextLyric(currentPosition);
                        //3.实时的发消息
                        handler.removeMessages(SHOW_LYRIC);
                        handler.sendEmptyMessage(SHOW_LYRIC);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;


                case PROGRESS:
                    try {

                        tvTime.setText(UIUtils.stringForTime(service.getCurrentPosition())+"/"+UIUtils.stringForTime(service.getDuration()));

                        seekbarAudio.setProgress(service.getCurrentPosition());

                        handler.removeMessages(PROGRESS);
                        handler.sendEmptyMessageDelayed(PROGRESS,1000);


                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        findViews();
        getData();
        bindAndStartService();
    }

    private void initData() {
        utils = new Utils();
        //        receiver = new BroadcastReceiver() {
        //            @Override
        //            public void onReceive(Context context, Intent intent) {
        //                showViewData();
        //                checkPlayMode();
        //            }
        //        };
        //        IntentFilter intentFilter=new IntentFilter();
        //        intentFilter.addAction(MusicPlayerService.OPENAUDIO);
        //        registerReceiver(receiver,intentFilter);


        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = false,priority = 0)
    public void showData(LocalMediaItem mediaItem){
        showViewData();
        checkPlayMode();
        showLyric();
        //setupVisualizerFxAndUi();
    }

    private void showLyric() {
        //解析歌词
        LyricUtils lyricUtils = new LyricUtils();

        try {
            String path = service.getAudioPath();//得到歌曲的绝对路径

            //传歌词文件
            //mnt/sdcard/audio/beijingbeijing.mp3
            //mnt/sdcard/audio/beijingbeijing.lrc
            path = path.substring(0,path.lastIndexOf("."));
            File file = new File(path + ".lrc");
            if(!file.exists()){
                file = new File(path + ".txt");
            }
            lyricUtils.readLyricFile(file);//解析歌词

            showLyricView.setLyrics(lyricUtils.getLyrics());

        } catch (RemoteException e) {
            e.printStackTrace();
        }



        if(lyricUtils.isExistsLyric()){
            handler.sendEmptyMessage(SHOW_LYRIC);
        }

    }

//    private Visualizer mVisualizer;

//    private void setupVisualizerFxAndUi(){
//
//        try {
//            int audioSession = service.getAudioSessionId();
//            mVisualizer=new Visualizer(audioSession);
//            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
//            baseVisualizerView.setVisualizer(mVisualizer);
//            mVisualizer.setEnabled(true);
//
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//
//    }

    private void checkPlayMode() {
        try {
            int playMode = service.getPlayMode();

            if (playMode== MusicPlayerService.REPEAT_NORMAL){
                btSetMode.setImageResource(R.drawable.btn_order_selector);

            }else if (playMode==MusicPlayerService.REPEAT_SINGLE){
                btSetMode.setImageResource(R.drawable.btn_single_selector);

            }else if (playMode==MusicPlayerService.REPEAT_ALL){
                btSetMode.setImageResource(R.drawable.btn_repeat_selector);

            }else{
                btSetMode.setImageResource(R.drawable.btn_repeat_selector);
            }

            if (service.isPlaying()){
                if (notificatoin){
                    btVideoStartPause.setImageResource(R.drawable.btn_audio_pause_selector);
                }else{
                    btVideoStartPause.setImageResource(R.drawable.btn_audio_start_selector);
                }

            }else{
                btVideoStartPause.setImageResource(R.drawable.btn_audio_pause_selector);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showViewData() {
        try {

            tvArtist.setText(service.getArtist());
            tvName.setText(service.getName());

            seekbarAudio.setMax(service.getDuration());

            handler.sendEmptyMessage(PROGRESS);


        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.setAction("com.drawshirt.mediamobile_OPENAUDIO");
        bindService(intent,conn,BIND_AUTO_CREATE);
        startService(intent);
    }

    private void getData() {

        notificatoin = getIntent().getBooleanExtra("notification",false);

        if (!notificatoin){
            position = getIntent().getIntExtra("position",0);
        }


    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-08-12 17:33:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_audio_player);
        ivIcon = (ImageView)findViewById( R.id.iv_icon );
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable rocketAnimation = (AnimationDrawable) ivIcon.getBackground();
        rocketAnimation.start();
        tvArtist = (TextView)findViewById( R.id.tv_artist );
        tvName = (TextView)findViewById( R.id.tv_name );
        tvTime = (TextView)findViewById( R.id.tv_time );
        seekbarAudio = (SeekBar)findViewById( R.id.seekbar_audio );
        btSetMode = (ImageButton)findViewById( R.id.bt_setmode );
        btPre = (ImageButton)findViewById( R.id.bt_pre );
        btVideoStartPause = (ImageButton)findViewById( R.id.bt_video_start_pause );
        btNext = (ImageButton)findViewById( R.id.bt_next );
        btnLyrc = (ImageButton)findViewById( R.id.btn_lyrc );

        showLyricView = (ShowLyricView) findViewById(R.id.showLyricView);
        //baseVisualizerView=(BaseVisualizerView) findViewById(R.id.baseVisualizerView);

        btSetMode.setOnClickListener( this );
        btPre.setOnClickListener( this );
        btVideoStartPause.setOnClickListener( this );
        btNext.setOnClickListener( this );
        btnLyrc.setOnClickListener( this );

        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-08-12 17:33:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btSetMode ) {
            try {
                int playMode = service.getPlayMode();

                if (playMode== MusicPlayerService.REPEAT_NORMAL){
                    playMode=MusicPlayerService.REPEAT_SINGLE;
                    btSetMode.setImageResource(R.drawable.btn_single_selector);
                    Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();

                }else if (playMode==MusicPlayerService.REPEAT_SINGLE){
                    playMode=MusicPlayerService.REPEAT_ALL;
                    btSetMode.setImageResource(R.drawable.btn_repeat_selector);
                    Toast.makeText(this, "全部循环", Toast.LENGTH_SHORT).show();

                }else if (playMode==MusicPlayerService.REPEAT_ALL){
                    playMode=MusicPlayerService.REPEAT_NORMAL;
                    btSetMode.setImageResource(R.drawable.btn_order_selector);
                    Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();

                }else{
                    playMode=MusicPlayerService.REPEAT_NORMAL;
                    btSetMode.setImageResource(R.drawable.btn_order_selector);
                    Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();
                }

                service.setPlayMode(playMode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }


        } else if ( v == btPre ) {
            // Handle clicks for btPre
            try {
                service.prev();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if ( v == btVideoStartPause ) {
            try {
                if (service.isPlaying()){
                    service.pause();
                    btVideoStartPause.setImageResource(R.drawable.btn_audio_start_selector);
                }else{
                    service.start();
                    btVideoStartPause.setImageResource(R.drawable.btn_audio_pause_selector);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // Handle clicks for btVideoStartPause
        } else if ( v == btNext ) {
            // Handle clicks for btNext
            try {
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if ( v == btnLyrc ) {
            // Handle clicks for btnLyrc
        }
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);

        //        if (receiver!=null){
        //            unregisterReceiver(receiver);
        //            receiver=null;
        //        }

        if (conn!=null){
            unbindService(conn);
            conn=null;
        }

        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b){
                try {
                    service.seekTo(i);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
