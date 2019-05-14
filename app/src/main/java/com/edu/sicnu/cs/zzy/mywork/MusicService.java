package com.edu.sicnu.cs.zzy.mywork;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener{
    int current = -1;
    boolean isfirst;
    NotificationManager manager;
    RemoteViews mRemoteViews;
    NotificationCompat.Builder builder;
    ArrayList<Music> musiclist = new ArrayList<>();
    static MediaPlayer mediaPlayer;
    final static String myChannel = "mymusicplayer";

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        getMusiclist();
        //sendNotification();
        //创建一个RemoteView实例
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_window_layout);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //由于每次都要先获取position,可能导致发送pause等命令时拿不到"position"
        int position = intent.getIntExtra("position",-1);
        if(position == -1){
            //position为-1时代表没有接受到PlayerActivity发来的信息，说明
            position= current;
        }

        if(position != current){
            isfirst = true;
        }
        initRemoteView();
        initNotification();
        //第一次进入PlayerActivity时直接播放
        if(isfirst){
            isfirst = false;
            current = position;
            playNew();
        }else {
            //之后可以接受其他命令
            int operate = intent.getIntExtra("operate",-1);
            switch (operate){
                case 0:
                    goPlay();
                    break;
                case 1:
                    play();
                    break;
                case 2:
                    stop();
                    break;
                case 3:
                    pause();
                    break;
                case 4:
                    next();
                    break;
                case 5:
                    prev();
                    break;
            }
        }

        String action = intent.getAction();
        int intExtra = intent.getIntExtra("index",-1);
        //校验action
        if(TextUtils.equals(action, "ntf")){
            switch (intExtra){
                case 1:
                    playNew();
                    break;
                case 4:
                    next();
                    break;
                case 5:
                    prev();
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

//    public void sendNotification(){
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        //判断安卓版本是否大于API-26
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(myChannel,"my music notification", NotificationManager.IMPORTANCE_DEFAULT);
//            manager.createNotificationChannel(channel);
//
//            builder = new NotificationCompat.Builder(this,myChannel);
//            builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle("MyMusicDemo:");
//
//            //设置延时意图，点击通知进入界面
//            Intent intent = new Intent(this,MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//            builder.setContentIntent(pendingIntent);
//            startForeground(1,builder.build());
//        }
//    }

    public void getMusiclist(){
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,null);
        while(cursor.moveToNext()){
            String musicName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String musicPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            String musicArtist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            musiclist.add(new Music(musicName,musicPath,musicArtist,duration));
        }
    }

    private void playNew() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musiclist.get(current).getMusicPath());
            mediaPlayer.prepareAsync(); //异步调用，防止阻塞
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer arg0) {
                    //如果当前歌曲播放完毕,自动播放下一首.
                    next();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void play(){
        playNew();
    }

    private void stop(){
        if(mediaPlayer.isPlaying()) mediaPlayer.stop();
    }

    private void pause(){
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    //继续播放
    public  void goPlay(){
        int position = getCurrentProgress();
        mediaPlayer.seekTo(position);//设置当前MediaPlayer的播放位置，单位是毫秒。
        try {
            mediaPlayer.prepare();//  同步的方式装载流媒体文件。
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    // 获取当前进度
    public static int getCurrentProgress() {
        if (mediaPlayer != null & mediaPlayer.isPlaying()) {
            return mediaPlayer.getCurrentPosition();
        } else if (mediaPlayer != null & (!mediaPlayer.isPlaying())) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    private void next(){
        current++;
        current = current % musiclist.size();
        playNew();
    }

    private void prev(){
        current--;
        if(current<0) current = musiclist.size()-1;
        playNew();
    }

    private void initRemoteView() {
        //实例化一个指向MusicService的intent
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction("ntf");

        //设置play按钮的点击事件
        intent.putExtra("index", 1);
        PendingIntent pendingIntent_play = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.ntf_btn_play, pendingIntent_play);

        //设置next按钮的点击事件
        intent.putExtra("index", 4);
        PendingIntent pendingIntent_next = PendingIntent.getService(this, 4, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.ntf_btn_next, pendingIntent_next);

        //设置prev按钮的点击事件
        intent.putExtra("index", 5);
        PendingIntent pendingIntent_prev = PendingIntent.getService(this, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.ntf_btn_prev, pendingIntent_prev);
    }

    private void initNotification() {
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //判断安卓版本是否大于API-26
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(myChannel, "my music notification", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{0});
            channel.setSound(null, null);
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, myChannel);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            //将remoteView设置进去
            builder.setContent(mRemoteViews);
            //获取NotificationManager实例
        }


    }


    //设置mediaplayer的回调方法
    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();

        Intent intent = new Intent(PlayerActivity.BroadMusic);
        intent.putExtra("name",musiclist.get(current).getMusicName());
        sendBroadcast(intent);


        mRemoteViews.setTextViewText(R.id.ntf_music__name, musiclist.get(current).getMusicName());
        mRemoteViews.setTextViewText(R.id.ntf_singer_name, musiclist.get(current).getMusicArtist());
        startForeground(10, builder.build());
    }
}
