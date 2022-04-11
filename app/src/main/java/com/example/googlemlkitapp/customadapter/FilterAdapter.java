package com.example.googlemlkitapp.customadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemlkitapp.R;
import com.example.googlemlkitapp.customlistener.HistoryitemPopUpListener;
import com.example.googlemlkitapp.customlistener.ItemListener;
import com.example.googlemlkitapp.modeldata.BarcodeData;

import java.util.ArrayList;
import java.util.Collection;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> implements Filterable {
    private ArrayList<BarcodeData> barcodeDatalist;
    private ArrayList<BarcodeData> barcodeDatafulllist;
    private Context context;
    private HistoryitemPopUpListener listener;
    private ItemListener itemListener;


    public FilterAdapter(Context context,ArrayList<BarcodeData> barcodeDatalist,  HistoryitemPopUpListener listener, ItemListener itemListener) {
        this.context = context;
        this.barcodeDatalist = barcodeDatalist;

        this.listener = listener;
        this.itemListener = itemListener;
        barcodeDatafulllist=new ArrayList<>(barcodeDatalist);
    }

    @NonNull
    @Override
    public FilterAdapter.FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_history_barcode,parent,false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterAdapter.FilterViewHolder holder, int position) {
        holder.titletext.setText(barcodeDatalist.get(position).getTitle());
        holder.contenttext.setText(barcodeDatalist.get(position).getContent());
        holder.date.setText(barcodeDatalist.get(position).getDate());


    }

    @Override
    public int getItemCount() {
        return barcodeDatalist.size();
    }

    @Override
    public Filter getFilter() {
        return barcodeFilter;
    }



    private Filter barcodeFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<BarcodeData> filteredBarcodelist= new ArrayList<BarcodeData>();
            if (constraint==null||constraint.length()==0){
                filteredBarcodelist.addAll(barcodeDatafulllist);
            }else {
                String filterPattern=constraint.toString().toLowerCase().trim();
                for (BarcodeData barcodeData:barcodeDatafulllist){
                    if (barcodeData.getRawvalue().toLowerCase().contains(filterPattern)){
                        filteredBarcodelist.add(barcodeData);
                    }
                }
            }
            FilterResults filterResults=new FilterResults();
            filterResults.values=filteredBarcodelist;
            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            barcodeDatalist.clear();
            barcodeDatalist.addAll((ArrayList) results.values);
            notifyDataSetChanged();

        }
    };

    public class FilterViewHolder extends RecyclerView.ViewHolder {


        TextView titletext,contenttext,date;
        ImageView imageView;



        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            titletext=itemView.findViewById(R.id.id_titlebarcodeitem);
            contenttext=itemView.findViewById(R.id.id_contentbarcodeitem);
            date=itemView.findViewById(R.id.id_date);
            imageView=itemView.findViewById(R.id.id_popuphistoryitem);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.actionpopupinHistoryitem(getAdapterPosition(),v);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.clickItem(getAdapterPosition());
                }
            });








        }
    }
}
