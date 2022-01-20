package com.mh.ajappnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mh.ajappnew.R;
import com.mh.ajappnew.comm.RemarkInfo;

import java.util.List;

public class RemarkAdapter extends BaseAdapter {

    LayoutInflater inflater;

    public List<RemarkInfo> list;

    public RemarkAdapter(Context context, List<RemarkInfo> data) {
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
            v = inflater.inflate(R.layout.item_remark_list, parent, false);
            holder = new ViewHolder(v);

            holder.cb_remark_goin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer i = Integer.parseInt(v.getTag().toString());
                    CheckBox cb = (CheckBox) v;
                    if (cb.isChecked() == true) {
                        list.get(i).setGoin("1");
                    } else {
                        list.get(i).setGoin("0");
                    }
                }
            });

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }


        holder.tv_remark_id.setText(list.get(position).getId());
        holder.cb_remark_goin.setChecked(list.get(position).getGoin().equals("1"));
        holder.tv_remark_tname.setText(list.get(position).getTname());
        holder.cb_remark_goin.setTag(position);

        return v;
    }

    class ViewHolder {
        TextView tv_remark_id = null;
        CheckBox cb_remark_goin = null;
        TextView tv_remark_tname = null;

        ViewHolder(View v) {
            tv_remark_id = (TextView) v.findViewById(R.id.tv_remark_id);
            cb_remark_goin = (CheckBox) v.findViewById(R.id.cb_remark_goin);
            tv_remark_tname = (TextView) v.findViewById(R.id.tv_remark_tname);

        }

    }

}
