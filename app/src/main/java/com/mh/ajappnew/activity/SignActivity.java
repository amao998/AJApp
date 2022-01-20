package com.mh.ajappnew.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.SaveVisPhotoInfo;
import com.mh.ajappnew.tools.CustomDialog;
import com.mh.ajappnew.tools.DateUtil;
import com.mh.ajappnew.tools.PicUtils;
import com.mh.ajappnew.tools.URLEncodeing;
import com.mh.ajappnew.tools.helper;
import com.mh.ajappnew.view.SignView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SignActivity extends AppCompatActivity {

    private String pathname = "/sdcard/123/123.jpg";
    private String typeid, typename, detectid, jylsh, jycs;
    private TextView tv_sign_typeid, tv_sign_typename, tv_sign_detectid;

    private Config config;

    public void setConfig(Config config) {
        this.config = config;
    }

    private ProgressDialog pd;

    private TextView mOffTextView;
    private Handler mOffHandler;
    private Timer mOffTime;
    private Dialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sign);

        Intent i = getIntent();
        typename = i.getStringExtra("typename");
        typeid = i.getStringExtra("typeid");
        detectid = i.getStringExtra("detectid");
        jylsh = i.getStringExtra("jylsh");
        jycs = i.getStringExtra("jycs");
        config = i.getParcelableExtra("Config");

        tv_sign_typeid = (TextView) findViewById(R.id.tv_sign_typeid);
        tv_sign_typename = (TextView) findViewById(R.id.tv_sign_typename);
        tv_sign_detectid = (TextView) findViewById(R.id.tv_sign_detectid);

        tv_sign_typeid.setText(typeid);
        tv_sign_typename.setText(typename);
        tv_sign_detectid.setText(jylsh);

        final SignView signView = findViewById(R.id.signview);
        Button but_sign_sign = findViewById(R.id.but_sign_sign);
        but_sign_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = signView.getBitmap();

                if (bitmap == null) {
                    Toast.makeText(SignActivity.this, "提示,未进行签名", Toast.LENGTH_LONG);
                    return;
                }

                String fold = CreatFold();
                pathname = fold + "/" + DateUtil.getNowDateTime() + ".jpg";

                File file = new File(pathname);
                try {
                    saveBitmapToJPG(bitmap, file);

                    SaveVisPhoto(pathname, typeid);
//                    //回传地址
//                    //设置返回的数据
//                    Intent intent = new Intent();
//                    intent.putExtra("pathname", pathname);
//                    intent.putExtra("typeid", typeid);
//                    setResult(3, intent);
//                    //关闭当前activity
//
//                    finish();


                } catch (IOException e) {
                    Toast.makeText(SignActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        Button but_sign_resign = findViewById(R.id.but_sign_resign);
        but_sign_resign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signView.clear();
            }
        });

        Button but_sign_exit = findViewById(R.id.but_sign_exit);
        but_sign_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //回传地址
                //设置返回的数据
                Intent intent = new Intent();
                intent.putExtra("pathname", "");
                setResult(0, intent);
                //关闭当前activity
                finish();
            }
        });
    }

    private String CreatFold() {
        //String fold = "/sdcard/sign/" + DateUtil.getNowyear() + "/" + DateUtil.getNowmonth() + "/" + DateUtil.getNowday();
        String fold1 = "/sdcard/sign";
        File file1 = new File(fold1);
        if (!file1.exists())
            file1.mkdir();

        String fold2 = fold1 + "/" + DateUtil.getNowyear();
        File file2 = new File(fold2);
        if (!file2.exists())
            file2.mkdir();

        String fold3 = fold2 + "/" + DateUtil.getNowmonth();
        File file3 = new File(fold3);
        if (!file3.exists())
            file3.mkdir();

        String fold4 = fold3 + "/" + DateUtil.getNowday();
        File file4 = new File(fold4);
        if (!file4.exists())
            file4.mkdir();

        return fold4;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (android.os.Build.VERSION.SDK_INT >= 27) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }
    }


    private void SaveVisPhoto(String path, String zpzl) {
        pd = ProgressDialog.show(SignActivity.this, "提示", "上传中...");
        //pd.setCancelable(true);

        File f = new File(path);
        String base64 = PicUtils.fileToBase64(f);

        SaveVisPhotoInfo info = new SaveVisPhotoInfo();
        info.setDetectid(detectid);
        info.setBase64(base64);
        info.setZpzl(zpzl);
        info.setFlag("0");
        info.setJylsh(jylsh);
        info.setJycs(jycs);

        String data = JSON.toJSONString(info);

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handlerphoto;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "SaveVisPhoto");
        maps.put("data", data);
        postRequestUtil.Run(maps);
    }

    Handler handlerphoto = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("图片上传网络错误", msg.obj.toString(), SignActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {

                        //ShowErrDialog("提示", typename + "签名:" + typeid + ",上传成功");
                        initDialog();


                        // byte[] decodedString = Base64.decode(infoA.getBase64(), Base64.DEFAULT);
                        // Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

//                        if (infoA.getZpzl().equals(zpzlWG) == true)
//                            img_vis_wgjyy.setImageBitmap(decodedByte);
//                        if (infoA.getZpzl().equals(zpzlYCY) == true)
//                            img_vis_ycy.setImageBitmap(decodedByte);
//                        if (infoA.getZpzl().equals(zpzlDTY) == true)
//                            img_vis_dtjyy.setImageBitmap(decodedByte);


                    } else {
                        helper.ShowErrDialog("图片上传失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), SignActivity.this);
                    }
                } catch (Exception ex) {
                    helper.ShowErrDialog("图片上传系统错误", ex.getMessage(), SignActivity.this);
                }
            }
        }
    };

    private void ShowErrDialog(String title, String desc) {
        CustomDialog customDialog = new CustomDialog(SignActivity.this);
        customDialog.setTitle(title);
        customDialog.setMessage(desc);
        customDialog.setCancel("返回", new CustomDialog.IOnCancelListener() {
            @Override
            public void onCancel(CustomDialog dialog) {
                //Toast.makeText(LogInActivity.this, "取消成功！",Toast.LENGTH_SHORT).show();
            }
        });
        customDialog.setConfirm("确定", new CustomDialog.IOnConfirmListener() {
            @Override
            public void onConfirm(CustomDialog dialog) {
                //Toast.makeText(LogInActivity.this, "确认成功！",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.putExtra("pathname", pathname);
                intent.putExtra("typeid", typeid);
                setResult(3, intent);

                finish();
            }
        });
        customDialog.show();
        customDialog.setCancelVisable(true);
    }

    private  void  off()
    {
        Intent intent = new Intent();
        intent.putExtra("pathname", pathname);
        intent.putExtra("typeid", typeid);
        setResult(3, intent);

        finish();
    }

    //////创建对话框

    void initDialog(){

        mOffTextView = new TextView(this);
        mOffTextView.setText("    即将关闭： 1");

        mDialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setCancelable(false)
                .setView(mOffTextView) ////

                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mOffTime.cancel();
                        off();////关闭后的一些操作
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        mOffTime.cancel();
                    }
                })
                .create();
        mDialog.show();
        mDialog.setCanceledOnTouchOutside(false);

        mOffHandler = new Handler() {
            public void handleMessage(Message msg) {

                if (msg.what > 0) {

                    ////动态显示倒计时
                    mOffTextView.setText("    即将关闭："+msg.what);

                } else {
                    ////倒计时结束自动关闭

                    if(mDialog!=null){
                        mDialog.dismiss();
                    }
                    off();////关闭后的操作

                    mOffTime.cancel();
                }
                super.handleMessage(msg);
            }

        };

        //////倒计时

        mOffTime = new Timer(true);
        TimerTask tt = new TimerTask() {
            int countTime = 1;
            public void run() {
                if (countTime > 0) {
                    countTime--;
                }
                Message msg = new Message();
                msg.what = countTime;
                mOffHandler.sendMessage(msg);
            }
        };
        mOffTime.schedule(tt, 1000, 1000);
    }


}
