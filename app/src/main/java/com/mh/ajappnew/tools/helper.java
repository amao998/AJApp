package com.mh.ajappnew.tools;

import android.app.Activity;


public class helper {

    public static void ShowErrDialog(String title, String desc,Activity activity ) {
        CustomDialog customDialog = new CustomDialog(activity);
        customDialog.setTitle(title);
        customDialog.setMessage(URLEncodeing.toURLDecoder(desc));
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

            }
        });
        customDialog.show();
        customDialog.setCancelVisable(false);
    }

}
