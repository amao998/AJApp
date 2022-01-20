package com.mh.ajappnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mh.ajappnew.R;
import com.mh.ajappnew.comm.VisSubInfo;

import java.util.List;

public class Dialog2Adapter extends BaseAdapter {
    LayoutInflater inflater;

    public List<VisSubInfo> list;

    public Dialog2Adapter(Context context, List<VisSubInfo> data) {
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
            v = inflater.inflate(R.layout.item_dialog2_listview_sub, parent, false);
            holder = new ViewHolder(v);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.tv_dialog2_sub_id.setText(list.get(position).getVisid());
        holder.tv_dialog2_sub_text.setText(list.get(position).getVisname()); // Set the question body
        holder.cb_dialog2_sub_check.setTag(position);

        holder.cb_dialog2_sub_check.setChecked(list.get(position).getResult().equals("1"));

//        holder.cb_dialog2_sub_check.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              String aa= String.valueOf(  v.getTag());
//                Toast.makeText(v.getContext(),aa,Toast.LENGTH_LONG).show();
//            }
//        });

        return v;
    }

    class ViewHolder {
        TextView tv_dialog2_sub_id = null;
        TextView tv_dialog2_sub_text = null;
        CheckBox cb_dialog2_sub_check=null;

        ViewHolder(View v) {
            tv_dialog2_sub_id = (TextView) v.findViewById(R.id.tv_dialog2_sub_id);
            tv_dialog2_sub_text= (TextView)v.findViewById(R.id.tv_dialog2_sub_text);
            cb_dialog2_sub_check= (CheckBox)v.findViewById(R.id.cb_dialog2_sub_check);
        }

    }
}
