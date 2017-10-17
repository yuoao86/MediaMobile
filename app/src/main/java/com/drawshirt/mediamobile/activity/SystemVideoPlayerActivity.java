package com.drawshirt.mediamobile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.bean.LocalMediaItem;
import com.drawshirt.mediamobile.utils.UIUtils;
import com.drawshirt.mediamobile.utils.Utils;
import com.drawshirt.mediamobile.view.VideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SystemVideoPlayerActivity extends Activity implements View.OnClickListener {

    private static final int VIDEO_PROGRESS = 1;
    private static final int HIDE_CONTROLLER = 2;
    private static final int FULL_SCREEN = 1;
    private static final int DEFAULT_SCREEN = 2;
    private static final int SHOW_SPEED = 3;
    private VideoView videoView;


    private Utils utils;

    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemTime;
    private ImageButton btVoice;
    private SeekBar seekbarVoice;
    private ImageButton btSwitchPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrentTime;
    private SeekBar seekbarVideo;
    private TextView tvTotalTime;
    private ImageButton btExit;
    private ImageButton btPre;
    private ImageButton btVideoStartPause;
    private ImageButton btNext;
    private ImageButton btnVideoSiwchScreen;
    private RelativeLayout video_controller;
    private LinearLayout loading_buffer;


    private BroadcastReceiver receiver;
    private AudioManager am;
    private int maxVolume;
    private int currentVolume;
    private boolean isMute = false;
    private ArrayList<LocalMediaItem> mediaItems;
    private int position;
    private Uri uri;
    private GestureDetector detector;
    private boolean isShowController = false;


    private int screenWidth;
    private int screenHeight;

    private int videoWidth;
    private int videoHeight;
    private boolean isFullScreen = false;
    private int mVolume;
    private float startY;
    private float touchRang;

    /**
     * 是否是网络uri
     */
    private boolean isNetUri;

    private boolean isUseSystem = true;

    private int preCurrentPosition;
    private TextView tv_buffer_netspeed;
    private TextView tv_loading_netspeed;
    private LinearLayout ll_loading;

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        findViews();
        setListener();


        getData();
        setData();


    }

    private void setData() {
        if (mediaItems != null && mediaItems.size() > 0) {
            setButtonStuat();

            LocalMediaItem mediaItem = mediaItems.get(position);
            isNetUri = UIUtils.isNetUri(mediaItem.getData());
            //System.out.println("---------------------uri===="+mediaItem.getData());
            tvName.setText(mediaItem.getName());
            videoView.setVideoPath(mediaItem.getData());
        } else {
            if (uri != null) {
                String uriStr=uri.toString();
                isNetUri = UIUtils.isNetUri(uriStr);

                //本地视频地址是从别人的其它应用传过来的
                if(uriStr.toLowerCase().startsWith("file:///")){
                    String newUriStr = uriStr.substring(7);
                    uri=Uri.parse(newUriStr);
                    //System.out.println("---------------------22222uri===="+uri);
                }

                videoView.setVideoURI(uri);
                setButtonEnable(false);
            }
        }

        if (isNetUri) {
            handler.sendEmptyMessage(SHOW_SPEED);
        }
        hideVideoController();

    }

    private void setButtonStuat() {
        if (!videoView.isPlaying()) {
            btVideoStartPause.setImageResource(R.drawable.btn_pause_selector);
        }
        if (mediaItems.size() == 1) {
            setButtonEnable(false);
        } else if (mediaItems.size() == 2) {
            if (position == 0) {
                btPre.setEnabled(false);
                btPre.setImageResource(R.drawable.btn_pre_gray);

                btNext.setEnabled(true);
                btNext.setImageResource(R.drawable.btn_next_selector);
            } else {
                btPre.setEnabled(true);
                btPre.setImageResource(R.drawable.btn_pre_selector);

                btNext.setEnabled(false);
                btNext.setImageResource(R.drawable.btn_next_gray);
            }
        } else {
            if (position == 0) {
                btPre.setEnabled(false);
                btPre.setImageResource(R.drawable.btn_pre_gray);
            } else if (position == mediaItems.size() - 1) {
                btNext.setEnabled(false);
                btNext.setImageResource(R.drawable.btn_next_gray);
            } else {
                setButtonEnable(true);
            }
        }
    }

    private void setButtonEnable(boolean isEnable) {
        btPre.setEnabled(isEnable);
        btNext.setEnabled(isEnable);
        if (isEnable) {
            btPre.setImageResource(R.drawable.btn_pre_selector);
            btNext.setImageResource(R.drawable.btn_next_selector);
        } else {
            btPre.setImageResource(R.drawable.btn_pre_gray);
            btNext.setImageResource(R.drawable.btn_next_gray);
        }


    }


    private void getData() {
        Intent intent = getIntent();
        mediaItems = (ArrayList<LocalMediaItem>) intent.getSerializableExtra("videolist");
        position = intent.getIntExtra("position", 0);
        uri = intent.getData();
        //System.out.println("---------------------uri===="+uri);
        //Log.d("TAG","---------------------uri===="+uri);
    }

    private void initData() {

        utils = new Utils();

        /**
         * 电量变化广播接收器
         */
        receiver = new BatteryReceiver();


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        /**
         * 注册电量变化广播
         */
        registerReceiver(receiver, intentFilter);


        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);


        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                if (isShowController) {
                    handler.removeMessages(HIDE_CONTROLLER);
                    hideVideoController();
                } else {
                    showVideoCOntroller();
                    handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 4000);
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                //Toast.makeText(SystemVideoPlayerActivity.this, "我被长按了", Toast.LENGTH_SHORT).show();
                startAndPauseVideo();
                super.onLongPress(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                setScreenFullAndDefault();
                //Toast.makeText(SystemVideoPlayerActivity.this, "我被双击了", Toast.LENGTH_SHORT).show();
                return super.onDoubleTap(e);
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;


    }

    private void hideVideoController() {
        isShowController = false;
        video_controller.setVisibility(View.INVISIBLE);
    }


    private void showVideoCOntroller() {
        isShowController = true;
        video_controller.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mVolume = currentVolume;
                startY = event.getY();
                touchRang = Math.min(screenHeight, screenWidth);//screenHeight

                handler.removeMessages(HIDE_CONTROLLER);

                break;
            case MotionEvent.ACTION_MOVE:

                float endY = event.getY();
                float distanceY = startY - endY;

                float delta = (distanceY * maxVolume) / touchRang;
                int volume = (int) Math.min(Math.max(mVolume + delta, 0), maxVolume);

                if (delta != 0) {
                    isMute = false;
                    updateVolume(volume, isMute);
                }


                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 4000);

                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取系统时间
     *
     * @return 格式化的时间
     */
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case VIDEO_PROGRESS:
                    int currentPosition = videoView.getCurrentPosition();
                    seekbarVideo.setProgress(currentPosition);


                    int currentTime = videoView.getCurrentPosition();
                    tvCurrentTime.setText(UIUtils.stringForTime(currentTime));

                    tvSystemTime.setText(getSystemTime());

                    if (isNetUri) {
                        int buffer = videoView.getBufferPercentage();
                        int totalBuffer = buffer * seekbarVideo.getMax();
                        int secondaryProgress = totalBuffer / 100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);

                    } else {
                        seekbarVideo.setSecondaryProgress(0);
                    }

                    if (!isUseSystem) {
                        if (videoView.isPlaying()) {

                            int buffer = currentPosition - preCurrentPosition;

                            if (buffer < 500) {
                                loading_buffer.setVisibility(View.VISIBLE);
                            } else {
                                loading_buffer.setVisibility(View.GONE);
                            }
                        }
                        preCurrentPosition = currentPosition;
                    }


                    handler.removeMessages(VIDEO_PROGRESS);
                    handler.sendEmptyMessageDelayed(VIDEO_PROGRESS, 1000);


                    break;

                case HIDE_CONTROLLER:
                    hideVideoController();
                    removeMessages(HIDE_CONTROLLER);
                    break;

                case SHOW_SPEED:
                    String netSpeed = getNetSpeed(SystemVideoPlayerActivity.this);
                    tv_buffer_netspeed.setText("缓存中..." + netSpeed);
                    tv_loading_netspeed.setText("玩命加载中..." + netSpeed);


                    //2.每两秒更新一次
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED, 2000);


                    break;
            }

        }
    };

    public String getNetSpeed(Context mContext) {
        String netSpeed = "0 kb/s";
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(mContext.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB;
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        netSpeed = String.valueOf(speed) + " kb/s";
        return netSpeed;
    }

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-08-06 11:54:37 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
        loading_buffer = (LinearLayout) findViewById(R.id.loading_buffer);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        videoView = (VideoView) findViewById(R.id.videoView);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemTime = (TextView) findViewById(R.id.tv_system_time);
        btVoice = (ImageButton) findViewById(R.id.bt_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btSwitchPlayer = (ImageButton) findViewById(R.id.bt_switch_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrentTime = (TextView) findViewById(R.id.tv_current_time);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvTotalTime = (TextView) findViewById(R.id.tv_total_time);
        btExit = (ImageButton) findViewById(R.id.bt_exit);
        btPre = (ImageButton) findViewById(R.id.bt_pre);
        btVideoStartPause = (ImageButton) findViewById(R.id.bt_video_start_pause);
        btNext = (ImageButton) findViewById(R.id.bt_next);
        btnVideoSiwchScreen = (ImageButton) findViewById(R.id.btn_video_siwch_screen);

        video_controller = (RelativeLayout) findViewById(R.id.video_controller);

        tv_buffer_netspeed = (TextView) findViewById(R.id.tv_buffer_netspeed);
        tv_loading_netspeed = (TextView) findViewById(R.id.tv_loading_netspeed);

        btVoice.setOnClickListener(this);
        btSwitchPlayer.setOnClickListener(this);
        btExit.setOnClickListener(this);
        btPre.setOnClickListener(this);
        btVideoStartPause.setOnClickListener(this);
        btNext.setOnClickListener(this);
        btnVideoSiwchScreen.setOnClickListener(this);

        seekbarVoice.setMax(maxVolume);
        seekbarVoice.setProgress(currentVolume);


    }


    private void setListener() {
        videoView.setOnPreparedListener(new MyOnPreparedListener());
        videoView.setOnErrorListener(new MyonErrorListener());
        videoView.setOnCompletionListener(new MyOnComletionListener());
        seekbarVideo.setOnSeekBarChangeListener(new VideoSeekBarChangeListener());
        seekbarVoice.setOnSeekBarChangeListener(new VolumeSeekBarChangeListener());

        if (isUseSystem) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.setOnInfoListener(new MyOnInfoListener());
            }
        }


    }

    private class MyOnInfoListener implements MediaPlayer.OnInfoListener {
        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
            switch (i) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    loading_buffer.setVisibility(View.VISIBLE);
                    //Toast.makeText(SystemVideoPlayerActivity.this,"开始缓冲了", Toast.LENGTH_SHORT).show();
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    loading_buffer.setVisibility(View.GONE);
                    //Toast.makeText(SystemVideoPlayerActivity.this,"缓冲结束了", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    }


    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-08-06 11:54:37 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btVoice) {
            // Handle clicks for btVoice
            isMute = !isMute;
            updateVolume(currentVolume, isMute);

        } else if (v == btSwitchPlayer) {
            // Handle clicks for btSwitchPlayer
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("系统播放器提示");
            builder.setMessage("当您播放视频，有声音没有画面的时候，请切换万能播放器播放");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startVitamioPlayer();
                }
            });
            builder.show();

        } else if (v == btExit) {
            // Handle clicks for btExit

            finish();
        } else if (v == btPre) {
            // Handle clicks for btPre
            playPreVideo();

        } else if (v == btNext) {
            // Handle clicks for btNext
            playNextVideo();

        } else if (v == btVideoStartPause) {
            // Handle clicks for btVideoStartPause
            startAndPauseVideo();

        } else if (v == btnVideoSiwchScreen) {
            // Handle clicks for btnVideoSiwchScreen
            setScreenFullAndDefault();
        }
    }

    private void playNextVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            if (position < mediaItems.size() - 1) {
                position++;
                ll_loading.setVisibility(View.VISIBLE);
                LocalMediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = UIUtils.isNetUri(mediaItem.getData());
                videoView.setVideoPath(mediaItems.get(position).getData());
                setButtonStuat();

            }
        }
    }

    private void playPreVideo() {
        if (mediaItems != null && mediaItems.size() > 0) {
            if (position > 0) {
                position--;
                ll_loading.setVisibility(View.VISIBLE);
                LocalMediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = UIUtils.isNetUri(mediaItem.getData());
                videoView.setVideoPath(mediaItems.get(position).getData());
                setButtonStuat();

            }
        }

    }


    private void startAndPauseVideo() {
        if (videoView.isPlaying()) {
            videoView.pause();
            btVideoStartPause.setImageResource(R.drawable.btn_start_selector);
        } else {
            videoView.start();
            btVideoStartPause.setImageResource(R.drawable.btn_pause_selector);
        }
    }

    private void setScreenFullAndDefault() {
        if (isFullScreen) {
            setVideoType(DEFAULT_SCREEN);

        } else {
            setVideoType(FULL_SCREEN);

        }
    }

    private void setVideoType(int type) {
        switch (type) {
            case FULL_SCREEN:
                videoView.onSetVideSize(screenWidth, screenHeight);

                btnVideoSiwchScreen.setImageResource(R.drawable.btn_defaut_screen_selector);
                isFullScreen = true;
                break;
            case DEFAULT_SCREEN:

                //视频真实的宽和高
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;

                //屏幕的宽和高
                int width = screenWidth;
                int height = screenHeight;

                // for compatibility, we adjust size based on aspect ratio
                if (mVideoWidth * height < width * mVideoHeight) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }

                videoView.onSetVideSize(width, height);

                btnVideoSiwchScreen.setImageResource(R.drawable.btn_full_screen_selector);
                isFullScreen = false;
                break;
        }

    }


    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {

            videoView.start();

            int totalTime = videoView.getDuration();

            tvTotalTime.setText(UIUtils.stringForTime(totalTime));

            seekbarVideo.setMax(totalTime);

            handler.sendEmptyMessage(VIDEO_PROGRESS);


            videoWidth = mediaPlayer.getVideoWidth();
            videoHeight = mediaPlayer.getVideoHeight();

            ll_loading.setVisibility(View.GONE);


            setVideoType(DEFAULT_SCREEN);


        }
    }


    private class MyonErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            //Toast.makeText(SystemVideoPlayerActivity.this, "播放错误....", Toast.LENGTH_SHORT).show();

            startVitamioPlayer();

            return false;
        }
    }

    private void startVitamioPlayer() {

        if (videoView != null) {
            videoView.stopPlayback();
        }

        Intent intent = new Intent(this, VitamioVideoPlayerActivity.class);

        if (mediaItems != null && mediaItems.size() > 0) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("videolist", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
        } else if (uri != null) {
            intent.setData(uri);
        }
        startActivity(intent);
        finish();


    }


    private class MyOnComletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //Toast.makeText(SystemVideoPlayerActivity.this, "视频播放完毕....", Toast.LENGTH_SHORT).show();
            playNextVideo();
        }
    }


    private class VideoSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                videoView.seekTo(i);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_CONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 4000);
        }
    }


    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int level = intent.getIntExtra("level", 0);
            if (level <= 0) {
                ivBattery.setImageResource(R.drawable.ic_battery_0);
            } else if (level <= 10) {
                ivBattery.setImageResource(R.drawable.ic_battery_10);
            } else if (level <= 20) {
                ivBattery.setImageResource(R.drawable.ic_battery_20);
            } else if (level <= 40) {
                ivBattery.setImageResource(R.drawable.ic_battery_40);
            } else if (level <= 60) {
                ivBattery.setImageResource(R.drawable.ic_battery_60);
            } else if (level <= 80) {
                ivBattery.setImageResource(R.drawable.ic_battery_80);
            } else if (level <= 100) {
                ivBattery.setImageResource(R.drawable.ic_battery_100);
            } else {
                ivBattery.setImageResource(R.drawable.ic_battery_100);
            }

        }
    }

    @Override
    protected void onDestroy() {


        handler.removeCallbacksAndMessages(null);

        unregisterReceiver(receiver);  //注销电量广播
        super.onDestroy();
    }


    private void updateVolume(int progress, boolean isMute) {
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);
        } else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekbarVoice.setProgress(progress);
            currentVolume = progress;
        }

    }

    private class VolumeSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                if (i > 0) {
                    isMute = false;
                } else {
                    isMute = true;
                }
                updateVolume(i, isMute);

            }


        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_CONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 4000);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                currentVolume++;
                updateVolume(currentVolume, false);
                handler.removeMessages(HIDE_CONTROLLER);
                handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 4000);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                currentVolume--;
                updateVolume(currentVolume, false);
                handler.removeMessages(HIDE_CONTROLLER);
                handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 4000);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


}



