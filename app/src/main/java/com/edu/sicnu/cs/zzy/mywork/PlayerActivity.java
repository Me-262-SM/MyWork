package com.edu.sicnu.cs.zzy.mywork;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    private ImageView imageView;
    private Intent intent,intent_service ;
    private SeekBar seekBar;
    private ArrayList<Music> musiclist = new ArrayList<>();
    int UPDATE = 0x101;
    private Handler handler;// 处理改变进度条事件
    private int currrntPosition = -1;
    private boolean isPaused = false;
    private boolean isFirst = false;
    private ImageButton imageButton_pause;
    TextView current_time,mount_time,music_name;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Mythread thread;
    private Animation mAnimation;
    MusicReceiver musicReceiver;
    final static String BroadMusic = "com.edu.sicnu.cs.zzy.receiver";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        sharedPreferences = getSharedPreferences("data",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        musicReceiver = new MusicReceiver(PlayerActivity.this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadMusic);
        registerReceiver(musicReceiver,intentFilter);

        imageView = findViewById(R.id.picture);
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.roraterepeat);
        imageView.startAnimation(mAnimation);

        music_name = findViewById(R.id.music_name);
        current_time = findViewById(R.id.current_time);
        mount_time = findViewById(R.id.mount_time);
        imageButton_pause = findViewById(R.id.imageButton_pause);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                int musicMax = MusicService.mediaPlayer.getDuration();//得到该首歌曲最长秒数
                int seekBarMax = seekBar.getMax();
                MusicService.mediaPlayer
                        .seekTo(musicMax * progress / seekBarMax);//跳到该曲该秒
                current_time.setText(msToM(musicMax * progress / seekBarMax));
            }
        });

        intent = getIntent();
        musiclist = (ArrayList<Music>) intent.getSerializableExtra("musiclist");
        final int position = intent.getIntExtra("number",0);
        currrntPosition = position;
        intent_service = new Intent(this,MusicService.class);
        intent_service.putExtra("position",position);
        startService(intent_service);

        if(currrntPosition != sharedPreferences.getInt("currrntPosition",-1)){
            isFirst = true;
        }

        //恢复数据
        if(!isFirst && sharedPreferences.getBoolean("isPaused",false)==true){
            isPaused=sharedPreferences.getBoolean("isPaused",true);
            imageButton_pause.setImageResource(android.R.drawable.ic_media_play);
            music_name.setText(sharedPreferences.getString("music_name",""));
            seekBar.setProgress(sharedPreferences.getInt("progress",0));
            current_time.setText(sharedPreferences.getString("current_time",""));
            mount_time.setText(sharedPreferences.getString("mount_time",""));
        }else{
            music_name.setText(sharedPreferences.getString("music_name",""));
        }

        thread = new Mythread();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //更新UI
                int mMax = MusicService.mediaPlayer.getDuration();//最大秒数
                mount_time.setText(msToM(mMax));
                //music_name.setText(musiclist.get(currrntPosition).getMusicName());
                if (msg.what == UPDATE) {
                    try {
                        seekBar.setProgress(msg.arg1);
                        current_time.setText(msToM(msg.arg2));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    seekBar.setProgress(0);
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(musicReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        editor.putString("current_time",current_time.getText().toString());
        editor.putString("mount_time",mount_time.getText().toString());
        editor.putInt("progress",seekBar.getProgress());
        editor.putInt("currrntPosition",currrntPosition);
        editor.putBoolean("isPaused",isPaused);
        if(music_name.getText().toString() != null && !music_name.getText().toString().equals("")){
            editor.putString("music_name",music_name.getText().toString());
        }
        editor.apply();

    }

    class Mythread extends Thread{
        @Override
        public void run() {
            super.run();
            int position, mMax, sMax;
            while (!Thread.currentThread().isInterrupted()) {
                if (MusicService.mediaPlayer != null && MusicService.mediaPlayer.isPlaying()) {
                    position = MusicService.getCurrentProgress();//得到当前歌曲播放进度(秒)
                    mMax = MusicService.mediaPlayer.getDuration();//最大秒数
                    sMax = seekBar.getMax();//seekBar最大值，算百分比
                    Message m = handler.obtainMessage();//获取一个Message
                    m.arg1 = position * sMax / mMax;//seekBar进度条的百分比
                    m.arg2 = position;
                    m.what = UPDATE;
                    handler.sendMessage(m);
                    //  handler.sendEmptyMessage(UPDATE);
                    try {
                        Thread.sleep(1000);// 每间隔1秒发送一次更新消息
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void stop(View view){
        intent.putExtra("operate",2);
        startService(intent);
    }

    public void pause(View view){
        if(isPaused){
            isPaused=false;
            imageView.startAnimation(mAnimation);
            imageButton_pause.setImageResource(android.R.drawable.ic_media_pause);
            Intent intent = new Intent(this,MusicService.class);
            intent.putExtra("operate",0);
            startService(intent);
        }else{
            isPaused=true;
            imageView.clearAnimation();
            imageButton_pause.setImageResource(android.R.drawable.ic_media_play);
            Intent intent = new Intent(this,MusicService.class);
            intent.putExtra("operate",3);
            startService(intent);
        }
    }

    public void go(View view){
        intent.putExtra("operate",0);
        startService(intent);
    }

    public void next(View view){
        imageView.startAnimation(mAnimation);
//        currrntPosition++;
//        currrntPosition = currrntPosition % musiclist.size();
//        music_name.setText(musiclist.get(currrntPosition).getMusicName());
        Intent intent = new Intent(this,MusicService.class);
        intent.putExtra("operate",4);
        startService(intent);
    }

    public void prev(View view){
        imageView.startAnimation(mAnimation);
//        currrntPosition--;
//        if(currrntPosition<0) currrntPosition = musiclist.size()-1;
//        music_name.setText(musiclist.get(currrntPosition).getMusicName());
        Intent intent = new Intent(this,MusicService.class);
        intent.putExtra("operate",5);
        startService(intent);
    }

    //将毫秒转换成
    public static String msToM(int ms) {
        int seconds = ms / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;

        String m = null;
        String s = null;

        //if (minutes == 0 && seconds == 0) seconds = 1;

        if (minutes < 10){
            m = "0" + minutes;
        }
        else{
            m = "" + minutes;
        }

        if (seconds < 10){
            s = "0" + seconds;
        }
        else{
            s = "" + seconds;
        }

        return m + ":" + s;
    }
}
