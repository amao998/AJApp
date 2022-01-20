package com.mh.ajappnew.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.adapter.Dialog1Adapter;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.db.ItemsDBHelper;
import com.mh.ajappnew.db.ItemsInfo;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.GetItemList;
import com.mh.ajappnew.jkid.GetItemListReturnInfo;
import com.mh.ajappnew.tools.CustomDialog;
import com.mh.ajappnew.tools.MessageTransmit;
import com.mh.ajappnew.tools.helper;
import com.mh.ajappnew.view.ComboxItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendInsActivity extends AppCompatActivity {

    private TextView tv_send_ins_hphm, tv_send_ins_jylsh, tv_send_ins_msg, tv_send_ins_caption;
    private Button but_send_ins_ok, but_send_ins_start;
    private MessageTransmit mTransmit; // 声明一个消息传输对象
    private Thread mthread;

    private Config config;
    private String hphm, jylsh,hphmcaption;
    private ProgressDialog pd;
    private Dialog1Adapter dialog1Adapter;

    private ComboxItemView combox_send_ins_line, combox_send_ins_ycy;
    private String zt = "send";
    private String flag = "1";
    private String line = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_ins);

        Intent i = getIntent();
        config = i.getParcelableExtra("Config");
        hphm = i.getStringExtra("hphm");
        jylsh = i.getStringExtra("jylsh");
        flag = i.getStringExtra("flag");
        line = i.getStringExtra("line");
        hphmcaption = i.getStringExtra("hphmcaption");

        tv_send_ins_msg = (TextView) findViewById(R.id.tv_send_ins_msg);
        tv_send_ins_hphm = (TextView) findViewById(R.id.tv_send_ins_hphm);
        tv_send_ins_jylsh = (TextView) findViewById(R.id.tv_send_ins_jylsh);
        combox_send_ins_line = (ComboxItemView) findViewById(R.id.combox_send_ins_line);
        combox_send_ins_ycy = (ComboxItemView) findViewById(R.id.combox_send_ins_ycy);

        tv_send_ins_msg.setText("");

        combox_send_ins_line = (ComboxItemView) findViewById(R.id.combox_send_ins_line);
        combox_send_ins_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryDataItem("100");
            }
        });

        combox_send_ins_ycy = (ComboxItemView) findViewById(R.id.combox_send_ins_ycy);
        combox_send_ins_ycy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QueryDataItem("101");
            }
        });

        //combox_send_ins_line.setText(config.getLine());
        combox_send_ins_ycy.setText(config.getJyy());

        tv_send_ins_hphm.setText(hphmcaption);
        tv_send_ins_jylsh.setText(jylsh);

        //加载控----------------------------------
        but_send_ins_ok = (Button) findViewById(R.id.but_send_ins_ok);
        but_send_ins_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //0.获取IP,port

                if (combox_send_ins_line.getTag() == null) {
                    helper.ShowErrDialog("提示", "请选择线号.", SendInsActivity.this);
                    return;
                }

                String remark = combox_send_ins_line.getTag().toString();
                String[] arr = remark.split(":");
                if (arr.length < 2) {
                    helper.ShowErrDialog("提示", "未设置线号IP,端口.", SendInsActivity.this);
                    return;
                }

                if (zt.equals("send") == true) {
                    ShowDialog();
                } else if (zt.equals("start") == true) {
                    String cmd = "start|" +
                            tv_send_ins_jylsh.getText() + "|" +
                            hphm + "|" +
                            combox_send_ins_line.getValue() + "|" +
                            combox_send_ins_ycy.getText();
                    SendCmd(cmd);
                }

            }
        });

        tv_send_ins_caption = (TextView) findViewById(R.id.tv_send_ins_caption);
        but_send_ins_start = (Button) findViewById(R.id.but_send_ins_start);


        but_send_ins_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmd = "start|" +
                        tv_send_ins_jylsh.getText() + "|" +
                        hphm + "|" +
                        combox_send_ins_line.getValue() + "|" +
                        combox_send_ins_ycy.getText();
                SendCmd(cmd);
            }
        });

        if (flag.equals("1") == true) {
            but_send_ins_start.setVisibility(View.GONE);
            but_send_ins_ok.setVisibility(View.VISIBLE);
            tv_send_ins_caption.setText("外廓上线检测-单机");
        } else if (flag.equals("2") == true) {
            but_send_ins_start.setVisibility(View.VISIBLE);
            but_send_ins_ok.setVisibility(View.GONE);
            tv_send_ins_caption.setText("外廓上线检测-联机");
        }

        but_send_ins_start.setEnabled(false);
        but_send_ins_ok.setEnabled(false);
        if (flag.equals("1") == true) {
            QueryDataItemOnlyOne();
        } else {
            QueryDataItemOnlyOneByline(line);
        }
    }

    private void QueryDataItem1(String flag) {
        GetItemList info = new GetItemList();
        info.setFlag(flag);
        info.setJcxlb("3");
        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(SendInsActivity.this, "提示", "项目查询中...");
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
                helper.ShowErrDialog("查询网络错误", msg.obj.toString(), SendInsActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        List<GetItemListReturnInfo> list = JSON.parseArray(resultInfo.getData(), GetItemListReturnInfo.class);
                        ShowDialogList(list);
                    } else {
                        helper.ShowErrDialog("查询失败", resultInfo.getMessage(), SendInsActivity.this);
                    }

                } catch (Exception ex) {
                    Toast.makeText(SendInsActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("查询系统错误", ex.getMessage(), SendInsActivity.this);
                }
            }
        }
    };

    private void QueryDataItem(String flag1) {
        pd = ProgressDialog.show(SendInsActivity.this, "提示", "项目加载中...");
        ItemsDBHelper db = ItemsDBHelper.getInstance(SendInsActivity.this, 1);
        db.openReadLink();
        ArrayList<ItemsInfo> list = db.query("flag='" + flag1 + "' and jcxlb='3'");
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

    private void QueryDataItemOnlyOne() {
        ItemsDBHelper db = ItemsDBHelper.getInstance(SendInsActivity.this, 1);
        db.openReadLink();
        ArrayList<ItemsInfo> list = db.query("flag='100' and jcxlb='3'");
        db.closeLink();
        if (list.size() > 0) {
            combox_send_ins_line.setText(list.get(0).value);
            combox_send_ins_line.setTag(list.get(0).remark);
            combox_send_ins_line.setValue(list.get(0).id);

            InitCmd(list.get(0).remark);
        }
    }

    private void QueryDataItemOnlyOneByline(String line1) {
        ItemsDBHelper db = ItemsDBHelper.getInstance(SendInsActivity.this, 1);
        db.openReadLink();
        ArrayList<ItemsInfo> list = db.query("flag='100' and jcxlb='3' and netid='" + line1 + "'");
        db.closeLink();
        if (list.size() > 0) {
            combox_send_ins_line.setText(list.get(0).value);
            combox_send_ins_line.setTag(list.get(0).remark);
            combox_send_ins_line.setValue(list.get(0).id);

            InitCmd(list.get(0).remark);
        }
    }

    private void ShowDialogList(List<GetItemListReturnInfo> list) {
        View view = View.inflate(SendInsActivity.this, R.layout.item_dialog1_listview, null);
        ListView lv = (ListView) view.findViewById(R.id.lv_dialog1_listview);
        dialog1Adapter = new Dialog1Adapter(SendInsActivity.this, list);
        lv.setAdapter(dialog1Adapter);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SendInsActivity.this);
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
                    combox_send_ins_line.setText(dialog1Adapter.list.get(position).getValue());
                    combox_send_ins_line.setTag(dialog1Adapter.list.get(position).getRemark());
                    combox_send_ins_line.setValue(dialog1Adapter.list.get(position).getId());

                    if (combox_send_ins_line.getValue() == null) {
                        helper.ShowErrDialog("提示", "请选择线号.", SendInsActivity.this);
                        return;
                    }

                    InitCmd(combox_send_ins_line.getTag().toString());

                } else if (temp.equals("101") == true) {
                    combox_send_ins_ycy.setText(dialog1Adapter.list.get(position).getValue());
                }

                dialog.dismiss();
            }
        });
    }

    private void InitCmd(String remark) {
        String[] arr = remark.split(":");
        if (arr.length < 2) {
            helper.ShowErrDialog("提示", "未设置线号IP,端口.", SendInsActivity.this);
            return;
        }

        tv_send_ins_msg.append("IP:" + arr[0] + ",端口:" + arr[1] + "\r\n");
        //1.开启Soket
        StartSoket(arr[0], arr[1]);
    }

    private void ShowDialog() {

        String title = "提示";
        String desc = "是否是否发送 车牌:" + tv_send_ins_hphm.getText() +
                "线号:" + combox_send_ins_line.getText();

        CustomDialog customDialog = new CustomDialog(SendInsActivity.this);
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
                String cmd = "send|" +
                        tv_send_ins_jylsh.getText() + "|" +
                        hphm + "|" +
                        combox_send_ins_line.getValue() + "|" +
                        combox_send_ins_ycy.getText();
                SendCmd(cmd);
            }
        });
        customDialog.show();
        customDialog.setCancelVisable(true);
    }


    private void StartSoket(String ip, String port) {
        mTransmit = new MessageTransmit(ip, port); // 创建一个消息传输
        mTransmit.handler = sHandler;
        mthread = new Thread(mTransmit); // 启动消息传输线程
        mthread.start();
    }

    // 创建一个主线程的接收处理器，专门处理服务器发来的消息
    public Handler sHandler = new Handler() {
        // 在收到消息时触发
        public void handleMessage(Message msg) {
            if (msg.what == 2) {
                ShowSoketErrDialog("提示", "连接检测程序Soket服务失败,请检查.\r\n" + msg.obj.toString());
            } else if (msg.what == 1) {
                new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message message) {
                        String cmd = "init" + "|" + "Y" + "|" + "网络初使化成功";
                        SendCmd(cmd);
                        but_send_ins_ok.setEnabled(true);
                        but_send_ins_start.setEnabled(true);

                        return true;
                    }
                }).sendEmptyMessageDelayed(0, 1000);


            } else if (msg.what == 3) {
                String desc = "检测程序Soket服务返回:" + msg.obj.toString();

                String cmd = msg.obj.toString();
                int len = cmd.length();
                if (len >= 2) {
                    if (cmd.substring(0, 1).equals("@") && cmd.substring(len - 1, len).equals("$")) {
                        String tempcmd = cmd.substring(1, len - 1);
                        SocketCmd(tempcmd);

                    }
                }

                tv_send_ins_msg.append("返回<-" + String.valueOf(msg.what) + " " + msg.obj.toString() + "\r\n");
            }
        }
    };

    private void SendCmd(String cmd) {
        tv_send_ins_msg.append("发送->" + cmd + "\r\n");
        Message msgS = Message.obtain();
        msgS.obj = "@" + cmd + "$";
        // 通过消息线程的发送处理器，向后端发送消息
        mTransmit.mSendHandler.sendMessage(msgS);
    }

    //包头@,包尾$
    //命令格式   接收命令格式  命令字,流水号,检测状态(步骤)
    //                      命令字：start,ing,end
    //         返回命令格式   命令字,流水号,结果,数据,收到的命令
    //                      命令字:data-时实数据,cmd-命令
    private void SocketCmd(String cmd) {
        String[] arr = cmd.split("\\|");
        if (arr.length < 3) {
            //tv_obd_return.setText("无效命令:" + cmd);
            return;
        }

        Toast.makeText(this, arr[0], Toast.LENGTH_SHORT).show();
        if (arr[0].equals("send") == true)  //开始检测
        {
            zt = "start";
            but_send_ins_ok.setText("启动");
        } else if (arr[0].equals("start") == true)  //开始检测
        {
            but_send_ins_ok.setText("检测中");
            zt = "";
        } else if (arr[0].equals("stop") == true)  //结束检测
        {
            but_send_ins_ok.setText("重新发送");
            zt = "send";
        } else if (arr[0].equals("ing") == true) {

        }

    }

    private void ShowSoketErrDialog(String title, String desc) {
        CustomDialog customDialog = new CustomDialog(SendInsActivity.this);
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

            }
        });
        customDialog.show();
        customDialog.setCancelVisable(false);

    }
}
