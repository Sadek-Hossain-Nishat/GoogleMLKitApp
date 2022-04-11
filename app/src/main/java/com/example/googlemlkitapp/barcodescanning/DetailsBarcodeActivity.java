package com.example.googlemlkitapp.barcodescanning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemlkitapp.R;
import com.example.googlemlkitapp.modeldata.BarcodeData;
import com.example.googlemlkitapp.websearch.WebSearchActivity;
import com.google.mlkit.vision.barcode.common.Barcode;

public class DetailsBarcodeActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView barcodedetails;
    private ImageView open,share,copy,delete,favourite;
    private BarcodeData barcodeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_barcode);
        getSupportActionBar().setTitle("Details Barcode Information");
        barcodedetails=findViewById(R.id.id_barcodedetailsresult);
        open=findViewById(R.id.id_open);
        share=findViewById(R.id.id_share);
        copy=findViewById(R.id.id_copy);
        delete=findViewById(R.id.id_delete);
        favourite=findViewById(R.id.id_favourite);

        open.setOnClickListener(this);
        share.setOnClickListener(this);
        copy.setOnClickListener(this);

        Intent i=getIntent();
        barcodeData= (BarcodeData) i.getSerializableExtra("barcode");
        barcodedetails.setText(barcodeData.toString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_open:
                openData();
                break;
            case R.id.id_share:
                shareData();
                break;
            case R.id.id_copy:
                copyData();
                break;
        }



    }

    private void shareData() {


        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_SUBJECT, "share this");
        intent.putExtra(Intent.EXTRA_TEXT, barcodeData.getRawvalue());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Share"));
        } else {
            Toast.makeText(this, "No apps are available", Toast.LENGTH_SHORT).show();
        }


    }

    private void openData() {

        int valuetype = barcodeData.getValuetype();
        String content = barcodeData.getContent();
        switch (valuetype) {
            case Barcode.TYPE_URL:
                goToWeb(content);

                break;
            case Barcode.TYPE_EMAIL:
                goToGmail(content);
                break;
            default:
                Toast.makeText(DetailsBarcodeActivity.this, "No Funcgion is Available for it", Toast.LENGTH_SHORT).show();


        }


    }


    private void goToGmail(String emailAddress) {

        String mailTo = "mailto:" + emailAddress + "?&subject=" + Uri.encode("Email");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mailTo)); // only email apps should handle this

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No apps are available", Toast.LENGTH_SHORT).show();
        }


    }

    private void goToWeb(String uri) {
        String fetcurl = "";

        if (uri.startsWith("https://") || uri.startsWith("http://")) fetcurl = uri;
        else {
            if (uri.contains("www")) {
                fetcurl = "https://" + uri;
            } else {
                if (Patterns.DOMAIN_NAME.matcher(uri).matches()) {
                    String[] httpscont = new String[]{
                            "https://",
                            "https://www.",
                            "http://",
                            "http://www.",

                    };
                    for (int i = 0; i < httpscont.length; i++) {
                        String testUrl = httpscont[i] + uri;
                        if (URLUtil.isNetworkUrl(testUrl)) {
                            fetcurl = testUrl;
                            break;
                        }
                    }
                }
            }
        }

        Intent i = new Intent(this, WebSearchActivity.class);
        i.putExtra("url", fetcurl);
        startActivity(i);


    }

    private void copyData(){


        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copy text", barcodeData.getRawvalue());
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();



    }







}