package com.example.googlemlkitapp.modeldata;

import java.io.Serializable;

public class BarcodeData implements Serializable {
    private String title;
    private String content;
    private String rawvalue;
    private int valuetype;
    private String date;










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

    public int getValuetype() {
        return valuetype;
    }

    public void setValuetype(int valuetype) {
        this.valuetype = valuetype;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return "BarcodeData{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", rawvalue='" + rawvalue + '\'' +
                ", valuetype=" + valuetype +
                ", date='" + date + '\'' +
                '}';
    }
}
