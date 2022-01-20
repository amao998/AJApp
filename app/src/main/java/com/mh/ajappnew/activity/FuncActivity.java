package com.mh.ajappnew.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.mh.ajappnew.R;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.db.ConfigDBHelper;
import com.mh.ajappnew.fragment.Fragment1;
import com.mh.ajappnew.fragment.Fragment2;
import com.mh.ajappnew.fragment.Fragment3;
import com.mh.ajappnew.fragment.Fragment4;

import java.util.ArrayList;
import java.util.List;

public class FuncActivity extends AppCompatActivity {
    private ProgressDialog pd;
    //TabLayout
    private TabLayout mTabLayout;
    //ViewPager
    private ViewPager mViewPager;
    //Title
    private List<String> mTitle;
    //Fragment
    private List<Fragment> mFragment;

    private Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_func);

        Intent i = getIntent();
        config = i.getParcelableExtra("Config");

        initData();
        initView();

    }


    //初始化View
    private void initData() {
        mTitle = new ArrayList<>();
        mFragment = new ArrayList<>();

        mTitle.add("登录车辆");
        Fragment2 f2 = new Fragment2();
        f2.setConfig(config);
        mFragment.add(f2);

        mTitle.add("外检列表");
        Fragment1 f1 = new Fragment1();
        f1.setConfig(config);
        mFragment.add(f1);


        mTitle.add("在线列表");
        Fragment3 f3 = new Fragment3();
        f3.setConfig(config);
        mFragment.add(f3);

        mTitle.add("个人中心");
        Fragment4 f4 = new Fragment4();
        f4.setConfig(config);
        mFragment.add(f4);
    }

    //初始化数据
    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.mTabLayout);
        mViewPager = (ViewPager) findViewById(R.id.mViewPager);

        //预加载
        mViewPager.setOffscreenPageLimit(mFragment.size());

        //设置设配器
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            //选中的item
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            //返回item的个数
            @Override
            public int getCount() {
                return mFragment.size();
            }

            //设置标题
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle.get(position);
            }
        });

        //绑定
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            exitTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        } else {
            mHandler.postDelayed(mFinish, 0);
        }
        //return super.onKeyDown(keyCode, event);
        return false;
    }

    private long exitTime = 0;

    private Handler mHandler = new Handler();
    private Runnable mFinish = new Runnable() {
        @Override
        public void run() {

            ConfigDBHelper db = ConfigDBHelper.getInstance(FuncActivity.this, 1);
            db.openWriteLink();
            db.updateOnlyPwd(config.getUsername(), "");
            db.closeLink();
            finish();
        }
    };



}
