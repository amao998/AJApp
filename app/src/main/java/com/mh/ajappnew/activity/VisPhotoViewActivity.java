package com.mh.ajappnew.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.GetVisPhotoInfo;
import com.mh.ajappnew.jkid.GetVisPhotoReturnInfo;
import com.mh.ajappnew.tools.helper;
import com.mh.ajappnew.view.ZoomImageView;

import java.util.HashMap;
import java.util.Map;

public class VisPhotoViewActivity extends AppCompatActivity {

    private ZoomImageView zoomImg;
    private Button but_photo_view_exit;
    private TextView tv_photo_view_visname;
    private Config config = null;
    private String zpzlname = "";
    private String zpzl = "";
    private String detectid,jylsh,jycs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vis_photo_view);
        ///-----------------------------------------
        Intent i = getIntent();
        detectid = i.getStringExtra("detectid");
        jylsh = i.getStringExtra("jylsh");
        jycs = i.getStringExtra("jycs");
        zpzl = i.getStringExtra("zpzl");
        zpzlname = i.getStringExtra("zpzlname");
        config = i.getParcelableExtra("Config");

        tv_photo_view_visname = (TextView) findViewById(R.id.tv_photo_view_visname);
        tv_photo_view_visname.setText(zpzl+" "+ zpzlname);

        but_photo_view_exit = (Button) findViewById(R.id.but_photo_view_exit);
        but_photo_view_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        zoomImg = (ZoomImageView) findViewById(R.id.zoom_image_view);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.delfaut);
        zoomImg.setImage(bitmap);

        QueryVisPhoto();
    }

    private void QueryVisPhoto() {

        GetVisPhotoInfo info = new GetVisPhotoInfo();
        info.setDetectid(detectid);
        info.setJylsh(jylsh);
        info.setJycs(jycs);
        info.setZpzl(zpzl);
        info.setFlag("0");

        String data = JSON.toJSONString(info);

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handlerviewphoto;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetVisPhoto");
        maps.put("data", data);
        postRequestUtil.Run(maps);
    }

    Handler handlerviewphoto = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            if (msg.what == 0) {
                helper.ShowErrDialog("图片下载网络错误", msg.obj.toString(), VisPhotoViewActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {


                        GetVisPhotoReturnInfo infoA = JSON.parseObject(resultInfo.getData(), GetVisPhotoReturnInfo.class);
                        //helper.ShowErrDialog("提示", "图片:"+ infoA.getZpzl() +",上传成功", VisActivity.this);

                        byte[] decodedString = Base64.decode(infoA.getBase64(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        zoomImg.setImage(decodedByte);

                    } else {
                        // helper.ShowErrDialog("图片上传失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), VisActivity.this);
                    }
                } catch (Exception ex) {
                    helper.ShowErrDialog("图片下载系统错误", ex.getMessage(), VisPhotoViewActivity.this);
                }
            }
        }
    };
}
