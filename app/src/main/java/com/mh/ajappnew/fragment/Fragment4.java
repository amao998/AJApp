package com.mh.ajappnew.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.db.ConfigDBHelper;
import com.mh.ajappnew.db.DefaultDBHelper;
import com.mh.ajappnew.db.DefaultInfo;
import com.mh.ajappnew.db.DeviceDBHelper;
import com.mh.ajappnew.db.DeviceInfo;
import com.mh.ajappnew.db.DeviceTDDBHelper;
import com.mh.ajappnew.db.DeviceTDInfo;
import com.mh.ajappnew.db.ItemsDBHelper;
import com.mh.ajappnew.db.ItemsInfo;
import com.mh.ajappnew.db.PDAVerDBHelper;
import com.mh.ajappnew.db.PDAVerDbInfo;
import com.mh.ajappnew.http.AutoUpdater;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.GetDefaultValueInfo;
import com.mh.ajappnew.jkid.GetDefaultValueReturnInfo;
import com.mh.ajappnew.jkid.GetDeviceListInfo;
import com.mh.ajappnew.jkid.GetDeviceListReturnInfo;
import com.mh.ajappnew.jkid.GetItemList;
import com.mh.ajappnew.jkid.GetItemListReturnInfo;
import com.mh.ajappnew.jkid.GetPDALineInfo;
import com.mh.ajappnew.jkid.GetPDALineReturnInfo;
import com.mh.ajappnew.jkid.ModifyPwd;
import com.mh.ajappnew.jkid.PDAVerInfo;
import com.mh.ajappnew.jkid.PDAVerReturnInfo;
import com.mh.ajappnew.tools.CustomDialog;
import com.mh.ajappnew.tools.URLEncodeing;
import com.mh.ajappnew.tools.helper;
import com.mh.ajappnew.view.PersonalItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fragment4 extends Fragment {
    private Config config;

    public void setConfig(Config config) {
        this.config = config;
    }

    private PersonalItemView item_personal_ver = null;
    private PersonalItemView item_personal4_verlocal = null;
    private PersonalItemView item_personal4_tempdata = null;
    private String path;
    private String remark;
    private int serverver;

    private ProgressDialog pd;
    private String TostMsg = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_funcsub4, null);
//        if (config == null) {
//            return view;
//        }

        PersonalItemView item_personal4_name = view.findViewById(R.id.item_personal4_name);
        item_personal4_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), config.getToken(), Toast.LENGTH_LONG).show();
            }
        });

        item_personal4_name.setData(config.getUsername() + " " + config.getJyy());

        PersonalItemView item_personal_modifypwd = view.findViewById(R.id.item_personal4_modifypwd);
        item_personal_modifypwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPwdDialog();
            }
        });

        PersonalItemView item_personal_quit = view.findViewById(R.id.item_personal4_quit);
        item_personal_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().finish();
                QuitApp();
            }
        });

        item_personal_ver = view.findViewById(R.id.item_personal4_ver);
        item_personal_ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (config.getVersioncode().equals(String.valueOf(serverver)) == true) {
                    Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(getActivity(), remark, Toast.LENGTH_LONG).show();

                String desc = "????????????????????????????????????:" + config.getVersioncode() + "????????????:" + serverver + ",??????????????????!";
                AutoUpdater manager = new AutoUpdater(getActivity(), config.getIp(), config.getPort(), desc, true);
                manager.Update(path);
            }
        });

        item_personal4_verlocal = view.findViewById(R.id.item_personal4_verlocal);
        item_personal4_tempdata = view.findViewById(R.id.item_personal4_tempdata);
        item_personal4_tempdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitItemsCombox();
            }
        });

        GetVersion();

        return view;
    }

    public void ShowPwdDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("????????????...");

        final View v = getLayoutInflater().inflate(R.layout.activity_loginpwddialog, null);
        builder.setView(v);
        final EditText et_pwd_userid = v.findViewById(R.id.et_pwd_userid);
        final EditText et_pwd_oldpwd = v.findViewById(R.id.et_pwd_oldpwd);
        final EditText et_pwd_newpwd = v.findViewById(R.id.et_pwd_newpwd);
        /*????????????*/
        et_pwd_userid.setText(config.getUsername());

        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                ModifyUserPwd(et_pwd_userid.getText().toString(), et_pwd_oldpwd.getText().toString(), et_pwd_newpwd.getText().toString());
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    private void ModifyUserPwd(String userid, String oldpwd, String newpwd) {
        if (newpwd.equals("") == true) {
            helper.ShowErrDialog("??????", "?????????????????????.", getActivity());
            return;
        }

        if (newpwd.equals(oldpwd) == true) {
            helper.ShowErrDialog("??????", "?????????????????????????????????.", getActivity());
            return;
        }

        ModifyPwd info = new ModifyPwd();
        info.setUser_no(userid);
        info.setPwdnew(newpwd);
        info.setPwdold(oldpwd);
        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(getActivity(), "??????", "???????????????...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = pwdhandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "ModifyPwd");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler pwdhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler???????????????????????????????????????
            pd.dismiss();// ??????ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("????????????????????????", msg.obj.toString(), getActivity());
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        helper.ShowErrDialog("??????", resultInfo.getMessage(), getActivity());
                    } else {
                        helper.ShowErrDialog("??????????????????", resultInfo.getMessage(), getActivity());
                    }
                } catch (Exception ex) {
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("????????????????????????", ex.getMessage(), getActivity());
                }
            }
        }
    };


    private void QuitApp() {
        CustomDialog customDialog = new CustomDialog(getActivity());
        customDialog.setTitle("???????????????");
        customDialog.setMessage("???????????????????");
        customDialog.setCancel("??????", new CustomDialog.IOnCancelListener() {
            @Override
            public void onCancel(CustomDialog dialog) {
                //Toast.makeText(LogInActivity.this, "???????????????",Toast.LENGTH_SHORT).show();
            }
        });
        customDialog.setConfirm("??????", new CustomDialog.IOnConfirmListener() {
            @Override
            public void onConfirm(CustomDialog dialog) {
                //Toast.makeText(LogInActivity.this, "???????????????",Toast.LENGTH_SHORT).show();
                ConfigDBHelper db = ConfigDBHelper.getInstance(getActivity(), 1);
                db.openWriteLink();
                db.updateOnlyPwd(config.getUsername(), "");
                db.closeLink();

                System.exit(0);
            }
        });
        customDialog.show();
    }

    private void GetVersion() {

        String appid = getActivity().getPackageName();

        PDAVerInfo info = new PDAVerInfo();
        info.setAppid(appid);
        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(getActivity(), "??????", "??????????????????...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = verhandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "PDAVer");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler verhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler???????????????????????????????????????
            pd.dismiss();// ??????ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("???????????????????????????", msg.obj.toString(), getActivity());
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        //helper.ShowErrDialog("??????", resultInfo.getMessage(), getActivity());

                        PDAVerReturnInfo info = JSON.parseObject(resultInfo.getData(), PDAVerReturnInfo.class);

                        if (config.getPicmodle().equals("0") || config.getPicmodle().equals("")) {
                            config.setPicmodle(info.getPicmodle());
                        }

                        if (config.getPichight().equals("0") || config.getPichight().equals("")) {
                            config.setPichight(info.getPichight());
                        }

                        if (config.getPicwidth().equals("0") || config.getPicwidth().equals("")) {
                            config.setPicwidth(info.getPicwidth());
                        }

                        if (config.getPicrarrate().equals("0") || config.getPicrarrate().equals("")) {
                            config.setPicrarrate(info.getPicrarrate());
                        }

                        CheckNeedUpdate(info);
                        ExpVer(info);
                        SavePDAVer(info);

                    } else {
                        helper.ShowErrDialog("?????????????????????", resultInfo.getMessage(), getActivity());
                    }

                } catch (Exception ex) {
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("????????????????????????", ex.getMessage(), getActivity());
                }
            }

        }
    };

    private void ExpVer(PDAVerReturnInfo info) {
        item_personal_ver.setData(config.getVersionname());
        path = info.getUrl();
        serverver = info.getVersioncode();
        remark = info.getRemark();

        if (config.getVersionname().equals(info.getVersionname())) {
            item_personal_ver.setData("??????????????????" + info.getVersionname());
        } else {
            item_personal_ver.setData("???????????????:" + info.getVersionname());

            String desc = "????????????????????????????????????:" + config.getVersionname() + "????????????:" + info.getVersionname() + ",??????????????????!";
            //????????????
            Boolean canquit = config.getUsername().contains("admin") || config.getUsername().equals("001");

            Toast.makeText(getActivity(), "??????????????????:" + info.getVersionname() + ".", Toast.LENGTH_LONG).show();
            AutoUpdater manager = new AutoUpdater(getActivity(), config.getIp(), config.getPort(), desc, canquit);
            manager.Update(path);
        }

        item_personal4_verlocal.setData(config.getVersionname());

    }


    //????????????---------------------------------------------------------------------------------------
    private void CheckNeedUpdate(PDAVerReturnInfo info) {
        PDAVerDBHelper db = PDAVerDBHelper.getInstance(getActivity(), 1);
        db.openReadLink();
        PDAVerDbInfo data = db.queryByappid(info.getAppid());
        db.closeLink();

        if (data == null) {
            InitItemsCombox();
            Toast.makeText(getActivity(), "???????????????1...", Toast.LENGTH_LONG).show();
        } else if (data.itemupdatetime.equals(info.getItemupdatetime()) == false) {
            InitItemsCombox();
            Toast.makeText(getActivity(), "???????????????3...", Toast.LENGTH_SHORT).show();
        }
        item_personal4_tempdata.setData(info.getItemupdatetime());
    }

    private void InitItemsCombox() {
        TostMsg = "";
        GetItemList GetItemList = new GetItemList();
        GetItemList.setFlag("");
        String data = JSON.toJSONString(GetItemList);

        pd = ProgressDialog.show(getActivity(), "??????", "???????????????...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = comboxhandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetItemList");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler comboxhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler???????????????????????????????????????
            pd.dismiss();// ??????ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("????????????????????????", msg.obj.toString(), getActivity());
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        List<GetItemListReturnInfo> list = JSON.parseArray(resultInfo.getData(), GetItemListReturnInfo.class);

                        initDB(list);
                        LoadPDALine();
                    } else {
                        helper.ShowErrDialog("??????????????????", resultInfo.getMessage(), getActivity());
                    }

                } catch (Exception ex) {
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("????????????????????????", ex.getMessage(), getActivity());
                }
            }
        }
    };

    private void initDB(List<GetItemListReturnInfo> list) {
        ItemsDBHelper db = ItemsDBHelper.getInstance(getActivity(), 1);
        db.openWriteLink();

        ArrayList<ItemsInfo> data = new ArrayList<ItemsInfo>();

        db.deleteAll();

        for (int i = 0; i <= list.size() - 1; i++) {
            ItemsInfo info = new ItemsInfo();
            info.id = list.get(i).getId();
            info.value = list.get(i).getValue();
            info.idandname = list.get(i).getIdandname();
            info.flag = list.get(i).getFlag();
            info.netid = list.get(i).getNetid();
            info.remark = list.get(i).getRemark();
            info.sort = list.get(i).getSort();
            info.jcxlb = list.get(i).getJcxlb();
            data.add(info);
        }

        db.insert(data);
        db.closeLink();

        TostMsg += "??????????????????" + String.valueOf(list.size()) + "?????????...\r\n";
        //Toast.makeText(getActivity(), "??????????????????" + String.valueOf(list.size()) + "?????????...", Toast.LENGTH_LONG).show();
    }

    private void SavePDAVer(PDAVerReturnInfo data) {
        PDAVerDBHelper db = PDAVerDBHelper.getInstance(getActivity(), 1);
        db.openWriteLink();

        db.deleteAll();

        PDAVerDbInfo info = new PDAVerDbInfo();
        info.appid = data.getAppid();
        info.versioncode = String.valueOf(data.getVersioncode());
        info.versionname = data.getVersionname();
        info.url = data.getUrl();
        info.remark = data.getRemark();
        info.itemupdatetime = data.getItemupdatetime();
        db.insert(info);

        db.closeLink();
    }

    private void LoadPDALine() {
        GetPDALineInfo info = new GetPDALineInfo();
        info.setDevice_group("");
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
        public void handleMessage(Message msg) {// handler???????????????????????????????????????
            if (msg.what == 0) {
                helper.ShowErrDialog("????????????????????????", msg.obj.toString(), getActivity());
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        //1.????????????
                        List<GetPDALineReturnInfo> infoA = JSON.parseArray(resultInfo.getData(), GetPDALineReturnInfo.class);
                        initDBLine(infoA);
                        InitItemsComboxDeviceTD();
                    } else {
                        helper.ShowErrDialog("??????????????????", URLEncodeing.toURLDecoder(resultInfo.getMessage()), getActivity());
                    }
                } catch (Exception ex) {
                    helper.ShowErrDialog("????????????????????????", ex.getMessage(), getActivity());
                }
            }
        }
    };

    private void initDBLine(List<GetPDALineReturnInfo> list) {
        DeviceDBHelper db = DeviceDBHelper.getInstance(getActivity(), 1);
        db.openWriteLink();

        ArrayList<DeviceInfo> data = new ArrayList<DeviceInfo>();

        db.deleteAll();

        for (int i = 0; i <= list.size() - 1; i++) {
            DeviceInfo info = new DeviceInfo();
            info.id = list.get(i).getId();
            info.device_group = list.get(i).getDevice_group();
            data.add(info);
        }

        db.insert(data);
        db.closeLink();
        TostMsg += "??????????????????" + String.valueOf(list.size()) + "?????????...\r\n";
        //Toast.makeText(getActivity(), "??????????????????" + String.valueOf(list.size()) + "?????????...", Toast.LENGTH_LONG).show();
    }

    private void InitItemsComboxDeviceTD() {
        GetDeviceListInfo info = new GetDeviceListInfo();
        info.setDevicetype("Photo");
        info.setZpzl("");
        String data = JSON.toJSONString(info);

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = comboxdevhandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetDeviceList");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler comboxdevhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler???????????????????????????????????????
            if (msg.what == 0) {
                helper.ShowErrDialog("????????????????????????", msg.obj.toString(), getActivity());
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        List<GetDeviceListReturnInfo> list = JSON.parseArray(resultInfo.getData(), GetDeviceListReturnInfo.class);
                        initDBChannel(list);
                        GetDefaultValue();
                    } else {
                        helper.ShowErrDialog("??????????????????", resultInfo.getMessage(), getActivity());
                    }

                } catch (Exception ex) {
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("????????????????????????", ex.getMessage(), getActivity());
                }
            }

        }
    };

    private void initDBChannel(List<GetDeviceListReturnInfo> list) {
        DeviceTDDBHelper db = DeviceTDDBHelper.getInstance(getActivity(), 1);
        db.openWriteLink();

        ArrayList<DeviceTDInfo> data = new ArrayList<DeviceTDInfo>();

        db.deleteAll();

        for (int i = 0; i <= list.size() - 1; i++) {
            DeviceTDInfo info = new DeviceTDInfo();
            info.PhotoType = list.get(i).getZpzl();
            info.device_name = list.get(i).getValue();
            info.device_sn = list.get(i).getId();
            data.add(info);
        }

        db.insert(data);
        db.closeLink();
        TostMsg += "??????????????????" + list.size() + "?????????...\r\n";
        //Toast.makeText(getActivity(), "??????????????????" + list.size() + "?????????...", Toast.LENGTH_LONG).show();
    }

    private void GetDefaultValue() {
        GetDefaultValueInfo info = new GetDefaultValueInfo();
        info.setFlag("1");
        info.setValue("*");
        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(getActivity(), "??????", "????????????????????????...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = defaulthandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetDefaultValue");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler defaulthandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler???????????????????????????????????????
            pd.dismiss();// ??????ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("?????????????????????????????????", msg.obj.toString(), getActivity());
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        List<GetDefaultValueReturnInfo> data = JSON.parseArray(resultInfo.getData(), GetDefaultValueReturnInfo.class);
                        initDBDefault(data);
                    } else {
                        helper.ShowErrDialog("??????", resultInfo.getMessage(), getActivity());
                    }

                } catch (Exception ex) {
                    Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("?????????????????????????????????", ex.getMessage(), getActivity());
                }
            }
        }
    };

    private void initDBDefault(List<GetDefaultValueReturnInfo> list) {
        DefaultDBHelper db = DefaultDBHelper.getInstance(getActivity(), 1);
        db.openWriteLink();

        ArrayList<DefaultInfo> data = new ArrayList<DefaultInfo>();

        db.deleteAll();

        for (int i = 0; i <= list.size() - 1; i++) {
            DefaultInfo info = new DefaultInfo();
            info.id = list.get(i).getId();
            info.value = list.get(i).getValue();
            info.flag = list.get(i).getFlag();
            data.add(info);
        }

        db.insert(data);
        db.closeLink();

        TostMsg += "??????????????????" + list.size() + "?????????...\r\n";
        Toast.makeText(getActivity(), TostMsg, Toast.LENGTH_LONG).show();
    }


}