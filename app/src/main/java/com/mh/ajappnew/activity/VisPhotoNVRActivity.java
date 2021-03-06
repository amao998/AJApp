package com.mh.ajappnew.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.bottompopfragmentmenu.BottomMenuFragment;
import com.mh.ajappnew.bottompopfragmentmenu.MenuItem;
import com.mh.ajappnew.bottompopfragmentmenu.MenuItemOnClickListener;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.db.DeviceTDDBHelper;
import com.mh.ajappnew.db.DeviceTDInfo;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.GetDeviceListInfo;
import com.mh.ajappnew.jkid.GetDeviceListReturnInfo;
import com.mh.ajappnew.jkid.GetVisPhotoInfo;
import com.mh.ajappnew.jkid.GetVisPhotoReturnInfo;
import com.mh.ajappnew.jkid.SendVideoCmdInfo;
import com.mh.ajappnew.tools.helper;
import com.mh.ajappnew.view.ComboxItemView;
import com.mh.ajappnew.view.ZoomImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class VisPhotoNVRActivity extends AppCompatActivity {

    private ZoomImageView zoomimage_photo_nvr_view;
    private Button but_photo_nvr_exit, but_photo_nvr_snap, but_photo_nvr_view;
    private TextView tv_photo_nvr_visname;
    private ComboxItemView combox_photo_nvr_channel;

    private Config config = null;
    private String zpzlname = "";
    private String zpzl = "";
    private String detectid, jylsh, jycs;

    private BottomMenuFragment bottomMenuFragment;
    private ProgressDialog pd;

    private TextView mOffTextView;
    private Handler mOffHandler;
    private Timer mOffTime;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vis_photo_nvr);
        //-----------------------------------------
        Intent i = getIntent();
        detectid = i.getStringExtra("detectid");
        jylsh = i.getStringExtra("jylsh");
        jycs = i.getStringExtra("jycs");
        zpzl = i.getStringExtra("zpzl");
        zpzlname = i.getStringExtra("zpzlname");
        config = i.getParcelableExtra("Config");

        InitControl();
    }

    private void InitControl() {
        zoomimage_photo_nvr_view = (ZoomImageView) findViewById(R.id.zoomimage_photo_nvr_view);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.delfaut);
        zoomimage_photo_nvr_view.setImage(bitmap);

        but_photo_nvr_exit = (Button) findViewById(R.id.but_photo_nvr_exit);
        but_photo_nvr_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("zpzl", zpzl);
                setResult(1000, intent);
                finish();
            }
        });

        tv_photo_nvr_visname = (TextView) findViewById(R.id.tv_photo_nvr_visname);
        tv_photo_nvr_visname.setText(zpzl + " " + zpzlname);

        combox_photo_nvr_channel = (ComboxItemView) findViewById(R.id.combox_photo_nvr_channel);
        combox_photo_nvr_channel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitItemsCombox();
            }
        });

        but_photo_nvr_view = (Button) findViewById(R.id.but_photo_nvr_view);
        but_photo_nvr_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryVisPhoto();
            }
        });

        but_photo_nvr_snap = (Button) findViewById(R.id.but_photo_nvr_snap);
        but_photo_nvr_snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (combox_photo_nvr_channel.getText().equals("")) {
                    helper.ShowErrDialog("??????", "??????????????????.", VisPhotoNVRActivity.this);
                    return;
                }
                SendVideoCmd();
            }
        });
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
        public void handleMessage(Message msg) {// handler???????????????????????????????????????
            if (msg.what == 0) {
                helper.ShowErrDialog("????????????????????????", msg.obj.toString(), VisPhotoNVRActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        GetVisPhotoReturnInfo infoA = JSON.parseObject(resultInfo.getData(), GetVisPhotoReturnInfo.class);
                        //helper.ShowErrDialog("??????", "??????:"+ infoA.getZpzl() +",????????????", VisActivity.this);

                        byte[] decodedString = Base64.decode(infoA.getBase64(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        zoomimage_photo_nvr_view.setImage(decodedByte);
                        Toast.makeText(VisPhotoNVRActivity.this, "??????????????????", Toast.LENGTH_LONG).show();

                    } else {
                        // helper.ShowErrDialog("??????????????????", URLEncodeing.toURLDecoder(resultInfo.getMessage()), VisActivity.this);
                        Toast.makeText(VisPhotoNVRActivity.this, "??????????????????", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    helper.ShowErrDialog("????????????????????????", ex.getMessage(), VisPhotoNVRActivity.this);
                }
            }
        }
    };


    private void InitBottomUenu(List<GetDeviceListReturnInfo> list) {
        bottomMenuFragment = new BottomMenuFragment();

        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        for (int i = 0; i <= list.size() - 1; i++) {
            MenuItem menuItem1 = new MenuItem();
            menuItem1.setText(list.get(i).getIdandvalue());
            menuItem1.setItem_name(list.get(i).getId());
            menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);


            menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
                @Override
                public void onClickMenuItem(View v, MenuItem menuItem) {
                    //Toast.makeText(VisActivity.this, menuItem.getText() + " " + menuItem.getItem_name(), Toast.LENGTH_LONG).show();
                    combox_photo_nvr_channel.setText(menuItem.getText());
                    combox_photo_nvr_channel.setTag(menuItem.getItem_name());
                }
            });

            menuItemList.add(menuItem1);
        }

        bottomMenuFragment.setMenuItems(menuItemList);

    }


    private void InitItemsCombox1() {
        GetDeviceListInfo info = new GetDeviceListInfo();
        info.setDevicetype("Photo");
        info.setZpzl(zpzl);
        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(VisPhotoNVRActivity.this, "??????", "???????????????...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = comboxhandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetDeviceList");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler comboxhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler???????????????????????????????????????
            pd.dismiss();// ??????ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("????????????????????????", msg.obj.toString(), VisPhotoNVRActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        List<GetDeviceListReturnInfo> list = JSON.parseArray(resultInfo.getData(), GetDeviceListReturnInfo.class);
                        InitBottomUenu(list);
                        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
                    } else {
                        helper.ShowErrDialog("??????????????????", resultInfo.getMessage(), VisPhotoNVRActivity.this);
                    }

                } catch (Exception ex) {
                    Toast.makeText(VisPhotoNVRActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("????????????????????????", ex.getMessage(), VisPhotoNVRActivity.this);
                }
            }

        }
    };

    private void InitItemsCombox() {
        try {
            DeviceTDDBHelper db = DeviceTDDBHelper.getInstance(VisPhotoNVRActivity.this, 1);
            db.openReadLink();
            ArrayList<DeviceTDInfo> list = db.query("PhotoType='" + zpzl + "'");
            db.closeLink();
            //------------------------------------------------------------------------------------
            ArrayList<GetDeviceListReturnInfo> data = new ArrayList<GetDeviceListReturnInfo>();
            for (int i = 0; i <= list.size() - 1; i++) {
                GetDeviceListReturnInfo info = new GetDeviceListReturnInfo();
                info.setId(list.get(i).device_sn);
                info.setValue(list.get(i).device_name);
                info.setIdandvalue(list.get(i).device_sn + " " + list.get(i).device_name);
                info.setZpzl(list.get(i).PhotoType);
                data.add(info);
            }
            InitBottomUenu(data);
            bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
        } catch (Exception ex) {
            helper.ShowErrDialog("????????????????????????", ex.getMessage(), VisPhotoNVRActivity.this);
        }
    }

    private void SendVideoCmd() {
        SendVideoCmdInfo info = new SendVideoCmdInfo();
        info.setDevicetype("Photo");
        info.setZpzl(zpzl);
        info.setChannel(combox_photo_nvr_channel.getTag().toString());
        info.setDetectid(detectid);
        info.setFlag("");
        info.setJylsh(jylsh);
        info.setJycs(jycs);
        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(VisPhotoNVRActivity.this, "??????", "?????????????????????...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = cmdhandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "SendVideoCmd");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler cmdhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler???????????????????????????????????????
            pd.dismiss();// ??????ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("????????????????????????", msg.obj.toString(), VisPhotoNVRActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        //helper.ShowErrDialog("????????????", resultInfo.getMessage(), VisPhotoNVRActivity.this);
                        initDialog(resultInfo.getMessage());
                    } else {
                        helper.ShowErrDialog("????????????????????????", resultInfo.getMessage(), VisPhotoNVRActivity.this);
                    }

                } catch (Exception ex) {
                    Toast.makeText(VisPhotoNVRActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("????????????????????????", ex.getMessage(), VisPhotoNVRActivity.this);
                }
            }

        }
    };


    //////???????????????

    void initDialog(String desc) {

        mOffTextView = new TextView(this);
        mOffTextView.setText("????????????:" + desc + ",??????????????? 5");

        mDialog = new AlertDialog.Builder(this)
                .setTitle("??????")
                .setCancelable(false)
                .setView(mOffTextView) ////

                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mOffTime.cancel();
                        QueryVisPhoto();
                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
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

                    ////?????????????????????
                    mOffTextView.setText("   ???????????????????????????" + msg.what);

                } else {
                    ////???????????????????????????

                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    QueryVisPhoto();
                    mOffTime.cancel();
                }
                super.handleMessage(msg);
            }

        };
        //////?????????

        mOffTime = new Timer(true);
        TimerTask tt = new TimerTask() {
            int countTime = 6;

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
