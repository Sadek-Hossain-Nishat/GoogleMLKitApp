package com.example.googlemlkitapp.websearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.googlemlkitapp.R;

import java.util.Objects;

public class WebSearchActivity extends AppCompatActivity {


    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_search);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Web Search");

        webView=findViewById(R.id.id_websearchwithwebview);



        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setAllowFileAccess(true);

        Intent i= getIntent();
        if (i!=null){
            webView.loadUrl(i.getStringExtra("url"));
        }



    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        }
        else {
            super.onBackPressed();

        }

    }











}