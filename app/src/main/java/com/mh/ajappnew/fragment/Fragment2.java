package com.mh.ajappnew.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.activity.SendInsActivity;
import com.mh.ajappnew.activity.VehicleActivity;
import com.mh.ajappnew.adapter.VehicleFunc2Adapter;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.QVehicleInfo;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.GetVehicleInfoList;
import com.mh.ajappnew.tools.URLEncodeing;
import com.mh.ajappnew.tools.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment2 extends Fragment {
    private Config config;

    public void setConfig(Config config) {
        this.config = config;
    }

    private ProgressDialog pd;
    private EditText et_funcsub2_query;
    private Button but_funcsub2_query, but_funcsub2_add;
    private VehicleFunc2Adapter adapter;
    private ListView lv_funcsub2_vehicle;
    private String tempdetectid, temphphm, tempzt;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_funcsub2, null);

//        if (config == null) {
//            return view;
//        }

        et_funcsub2_query = (EditText) view.findViewById(R.id.et_funcsub2_query);
        but_funcsub2_query = (Button) view.findViewById(R.id.but_funcsub2_query);
        but_funcsub2_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryVehicle();
            }
        });


        // 从布局视图中获取名叫lv_planet的列表视图
        lv_funcsub2_vehicle = (ListView) view.findViewById(R.id.lv_funcsub2_vehicle);

        lv_funcsub2_vehicle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tempdetectid = adapter.mPlanetList.get(position).getJylsh();
                temphphm = adapter.mPlanetList.get(position).getHphmmac();
                tempzt = adapter.mPlanetList.get(position).getZt();
                Intent intent = new Intent(view.getContext(), VehicleActivity.class);
                //加入参数，传递给AnotherActivity
                intent.putExtra("Config", config);
                intent.putExtra("caotype", "modify");
                intent.putExtra("jylsh", tempdetectid);
                intent.putExtra("zt", tempzt);
                startActivity(intent);

            }
        });

        but_funcsub2_add = (Button) view.findViewById(R.id.but_funcsub2_add);
        but_funcsub2_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VehicleActivity.class);
                //加入参数，传递给AnotherActivity
                intent.putExtra("Config", config);
                intent.putExtra("caotype", "add");
                intent.putExtra("jylsh", "");
                intent.putExtra("zt", "");
                startActivity(intent);

            }
        });

        LoadRightKey();

        return view;
    }

    private void QueryVehicle() {
        pd = ProgressDialog.show(this.getActivity(), "提示", "查询中...");
        pd.setCancelable(true);

        GetVehicleInfoList GetVehicleInfoList = new GetVehicleInfoList();
        GetVehicleInfoList.setHphmmac(et_funcsub2_query.getText().toString());
        GetVehicleInfoList.setHphm("");
        GetVehicleInfoList.setZt("0,1,2");
        String data = JSON.toJSONString(GetVehicleInfoList);

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetVehicleInfoList");
        maps.put("data", data);
        postRequestUtil.Run(maps);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("车辆查询网络错误", msg.obj.toString(), getActivity());
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        //1.加载数据
                        List<QVehicleInfo> qlist = JSON.parseArray(resultInfo.getData(), QVehicleInfo.class);
                        adapter = new VehicleFunc2Adapter(getActivity(), qlist);
                        Toast.makeText(getActivity(), "登录记录数:" + String.valueOf(qlist.size()), Toast.LENGTH_LONG).show();
                        // 给lv_planet设置行星列表适配器
                        lv_funcsub2_vehicle.setAdapter(adapter);

                    } else {
                        helper.ShowErrDialog("车辆查询失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), getActivity());
                    }
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("车辆查询系统错误", ex.getMessage(), getActivity());
                }
            }
        }
    };

    private void LoadRightKey() {
        lv_funcsub2_vehicle.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("功能选择？");

                menu.add(2, 20, 0, "资料修改");
                menu.add(2, 21, 0, "外廓检测");
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.
                        getMenuInfo();

        if (item.getGroupId() != 2) {
            return false;
        }
        //position就是获取到的当前listview的位置
        int position = menuInfo.position;
        tempdetectid = adapter.mPlanetList.get(position).getJylsh();
        temphphm = adapter.mPlanetList.get(position).getHphmmac();
        tempzt = adapter.mPlanetList.get(position).getZt();

        switch (item.getItemId()) {
            case 20:

                Intent intent = new Intent(getActivity(), VehicleActivity.class);
                //加入参数，传递给AnotherActivity
                intent.putExtra("Config", config);
                intent.putExtra("caotype", "modify");
                intent.putExtra("jylsh", tempdetectid);
                intent.putExtra("zt", tempzt);
                startActivity(intent);
                break;
            case 21:
                Intent intent1 = new Intent(getActivity(), SendInsActivity.class);
                //加入参数，传递给AnotherActivity
                intent1.putExtra("Config", config);
                intent1.putExtra("caotype", "modify");
                intent1.putExtra("jylsh", tempdetectid);
                intent1.putExtra("zt", tempzt);
                intent1.putExtra("hphm", temphphm);
                intent1.putExtra("flag","1");
                intent1.putExtra("line","");
                intent1.putExtra("hphmcaption",temphphm);

                startActivity(intent1);
                break;

            default:
                Toast.makeText(getActivity(), "Fragment2 ERR：" + item.getItemId() + " " + adapter.mPlanetList.get(position).getJylsh(), Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }




}
