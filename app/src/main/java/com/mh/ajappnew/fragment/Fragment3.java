package com.mh.ajappnew.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.activity.SendInsActivity;
import com.mh.ajappnew.activity.VisActivity;
import com.mh.ajappnew.activity.VisPhotoActivity;
import com.mh.ajappnew.adapter.Dialog1Adapter;
import com.mh.ajappnew.adapter.VehicleFunc3Adapter;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.db.ItemsDBHelper;
import com.mh.ajappnew.db.ItemsInfo;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.GetItemList;
import com.mh.ajappnew.jkid.GetItemListReturnInfo;
import com.mh.ajappnew.jkid.GetOnOffLineTaskInfo;
import com.mh.ajappnew.jkid.GetOnOffLineTaskReturnInfo;
import com.mh.ajappnew.jkid.SendOnOffLineInfo;
import com.mh.ajappnew.tools.CustomDialog;
import com.mh.ajappnew.tools.URLEncodeing;
import com.mh.ajappnew.tools.helper;
import com.mh.ajappnew.view.ComboxItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment3 extends Fragment {

    private Config config;

    public void setConfig(Config config) {
        this.config = config;
    }

    private ProgressDialog pd;
    private EditText et_funcsub3_query;
    private Button but_funcsub3_query;
    private VehicleFunc3Adapter adapter;
    private ListView lv_funcsub3_vehicle;
    private String tempdetectid, temphphm, tempflag, tempywlx, tempjylsh, tempjycs, temphphmmac, templine;
    private ComboxItemView combox_funcsub3_line, combox_funcsub3_ycy;
    private RadioGroup rg_funcsub3_flag;
    private Dialog1Adapter dialog1Adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_funcsub3, null);
//        if(config==null)
//        {
//            return view;
//        }

        combox_funcsub3_line = (ComboxItemView) view.findViewById(R.id.combox_funcsub3_line);
        combox_funcsub3_ycy = (ComboxItemView) view.findViewById(R.id.combox_funcsub3_ycy);

        rg_funcsub3_flag = (RadioGroup) view.findViewById(R.id.rg_funcsub3_flag);


        combox_funcsub3_line.setText(config.getLine());
        combox_funcsub3_ycy.setText(config.getJyy());

        et_funcsub3_query = (EditText) view.findViewById(R.id.et_funcsub3_query);
        but_funcsub3_query = (Button) view.findViewById(R.id.but_funcsub3_query);
        but_funcsub3_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryVehicleA();
            }
        });

        // 从布局视图中获取名叫lv_planet的列表视图
        lv_funcsub3_vehicle = (ListView) view.findViewById(R.id.lv_funcsub3_vehicle);
        lv_funcsub3_vehicle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tempdetectid = adapter.mPlanetList.get(position).getDetectid();
                temphphm = adapter.mPlanetList.get(position).getHphm();
                tempflag = adapter.mPlanetList.get(position).getFlag();
                temphphmmac = adapter.mPlanetList.get(position).getHphmmac();
                templine = adapter.mPlanetList.get(position).getLine();

                ShowDialog();
            }
        });

        //2021-04-15加
        combox_funcsub3_line = (ComboxItemView) view.findViewById(R.id.combox_funcsub3_line);
        combox_funcsub3_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryDataItem("100");
            }
        });

        combox_funcsub3_ycy = (ComboxItemView) view.findViewById(R.id.combox_funcsub3_ycy);
        combox_funcsub3_ycy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryDataItem("101");
            }
        });


        LoadRightKey();

        return view;
    }

    private void QueryVehicleA() {
        String flag = "0";
        for (int i = 0; i < rg_funcsub3_flag.getChildCount(); i++) {
            RadioButton rb = (RadioButton) rg_funcsub3_flag.getChildAt(i);
            if (rb.isChecked()) {
                flag = rb.getTag().toString();
                break;
            }
        }
        //为1表示联网登录
        QueryVehicle(flag);
    }


    private void QueryVehicle(String flag) {
        pd = ProgressDialog.show(this.getActivity(), "提示", "查询中...");
        pd.setCancelable(true);

        GetOnOffLineTaskInfo info = new GetOnOffLineTaskInfo();
        info.setHphm(et_funcsub3_query.getText().toString());
        info.setFlag(flag);
        String data = JSON.toJSONString(info);

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetOnOffLineTask");
        maps.put("data", data);
        postRequestUtil.Run(maps);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("待检查询网络错误", msg.obj.toString(), getActivity());
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        //1.加载数据
                        List<GetOnOffLineTaskReturnInfo> qlist = JSON.parseArray(resultInfo.getData(), GetOnOffLineTaskReturnInfo.class);
                        adapter = new VehicleFunc3Adapter(getActivity(), qlist);
                        Toast.makeText(getActivity(), "记录数:" + String.valueOf(qlist.size()), Toast.LENGTH_LONG).show();
                        // 给lv_planet设置行星列表适配器
                        lv_funcsub3_vehicle.setAdapter(adapter);

                    } else {
                        helper.ShowErrDialog("待检查询失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), getActivity());
                    }
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("待检查询系统错误", ex.getMessage(), getActivity());
                }
            }
        }
    };


    private void QueryDataItem11(String flag) {
        GetItemList info = new GetItemList();
        info.setFlag(flag);
        info.setJcxlb("1");
        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(getActivity(), "提示", "项目查询中...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = queryjcdlhandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetItemList");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler queryjcdlhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("查询网络错误", msg.obj.toString(), getActivity());
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        List<GetItemListReturnInfo> list = JSON.parseArray(resultInfo.getData(), GetItemListReturnInfo.class);
                        ShowDialogList(list);
                    } else {
                        helper.ShowErrDialog("查询失败", resultInfo.getMessage(), getActivity());
                    }

                } catch (Exception ex) {
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("查询系统错误", ex.getMessage(), getActivity());
                }
            }
        }
    };

    private void QueryDataItem(String flag1) {
        pd = ProgressDialog.show(getActivity(), "提示", "项目加载中...");
        ItemsDBHelper db = ItemsDBHelper.getInstance(getActivity(), 1);
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
        View view = View.inflate(getActivity(), R.layout.item_dialog1_listview, null);
        ListView lv = (ListView) view.findViewById(R.id.lv_dialog1_listview);
        dialog1Adapter = new Dialog1Adapter(getActivity(), list);
        lv.setAdapter(dialog1Adapter);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
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
                if (temp.equals("100") == true) {
                    combox_funcsub3_line.setText(dialog1Adapter.list.get(position).getId());
                } else if (temp.equals("101") == true) {
                    combox_funcsub3_ycy.setText(dialog1Adapter.list.get(position).getValue());
                }

                dialog.dismiss();
            }
        });
    }

    private void ShowDialog() {
        String flagname = "";
        if (tempflag.equals("1") == true) {
            flagname = "[上线]";
        } else if (tempflag.equals("2") == true) {
            flagname = "[下线]";
        }

        String title = "提示";
        String desc = "是否" + flagname + "?\r\n车牌:" + temphphm + "/" + temphphmmac +
                "线号:" + combox_funcsub3_line.getText();

        CustomDialog customDialog = new CustomDialog(getActivity());
        customDialog.setTitle(title);
        customDialog.setMessage(desc);
        customDialog.setCancel("", new CustomDialog.IOnCancelListener() {
            @Override
            public void onCancel(CustomDialog dialog) {
                //Toast.makeText(LogInActivity.this, "取消成功！",Toast.LENGTH_SHORT).show();
            }
        });
        customDialog.setConfirm("确定", new CustomDialog.IOnConfirmListener() {
            @Override
            public void onConfirm(CustomDialog dialog) {
                //Toast.makeText(LogInActivity.this, "确认成功！",Toast.LENGTH_SHORT).show();
                SendVehicleOnOffline(tempflag);
            }
        });
        customDialog.show();
        customDialog.setCancelVisable(true);
    }


    private void SendVehicleOnOffline(String flag) {
        SendOnOffLineInfo info = new SendOnOffLineInfo();
        info.setFlag(flag);
        info.setDetectid(tempdetectid);
        info.setJyy(combox_funcsub3_ycy.getText());
        info.setYcy(combox_funcsub3_ycy.getText());
        info.setSjy(config.getJyy());
        info.setLine(combox_funcsub3_line.getText());
        info.setLinename(combox_funcsub3_line.getText());
        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(getActivity(), "提示", "发送命令中...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handlerA;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "SendOnOffLine");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler handlerA = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("查询网络错误", msg.obj.toString(), getActivity());
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        helper.ShowErrDialog("提示", resultInfo.getMessage(), getActivity());
                    } else {
                        helper.ShowErrDialog("查询失败", resultInfo.getMessage(), getActivity());
                    }

                } catch (Exception ex) {
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("查询系统错误", ex.getMessage(), getActivity());
                }
            }
        }
    };

    private void LoadRightKey() {
        lv_funcsub3_vehicle.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("功能选择？");

                menu.add(3, 32, 0, "外观检测");
                //menu.add(1, 0, 0, "资料修改");
                menu.add(3, 39, 0, "外观拍照");
                menu.add(3, 40, 0, "外廓上线");

                menu.add(3, 30, 0, "联网查询");
                menu.add(3, 31, 0, "唯一查验");

                menu.add(3, 33, 0, "底盘检测");
                menu.add(3, 34, 0, "动态检测");


            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.
                        getMenuInfo();

        if (item.getGroupId() != 3) {
            return false;
        }
        //position就是获取到的当前listview的位置
        int position = menuInfo.position;
        tempdetectid = adapter.mPlanetList.get(position).getDetectid();
        tempjycs = adapter.mPlanetList.get(position).getJycs();
        tempjylsh = adapter.mPlanetList.get(position).getJylsh();
        temphphm = adapter.mPlanetList.get(position).getHphm();
        tempywlx = adapter.mPlanetList.get(position).getYwlx();
        templine = adapter.mPlanetList.get(position).getLine();
        temphphmmac = adapter.mPlanetList.get(position).getHphmmac();

        switch (item.getItemId()) {
            case 39:
                Intent intent1 = new Intent(getActivity(), VisPhotoActivity.class);
                //加入参数，传递给AnotherActivity
                intent1.putExtra("Config", config);
                intent1.putExtra("caotype", "modify");
                intent1.putExtra("detectid", tempdetectid);
                intent1.putExtra("jylsh", tempjylsh);
                intent1.putExtra("jycs", tempjycs);
                intent1.putExtra("hphmmac", temphphm);
                intent1.putExtra("ywlx", tempywlx);
                intent1.putExtra("hphmcaption", temphphm + "/" + temphphmmac);

                intent1.putExtra("zt", "2");
                startActivity(intent1);
                break;
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                String device_group = "";
                if (item.getItemId() == 30) {
                    device_group = "LWCX";
                } else if (item.getItemId() == 31) {
                    device_group = "CLWYX";
                } else if (item.getItemId() == 32) {
                    device_group = "WG";
                } else if (item.getItemId() == 33) {
                    device_group = "DPDG";
                } else if (item.getItemId() == 34) {
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
                if (temphphm.contains("粤") == true) {
                    intent10.putExtra("hphmcaption", temphphm);
                } else {
                    intent10.putExtra("hphmcaption", temphphmmac);
                }
                startActivity(intent10);


                break;
            case 40:
                Intent intent40 = new Intent(getActivity(), SendInsActivity.class);
                //加入参数，传递给AnotherActivity
                intent40.putExtra("Config", config);
                intent40.putExtra("caotype", "modify");
                intent40.putExtra("jylsh", tempdetectid);
                intent40.putExtra("zt", "");
                intent40.putExtra("hphm", temphphm);
                intent40.putExtra("flag", "2");
                intent40.putExtra("line", templine);
                intent40.putExtra("hphmcaption", temphphm + "/" + temphphmmac);
                startActivity(intent40);
                break;

            default:
                Toast.makeText(getActivity(), "Fragment3 ERR：" + item.getItemId() + " " + adapter.mPlanetList.get(position).getJylsh(), Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

}
