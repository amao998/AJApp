package com.mh.ajappnew.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mh.ajappnew.R;
import com.mh.ajappnew.jkid.GetVisInsTaskReturnInfo;

import java.util.List;

@SuppressLint("DefaultLocale")
public class VehicleFunc1Adapter extends BaseAdapter implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    public Context mContext; // 声明一个上下文对象
    public List<GetVisInsTaskReturnInfo> mPlanetList; // 声明一个行星信息队列

    // 行星适配器的构造函数，传入上下文与行星队列
    public VehicleFunc1Adapter(Context context, List<GetVisInsTaskReturnInfo> planet_list) {
        mContext = context;
        mPlanetList = planet_list;
    }

    // 获取列表项的个数
    public int getCount() {
        return mPlanetList.size();
    }

    // 获取列表项的数据
    public Object getItem(int arg0) {
        return mPlanetList.get(arg0);
    }

    // 获取列表项的编号
    public long getItemId(int arg0) {
        return arg0;
    }

    // 获取指定位置的列表项视图
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) { // 转换视图为空
            holder = new ViewHolder(); // 创建一个新的视图持有者
            // 根据布局文件item_list.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_func1_list, null);
            holder.tv_item1_hphmmac = convertView.findViewById(R.id.tv_item1_hphmmac);
            holder.tv_item1_cllx = convertView.findViewById(R.id.tv_item1_cllx);
            holder.tv_item1_detectid = convertView.findViewById(R.id.tv_item1_detectid);
            holder.tv_item1_jycs = convertView.findViewById(R.id.tv_item1_jycs);
            holder.tv_item1_jylsh = convertView.findViewById(R.id.tv_item1_jylsh);
            holder.tv_item1_dlsj = convertView.findViewById(R.id.tv_item1_dlsj);

            holder.tv_item1_wg = convertView.findViewById(R.id.tv_item1_wg);
            holder.tv_item1_dp = convertView.findViewById(R.id.tv_item1_dp);
            holder.tv_item1_dt = convertView.findViewById(R.id.tv_item1_dt);
            holder.tv_item1_lwcx = convertView.findViewById(R.id.tv_item1_lwcx);
            holder.tv_item1_clwyx = convertView.findViewById(R.id.tv_item1_clwyx);
            holder.tv_item1_pz = convertView.findViewById(R.id.tv_item1_pz);

            holder.tv_item1_ywlx = convertView.findViewById(R.id.tv_item1_ywlx);

            holder.lay1 = convertView.findViewById(R.id.lay1);
            holder.lay1.bringToFront();

//            holder.img_item_car.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(view.getContext(), VisActivity.class);
//                    //加入参数，传递给AnotherActivity
//                    intent.putExtra("caotype", "add");
//                    intent.putExtra("detectid", "");
//                    intent.putExtra("zt", "");
//                   view.getContext().startActivity(intent);
//                }
//            });


            // 将视图持有者保存到转换视图当中
            convertView.setTag(holder);
        } else { // 转换视图非空
            // 从转换视图中获取之前保存的视图持有者
            holder = (ViewHolder) convertView.getTag();
        }
        GetVisInsTaskReturnInfo planet = mPlanetList.get(position);
        holder.tv_item1_hphmmac.setText(planet.getHphm()); // 显示行星的名称
        holder.tv_item1_cllx.setText(planet.getCllx());
        holder.tv_item1_jylsh.setText(planet.getJylsh());
        holder.tv_item1_detectid.setText(planet.getDetectid());
        holder.tv_item1_jycs.setText(planet.getJycs());
        holder.tv_item1_dlsj.setText(planet.getPssj());
        holder.tv_item1_ywlx.setText(planet.getYwlx());

        if (planet.getLwcx().equals("1") == true) {
            holder.tv_item1_lwcx.setTextColor(Color.rgb(51, 153, 255));
            holder.tv_item1_lwcx.setText("联网查询√");
        } else {
            holder.tv_item1_lwcx.setTextColor(Color.BLACK);
            holder.tv_item1_lwcx.setText("联网查询");
        }

        if (planet.getClwyx().equals("1") == true) {
            holder.tv_item1_clwyx.setTextColor(Color.rgb(51, 153, 255));
            holder.tv_item1_clwyx.setText("唯一检验√");
        } else {
            holder.tv_item1_clwyx.setTextColor(Color.BLACK);
            holder.tv_item1_clwyx.setText("唯一检验");
        }

        if (planet.getWg().equals("1") == true) {
            holder.tv_item1_wg.setTextColor(Color.rgb(51, 153, 255));
            holder.tv_item1_wg.setText("外观检验√");
        } else {
            holder.tv_item1_wg.setTextColor(Color.BLACK);
            holder.tv_item1_wg.setText("外观检验");
        }

        holder.tv_item1_dp.setVisibility(View.VISIBLE);
        if (planet.getDp().equals("1") == true) {
            holder.tv_item1_dp.setTextColor(Color.rgb(51, 153, 255));
            holder.tv_item1_dp.setText("底盘检验√");
        } else if (planet.getDp().equals("0") == true) {
            holder.tv_item1_dp.setTextColor(Color.BLACK);
            holder.tv_item1_dp.setText("底盘检验");
        } else {
            holder.tv_item1_dp.setVisibility(View.GONE);
        }

        holder.tv_item1_dt.setVisibility(View.VISIBLE);
        if (planet.getDt().equals("1") == true) {
            holder.tv_item1_dt.setTextColor(Color.rgb(51, 153, 255));
            holder.tv_item1_dt.setText("动态检验√");
        } else if (planet.getDt().equals("0") == true) {
            holder.tv_item1_dt.setTextColor(Color.BLACK);
            holder.tv_item1_dt.setText("动态检验");
        } else {
            holder.tv_item1_dt.setVisibility(View.GONE);
        }

        holder.tv_item1_pz.setText(planet.getPz());
        if (planet.getPz().equals("已拍") == true) {
            holder.tv_item1_pz.setTextColor(Color.rgb(51, 153, 255));
        }else {
            holder.tv_item1_pz.setTextColor(Color.BLACK);
        }

        return convertView;
    }

    // 定义一个视图持有者，以便重用列表项的视图资源
    public final class ViewHolder {
        public TextView tv_item1_hphmmac; // 声明行星名称的文本视图对象
        public TextView tv_item1_cllx; // 声明行星描述的文本视图对象
        public TextView tv_item1_detectid;
        public TextView tv_item1_jycs;
        public TextView tv_item1_dlsj;


        public TextView tv_item1_wg;
        public TextView tv_item1_dp;
        public TextView tv_item1_dt;

        public TextView tv_item1_lwcx = null;
        public TextView tv_item1_clwyx;

        public RelativeLayout lay1 = null;
        public TextView tv_item1_pz;
        public TextView tv_item1_jylsh;

        public TextView tv_item1_ywlx;

    }

    //处理列表项的点击事件，由接口OnItemClickListener触发
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String desc = String.format("您点击了第%d个行星，它的名字是%s", position + 1,
                mPlanetList.get(position).getJylsh());
        Toast.makeText(mContext, desc, Toast.LENGTH_LONG).show();
    }

    // 处理列表项的长按事件，由接口OnItemLongClickListener触发
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String desc = String.format("您长按了第%d个行星，它的名字是%s", position + 1,
                mPlanetList.get(position).getJylsh());
        Toast.makeText(mContext, desc, Toast.LENGTH_LONG).show();
        return true;
    }
}
