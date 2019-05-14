package com.edu.sicnu.cs.zzy.mywork.login;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.edu.sicnu.cs.zzy.mywork.R;

public class ExitLoginActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_login);
        //Marked:改变状态栏颜色
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorRed));

        sharedPreferences = getSharedPreferences("login_status",MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public void btn_exit(View view){
        editor.putString("islogin","false");
        editor.apply();
        finish();
    }
}
