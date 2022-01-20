package com.mh.ajappnew.tools;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

@SuppressLint("HandlerLeak")
public class MessageTransmit implements Runnable {
    private static final String TAG = "MessageTransmit";
    // 以下为Socket服务器的IP和端口，根据实际情况修改
    private String SOCKET_IP = "192.168.1.125";
    private int SOCKET_PORT = 8081;
    private BufferedReader mReader = null; // 声明一个缓存读取器对象
    private OutputStream mWriter = null; // 声明一个输出流对象

    public Handler handler;
    private Socket socket;


    public MessageTransmit(String ip, String port) {
        SOCKET_IP = ip;
        SOCKET_PORT = Integer.valueOf(port);
    }

    @Override
    public void run() {

        connect();
    }

    private void connect() {
        // 创建一个套接字对象

        try {
            socket  = new Socket();
            // 命令套接字连接指定地址的指定端口
            socket.connect(new InetSocketAddress(SOCKET_IP, SOCKET_PORT), 3000);
            // 根据套接字的输入流，构建缓存读取器
            mReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"gbk"));
            // 获得套接字的输出流
            mWriter = socket.getOutputStream();
            // 启动一条子线程来读取服务器的返回数据
            new RecvThread().start();

            Message msg = Message.obtain();
            msg.obj = "客户端连接成功."; // 消息描述
            msg.what = 1;
            // 通知SocketActivity收到消息
            handler.sendMessage(msg);

            // 为当前线程初始化消息队列
            Looper.prepare();
            // 让线程的消息队列开始运行，之后就可以接收消息了
            Looper.loop();


        } catch (Exception e) {
            Message msg = Message.obtain();
            msg.what = 2;
            msg.obj = "客户端连接失败."+ SOCKET_IP+":"+ String.valueOf(SOCKET_PORT) + e.getMessage(); // 消息描述
            // 通知SocketActivity收到消息
            handler.sendMessage(msg);

            e.printStackTrace();
        }
    }


//    public void close() {
//        if (socket != null) {
//            try {
//
//                mWriter.close();
//                //socket.close();
//            } catch (Exception ex) {
//
//            }
//        }
//    }

    // 创建一个发送处理器对象，让App向后台服务器发送消息
    public Handler mSendHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
//            Log.d(TAG, "handleMessage: " + msg.obj);
//            // 换行符相当于回车键，表示我写好了发出去吧
//            String send_msg = msg.obj.toString() + "\n";
//            try {
//                // 往输出流对象中写入数据
//                mWriter.write(send_msg.getBytes("utf8"));
//            } catch (Exception e) {
//                String ss = e.getMessage();
//                connect();
//            }

            final String  send_msg1 = msg.obj.toString() + "\n";

            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        // 往输出流对象中写入数据
                        mWriter.write(send_msg1.getBytes("gbk"));
                    } catch (Exception e) {
                        String ss = e.getMessage();
                        connect();
                    }
                }
            }).start();
        }
    };

    // 定义消息接收子线程，让App从后台服务器接收消息
    private class RecvThread extends Thread {
        @Override
        public void run() {
            try {
                String content;
                // 读取到来自服务器的数据

                while ((content = mReader.readLine()) != null) {
                    // 获得一个默认的消息对象
                    Message msg = Message.obtain();
                    msg.obj = content; // 消息描述
                    msg.what = 3;
                    // 通知SocketActivity收到消息
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                connect();
                e.printStackTrace();
            }
        }
    }

}

