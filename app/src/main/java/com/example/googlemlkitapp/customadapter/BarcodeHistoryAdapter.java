package com.example.googlemlkitapp.customadapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemlkitapp.modeldata.BarcodeData;

import java.util.ArrayList;

public class BarcodeHistoryAdapter extends RecyclerView.Adapter<BarcodeHistoryAdapter.BarcodeHistoryViewHolder> {
    private Activity activity;
    private ArrayList<BarcodeData> barcodeDatalist;


    public BarcodeHistoryAdapter(Activity activity, ArrayList<BarcodeData> barcodeDatalist) {
        this.activity = activity;
        this.barcodeDatalist = barcodeDatalist;
    }




    @NonNull
    @Override
    public BarcodeHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull BarcodeHistoryViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return barcodeDatalist.size();
    }

    public class BarcodeHistoryViewHolder extends RecyclerView.ViewHolder {
        public BarcodeHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
