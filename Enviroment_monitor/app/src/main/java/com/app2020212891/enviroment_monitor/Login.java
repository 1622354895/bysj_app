package com.app2020212891.enviroment_monitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {
    private Button mbtn1;
    private Button mbtn2;
    private EditText editTextTextPersonName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dl_linear);

        mbtn1=(Button) findViewById(R.id.btn_1);
        mbtn2=(Button) findViewById(R.id.btn_2);//注册

        mbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent zcIntent = new Intent(Login.this, Register.class);
                startActivity(zcIntent);
            }
        });
        mbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username_dl = ((EditText) findViewById(R.id.username_dl)).getText().toString();
                String password_dl = ((EditText) findViewById(R.id.password_dl)).getText().toString();
                SharedPreferences sharedPreferences= getSharedPreferences(username_dl, MODE_PRIVATE);//获取盖登录名对应的文件
                //boolean isRemember =sharedPreferences.getBoolean("remember_password", false);
                if(username_dl.isEmpty() || password_dl.isEmpty()){
                    Toast.makeText(Login.this, "用户名或密码不能为空", Toast.LENGTH_LONG).show();
                }
                ////获取盖登录名对应的文件 的内部的登录名密码
                else if(username_dl.equals(sharedPreferences.getString("name", "NULL")) && (password_dl.equals(sharedPreferences.getString("password", "")))){

                    /*editor = sharedPreferences.edit();
                    if (rememberpass.isChecked()) {
                        editor.putBoolean("remember_password", true);
                    }
                    else{
                        editor.clear();
                    }
                    editor.commit();
                    */
                    Toast.makeText(Login.this, "登陆成功", Toast.LENGTH_SHORT).show();
                   Intent intent = new Intent(Login.this, data_display.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(Login.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
                }


               // Intent zcIntent = new Intent(dl.this,zc.class);
               // startActivity(zcIntent);

            }
        });
    }
}