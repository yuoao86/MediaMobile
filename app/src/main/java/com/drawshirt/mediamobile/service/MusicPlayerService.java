package com.drawshirt.mediamobile.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;

import com.drawshirt.mediamobile.IMusicPlayerService;
import com.drawshirt.mediamobile.R;
import com.drawshirt.mediamobile.activity.AudioPlayerActivity;
import com.drawshirt.mediamobile.bean.LocalMediaItem;
import com.drawshirt.mediamobile.utils.CacheUtils;
import com.drawshirt.mediamobile.utils.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerService extends Service {


    public static final String OPENAUDIO = "com.drawshirt.mediamobile_OPENAUDIO";

    private ArrayList<LocalMediaItem> mediaItems;
    private LocalMediaItem mediaItem;
    private MediaPlayer mediaPlayer;
    private int position;
    private NotificationManager manager;


    public static final int REPEAT_NORMAL = 1;
    public static final int REPEAT_SINGLE = 2;
    public static final int REPEAT_ALL = 3;
    private int playMode=REPEAT_NORMAL;


    @Override
    public void onCreate() {
        super.onCreate();

        playMode= CacheUtils.getSharedMode(this,"playmode");
        getDataFromLocal();

    }

    private void getDataFromLocal() {

        new Thread(){
            @Override
            public void run() {
                super.run();
                mediaItems=new ArrayList<>();
                ContentResolver resolver = getContentResolver();
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
                            mediaItem.setName(cursor.getString(0));
                            mediaItem.setDuration(cursor.getLong(1));
                            mediaItem.setData(cursor.getString(2));
                            mediaItem.setSize(cursor.getLong(3));
                            mediaItem.setArtist(cursor.getString(4));
                            mediaItems.add(mediaItem);
                        }
                    }

                }
                cursor.close();
            }
        }.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }


    private IMusicPlayerService.Stub stub=new IMusicPlayerService.Stub(){

        MusicPlayerService service=MusicPlayerService.this;

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {
            service.stop();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public void prev() throws RemoteException {
            service.prev();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            service.setPlayMode(playMode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            mediaPlayer.seekTo(position);
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return mediaPlayer.getAudioSessionId();
        }
    };



    private void openAudio(int position){
        this.position=position;
        if (mediaItems!=null&&mediaItems.size()>0){
            mediaItem=mediaItems.get(position);

            if (mediaPlayer!=null){

                mediaPlayer.reset();
            }

            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());
                mediaPlayer.setDataSource(mediaItem.getData());
                mediaPlayer.prepareAsync();

                if (playMode==REPEAT_SINGLE){
                    mediaPlayer.setLooping(true);
                }else {
                    mediaPlayer.setLooping(false);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            LogUtil.d("还没有找到数据！");
        }



    }

    private void start(){
        mediaPlayer.start();
        sendNotification();
    }

    private void pause(){
        mediaPlayer.pause();
        manager.cancel(1);
    }

    private void stop(){

    }

    private int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    private int getDuration(){
        return mediaPlayer.getDuration();
    }

    private String getArtist(){
        return mediaItem.getArtist();
    }

    private String getName(){
        return mediaItem.getName();
    }

    private String getAudioPath(){
        return mediaItem.getData();
    }


    private void prev(){
        setPrevPosition();
        openPrevAudio();
    }

    private void openPrevAudio() {
        int playMode=getPlayMode();

        if (playMode==REPEAT_NORMAL){
            if (position>=0){
                openAudio(position);
            }else {
                position=0;
            }

        }else if (playMode==REPEAT_SINGLE){
            openAudio(position);

        }else if (playMode==REPEAT_ALL){
            openAudio(position);
        }else{
            if (position>=0){
                openAudio(position);
            }else {
                position=0;
            }
        }
    }

    private void setPrevPosition() {
        int playMode=getPlayMode();

        if (playMode==REPEAT_NORMAL){
            position--;

        }else if (playMode==REPEAT_SINGLE){
            position--;
            if (position<0){
                position=mediaItems.size()-1;
            }

        }else if (playMode==REPEAT_ALL){
            position--;
            if (position<0){
                position=mediaItems.size()-1;
            }
        }else{
            position--;
        }
    }

    private void next(){
        setNextPosition();
        openNextAudio();
    }

    private void openNextAudio() {
        int playMode=getPlayMode();

        if (playMode==REPEAT_NORMAL){
            if (position<mediaItems.size()){
                openAudio(position);
            }else {
                position=mediaItems.size()-1;
            }

        }else if (playMode==REPEAT_SINGLE){
            openAudio(position);

        }else if (playMode==REPEAT_ALL){
            openAudio(position);
        }else{
            if (position<mediaItems.size()){
                openAudio(position);
            }else {
                position=mediaItems.size()-1;
            }
        }
    }

    private void setNextPosition() {
        int playMode=getPlayMode();

        if (playMode==REPEAT_NORMAL){
            position++;

        }else if (playMode==REPEAT_SINGLE){
            position++;
            if (position>=mediaItems.size()){
                position=0;
            }

        }else if (playMode==REPEAT_ALL){
            position++;
            if (position>=mediaItems.size()){
                position=0;
            }
        }else{
            position++;
        }

    }

    private void setPlayMode(int playMode){
        this.playMode=playMode;
        CacheUtils.putSharedMode(this,"playmode",playMode);
        if (playMode==REPEAT_SINGLE){
            mediaPlayer.setLooping(true);
        }else {
            mediaPlayer.setLooping(false);
        }
    }

    private int getPlayMode(){
        return playMode;
    }

    private boolean isPlaying(){

        return mediaPlayer.isPlaying();
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {

            //onSendBroadcast(OPENAUDIO);
            EventBus.getDefault().post(mediaItem);
            start();

        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification() {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent=new Intent(this, AudioPlayerActivity.class);
        intent.putExtra("notification",true);
        PendingIntent pi=PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification= new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("播播影视")
                .setContentText("正在播放："+getName())
                .setContentIntent(pi)
                .build();
        manager.notify(1,notification);
    }

    private void onSendBroadcast(String action) {
        Intent intent=new Intent(action);
        sendBroadcast(intent);
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

            next();
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            next();
            return true;
        }
    }
}
