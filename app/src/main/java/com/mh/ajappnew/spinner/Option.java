package com.mh.ajappnew.spinner;

public class Option {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String id;
    private  String value;


    @Override
    public String toString() {
        return value;
    }

    public Option(String id, String value) {
        this.value = value;
        this.id = id;
    }
}