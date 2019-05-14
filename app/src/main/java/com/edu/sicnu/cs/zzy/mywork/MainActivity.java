package com.edu.sicnu.cs.zzy.mywork;


import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.edu.sicnu.cs.zzy.mywork.Fragment.Fragment_1;
import com.edu.sicnu.cs.zzy.mywork.Fragment.Fragment_2;
import com.edu.sicnu.cs.zzy.mywork.Fragment.Fragment_3;
import com.edu.sicnu.cs.zzy.mywork.login.ExitLoginActivity;
import com.edu.sicnu.cs.zzy.mywork.login.LoginActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener{
    private BottomNavigationView bottomNavigationView;
    private Fragment fragment1,fragment2,fragment3;
    private Fragment currentFragment=null;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ImageButton imageButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean isQuit = false;
    private long firstTime = 0;
    FragmentManager fragmentManager;
    ArrayList<Music> musiclist = new ArrayList<>();

    //设置bottomNavigationView的点击方法
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_info:
                switchFragment(fragment1);
                break;
            case R.id.navigation_music:
                switchFragment(fragment2);
                break;
            case R.id.navigation_mine:
                switchFragment(fragment3);
                break;
        }

        navigationView.setCheckedItem(item.getItemId());
//        MenuItem menuItem = bottomNavigationView.getMenu().findItem(item.getItemId());
//        menuItem.setChecked(true);  //当侧边栏选中菜单项跳转时使底部导航栏状态也随之改变
        drawerLayout.closeDrawers();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Marked:改变状态栏颜色
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorRed));


        //获取音乐列表
        getMusiclist();

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_stat_name_mine);
        }

        imageButton = findViewById(R.id.header_btn);
        sharedPreferences = getSharedPreferences("login_status",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_music);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        navigationView = findViewById(R.id.navigationView);
        navigationView.setCheckedItem(R.id.navigation_music);
        navigationView.setNavigationItemSelectedListener(this);

        //实例化fragment
        fragment1 = new Fragment_1();
        fragment2 = new Fragment_2();
        fragment3 = new Fragment_3();

        switchFragment(fragment2);  //将第二个fragment放在前面


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.navgation,menu);
//        return true;
//    }

    public void btn_head(View view){
        if(sharedPreferences.getString("islogin","").equals("false")) {
            //跳转到登录界面
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }else{
            //跳转到有关是否注销登录的界面
            Intent intent = new Intent(this, ExitLoginActivity.class);
            startActivity(intent);
        }
    }

    public void switchFragment(Fragment fragment){
        Bundle bundle = new Bundle();
        if (currentFragment != fragment){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if(currentFragment!=null){
                transaction.hide(currentFragment);    //将原先的fragment隐藏
            }
            currentFragment = fragment; //替换当前fragment
            if(!fragment.isAdded()){
                transaction.add(R.id.frame,fragment);   //如未加入则加入
            }
            if(fragment == fragment2){
                bundle.putSerializable("array",musiclist);
                fragment.setArguments(bundle);
            }
            transaction.show(fragment);
            transaction.commit();
        }
    }

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

    @Override
    public void onBackPressed() {
        if (!isQuit) {
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            isQuit = true;

            //这段代码意思是,在两秒钟之后isQuit会变成false
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        isQuit = false;
                    }
                }
            }).start();
        } else {
            System.exit(0);
        }
    }
}