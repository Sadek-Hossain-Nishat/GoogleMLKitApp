package com.example.googlemlkitapp.customadapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemlkitapp.R;
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
        View view= LayoutInflater.from(activity).inflate(R.layout.item_history_barcode,parent,false);
        return new BarcodeHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarcodeHistoryViewHolder holder, int position) {
       holder.titletext.setText(barcodeDatalist.get(position).getTitle());
       holder.contenttext.setText(barcodeDatalist.get(position).getContent());

    }

    @Override
    public int getItemCount() {
        return barcodeDatalist.size();
    }

    public class BarcodeHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView titletext,contenttext;


        public BarcodeHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            titletext=itemView.findViewById(R.id.id_titlebarcodeitem);
            contenttext=itemView.findViewById(R.id.id_contentbarcodeitem);




        }
    }
}
