package com.example.googlemlkitapp.barcodescanning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IntermediumBarcodeActivity extends AppCompatActivity {


    private static final String TAG = "save";
    public TextView barcodeResulttitle, barcodeResultcontent;
    private Button scanbarcodeBtn, actionBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;
    private Button popupBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermedium_barcode);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();


        barcodeResulttitle = findViewById(R.id.id_barcoderesulttitle);
        barcodeResultcontent = findViewById(R.id.id_barcoderesultcontent);
        scanbarcodeBtn = findViewById(R.id.id_scanbarcodebtn);
        actionBtn = findViewById(R.id.id_buttonaction);
        popupBtn = findViewById(R.id.id_popupbtn);


        popupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PopupMenu popup = new PopupMenu(IntermediumBarcodeActivity.this, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.popup_menu, popup.getMenu());
                if (popup.getMenu() instanceof MenuBuilder) {
                    MenuBuilder m = (MenuBuilder) popup.getMenu();
                    //noinspection RestrictedApi
                    m.setOptionalIconsVisible(true);
                }

                popup.show();


            }
        });


        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAction();
            }


        });


        Intent i = getIntent();


        if (i != null) {
            if (i.getStringExtra("uniqid").equals("barcodeml")) {


                String titleBarcode = i.getStringExtra("title");
                String contentBarcode = i.getStringExtra("content");
                String rawvalueBarcode = i.getStringExtra("rawvalue");
                int valuetypeBarcode = i.getIntExtra("valuetype", 0);
                ArrayList<String> rawvaluesbeforescanning = new ArrayList<>();

                firestore.collection("barcodescanning/" + currentUser.getUid() + "/barcodedata")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot documnet : task.getResult()) {

                                        String rawvaluebeforscan = documnet.getString("rawvalue");
                                        rawvaluesbeforescanning.add(rawvaluebeforscan);

                                    }

                                    if (rawvaluesbeforescanning.contains(rawvalueBarcode)) {


                                        showWarningDialog(rawvalueBarcode);

                                    } else {
                                        gotoFunctions(titleBarcode, contentBarcode, rawvalueBarcode, valuetypeBarcode);

                                    }


                                } else {

                                }
                            }
                        });
            }


        }


        scanbarcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntermediumBarcodeActivity.this, BarcodeScannerMLActivity.class));
            }
        });


    }

    private void gotoFunctions(String titleBarcode, String contentBarcode, String rawvalueBarcode, int valuetypeBarcode) {
        Log.i(TAG, "gotoFunctions: +more");


        savetoDatabase(titleBarcode, contentBarcode, rawvalueBarcode, valuetypeBarcode);


        barcodeResulttitle.setText(titleBarcode);
        barcodeResultcontent.setText(contentBarcode);

        switch (valuetypeBarcode) {
            case Barcode.TYPE_URL:
            case Barcode.TYPE_EMAIL:
                popupBtn.setVisibility(View.VISIBLE);
                actionBtn.setVisibility(View.VISIBLE);

                break;
            case Barcode.TYPE_TEXT:
            case Barcode.TYPE_UNKNOWN:
                popupBtn.setVisibility(View.VISIBLE);
                actionBtn.setVisibility(View.GONE);

                break;

        }


    }


    private void showWarningDialog(String rawvalueBarcode) {


        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("You scanned it before\nPlease,Search following result in History List\n" + rawvalueBarcode)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .create()
                .show();


    }


    private void savetoDatabase(String titleBarcode, String contentBarcode, String rawvalueBarcode, int valuetypeBarcode) {
        if (rawvalueBarcode != null) {


            Map<String, Object> barcodeData = new HashMap<>();
            barcodeData.put("title", titleBarcode);
            barcodeData.put("content", contentBarcode);
            barcodeData.put("rawvalue", rawvalueBarcode);
            barcodeData.put("valuetype", valuetypeBarcode);


            firestore.collection("barcodescanning/" + currentUser.getUid() + "/barcodedata").add(barcodeData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.i(TAG, "onSuccess: saved");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "onFailure: failed");

                        }
                    });


        }


    }


    private void goAction() {


        Intent i = getIntent();
        if (i != null) {


            int valuetype = i.getIntExtra("valuetype", 0);
            String content = i.getStringExtra("content");
            switch (valuetype) {
                case Barcode.TYPE_URL:
                    goToWeb(content);

                    break;
                case Barcode.TYPE_EMAIL:
                    goToGmail(content);
                    break;


            }


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

        Intent i = new Intent(IntermediumBarcodeActivity.this, WebSearchActivity.class);
        i.putExtra("url", fetcurl);
        startActivity(i);


    }


}