package com.mh.ajappnew.http;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.mh.ajappnew.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AutoUpdater {
    // 下载安装包的网络路径
    private String apkUrl = "http://192.168.1.101:9099/apk/";
    protected String checkUrl = apkUrl + "output.json";

    // 保存APK的文件名
    private static final String saveFileName = "app-release.apk";
    private static File apkFile;

    // 下载线程
    private Thread downLoadThread;
    private int progress;// 当前进度
    // 应用程序Context
    private Context mContext;
    // 是否是最新的应用,默认为false
    private boolean isNew = false;
    private boolean intercept = false;
    // 进度条与通知UI刷新的handler和msg常量
    private ProgressBar mProgress;
    private TextView txtStatus;

    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int SHOWDOWN = 3;

    private String Remark;
    private Boolean CanQuit;

    public AutoUpdater(Context context, String ip, String port,String remark,boolean canquit) {
        mContext = context;
        apkFile = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), saveFileName);
        apkUrl = "http://" + ip + ":" + port + "/apk/";
        CanQuit=canquit;
        Remark=remark;
    }

    public void ShowUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("软件版本更新");
        builder.setMessage(Remark);
        builder.setPositiveButton("立即下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShowDownloadDialog();
            }
        });

        if(CanQuit==true) {
            builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        builder.setCancelable(false);
        builder.create().show();
    }

    private void ShowDownloadDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("软件版本更新");
        dialog.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.autoupdate_item, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);
        txtStatus = v.findViewById(R.id.txtStatus_autoupdate);
        dialog.setView(v);
//        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                intercept = true;
//            }
//        });
        dialog.show();
        DownloadApk();
    }

    /**
     * 检查是否更新的内容
     */
    private void CheckUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int localVersion = 0;
                try {
                    localVersion = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String versionName = "1";
                String outputFile = "app-release.apk";
                String config = doGet(checkUrl);
                if (config != null && config.length() > 0) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        Matcher m = Pattern.compile("\"outputFile\":\\s*\"(?<m>[^\"]*?)\"").matcher(config);
//
//                        if (m.find()) {
//                            outputFile = m.group("m");
//                        }
//                        m = Pattern.compile("\"versionName\":\\s*\"(?<m>[^\"]*?)\"").matcher(config);
//                        if (m.find()) {
//                            String v = m.group("m");
//                            versionName = m.group("m").replace("v1.0.", "");
//                        }
//                    }

                    versionName = "2";

                }
                if (localVersion < Double.parseDouble(versionName)) {
                    apkUrl = apkUrl + outputFile;
                    mHandler.sendEmptyMessage(SHOWDOWN);
                } else {
                    return;
                }
            }
        }).start();
    }

    public void Update(String path) {
        apkUrl = path;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(SHOWDOWN);
            }
        }).start();
    }

    /**
     * 从服务器下载APK安装包
     */
    public void DownloadApk() {
        downLoadThread = new Thread(DownApkWork);
        downLoadThread.start();
    }

    private Runnable DownApkWork = new Runnable() {
        @Override
        public void run() {
            URL url;
            try {
                url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream ins = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(apkFile);
                int count = 0;
                byte[] buf = new byte[4096];
                while (!intercept) {
                    int numread = ins.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 下载进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                }
                fos.close();
                ins.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 安装APK内容
     */
    public void installAPK() {
        try {
            if (!apkFile.exists()) {
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//安装完成后打开新版本
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 给目标应用一个临时授权
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
                //如果SDK版本>=24，即：Build.VERSION.SDK_INT >= 24，使用FileProvider兼容安装apk
                String packageName = mContext.getApplicationContext().getPackageName();
                String authority = new StringBuilder(packageName).append(".fileprovider").toString();
                Uri apkUri = FileProvider.getUriForFile(mContext, authority, apkFile);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }

            mContext.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());//安装完之后会提示”完成” “打开”。


        } catch (Exception e) {
            String err = e.getMessage();
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SHOWDOWN:
                    ShowUpdateDialog();
                    break;
                case DOWN_UPDATE:
                    txtStatus.setText(progress + "%");
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    Toast.makeText(mContext, "下载完毕", Toast.LENGTH_SHORT).show();
                    installAPK();
                    break;
                default:
                    break;
            }
        }

    };

    public static String doGet(String httpurl) {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;
        try {
            URL url = new URL(httpurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.connect();
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            connection.disconnect();
        }
        return result;
    }
}

