package com.mh.ajappnew.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.adapter.RemarkAdapter;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.RemarkInfo;
import com.mh.ajappnew.comm.ResultInfo;
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

public class RemarkActivity extends AppCompatActivity {

    private ListView lv_remark_data;
    private RemarkAdapter remarkAdapter;
    private Button but_remark_exit, but_remark_ok;

    private Config config = null;
    private ProgressDialog pd;
    private String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark);
//加载项目
        Intent i = getIntent();
        config = i.getParcelableExtra("Config");
        json = i.getStringExtra("json");

        InitButton();
        InitItemsCombox("17");

    }

    private void InitButton() {
        but_remark_ok = (Button) findViewById(R.id.but_remark_ok);
        but_remark_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = JSON.toJSONString(remarkAdapter.list);

                String value = GetValue();

                Intent intent = new Intent();
                intent.putExtra("value", value);
                intent.putExtra("json", json);
                setResult(17, intent);
                //关闭当前activity
                finish();
            }
        });

        but_remark_exit = (Button) findViewById(R.id.but_remark_exit);
        but_remark_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String GetValue() {
        String temp = "";
        List<RemarkInfo> list = remarkAdapter.list;
        for (int i = 0; i <= remarkAdapter.list.size() - 1; i++) {
            if (list.get(i).getGoin().equals("1") == true) {
                temp += list.get(i).getTname()+",";
            }
        }

        if (temp.length() >= 2) {
            temp = temp.substring(0, temp.length() - 1);
        }

        return temp;

    }


    private void InitItemsCombox11(String flag) {
        GetItemList GetItemList = new GetItemList();
        GetItemList.setFlag(flag);
        String data = JSON.toJSONString(GetItemList);

        pd = ProgressDialog.show(RemarkActivity.this, "提示", "项目加载中...");
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
                helper.ShowErrDialog("加载项目网络错误", msg.obj.toString(), RemarkActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        List<GetItemListReturnInfo> temp = JSON.parseArray(resultInfo.getData(), GetItemListReturnInfo.class);
                        Expdata(temp);
                    } else {
                        helper.ShowErrDialog("加载项目失败", resultInfo.getMessage(), RemarkActivity.this);
                    }

                } catch (Exception ex) {
                    Toast.makeText(RemarkActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("加载项目系统错误", ex.getMessage(), RemarkActivity.this);
                }
            }

        }
    };

    private void InitItemsCombox(String flag1) {
        pd = ProgressDialog.show(RemarkActivity.this, "提示", "项目加载中...");
        ItemsDBHelper db = ItemsDBHelper.getInstance(RemarkActivity.this, 1);
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


    private void Expdata(List<GetItemListReturnInfo> moudles) {
        List<RemarkInfo> temp = new ArrayList<RemarkInfo>();
        List<RemarkInfo> data = new ArrayList<RemarkInfo>();
        //1.从上一页面传来的scwpjson
        if (json.equals("") == false) {
            temp = JSON.parseArray(json, RemarkInfo.class);
        }
        //2.
        for (int i = 0; i <= moudles.size() - 1; i++) {
            String id=moudles.get(i).getId();

            RemarkInfo info = new RemarkInfo();
            info.setId(moudles.get(i).getId());
            info.setTname(moudles.get(i).getValue());

            RemarkInfo findinfo = FindData(temp, moudles.get(i).getId());

            if (findinfo != null) {
                 String goin=findinfo.getGoin();
                info.setGoin(goin);
            } else {
                info.setGoin("0");
            }
            data.add(info);
        }

        lv_remark_data = (ListView) findViewById(R.id.lv_remark_data);
        remarkAdapter = new RemarkAdapter(RemarkActivity.this, data);
        lv_remark_data.setAdapter(remarkAdapter);

    }

    private RemarkInfo FindData(List<RemarkInfo> temp, String id) {
        RemarkInfo info = null;
        for (int i = 0; i <= temp.size() - 1; i++) {
            if (id.equals(temp.get(i).getId()) == true) {
                info = temp.get(i);
                continue;
            }
        }
        return info;
    }


}
