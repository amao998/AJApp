package com.mh.ajappnew.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.adapter.SearchAdapter;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.comm.SearchInfo;
import com.mh.ajappnew.db.ItemsDBHelper;
import com.mh.ajappnew.db.ItemsInfo;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.GetItemList;
import com.mh.ajappnew.tools.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private List<SearchInfo> data = new ArrayList<SearchInfo>();
    private SearchAdapter searchAdapter;
    private ListView lv_search_list;
    private SearchView search_searchview;
    private Button but_search_quit;
    private TextView tv_search_caption;

    private Config config;
    private String flag;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
//返回
        Intent i = getIntent();
        config = i.getParcelableExtra("Config");
        flag = i.getStringExtra("flag");


        InitSearch();
        InitItemsCombox(flag);

        but_search_quit = (Button) findViewById(R.id.but_search_quit);
        but_search_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_search_caption = (TextView) findViewById(R.id.tv_search_caption);
        if (flag.equals("2") == true) {
            tv_search_caption.setText("【车辆类型】搜索");
        } else if (flag.equals("11") == true) {
            tv_search_caption.setText("【备胎规格】搜索");
        } else if (flag.equals("12") == true) {
            tv_search_caption.setText("【车辆品牌】搜索");
        }

    }


    private void InitSearch() {
        lv_search_list = findViewById(R.id.lv_search_list);
        search_searchview = findViewById(R.id.search_searchview);

        searchAdapter = new SearchAdapter(SearchActivity.this, data);
        lv_search_list.setAdapter(searchAdapter);
        lv_search_list.setDividerHeight(0);


//        设置ListView启动过滤
        lv_search_list.setTextFilterEnabled(true);
//        设置该SearchView默认是否自动缩小为图标
        search_searchview.setIconifiedByDefault(false);
//        设置该SearchView显示搜索图标
        search_searchview.setSubmitButtonEnabled(true);
//        设置该SearchView内默认显示的搜索文字
        search_searchview.setQueryHint("查找");
//        为SearchView组件设置事件的监听器
        search_searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //            单击搜索按钮时激发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
//                实际应用中应该在该方法内执行实际查询
//                此处仅使用Toast显示用户输入的查询内容
//                Toast.makeText(SearchActivity.this, "您选择的是：" + query,
//                        Toast.LENGTH_SHORT).show();
                CloseActivity(query);
                return false;
            }

            //            用户输入时激发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
//                如果newText不是长度为0的字符串
                if (TextUtils.isEmpty(newText)) {
//                    清除ListView的过滤
                    lv_search_list.clearTextFilter();
                    searchAdapter.getFilter().filter("");
                } else {
//                    使用用户输入的内容对ListView的列表项进行过滤
                    //list.setFilterText(newText);
                    searchAdapter.getFilter().filter(newText.toUpperCase());

                }
                return true;
            }
        });

        lv_search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String value = data.get(position).getValue();
                String value = searchAdapter.citysBeanList.get(position).getValue();
                //Toast.makeText(SearchActivity.this, value, Toast.LENGTH_LONG).show();
                CloseActivity(value);
            }
        });

    }

    private void CloseActivity(String value) {
        Intent i = new Intent();
        i.putExtra("value", value);
        i.putExtra("flag", flag);
        setResult(1, i);
        finish();

    }

    private void InitItemsComboxOLD(String flag) {
        GetItemList GetItemList = new GetItemList();
        GetItemList.setFlag(flag);
        String data = JSON.toJSONString(GetItemList);

        pd = ProgressDialog.show(SearchActivity.this, "提示", "项目加载中...");
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
                helper.ShowErrDialog("加载项目网络错误", msg.obj.toString(), SearchActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        data = JSON.parseArray(resultInfo.getData(), SearchInfo.class);
                        searchAdapter = new SearchAdapter(SearchActivity.this, data);
                        lv_search_list.setAdapter(searchAdapter);

                    } else {
                        helper.ShowErrDialog("加载项目失败", resultInfo.getMessage(), SearchActivity.this);
                    }

                } catch (Exception ex) {
                    Toast.makeText(SearchActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    helper.ShowErrDialog("加载项目系统错误", ex.getMessage(), SearchActivity.this);
                }
            }

        }
    };

    private void InitItemsCombox(String flag1) {
        pd = ProgressDialog.show(SearchActivity.this, "提示", "项目加载中...");
        ItemsDBHelper db = ItemsDBHelper.getInstance(SearchActivity.this, 1);
        db.openReadLink();
        ArrayList<ItemsInfo> list = db.query("flag='" + flag1 + "'");
        pd.dismiss();
        db.closeLink();
        //------------------------------------------------------------------------------------
        for (int i = 0; i <= list.size() - 1; i++) {
            SearchInfo info = new SearchInfo();
            info.setId(list.get(i).id);
            info.setValue(list.get(i).value);
            info.setFlag(list.get(i).flag);
            info.setIdandname(list.get(i).idandname);
            data.add(info);
        }
        searchAdapter = new SearchAdapter(SearchActivity.this, data);
        lv_search_list.setAdapter(searchAdapter);

    }


}
