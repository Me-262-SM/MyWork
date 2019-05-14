package com.edu.sicnu.cs.zzy.mywork.login;

import android.Manifest;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.edu.sicnu.cs.zzy.mywork.ExitReceiver;
import com.edu.sicnu.cs.zzy.mywork.MainActivity;
import com.edu.sicnu.cs.zzy.mywork.R;

public class LoginActivity extends AppCompatActivity {
    private MySQLiteHepler mySQLiteHepler;
    private SQLiteDatabase db;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private EditText editText_usr,editText_psw;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Marked:改变状态栏颜色
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorBar));


        sharedPreferences = getSharedPreferences("login_status",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(sharedPreferences.getString("islogin",null)==null){
            //什么也不做
        }else if(sharedPreferences.getString("islogin","").equals("true")){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }else{
            //什么也不做
        }

        //加载页面
        setContentView(R.layout.activity_login);

        //申请权限
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }

        editText_usr = findViewById(R.id.editText_usr);
        editText_psw = findViewById(R.id.editText_psw);


        mySQLiteHepler = new MySQLiteHepler(this,"db_test",null,1);
        db = mySQLiteHepler.getWritableDatabase();
    }
    public void btn_login(View view){
        String user_name = editText_usr.getText().toString();
        String user_password = editText_psw.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("错误信息");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });

        if(user_name.equals("") || user_name == null){
            //
            builder.setMessage("用户名不能为空！！！");
            builder.show();
        }else if(user_password.equals("") || user_password == null){
            //
            builder.setMessage("密码不能为空！！！");
            builder.show();
        } else if(!isExist(user_name)){
            builder.setMessage("用户名不存在！！！");
            builder.show();
        }else if (!isMatched(user_name,user_password)){
            builder.setMessage("用户名与密码不匹配！！！");
            builder.show();
        }else {
            editor.putString("islogin","true");
            editor.putString("usr",user_name);
            editor.apply();
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }

    public void btn_register(View view){
        intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    //没有申请则一直请求
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 0) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
            }
        }
    }

    //判断用户名是否已经存在
    private boolean isExist(String userName){
        Cursor cursor = db.rawQuery("select * from LoginInfo where name=?",new String[]{userName});
        while (cursor.moveToNext()){
            return true;
        }
        return false;
    }

    //判断用户名密码是否匹配
    private boolean isMatched(String userName,String userPassword){
        Cursor cursor = db.rawQuery("select * from LoginInfo where name=?",new String[]{userName});
        if(cursor.moveToFirst()){
            String C_psw = cursor.getString(cursor.getColumnIndex("psw"));
            if (C_psw.equals(userPassword)){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }
}
