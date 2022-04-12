package com.example.googlemlkitapp.fragments;

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
import com.example.googlemlkitapp.barcodescanning.DetailsBarcodeActivity;
import com.example.googlemlkitapp.customadapter.BarcodeHistoryAdapter;
import com.example.googlemlkitapp.customlistener.HistoryitemPopUpListener;
import com.example.googlemlkitapp.customlistener.ItemListener;
import com.example.googlemlkitapp.modeldata.BarcodeData;
import com.example.googlemlkitapp.searchitem.SearchActivity;
import com.example.googlemlkitapp.websearch.WebSearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FavouriteFragment extends Fragment {

    private RecyclerView favouritemRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BarcodeHistoryAdapter barcodeHistoryAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;
    ArrayList<BarcodeData> barcodeDatalist;
    ArrayList<String> documentpathlist;
    private TextView titlefavouriteFragment, showemptyfavourite;
    private Button clearallfavouriteBtn;
    private ImageView searchimage;

    private ItemListener itemListener=new ItemListener() {
        @Override
        public void clickItem(int position) {
            Intent idetails=new Intent(requireActivity(), DetailsBarcodeActivity.class);
            idetails.putExtra("barcode", (Serializable) barcodeDatalist.get(position));
            startActivity(idetails);



        }
    };


    private HistoryitemPopUpListener favouriteitemPopUpListener = new HistoryitemPopUpListener() {
        @Override
        public void actionpopupinHistoryitem(int position, View view) {


            PopupMenu popup = new PopupMenu(requireActivity(), view);
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
                            openFavouriteItem(position);
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

    private void openFavouriteItem(int position) {


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
                        Toast.makeText(requireActivity(), "Favourite Item has been removed", Toast.LENGTH_SHORT).show();
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


                            if (barcodeDatalist.isEmpty()) {
                                titlefavouriteFragment.setVisibility(View.GONE);
                                clearallfavouriteBtn.setVisibility(View.GONE);
                                showemptyfavourite.setVisibility(View.VISIBLE);
                                searchimage.setVisibility(View.GONE);


                            } else {


                                titlefavouriteFragment.setVisibility(View.VISIBLE);
                                clearallfavouriteBtn.setVisibility(View.VISIBLE);
                                showemptyfavourite.setVisibility(View.GONE);
                                searchimage.setVisibility(View.VISIBLE);


                                barcodeHistoryAdapter = new BarcodeHistoryAdapter(requireActivity(), barcodeDatalist, favouriteitemPopUpListener,itemListener);
                                layoutManager = new LinearLayoutManager(requireActivity());
                                favouritemRecyclerView.setLayoutManager(layoutManager);
                                favouritemRecyclerView.setAdapter(barcodeHistoryAdapter);
                                barcodeHistoryAdapter.notifyDataSetChanged();


                            }


                        } else {

                        }
                    }
                });


    }



    private void copybarResult(int position) {


        ClipboardManager clipboardManager=(ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
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




    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);


        favouritemRecyclerView = view.findViewById(R.id.id_recyclerviewfavourite);
        titlefavouriteFragment = view.findViewById(R.id.id_titlefavouritefragment);
        showemptyfavourite = view.findViewById(R.id.id_showemptyfavouritelist);
        clearallfavouriteBtn = view.findViewById(R.id.id_clearallfavourite);
        searchimage=view.findViewById(R.id.id_opensearchactivity);

        searchimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(requireActivity(), SearchActivity.class);
                i.putExtra("accept","favourite");
                startActivity(i);
            }
        });


        // Inflate the layout for this fragment
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        barcodeDatalist = new ArrayList<>();
        documentpathlist = new ArrayList<>();

        clearallfavouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i=0;i<documentpathlist.size();i++){



                    Map<String, Object> barcodeData = new HashMap<>();
                    barcodeData.put("title", barcodeDatalist.get(i).getTitle());
                    barcodeData.put("content", barcodeDatalist.get(i).getContent());
                    barcodeData.put("rawvalue", barcodeDatalist.get(i).getRawvalue());
                    barcodeData.put("valuetype", barcodeDatalist.get(i).getValuetype());
                    barcodeData.put("date",barcodeDatalist.get(i).getDate());


                    firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata").document(documentpathlist.get(i)).delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(requireActivity(), "Favourite Item has been removed", Toast.LENGTH_SHORT).show();
                                    updatingUi();


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });








                }


                Toast.makeText(requireActivity(), "All Favourite have been deleted", Toast.LENGTH_SHORT).show();











            }
        });







        updatingUi();


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        updatingUi();
    }
}