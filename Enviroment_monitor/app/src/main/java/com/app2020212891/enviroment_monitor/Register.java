package com.app2020212891.enviroment_monitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {
    private Button mbtn3;
    private String admin = "660282";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zc_linear);

        mbtn3=(Button) findViewById(R.id.btn_3);
        //mbtn3.setText("点击注册");

        mbtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dl = new Intent(Register.this, Login.class);
                //startActivity(subActivityIntent);
                String username = ((EditText) findViewById(R.id.zcuser)).getText().toString();
                String password = ((EditText) findViewById(R.id.zcwd)).getText().toString();
                String admin_wd = ((EditText) findViewById(R.id.gly)).getText().toString();
                //subActivityIntent.putExtra("sub_activity_info",yourName);
               // subActivityIntent.putExtra("sub_activity_info_2",yourName_2);
               // setResult(RESULT_OK,subActivityIntent);

                //比较管理员密钥
                if(admin_wd.equals(admin)) {
                    Toast.makeText(Register.this,"注册成功~~",Toast.LENGTH_SHORT).show();
                    //获取SharedPreferences对象
                    SharedPreferences sharedPreferences = getSharedPreferences(username, MODE_PRIVATE);
                    //获取Editor对象的引用
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //将获取过来的值放入文件
                    editor.putString("name", username);
                    editor.putString("password", password);
                    //editor.putBoolean("islogin",true);
                    // 提交数据
                    editor.commit();
                    finish();
                    Register.this.startActivity(dl);
                }
                else {
                    Toast.makeText(Register.this,"管理员密钥错误",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}