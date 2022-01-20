package com.mh.ajappnew.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.mh.ajappnew.R;
import com.mh.ajappnew.comm.LTInputInfo;

import java.util.List;

public class LTIputListAdapter extends BaseAdapter {

    LayoutInflater inflater;

    public List<LTInputInfo> list;

    public LTIputListAdapter(Context context, List<LTInputInfo> data) {
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
            v = inflater.inflate(R.layout.item_ltinput_list, parent, false);
            holder = new ViewHolder(v);

            holder.et_ltinput_value.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        int i = (Integer) view.getTag();
                    }

                    return false;
                }
            });

            class MyTextWatcher implements TextWatcher {
                public MyTextWatcher(ViewHolder holder) {
                    mHolder = holder;
                }

                private ViewHolder mHolder;

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    int position = (Integer) mHolder.et_ltinput_value.getTag();
                    LTInputInfo element = list.get(position);

                    if (s != null && !"".equals(s.toString())) {
                        double stand = Double.parseDouble(list.get(position).getStand());
                        double value = Double.parseDouble(s.toString());
                        if (stand <= value) {
                            element.setPd("合格");
                        } else {
                            element.setPd("不合格");
                        }
                        element.setValue(s.toString());
                        mHolder.tv_ltinput_pd.setText(element.getPd());
                    } else {
                        element.setValue("");
                        mHolder.tv_ltinput_pd.setText("");
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
//                    if (s != null && !"".equals(s.toString())) {
//                        int position = (Integer) mHolder.et_ltinput_value.getTag();
//                        double stand = Double.parseDouble(list.get(position).getStand());
//                        double value = Double.parseDouble(s.toString());
//                        if (stand < value) {
//                            list.get(position).setPd("不合格");
//                        } else {
//                            list.get(position).setPd("合格");
//                        }
//                    }
                }
            }
            holder.et_ltinput_value.addTextChangedListener(new MyTextWatcher(holder));


            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.et_ltinput_value.setTag(new Integer(position));
        holder.tv_ltinput_id.setTag(new Integer(position)); // I passed the current
        // position as a tag
        holder.tv_ltinput_id.setText(list.get(position).getId());
        holder.tv_ltinput_pd.setText(list.get(position).getPd());
        holder.tv_ltinput_stand.setText(list.get(position).getStand());
        holder.et_ltinput_value.setText(list.get(position).getValue());

        return v;
    }

    class ViewHolder {
        TextView tv_ltinput_id = null;
        TextView tv_ltinput_pd = null;
        TextView tv_ltinput_stand = null;
        EditText et_ltinput_value = null;


        ViewHolder(View v) {
            tv_ltinput_id = (TextView) v.findViewById(R.id.tv_ltinput_id);
            tv_ltinput_pd = (TextView) v.findViewById(R.id.tv_ltinput_pd);
            tv_ltinput_stand = (TextView) v.findViewById(R.id.tv_ltinput_stand);
            et_ltinput_value = (EditText) v.findViewById(R.id.et_ltinput_value);
        }

    }

}
