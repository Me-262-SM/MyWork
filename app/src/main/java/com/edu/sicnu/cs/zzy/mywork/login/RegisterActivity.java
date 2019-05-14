package com.edu.sicnu.cs.zzy.mywork.login;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.edu.sicnu.cs.zzy.mywork.MainActivity;
import com.edu.sicnu.cs.zzy.mywork.R;

public class RegisterActivity extends AppCompatActivity {
    private MySQLiteHepler mySQLiteHepler;
    private SQLiteDatabase db;
    private EditText rg_editText_usr,rg_editText_psw,rg_editText_psw_repeat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //改变状态栏颜色
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(this.getResources().getColor(R.color.colorBar));

        rg_editText_usr = findViewById(R.id.rg_editText_usr);
        rg_editText_psw = findViewById(R.id.rg_editText_psw);
        rg_editText_psw_repeat = findViewById(R.id.rg_editText_psw_repeat);

        mySQLiteHepler = new MySQLiteHepler(this,"db_test",null,1);
        db = mySQLiteHepler.getWritableDatabase();

    }
    public void btn_regist(View view){
        String user_name = rg_editText_usr.getText().toString();
        String user_password = rg_editText_psw.getText().toString();
        String user_password_repeat = rg_editText_psw_repeat.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("错误信息");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });

        if(user_name.equals("") || user_name == null){
            builder.setMessage("用户名不能为空！！！");
            builder.show();
        }else if(user_password.equals("") || user_password == null){
            builder.setMessage("密码不能为空！！！");
            builder.show();
        }else if(user_password_repeat.equals("") || user_password_repeat == null){
            builder.setMessage("请再次输入密码！！！");
            builder.show();
        }else if(!user_password.equals(user_password_repeat)){
            builder.setMessage("两次输入的密码不一致！！！");
            builder.show();
        }else if (isExist(user_name)){
            builder.setMessage("用户名已存在！！！");
            builder.show();
        }else{
            Add(user_name,user_password);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
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

    //增加用户信息
    public void Add(String userName,String userPassword){
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",userName);
        contentValues.put("psw",userPassword);
        db.insert(MySQLiteHepler.tableName,null,contentValues);
    }
}
