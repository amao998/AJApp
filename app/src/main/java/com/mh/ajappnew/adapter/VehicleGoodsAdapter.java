package com.mh.ajappnew.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mh.ajappnew.R;
import com.mh.ajappnew.comm.VehicleGoodsInfo;

import java.util.List;

public class VehicleGoodsAdapter extends BaseAdapter {

    LayoutInflater inflater;

    public List<VehicleGoodsInfo> list;
    private boolean Showprefix;
    Context context1;

    public VehicleGoodsAdapter(Context context, List<VehicleGoodsInfo> data, boolean showprefix) {
        // TODO Auto-generated constructor stub
        inflater = LayoutInflater.from(context);
        this.list = data;
        Showprefix = showprefix;
        context1=context;
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
            v = inflater.inflate(R.layout.item_vehiclegoods_list, parent, false);
            holder = new ViewHolder(v);

            class MyImageWatcher implements View.OnClickListener {
                private VehicleGoodsAdapter.ViewHolder mHolder;

                public MyImageWatcher(VehicleGoodsAdapter.ViewHolder holder) {
                    mHolder = holder;
                }

                @Override
                public void onClick(View v) {
                    String[] arr = v.getTag().toString().split(",");

                    Integer pos = Integer.valueOf(arr[1]);
                    VehicleGoodsInfo element = list.get(pos);
                    Integer js = 0;
                    if (arr[0].equals("+") == true) {
                        js = Integer.valueOf(element.getQty()) + 1;
                    } else {
                        js = Integer.valueOf(element.getQty()) - 1;
                    }
                    if (js <= 0) {
                        js = 0;
                    }
                    Toast.makeText(context1, String.valueOf(js), Toast.LENGTH_SHORT).show();
                    element.setQty(String.valueOf(js));
                    mHolder.tv_vehiclegoods_qty.setText(String.valueOf(js));
                }
            }
            holder.tv_vehiclegoods_jian.setOnClickListener(new MyImageWatcher(holder));
            holder.tv_vehiclegoods_add.setOnClickListener(new MyImageWatcher(holder));


            class MyTextWatcher implements TextWatcher {
                public MyTextWatcher(VehicleGoodsAdapter.ViewHolder holder) {
                    mHolder = holder;
                }

                private VehicleGoodsAdapter.ViewHolder mHolder;

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    int position = (Integer) mHolder.et_vehiclegoods_prefix.getTag();
                    VehicleGoodsInfo element = list.get(position);

                    String prefix = mHolder.et_vehiclegoods_prefix.getText().toString();
                    element.setPrefix(prefix);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            }
            holder.et_vehiclegoods_prefix.addTextChangedListener(new MyTextWatcher(holder));

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }


        holder.tv_vehiclegoods_jian.setTag("-," + position);
        holder.tv_vehiclegoods_add.setTag("+," + position); // I passed the current
        holder.et_vehiclegoods_prefix.setTag(position);
        // position as a tag
        holder.tv_vehiclegoods_id.setText(list.get(position).getId());
        holder.tv_vehiclegoods_tname.setText(list.get(position).getTname());
        holder.tv_vehiclegoods_qty.setText(list.get(position).getQty());
        holder.et_vehiclegoods_prefix.setText(list.get(position).getPrefix());

        if (Showprefix == true) {
            holder.et_vehiclegoods_prefix.setVisibility(View.VISIBLE);
        } else {
            holder.et_vehiclegoods_prefix.setVisibility(View.GONE);
        }

        holder.tv_vehiclegoods_unit.setText(list.get(position).getUnit());
        return v;
    }

    class ViewHolder {
        TextView tv_vehiclegoods_id = null;
        TextView tv_vehiclegoods_tname = null;
        TextView tv_vehiclegoods_qty = null;
        EditText et_vehiclegoods_prefix = null;
        TextView tv_vehiclegoods_unit = null;
        ImageView tv_vehiclegoods_jian = null;
        ImageView tv_vehiclegoods_add = null;

        ViewHolder(View v) {
            tv_vehiclegoods_id = (TextView) v.findViewById(R.id.tv_vehiclegoods_id);
            tv_vehiclegoods_tname = (TextView) v.findViewById(R.id.tv_vehiclegoods_tname);
            tv_vehiclegoods_qty = (TextView) v.findViewById(R.id.tv_vehiclegoods_qty);
            tv_vehiclegoods_unit = (TextView) v.findViewById(R.id.tv_vehiclegoods_unit);
            et_vehiclegoods_prefix = (EditText) v.findViewById(R.id.et_vehiclegoods_prefix);

            tv_vehiclegoods_jian = (ImageView) v.findViewById(R.id.tv_vehiclegoods_jian);
            tv_vehiclegoods_add = (ImageView) v.findViewById(R.id.tv_vehiclegoods_add);
        }

    }

}
