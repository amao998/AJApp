package com.mh.ajappnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mh.ajappnew.R;
import com.mh.ajappnew.jkid.GetItemListReturnInfo;

import java.util.List;

public class Dialog1Adapter extends BaseAdapter {
    LayoutInflater inflater;

    public List<GetItemListReturnInfo> list;

    public Dialog1Adapter(Context context, List<GetItemListReturnInfo> data) {
        // TODO Auto-generated constructor stub
        inflater = LayoutInflater.from(context);
        this.list = data;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        ViewHolder holder = null;

        if (v == null) {
            v = inflater.inflate(R.layout.item_dialog1_listview_sub, parent, false);
            holder = new ViewHolder(v);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.tv_dialog1_sub_id.setText(list.get(position).getId());
        holder.tv_dialog1_sub_text.setText(list.get(position).getValue()); // Set the question body

        return v;
    }

    class ViewHolder {
        TextView tv_dialog1_sub_id = null;
        TextView tv_dialog1_sub_text = null;

        ViewHolder(View v) {
            tv_dialog1_sub_id = (TextView) v.findViewById(R.id.tv_dialog1_sub_id);
            tv_dialog1_sub_text= (TextView)v.findViewById(R.id.tv_dialog1_sub_text);
        }

    }
}
