package com.example.googlemlkitapp.modeldata;

public class BarcodeData {
    private String title;
    private String content;
    private String rawvalue;
    private String valuetype;


    public BarcodeData(String title, String content, String rawvalue, String valuetype) {
        this.title = title;
        this.content = content;
        this.rawvalue = rawvalue;
        this.valuetype = valuetype;
    }




    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRawvalue() {
        return rawvalue;
    }

    public void setRawvalue(String rawvalue) {
        this.rawvalue = rawvalue;
    }

    public String getValuetype() {
        return valuetype;
    }

    public void setValuetype(String valuetype) {
        this.valuetype = valuetype;
    }







}
