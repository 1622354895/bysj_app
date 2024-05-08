package com.app2020212891.enviroment_monitor;

import static android.os.SystemClock.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app2020212891.enviroment_monitor.AdminActivity;
import com.app2020212891.enviroment_monitor.ApiOrderResponse;
import com.app2020212891.enviroment_monitor.OneNetAPIOrder;
import com.app2020212891.enviroment_monitor.R;
import com.app2020212891.enviroment_monitor.data_display;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class yztc_activity extends AppCompatActivity {
    OneNetAPIOrder oneNetAPIOrder = new OneNetAPIOrder();
    ApiOrderResponse apiOrderResponse = new ApiOrderResponse();
    ApiOrderResponse.Data data = new ApiOrderResponse.Data();

    private EditText tjpmEditText;
    private EditText tjwdEditText;
    private EditText tjsdEditText;
    private Button confirmButton;
    private Button confirmButton2;
    private Button confirmButton3;
    private Button controlButton;
    private Button displayButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yztj);

        // 找到EditText和Button视图对象
        tjpmEditText = findViewById(R.id.tjpm);
        tjwdEditText = findViewById(R.id.tjwd);
        tjsdEditText = findViewById(R.id.tjsd);
        confirmButton = findViewById(R.id.btn_xiugai1);
        confirmButton2 = findViewById(R.id.btn_xiugai2);
        confirmButton3 = findViewById(R.id.btn_xiugai3);
        controlButton = findViewById(R.id.control2);
        displayButton = findViewById(R.id.display2);

        // 设置确认修改按钮的点击事件监听器
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在此处添加确认修改按钮点击后的逻辑处理代码
                String pmValue = tjpmEditText.getText().toString();

                if( 0<=Integer.parseInt(pmValue)&&Integer.parseInt(pmValue)<=5000) {
                    Toast.makeText(yztc_activity.this,"发送阈值调节指令", Toast.LENGTH_SHORT).show();
                    PostOrder_2("pmmax:"+ new String(pmValue));
                    sleep(200);
                }
                else{
                    Toast.makeText(yztc_activity.this,"阈值范围出错，请重新输入", Toast.LENGTH_SHORT).show();
                }


            }

        });
        confirmButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String wdValue = tjwdEditText.getText().toString();

                if( 0<=Integer.parseInt(wdValue)&&Integer.parseInt(wdValue)<=100) {
                    Toast.makeText(yztc_activity.this,"发送阈值调节指令", Toast.LENGTH_SHORT).show();
                    PostOrder_2("tempmax:"+new String(wdValue));
                    sleep(200);
                }
                else{
                    Toast.makeText(yztc_activity.this,"阈值范围出错，请重新输入", Toast.LENGTH_SHORT).show();
                }

            }
        });
        confirmButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sdValue = tjsdEditText.getText().toString();

                if( 0<=Integer.parseInt(sdValue)&&Integer.parseInt(sdValue)<=100) {
                    Toast.makeText(yztc_activity.this,"发送阈值调节指令", Toast.LENGTH_SHORT).show();
                    PostOrder_2("humimax:" + new String(sdValue));
                    sleep(200);
                }
                else{
                    Toast.makeText(yztc_activity.this,"阈值范围出错，请重新输入", Toast.LENGTH_SHORT).show();
                }

            }
        });


        // 设置控制界面按钮的点击事件监听器
        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在此处添加跳转到控制界面的逻辑处理代码
                Intent intent = new Intent(yztc_activity.this, AdminActivity.class);
                startActivity(intent);
                Toast.makeText(yztc_activity.this,"进入控制界面", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // 设置显示界面按钮的点击事件监听器
        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在此处添加跳转到显示界面的逻辑处理代码
                Intent intent = new Intent(yztc_activity.this, data_display.class);
                startActivity(intent);
                Toast.makeText(yztc_activity.this,"进入数据显示界面", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }



    //发送命令API相关：
    public void PostOrder_2(String ml){
        Thread thread = null;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String orderBack = "";
                    orderBack = oneNetAPIOrder.sendOrder(ml);
                    parseOrderJson(orderBack);
                    System.out.println(orderBack);
                    System.out.println(apiOrderResponse.getMsg());
                }catch (Exception e){
                    System.out.println("2"+e);
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void parseOrderJson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        int code = jsonObject.getInt("code");
        String msg = jsonObject.getString("msg");

        apiOrderResponse.SetApiOrderResponse(msg,code);
        if (code ==0) {
            if (!jsonObject.isNull("data")) {
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                String cmd_uuid = jsonObject1.getString("cmd_uuid");
                data.setCmd_uuid(cmd_uuid);
                apiOrderResponse.SetApiOrderResponseData(data);
            }
        }
    }


}
