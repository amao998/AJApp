package com.mh.ajappnew.http;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mh.ajappnew.comm.Config;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class PostRequestUtil {
    private Config config;
    public Handler handler;

    public PostRequestUtil(Config _config) {
        this.config = _config;
    }

    public void Run(final Map<String, String> maps) {
        new Thread() {
            @Override
            public void run() {
                SendData(maps);
            }
        }.start();
    }

    public void RunPic(final Map<String, String> maps) {
        new Thread() {
            @Override
            public void run() {
                SendPic(maps);
            }
        }.start();
    }
    private void SendData(Map<String, String> maps) {
        String result = "";
        int what = 0;
        try {
            String url = "http://" + this.config.getIp() + ":" + this.config.getPort() + "/ashx/PdaMacFunc.ashx";
            URL mUrl = new URL(url);
            HttpURLConnection mHttpURLConnection = (HttpURLConnection) mUrl.openConnection();
            //设置链接超时时间
            mHttpURLConnection.setConnectTimeout(15000);
            //设置读取超时时间
            mHttpURLConnection.setReadTimeout(15000);
            //设置请求参数
            mHttpURLConnection.setRequestMethod("POST");
            //添加Header
            mHttpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            //接收输入流
            mHttpURLConnection.setDoInput(true);
            //传递参数时需要开启
            mHttpURLConnection.setDoOutput(true);
            //Post方式不能缓存,需手动设置为false
            mHttpURLConnection.setUseCaches(false);

            mHttpURLConnection.connect();

            DataOutputStream dos = new DataOutputStream(mHttpURLConnection.getOutputStream());

            maps.put("token", config.getToken());
            maps.put("imei", config.getImei());
            maps.put("jyy", config.getJyy());


            String postContent = "";
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                postContent += URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
            }
            dos.write(postContent.getBytes());
            dos.flush();
            // 执行完dos.close()后，POST请求结束
            dos.close();
            // 获取代码返回值
            int respondCode = mHttpURLConnection.getResponseCode();
            Log.d("respondCode", "respondCode=" + respondCode);
            // 获取返回内容类型
            String type = mHttpURLConnection.getContentType();
            Log.d("type", "type=" + type);
            // 获取返回内容的字符编码
            String encoding = mHttpURLConnection.getContentEncoding();
            Log.d("encoding", "encoding=" + encoding);
            // 获取返回内容长度，单位字节
            int length = mHttpURLConnection.getContentLength();
            Log.d("length", "length=" + length);
//            // 获取头信息的Key
//            String key = mHttpURLConnection.getHeaderField(idx);
//            Log.d("key", "key="+key);
            // 获取完整的头信息Map
            Map<String, List<String>> map = mHttpURLConnection.getHeaderFields();
            if (respondCode == 200) {
                // 获取响应的输入流对象
                InputStream is = mHttpURLConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                String msg = new String(message.toByteArray());
                Log.d("Common", msg);
                result = msg;
                what = 1;
            } else {
                result = "连接超时:"+String.valueOf(respondCode);
            }
        } catch (Exception e) {
            result = "错误."+ e.getMessage();
        }

        Message msg = new Message();
        msg.what = what;
        msg.obj = result;
        handler.sendMessage(msg);

    }

    private void SendPic(Map<String, String> maps) {
        String result = "";
        int what = 0;
        try {
            String url = "http://" + this.config.getIp() + ":" + this.config.getPort() + "/ashx/EnvPicFunc.ashx";
            URL mUrl = new URL(url);
            HttpURLConnection mHttpURLConnection = (HttpURLConnection) mUrl.openConnection();
            //设置链接超时时间
            mHttpURLConnection.setConnectTimeout(15000);
            //设置读取超时时间
            mHttpURLConnection.setReadTimeout(15000);
            //设置请求参数
            mHttpURLConnection.setRequestMethod("POST");
            //添加Header
            mHttpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            //接收输入流
            mHttpURLConnection.setDoInput(true);
            //传递参数时需要开启
            mHttpURLConnection.setDoOutput(true);
            //Post方式不能缓存,需手动设置为false
            mHttpURLConnection.setUseCaches(false);

            mHttpURLConnection.connect();

            DataOutputStream dos = new DataOutputStream(mHttpURLConnection.getOutputStream());

            maps.put("token", config.getToken());
            maps.put("imei", config.getImei());
            maps.put("jyy", config.getJyy());

            String postContent = "";
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                postContent += URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
            }
            dos.write(postContent.getBytes());
            dos.flush();
            // 执行完dos.close()后，POST请求结束
            dos.close();
            // 获取代码返回值
            int respondCode = mHttpURLConnection.getResponseCode();
            Log.d("respondCode", "respondCode=" + respondCode);
            // 获取返回内容类型
            String type = mHttpURLConnection.getContentType();
            Log.d("type", "type=" + type);
            // 获取返回内容的字符编码
            String encoding = mHttpURLConnection.getContentEncoding();
            Log.d("encoding", "encoding=" + encoding);
            // 获取返回内容长度，单位字节
            int length = mHttpURLConnection.getContentLength();
            Log.d("length", "length=" + length);
//            // 获取头信息的Key
//            String key = mHttpURLConnection.getHeaderField(idx);
//            Log.d("key", "key="+key);
            // 获取完整的头信息Map
            Map<String, List<String>> map = mHttpURLConnection.getHeaderFields();
            if (respondCode == 200) {
                // 获取响应的输入流对象
                InputStream is = mHttpURLConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                String msg = new String(message.toByteArray());
                Log.d("Common", msg);
                result = msg;
                what = 1;
            } else {
                result = "连接超时";
            }
        } catch (Exception e) {
            result = "错误."+ e.getMessage();
        }

        Message msg = new Message();
        msg.what = what;
        msg.obj = result;
        handler.sendMessage(msg);

    }
}
