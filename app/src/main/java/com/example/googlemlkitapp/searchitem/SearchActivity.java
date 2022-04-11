package com.example.googlemlkitapp.searchitem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.example.googlemlkitapp.R;
import com.example.googlemlkitapp.barcodescanning.DetailsBarcodeActivity;
import com.example.googlemlkitapp.customadapter.BarcodeHistoryAdapter;
import com.example.googlemlkitapp.customadapter.FilterAdapter;
import com.example.googlemlkitapp.customlistener.HistoryitemPopUpListener;
import com.example.googlemlkitapp.customlistener.ItemListener;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<BarcodeData> barcodeDatalist;
    private FilterAdapter adapter;
    private String documentpathRef="";
    private ArrayList<String> documentPathlist;
    ArrayList<String> documentpathlist;

    private ItemListener itemListener=new ItemListener() {
        @Override
        public void clickItem(int position) {
            Intent idetails=new Intent(SearchActivity.this, DetailsBarcodeActivity.class);
            idetails.putExtra("barcode", (Serializable) barcodeDatalist.get(position));
            startActivity(idetails);


        }
    };
    private HistoryitemPopUpListener historyitemPopUpListener = new HistoryitemPopUpListener() {
        @Override
        public void actionpopupinHistoryitem(int position, View view) {

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




                                }


                                if (rawvalues.contains(barcodeDatalist.get(position).getRawvalue())) {
                                    documentpathRef = documentrefpathlist.get(rawvalues.indexOf(barcodeDatalist.get(position).getRawvalue()));
                                    PopupMenu popup = new PopupMenu(SearchActivity.this, view);
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
                                                case R.id.id_openhistoryitem:
                                                    openItem(position);
                                                    break;
                                                case R.id.id_share:
                                                    sharebarResult(position);
                                                    break;
                                                case R.id.id_copy:
                                                    copybarResult(position);
                                                    break;
                                                case R.id.id_removefavourite:
                                                    item.setTitle("Add to Favourite");
                                                    item.setIcon(R.drawable.ic_addtofavourite);

                                                    removeFavourite();
                                                    break;
                                                case R.id.id_deletebarresult:
                                                    deletebarResult(position);
                                                    break;
                                            }
                                            return false;
                                        }
                                    });

                                    popup.show();


                                } else {


                                    PopupMenu popup = new PopupMenu(SearchActivity.this, view);
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
                                                case R.id.id_openhistoryitem:
                                                    openItem(position);
                                                    break;

                                                case R.id.id_share:
                                                    sharebarResult(position);
                                                    break;
                                                case R.id.id_copy:
                                                    copybarResult(position);
                                                    break;
                                                case R.id.id_addtofavourite:
                                                    item.setIcon(R.drawable.ic_favourite);
                                                    item.setTitle("Remove Favourite");

                                                    addtoFavouritebaraResult(position);

                                                    break;
                                                case R.id.id_deletebarresult:
                                                    deletebarResult(position);
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
    };


    private HistoryitemPopUpListener favouriteitemPopUpListener = new HistoryitemPopUpListener() {
        @Override
        public void actionpopupinHistoryitem(int position, View view) {


            PopupMenu popup = new PopupMenu(SearchActivity.this, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.favourite_pop_up, popup.getMenu());
            if (popup.getMenu() instanceof MenuBuilder) {
                MenuBuilder m = (MenuBuilder) popup.getMenu();
                //noinspection RestrictedApi
                m.setOptionalIconsVisible(true);
            }
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {

                        case R.id.id_favouriteitem:
                            removefromFavourite(position);
                            break;
                        case R.id.id_openfavouriteitem:
                            openItem(position);
                            break;

                        case R.id.id_share:
                            sharebarResult(position);
                            break;
                        case R.id.id_copy:
                            copybarResult(position);
                            break;
                    }
                    return false;
                }
            });

            popup.show();


        }
    };




    private void removefromFavourite(int position) {


        Map<String, Object> barcodeData = new HashMap<>();
        barcodeData.put("title", barcodeDatalist.get(position).getTitle());
        barcodeData.put("content", barcodeDatalist.get(position).getContent());
        barcodeData.put("rawvalue", barcodeDatalist.get(position).getRawvalue());
        barcodeData.put("valuetype", barcodeDatalist.get(position).getValuetype());
        barcodeData.put("date",barcodeDatalist.get(position).getDate());


        firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata").document(documentpathlist.get(position)).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SearchActivity.this, "Favourite Item has been removed", Toast.LENGTH_SHORT).show();
                        updatingUi2();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }


    private void updatingUi2() {


        firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            barcodeDatalist.clear();
                            documentpathlist.clear();
                            for (QueryDocumentSnapshot documnet : task.getResult()) {

                                BarcodeData barcodeData = documnet.toObject(BarcodeData.class);

                                barcodeDatalist.add(barcodeData);
                                documentpathlist.add(documnet.getId());
                            }





                                adapter = new FilterAdapter(SearchActivity.this, barcodeDatalist, favouriteitemPopUpListener,itemListener);
                                layoutManager = new LinearLayoutManager(SearchActivity.this);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();




                        } else {

                        }
                    }
                });


    }











    private void addtoFavouritebaraResult(int position) {


        Map<String, Object> barcodeData = new HashMap<>();
        barcodeData.put("title", barcodeDatalist.get(position).getTitle());
        barcodeData.put("content", barcodeDatalist.get(position).getContent());
        barcodeData.put("rawvalue", barcodeDatalist.get(position).getRawvalue());
        barcodeData.put("valuetype", barcodeDatalist.get(position).getValuetype());
        barcodeData.put("date",barcodeDatalist.get(position).getDate());


        firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata").add(barcodeData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        Toast.makeText(SearchActivity.this, "Added to Favourite", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });


    }

    private void copybarResult(int position) {


        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copy text", barcodeDatalist.get(position).getRawvalue());
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(SearchActivity.this, "Copied", Toast.LENGTH_SHORT).show();


    }

    private void sharebarResult(int position) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_SUBJECT, "share this");
        intent.putExtra(Intent.EXTRA_TEXT, barcodeDatalist.get(position).getRawvalue());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Share"));
        } else {
            Toast.makeText(SearchActivity.this, "No apps are available", Toast.LENGTH_SHORT).show();
        }


    }













    private void removeFavourite() {




        firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata").document(documentpathRef).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SearchActivity.this, "Favourite Item has been removed", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });




    }


    private void openItem(int position) {


        int valuetype = barcodeDatalist.get(position).getValuetype();
        String content = barcodeDatalist.get(position).getContent();
        switch (valuetype) {
            case Barcode.TYPE_URL:
                goToWeb(content);

                break;
            case Barcode.TYPE_EMAIL:
                goToGmail(content);
                break;
            default:
                Toast.makeText(SearchActivity.this, "No Funcgion is Available for it", Toast.LENGTH_SHORT).show();


        }


    }

    private void goToGmail(String emailAddress) {

        String mailTo = "mailto:" + emailAddress + "?&subject=" + Uri.encode("Email");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mailTo)); // only email apps should handle this

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(SearchActivity.this, "No apps are available", Toast.LENGTH_SHORT).show();
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

        Intent i = new Intent(SearchActivity.this, WebSearchActivity.class);
        i.putExtra("url", fetcurl);
        startActivity(i);


    }


    private void deletebarResult(int position) {


        Map<String, Object> barcodeData = new HashMap<>();
        barcodeData.put("title", barcodeDatalist.get(position).getTitle());
        barcodeData.put("content", barcodeDatalist.get(position).getContent());
        barcodeData.put("rawvalue", barcodeDatalist.get(position).getRawvalue());
        barcodeData.put("valuetype", barcodeDatalist.get(position).getValuetype());
        barcodeData.put("date",barcodeDatalist.get(position).getDate());


        firestore.collection("barcodescanning/" + currentUser.getUid() + "/barcodedata").document(documentPathlist.get(position)).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {



                        Toast.makeText(SearchActivity.this, "Data has been removed", Toast.LENGTH_SHORT).show();


                        updatingUi();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    private void updatingUi() {


        firestore.collection("barcodescanning/" + currentUser.getUid() + "/barcodedata")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            barcodeDatalist.clear();
                            documentPathlist.clear();
                            for (QueryDocumentSnapshot documnet : task.getResult()) {

                                BarcodeData barcodeData = documnet.toObject(BarcodeData.class);

                                barcodeDatalist.add(barcodeData);
                                documentPathlist.add(documnet.getId());


                            }

                            if (barcodeDatalist.isEmpty()) {



                            } else {




                                adapter = new FilterAdapter(SearchActivity.this, barcodeDatalist, historyitemPopUpListener,itemListener);
                                layoutManager = new LinearLayoutManager(SearchActivity.this);
                                recyclerView.setLayoutManager(layoutManager);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();


                            }


                        } else {

                        }
                    }
                });


    }








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setTitle("Search");
        searchView=findViewById(R.id.id_searchview);
        recyclerView=findViewById(R.id.id_recyclerviewinsearchview);
        barcodeDatalist=new ArrayList<>();
        documentPathlist=new ArrayList<>();
        documentpathlist=new ArrayList<>();

        firebaseAuth=FirebaseAuth.getInstance();
        currentUser=firebaseAuth.getCurrentUser();
        firestore=FirebaseFirestore.getInstance();

        searchView.requestFocus();
        Intent i=getIntent();

        if (i!=null){
            if (i.getStringExtra("accept").equals("history")){
                fromHistory();

            }
            else {
                fromFavourite();

            }


        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);


                return false;
            }
        });


    }

    private void fromFavourite() {

//        firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            barcodeDatalist.clear();
//
//                            for (QueryDocumentSnapshot documnet : task.getResult()) {
//
//                                BarcodeData barcodeData = documnet.toObject(BarcodeData.class);
//
//                                barcodeDatalist.add(barcodeData);
//
//
//
//                            }
//                            layoutManager=new LinearLayoutManager(SearchActivity.this);
//                            adapter=new FilterAdapter(SearchActivity.this,barcodeDatalist,favouriteitemPopUpListener);
//                            recyclerView.setLayoutManager(layoutManager);
//                            recyclerView.setAdapter(adapter);
//
//
//
//
//                        } else {
//
//                        }
//                    }
//                });
//
//


        updatingUi2();






















    }

    private void fromHistory() {
//        firestore.collection("barcodescanning/" + currentUser.getUid() + "/barcodedata")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            barcodeDatalist.clear();
//
//                            for (QueryDocumentSnapshot documnet : task.getResult()) {
//
//                                BarcodeData barcodeData = documnet.toObject(BarcodeData.class);
//
//                                barcodeDatalist.add(barcodeData);
//
//
//
//                            }
//                            layoutManager=new LinearLayoutManager(SearchActivity.this);
//                            adapter=new FilterAdapter(SearchActivity.this,barcodeDatalist,historyitemPopUpListener);
//                            recyclerView.setLayoutManager(layoutManager);
//                            recyclerView.setAdapter(adapter);
//
//
//
//
//                        } else {
//
//                        }
//                    }
//                });
//
//
//
//
//
//
//
//


        updatingUi();










    }
}