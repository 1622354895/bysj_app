package com.app2020212891.enviroment_monitor;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OneNetAPIOrder {
    public String orderBackMessage = null;

    //发送命令
    public String sendOrder(String order) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, order);
        Request request = new Request.Builder()
                .url("https://iot-api.heclouds.com/datapoint/synccmds?product_id=i4g9oLr6AU&device_name=mqtt1&timeout=10")
                .method("POST", body)
                .addHeader("Authorization", "version=2022-05-01&res=userid%2F337011&et=2344492222&method=md5&sign=%2B8HKymKeEh%2BsVLnoXTO6hg%3D%3D")
                .addHeader("Content-Type", "text/plain")
                .build();
        Response response = client.newCall(request).execute();

        orderBackMessage = response.body().string();
        try {
            return orderBackMessage;
        }catch (Exception e){
            System.out.println("order"+e);
        }
        return null;
    }

    //获取数据
    public String GetData() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://iot-api.heclouds.com/datapoint/current-datapoints?product_id=i4g9oLr6AU&device_name=mqtt1")
                .get()
                .addHeader("Authorization", "version=2022-05-01&res=userid%2F337011&et=2344492222&method=md5&sign=%2B8HKymKeEh%2BsVLnoXTO6hg%3D%3D")
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string(); // 返回响应内容
        } else {
            throw new IOException("Unexpected response code: " + response.code());
        }
    }


}
