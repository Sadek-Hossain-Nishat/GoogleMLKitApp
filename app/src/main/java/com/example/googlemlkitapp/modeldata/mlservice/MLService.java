package com.example.googlemlkitapp.modeldata.mlservice;

public class MLService {

    private int mlservicelogoid;
    private String mlservicetitle;

    public MLService(int mlservicelogoid, String mlservicetitle) {
        this.mlservicelogoid = mlservicelogoid;
        this.mlservicetitle = mlservicetitle;
    }

    public int getMlservicelogoid() {
        return mlservicelogoid;
    }

    public String getMlservicetitle() {
        return mlservicetitle;
    }
}
