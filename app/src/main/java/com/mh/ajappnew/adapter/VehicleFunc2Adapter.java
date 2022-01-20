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
import com.mh.ajappnew.comm.QVehicleInfo;

import java.util.List;

@SuppressLint("DefaultLocale")
public class VehicleFunc2Adapter extends BaseAdapter implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    public Context mContext; // 声明一个上下文对象
    public List<QVehicleInfo> mPlanetList; // 声明一个行星信息队列

    // 行星适配器的构造函数，传入上下文与行星队列
    public VehicleFunc2Adapter(Context context, List<QVehicleInfo> planet_list) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_func2_list, null);
            holder.tv_item2_hphmmac = convertView.findViewById(R.id.tv_item2_hphmmac);
            holder.tv_item2_cllx = convertView.findViewById(R.id.tv_item2_cllx);
            holder.tv_item2_detectid = convertView.findViewById(R.id.tv_item2_detectid);
            holder.tv_item2_cp = convertView.findViewById(R.id.tv_item2_cp);
            holder.tv_item2_dlsj = convertView.findViewById(R.id.tv_item2_dlsj);
            holder.tv_item2_ztname = convertView.findViewById(R.id.tv_item2_ztname);
            holder.tv_item2_ywlx = convertView.findViewById(R.id.tv_item2_ywlx);

            holder.lay2 = convertView.findViewById(R.id.lay2);
            holder.lay2.bringToFront();

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
        QVehicleInfo planet = mPlanetList.get(position);
        holder.tv_item2_hphmmac.setText(planet.getHphmmac()); // 显示行星的名称
        holder.tv_item2_cllx.setText(planet.getCllx());
        holder.tv_item2_detectid.setText(planet.getJylsh());
        holder.tv_item2_cp.setText(planet.getCp());
        holder.tv_item2_dlsj.setText(planet.getDlsj());
        holder.tv_item2_ztname.setText(planet.getZtname());
        holder.tv_item2_ywlx.setText(planet.getYwlx());


        if (planet.getZt().equals("1") == true) {
            holder.tv_item2_ztname.setTextColor(Color.rgb(80, 199, 103));
        } else if (planet.getZt().equals("2") == true) {
            holder.tv_item2_ztname.setTextColor(Color.RED);
        }else if (planet.getZt().equals("0") == true) {
            holder.tv_item2_ztname.setTextColor(Color.BLACK);
        }

        return convertView;
    }

    // 定义一个视图持有者，以便重用列表项的视图资源
    public final class ViewHolder {
        public TextView tv_item2_hphmmac; // 声明行星名称的文本视图对象
        public TextView tv_item2_cllx; // 声明行星描述的文本视图对象
        public TextView tv_item2_detectid;
        public TextView tv_item2_cp;
        public TextView tv_item2_dlsj;
        public TextView tv_item2_ztname;
        public RelativeLayout lay2 = null;
        public TextView tv_item2_ywlx;

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
