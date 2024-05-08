package com.app2020212891.enviroment_monitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class data_display extends AppCompatActivity {
    // 在类的开头定义一个全局变量来存储Runnable对象和线程
    private Runnable backgroundRunnable;
    private Thread backgroundThread;
    OneNetAPIOrder oneNetAPIOrder = new OneNetAPIOrder();
    ApiOrderResponse apiOrderResponse = new ApiOrderResponse();
    ApiOrderResponse.Data data = new ApiOrderResponse.Data();
    private Button open,close,digit,control;
    private EditText temp_display,humi_display,pm_display,light_display;
    private TextView warning,system;
    int system_flag=0,pm_bj=0,temp_bj=0,humi_bj=0;
    int pm,temp,humi,light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_display);

        open=(Button) findViewById(R.id.button_open);
        close=(Button) findViewById(R.id.button_close);
        digit=(Button) findViewById(R.id.button_digit);
        control=(Button) findViewById(R.id.button_control);
        temp_display=(EditText) findViewById(R.id.temp);
        humi_display=(EditText) findViewById(R.id.humi);
        pm_display=(EditText) findViewById(R.id.PM);
        light_display=(EditText) findViewById(R.id.light);
        system=(TextView)findViewById(R.id.text_system);
        warning=(TextView)findViewById(R.id.text_warning);

        // 创建Runnable对象
        backgroundRunnable = new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // 在后台线程中获取数据
                        Getdata();
                        // 在UI线程中更新UI
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 在UI线程中显示数据
                                Showdata();
                            }
                        });
                        // 每隔一段时间执行一次
                        Thread.sleep(1000); // 2500毫秒（2.5秒）
                    } catch (InterruptedException e) {
                        // 处理中断异常
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };

        // 启动后台线程
        startBackgroundThread();

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostOrder_2("system:1");
                Toast.makeText(data_display.this,"发送指令", Toast.LENGTH_SHORT).show();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostOrder_2("system:0");
                Toast.makeText(data_display.this,"发送指令", Toast.LENGTH_SHORT).show();
            }
        });

        digit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(data_display.this,"进入数据可视化界面", Toast.LENGTH_SHORT).show();
                // 创建一个Intent对象
                Intent intent = new Intent(Intent.ACTION_VIEW);
                // 设置Intent的数据为指定的网址
                intent.setData(Uri.parse("https://open.iot.10086.cn/view/main/index.html#/share2d?id=6622133ef17988003511d14a"));
                // 启动Intent，打开浏览器并加载网址
                startActivity(intent);
            }
        });


        control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(data_display.this, AdminActivity.class);
                startActivity(intent);
                Toast.makeText(data_display.this,"进入控制界面", Toast.LENGTH_SHORT).show();
                finish();
            }
        });




    }
    //获取数据
    public void Getdata(){
        PostOrder();

    }
    public void Showdata(){
        if(system_flag==1) {
            system.setText("系统状态:在线");
            temp_display.setText(String.valueOf(temp) + "℃");
            humi_display.setText(String.valueOf(humi) + "%");
            pm_display.setText(String.valueOf(pm) + "ug/m³");
            light_display.setText(String.valueOf(light));
            //报警显示
            StringBuilder warnings = new StringBuilder();
            if (pm_bj == 1) {
                warnings.append("PM超标！");
            }
            if (temp_bj == 1) {
                warnings.append("温度超标！");
            }
            if (humi_bj == 1) {
                warnings.append("湿度超标！");
            }

            // 如果没有任何超标，清空显示
            if (pm_bj != 1 && temp_bj != 1 && humi_bj != 1) {
                warning.setText("");
            } else {
                // 否则显示警告信息
                warning.setText(warnings.toString());
            }
        }
        else{
            system.setText("系统状态:离线");
            temp_display.setText("");
            humi_display.setText("");
            pm_display.setText("");
            light_display.setText("");
            warning.setText(""); //新加未打包
        }

    }

    //发送命令API相关：
    public void PostOrder(){
        Thread thread = null;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String orderBack = "";
                    orderBack = oneNetAPIOrder.GetData();
                    parseData(orderBack);
                    //Showdata();
                    //Showdata(); // 在获取数据后更新UI
                    System.out.println(orderBack);
                    System.out.println(apiOrderResponse.getMsg());
                }catch (Exception e){
                    System.out.println("1"+e);
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
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
    public void parseData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            int code = jsonObject.getInt("code");
            if (code == 0) {
                JSONArray devices = jsonObject.getJSONObject("data").getJSONArray("devices");
                for (int i = 0; i < devices.length(); i++) {
                    JSONObject device = devices.getJSONObject(i);
                    String deviceId = device.getString("id");
                    JSONArray datastreams = device.getJSONArray("datastreams");
                    for (int j = 0; j < datastreams.length(); j++) {
                        JSONObject datastream = datastreams.getJSONObject(j);
                        String datastreamId = datastream.getString("id");
                        if (datastream.has("value")) {
                            Object value = datastream.get("value");
                            // 根据数据流ID将值赋给相应的变量
                            // 根据数据流ID将值赋给相应的变量
                            switch (datastreamId) {
                                case "system":
                                    system_flag = value instanceof Integer ? (int) value : 0;
                                    break;
                                case "pm_warning":
                                    pm_bj = value instanceof Integer ? (int) value : 0;
                                    break;
                                case "temp_warning":
                                    temp_bj = value instanceof Integer ? (int) value : 0;
                                    break;
                                case "humi_warning":
                                    humi_bj = value instanceof Integer ? (int) value : 0;
                                    break;
                                case "PM":
                                    pm = value instanceof Integer ? (int) value : 0;
                                    break;
                                case "temp":
                                    temp = (int) Math.round((double) value);
                                    break;
                                case "humi":
                                    humi = (int) Math.round((double) value);
                                    break;
                                case "adcx":
                                    light = value instanceof Integer ? (int) value : 0;
                                    break;
                                default:
                                    break;
                            }

                        }
                    }
                }

            } else {
                String msg = jsonObject.getString("msg");
                System.out.println("Error: " + msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // 在onPause()方法中停止后台线程
    @Override
    protected void onPause() {
        super.onPause();
        // 停止后台线程
        stopBackgroundThread();
    }

    // 在onResume()方法中重新启动后台线程
    @Override
    protected void onResume() {
        super.onResume();
        // 启动后台线程
        startBackgroundThread();
    }

    // 启动后台线程
    private void startBackgroundThread() {
        if (backgroundThread == null || !backgroundThread.isAlive()) {
            backgroundThread = new Thread(backgroundRunnable);
            backgroundThread.start();
        }
    }

    // 停止后台线程
    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.interrupt();
            backgroundThread = null;
        }

}
}