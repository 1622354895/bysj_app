package com.app2020212891.enviroment_monitor;

import static android.os.SystemClock.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.app2020212891.enviroment_monitor.yztc_activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdminActivity extends AppCompatActivity {
    private Runnable backgroundRunnable_2;
    private Thread backgroundThread;
    OneNetAPIOrder oneNetAPIOrder = new OneNetAPIOrder();
    ApiOrderResponse apiOrderResponse = new ApiOrderResponse();
    ApiOrderResponse.Data data = new ApiOrderResponse.Data();
    private EditText tempmaxEditText;
    private EditText pmmaxEditText;
    private EditText humimaxEditText;

    private Button yztj; // 添加了参数调节按钮
    private Button open_led0;
    private Button close_led0;
    private Button open_led1;
    private Button close_led1;
    private Button open_beep;
    private Button close_beep;
    private Button open_alarm;
    private Button close_alarm;
    private Button display; // 添加了显示界面按钮
    int pmmax;
    int humimax;
    int tempmax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        tempmaxEditText = findViewById(R.id.tempmax);
        pmmaxEditText = findViewById(R.id.pmmax);
        humimaxEditText = findViewById(R.id.humimax);



        yztj = findViewById(R.id.yztj);
        open_led0 = findViewById(R.id.button_LED0K);
        close_led0 = findViewById(R.id.button_LED0G);
        open_led1 = findViewById(R.id.button_LED1K);
        close_led1 = findViewById(R.id.button_LED1G);
        open_beep = findViewById(R.id.button_BEEPK);
        close_beep = findViewById(R.id.button_BEEPG);
        open_alarm = findViewById(R.id.button_BJK);
        close_alarm = findViewById(R.id.button_BJG);
        display = findViewById(R.id.display);

        // 创建Runnable对象
        backgroundRunnable_2 = new Runnable() {
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
                        Thread.sleep(1000); // 2000毫秒（4秒）
                    } catch (InterruptedException e) {
                        // 处理中断异常
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };



        yztj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, yztc_activity.class);
                startActivity(intent);
                Toast.makeText(AdminActivity.this,"进入阈值调节界面", Toast.LENGTH_SHORT).show();
                //finish();

            }
        });

        open_led0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sleep(250); //保证前面处理完发送
                PostOrder_2("LED0:1");
            }
        });

        close_led0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sleep(250); //保证前面处理完发送
                PostOrder_2("LED0:0");
            }
        });

        open_led1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostOrder_2("LED1:1");
            }
        });

        close_led1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostOrder_2("LED1:0");
            }
        });

        open_beep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostOrder_2("BEEP:1");
            }
        });

        close_beep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostOrder_2("BEEP:0");
            }
        });

        open_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostOrder_2("bj:1");
            }
        });

        close_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostOrder_2("bj:0");
            }
        });

        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, data_display.class);
                startActivity(intent);
                Toast.makeText(AdminActivity.this,"进入显示界面", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }






    //获取数据
    public void Getdata(){
        PostOrder();

    }
    public void Showdata(){
        tempmaxEditText.setText(String.valueOf(tempmax) + "℃");
        humimaxEditText.setText(String.valueOf(humimax) + "%");
        pmmaxEditText.setText(String.valueOf(pmmax) + "ug/m³");
    }

    //获取数据：
    public void PostOrder(){
        Thread thread = null;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String orderBack = "";
                    orderBack = oneNetAPIOrder.GetData();
                    parseData(orderBack);
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
                                case "pmmax":
                                    pmmax = value instanceof Integer ? (int) value : 0;
                                    break;
                                case "humimax":
                                    humimax = value instanceof Integer ? (int) value : 0;
                                    break;
                                case "tempmax":
                                    tempmax = value instanceof Integer ? (int) value : 0;
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
            backgroundThread = new Thread(backgroundRunnable_2);
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