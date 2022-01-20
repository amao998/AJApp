package com.mh.ajappnew.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.adapter.VehicleGoodsAdapter;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.comm.VehicleGoodsInfo;
import com.mh.ajappnew.db.ItemsDBHelper;
import com.mh.ajappnew.db.ItemsInfo;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.GetItemList;
import com.mh.ajappnew.jkid.GetItemListReturnInfo;
import com.mh.ajappnew.tools.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleGoodsActivity extends AppCompatActivity {

    private ListView lv_vehiclegoods_data;
    private VehicleGoodsAdapter vehicleGoodsAdapter;
    //private List<VehicleGoodsInfo> data = new ArrayList<VehicleGoodsInfo>();
    private TextView tv_vehiclegoods_typename;
    private Button but_vehiclegoods_exit, but_vehiclegoods_ok;
    //private List<GetItemListReturnInfo> moudles = new ArrayList<GetItemListReturnInfo>();
    private Config config = null;
    private ProgressDialog pd;
    private String json;

    private String flag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_goods);
//加载项目
        Intent i = getIntent();
        config = i.getParcelableExtra("Config");
        json = i.getStringExtra("json");
        flag = i.getStringExtra("flag");

        InitButton();
        InitItemsCombox(flag);  //15
    }

    private void InitButton() {
        tv_vehiclegoods_typename = (TextView) findViewById(R.id.tv_vehiclegoods_typename);
        tv_vehiclegoods_typename.setText("");
        if (flag.equals("15") == true) {
            tv_vehiclegoods_typename.setText("随车物品");
        } else if (flag.equals("17") == true) {
            tv_vehiclegoods_typename.setText("备注");
        }

        but_vehiclegoods_ok = (Button) findViewById(R.id.but_vehiclegoods_ok);
        but_vehiclegoods_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = JSON.toJSONString(vehicleGoodsAdapter.list);
                //Toast.makeText(VehicleGoodsActivity.this, json, Toast.LENGTH_LONG).show();

                String value = GetScwp();

                Intent intent = new Intent();
                intent.putExtra("value", value);
                intent.putExtra("json", json);
                setResult(Integer.valueOf(flag), intent);
                //关闭当前activity
                finish();
            }
        });

        but_vehiclegoods_exit = (Button) findViewById(R.id.but_vehiclegoods_exit);
        but_vehiclegoods_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String GetScwp() {
        String temp = "";
        List<VehicleGoodsInfo> list = vehicleGoodsAdapter.list;
        for (int i = 0; i <= vehicleGoodsAdapter.list.size() - 1; i++) {

            if (list.get(i).getQty().equals("0") == false) {
                if (flag.equals("15") == true) {
                    temp += list.get(i).getPrefix() +
                            list.get(i).getTname() +
                            list.get(i).getQty() +
                            list.get(i).getUnit() + ",";
                } else if (flag.equals("17") == true) {
                    temp += list.get(i).getTname() + ",";
                }
            }
        }

        if (temp.length() >= 2) {
            temp = temp.substring(0, temp.length() - 1);
        }

        return temp;

    }


    private void InitItemsCombox1(String flag) {
        GetItemList GetItemList = new GetItemList();
        GetItemList.setFlag(flag);
        String data = JSON.toJSONString(GetItemList);

        pd = ProgressDialog.show(VehicleGoodsActivity.this, "提示", "项目加载中...");
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
                helper.ShowErrDialog("加载项目网络错误", msg.obj.toString(), VehicleGoodsActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        List<GetItemListReturnInfo> temp = JSON.parseArray(resultInfo.getData(), GetItemListReturnInfo.class);
                        Expdata(temp);
                    } else {
                        helper.ShowErrDialog("加载项目失败", resultInfo.getMessage(), VehicleGoodsActivity.this);
                    }

                } catch (Exception ex) {
                    Toast.makeText(VehicleGoodsActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("加载项目系统错误", ex.getMessage(), VehicleGoodsActivity.this);
                }
            }

        }
    };


    private void Expdata(List<GetItemListReturnInfo> moudles) {
        List<VehicleGoodsInfo> temp = new ArrayList<VehicleGoodsInfo>();
        List<VehicleGoodsInfo> data = new ArrayList<VehicleGoodsInfo>();
        //1.从上一页面传来的scwpjson
        if (json.equals("") == false) {
            temp = JSON.parseArray(json, VehicleGoodsInfo.class);
        }
        //2.
        for (int i = 0; i <= moudles.size() - 1; i++) {
            VehicleGoodsInfo info = new VehicleGoodsInfo();
            info.setId(moudles.get(i).getId());
            info.setTname(moudles.get(i).getValue());
            if(flag.equals("15")==true) {
                info.setUnit(moudles.get(i).getNetid());
            }else  if(flag.equals("17")==true) {
                info.setUnit("");
            }
            info.setNetid(moudles.get(i).getNetid());

            VehicleGoodsInfo findinfo = FindData(temp, moudles.get(i).getId());

            if (findinfo != null) {
                info.setQty(findinfo.getQty());
                info.setPrefix(findinfo.getPrefix());
            } else {
                //if(flag.equals("17")==true) {
                //    info.setQty(FindHasMap(moudles.get(i).getId()));
                //}else {
                info.setQty("0");
                //}
                info.setPrefix(moudles.get(i).getRemark());
            }
            data.add(info);
        }

        Boolean showprefix = flag.equals("17") ? false : true;
        lv_vehiclegoods_data = (ListView) findViewById(R.id.lv_vehiclegoods_data);
        vehicleGoodsAdapter = new VehicleGoodsAdapter(VehicleGoodsActivity.this, data, showprefix);
        lv_vehiclegoods_data.setAdapter(vehicleGoodsAdapter);

    }

    private VehicleGoodsInfo FindData(List<VehicleGoodsInfo> temp, String id) {
        VehicleGoodsInfo info = null;
        for (int i = 0; i <= temp.size() - 1; i++) {
            if (id.equals(temp.get(i).getId()) == true) {
                info = temp.get(i);
                continue;
            }
        }
        return info;
    }


    private void InitItemsCombox(String flag1) {
        pd = ProgressDialog.show(VehicleGoodsActivity.this, "提示", "项目加载中...");
        ItemsDBHelper db = ItemsDBHelper.getInstance(VehicleGoodsActivity.this, 1);
        db.openReadLink();
        ArrayList<ItemsInfo> list = db.query("flag='" + flag1 + "'");
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
            info.setRemark(list.get(i).remark);
            info.setNetid(list.get(i).netid);
            data.add(info);
        }
        Expdata(data);
    }


}
