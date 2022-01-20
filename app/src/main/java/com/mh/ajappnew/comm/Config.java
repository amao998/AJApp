package com.mh.ajappnew.comm;

import android.os.Parcel;
import android.os.Parcelable;

public class Config implements Parcelable {
    private String ip;
    private String port;
    private String line;
    private String username;
    private String token;
    private String imei;
    private String jyy;

    private String picwidth;
    private String pichight;
    private String configjson;

    private String versioncode;
    private String appid;
    private String versionname;

    private String picmodle;
    private String picrarrate;

    public String getPicmodle() {
        return picmodle;
    }

    public void setPicmodle(String picmodle) {
        this.picmodle = picmodle;
    }

    public String getPicrarrate() {
        return picrarrate;
    }

    public void setPicrarrate(String picrarrate) {
        this.picrarrate = picrarrate;
    }


    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }


    public String getVersioncode() {
        return versioncode;
    }

    public void setVersioncode(String versioncode) {
        this.versioncode = versioncode;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }


    public String getConfigjson() {
        return configjson;
    }

    public void setConfigjson(String configjson) {
        this.configjson = configjson;
    }

    public String getPicwidth() {
        return picwidth;
    }

    public void setPicwidth(String picwidth) {
        this.picwidth = picwidth;
    }

    public String getPichight() {
        return pichight;
    }

    public void setPichight(String pichight) {
        this.pichight = pichight;
    }


    public String getJyy() {
        return jyy;
    }

    public void setJyy(String jyy) {
        this.jyy = jyy;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public Config(String ip, String port, String line, String username, String token, String imei,
                  String jyy, String picwidth, String pichight, String configjson,
                  String appid, String versioncode, String versionname,  String picrarrate,String picmodle) {
        this.ip = ip;
        this.port = port;
        this.line = line;
        this.username = username;
        this.token = token;
        this.imei = imei;
        this.jyy = jyy;

        this.picwidth = picwidth;
        this.pichight = pichight;

        this.configjson = configjson;

        this.appid = appid;
        this.versioncode = versioncode;
        this.versionname = versionname;

        this.picrarrate = picrarrate;
        this.picmodle = picmodle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //将这两条数据保存起来用于方便传递
        parcel.writeString(getIp());
        parcel.writeString(getPort());
        parcel.writeString(getLine());
        parcel.writeString(getUsername());
        parcel.writeString(getToken());
        parcel.writeString(getImei());
        parcel.writeString(getJyy());
        parcel.writeString(getPicwidth());
        parcel.writeString(getPichight());

        parcel.writeString(getConfigjson());

        parcel.writeString(getAppid());
        parcel.writeString(getVersioncode());
        parcel.writeString(getVersionname());

        parcel.writeString(getPicrarrate());
        parcel.writeString(getPicmodle());


    }

    public static final Creator<Config> CREATOR = new Creator<Config>() {
        @Override
        public Config createFromParcel(Parcel parcel) {
            return new Config(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(),
                    parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString()
                    , parcel.readString(), parcel.readString(), parcel.readString(),parcel.readString(),parcel.readString());
        }

        @Override
        public Config[] newArray(int i) {
            return new Config[i];
        }
    };
}