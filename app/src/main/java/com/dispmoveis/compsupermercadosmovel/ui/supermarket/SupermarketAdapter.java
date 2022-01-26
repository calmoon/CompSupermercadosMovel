package com.dispmoveis.compsupermercadosmovel.ui.supermarket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.R;
import com.dispmoveis.compsupermercadosmovel.databinding.AdapterSupermarketBinding;
import com.dispmoveis.compsupermercadosmovel.model.CustomViewHolder;

import java.util.List;

public class SupermarketAdapter extends RecyclerView.Adapter {

    private final List<SupermarketsData> supermarketsDataList;

    private final Context context;

    public SupermarketAdapter(Context context, List<SupermarketsData> supermarketsDataList) {
        this.context = context;
        this.supermarketsDataList = supermarketsDataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_supermarket, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AdapterSupermarketBinding binding = AdapterSupermarketBinding.bind(holder.itemView);

        SupermarketsData supermarketsData = this.supermarketsDataList.get(position);

        binding.textSupermarketName.setText(supermarketsData.nome);
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent();
            i.putExtra("supermarketId", supermarketsData.id);
            ((SupermarketActivity) context).setResult(Activity.RESULT_OK, i);
            ((SupermarketActivity) context).finish();
        });
    }

    @Override
    public int getItemCount() { return supermarketsDataList.size(); }
}
