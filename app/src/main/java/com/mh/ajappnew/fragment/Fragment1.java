package com.mh.ajappnew.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.activity.VehicleActivity;
import com.mh.ajappnew.activity.VisActivity;
import com.mh.ajappnew.activity.VisPhotoActivity;
import com.mh.ajappnew.adapter.VehicleFunc1Adapter;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.GetVisInsTaskInfo;
import com.mh.ajappnew.jkid.GetVisInsTaskReturnInfo;
import com.mh.ajappnew.tools.URLEncodeing;
import com.mh.ajappnew.tools.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment1 extends Fragment {

    private Config config;

    public void setConfig(Config config) {
        this.config = config;
    }

    private ProgressDialog pd;
    private EditText et_funcsub1_query;
    private Button but_funcsub1_query;
    private VehicleFunc1Adapter adapter;
    private ListView lv_funcsub1_vehicle;
    private String tempdetectid, temphphm,tempjylsh,tempjycs,tempywlx;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_funcsub1, null);

//        if(config==null)
//        {
//            return view;
//        }

        et_funcsub1_query = (EditText) view.findViewById(R.id.et_funcsub1_query);
        but_funcsub1_query = (Button) view.findViewById(R.id.but_funcsub1_query);
        but_funcsub1_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryVehicle();
            }
        });


        // 从布局视图中获取名叫lv_planet的列表视图
        lv_funcsub1_vehicle = (ListView) view.findViewById(R.id.lv_funcsub1_vehicle);

        lv_funcsub1_vehicle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tempdetectid = adapter.mPlanetList.get(position).getDetectid();
                tempjylsh = adapter.mPlanetList.get(position).getJylsh();
                tempjycs = adapter.mPlanetList.get(position).getJycs();
                temphphm = adapter.mPlanetList.get(position).getHphm();
                tempywlx = adapter.mPlanetList.get(position).getYwlx();
                Intent intent = new Intent(getActivity(), VisActivity.class);
                //加入参数，传递给AnotherActivity
                intent.putExtra("Config", config);
                intent.putExtra("detectid", tempdetectid);
                intent.putExtra("jylsh", tempjylsh);
                intent.putExtra("jycs", tempjycs);
                intent.putExtra("hphmmac", temphphm);
                intent.putExtra("device_group", "WG");
                intent.putExtra("ywlx", tempywlx);
                intent.putExtra("hphmcaption", temphphm);
                startActivity(intent);

            }
        });

//        lv_funcsub1_vehicle.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//               String lwcx = adapter.mPlanetList.get(position).getLwcx();
//                String clwyx = adapter.mPlanetList.get(position).getClwyx();
//                String wg = adapter.mPlanetList.get(position).getWg();
//                String dp = adapter.mPlanetList.get(position).getDp();
//                String dt = adapter.mPlanetList.get(position).getDt();
//
//                Toast.makeText(getActivity(), lwcx, Toast.LENGTH_SHORT).show();
//
//                //LoadRightKey(lwcx,clwyx,wg,dp,dt);
//                return true;
//            }
//        });

        LoadRightKey();

        return view;
    }

    private void QueryVehicle() {
        pd = ProgressDialog.show(this.getActivity(), "提示", "查询中...");
        pd.setCancelable(true);

        GetVisInsTaskInfo info = new GetVisInsTaskInfo();
        info.setHphm(et_funcsub1_query.getText().toString());
        info.setDevice_group("");
        String data = JSON.toJSONString(info);

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetVisInsTask");
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
                        List<GetVisInsTaskReturnInfo> qlist = JSON.parseArray(resultInfo.getData(), GetVisInsTaskReturnInfo.class);
                        adapter = new VehicleFunc1Adapter(getActivity(), qlist);

                        Toast.makeText(getActivity(),"外检记录数:"+ String.valueOf(qlist.size()),Toast.LENGTH_LONG).show();

                        // 给lv_planet设置行星列表适配器
                        lv_funcsub1_vehicle.setAdapter(adapter);

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
        lv_funcsub1_vehicle.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("功能选择？");

                menu.add(1, 12, 0, "外观检测");
                menu.add(1, 1, 0, "外观拍照");
                menu.add(1, 10, 0, "联网查询");
                menu.add(1, 11, 0, "唯一查验");

                menu.add(1, 13, 0, "底盘检测");
                menu.add(1, 14, 0, "动态检测");
                //menu.add(1, 0, 0, "资料修改");



            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.
                        getMenuInfo();

        if (item.getGroupId() != 1) {
            return false;
        }
        //position就是获取到的当前listview的位置
        int position = menuInfo.position;
        tempdetectid = adapter.mPlanetList.get(position).getDetectid();
        tempjycs = adapter.mPlanetList.get(position).getJycs();
        tempjylsh = adapter.mPlanetList.get(position).getJylsh();
        temphphm = adapter.mPlanetList.get(position).getHphm();
        tempywlx= adapter.mPlanetList.get(position).getYwlx();

        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(getActivity(), VehicleActivity.class);
                //加入参数，传递给AnotherActivity
                intent.putExtra("Config", config);
                intent.putExtra("caotype", "modify");
                intent.putExtra("detectid", tempdetectid);
                intent.putExtra("zt", "1");
                startActivity(intent);
                break;
            case 1:
                Intent intent1 = new Intent(getActivity(), VisPhotoActivity.class);
                //加入参数，传递给AnotherActivity
                intent1.putExtra("Config", config);
                intent1.putExtra("caotype", "modify");
                intent1.putExtra("detectid", tempdetectid);
                intent1.putExtra("jylsh", tempjylsh);
                intent1.putExtra("jycs", tempjycs);
                intent1.putExtra("hphmmac", temphphm);
                intent1.putExtra("ywlx", tempywlx);
                intent1.putExtra("hphmcaption", temphphm);

                intent1.putExtra("zt", "0");
                startActivity(intent1);
                break;
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                String device_group = "";
                if (item.getItemId() == 10) {
                    device_group = "LWCX";
                } else if (item.getItemId() == 11) {
                    device_group = "CLWYX";
                } else if (item.getItemId() == 12) {
                    device_group = "WG";
                } else if (item.getItemId() == 13) {
                    device_group = "DPDG";
                } else if (item.getItemId() == 14) {
                    device_group = "DPDT";
                }
                Intent intent10 = new Intent(getActivity(), VisActivity.class);
                //加入参数，传递给AnotherActivity
                intent10.putExtra("Config", config);
                intent10.putExtra("detectid", tempdetectid);
                intent10.putExtra("jylsh", tempjylsh);
                intent10.putExtra("jycs", tempjycs);
                intent10.putExtra("device_group", device_group);
                intent10.putExtra("hphmmac", temphphm);
                intent10.putExtra("ywlx", tempywlx);
                intent10.putExtra("hphmcaption", temphphm);
                startActivity(intent10);


                break;
            default:
                Toast.makeText(getActivity(), "Fragment1 ERR："+ item.getItemId()+" " + adapter.mPlanetList.get(position).getJylsh(), Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }


}
