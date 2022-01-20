package com.mh.ajappnew.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mh.ajappnew.R;
import com.mh.ajappnew.adapter.LTIputListAdapter;
import com.mh.ajappnew.comm.LTInputInfo;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class LTInputActivity extends AppCompatActivity {

    private ListView lv_ltinput_data;
    private List<LTInputInfo> LTList = new ArrayList<>();
    private LTIputListAdapter cadapter;

    private Button but_ltinput_ok, but_ltinput_exit;

    private int starti = 1;
    private int endi = 3;
    private String value = "A1:1.5/A2:1.2/C3:12/";
    private String stand = "2.5";
    private String typeid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ltinput);
        //参数
        Intent i = getIntent();
        starti = Integer.parseInt(i.getStringExtra("starti"));
        endi = Integer.parseInt(i.getStringExtra("endi"));
        value = i.getStringExtra("value");
        stand = i.getStringExtra("stand");
        typeid = i.getStringExtra("typeid");
        //显示
        TextView tv_ltinput_typename = (TextView) findViewById(R.id.tv_ltinput_typename);
        if (typeid.equals("1")) {
            tv_ltinput_typename.setText("转向轮胎");
        }else  if (typeid.equals("2")) {
            tv_ltinput_typename.setText("其它轮胎");
        }else  if (typeid.equals("3")) {
            tv_ltinput_typename.setText("挂车轮胎");
        }

        but_ltinput_ok = (Button) findViewById(R.id.but_ltinput_ok);
        but_ltinput_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp1 = "";
                for (int i = 0; i <= cadapter.list.size() - 1; i++) {
                    if (cadapter.list.get(i).getValue() != null) {
                        if (cadapter.list.get(i).getValue().equals("") == false) {
                            temp1 += cadapter.list.get(i).getId() + ":" + cadapter.list.get(i).getValue() + "/";
                        }
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("typeid", typeid);
                intent.putExtra("value", temp1);
                setResult(4, intent);
                //关闭当前activity
                finish();
                Toast.makeText(LTInputActivity.this, temp1, Toast.LENGTH_LONG).show();

            }
        });

        but_ltinput_exit = (Button) findViewById(R.id.but_ltinput_exit);
        but_ltinput_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //回传地址
                //设置返回的数据
                Intent intent = new Intent();
                intent.putExtra("typeid", typeid);
                setResult(0, intent);
                //关闭当前activity
                finish();
            }
        });


        InitData(value);
    }


    private void InitData(String value) {

        if (value == null) value = "";
        Dictionary<String, String> dic = ExpDic(value);


        for (int i = starti; i <= endi; i++) {
            String code = "A";
            if (i == 1) code = "A";
            if (i == 2) code = "B";
            if (i == 3) code = "C";
            if (i == 4) code = "D";
            if (i == 5) code = "E";
            if (i == 6) code = "F";
            if (i == 7) code = "G";
            if (i == 8) code = "H";

            for (int j = 1; j <= 4; j++) {
                String code1 = code + String.valueOf(j);
                LTInputInfo info = new LTInputInfo();
                info.setId(code1);
                info.setPd("-");
                info.setStand(stand);
                info.setValue(dic.get(code1));
                LTList.add(info);
            }
        }


        lv_ltinput_data = (ListView) findViewById(R.id.lv_ltinput_data);
        cadapter = new LTIputListAdapter(LTInputActivity.this, LTList);
        lv_ltinput_data.setAdapter(cadapter);

    }

    private Dictionary<String, String> ExpDic(String value) {
        Dictionary<String, String> temp = new Hashtable<String, String>();
        String[] arr = value.split("/");
        for (int i = 0; i <= arr.length - 1; i++) {
            String[] arr1 = arr[i].split(":");
            if (arr1.length >= 2) {
                if (((Hashtable<String, String>) temp).contains(arr1[0]) == false) {
                    temp.put(arr1[0], arr1[1]);
                }
            }
        }

        return temp;
    }
}
