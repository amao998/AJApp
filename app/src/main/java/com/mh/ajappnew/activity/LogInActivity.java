package com.mh.ajappnew.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.db.ConfigDBHelper;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.CheckPdaLogIn;
import com.mh.ajappnew.jkid.CheckPdaLogInReturnInfo;
import com.mh.ajappnew.jkid.ModifyPwd;
import com.mh.ajappnew.tools.CustomDialog;
import com.mh.ajappnew.tools.DateUtil;
import com.mh.ajappnew.tools.URLEncodeing;
import com.mh.ajappnew.tools.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LogInActivity extends AppCompatActivity {
    private Button butlogin, butquit, but_pwd;
    private EditText etuserid = null;
    private EditText etpassword = null;

    private Config config = null;
    private String imei = "";

    private boolean isExit = false;
    private Timer timer;
    private TextView tv_imei, tv_ver;
    private ImageView img_setting;

    AlertDialog mPermissionDialog;
    String mPackName = "com.mh.ajapp";
    //1、首先声明一个数组permissions，将所有需要申请的权限都放在里面
    String[] permissions = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_PHONE_STATE};
    List<String> mPermissionList = new ArrayList<>();
    private final int mRequestCode = 100;//权限请求码

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        //加载控件
        etuserid = (EditText) findViewById(R.id.et_userid);
        etpassword = (EditText) findViewById(R.id.et_password);
        etpassword.setText("");

        butlogin = (Button) findViewById(R.id.but_login);
        butlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckLogIn();
                //GoInFunc();
            }
        });

        img_setting = (ImageView) findViewById(R.id.img_setting);
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
            }
        });

        //加载imei
        //imei = DeviceIdUtils.getDeviceId(this);
        tv_imei = (TextView) findViewById(R.id.tv_imei);
        tv_imei.setText(imei.toUpperCase());
        tv_ver = (TextView) findViewById(R.id.tv_ver);

        initPermission();
        LoadConfig();

        AutoLogIn();
    }

    private void AutoLogIn() {

        ConfigDBHelper db = ConfigDBHelper.getInstance(LogInActivity.this, 1);
        db.openReadLink();
        String pwd = db.queryPwdByUserID(etuserid.getText().toString());
        db.closeLink();

        if (pwd.equals("") == false) {
            etpassword.setText(pwd);
            CheckLogIn();
        }
    }


    private void CheckLogIn() {
        String user = etuserid.getText().toString();
        String pwd = etpassword.getText().toString();

        if (user.equals("") == true) {
            helper.ShowErrDialog("提示", "请输入用户名", LogInActivity.this);
            return;
        }

        if (pwd.equals("") == true) {
            helper.ShowErrDialog("提示", "请输入密码", LogInActivity.this);
            return;
        }

        CheckPdaLogIn CheckPdaLogIn = new CheckPdaLogIn();
        CheckPdaLogIn.setUser_no(user);
        CheckPdaLogIn.setUser_pwd(pwd);
        String data = JSON.toJSONString(CheckPdaLogIn);

        pd = ProgressDialog.show(LogInActivity.this, "提示", "用户登录中...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "CheckPdaLogIn");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                //ShowErrDialog("网络错误", msg.obj.toString());
                helper.ShowErrDialog("网络错误", msg.obj.toString(), LogInActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {

                        CheckPdaLogInReturnInfo logInInfo = JSON.parseObject(resultInfo.getData(), CheckPdaLogInReturnInfo.class);
                        config.setUsername(logInInfo.getUserno());
                        config.setJyy(logInInfo.getUsername());
                        config.setImei(imei);

                        GoInFunc();

//                        GetServerNowTime();
                    } else {
                        helper.ShowErrDialog("登录失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), LogInActivity.this);
                    }
                } catch (Exception ex) {
                    Toast.makeText(LogInActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    //ShowErrDialog("系统错误", ex.getMessage());
                }
            }
        }
    };


    private void GoInFunc() {

        ConfigDBHelper db = ConfigDBHelper.getInstance(LogInActivity.this, 1);
        db.openWriteLink();
        db.updateOnlyPwd(etuserid.getText().toString(),etpassword.getText().toString());
        db.closeLink();

        Intent intent = new Intent(LogInActivity.this, FuncActivity.class);
        //加入参数，传递给AnotherActivity
        intent.putExtra("Config", config);
        startActivity(intent);
        finish();
    }

    private void LoadConfig() {
        try {

            SharedPreferences shared = getSharedPreferences("share", MODE_PRIVATE);
            String ip = shared.getString("ip", "192.168.1.101");
            String port = shared.getString("port", "8082");
            String line = shared.getString("line", "01");
            String username = shared.getString("username", "001");
            String picwidth = shared.getString("picwidth", "1920");
            String pichight = shared.getString("pichight", "1080");
            String picmodle = shared.getString("picmodle", "1");
            String picrarrate = shared.getString("picrarrate", "50");

            String token = "GZMH3358" + DateUtil.getNowday();
            String versionName = LogInActivity.this.getPackageManager().getPackageInfo(LogInActivity.this.getPackageName(), 0).versionName;

            String appid = LogInActivity.this.getPackageName();
            int versioncode = LogInActivity.this.getPackageManager().getPackageInfo(LogInActivity.this.getPackageName(), 0).versionCode;

            config = new Config(ip, port, line, username, token, imei, "", picwidth, pichight, "", appid, String.valueOf(versioncode), versionName, picrarrate, picmodle);
            etuserid.setText(config.getUsername());

            tv_ver.setText(versionName);

        } catch (Exception ex) {
            Toast.makeText(LogInActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void ShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("参数设置...");

        final View v = getLayoutInflater().inflate(R.layout.activity_loginsettingdialog, null);
        builder.setView(v);
        final EditText etip = v.findViewById(R.id.etip);
        final EditText etport = v.findViewById(R.id.etport);
        final EditText etusername = v.findViewById(R.id.etusername);
        final EditText etline = v.findViewById(R.id.etline);
        final EditText etwidth = v.findViewById(R.id.etwidth);
        final EditText ethight = v.findViewById(R.id.ethight);
        final EditText etpicmodle = v.findViewById(R.id.etpicmodle);
        final EditText etpicrarrate = v.findViewById(R.id.etpicrarrate);

        /*读取参数*/
        etip.setText(config.getIp());
        etport.setText(config.getPort());
        etline.setText(config.getLine());
        etusername.setText(config.getUsername());
        etwidth.setText(config.getPicwidth());
        ethight.setText(config.getPichight());

        etpicmodle.setText(config.getPicmodle());
        etpicrarrate.setText(config.getPicrarrate());

        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences shared = getSharedPreferences("share", MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("ip", etip.getText().toString());
                editor.putString("port", etport.getText().toString());
                editor.putString("line", etline.getText().toString());
                editor.putString("username", etusername.getText().toString());

                editor.putString("picwidth", etwidth.getText().toString());
                editor.putString("pichight", ethight.getText().toString());

                editor.putString("picmodle", etpicmodle.getText().toString());
                editor.putString("picrarrate", etpicrarrate.getText().toString());
                editor.commit();

                Toast.makeText(LogInActivity.this, "参数保存成功.请重启APP.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(LogInActivity.this, "取消参数保存", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }


    private void QuitApp() {
        CustomDialog customDialog = new CustomDialog(LogInActivity.this);
        customDialog.setTitle("请做出选择");
        customDialog.setMessage("确定要退出吗?");
        customDialog.setCancel("取消", new CustomDialog.IOnCancelListener() {
            @Override
            public void onCancel(CustomDialog dialog) {
                //Toast.makeText(LogInActivity.this, "取消成功！",Toast.LENGTH_SHORT).show();
            }
        });
        customDialog.setConfirm("确定", new CustomDialog.IOnConfirmListener() {
            @Override
            public void onConfirm(CustomDialog dialog) {
                //Toast.makeText(LogInActivity.this, "确认成功！",Toast.LENGTH_SHORT).show();
                System.exit(0);
            }
        });
        customDialog.show();
    }


    public void ShowPwdDialog() {
        if (etuserid.getText().equals("") == true) {
            helper.ShowErrDialog("提示", "请输入用户名", LogInActivity.this);
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("密码修改...");

        final View v = getLayoutInflater().inflate(R.layout.activity_loginpwddialog, null);
        builder.setView(v);
        final EditText et_pwd_userid = v.findViewById(R.id.et_pwd_userid);
        final EditText et_pwd_oldpwd = v.findViewById(R.id.et_pwd_oldpwd);
        final EditText et_pwd_newpwd = v.findViewById(R.id.et_pwd_newpwd);
        /*读取参数*/
        et_pwd_userid.setText(etuserid.getText());

        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                ModifyUserPwd(et_pwd_userid.getText().toString(), et_pwd_oldpwd.getText().toString(), et_pwd_newpwd.getText().toString());
            }
        });
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();

    }

    private void ModifyUserPwd(String userid, String oldpwd, String newpwd) {
        if (newpwd.equals("") == true) {
            helper.ShowErrDialog("提示", "新密码不能为空.", LogInActivity.this);
            return;
        }

        ModifyPwd ModifyPwd = new ModifyPwd();
        ModifyPwd.setUser_no(userid);
        ModifyPwd.setPwdnew(newpwd);
        ModifyPwd.setPwdold(oldpwd);
        String data = JSON.toJSONString(ModifyPwd);

        pd = ProgressDialog.show(LogInActivity.this, "提示", "密码保存中...");
        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = pwdhandler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "ModifyPwd");
        maps.put("data", data);

        postRequestUtil.Run(maps);
    }

    Handler pwdhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("密码修改网络错误", msg.obj.toString(), LogInActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        helper.ShowErrDialog("提示", resultInfo.getMessage(), LogInActivity.this);
                    } else {
                        helper.ShowErrDialog("密码修改失败", resultInfo.getMessage(), LogInActivity.this);
                    }

                } catch (Exception ex) {
                    Toast.makeText(LogInActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("密码修改系统错误", ex.getMessage(), LogInActivity.this);
                }
            }

        }
    };


    private String getAndroidIMEI() {
        String deviceID = "";

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            final TelephonyManager tm = (TelephonyManager) this.getSystemService
                    (Context.TELEPHONY_SERVICE);
            String tmDevice, tmSerial, androidId;
            tmDevice = "" + tm.getDeviceId();

            if (TextUtils.isEmpty(tmDevice)) {
                tmDevice = Settings.System.getString(
                        this.getContentResolver(), Settings.Secure.ANDROID_ID);
            }

//            tmSerial = "" + tm.getSimSerialNumber();
//            androidId = "" + android.provider.Settings.Secure.getString(this
//                    .getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
//
//            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() <<
//                    32) | tmSerial.hashCode());
            deviceID = tmDevice;
        }
        return deviceID;
    }

    //重写onKeyDown()方法
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //点击返回键调用方法
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
        }
        return false;
    }

    //点击返回键调用的方法
    private void exit() {
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            //2000ms内按第二次则退出
            finish();
            System.exit(0);
        }
    }

    /**
     * 5.请求权限后回调的方法
     *
     * @param requestCode  是我们自己定义的权限请求码
     * @param permissions  是我们请求的权限名称数组
     * @param grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限
     *                     名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                    break;
                }
            }
        }
        if (hasPermissionDismiss) {//如果有没有被允许的权限
            showPermissionDialog();
        } else {
            //权限已经都通过了，可以将程序继续打开了

        }
    }

    private void showPermissionDialog() {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(this)
                    .setMessage("已禁用权限，请手动授予")
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelPermissionDialog();

                            Uri packageURI = Uri.parse("package:" + mPackName);
                            Intent intent = new Intent(Settings.
                                    ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作
                            cancelPermissionDialog();
                            LogInActivity.this.finish();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    private void cancelPermissionDialog() {
        mPermissionDialog.cancel();
    }

    //4、权限判断和申请
    private void initPermission() {
        mPermissionList.clear();//清空已经允许的没有通过的权限
        //逐个判断是否还有未通过的权限
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) !=
                    PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限到mPermissionList中
            }
        }
        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, mRequestCode);
        } else {
            //权限已经都通过了，可以将程序继续打开了
            //init();
        }
    }
}
