package com.mh.ajappnew.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.mh.ajappnew.R;
import com.mh.ajappnew.adapter.MyGridViewAdapter;
import com.mh.ajappnew.bottompopfragmentmenu.BottomMenuFragment;
import com.mh.ajappnew.bottompopfragmentmenu.MenuItem;
import com.mh.ajappnew.bottompopfragmentmenu.MenuItemOnClickListener;
import com.mh.ajappnew.comm.Config;
import com.mh.ajappnew.comm.PhotoInfo;
import com.mh.ajappnew.comm.ResultInfo;
import com.mh.ajappnew.http.PostRequestUtil;
import com.mh.ajappnew.jkid.GetVisPhotoInfo;
import com.mh.ajappnew.jkid.GetVisPhotoReturnInfo;
import com.mh.ajappnew.jkid.GetVisPhotoTaskInfo;
import com.mh.ajappnew.jkid.GetVisPhotoTaskReturnInfo;
import com.mh.ajappnew.jkid.SaveVisPhotoInfo;
import com.mh.ajappnew.jkid.SaveVisPhotoReturnInfo;
import com.mh.ajappnew.tools.PicUtils;
import com.mh.ajappnew.tools.URLEncodeing;
import com.mh.ajappnew.tools.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class VisPhotoActivity extends AppCompatActivity {

    //声明引用
    private GridView gv_photo_pic;
    private Uri imageUri;
    private File outputImage;
    private String detectid, hphmmac, jylsh, jycs, ywlx, hphmcaption;
    private MyGridViewAdapter adapter;
    private List<PhotoInfo> photolists = new ArrayList<PhotoInfo>();

    private TextView tv_photo_hphm;
    private TextView tv_photo_jylsh, tv_photo_jycs, tv_photo_space;
    private ProgressDialog pd;
    private Button but_photo_exit, but_photo_exitandsave;

    private Config config;
    private Integer nowpos = 0;
    private BottomMenuFragment bottomMenuFragment;
    private String zt = "";

    public Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vis_photo);
        //----------------------------------------------------
        Intent i = getIntent();
        config = i.getParcelableExtra("Config");
        hphmmac = i.getStringExtra("hphmmac");
        detectid = i.getStringExtra("detectid");
        jycs = i.getStringExtra("jycs");
        jylsh = i.getStringExtra("jylsh");
        ywlx = i.getStringExtra("ywlx");
        zt = i.getStringExtra("zt");
        hphmcaption = i.getStringExtra("hphmcaption");

        tv_photo_jylsh = (TextView) findViewById(R.id.tv_photo_jylsh);
        tv_photo_jycs = (TextView) findViewById(R.id.tv_photo_jycs);
        tv_photo_hphm = (TextView) findViewById(R.id.tv_photo_hphm);
        tv_photo_jylsh.setText(jylsh);
        tv_photo_hphm.setText(hphmcaption);


        gv_photo_pic = (GridView) findViewById(R.id.gv_photo_pic);
        //为GridView设置监听器
        gv_photo_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //设置一个弹窗Toast,显示被短点击的条目
                //判断版本号
                //File outputImage = new File(getExternalCacheDir(), "temp_original.jpg");
                outputImage = new File(Environment.getExternalStorageDirectory(), "pictureCallback1024.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                if (Build.VERSION.SDK_INT < 24) {
                    imageUri = Uri.fromFile(outputImage);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                } else {
                    imageUri = FileProvider.getUriForFile(VisPhotoActivity.this, "com.mh.ajapp.fileprovider", outputImage);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                }

                startActivityForResult(intent, 100 + position);
            }
        });

        gv_photo_pic.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                nowpos = position;
                bottomMenuFragment.show(getFragmentManager(), "BottomMenuFragment");
                return true;
            }
        });

        but_photo_exit = (Button) findViewById(R.id.but_photo_exit);
        but_photo_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        but_photo_exitandsave = (Button) findViewById(R.id.but_photo_exitandsave);
        but_photo_exitandsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(44, intent);
                finish();
            }
        });

        tv_photo_space = (TextView) findViewById(R.id.tv_photo_space);

        if (zt.equals("1") == false) {
            but_photo_exitandsave.setVisibility(View.GONE);
            tv_photo_space.setVisibility(View.GONE);
        }

        GetVisPhotoTask();

        InItBottomMenuFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int pos = requestCode;

        Toast.makeText(VisPhotoActivity.this, String.valueOf(resultCode), Toast.LENGTH_LONG).show();

        if (resultCode == 0) {
            return;
        }
        if (resultCode == 1000) {
            String zpzl = data.getStringExtra("zpzl");
            QueryVisPhoto(detectid, zpzl);
            return;
        }

        try {

            //pd = ProgressDialog.show(VisPhotoActivity.this, "提示", "上传中...");
            //pd.setCancelable(true);

            if (requestCode >= 100 && requestCode < 200) {
                pos = requestCode - 100;
            } else {
                pos = requestCode - 200;
                imageUri = data.getData();
            }

            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            File f = new File(getExternalCacheDir(), "temp_compress" + String.valueOf(pos) + ".jpg");
            int w = Integer.valueOf(config.getPicwidth());
            int h = Integer.valueOf(config.getPichight());
            int rate = Integer.valueOf(config.getPicrarrate());

            //2021-11-17 按后台设置
            //2021-12-10 近比例
            String remark = adapter.list.get(pos).getRemark();
            String[] arr = remark.split(",");
            if (arr.length >= 2) {
                int w1 = Integer.valueOf(arr[0]);
                int h1 = Integer.valueOf(arr[1]);

                if (w1 == 0 && h1 != 0) {
                    double rate1 = (double) bitmap.getHeight() / h1;
                    w = (int) ((double)bitmap.getWidth() / rate1);
                    h = h1;
                } else if (w1 != 0 && h1 == 0) {
                    double rate2 = (double) bitmap.getWidth() / w1;
                    w = w1;
                    h = (int)((double)bitmap.getHeight() / rate2);
                } else {
                    w = w1;
                    h = h1;
                }

                Toast.makeText(VisPhotoActivity.this, remark + " " + w + "," + h, Toast.LENGTH_LONG).show();
            }


            PicUtils.compressBitmapToFile1(bitmap, w, h, rate, f);


//                File f = new Compressor(this)
//                        .setMaxWidth(w)
//                        .setMaxHeight(h)
//                        .setQuality(100)
//                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                        .compressToFile(outputImage);

//                Bitmap bitmap=BitmapFactory.decodeFile(f1.getPath());
//                File f = new File(getExternalCacheDir(), "temp_compress" + String.valueOf(pos) + ".jpg");
//                int w = Integer.valueOf(config.getPicwidth());
//                int h = Integer.valueOf(config.getPichight());
//
//                PicUtils.compressBitmapToFile1(bitmap, w, h, 100, f);


            if (f.exists() == true) {
                String zpzl = adapter.list.get(pos).getVisid();
                if (config.getPicmodle().equals("1")) {
                    String base64 = PicUtils.fileToBase64(f);
                    SaveVisPhoto(base64, zpzl);
                } else {
                    SaveVisPhotoOkHttp(zpzl, f);
                }
            } else {
                if (pd != null) {
                    pd.dismiss();
                }
            }
        } catch (Exception ex) {
            if (pd != null) {
                pd.dismiss();
            }
        }
    }

    /**
     * 使用Compressor IO模式自定义压缩
     *
     * @param path .setMaxWidth(640).setMaxHeight(480)这两个数值越高，压缩力度越小，图片也不清晰
     * .setQuality(75)这个方法只是设置图片质量，并不影响压缩图片的大小KB
     * .setCompressFormat(Bitmap.CompressFormat.WEBP) WEBP图片格式是Google推出的 压缩强，质量 高，但是IOS不识别，需要把图片转为字节流然后转PNG格式
     * .setCompressFormat(Bitmap.CompressFormat.PNG)PNG格式的压缩，会导致图片变大，并耗过大的内 存，手机反应缓慢
     * .setCompressFormat(Bitmap.CompressFormat.JPEG)JPEG压缩；压缩速度比PNG快，质量一般，基本上属于1/10的压缩比例
     */
//    private void initCompressorIO(String path) {
//        try {
//            File file = new Compressor(this)
//                    .setMaxWidth(640)
//                    .setMaxHeight(480)
//                    .setQuality(75)
//                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                    .compressToFile(new File(path));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    Handler handlertest = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();
            if (msg.what == 1) {

                ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                SaveVisPhotoReturnInfo info = JSON.parseObject(resultInfo.getData(), SaveVisPhotoReturnInfo.class);
                ShowPhoto(info.getZpzl(), info.getBase64());

                Toast.makeText(VisPhotoActivity.this, "图片:" + info.getZpzl() + ",保存成功,okhttp3", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(VisPhotoActivity.this, "失败:" + msg.obj.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };

    public void SaveVisPhotoOkHttp(String zpzl, File file) {
        String url = "http://" + config.getIp() + ":" + config.getPort() + "/ashx/PdaMacFuncPic.ashx";
        pd.setCancelable(true);

        SaveVisPhotoInfo info = new SaveVisPhotoInfo();
        info.setDetectid(detectid);
        info.setJylsh(jylsh);
        info.setJycs(jycs);
        info.setBase64("");
        info.setZpzl(zpzl);
        info.setFlag("1");
        info.setYwlx(ywlx);
        info.setHphm(hphmmac);

        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(VisPhotoActivity.this, "提示", "上传中...");


        OkHttpClient client = new OkHttpClient();
// form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            String filename = file.getName();
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("file", filename, body)
                    .addFormDataPart("jylsh", jylsh)
                    .addFormDataPart("zpzl", zpzl)
                    .addFormDataPart("data", data)
                    .addFormDataPart("Method", "SaveVisPhoto");
        }

        Request request = new Request.Builder().url(url).post(requestBody.build()).build();
        // readTimeout("请求超时时间" , 时间单位);
        client.newBuilder().readTimeout(5000, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String string = e.getMessage();
                Message message = new Message();
                message.what = 0;
                message.obj = string;
                handlertest.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Message message = new Message();
                message.what = 1;
                message.obj = string;
                handlertest.sendMessage(message);
            }
        });
    }


    private void SaveVisPhoto(String base64, String zpzl) {

        //pd = ProgressDialog.show(VisPhotoActivity.this, "提示", "上传中...");
        pd.setCancelable(true);

        SaveVisPhotoInfo info = new SaveVisPhotoInfo();
        info.setDetectid(detectid);
        info.setJylsh(jylsh);
        info.setJycs(jycs);
        info.setBase64(base64);
        info.setZpzl(zpzl);
        info.setFlag("1");
        info.setYwlx(ywlx);
        info.setHphm(hphmmac);

        String data = JSON.toJSONString(info);

        pd = ProgressDialog.show(VisPhotoActivity.this, "提示", "上传中...");

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handlerphoto;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "SaveVisPhoto");
        maps.put("data", data);
        postRequestUtil.Run(maps);
    }

    Handler handlerphoto = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("图片上传网络错误", msg.obj.toString(), VisPhotoActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {


                        SaveVisPhotoReturnInfo info = JSON.parseObject(resultInfo.getData(), SaveVisPhotoReturnInfo.class);
                        ShowPhoto(info.getZpzl(), info.getBase64());
                        //helper.ShowErrDialog("提示", "图片:" + info.getZpzl() + ",上传成功", VisPhotoActivity.this);

                        Toast.makeText(VisPhotoActivity.this, "图片:" + info.getZpzl() + ",保存成功", Toast.LENGTH_LONG).show();

                    } else {
                        helper.ShowErrDialog("图片上传失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), VisPhotoActivity.this);
                    }
                } catch (Exception ex) {
                    helper.ShowErrDialog("图片上传系统错误", ex.getMessage(), VisPhotoActivity.this);
                }
            }
        }
    };


    private void ShowPhoto(String zpzl, String base64) {
        Integer pos = 0;
        for (int i = 0; i <= photolists.size() - 1; i++) {
            if (zpzl.equals(photolists.get(i).getVisid())) {
                pos = i;
                continue;
            }
        }

        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        photolists.get(pos).setPic(decodedByte);

        adapter.notifyDataSetChanged();


    }


    private void InItBottomMenuFragment() {
        bottomMenuFragment = new BottomMenuFragment();

        List<MenuItem> menuItemList = new ArrayList<MenuItem>();

        MenuItem menuItem1 = new MenuItem();
        menuItem1.setText("来源相册");
        menuItem1.setItem_name("");
        menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);
        menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                //Toast.makeText(VisPhotoActivity.this, "来源相册", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 200 + nowpos);
            }
        });

        MenuItem menuItem2 = new MenuItem();
        menuItem2.setText("查看照片");
        menuItem2.setItem_name("");
        menuItem2.setStyle(MenuItem.MenuItemStyle.COMMON);
        menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                //Toast.makeText(VisPhotoActivity.this, "查看照片", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(VisPhotoActivity.this, VisPhotoViewActivity.class);
                //加入参数，传递给AnotherActivity
                intent.putExtra("Config", config);
                intent.putExtra("zpzl", photolists.get(nowpos).getVisid());
                intent.putExtra("zpzlname", photolists.get(nowpos).getVisname());
                intent.putExtra("jylsh", jylsh);
                intent.putExtra("detectid", detectid);
                intent.putExtra("jycs", jycs);

                startActivity(intent);
                //finish();
            }
        });

        MenuItem menuItem3 = new MenuItem();
        menuItem3.setText("录像抓拍");
        menuItem3.setItem_name("");
        menuItem3.setStyle(MenuItem.MenuItemStyle.COMMON);
        menuItem3.setMenuItemOnClickListener(new MenuItemOnClickListener(bottomMenuFragment, menuItem3) {
            @Override
            public void onClickMenuItem(View v, MenuItem menuItem) {
                //Toast.makeText(VisPhotoActivity.this, "查看照片", Toast.LENGTH_LONG).show();
                Intent intent3 = new Intent(VisPhotoActivity.this, VisPhotoNVRActivity.class);
                //加入参数，传递给AnotherActivity
                intent3.putExtra("Config", config);
                intent3.putExtra("zpzl", photolists.get(nowpos).getVisid());
                intent3.putExtra("zpzlname", photolists.get(nowpos).getVisname());
                intent3.putExtra("jylsh", jylsh);
                intent3.putExtra("detectid", detectid);
                intent3.putExtra("jycs", jycs);

                startActivityForResult(intent3, 1000);
            }
        });


        menuItemList.add(menuItem1);
        menuItemList.add(menuItem2);
        menuItemList.add(menuItem3);

        bottomMenuFragment.setMenuItems(menuItemList);

    }

    private void GetVisPhotoTask() {

        pd = ProgressDialog.show(VisPhotoActivity.this, "提示", "查询中...");
        pd.setCancelable(true);

        GetVisPhotoTaskInfo info = new GetVisPhotoTaskInfo();
        info.setDetectid(detectid);
        info.setJylsh(jylsh);
        info.setJycs(jycs);
        info.setDevice_group("WG");

        String data = JSON.toJSONString(info);

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handlerphototask;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetVisPhotoTask");
        maps.put("data", data);
        postRequestUtil.Run(maps);
    }

    Handler handlerphototask = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("图片任务查询网络错误", msg.obj.toString(), VisPhotoActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        List<GetVisPhotoTaskReturnInfo> infoA = JSON.parseArray(resultInfo.getData(), GetVisPhotoTaskReturnInfo.class);

                        photolists = new ArrayList<>();

                        for (int i = 0; i <= infoA.size() - 1; i++) {
                            PhotoInfo info = new PhotoInfo();
                            info.setVisid(infoA.get(i).getVisid());
                            info.setVisname(infoA.get(i).getVisname());
                            info.setState(infoA.get(i).getZt());
                            info.setRemark(infoA.get(i).getRemark());
                            info.setPic(null);
                            photolists.add(info);
                        }


                        adapter = new MyGridViewAdapter(VisPhotoActivity.this, photolists);
                        adapter.list = photolists;
                        gv_photo_pic.setAdapter(adapter);

                        ShowAllPic();

                    } else {
                        helper.ShowErrDialog("图片任务查询失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), VisPhotoActivity.this);
                    }
                } catch (Exception ex) {
                    helper.ShowErrDialog("图片任务查询系统错误", ex.getMessage(), VisPhotoActivity.this);
                }
            }
        }
    };


    private void ShowAllPic() {
        for (int i = 0; i <= photolists.size() - 1; i++) {
            if (photolists.get(i).getState().equals("-1") == false) {
                QueryVisPhoto(detectid, photolists.get(i).getVisid());
            }
        }
    }

    private void QueryVisPhoto(String detectid, String zpzl) {

        GetVisPhotoInfo info = new GetVisPhotoInfo();
        info.setDetectid(detectid);
        info.setZpzl(zpzl);
        info.setJylsh(jylsh);
        info.setJycs(jycs);
        info.setFlag("1");

        String data = JSON.toJSONString(info);

        PostRequestUtil postRequestUtil = new PostRequestUtil(config);
        postRequestUtil.handler = handlerviewphoto;
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("jkid", "GetVisPhoto");
        maps.put("data", data);
        postRequestUtil.Run(maps);
    }

    Handler handlerviewphoto = new Handler() {
        @Override
        public void handleMessage(Message msg) {// handler接收到消息后就会执行此方法
            pd.dismiss();// 关闭ProgressDialog
            if (msg.what == 0) {
                helper.ShowErrDialog("图片下载网络错误", msg.obj.toString(), VisPhotoActivity.this);
            } else {
                try {
                    ResultInfo resultInfo = JSON.parseObject(msg.obj.toString(), ResultInfo.class);
                    if (resultInfo.getCode() == 1) {
                        GetVisPhotoReturnInfo infoA = JSON.parseObject(resultInfo.getData(), GetVisPhotoReturnInfo.class);
                        ShowPhoto(infoA.getZpzl(), infoA.getBase64());

                    } else {
                        // helper.ShowErrDialog("图片上传失败", URLEncodeing.toURLDecoder(resultInfo.getMessage()), VisActivity.this);
                    }
                } catch (Exception ex) {
                    helper.ShowErrDialog("图片下载系统错误", ex.getMessage(), VisPhotoActivity.this);
                }
            }
        }
    };


}
