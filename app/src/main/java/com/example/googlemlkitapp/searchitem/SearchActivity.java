package com.example.googlemlkitapp.searchitem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.googlemlkitapp.R;
import com.example.googlemlkitapp.customadapter.BarcodeHistoryAdapter;
import com.example.googlemlkitapp.customadapter.FilterAdapter;
import com.example.googlemlkitapp.modeldata.BarcodeData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<BarcodeData> barcodeDatalist;
    private FilterAdapter adapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView=findViewById(R.id.id_searchview);
        recyclerView=findViewById(R.id.id_recyclerviewinsearchview);
        barcodeDatalist=new ArrayList<>();

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

        firestore.collection("favouritebarcode/" + currentUser.getUid() + "/favouritebarcodedata")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            barcodeDatalist.clear();

                            for (QueryDocumentSnapshot documnet : task.getResult()) {

                                BarcodeData barcodeData = documnet.toObject(BarcodeData.class);

                                barcodeDatalist.add(barcodeData);



                            }
                            layoutManager=new LinearLayoutManager(SearchActivity.this);
                            adapter=new FilterAdapter(SearchActivity.this,barcodeDatalist);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);




                        } else {

                        }
                    }
                });
























    }

    private void fromHistory() {
        firestore.collection("barcodescanning/" + currentUser.getUid() + "/barcodedata")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            barcodeDatalist.clear();

                            for (QueryDocumentSnapshot documnet : task.getResult()) {

                                BarcodeData barcodeData = documnet.toObject(BarcodeData.class);

                                barcodeDatalist.add(barcodeData);



                            }
                            layoutManager=new LinearLayoutManager(SearchActivity.this);
                            adapter=new FilterAdapter(SearchActivity.this,barcodeDatalist);
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setAdapter(adapter);




                        } else {

                        }
                    }
                });



















    }
}