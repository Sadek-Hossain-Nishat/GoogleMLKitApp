package com.example.googlemlkitapp.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.googlemlkitapp.R;
import com.example.googlemlkitapp.customadapter.BarcodeHistoryAdapter;
import com.example.googlemlkitapp.modeldata.BarcodeData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class HistoryFragment extends Fragment {
    private static final String TAG = "show";
    private RecyclerView historyitemRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BarcodeHistoryAdapter barcodeHistoryAdapter;





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



        View view= inflater.inflate(R.layout.fragment_history, container, false);
       historyitemRecyclerView=view.findViewById(R.id.id_recyclerviewhistory);

        // Inflate the layout for this fragment
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser=firebaseAuth.getCurrentUser();
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        ArrayList<BarcodeData> barcodeDatalist=new ArrayList<>();
        firestore.collection("barcodescanning/"+currentUser.getUid()+"/barcodedata")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot documnet:task.getResult()){

                                BarcodeData barcodeData=documnet.toObject(BarcodeData.class);
                                Log.i(TAG, "onComplete: "+barcodeData.getRawvalue());
                                barcodeDatalist.add(barcodeData);
                            }

                            barcodeHistoryAdapter=new BarcodeHistoryAdapter(requireActivity(),barcodeDatalist);
                            layoutManager =new LinearLayoutManager(requireActivity());
                            historyitemRecyclerView.setLayoutManager(layoutManager);
                            historyitemRecyclerView.setAdapter(barcodeHistoryAdapter);
                            barcodeHistoryAdapter.notifyDataSetChanged();





                        }
                        else {

                        }
                    }
                });






        return view;
    }




}