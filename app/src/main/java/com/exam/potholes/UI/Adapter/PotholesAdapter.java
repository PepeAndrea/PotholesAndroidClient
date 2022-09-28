package com.exam.potholes.UI.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exam.potholes.Model.Pothole;
import com.exam.potholes.R;

import java.util.List;

public class PotholesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private List<Pothole> potholeList;

    public PotholesAdapter() {
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.potholes_list_item,parent,false);
        return new PotholeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((PotholeViewHolder) holder).username.setText(potholeList.get(position).getUsername());
        ((PotholeViewHolder) holder).latitude.setText("Lat: "+String.valueOf(potholeList.get(position).getLatitude()));
        ((PotholeViewHolder) holder).longitude.setText("Lon: "+String.valueOf(potholeList.get(position).getLongitude()));
        ((PotholeViewHolder) holder).variation.setText("Variazione: "+String.valueOf(potholeList.get(position).getVariation()));
    }

    @Override
    public int getItemCount() {
        if (potholeList != null)
            return potholeList.size();
        return 0;
    }

    public void setPotholes(List<Pothole> potholeList) {
        this.potholeList = potholeList;
        notifyDataSetChanged();
    }
}
