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
import android.text.InputType;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.TimePickerView;
import com.mh.ajappnew.R;
import com.mh.ajappnew.adapter.DialogAdapter;
import com.mh.ajappnew.bottompopfragmentmenu.BottomMenuFragment;
import com.mh.ajappnew.bottompopfragmentmenu.MenuItem;
import com.mh.ajappnew.bottompopfragmentmenu.MenuItemOnClickListener;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.comm.VehicleGoodsInfo;
import com.mh.ajappnew.db.DefaultDBHelper;
import com.mh.ajappnew.db.DefaultInfo;
import com.mh.ajappnew.db.ItemsDBHelper;
import com.mh.ajappnew.db.ItemsInfo;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.CheckRegVehicleInfo;
import com.mh.ajappnew.jkid.GetDefaultValueInfo;
import com.mh.ajappnew.jkid.GetDefaultValueReturnInfo;
import com.mh.ajappnew.jkid.GetItemList;
import com.mh.ajappnew.jkid.GetItemListReturnInfo;
import com.mh.ajappnew.jkid.GetVehicleInfo;
import com.mh.ajappnew.jkid.GetVehicleInfoReturnInfo;
import com.mh.ajappnew.jkid.GetVehicleJcdlInfo;
import com.mh.ajappnew.jkid.GetVehicleJcdlReturnInfo;
import com.mh.ajappnew.jkid.GetVisPhotoInfo;
import com.mh.ajappnew.jkid.GetVisPhotoReturnInfo;
import com.mh.ajappnew.jkid.QNetVehicleInfo;
import com.mh.ajappnew.jkid.QNetVehicleInfoReturnInfo;
import com.mh.ajappnew.jkid.RegVehicleInfo;
import com.mh.ajappnew.jkid.RegVehicleInfoReturnInfo;
import com.mh.ajappnew.tools.CustomDialog;
import com.mh.ajappnew.tools.DateUtil;
import com.mh.ajappnew.tools.helper;
import com.mh.ajappnew.view.ComboxItemView;
import com.mh.ajappnew.view.HPEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleActivity extends AppCompatActivity {

    private Config config;
    private String jylsh, caotype, zt;

    private String zpzlWG = "0901";
    private String zpzlJBR = "0904";

    private boolean needchange = true;  //修改时，或已查询出资料时就不需变化

    private ComboxItemView combox_vehicle_ywlx, combox_vehicle_cp, combox_vehicle_cllx,
            combox_vehicle_cyys, combox_vehicle_fxwz, combox_vehicle_djys,
            combox_vehicle_qzdz, combox_vehicle_xgfs,
            combox_vehicle_zdfs, combox_vehicle_ryzl,
            combox_vehicle_zcwz, combox_vehicle_qdxs, combox_vehicle_btgg,
            combox_vehicle_zs, combox_vehicle_zxzs;

    private CheckBox cb_vehicle_zcwz1, cb_vehicle_zcwz2, cb_vehicle_zcwz3, cb_vehicle_zcwz4, cb_vehicle_caninputheng;

    private BottomMenuFragment bottomMenuFragment;

    private CheckBox cb_vehicle_sfdzzc, cb_vehicle_sfkqxj;

    private ProgressDialog pd;
    private LinearLayout linelayout_vehicle;

    private Button but_vehicle_save, but_vehicle_savetemp, but_vehicle_query, but_vehicle_update, but_vehicle_exit;
    private EditText et_vehicle_hphmmac1, et_vehicle_xslc, et_vehicle_lxr, et_vehicle_lxdh,
            et_vehicle_lxdhmac, et_vehicle_queryhphmmac, et_vehicle_sqbh, et_vehicle_scwpjson,
            et_vehicle_scwp, et_vehicle_remarkjson, et_vehicle_remark, et_vehicle_djrqmac, et_vehicle_zws,
            et_vehicle_qprs, et_vehicle_hxnc, et_vehicle_hxnk, et_vehicle_hxng;
    private RadioGroup rg_vehicle_sex;
    private EditText et_vehicle_cwkc, et_vehicle_cwkk, et_vehicle_cwkg;

    private DialogAdapter dialogAdapter = null;
    private TextView tv_vehicle_space, tv_vehicle_jylsh, tv_vehicle_space2;

    private Button but_vehicle_cyys, but_vehicle_querysqbh, but_vehicle_scwp, but_vehicle_remark;

    private LinearLayout lay_vehicle_hxnc, lay_vehicle_jylsh;
    private HPEditText hp_vehicle_hphmmac = null;

    private LinearLayout lay_vehicle_sign;
    private ImageView img_vehicle_jbr, img_vehicle_wgjyy;
    private TextView tv_vehicle_cyy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        Intent i = getIntent();
        config = i.getParcelableExtra("Config");
        caotype = i.getStringExtra("caotype");
        jylsh = i.getStringExtra("jylsh");
        zt = i.getStringExtra("zt");


        //0.初使化项目
        tv_vehicle_jylsh = (TextView) findViewById(R.id.tv_vehicle_jylsh);
        lay_vehicle_hxnc = (LinearLayout) findViewById(R.id.lay_vehicle_hxnc);
        lay_vehicle_jylsh = (LinearLayout) findViewById(R.id.lay_vehicle_jylsh);

        cb_vehicle_zcwz1 = (CheckBox) findViewById(R.id.cb_vehicle_zcwz1);
        cb_vehicle_zcwz2 = (CheckBox) findViewById(R.id.cb_vehicle_zcwz2);
        cb_vehicle_zcwz3 = (CheckBox) findViewById(R.id.cb_vehicle_zcwz3);
        cb_vehicle_zcwz4 = (CheckBox) findViewById(R.id.cb_vehicle_zcwz4);

        et_vehicle_qprs = (EditText) findViewById(R.id.et_vehicle_qprs);
        et_vehicle_hxnc = (EditText) findViewById(R.id.et_vehicle_hxnc);
        et_vehicle_hxnk = (EditText) findViewById(R.id.et_vehicle_hxnk);
        et_vehicle_hxng = (EditText) findViewById(R.id.et_vehicle_hxng);

        rg_vehicle_sex = (RadioGroup) findViewById(R.id.rg_vehicle_sex);

        et_vehicle_queryhphmmac = (EditText) findViewById(R.id.et_vehicle_queryhphmmac);
        hp_vehicle_hphmmac = (HPEditText) findViewById(R.id.hp_vehicle_hphmmac);
        cb_vehicle_caninputheng = (CheckBox) findViewById(R.id.cb_vehicle_caninputheng);


        et_vehicle_xslc = (EditText) findViewById(R.id.et_vehicle_xslc);
        et_vehicle_lxr = (EditText) findViewById(R.id.et_vehicle_lxr);
        et_vehicle_lxdh = (EditText) findViewById(R.id.et_vehicle_lxdh);
        et_vehicle_lxdhmac = (EditText) findViewById(R.id.et_vehicle_lxdhmac);

        et_vehicle_cwkc = (EditText) findViewById(R.id.et_vehicle_cwkc);
        et_vehicle_cwkk = (EditText) findViewById(R.id.et_vehicle_cwkk);
        et_vehicle_cwkg = (EditText) findViewById(R.id.et_vehicle_cwkg);

        et_vehicle_sqbh = (EditText) findViewById(R.id.et_vehicle_sqbh);

        et_vehicle_scwp = (EditText) findViewById(R.id.et_vehicle_scwp);
        et_vehicle_scwpjson = (EditText) findViewById(R.id.et_vehicle_scwpjson);

        et_vehicle_remark = (EditText) findViewById(R.id.et_vehicle_remark);
        et_vehicle_remark.setEnabled(false);
        et_vehicle_remarkjson = (EditText) findViewById(R.id.et_vehicle_remarkjson);

        et_vehicle_zws = (EditText) findViewById(R.id.et_vehicle_zws);
        et_vehicle_djrqmac = (EditText) findViewById(R.id.et_vehicle_djrqmac);


        linelayout_vehicle = (LinearLayout) findViewById(R.id.linelayout_vehicle);

        //3.checkbox
        cb_vehicle_sfdzzc = (CheckBox) findViewById(R.id.cb_vehicle_sfdzzc);
        cb_vehicle_sfkqxj = (CheckBox) findViewById(R.id.cb_vehicle_sfkqxj);

        cb_vehicle_caninputheng.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hp_vehicle_hphmmac.SetStlye(isChecked);
            }
        });

        //3.按钮
        but_vehicle_query = (Button) findViewById(R.id.but_vehicle_query);
        but_vehicle_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_vehicle_queryhphmmac.getText().toString().length() < 4) {
                    Toast.makeText(VehicleActivity.this, "提示,号牌号码小于4位", Toast.LENGTH_LONG).show();
                    return;
                }

                QueryDataByHphmJcdl(et_vehicle_queryhphmmac.getText().toString(), "");
            }
        });
        but_vehicle_save = (Button) findViewById(R.id.but_vehicle_save);
        but_vehicle_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_vehicle_cwkc.requestFocus();

                zt = "1";
                CheckRegVehicle();
            }
        });
        but_vehicle_savetemp = (Button) findViewById(R.id.but_vehicle_savetemp);
        but_vehicle_savetemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_vehicle_cwkc.requestFocus();

                zt = "0";
                CheckRegVehicle();
            }
        });

        but_vehicle_update = (Button) findViewById(R.id.but_vehicle_update);
        but_vehicle_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckRegVehicle();
            }
        });
        tv_vehicle_space = (TextView) findViewById(R.id.tv_vehicle_space);
        tv_vehicle_space2 = (TextView) findViewById(R.id.tv_vehicle_space2);

        //2021-04-15加
        combox_vehicle_ywlx = (ComboxItemView) findViewById(R.id.combox_vehicle_ywlx);
        combox_vehicle_ywlx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitItemsCombox("1");
            }
        });

        combox_vehicle_cp = (ComboxItemView) findViewById(R.id.combox_vehicle_cp);
        combox_vehicle_cp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //InitItemsCombox("12");
                OpenSearchActivity("12");
            }
        });

        combox_vehicle_cllx = (ComboxItemView) findViewById(R.id.combox_vehicle_cllx);
        combox_vehicle_cllx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //InitItemsCombox("2");
                OpenSearchActivity("2");
            }
        });

        combox_vehicle_cyys = (ComboxItemView) findViewById(R.id.combox_vehicle_cyys);
        combox_vehicle_cyys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitItemsCombox("3");
            }
        });

        combox_vehicle_djys = (ComboxItemView) findViewById(R.id.combox_vehicle_djys);
        combox_vehicle_djys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitItemsCombox("33");
            }
        });


        combox_vehicle_fxwz = (ComboxItemView) findViewById(R.id.combox_vehicle_fxwz);
        combox_vehicle_fxwz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitItemsCombox("4");
            }
        });

        combox_vehicle_qzdz = (ComboxItemView) findViewById(R.id.combox_vehicle_qzdz);
        combox_vehicle_qzdz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitItemsCombox("5");
            }
        });

        combox_vehicle_xgfs = (ComboxItemView) findViewById(R.id.combox_vehicle_xgfs);
        combox_vehicle_xgfs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitItemsCombox("6");
            }
        });

        combox_vehicle_zdfs = (ComboxItemView) findViewById(R.id.combox_vehicle_zdfs);
        combox_vehicle_zdfs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitItemsCombox("7");
            }
        });

        combox_vehicle_ryzl = (ComboxItemView) findViewById(R.id.combox_vehicle_ryzl);
        combox_vehicle_ryzl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitItemsCombox("8");
            }
        });

        combox_vehicle_zcwz = (ComboxItemView) findViewById(R.id.combox_vehicle_zcwz);
        combox_vehicle_zcwz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitItemsCombox("9");
            }
        });

        combox_vehicle_qdxs = (ComboxItemView) findViewById(R.id.combox_vehicle_qdxs);
        combox_vehicle_qdxs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitItemsCombox("10");
            }
        });

        combox_vehicle_btgg = (ComboxItemView) findViewById(R.id.combox_vehicle_btgg);
        combox_vehicle_btgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //InitItemsCombox("11");
                OpenSearchActivity("11");
            }
        });

        combox_vehicle_zxzs = (ComboxItemView) findViewById(R.id.combox_vehicle_zxzs);
        combox_vehicle_zxzs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitItemsCombox("13");
            }
        });

        combox_vehicle_zs = (ComboxItemView) findViewById(R.id.combox_vehicle_zs);
        combox_vehicle_zs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitItemsCombox("14");
            }
        });

        //2021-08-03签名用
        lay_vehicle_sign = (LinearLayout) findViewById(R.id.lay_vehicle_sign);
        tv_vehicle_cyy = (TextView) findViewById(R.id.tv_vehicle_cyy);


        LoadData();
        LoadSign();


        but_vehicle_cyys = (Button) findViewById(R.id.but_vehicle_cyys);
        but_vehicle_cyys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                combox_vehicle_cyys.setText("");
            }
        });
        but_vehicle_cyys = (Button) findViewById(R.id.but_vehicle_djys);
        but_vehicle_cyys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                combox_vehicle_djys.setText("");
            }
        });

        but_vehicle_querysqbh = (Button) findViewById(R.id.but_vehicle_querysqbh);
        but_vehicle_querysqbh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QNetVehicleInfo(hp_vehicle_hphmmac.getText().toString().toUpperCase(), combox_vehicle_ywlx.getText());
            }
        });

        but_vehicle_scwp = (Button) findViewById(R.id.but_vehicle_scwp);
        but_vehicle_scwp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VehicleActivity.this, VehicleGoodsActivity.class);
                //加入参数，传递给AnotherActivity
                intent.putExtra("Config", config);
                intent.putExtra("value", et_vehicle_scwp.getText().toString());
                intent.putExtra("json", et_vehicle_scwpjson.getText().toString());
                intent.putExtra("flag", "15");
                intent.putExtra("ywlx", combox_vehicle_ywlx.getText());
                startActivityForResult(intent, 15);
            }
        });

        but_vehicle_remark = (Button) findViewById(R.id.but_vehicle_remark);
        but_vehicle_remark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VehicleActivity.this, VehicleGoodsActivity.class);
                //加入参数，传递给AnotherActivity
                intent.putExtra("Config", config);
                intent.putExtra("value", et_vehicle_remark.getText().toString());
                intent.putExtra("json", et_vehicle_remarkjson.getText().toString());
                intent.putExtra("flag", "17");
                intent.putExtra("ywlx", combox_vehicle_ywlx.getText());
                startActivityForResult(intent, 17);
            }
        });

        timePicke(et_vehicle_djrqmac);

        but_vehicle_exit = (Button) findViewById(R.id.but_vehicle_exit);
        but_vehicle_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void InitItemsCombox(String flag1) {
        pd = ProgressDialog.show(VehicleActivity.this, "提示", "项目加载中...");
        ItemsDBHelper db = ItemsDBHelper.getInstance(VehicleActivity.this, 1);
        db.openReadLink();
        ArrayList<ItemsInfo> list = db.query("flag='" + flag1 + "'");
        pd.dismiss();
        db.closeLink();
        //------------------------------------------------------------------------------------

        bottomMenuFragment = new BottomMenuFragment();
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();

        final String flag = flag1;
        for (int i = 0; i <= list.size() - 1; i++) {
            MenuItem menuItem1 = new MenuItem();
            menuItem1.setText(list.get(i).value);
            menuItem1.setItem_name(list.get(i).id);
            menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);

            menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
                @Override
                public void onClickMenuItem(View v, MenuItem menuItem) {
                    //Toast.makeText(VisActivity.this, menuItem.getText() + " " + menuItem.getItem_name(), Toast.LENGTH_LONG).show();
                    if (flag.equals("1")) {
                        combox_vehicle_ywlx.setText(menuItem.getText());
                        InitRemarkByYwlx(menuItem.getText());
                    } else if (flag.equals("12")) {
                        combox_vehicle_cp.setText(menuItem.getText());
                    } else if (flag.equals("2")) {
                        combox_vehicle_cllx.setText(menuItem.getText());
                    } else if (flag.equals("3")) {
                        String temp1 = combox_vehicle_cyys.getText() + "/" + menuItem.getText();
                        if (combox_vehicle_cyys.getText().equals("")) {
                            temp1 = menuItem.getText();
                        }
                        combox_vehicle_cyys.setText(temp1);
                    }
                    if (flag.equals("33")) {
                        String temp2 = combox_vehicle_djys.getText() + "/" + menuItem.getText();
                        if (combox_vehicle_djys.getText().equals("")) {
                            temp2 = menuItem.getText();
                        }
                        combox_vehicle_djys.setText(temp2);
                    } else if (flag.equals("4")) {
                        combox_vehicle_fxwz.setText(menuItem.getText());
                    } else if (flag.equals("5")) {
                        combox_vehicle_qzdz.setText(menuItem.getText());
                    } else if (flag.equals("6")) {
                        combox_vehicle_xgfs.setText(menuItem.getText());
                    } else if (flag.equals("7")) {
                        combox_vehicle_zdfs.setText(menuItem.getText());
                    } else if (flag.equals("8")) {
                        combox_vehicle_ryzl.setText(menuItem.getText());
                    } else if (flag.equals("9")) {
                        combox_vehicle_zcwz.setText(menuItem.getText());
                    } else if (flag.equals("10")) {
                        combox_vehicle_qdxs.setText(menuItem.getText());
                        //联网查询默认值
                        GetDefaultValue("10", menuItem.getText());
                    } else if (flag.equals("11")) {
                        combox_vehicle_btgg.setText(menuItem.getText());
                    } else if (flag.equals("13")) {
                        combox_vehicle_zxzs.setText(menuItem.getText());
                    } else if (flag.equals("14")) {
                        combox_vehicle_zs.setText(menuItem.getText());
                        ChangeZcwz(combox_vehicle_zs.getText());
                    }
                }
            });

            menuItemList.add(menuItem1);
        }

        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");

    }

    private void InitBottomUenu(List<GetItemListReturnInfo> list) {
        bottomMenuFragment = new BottomMenuFragment();

        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        for (int i = 0; i <= list.size() - 1; i++) {
            MenuItem menuItem1 = new MenuItem();
            menuItem1.setText(list.get(i).getValue());
            menuItem1.setItem_name(list.get(i).getId());
            menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);

            final String flag = list.get(i).getFlag();

            menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
                @Override
                public void onClickMenuItem(View v, MenuItem menuItem) {
                    //Toast.makeText(VisActivity.this, menuItem.getText() + " " + menuItem.getItem_name(), Toast.LENGTH_LONG).show();
                    if (flag.equals("1")) {
                        combox_vehicle_ywlx.setText(menuItem.getText());
                    } else if (flag.equals("12")) {
                        combox_vehicle_cp.setText(menuItem.getText());
                    } else if (flag.equals("2")) {
                        combox_vehicle_cllx.setText(menuItem.getText());
                    } else if (flag.equals("3")) {
                        String temp1 = combox_vehicle_cyys.getText() + "/" + menuItem.getText();
                        if (combox_vehicle_cyys.getText().equals("")) {
                            temp1 = menuItem.getText();
                        }
                        combox_vehicle_cyys.setText(temp1);
                    }
                    if (flag.equals("33")) {
                        String temp2 = combox_vehicle_djys.getText() + "/" + menuItem.getText();
                        if (combox_vehicle_djys.getText().equals("")) {
                            temp2 = menuItem.getText();
                        }
                        combox_vehicle_djys.setText(temp2);
                    } else if (flag.equals("4")) {
                        combox_vehicle_fxwz.setText(menuItem.getText());
                    } else if (flag.equals("5")) {
                        combox_vehicle_qzdz.setText(menuItem.getText());
                    } else if (flag.equals("6")) {
                        combox_vehicle_xgfs.setText(menuItem.getText());
                    } else if (flag.equals("7")) {
                        combox_vehicle_zdfs.setText(menuItem.getText());
                    } else if (flag.equals("8")) {
                        combox_vehicle_ryzl.setText(menuItem.getText());
                    } else if (flag.equals("9")) {
                        combox_vehicle_zcwz.setText(menuItem.getText());
                    } else if (flag.equals("10")) {
                        combox_vehicle_qdxs.setText(menuItem.getText());
                        //联网查询默认值
                        GetDefaultValue("10", menuItem.getText());
                    } else if (flag.equals("11")) {
                        combox_vehicle_btgg.setText(menuItem.getText());
                    } else if (flag.equals("13")) {
                        combox_vehicle_zxzs.setText(menuItem.getText());
                    } else if (flag.equals("14")) {
                        combox_vehicle_zs.setText(menuItem.getText());
                        ChangeZcwz(combox_vehicle_zs.getText());
                    }
                }
            });

            menuItemList.add(menuItem1);
        }

        bottomMenuFragment.setMenuItems(menuItemList);

    }


    private void InitItemsCombox11111(String flag) {
        but_vehicle_exit.setFocusable(true);
        but_vehicle_exit.setFocusableInTouchMode(true);
        but_vehicle_exit.requestFocus();

        GetItemList GetItemList = new GetItemList();
        GetItemList.setFlag(flag);
        String data = JSON.toJSONString(GetItemList);

        pd = ProgressDialog.show(VehicleActivity.this, "提示", "项目加载中...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = comboxhandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetItemList");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler comboxhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("加载项目网络错误", msg.obj.toString(), VehicleActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        List<GetItemListReturnInfo> list = JSON.parseArray(resultInfo.getData(), GetItemListReturnInfo.class);
                        InitBottomUenu(list);
                        bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
                    } else {
                        helper.ShowErrDialog("加载项目失败", resultInfo.getMessage(), VehicleActivity.this);
                    }

                } catch (Exception ex) {
                    Toast.makeText(VehicleActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("加载项目系统错误", ex.getMessage(), VehicleActivity.this);
                }
            }

        }
    };


    private void LoadData() {
        if (caotype.equals("modify")) {
            needchange = false;
            hp_vehicle_hphmmac.SetStlye(false);
        } else {
            needchange = true;
        }

        if (caotype.equals("modify")) {
            lay_vehicle_sign.setVisibility(View.VISIBLE);
            linelayout_vehicle.setVisibility(View.GONE);
            QueryDataByHphm("", jylsh, "0");
            if (zt.equals("1") == true) {
                but_vehicle_savetemp.setVisibility(View.GONE);
                but_vehicle_save.setVisibility(View.GONE);
                but_vehicle_update.setVisibility(View.VISIBLE);
                tv_vehicle_space.setVisibility(View.GONE);

            }

            if (zt.equals("1") != true && zt.equals("0") != true) {
                but_vehicle_savetemp.setVisibility(View.GONE);
                but_vehicle_save.setVisibility(View.GONE);
                but_vehicle_update.setVisibility(View.GONE);
                tv_vehicle_space.setVisibility(View.GONE);
                tv_vehicle_space2.setVisibility(View.GONE);
            }
            hp_vehicle_hphmmac.setEnabled(false);
            lay_vehicle_jylsh.setVisibility(View.VISIBLE);

            QueryVisPhoto(jylsh + "1", zpzlWG);
            QueryVisPhoto(jylsh + "1", zpzlJBR);

        } else {
            lay_vehicle_jylsh.setVisibility(View.GONE);
            lay_vehicle_sign.setVisibility(View.GONE);
        }
    }

    private void QueryDataByHphm(String hphmmac, String detectid, String flag) {
        GetVehicleInfo info = new GetVehicleInfo();
        info.setHphmmac(hphmmac);
        info.setJylsh(detectid);
        info.setFlag(flag);
        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(VehicleActivity.this, "提示", "数据查询中...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = queryhandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetVehicleInfo");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler queryhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("查询网络错误", msg.obj.toString(), VehicleActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        GetVehicleInfoReturnInfo info = JSON.parseObject(resultInfo.getData(), GetVehicleInfoReturnInfo.class);
                        EvalText(info);
                    } else {
                        helper.ShowErrDialog("查询失败", resultInfo.getMessage(), VehicleActivity.this);
                    }

                } catch (Exception ex) {
                    Toast.makeText(VehicleActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("查询系统错误", ex.getMessage(), VehicleActivity.this);
                }
            }
        }
    };

    private void EvalText(GetVehicleInfoReturnInfo info) {
        hp_vehicle_hphmmac.setText(info.getHphmmac());
        et_vehicle_xslc.setText(info.getXslc());
        et_vehicle_lxdh.setText(info.getLxdh());
        et_vehicle_lxdhmac.setText(info.getLxdhmac());
        et_vehicle_lxr.setText(info.getLxr());

        combox_vehicle_ywlx.setText(info.getYwlx());
        combox_vehicle_cp.setText(info.getCp());
        combox_vehicle_cllx.setText(info.getCllx());

        combox_vehicle_djys.setText(info.getDjys());
        combox_vehicle_cyys.setText(info.getCyys());
        combox_vehicle_fxwz.setText(info.getFxwz());

        combox_vehicle_qzdz.setText(info.getQzdz());
        combox_vehicle_xgfs.setText(info.getXgfs());
        combox_vehicle_zdfs.setText(info.getZdfs());

        combox_vehicle_ryzl.setText(info.getRyzl());

        combox_vehicle_qdxs.setText(info.getQdxs());

        combox_vehicle_btgg.setText(info.getBtgg());
        combox_vehicle_zxzs.setText(info.getZxzs());
        combox_vehicle_zs.setText(info.getZs());

        cb_vehicle_sfdzzc.setChecked(info.getSfdzzc().equals("1"));
        cb_vehicle_sfkqxj.setChecked(info.getSfkqxj().equals("1"));

        et_vehicle_cwkc.setText(info.getCwkc());
        et_vehicle_cwkk.setText(info.getCwkk());
        et_vehicle_cwkg.setText(info.getCwkg());

        et_vehicle_qprs.setText(info.getQprs().equals("") ? "2" : info.getQprs());
        et_vehicle_hxnc.setText(info.getHxnc());
        et_vehicle_hxnk.setText(info.getHxnk());
        et_vehicle_hxng.setText(info.getHxng());


        for (int i = 0; i < rg_vehicle_sex.getChildCount(); i++) {
            RadioButton rb = (RadioButton) rg_vehicle_sex.getChildAt(i);
            if (info.getSex().equals(rb.getTag().toString())) {
                rb.setChecked(true);
            }
        }

        et_vehicle_sqbh.setText(info.getSqbh());

        et_vehicle_scwpjson.setText(info.getScwpjson());
        et_vehicle_scwp.setText(info.getScwp());

        et_vehicle_remarkjson.setText(info.getRemarkjson());
        et_vehicle_remark.setText(info.getRemark());

        cb_vehicle_zcwz1.setChecked(info.getZcwz().contains("1"));
        cb_vehicle_zcwz2.setChecked(info.getZcwz().contains("2"));
        cb_vehicle_zcwz3.setChecked(info.getZcwz().contains("3"));
        cb_vehicle_zcwz4.setChecked(info.getZcwz().contains("4"));
        combox_vehicle_zcwz.setText(info.getZcwz());

        et_vehicle_djrqmac.setText(info.getDjrqmac());
        et_vehicle_zws.setText(info.getZws());

        tv_vehicle_jylsh.setText(jylsh);

        if (info.getCllx().contains("厢式") == true) {
            lay_vehicle_hxnc.setVisibility(View.VISIBLE);
        } else {
            lay_vehicle_hxnc.setVisibility(View.GONE);
        }

        if (info.getModifypeople().equals("") == false) {
            tv_vehicle_cyy.setText(info.getModifypeople());
        } else {
            tv_vehicle_cyy.setText(info.getAddpeople());
        }

        ChangeZcwz(combox_vehicle_zs.getText());

    }


    private void SaveData() {
        if (zt.equals("1") == true) {
            if (CheckData1() == false) {
                return;
            }
        } else if (zt.equals("0") == true) {
            if (CheckData0() == false) {
                return;
            }
        }

        RegVehicleInfo info = new RegVehicleInfo();
        info.setJylsh(jylsh);
        info.setHphmmac(hp_vehicle_hphmmac.getText().toString().toUpperCase());
        info.setCllx(combox_vehicle_cllx.getText());
        info.setYwlx(combox_vehicle_ywlx.getText());
        info.setCyys(combox_vehicle_cyys.getText());
        info.setDjys(combox_vehicle_djys.getText());
        info.setFxwz(combox_vehicle_fxwz.getText());

        info.setQzdz(combox_vehicle_qzdz.getText());
        info.setXgfs(combox_vehicle_xgfs.getText());
        info.setZdfs(combox_vehicle_zdfs.getText());
        info.setRyzl(combox_vehicle_ryzl.getText());

        String zcwz = "";
        if (cb_vehicle_zcwz1.isChecked() == true) {
            zcwz += "1";
        }
        if (cb_vehicle_zcwz2.isChecked() == true) {
            zcwz += "2";
        }
        if (cb_vehicle_zcwz3.isChecked() == true) {
            zcwz += "3";
        }
        if (cb_vehicle_zcwz4.isChecked() == true) {
            zcwz += "4";
        }

        info.setZcwz(zcwz);
        info.setQdxs(combox_vehicle_qdxs.getText());
        info.setBtgg(combox_vehicle_btgg.getText());

        info.setCp(combox_vehicle_cp.getText());

        info.setXslc(et_vehicle_xslc.getText().toString());
        info.setLxr(et_vehicle_lxr.getText().toString());
        info.setLxdh(et_vehicle_lxdh.getText().toString());
        info.setLxdhmac(et_vehicle_lxdhmac.getText().toString());
        info.setZt(zt);

        info.setZs(combox_vehicle_zs.getText());
        info.setZxzs(combox_vehicle_zxzs.getText());

        String sex = "0";
        for (int i = 0; i < rg_vehicle_sex.getChildCount(); i++) {
            RadioButton rb = (RadioButton) rg_vehicle_sex.getChildAt(i);
            if (rb.isChecked()) {
                sex = rb.getTag().toString();
                break;
            }
        }
        info.setSex(sex);

        info.setCwkc(et_vehicle_cwkc.getText().toString());
        info.setCwkk(et_vehicle_cwkk.getText().toString());
        info.setCwkg(et_vehicle_cwkg.getText().toString());

        info.setSqbh(et_vehicle_sqbh.getText().toString());

        info.setSfdzzc(cb_vehicle_sfdzzc.isChecked() == true ? "1" : "0");
        info.setSfkqxj(cb_vehicle_sfkqxj.isChecked() == true ? "1" : "0");

        info.setScwp(et_vehicle_scwp.getText().toString());
        info.setScwpjson(et_vehicle_scwpjson.getText().toString());

        info.setQprs(et_vehicle_qprs.getText().toString());
        info.setHxnc(et_vehicle_hxnc.getText().toString());
        info.setHxnk(et_vehicle_hxnk.getText().toString());
        info.setHxng(et_vehicle_hxng.getText().toString());

        info.setRemark(et_vehicle_remark.getText().toString());
        info.setRemarkjson(et_vehicle_remarkjson.getText().toString());

        info.setZws(et_vehicle_zws.getText().toString());
        info.setDjrqmac(et_vehicle_djrqmac.getText().toString());
        info.setCzy(config.getJyy());

        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(VehicleActivity.this, "提示", "数据保存中...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = savehandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "RegVehicleInfo");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    private Boolean CheckData0() {
        if (combox_vehicle_ywlx.getText().equals("")) {
            helper.ShowErrDialog("提示", "[业务类型]为空.", VehicleActivity.this);
            return false;
        }

        if (hp_vehicle_hphmmac.getText().equals("")) {
            helper.ShowErrDialog("提示", "[号牌号码]为空.", VehicleActivity.this);
            return false;
        }
        return true;
    }

    private Boolean CheckData1() {

        if (combox_vehicle_ywlx.getText().equals("")) {
            combox_vehicle_ywlx.setFocusAA();
            helper.ShowErrDialog("提示", "[业务类型]为空.", VehicleActivity.this);
            return false;
        }

        if (hp_vehicle_hphmmac.getText().equals("")) {
            hp_vehicle_hphmmac.requestFocus();
            helper.ShowErrDialog("提示", "[号牌号码]为空.", VehicleActivity.this);
            return false;
        }

        if (combox_vehicle_cp.getText().equals("")) {
            combox_vehicle_cp.setFocusAA();
            helper.ShowErrDialog("提示", "[车辆品牌]为空.", VehicleActivity.this);
            return false;
        }

        if (combox_vehicle_cllx.getText().equals("")) {
            combox_vehicle_cllx.setFocusAA();
            helper.ShowErrDialog("提示", "[车辆类型]为空.", VehicleActivity.this);
            return false;
        }

        if (combox_vehicle_djys.getText().equals("")) {
            combox_vehicle_djys.setFocusAA();
            helper.ShowErrDialog("提示", "[登记颜色]为空.", VehicleActivity.this);
            return false;
        }

        if (combox_vehicle_cyys.getText().equals("")) {
            combox_vehicle_cyys.setFocusAA();
            helper.ShowErrDialog("提示", "[查验颜色]为空.", VehicleActivity.this);
            return false;
        }

        if (combox_vehicle_fxwz.getText().equals("")) {
            combox_vehicle_fxwz.setFocusAA();
            helper.ShowErrDialog("提示", "[方向位置]为空.", VehicleActivity.this);
            return false;
        }

        if (combox_vehicle_qzdz.getText().equals("")) {
            combox_vehicle_qzdz.setFocusAA();
            helper.ShowErrDialog("提示", "[前照灯制]为空.", VehicleActivity.this);
            return false;
        }

        if (combox_vehicle_xgfs.getText().equals("")) {
            combox_vehicle_xgfs.setFocusAA();
            helper.ShowErrDialog("提示", "[悬挂方式]为空.", VehicleActivity.this);
            return false;
        }

        if (combox_vehicle_zdfs.getText().equals("")) {
            combox_vehicle_zdfs.setFocusAA();
            helper.ShowErrDialog("提示", "[制动方式]为空.", VehicleActivity.this);
            return false;
        }

        if (combox_vehicle_ryzl.getText().equals("")) {
            combox_vehicle_ryzl.setFocusAA();
            helper.ShowErrDialog("提示", "[燃料种类]为空.", VehicleActivity.this);
            return false;
        }

        if (cb_vehicle_zcwz1.isChecked() == false && cb_vehicle_zcwz2.isChecked() == false &&
                cb_vehicle_zcwz3.isChecked() == false && cb_vehicle_zcwz4.isChecked() == false) {

            cb_vehicle_zcwz1.requestFocus();

            helper.ShowErrDialog("提示", "[驻车位置]未选择.", VehicleActivity.this);
            return false;
        }

        if (combox_vehicle_qdxs.getText().equals("")) {
            combox_vehicle_qdxs.setFocusAA();
            helper.ShowErrDialog("提示", "[驱动型式]为空.", VehicleActivity.this);
            return false;
        }

        if (combox_vehicle_zs.getText().equals("")) {
            combox_vehicle_zs.setFocusAA();
            helper.ShowErrDialog("提示", "[轴数]为空.", VehicleActivity.this);
            return false;
        }

        if (combox_vehicle_zxzs.getText().equals("")) {
            combox_vehicle_zxzs.setFocusAA();
            helper.ShowErrDialog("提示", "[转向轴数]为空.", VehicleActivity.this);
            return false;
        }

        if (et_vehicle_qprs.getText().equals("")) {
            et_vehicle_qprs.requestFocus();
            helper.ShowErrDialog("提示", "[前排人数]为空.", VehicleActivity.this);
            return false;
        }

        if (et_vehicle_zws.getText().equals("")) {
            et_vehicle_zws.requestFocus();
            helper.ShowErrDialog("提示", "[座位数]为空.", VehicleActivity.this);
            return false;
        }

        if (et_vehicle_djrqmac.getText().equals("")) {
            et_vehicle_djrqmac.requestFocus();
            helper.ShowErrDialog("提示", "[登记日期]为空.", VehicleActivity.this);
            return false;
        }

        if (img_vehicle_wgjyy.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.pen1).getConstantState())) {
            if (caotype.equals("add") == true) {
                helper.ShowErrDialog("提示", "查验员未签名,请先暂存.", VehicleActivity.this);
            } else {
                helper.ShowErrDialog("提示", "查验员未签名", VehicleActivity.this);
            }
            return false;
        }

        if (img_vehicle_jbr.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.pen1).getConstantState())) {
            helper.ShowErrDialog("提示", "经办人未签名", VehicleActivity.this);
            return false;
        }

        return true;
    }

    Handler savehandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("加载项目网络错误", msg.obj.toString(), VehicleActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        ReturnFX(resultInfo);
                    } else {
                        helper.ShowErrDialog("保存失败", resultInfo.getMessage(), VehicleActivity.this);
                    }

                } catch (Exception ex) {
                    Toast.makeText(VehicleActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("保存系统错误", ex.getMessage(), VehicleActivity.this);
                }
            }
        }
    };

    private void ReturnFX(ResultInfo resultInfo) {
        helper.ShowErrDialog("保存成功", resultInfo.getMessage(), VehicleActivity.this);
        RegVehicleInfoReturnInfo RegVehicleInfoReturnInfo = JSON.parseObject(resultInfo.getData(), RegVehicleInfoReturnInfo.class);
        if (caotype.equals("modify") == true) {
            finish();
        } else if (caotype.equals("add") == true && zt.equals("0")) {
            //新增且暂存时需新加载，并显示流水
            caotype = "modify";
            zt = "0";
            jylsh = RegVehicleInfoReturnInfo.getJylsh();
            LoadData();
        }
    }


    private void QueryDataByHphmJcdl(String hphm, String detectid) {
        GetVehicleJcdlInfo info = new GetVehicleJcdlInfo();
        info.setHphm(hphm);
        info.setDetectid("");
        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(VehicleActivity.this, "提示", "数据JCDL查询中...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = queryjcdlhandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetVehicleJcdl");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler queryjcdlhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("查询网络错误", msg.obj.toString(), VehicleActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        needchange = false;
                        List<GetVehicleJcdlReturnInfo> list = JSON.parseArray(resultInfo.getData(), GetVehicleJcdlReturnInfo.class);
                        ShowDialogList(list);
                    } else {
                        needchange = true;
                        helper.ShowErrDialog("查询失败", resultInfo.getMessage(), VehicleActivity.this);
                    }

                } catch (Exception ex) {
                    Toast.makeText(VehicleActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("查询系统错误", ex.getMessage(), VehicleActivity.this);
                }
            }
        }
    };


    private void ShowDialogList(List<GetVehicleJcdlReturnInfo> list) {
        View view = View.inflate(VehicleActivity.this, R.layout.item_dialog_listview, null);
        ListView lv = (ListView) view.findViewById(R.id.lv_dialog_listview);
        dialogAdapter = new DialogAdapter(VehicleActivity.this, list);
        lv.setAdapter(dialogAdapter);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(VehicleActivity.this);
        final android.app.AlertDialog dialog = builder.setTitle("具体车辆选择：")
                .setView(view).setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub
                    }

                }).show();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String vciid = dialogAdapter.list.get(position).getHphm();
                et_vehicle_queryhphmmac.setText(vciid);
                String temp = dialogAdapter.list.get(position).getDetectid();
                Toast.makeText(VehicleActivity.this, temp, Toast.LENGTH_LONG).show();
                dialog.dismiss();

                QueryDataByHphm("", temp, "1");

            }
        });
    }


    private void QNetVehicleInfo(String hphmmac, String ywlx) {
        QNetVehicleInfo info = new QNetVehicleInfo();
        info.setHphm(hphmmac);
        info.setYwlx(ywlx);
        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(VehicleActivity.this, "提示", "数据查询中...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = querysqbhhandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "QNetVehicleInfo");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler querysqbhhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("查询网络错误", msg.obj.toString(), VehicleActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        QNetVehicleInfoReturnInfo info = JSON.parseObject(resultInfo.getData(), QNetVehicleInfoReturnInfo.class);
                        et_vehicle_sqbh.setText(info.getSqbh());
                        helper.ShowErrDialog("查询成功", info.getMessage(), VehicleActivity.this);

                    } else {
                        helper.ShowErrDialog("查询失败", resultInfo.getMessage(), VehicleActivity.this);
                    }

                } catch (Exception ex) {
                    Toast.makeText(VehicleActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("查询系统错误", ex.getMessage(), VehicleActivity.this);
                }
            }
        }
    };


    private void ShowSaveDialog(String title, String desc) {
        CustomDialog customDialog = new CustomDialog(VehicleActivity.this);
        customDialog.setTitle(title);
        customDialog.setMessage(desc);
        customDialog.setCancel("返回", new CustomDialog.IOnCancelListener() {
            @Override
            public void onCancel(CustomDialog dialog) {

            }
        });
        customDialog.setConfirm("确定", new CustomDialog.IOnConfirmListener() {
            @Override
            public void onConfirm(CustomDialog dialog) {
                SaveData();
            }
        });
        customDialog.show();
        customDialog.setCancelVisable(true);
    }

    private void OpenSearchActivity(String flag) {
        Intent intent = new Intent(VehicleActivity.this, SearchActivity.class);
        //加入参数，传递给AnotherActivity
        intent.putExtra("Config", config);
        intent.putExtra("flag", flag);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int pos = requestCode;

        //Toast.makeText(VehicleActivity.this,String.valueOf(resultCode),Toast.LENGTH_LONG).show();

        if (resultCode == 1) {
            String flag = data.getStringExtra("flag");
            String value = data.getStringExtra("value");

            if (flag.equals("2") == true) {
                combox_vehicle_cllx.setText(value);
                //联网查询默认值
                GetDefaultValue("2", value);
            } else if (flag.equals("11") == true) {
                combox_vehicle_btgg.setText(value);
            } else if (flag.equals("12") == true) {
                combox_vehicle_cp.setText(value);
            }

            but_vehicle_exit.setFocusable(true);
            but_vehicle_exit.setFocusableInTouchMode(true);
            but_vehicle_exit.requestFocus();

        } else if (resultCode == 15) {
            String scwp = data.getStringExtra("value");
            String scwpjson = data.getStringExtra("json");

            et_vehicle_scwpjson.setText(scwpjson);
            et_vehicle_scwp.setText(scwp);

        } else if (resultCode == 17) {
            String value = data.getStringExtra("value");
            String json = data.getStringExtra("json");

            et_vehicle_remarkjson.setText(json);
            et_vehicle_remark.setText(value);

        } else if (resultCode == 3) {  //图片
            String pathname = data.getStringExtra("pathname");
            String typeid = data.getStringExtra("typeid");
            ///SaveVisPhoto(pathname, typeid);

            Bitmap bitmap = BitmapFactory.decodeFile(pathname);
            if (typeid.equals(zpzlWG) == true)
                img_vehicle_wgjyy.setImageBitmap(bitmap);
            if (typeid.equals(zpzlJBR) == true)
                img_vehicle_jbr.setImageBitmap(bitmap);

        }

    }

    private void CheckRegVehicle() {

//                et_vehicle_qprs.setFocusable(true);
//        et_vehicle_qprs.setFocusableInTouchMode(true);
//        et_vehicle_qprs.requestFocus();

        if (hp_vehicle_hphmmac.getText().toString().equals("")) {
            hp_vehicle_hphmmac.requestFocus();
            helper.ShowErrDialog("提示", "澳门车牌不能为空!", VehicleActivity.this);
            return;
        }

        if (combox_vehicle_ywlx.getText().toString().equals("")) {
            combox_vehicle_ywlx.setFocusAA();
            helper.ShowErrDialog("提示", "业务类型不能为空!", VehicleActivity.this);
            return;
        }

        CheckRegVehicleInfo info = new CheckRegVehicleInfo();
        info.setHphmmac(hp_vehicle_hphmmac.getText().toString());
        info.setCllx(combox_vehicle_cllx.getText());
        info.setYwlx(combox_vehicle_ywlx.getText());
        info.setSqbh(et_vehicle_sqbh.getText().toString());
        info.setZt(zt);
        info.setFlag(caotype);
        info.setJylsh(jylsh);
        info.setJyy(config.getJyy());
        info.setCwkc(et_vehicle_cwkc.getText().toString());
        info.setCwkk(et_vehicle_cwkk.getText().toString());
        info.setCwkg(et_vehicle_cwkg.getText().toString());

        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(VehicleActivity.this, "提示", "数据判断中...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = checkregvehiclehandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "CheckRegVehicle");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler checkregvehiclehandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("验证查询网络错误", msg.obj.toString(), VehicleActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        SaveData();
                    } else if (resultInfo.getCode() == 2) {
                        ShowSaveDialog("提示", resultInfo.getMessage() + ",是否继续保存?");
                    } else {
                        helper.ShowErrDialog("提示", resultInfo.getMessage(), VehicleActivity.this);
                    }

                } catch (Exception ex) {
                    Toast.makeText(VehicleActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("验证查询系统错误", ex.getMessage(), VehicleActivity.this);
                }
            }
        }
    };

    private void GetDefaultValue(String flag, String value) {
        if (needchange==false) {
            return;
        }

        GetDefaultValueInfo info = new GetDefaultValueInfo();
        info.setFlag(flag);
        info.setValue(value);
        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(VehicleActivity.this, "提示", "数据默认值加载中...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = defaulthandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetDefaultValue");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler defaulthandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("数据默认值查询网络错误", msg.obj.toString(), VehicleActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        ChangeCllxDisplay(resultInfo.getData());
                    } else {
                        helper.ShowErrDialog("提示", resultInfo.getMessage(), VehicleActivity.this);
                    }

                } catch (Exception ex) {
                    Toast.makeText(VehicleActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("数据默认值查询系统错误", ex.getMessage(), VehicleActivity.this);
                }
            }
        }
    };

    private void ChangeCllxDisplay(String returnjson) {
        List<GetDefaultValueReturnInfo> data = JSON.parseArray(returnjson, GetDefaultValueReturnInfo.class);

        for (int i = 0; i <= data.size() - 1; i++) {
            String value = data.get(i).getValue();
            String id = data.get(i).getId();

            if (id.equals("zxzs")) {
                combox_vehicle_zxzs.setText(value);
            } else if (id.equals("zs")) {
                combox_vehicle_zs.setText(value);
            } else if (id.equals("qprs")) {
                et_vehicle_qprs.setText(value);
            } else if (id.equals("qzdz")) {
                combox_vehicle_qzdz.setText(value);
            } else if (id.equals("fxwz")) {
                combox_vehicle_fxwz.setText(value);
            } else if (id.equals("xgfs")) {
                combox_vehicle_xgfs.setText(value);
            } else if (id.equals("zdfs")) {
                combox_vehicle_zdfs.setText(value);
            } else if (id.equals("hxcc")) {
                if (value.equals("1")) {
                    lay_vehicle_hxnc.setVisibility(View.VISIBLE);
                } else {
                    lay_vehicle_hxnc.setVisibility(View.GONE);
                }
            } else if (id.equals("zczw")) {
                if (value.length() >= 4) {
                    cb_vehicle_zcwz1.setChecked(value.substring(0, 1).equals("1"));
                    cb_vehicle_zcwz2.setChecked(value.substring(1, 2).equals("1"));
                    cb_vehicle_zcwz3.setChecked(value.substring(2, 3).equals("1"));
                    cb_vehicle_zcwz4.setChecked(value.substring(3, 4).equals("1"));
                }
            } else if (id.equals("ywyx")) {   //以下扩展用
                combox_vehicle_ywlx.setText(value);
            } else if (id.equals("cp")) {
                combox_vehicle_cp.setText(value);
            } else if (id.equals("djys")) {
                combox_vehicle_djys.setText(value);
            } else if (id.equals("cyys")) {
                combox_vehicle_cyys.setText(value);
            } else if (id.equals("ryzl")) {
                combox_vehicle_ryzl.setText(value);
            } else if (id.equals("btgg")) {
                combox_vehicle_btgg.setText(value);
            } else if (id.equals("sfkqxj")) {
                cb_vehicle_sfkqxj.setChecked(value.equals("1"));
            } else if (id.equals("sfdzzc")) {
                cb_vehicle_sfdzzc.setChecked(value.equals("1"));
            }
        }

        ChangeZcwz(combox_vehicle_zs.getText());

    }

    private void timePicke(final EditText editText) {
        editText.setInputType(InputType.TYPE_NULL);
        final TimePickerView pvTime = new TimePickerView(VehicleActivity.this, TimePickerView.Type.YEAR_MONTH_DAY);
        Integer end = Integer.valueOf(DateUtil.getNowyear());
        pvTime.setRange(1960, end);
        pvTime.setTime(new Date());
        pvTime.setCyclic(true);
        pvTime.setCancelable(true);


        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                editText.setText(format.format(date));
            }
        });
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                pvTime.show();
                return true;
            }
        });


    }

    private void ChangeZcwz(String zs) {
        cb_vehicle_zcwz3.setVisibility(View.VISIBLE);
        cb_vehicle_zcwz4.setVisibility(View.VISIBLE);

        if (zs.equals("2") == true) {
            cb_vehicle_zcwz3.setVisibility(View.GONE);
            cb_vehicle_zcwz4.setVisibility(View.GONE);
        } else if (zs.equals("3") == true) {
            cb_vehicle_zcwz4.setVisibility(View.GONE);
        }
    }


    private void InitRemarkByYwlx(String ywlx) {
        try {
            et_vehicle_remark.setText("");
            et_vehicle_remarkjson.setText("");

            DefaultDBHelper db = DefaultDBHelper.getInstance(VehicleActivity.this, 1);
            db.openReadLink();
            ArrayList<DefaultInfo> list = db.query("flag='1' and id='" + ywlx + "'");
            db.closeLink();
            //------------------------------------------------------------------------------------
            if (list.size() <= 0) {
                return;
            }

            DefaultInfo info = list.get(0);
            List<VehicleGoodsInfo> temp = JSON.parseArray(info.value, VehicleGoodsInfo.class);


            String txtremark = "";
            for (int i = 0; i <= temp.size() - 1; i++) {
                if (temp.get(i).getQty().equals("0") == false) {
                    txtremark += temp.get(i).getTname() + ",";
                }
            }
            if (txtremark.length() >= 2) {
                txtremark = txtremark.substring(0, txtremark.length() - 1);
            }

            et_vehicle_remark.setText(txtremark);
            et_vehicle_remarkjson.setText(info.value);


        } catch (Exception ex) {
            Toast.makeText(VehicleActivity.this, "加载备注默认值：" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    private void QueryVisPhoto(String detectid, String zpzl) {

        GetVisPhotoInfo info = new GetVisPhotoInfo();
        info.setDetectid(detectid);
        info.setJylsh(jylsh);
        info.setJycs("1");
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
                helper.ShowErrDialog("图片下载网络错误", msg.obj.toString(), VehicleActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {

                        GetVisPhotoReturnInfo infoA = JSON.parseObject(resultInfo.getData(), GetVisPhotoReturnInfo.class);
                        //helper.ShowErrDialog("提示", "图片:"+ infoA.getZpzl() +",上传成功", VisActivity.this);

                        byte[] decodedString = Base64.decode(infoA.getBase64(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        if (infoA.getZpzl().equals(zpzlWG) == true)
                            img_vehicle_wgjyy.setImageBitmap(decodedByte);
                        if (infoA.getZpzl().equals(zpzlJBR) == true)
                            img_vehicle_jbr.setImageBitmap(decodedByte);


                    } else {
                        // helper.ShowErrDialog("图片上传失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), VisActivity.this);
                    }
                } catch (Exception ex) {
                    helper.ShowErrDialog("图片下载系统错误", ex.getMessage(), VehicleActivity.this);
                }
            }
        }
    };

    private void LoadSign() {
        img_vehicle_wgjyy = (ImageView) findViewById(R.id.img_vehicle_wgjyy);
        img_vehicle_wgjyy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VehicleActivity.this, SignActivity.class);
                //加入参数，传递给AnotherActivity
                intent.putExtra("Config", config);
                intent.putExtra("typeid", zpzlWG);
                intent.putExtra("jylsh", jylsh);
                intent.putExtra("jycs", "1");
                intent.putExtra("detectid", jylsh + "1");
                intent.putExtra("typename", "外检员签名");
                startActivityForResult(intent, 11);
            }
        });

        img_vehicle_jbr = (ImageView) findViewById(R.id.img_vehicle_jbr);
        img_vehicle_jbr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VehicleActivity.this, SignActivity.class);
                //加入参数，传递给AnotherActivity
                intent.putExtra("Config", config);
                intent.putExtra("typeid", zpzlJBR);
                intent.putExtra("jylsh", jylsh);
                intent.putExtra("jycs", "1");
                intent.putExtra("detectid", jylsh + "1");
                intent.putExtra("typename", "经办人签名");
                startActivityForResult(intent, 11);
            }
        });

    }


}
