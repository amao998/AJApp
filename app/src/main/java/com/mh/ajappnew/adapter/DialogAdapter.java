package com.mh.ajappnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mh.ajappnew.R;
import com.mh.ajappnew.jkid.GetVehicleJcdlReturnInfo;

import java.util.List;

public class DialogAdapter extends BaseAdapter {
    LayoutInflater inflater;

    public List<GetVehicleJcdlReturnInfo> list;

    public DialogAdapter(Context context, List<GetVehicleJcdlReturnInfo> data) {
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
            v = inflater.inflate(R.layout.item_dialog_listview_sub, parent, false);
            holder = new ViewHolder(v);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.tv_dialog_sub_hphm.setText(list.get(position).getHphm());
        holder.tv_dialog_sub_clsbdh.setText(list.get(position).getClsbdh()); // Set the question body
        holder.tv_dialog_sub_dlsj.setText(list.get(position).getDlsj());
        holder.tv_dialog_sub_hphmmac.setText(list.get(position).getHphmmac());

        return v;
    }

    class ViewHolder {
        TextView tv_dialog_sub_hphm = null;
        TextView tv_dialog_sub_clsbdh = null;
        TextView tv_dialog_sub_dlsj=null;
        TextView tv_dialog_sub_hphmmac=null;

        ViewHolder(View v) {
            tv_dialog_sub_hphm = (TextView) v.findViewById(R.id.tv_dialog_sub_hphm);
            tv_dialog_sub_clsbdh= (TextView)v.findViewById(R.id.tv_dialog_sub_clsbdh);
            tv_dialog_sub_dlsj= (TextView)v.findViewById(R.id.tv_dialog_sub_dlsj);
            tv_dialog_sub_hphmmac= (TextView)v.findViewById(R.id.tv_dialog_sub_hphmmac);
        }

    }
}
