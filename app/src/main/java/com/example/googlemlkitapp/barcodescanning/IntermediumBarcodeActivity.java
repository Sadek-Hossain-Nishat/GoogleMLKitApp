package com.example.googlemlkitapp.barcodescanning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemlkitapp.R;
import com.example.googlemlkitapp.customadapter.BarcodeHistoryAdapter;
import com.example.googlemlkitapp.modeldata.BarcodeData;
import com.example.googlemlkitapp.searchitem.SearchActivity;
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
    private String rawvalueBarcode = "";
    String titleBarcode = "";
    String contentBarcode = "";
    int valuetypeBarcode = 0;
    String documentrefpath = "";
    boolean statefavouritebarcode = false;


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


                firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    ArrayList<String> rawvalues = new ArrayList<>();
                                    ArrayList<String> documentrefpathlist = new ArrayList<>();

                                    for (QueryDocumentSnapshot documnet : task.getResult()) {

                                        rawvalues.add(documnet.getString("rawvalue"));
                                        documentrefpathlist.add(documnet.getId());

                                        Log.i(TAG, "onComplete: " + documnet.getString("rawvalue"));


                                    }


                                    if (rawvalues.contains(rawvalueBarcode)) {
                                        documentrefpath = documentrefpathlist.get(rawvalues.indexOf(rawvalueBarcode));
                                        PopupMenu popup = new PopupMenu(IntermediumBarcodeActivity.this, v);
                                        MenuInflater inflater = popup.getMenuInflater();
                                        inflater.inflate(R.menu.popup_menu2, popup.getMenu());

                                        if (popup.getMenu() instanceof MenuBuilder) {
                                            MenuBuilder m = (MenuBuilder) popup.getMenu();
                                            //noinspection RestrictedApi
                                            m.setOptionalIconsVisible(true);
                                        }


                                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem item) {
                                                switch (item.getItemId()) {
                                                    case R.id.id_share:
                                                        sharebarResult();
                                                        break;
                                                    case R.id.id_copy:
                                                        copybarResult();
                                                        break;
                                                    case R.id.id_removefavourite:
                                                        item.setTitle("Add to Favourite");
                                                        item.setIcon(R.drawable.ic_addtofavourite);

                                                        removeFavourite();
                                                        break;
                                                    case R.id.id_deletebarresult:
                                                        deletebarResult();
                                                        break;
                                                }
                                                return false;
                                            }
                                        });

                                        popup.show();


                                    } else {


                                        PopupMenu popup = new PopupMenu(IntermediumBarcodeActivity.this, v);
                                        MenuInflater inflater = popup.getMenuInflater();
                                        inflater.inflate(R.menu.popup_menu, popup.getMenu());
                                        if (popup.getMenu() instanceof MenuBuilder) {
                                            MenuBuilder m = (MenuBuilder) popup.getMenu();
                                            //noinspection RestrictedApi
                                            m.setOptionalIconsVisible(true);
                                        }


                                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem item) {
                                                switch (item.getItemId()) {
                                                    case R.id.id_share:
                                                        sharebarResult();
                                                        break;
                                                    case R.id.id_copy:
                                                        copybarResult();
                                                        break;
                                                    case R.id.id_addtofavourite:
                                                        item.setIcon(R.drawable.ic_favourite);
                                                        item.setTitle("Remove Favourite");

                                                        addtoFavouritebaraResult();

                                                        break;
                                                    case R.id.id_deletebarresult:
                                                        deletebarResult();
                                                        break;
                                                }
                                                return false;
                                            }
                                        });


                                        popup.show();


                                    }


                                } else {

                                }
                            }
                        });


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


                titleBarcode = i.getStringExtra("title");
                contentBarcode = i.getStringExtra("content");
                rawvalueBarcode = i.getStringExtra("rawvalue");

                valuetypeBarcode = i.getIntExtra("valuetype", 0);
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

    private void removeFavourite() {

        Map<String, Object> barcodeData = new HashMap<>();
        barcodeData.put("title", titleBarcode);
        barcodeData.put("content", contentBarcode);
        barcodeData.put("rawvalue", rawvalueBarcode);
        barcodeData.put("valuetype", valuetypeBarcode);


        firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata").document(documentrefpath).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(IntermediumBarcodeActivity.this, "Favourite Item has been removed", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }


    private void deletebarResult() {


        if (rawvalueBarcode != null) {


            Map<String, Object> barcodeData = new HashMap<>();
            barcodeData.put("title", titleBarcode);
            barcodeData.put("content", contentBarcode);
            barcodeData.put("rawvalue", rawvalueBarcode);
            barcodeData.put("valuetype", valuetypeBarcode);


            firestore.collection("barcodescanning/" + currentUser.getUid() + "/barcodedata").document(documentrefpath).delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(IntermediumBarcodeActivity.this, "Data has been removed", Toast.LENGTH_SHORT).show();
                            barcodeResulttitle.setVisibility(View.GONE);
                            barcodeResultcontent.setVisibility(View.GONE);
                            actionBtn.setVisibility(View.GONE);
                            popupBtn.setVisibility(View.GONE);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


        }


    }

    private void addtoFavouritebaraResult() {


        if (rawvalueBarcode != null) {


            Map<String, Object> barcodeData = new HashMap<>();
            barcodeData.put("title", titleBarcode);
            barcodeData.put("content", contentBarcode);
            barcodeData.put("rawvalue", rawvalueBarcode);
            barcodeData.put("valuetype", valuetypeBarcode);


            firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata").add(barcodeData)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.i(TAG, "onSuccess: saved");
                            Toast.makeText(IntermediumBarcodeActivity.this, "Added to Favourite", Toast.LENGTH_SHORT).show();


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

    private void copybarResult() {

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copy text", rawvalueBarcode);
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show();


    }

    private void sharebarResult() {


        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_SUBJECT, "share this");
        intent.putExtra(Intent.EXTRA_TEXT, rawvalueBarcode);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Share"));
        } else {
            Toast.makeText(this, "No apps are available", Toast.LENGTH_SHORT).show();
        }


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
                        Intent i=new Intent(IntermediumBarcodeActivity.this, SearchActivity.class);
                        i.putExtra("accept", "history");
                        startActivity(i);

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
                            documentrefpath = documentReference.getId();

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