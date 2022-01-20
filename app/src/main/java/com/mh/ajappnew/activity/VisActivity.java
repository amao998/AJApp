package com.mh.ajappnew.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.adapter.Dialog1Adapter;
import com.mh.ajappnew.adapter.VisListAdapter;
import com.mh.ajappnew.bottompopfragmentmenu.BottomMenuFragment;
import com.mh.ajappnew.bottompopfragmentmenu.MenuItem;
import com.mh.ajappnew.bottompopfragmentmenu.MenuItemOnClickListener;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.comm.VisInfo;
import com.mh.ajappnew.db.DeviceDBHelper;
import com.mh.ajappnew.db.DeviceInfo;
import com.mh.ajappnew.db.ItemsDBHelper;
import com.mh.ajappnew.db.ItemsInfo;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.GetItemListReturnInfo;
import com.mh.ajappnew.jkid.GetPDALineInfo;
import com.mh.ajappnew.jkid.GetPDALineReturnInfo;
import com.mh.ajappnew.jkid.GetVisInputData;
import com.mh.ajappnew.jkid.GetVisInputDataReturnInfo;
import com.mh.ajappnew.jkid.GetVisPhotoInfo;
import com.mh.ajappnew.jkid.GetVisPhotoReturnInfo;
import com.mh.ajappnew.jkid.InsStartInfo;
import com.mh.ajappnew.jkid.InsStartReturnInfo;
import com.mh.ajappnew.jkid.InsStopInfo;
import com.mh.ajappnew.jkid.InsStopReturnInfo;
import com.mh.ajappnew.jkid.SaveVisInputDataInfo;
import com.mh.ajappnew.tools.CustomDialog;
import com.mh.ajappnew.tools.DateUtil;
import com.mh.ajappnew.tools.DisplayUtil;
import com.mh.ajappnew.tools.URLEncodeing;
import com.mh.ajappnew.tools.helper;
import com.mh.ajappnew.view.ComboxItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VisActivity extends AppCompatActivity {
    private ProgressDialog pd;
    private ProgressDialog pd1;
    private String detectid, jylsh, jycs, ywlx;
    private String hphmmac,hphmcaption;
    private Config config = null;

    private ListView lv_vis_data;
    private List<VisInfo> EnvList = new ArrayList<VisInfo>();
    private VisListAdapter cadapter;

    private TextView tv_vis_hphmmac, tv_vis_jylsh, tv_vis_default, tv_vis_jycs,tv_vis_ycy,tv_vis_jyy;

    private ImageView img_vis_wgjyy, img_vis_ycy, img_vis_dtjyy, img_vis_jbr;
    private String zs, zxzs, qtzs;
    private EditText et_vis_cwkc, et_vis_cwkk, et_vis_cwkg, et_vis_zj, et_vis_dczxlhwsd, et_vis_dcqtlhwsd, et_vis_gchwsd,
            et_vis_yzzgd, et_vis_yzygd, et_vis_yzzygdc, et_vis_zhzzgd, et_vis_zhzygd, et_vis_zhzzygdc, et_vis_gczgd, et_vis_gcygd, et_vis_gczygdc,
            et_vis_fxpzdzyzdl, et_vis_cxlbgd, et_vis_cxlbgdgc;

    private Button but_vis_quit, but_vis_photo, but_vis_func;
    private BottomMenuFragment bottomMenuFragment;
    private ComboxItemView combox_vis_line;

    private String kssj = "";
    private GetVisInputDataReturnInfo visinputinfo = new GetVisInputDataReturnInfo();
    private String zpzlWG = "0901";
    private String zpzlYCY = "0902";
    private String zpzlDTY = "0903";
    private String zpzlJBR = "0904";

    private String device_group = "";
    private LinearLayout line_vis_lwcx, line_vis_wg, line_vis_dt, line_vis_sign;
    private EditText et_vis_lwcxjgms, et_vis_lwcxlxdz, et_vis_lwcxsjhm, et_vis_lwcxsyr, et_vis_lwcxyzbm, et_vis_jyyjy, et_vis_bz;
    private TextView tv_vis_fxpzdzyzdlpd, tv_vis_yzzygdcpd, tv_vis_zhzzygdcpd, tv_vis_gczygdcpd;
    private  Dialog1Adapter  dialog1Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vis);

        DisplayUtil.setDefaultDisplay(VisActivity.this);

        //---------------------------------------------
        Intent i = getIntent();
        config = i.getParcelableExtra("Config");
        hphmmac = i.getStringExtra("hphmmac");
        detectid = i.getStringExtra("detectid");
        jylsh = i.getStringExtra("jylsh");
        jycs = i.getStringExtra("jycs");
        device_group = i.getStringExtra("device_group");
        ywlx = i.getStringExtra("ywlx");
        hphmcaption=i.getStringExtra("hphmcaption");

        tv_vis_hphmmac = (TextView) findViewById(R.id.tv_vis_hphmmac);
        tv_vis_jylsh = (TextView) findViewById(R.id.tv_vis_jylsh);
        tv_vis_jycs = (TextView) findViewById(R.id.tv_vis_jycs);

        tv_vis_fxpzdzyzdlpd = (TextView) findViewById(R.id.tv_vis_fxpzdzyzdlpd);
        tv_vis_yzzygdcpd = (TextView) findViewById(R.id.tv_vis_yzzygdcpd);
        tv_vis_zhzzygdcpd = (TextView) findViewById(R.id.tv_vis_zhzzygdcpd);
        tv_vis_gczygdcpd = (TextView) findViewById(R.id.tv_vis_gczygdcpd);

        et_vis_fxpzdzyzdl = (EditText) findViewById(R.id.et_vis_fxpzdzyzdl);
        et_vis_cxlbgd = (EditText) findViewById(R.id.et_vis_cxlbgd);
        et_vis_cxlbgdgc = (EditText) findViewById(R.id.et_vis_cxlbgdgc);

        et_vis_yzzgd = (EditText) findViewById(R.id.et_vis_yzzgd);
        et_vis_yzygd = (EditText) findViewById(R.id.et_vis_yzygd);
        et_vis_yzzygdc = (EditText) findViewById(R.id.et_vis_yzzygdc);

        et_vis_zhzzgd = (EditText) findViewById(R.id.et_vis_zhzzgd);
        et_vis_zhzygd = (EditText) findViewById(R.id.et_vis_zhzygd);
        et_vis_zhzzygdc = (EditText) findViewById(R.id.et_vis_zhzzygdc);

        et_vis_gczgd = (EditText) findViewById(R.id.et_vis_gczgd);
        et_vis_gcygd = (EditText) findViewById(R.id.et_vis_gcygd);
        et_vis_gczygdc = (EditText) findViewById(R.id.et_vis_gczygdc);

        et_vis_cwkc = (EditText) findViewById(R.id.et_vis_cwkc);
        et_vis_cwkk = (EditText) findViewById(R.id.et_vis_cwkk);
        et_vis_cwkg = (EditText) findViewById(R.id.et_vis_cwkg);
        et_vis_zj = (EditText) findViewById(R.id.et_vis_zj);
        et_vis_dczxlhwsd = (EditText) findViewById(R.id.et_vis_dczxlhwsd);
        et_vis_dcqtlhwsd = (EditText) findViewById(R.id.et_vis_dcqtlhwsd);
        et_vis_gchwsd = (EditText) findViewById(R.id.et_vis_gchwsd);

        et_vis_lwcxjgms = (EditText) findViewById(R.id.et_vis_lwcxjgms);
        et_vis_lwcxlxdz = (EditText) findViewById(R.id.et_vis_lwcxlxdz);
        et_vis_lwcxsjhm = (EditText) findViewById(R.id.et_vis_lwcxsjhm);
        et_vis_lwcxsyr = (EditText) findViewById(R.id.et_vis_lwcxsyr);
        et_vis_lwcxyzbm = (EditText) findViewById(R.id.et_vis_lwcxyzbm);

        et_vis_bz = (EditText) findViewById(R.id.et_vis_bz);
        et_vis_jyyjy = (EditText) findViewById(R.id.et_vis_jyyjy);

        tv_vis_hphmmac.setText(hphmcaption);
        tv_vis_jylsh.setText(detectid);
        tv_vis_jycs.setText(jycs);

        LoadCaptionAndLine();

        LoadButton();

        LoadData();
        LoadSign();

        LoadHwsd();
        LoadGdc();
        LoadFxpzyzdl();
        LoadGdcYZ();
        LoadGdcZH();
        LoadGdcGC();

        LoadPDALine();

        QueryVisPhoto(detectid, zpzlWG);
        QueryVisPhoto(detectid, zpzlDTY);
        QueryVisPhoto(detectid, zpzlYCY);
        QueryVisPhoto(detectid, zpzlJBR);
    }

    private void LoadCaptionAndLine() {
        line_vis_lwcx = (LinearLayout) findViewById(R.id.line_vis_lwcx);
        line_vis_wg = (LinearLayout) findViewById(R.id.line_vis_wg);
        line_vis_dt = (LinearLayout) findViewById(R.id.line_vis_dt);
        line_vis_sign = (LinearLayout) findViewById(R.id.line_vis_sign);

        line_vis_lwcx.setVisibility(View.GONE);
        line_vis_wg.setVisibility(View.GONE);
        line_vis_dt.setVisibility(View.GONE);
        line_vis_sign.setVisibility(View.GONE);

        tv_vis_default = (TextView) findViewById(R.id.tv_vis_default);
        if (device_group.equals("LWCX") == true) {
            tv_vis_default.setText("车辆联网查询");
            line_vis_lwcx.setVisibility(View.VISIBLE);
        } else if (device_group.equals("CLWYX") == true) {
            tv_vis_default.setText("车辆唯一性查验");
        } else if (device_group.equals("WG") == true) {
            tv_vis_default.setText("车辆外观查验");
            line_vis_wg.setVisibility(View.VISIBLE);
            line_vis_dt.setVisibility(View.VISIBLE);
            line_vis_sign.setVisibility(View.VISIBLE);
        } else if (device_group.equals("DPDG") == true) {
            tv_vis_default.setText("车辆底盘查验");
        } else if (device_group.equals("DPDT") == true) {
            tv_vis_default.setText("车辆动态查验");
            line_vis_dt.setVisibility(View.VISIBLE);
        }
    }

    private void LoadButton() {
        tv_vis_jyy=(TextView)findViewById(R.id.tv_vis_jyy);
        tv_vis_ycy=(TextView)findViewById(R.id.tv_vis_ycy);
        tv_vis_ycy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryDataItem("101");
            }
        });


        but_vis_func = (Button) findViewById(R.id.but_vis_func);
        but_vis_func.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kssj.equals("") == true) {
                    InsStart();
                } else {
                    SaveData();
                }
            }
        });

        but_vis_quit = (Button) findViewById(R.id.but_vis_quit);
        but_vis_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowErrDialog("提示", "是否确定退出");
            }
        });


        combox_vis_line = (ComboxItemView) findViewById(R.id.combox_vis_line);
        combox_vis_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
            }
        });
        combox_vis_line.setText(config.getLine());


        but_vis_photo = (Button) findViewById(R.id.but_vis_photo);
        but_vis_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(VisActivity.this, VisPhotoActivity.class);
                //加入参数，传递给AnotherActivity
                intent1.putExtra("Config", config);
                intent1.putExtra("hphmmac", hphmmac);
                intent1.putExtra("detectid", detectid);
                intent1.putExtra("jylsh", jylsh);
                intent1.putExtra("jycs", jycs);
                intent1.putExtra("zt", "1");
                intent1.putExtra("ywlx", ywlx);
                intent1.putExtra("hphmcaption", hphmcaption);


                startActivityForResult(intent1, 44);
            }
        });
    }


    private void LoadGdc() {
        class gdcyzTextWatcher implements TextWatcher {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String temp = GetCha(et_vis_yzzgd.getText().toString(), et_vis_yzygd.getText().toString());
                et_vis_yzzygdc.setText(temp);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }

        class gdczhzTextWatcher implements TextWatcher {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String temp = GetCha(et_vis_zhzzgd.getText().toString(), et_vis_zhzygd.getText().toString());
                et_vis_zhzzygdc.setText(temp);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }

        class gdcgcTextWatcher implements TextWatcher {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String temp = GetCha(et_vis_gcygd.getText().toString(), et_vis_gczgd.getText().toString());
                et_vis_gczygdc.setText(temp);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }

        et_vis_yzygd.addTextChangedListener(new gdcyzTextWatcher());
        et_vis_yzzgd.addTextChangedListener(new gdcyzTextWatcher());

        et_vis_zhzygd.addTextChangedListener(new gdczhzTextWatcher());
        et_vis_zhzzgd.addTextChangedListener(new gdczhzTextWatcher());

        et_vis_gczgd.addTextChangedListener(new gdcgcTextWatcher());
        et_vis_gcygd.addTextChangedListener(new gdcgcTextWatcher());

    }

    private String GetCha(String value1, String value2) {
        String temp = "";
        if (value1 == null || value2 == null) {
            return "";
        }

        if (value1.equals("") || value2.equals("")) {
            return "";
        }
        Integer i1 = Integer.parseInt(value1);
        Integer i2 = Integer.parseInt(value2);

        temp = String.valueOf(Math.abs(i1 - i2));
        return temp;

    }

    private void LoadFxpzyzdl() {
        class fxpzdlTextWatcher implements TextWatcher {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (visinputinfo.getMaxzdlstand().equals("") == true) {
                    return;
                }

                if (et_vis_fxpzdzyzdl.getText().toString().equals("") == true) {
                    tv_vis_fxpzdzyzdlpd.setText("");
                    return;
                }

                double maxzdl = Double.parseDouble(et_vis_fxpzdzyzdl.getText().toString());
                double stand = Double.parseDouble(visinputinfo.getMaxzdlstand());

                if (maxzdl <= stand) {
                    tv_vis_fxpzdzyzdlpd.setText("合格");
                } else {
                    tv_vis_fxpzdzyzdlpd.setText("不合格");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }
        et_vis_fxpzdzyzdl.addTextChangedListener(new fxpzdlTextWatcher());
    }

    private void LoadGdcYZ() {
        class gdcTextWatcher implements TextWatcher {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (visinputinfo.getGdcqstand().equals("") == true) {
                    return;
                }

                if (et_vis_yzzygdc.getText().toString().equals("") == true) {
                    tv_vis_yzzygdcpd.setText("");
                    return;
                }

                double yzgdc = Double.parseDouble(et_vis_yzzygdc.getText().toString());
                double stand = Double.parseDouble(visinputinfo.getGdcqstand());

                if (yzgdc <= stand) {
                    tv_vis_yzzygdcpd.setText("合格");
                } else {
                    tv_vis_yzzygdcpd.setText("不合格");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }
        et_vis_yzzygdc.addTextChangedListener(new gdcTextWatcher());
    }

    private void LoadGdcZH() {
        class gdcTextWatcher implements TextWatcher {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (visinputinfo.getGdchstand().equals("") == true) {
                    return;
                }

                if (et_vis_zhzzygdc.getText().toString().equals("") == true) {
                    tv_vis_yzzygdcpd.setText("");
                    return;
                }

                double yzgdc = Double.parseDouble(et_vis_zhzzygdc.getText().toString());
                double stand = Double.parseDouble(visinputinfo.getGdchstand());

                if (yzgdc <= stand) {
                    tv_vis_zhzzygdcpd.setText("合格");
                } else {
                    tv_vis_zhzzygdcpd.setText("不合格");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }
        et_vis_zhzzygdc.addTextChangedListener(new gdcTextWatcher());
    }

    private void LoadGdcGC() {
        class gdcTextWatcher implements TextWatcher {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (visinputinfo.getGdchstand().equals("") == true) {
                    return;
                }

                if (et_vis_gczygdc.getText().toString().equals("") == true) {
                    tv_vis_gczygdcpd.setText("");
                    return;
                }

                double yzgdc = Double.parseDouble(et_vis_gczygdc.getText().toString());
                double stand = Double.parseDouble(visinputinfo.getGdchstand());

                if (yzgdc <= stand) {
                    tv_vis_gczygdcpd.setText("合格");
                } else {
                    tv_vis_gczygdcpd.setText("不合格");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        }
        et_vis_gczygdc.addTextChangedListener(new gdcTextWatcher());
    }

    private void LoadSign() {
        img_vis_wgjyy = (ImageView) findViewById(R.id.img_vis_wgjyy);
//        img_vis_wgjyy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(VisActivity.this, SignActivity.class);
//                //加入参数，传递给AnotherActivity
//                intent.putExtra("Config", config);
//                intent.putExtra("typeid", zpzlWG);
//                intent.putExtra("jylsh", jylsh);
//                intent.putExtra("jycs", jycs);
//                intent.putExtra("detectid", detectid);
//                intent.putExtra("typename", "外检员签名");
//                startActivityForResult(intent, 11);
//            }
//        });

        img_vis_ycy = (ImageView) findViewById(R.id.img_vis_ycy);
        img_vis_ycy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VisActivity.this, SignActivity.class);
                //加入参数，传递给AnotherActivity
                intent.putExtra("Config", config);
                intent.putExtra("typeid", zpzlYCY);
                intent.putExtra("jylsh", jylsh);
                intent.putExtra("jycs", jycs);
                intent.putExtra("detectid", detectid);
                intent.putExtra("typename", "引车员签名");
                startActivityForResult(intent, 11);
            }
        });

        img_vis_dtjyy = (ImageView) findViewById(R.id.img_vis_dtjyy);
        img_vis_dtjyy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VisActivity.this, SignActivity.class);
                //加入参数，传递给AnotherActivity
                intent.putExtra("Config", config);
                intent.putExtra("typeid", zpzlDTY);
                intent.putExtra("jylsh", jylsh);
                intent.putExtra("jycs", jycs);
                intent.putExtra("detectid", detectid);
                intent.putExtra("typename", "动态检验员签名");
                startActivityForResult(intent, 11);
            }
        });

        img_vis_jbr = (ImageView) findViewById(R.id.img_vis_jbr);
//        img_vis_jbr.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(VisActivity.this, SignActivity.class);
//                //加入参数，传递给AnotherActivity
//                intent.putExtra("Config", config);
//                intent.putExtra("typeid", zpzlJBR);
//                intent.putExtra("jylsh", jylsh);
//                intent.putExtra("jycs", jycs);
//                intent.putExtra("detectid", detectid);
//                intent.putExtra("typename", "经办人签名");
//                startActivityForResult(intent, 11);
//            }
//        });

    }

    private void LoadHwsd() {
        Button but_vis_dczxlhwsd = (Button) findViewById(R.id.but_vis_dczxlhwsd);
        but_vis_dczxlhwsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VisActivity.this, LTInputActivity.class);
                //加入参数，传递给AnotherActivity
                String value = et_vis_dczxlhwsd.getText().toString();
                intent.putExtra("typeid", "1");
                intent.putExtra("starti", "1");
                intent.putExtra("endi", zxzs);
                intent.putExtra("value", value);
                intent.putExtra("stand", visinputinfo.getLtzxstand());

                startActivityForResult(intent, 12);
            }
        });

        Button but_vis_dcqtlhwsd = (Button) findViewById(R.id.but_vis_dcqtlhwsd);
        but_vis_dcqtlhwsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VisActivity.this, LTInputActivity.class);
                //加入参数，传递给AnotherActivity
                String value = et_vis_dcqtlhwsd.getText().toString();
                intent.putExtra("typeid", "2");
                intent.putExtra("starti", String.valueOf(Integer.parseInt(zxzs) + 1));
                intent.putExtra("endi", zs);
                intent.putExtra("value", value);
                intent.putExtra("stand", visinputinfo.getLtqtstand());

                startActivityForResult(intent, 12);
            }
        });

        Button but_vis_gchwsd = (Button) findViewById(R.id.but_vis_gchwsd);
        but_vis_gchwsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VisActivity.this, LTInputActivity.class);
                //加入参数，传递给AnotherActivity
                String value = et_vis_gchwsd.getText().toString();
                intent.putExtra("typeid", "3");
                intent.putExtra("starti", "1");
                intent.putExtra("endi", zs);
                intent.putExtra("value", value);
                intent.putExtra("stand", visinputinfo.getLtqtstand());

                startActivityForResult(intent, 12);
            }
        });
    }


    private void LoadData() {
        pd = ProgressDialog.show(this, "提示", "查询中...");
        pd.setCancelable(true);

        GetVisInputData info = new GetVisInputData();
        info.setDetectid(detectid);
        info.setJylsh(jylsh);
        info.setJycs(jycs);
        info.setDevice_group(device_group);
        String data = JSON.toJSONString(info);

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetVisInputData");
        maps.put("data", data);
        postRequestUtil.Run(maps);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("外检查询网络错误", msg.obj.toString(), VisActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        //1.加载数据
                        visinputinfo = JSON.parseObject(resultInfo.getData(), GetVisInputDataReturnInfo.class);
                        EvalTxt(visinputinfo);

                    } else {
                        helper.ShowErrDialog("外检查询失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), VisActivity.this);
                    }
                } catch (Exception ex) {
                    helper.ShowErrDialog("外检查询系统错误", ex.getMessage(), VisActivity.this);
                }
            }
        }
    };

    private void EvalTxt(GetVisInputDataReturnInfo infoA) {
        et_vis_cwkc.setText(infoA.getCwkc());
        et_vis_cwkk.setText(infoA.getCwkk());
        et_vis_cwkg.setText(infoA.getCwkg());

        et_vis_zj.setText(infoA.getZj());
        et_vis_dcqtlhwsd.setText(infoA.getDczxlhwsd());
        et_vis_dcqtlhwsd.setText(infoA.getDcqtlhwsd());
        et_vis_gchwsd.setText(infoA.getGchwsd());

        et_vis_yzzgd.setText(infoA.getYzzgd());
        et_vis_yzygd.setText(infoA.getYzygd());
        et_vis_yzzygdc.setText(infoA.getYzzygdc());

        et_vis_zhzzgd.setText(infoA.getZhzzgd());
        et_vis_zhzygd.setText(infoA.getZhzygd());
        et_vis_zhzzygdc.setText(infoA.getZhzzygdc());

        et_vis_gczgd.setText(infoA.getGczgd());
        et_vis_gcygd.setText(infoA.getGcygd());
        et_vis_gczygdc.setText(infoA.getGczygdc());

        et_vis_fxpzdzyzdl.setText(infoA.getFxpzdzyzdl());
        et_vis_cxlbgd.setText(infoA.getCxlbgd());
        et_vis_cxlbgdgc.setText(infoA.getCxlbgdgc());

        et_vis_dczxlhwsd.setText(infoA.getDczxlhwsd());
        et_vis_dcqtlhwsd.setText(infoA.getDcqtlhwsd());
        et_vis_gchwsd.setText(infoA.getGchwsd());

        et_vis_lwcxjgms.setText(infoA.getLwcxjgms());
        et_vis_lwcxlxdz.setText(infoA.getLxdz());
        et_vis_lwcxsjhm.setText(infoA.getSjhm());
        et_vis_lwcxsyr.setText(infoA.getSyr());
        et_vis_lwcxyzbm.setText(infoA.getYzbm());

        et_vis_bz.setText(infoA.getBz());
        et_vis_jyyjy.setText(infoA.getJyyjy());

        tv_vis_ycy.setText(infoA.getYcy());
        tv_vis_jyy.setText(infoA.getJyy());

        zs = infoA.getZs();
        zxzs = infoA.getZxzs();
        qtzs = String.valueOf(Integer.parseInt(infoA.getZs()) - Integer.parseInt(infoA.getZxzs()));

        EnvList = infoA.getChoicedata();
        lv_vis_data = (ListView) findViewById(R.id.lv_vis_data);

        ViewGroup.LayoutParams params = lv_vis_data.getLayoutParams();
        params.height = 73 * EnvList.size();
        lv_vis_data.setLayoutParams(params);

        cadapter = new VisListAdapter(VisActivity.this, EnvList, config, detectid, jylsh, jycs);
        lv_vis_data.setAdapter(cadapter);
    }


    private void SaveData() {
        if (kssj.equals("") == true) {
            helper.ShowErrDialog("提示", "检测未开始", VisActivity.this);
            return;
        }

        if (combox_vis_line.getText().equals("") == true) {
            helper.ShowErrDialog("提示", "未选择线号", VisActivity.this);
            return;
        }

        if (img_vis_wgjyy.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.pen1).getConstantState())) {
            helper.ShowErrDialog("提示", "查验员未签名", VisActivity.this);
            return;
        }

        if (img_vis_jbr.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.pen1).getConstantState())) {
            helper.ShowErrDialog("提示", "经办人未签名", VisActivity.this);
            return;
        }

        SaveVisInputDataInfo info = new SaveVisInputDataInfo();
        info.setDetectid(detectid);
        info.setLine(combox_vis_line.getText());
        info.setKssj(kssj);
        info.setJssj(DateUtil.getNowDateTime1());
        info.setJyy(config.getJyy());
        info.setDevice_group(device_group);

        info.setJylsh(jylsh);
        info.setJycs(jycs);

        info.setZs(visinputinfo.getZs());
        info.setZxzs(visinputinfo.getZxzs());
        info.setCwkc(et_vis_cwkc.getText().toString());
        info.setCwkk(et_vis_cwkk.getText().toString());
        info.setCwkg(et_vis_cwkg.getText().toString());

        info.setZj(et_vis_zj.getText().toString());
        info.setDczxlhwsd(et_vis_dczxlhwsd.getText().toString());
        info.setDcqtlhwsd(et_vis_dcqtlhwsd.getText().toString());
        info.setGchwsd(et_vis_gchwsd.getText().toString());

        info.setYzzgd(et_vis_yzzgd.getText().toString());
        info.setYzygd(et_vis_yzygd.getText().toString());
        info.setYzzygdc(et_vis_yzzygdc.getText().toString());

        info.setZhzzgd(et_vis_zhzzgd.getText().toString());
        info.setZhzygd(et_vis_zhzygd.getText().toString());
        info.setZhzzygdc(et_vis_zhzzygdc.getText().toString());

        info.setGczgd(et_vis_gczgd.getText().toString());
        info.setGcygd(et_vis_gcygd.getText().toString());
        info.setGczygdc(et_vis_gczygdc.getText().toString());

        info.setCxlbgd(et_vis_cxlbgd.getText().toString());
        info.setCxlbgdgc(et_vis_cxlbgdgc.getText().toString());
        info.setFxpzdzyzdl(et_vis_fxpzdzyzdl.getText().toString());

        info.setZs(visinputinfo.getZs());
        info.setZxzs(visinputinfo.getZxzs());

        info.setLwcxjgms(et_vis_lwcxjgms.getText().toString());
        info.setLxdz(et_vis_lwcxlxdz.getText().toString());
        info.setSjhm(et_vis_lwcxsjhm.getText().toString());
        info.setSyr(et_vis_lwcxsyr.getText().toString());
        info.setYzbm(et_vis_lwcxyzbm.getText().toString());

        info.setBz(et_vis_bz.getText().toString());
        info.setJyyjy(et_vis_jyyjy.getText().toString());
        info.setYcy(tv_vis_ycy.getText().toString());

        info.setChoicedata(EnvList);

        pd1 = ProgressDialog.show(this, "提示", "保存中...");
        pd1.setCancelable(true);
        String data = JSON.toJSONString(info);

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handlersave;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "SaveVisInputData");
        maps.put("data", data);
        postRequestUtil.Run(maps);
    }

    Handler handlersave = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd1.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("保存网络错误", msg.obj.toString(), VisActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        //ShowErrDialog("保存成功",  resultInfo.getMessage()+",是否退出?");
                        InsStop();
                    } else {
                        helper.ShowErrDialog("保存失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), VisActivity.this);
                    }
                } catch (Exception ex) {
                    helper.ShowErrDialog("保存系统错误", ex.getMessage(), VisActivity.this);
                }
            }
        }
    };


    private void ShowErrDialog(String title, String desc) {
        CustomDialog customDialog = new CustomDialog(VisActivity.this);
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
                finish();
            }
        });
        customDialog.show();
        customDialog.setCancelVisable(true);
    }


    private void LoadPDALine1() {
        pd1 = ProgressDialog.show(this, "提示", "查询中...");
        pd1.setCancelable(true);

        GetPDALineInfo info = new GetPDALineInfo();
        info.setDevice_group("WG");
        String data = JSON.toJSONString(info);

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handlerline;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetPDALine");
        maps.put("data", data);
        postRequestUtil.Run(maps);
    }

    Handler handlerline = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd1.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("线号查询网络错误", msg.obj.toString(), VisActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        //1.加载数据
                        List<GetPDALineReturnInfo> infoA = JSON.parseArray(resultInfo.getData(), GetPDALineReturnInfo.class);
                        InitBottomUenu(infoA);
                    } else {
                        helper.ShowErrDialog("线号查询失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), VisActivity.this);
                    }
                } catch (Exception ex) {
                    helper.ShowErrDialog("线号查询系统错误", ex.getMessage(), VisActivity.this);
                }
            }
        }
    };

    private void LoadPDALine() {
        DeviceDBHelper db = DeviceDBHelper.getInstance(VisActivity.this, 1);
        db.openReadLink();
        ArrayList<DeviceInfo> list = db.query("device_group='" + device_group + "'");
        db.closeLink();
        //------------------------------------------------------------------------------------
        ArrayList<GetPDALineReturnInfo> data = new ArrayList<GetPDALineReturnInfo>();
        for (int i = 0; i <= list.size() - 1; i++) {
            GetPDALineReturnInfo info = new GetPDALineReturnInfo();
            info.setId(list.get(i).id);
            info.setDevice_group(list.get(i).device_group);
            data.add(info);
        }
        InitBottomUenu(data);
    }


    private void InitBottomUenu(List<GetPDALineReturnInfo> infoA) {
        bottomMenuFragment = new BottomMenuFragment();

        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        for (int i = 0; i <= infoA.size() - 1; i++) {
            MenuItem menuItem1 = new MenuItem();
            menuItem1.setText(infoA.get(i).getId() + "线");
            menuItem1.setItem_name(infoA.get(i).getId());
            menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);

            menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
                @Override
                public void onClickMenuItem(View v, MenuItem menuItem) {
                    Log.i("", "onClickMenuItem: ");
                    //Toast.makeText(VisActivity.this, menuItem.getText() + " " + menuItem.getItem_name(), Toast.LENGTH_LONG).show();
                    combox_vis_line.setText(menuItem.getItem_name());
                    combox_vis_line.setValue(menuItem.getItem_name());
                }
            });

            menuItemList.add(menuItem1);
        }

        bottomMenuFragment.setMenuItems(menuItemList);
    }

    private void InsStart() {
        pd1 = ProgressDialog.show(this, "提示", "开始中...");
        pd1.setCancelable(true);

        InsStartInfo info = new InsStartInfo();
        info.setDetectid(detectid);
        info.setDevice_group(device_group);
        info.setJylsh(jylsh);
        info.setJycs(jycs);
        info.setLine(combox_vis_line.getText());
        String data = JSON.toJSONString(info);

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handlerinsstart;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "InsStart");
        maps.put("data", data);
        postRequestUtil.Run(maps);
    }

    Handler handlerinsstart = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd1.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("开始信号查询网络错误", msg.obj.toString(), VisActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        InsStartReturnInfo infoA = JSON.parseObject(resultInfo.getData(), InsStartReturnInfo.class);
                        kssj = infoA.getKssj();
                        helper.ShowErrDialog("提示", "开始成功", VisActivity.this);
                        but_vis_func.setText("提交");
                        if (device_group.equals("WG") == true) {
                            but_vis_photo.setVisibility(View.VISIBLE);
                        }
                    } else {
                        helper.ShowErrDialog("开始信号查询失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), VisActivity.this);
                    }
                } catch (Exception ex) {
                    helper.ShowErrDialog("开始信号系统错误", ex.getMessage(), VisActivity.this);
                }
            }
        }
    };


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 3) {  //图片
            String pathname = data.getStringExtra("pathname");
            String typeid = data.getStringExtra("typeid");
            ///SaveVisPhoto(pathname, typeid);

            Bitmap bitmap = BitmapFactory.decodeFile(pathname);
            if (typeid.equals(zpzlWG) == true)
                img_vis_wgjyy.setImageBitmap(bitmap);
            if (typeid.equals(zpzlYCY) == true)
                img_vis_ycy.setImageBitmap(bitmap);
            if (typeid.equals(zpzlDTY) == true)
                img_vis_dtjyy.setImageBitmap(bitmap);
            if (typeid.equals(zpzlJBR) == true)
                img_vis_jbr.setImageBitmap(bitmap);

        } else if (resultCode == 4) {
            String value = data.getStringExtra("value");
            String typeid = data.getStringExtra("typeid");
            if (typeid.equals("1") == true)
                et_vis_dczxlhwsd.setText(value);
            if (typeid.equals("2") == true)
                et_vis_dcqtlhwsd.setText(value);
            if (typeid.equals("3") == true)
                et_vis_gchwsd.setText(value);

        } else if (resultCode == 44) {
            //外检拍照后并保存数据
            SaveData();
        }
    }


    private void QueryVisPhoto(String detectid, String zpzl) {

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
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("图片下载网络错误", msg.obj.toString(), VisActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {

                        GetVisPhotoReturnInfo infoA = JSON.parseObject(resultInfo.getData(), GetVisPhotoReturnInfo.class);
                        //helper.ShowErrDialog("提示", "图片:"+ infoA.getZpzl() +",上传成功", VisActivity.this);

                        byte[] decodedString = Base64.decode(infoA.getBase64(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        if (infoA.getZpzl().equals(zpzlWG) == true)
                            img_vis_wgjyy.setImageBitmap(decodedByte);
                        if (infoA.getZpzl().equals(zpzlYCY) == true)
                            img_vis_ycy.setImageBitmap(decodedByte);
                        if (infoA.getZpzl().equals(zpzlDTY) == true)
                            img_vis_dtjyy.setImageBitmap(decodedByte);
                        if (infoA.getZpzl().equals(zpzlJBR) == true)
                            img_vis_jbr.setImageBitmap(decodedByte);


                    } else {
                        // helper.ShowErrDialog("图片上传失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), VisActivity.this);
                    }
                } catch (Exception ex) {
                    helper.ShowErrDialog("图片下载系统错误", ex.getMessage(), VisActivity.this);
                }
            }
        }
    };


    private void InsStop() {
        pd1 = ProgressDialog.show(this, "提示", "结束中...");
        pd1.setCancelable(true);

        InsStopInfo info = new InsStopInfo();
        info.setDetectid(detectid);
        info.setDevice_group(device_group);
        info.setJylsh(jylsh);
        info.setJycs(jycs);
        info.setLine(combox_vis_line.getText());
        info.setKssj(kssj);
        String data = JSON.toJSONString(info);

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handlerinsstop;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "InsStop");
        maps.put("data", data);
        postRequestUtil.Run(maps);
    }

    Handler handlerinsstop = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd1.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("结束信号查询网络错误", msg.obj.toString(), VisActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        InsStopReturnInfo infoA = JSON.parseObject(resultInfo.getData(), InsStopReturnInfo.class);
                        ShowErrDialog("结束成功", infoA.getJssj() + ",是否退出?");
                        //helper.ShowErrDialog("提示", "结束成功:"+ infoA.getJssj()+".", VisActivity.this);
                    } else {
                        helper.ShowErrDialog("结束信号查询失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), VisActivity.this);
                    }
                } catch (Exception ex) {
                    helper.ShowErrDialog("结束信号系统错误", ex.getMessage(), VisActivity.this);
                }
            }
        }
    };

    private void QueryDataItem(String flag1) {
        pd = ProgressDialog.show(VisActivity.this, "提示", "项目加载中...");
        ItemsDBHelper db = ItemsDBHelper.getInstance(VisActivity.this, 1);
        db.openReadLink();
        ArrayList<ItemsInfo> list = db.query("flag='" + flag1 + "' and jcxlb='1'");
        pd.dismiss();
        db.closeLink();
        //------------------------------------------------------------------------------------
        ArrayList<GetItemListReturnInfo> data = new ArrayList<GetItemListReturnInfo>();
        for (int i = 0; i <= list.size() - 1; i++) {
            GetItemListReturnInfo info = new GetItemListReturnInfo();
            info.setId(list.get(i).id);
            info.setValue(list.get(i).value);
            info.setFlag(list.get(i).flag);
            info.setIdandname(list.get(i).idandname);
            info.setNetid(list.get(i).netid);
            info.setSort(list.get(i).sort);
            info.setRemark(list.get(i).remark);
            data.add(info);
        }
        ShowDialogList(data);
    }


    private void ShowDialogList(List<GetItemListReturnInfo> list) {
        View view = View.inflate(VisActivity.this, R.layout.item_dialog1_listview, null);
        ListView lv = (ListView) view.findViewById(R.id.lv_dialog1_listview);
        dialog1Adapter = new Dialog1Adapter(VisActivity.this, list);
        lv.setAdapter(dialog1Adapter);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VisActivity.this);
        final android.app.AlertDialog dialog = builder.setTitle("具体项目选择：")
                .setView(view).setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                    }

                }).show();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String temp = dialog1Adapter.list.get(position).getFlag();
                if (temp.equals("101") == true) {
                    tv_vis_ycy.setText(dialog1Adapter.list.get(position).getValue());
                }

                dialog.dismiss();
            }
        });
    }


}
