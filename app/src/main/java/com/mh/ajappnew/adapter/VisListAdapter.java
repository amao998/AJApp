package com.mh.ajappnew.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.comm.VisInfo;
import com.mh.ajappnew.comm.VisSubInfo;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.GetVisSubListInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisListAdapter extends BaseAdapter {

    LayoutInflater inflater;
    Context context1;
    Dialog2Adapter dialog2Adapter;
    private ProgressDialog pd;
    Config config;
    private int pos;
    String detectid;
    String jylsh;
    String jycs;

    public List<VisInfo> list;

    public VisListAdapter(Context context, List<VisInfo> data, Config config,String detectid,String jylsh,String jycs) {
        // TODO Auto-generated constructor stub
        inflater = LayoutInflater.from(context);
        this.list = data;
        context1 = context;
        this.config = config;

        this.detectid=detectid;
        this.jylsh=jylsh;
        this.jycs=jycs;

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
    public View getView(int position, final View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        ViewHolder holder = null;

        if (v == null) {
            v = inflater.inflate(R.layout.item_vis_list, parent, false);
            holder = new ViewHolder(v);
            v.setTag(holder);

            class MyTextWatcher implements View.OnClickListener {
                public MyTextWatcher(ViewHolder holder) {
                    mHolder = holder;
                }

                private ViewHolder mHolder;

                @Override
                public void onClick(View view) {
                    Integer i = Integer.parseInt(view.getTag().toString());
                    pos = i;
                    String pd = list.get(i).getResult().toString();
                    list.get(pos).setResultsub("");
                    if (pd.equals("1") == true) {
                        mHolder.img_visitem_resultnow.setImageResource(R.drawable.nopass);
                        list.get(i).setResult("0");
                        LoadData(list.get(i).getVisid());
                    } else if (pd.equals("0") == true) {
                        mHolder.img_visitem_resultnow.setImageResource(R.drawable.nodone);
                        list.get(i).setResult("2");
                    } else if (pd.equals("2") == true) {
                        mHolder.img_visitem_resultnow.setImageResource(R.drawable.pass1);
                        list.get(i).setResult("1");
                        //Intent intent = new Intent(view.getContext(), VehicleActivity.class);
                        //view.getContext().startActivity(intent);
                    }
                }
            }
            holder.img_visitem_resultnow.setOnClickListener(new MyTextWatcher(holder));
            holder.lay_visitem_item.setOnClickListener(new MyTextWatcher(holder));

        } else {
            holder = (ViewHolder) v.getTag();
        }
        holder.tv_visitem_id.setText(list.get(position).getVisid());
        holder.tv_visitem_name.setText(list.get(position).getVisname()); // Set the question body


        if (list.get(position).getResult().equals("0")) {
            holder.img_visitem_resultnow.setImageResource(R.drawable.nopass);
        } else if (list.get(position).getResult().equals("1")) {
            holder.img_visitem_resultnow.setImageResource(R.drawable.pass1);
        } else if (list.get(position).getResult().equals("2")) {
            holder.img_visitem_resultnow.setImageResource(R.drawable.nodone);
        }
        holder.lay_visitem_item.setTag(position);
        holder.img_visitem_resultnow.setTag(position);

        if (list.get(position).getResultlast().equals("0")) {
            holder.img_visitem_resultlast.setImageResource(R.drawable.nopass);
        } else if (list.get(position).getResultlast().equals("1")) {
            holder.img_visitem_resultlast.setImageResource(R.drawable.pass1);
        } else if (list.get(position).getResultlast().equals("2")) {
            holder.img_visitem_resultlast.setImageResource(R.drawable.nodone);
        } else if (list.get(position).getResultlast().equals("9")) {
            holder.img_visitem_resultlast.setImageDrawable(null);
        }

        return v;
    }

    class ViewHolder {
        TextView tv_visitem_id = null;
        TextView tv_visitem_name = null;
        ImageView img_visitem_resultnow = null;
        ImageView img_visitem_resultlast = null;
        LinearLayout lay_visitem_item = null;


        ViewHolder(View v) {
            tv_visitem_id = (TextView) v.findViewById(R.id.tv_visitem_id);
            tv_visitem_name = (TextView) v.findViewById(R.id.tv_visitem_name);
            img_visitem_resultnow = (ImageView) v.findViewById(R.id.img_visitem_resultnow);
            img_visitem_resultlast = (ImageView) v.findViewById(R.id.img_visitem_resultlast);
            lay_visitem_item = (LinearLayout) v.findViewById(R.id.lay_visitem_item);


        }

    }


    private void LoadData(final String typeid) {
        pd = ProgressDialog.show(context1, "提示", "查询中...");
        pd.setCancelable(true);

        GetVisSubListInfo info = new GetVisSubListInfo();
        info.setVisid(typeid);
        info.setDetectid(this.detectid);
        info.setJylsh(this.jylsh);
        info.setJycs(this.jycs);

        String data = JSON.toJSONString(info);

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handler;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetVisSubList");
        maps.put("data", data);
        postRequestUtil.Run(maps);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {

            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        //1.加载数据
                        List<VisSubInfo> infos = JSON.parseArray(resultInfo.getData(), VisSubInfo.class);
                        ShowDialogList(infos);

                    } else {

                    }
                } catch (Exception ex) {

                }
            }
        }
    };


    private void ShowDialogList(final List<VisSubInfo> listsub) {


        final View view = View.inflate(context1, R.layout.item_dialog1_listview, null);
        final ListView lv = (ListView) view.findViewById(R.id.lv_dialog1_listview);
        dialog2Adapter = new Dialog2Adapter(context1, listsub);
        lv.setAdapter(dialog2Adapter);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context1);
        builder.setTitle("选择不合格项目：")
                .setView(view).setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
            }

        });

        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = "";
                for (int i = 0; i <= dialog2Adapter.list.size() - 1; i++) {
                    if (dialog2Adapter.list.get(i).getResult().equals("1")) {
                        value += dialog2Adapter.list.get(i).getVisid() + ",";
                    }
                }
                list.get(pos).setResultsub(value);
                //dialog.dismiss();
                Toast.makeText(context1, String.valueOf(pos) + "|" + value, Toast.LENGTH_LONG).show();
            }
        });

        builder.show();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(context1, String.valueOf(position), Toast.LENGTH_LONG).show();
                if (dialog2Adapter.list.get(position).getResult().equals("1") == true) {
                    dialog2Adapter.list.get(position).setResult("0");
                } else {
                    dialog2Adapter.list.get(position).setResult("1");
                }
                dialog2Adapter.notifyDataSetChanged();
            }
        });
    }


}
