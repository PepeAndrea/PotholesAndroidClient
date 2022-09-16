package com.exam.potholes.UI.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exam.potholes.R;

public class PotholeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView username,latitude,longitude,variation;

    public PotholeViewHolder(@NonNull View itemView) {
        super(itemView);

        username = itemView.findViewById(R.id.usernameValueItem);
        latitude = itemView.findViewById(R.id.latValueItem);
        longitude = itemView.findViewById(R.id.lonValueItem);
        variation = itemView.findViewById(R.id.variationValueItem);
    }

    @Override
    public void onClick(View view) {

    }
}
