package com.mh.ajappnew.comm;

import android.graphics.Bitmap;

public class PhotoInfo {
    private String detectid;
    private String visid;
    private String visname;
    private String state;
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    private Bitmap pic;




    public String getDetectid() {
        return detectid;
    }

    public void setDetectid(String detectid) {
        this.detectid = detectid;
    }

    public String getVisid() {
        return visid;
    }

    public void setVisid(String visid) {
        this.visid = visid;
    }

    public String getVisname() {
        return visname;
    }

    public void setVisname(String visname) {
        this.visname = visname;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
