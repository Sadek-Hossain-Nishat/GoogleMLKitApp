package com.example.googlemlkitapp.fragments;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemlkitapp.R;
import com.example.googlemlkitapp.barcodescanning.IntermediumBarcodeActivity;
import com.example.googlemlkitapp.customadapter.BarcodeHistoryAdapter;
import com.example.googlemlkitapp.customlistener.HistoryitemPopUpListener;
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


public class HistoryFragment extends Fragment {
    private static final String TAG = "show";
    private RecyclerView historyitemRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BarcodeHistoryAdapter barcodeHistoryAdapter;
    private ArrayList<BarcodeData> barcodeDatalist;
    private ArrayList<String> documentPathlist;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;
    private TextView showemptylist, titleHistoryFragment;
    private Button clearhistoryBtn;
    private ImageView searchimage;
    private String documentpathRef="";



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

                                    Log.i(TAG, "onComplete: " + documnet.getString("rawvalue"));


                                }


                                if (rawvalues.contains(barcodeDatalist.get(position).getRawvalue())) {
                                    documentpathRef = documentrefpathlist.get(rawvalues.indexOf(barcodeDatalist.get(position).getRawvalue()));
                                    PopupMenu popup = new PopupMenu(requireActivity(), view);
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


                                    PopupMenu popup = new PopupMenu(requireActivity(), view);
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

    private void removeFavourite() {




        firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata").document(documentpathRef).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(requireActivity(), "Favourite Item has been removed", Toast.LENGTH_SHORT).show();


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
                Toast.makeText(requireActivity(), "No Funcgion is Available for it", Toast.LENGTH_SHORT).show();


        }


    }


    private void goToGmail(String emailAddress) {

        String mailTo = "mailto:" + emailAddress + "?&subject=" + Uri.encode("Email");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mailTo)); // only email apps should handle this

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(requireActivity(), "No apps are available", Toast.LENGTH_SHORT).show();
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

        Intent i = new Intent(requireActivity(), WebSearchActivity.class);
        i.putExtra("url", fetcurl);
        startActivity(i);


    }


    private void deletebarResult(int position) {


        Map<String, Object> barcodeData = new HashMap<>();
        barcodeData.put("title", barcodeDatalist.get(position).getTitle());
        barcodeData.put("content", barcodeDatalist.get(position).getContent());
        barcodeData.put("rawvalue", barcodeDatalist.get(position).getRawvalue());
        barcodeData.put("valuetype", barcodeDatalist.get(position).getValuetype());


        firestore.collection("barcodescanning/" + currentUser.getUid() + "/barcodedata").document(documentPathlist.get(position)).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {



                        Toast.makeText(requireActivity(), "Data has been removed", Toast.LENGTH_SHORT).show();


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
                                Log.i(TAG, "onComplete: " + barcodeData.getRawvalue());
                                barcodeDatalist.add(barcodeData);
                                documentPathlist.add(documnet.getId());


                            }

                            if (barcodeDatalist.isEmpty()) {

                                showemptylist.setVisibility(View.VISIBLE);
                                titleHistoryFragment.setVisibility(View.GONE);
                                clearhistoryBtn.setVisibility(View.GONE);
                                searchimage.setVisibility(View.GONE);

                            } else {

                                showemptylist.setVisibility(View.GONE);
                                titleHistoryFragment.setVisibility(View.VISIBLE);
                                clearhistoryBtn.setVisibility(View.VISIBLE);
                                searchimage.setVisibility(View.VISIBLE);


                                barcodeHistoryAdapter = new BarcodeHistoryAdapter(requireActivity(), barcodeDatalist, historyitemPopUpListener);
                                layoutManager = new LinearLayoutManager(requireActivity());
                                historyitemRecyclerView.setLayoutManager(layoutManager);
                                historyitemRecyclerView.setAdapter(barcodeHistoryAdapter);
                                barcodeHistoryAdapter.notifyDataSetChanged();


                            }


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


        firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata").add(barcodeData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i(TAG, "onSuccess: saved");
                        Toast.makeText(requireActivity(), "Added to Favourite", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "onFailure: failed");

                    }
                });


    }

    private void copybarResult(int position) {


        ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copy text", barcodeDatalist.get(position).getRawvalue());
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(requireActivity(), "Copied", Toast.LENGTH_SHORT).show();


    }

    private void sharebarResult(int position) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_SUBJECT, "share this");
        intent.putExtra(Intent.EXTRA_TEXT, barcodeDatalist.get(position).getRawvalue());

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Share"));
        } else {
            Toast.makeText(requireActivity(), "No apps are available", Toast.LENGTH_SHORT).show();
        }


    }


    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_history, container, false);
        historyitemRecyclerView = view.findViewById(R.id.id_recyclerviewhistory);
        showemptylist = view.findViewById(R.id.id_showemptylist);
        titleHistoryFragment = view.findViewById(R.id.id_ttlehistoryfragment);
        clearhistoryBtn = view.findViewById(R.id.id_clearhistorybarcodedata);
        searchimage = view.findViewById(R.id.id_opensearchactivity);




        searchimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(requireActivity(), SearchActivity.class);
                i.putExtra("accept", "history");
                startActivity(i);
            }
        });


        // Inflate the layout for this fragment
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        barcodeDatalist = new ArrayList<>();
        documentPathlist = new ArrayList<>();



        clearhistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                for (int i=0;i<documentPathlist.size();i++){



                    Map<String, Object> barcodeData = new HashMap<>();
                    barcodeData.put("title", barcodeDatalist.get(i).getTitle());
                    barcodeData.put("content", barcodeDatalist.get(i).getContent());
                    barcodeData.put("rawvalue", barcodeDatalist.get(i).getRawvalue());
                    barcodeData.put("valuetype", barcodeDatalist.get(i).getValuetype());


                    firestore.collection("barcodescanning/" + currentUser.getUid() + "/barcodedata").document(documentPathlist.get(i)).delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {






                                    updatingUi();


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });


















                }

                Toast.makeText(requireActivity(), "All records have been deleted", Toast.LENGTH_SHORT).show();




            }
        });











        updatingUi();


        return view;
    }


}