package com.example.googlemlkitapp.customadapter.mladapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemlkitapp.R;
import com.example.googlemlkitapp.customlistener.ItemListener;
import com.example.googlemlkitapp.modeldata.mlservice.MLService;

import java.util.ArrayList;

public class MLServicesAdapter extends RecyclerView.Adapter<MLServicesAdapter.MLServicesViewHolder> {
    private Context context;
    private ArrayList<MLService> mlServices;
    private ItemListener itemListener;


    public MLServicesAdapter(Context context, ArrayList<MLService> mlServices, ItemListener itemListener) {
        this.context = context;
        this.mlServices = mlServices;
        this.itemListener = itemListener;
    }







    @NonNull
    @Override
    public MLServicesAdapter.MLServicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.ml_service_item,parent,false);

        return new MLServicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MLServicesAdapter.MLServicesViewHolder holder, int position) {

        holder.servicetitle.setText(mlServices.get(position).getMlservicetitle().toString());
        holder.servicelogo.setImageResource(mlServices.get(position).getMlservicelogoid());

    }

    @Override
    public int getItemCount() {
        return mlServices.size();
    }

    public class MLServicesViewHolder extends RecyclerView.ViewHolder  {
        ImageView servicelogo;
        TextView servicetitle;
        public MLServicesViewHolder(@NonNull View itemView) {
            super(itemView);
            servicelogo=itemView.findViewById(R.id.servicelogoid);
            servicetitle=itemView.findViewById(R.id.servicetitleid);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.clickItem(getAdapterPosition());

                }
            });

        }


    }
}