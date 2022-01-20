package com.mh.ajappnew.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.mh.ajappnew.R;
import com.mh.ajappnew.comm.SearchInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ASUS on 2017/7/20.
 *
 * @author songshufan
 */

public class SearchAdapter extends BaseAdapter implements Filterable {
    private int resoureId;
    private Context context;
    public List<SearchInfo> citysBeanList; //这个数据是会改变的，所以要有个变量来备份一下原始数据
    private List<SearchInfo> backcitysBeanList; //用来备份原始数据

    MyFilter mFilter;

    public SearchAdapter(Context context, List<SearchInfo> citysBeanList) {
        this.context = context;
        this.citysBeanList = citysBeanList;
        backcitysBeanList = citysBeanList;
    }

    public SearchAdapter() {

    }

    @Override
    public int getCount() {
        return citysBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    //用来优化的viewholder内部类，优化控件findviewbyid
    class ViewHolder {
        TextView cityName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search, null);
            holder = new ViewHolder();
            holder.cityName = (TextView) convertView.findViewById(R.id.item_search_value);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.cityName.setText( citysBeanList.get(position).getIdandname());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new MyFilter();
        }
        return mFilter;
    }

    //我们需要定义一个过滤器的类来定义过滤规则
    class MyFilter extends Filter {
        //我们在performFiltering(CharSequence charSequence)这个方法中定义过滤规则
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();
            List<SearchInfo> list;
            if (TextUtils.isEmpty(charSequence)) {//当过滤的关键字为空的时候，我们则显示所有的数据
                list = backcitysBeanList;
            } else {//否则把符合条件的数据对象添加到集合中
                list = new ArrayList<>();
                for (SearchInfo citysBean : backcitysBeanList) {
                    if (citysBean.getIdandname().toUpperCase().contains(charSequence)) { //要匹配的item中的view
                        list.add(citysBean);
                    }
                }
            }
            result.values = list; //将得到的集合保存到FilterResults的value变量中
            result.count = list.size();//将集合的大小保存到FilterResults的count变量中

            return result;
        }

        //在publishResults方法中告诉适配器更新界面
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            citysBeanList = (List<SearchInfo>) filterResults.values;
            if (filterResults.count > 0) {
                notifyDataSetChanged();//通知数据发生了改变
            } else {
                notifyDataSetInvalidated();//通知数据失效
            }
        }
    }
}

