package com.example.googlemlkitapp.barcodescanning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemlkitapp.R;
import com.example.googlemlkitapp.customadapter.BarcodeHistoryAdapter;
import com.example.googlemlkitapp.modeldata.BarcodeData;
import com.example.googlemlkitapp.websearch.WebSearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.HashMap;
import java.util.Map;

public class DetailsBarcodeActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView barcodedetails,favouritetext,deletetext;
    private ImageView open,share,copy,delete,favourite;
    private BarcodeData barcodeData;
    private String documnetpathRefdata="";
    private String documentpathfavouriteRef="";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;
    private String rawvalue="";
    private LinearLayout actionlayout;
    private String title="";
    private String content="";
    private String date="";
    private int valuetype=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_barcode);
        getSupportActionBar().setTitle("Details Barcode Information");
        firebaseAuth=FirebaseAuth.getInstance();
        currentUser =firebaseAuth.getCurrentUser();
        firestore= FirebaseFirestore.getInstance();
        actionlayout=findViewById(R.id.id_actionlayout);
        favouritetext=findViewById(R.id.id_textfavourite);
        deletetext=findViewById(R.id.id_deleteitem);

        barcodedetails=findViewById(R.id.id_barcodedetailsresult);
        open=findViewById(R.id.id_open);
        share=findViewById(R.id.id_share);
        copy=findViewById(R.id.id_copy);
        delete=findViewById(R.id.id_delete);
        favourite=findViewById(R.id.id_favourite);








        open.setOnClickListener(this);
        share.setOnClickListener(this);
        copy.setOnClickListener(this);
        delete.setOnClickListener(this);
        favourite.setOnClickListener(this);

        Intent i=getIntent();
        barcodeData= (BarcodeData) i.getSerializableExtra("barcode");
        rawvalue=barcodeData.getRawvalue();
        title=barcodeData.getTitle();
        content=barcodeData.getContent();
        valuetype=barcodeData.getValuetype();
        date=barcodeData.getDate();

        barcodedetails.setText(barcodeData.toString());

        doinBackgroundTask();



    }

    private void doinBackgroundTask() {

        firestore.collection("barcodescanning/" + currentUser.getUid() + "/barcodedata")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot documnet : task.getResult()) {

                               if (rawvalue.equals(documnet.getString("rawvalue"))){
                                   documnetpathRefdata=documnet.getId();
                                   break;
                               }




                            }

                            checkingFavourite();


                        } else {

                        }
                    }
                });




    }

    private void checkingFavourite() {



        firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot documnet : task.getResult()) {
                                if (rawvalue.equals(documnet.getString("rawvalue"))){
                                    documentpathfavouriteRef=documnet.getId();
                                    break;
                                }

                            }


                            if (!documnetpathRefdata.isEmpty()&&!documentpathfavouriteRef.isEmpty()){
                                actionlayout.setVisibility(View.VISIBLE);


                            }
                            else {
                                if (documentpathfavouriteRef.isEmpty()){
                                    actionlayout.setVisibility(View.VISIBLE);
                                    favourite.setImageResource(R.drawable.ic_addtofavourite);
                                    favouritetext.setText("Add to Favourite");

                                }
                                if (documnetpathRefdata.isEmpty()){

                                    actionlayout.setVisibility(View.VISIBLE);
                                    delete.setVisibility(View.GONE);
                                    deletetext.setVisibility(View.GONE);

                                }
                            }



                        } else {

                        }
                    }
                });



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
            case R.id.id_favourite:
                if (favouritetext.getText().equals("Remove Favourite")) removeFavourite();
                else addtoFavourite();
                break;
            case R.id.id_delete:
                removeItem();
                break;
        }



    }

    private void removeItem() {

        firestore.collection("barcodescanning/" + currentUser.getUid() + "/barcodedata").document(documnetpathRefdata).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {



                        Toast.makeText(DetailsBarcodeActivity.this, "Data has been removed", Toast.LENGTH_SHORT).show();

                        barcodedetails.setVisibility(View.GONE);
                        actionlayout.setVisibility(View.GONE);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });



    }

    private void addtoFavourite() {

        Map<String, Object> barcodeData = new HashMap<>();
        barcodeData.put("title",title );
        barcodeData.put("content",content);
        barcodeData.put("rawvalue", rawvalue);
        barcodeData.put("valuetype", valuetype);
        barcodeData.put("date",date);


        firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata").add(barcodeData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Toast.makeText(DetailsBarcodeActivity.this, "Added to Favourite", Toast.LENGTH_SHORT).show();
                        actionlayout.setVisibility(View.VISIBLE);
                        favourite.setImageResource(R.drawable.ic_favourite);
                        favouritetext.setText("Remove Favourite");


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });



    }

    private void removeFavourite() {

        firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata").document(documentpathfavouriteRef).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(DetailsBarcodeActivity.this, "Favourite Item has been removed", Toast.LENGTH_SHORT).show();
                        if (documnetpathRefdata.isEmpty()){
                            barcodedetails.setVisibility(View.GONE);
                            actionlayout.setVisibility(View.GONE);
                        }
                        else {
                            actionlayout.setVisibility(View.VISIBLE);
                            favourite.setImageResource(R.drawable.ic_addtofavourite);
                            favouritetext.setText("Add to Favourite");


                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });



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