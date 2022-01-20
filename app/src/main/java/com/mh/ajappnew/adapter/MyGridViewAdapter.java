package com.mh.ajappnew.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mh.ajappnew.R;
import com.mh.ajappnew.comm.PhotoInfo;

import java.util.List;

public class MyGridViewAdapter extends BaseAdapter {
    //声明引用
    private Context mContext;   //这个Context类型的变量用于第三方图片加载时用到
    private LayoutInflater mlayoutInflater;
    public boolean isrefresh = false;
    public List<PhotoInfo> list;
    public String[] paths = null;

    //创建一个构造函数
    public MyGridViewAdapter(Context context, List<PhotoInfo> data) {
        this.mContext = context;
        //利用LayoutInflate把控件所在的布局文件加载到当前类中
        mlayoutInflater = LayoutInflater.from(context);
        list = data;
        paths = new String[list.size()];
    }

    @Override
    public int getCount() {
        return list.size(); //GridView的数目总共10个
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //写一个静态的class,把layout_grid_item的控件转移过来使用
    static class ViewHolder {
        public ImageView item_photo_img;
        public TextView item_photo_id;
        public TextView item_photo_name;
        public TextView item_photo_state;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            //填写ListView的图标和标题等控件的来源，来自于layout_list_item这个布局文件
            //把控件所在的布局文件加载到当前类中
            convertView = mlayoutInflater.inflate(R.layout.item_photo, null);
            //生成一个ViewHolder的对象
            holder = new ViewHolder();
            //获取控件对象
            holder.item_photo_img = convertView.findViewById(R.id.item_photo_img);
            holder.item_photo_id = convertView.findViewById(R.id.item_photo_id);
            holder.item_photo_name = convertView.findViewById(R.id.item_photo_name);
            holder.item_photo_state = convertView.findViewById(R.id.item_photo_state);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //修改空间属性
        holder.item_photo_id.setText(list.get(position).getVisid());
        holder.item_photo_name.setText(list.get(position).getVisname());

        String state = list.get(position).getState();
        String statedesc = "";
        if (state.equals("-1") == true) {
            statedesc = "未拍照";
        } else if (state.equals("0") == true) {
            statedesc = "已拍照";
        } else if (state.equals("1") == true) {
            statedesc = "已上传";
        } else {
            statedesc = state;
        }
        holder.item_photo_state.setText(statedesc);


//        String path = list.get(position).getPath();
//        if (path.equals("") == false) {
//            File file = new File(path);
//            if(file.exists()==true) {
//                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                holder.item_photo_img.setImageBitmap(bitmap);
//            }
//        }else {
//            holder.item_photo_img.setImageResource(R.drawable.camera);
//        }

        Bitmap bitmap = list.get(position).getPic();
        if (bitmap!=null) {
            holder.item_photo_img.setImageBitmap(bitmap);
        } else {
            holder.item_photo_img.setImageResource(R.drawable.camera);
        }


        return convertView;
    }
}