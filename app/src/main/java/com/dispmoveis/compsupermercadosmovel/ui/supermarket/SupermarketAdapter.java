package com.dispmoveis.compsupermercadosmovel.ui.supermarket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.databinding.AdapterSupermarketBinding;
import com.dispmoveis.compsupermercadosmovel.model.CustomViewHolder;

import java.util.List;

public class SupermarketAdapter extends RecyclerView.Adapter {

    private AdapterSupermarketBinding binding;

    private List<SupermarketsData> supermarketsDataList;

    private Context context;

    public SupermarketAdapter(List<SupermarketsData> supermarketsDataList) {
        this.supermarketsDataList = supermarketsDataList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        binding = AdapterSupermarketBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CustomViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SupermarketsData supermarketsData = this.supermarketsDataList.get(position);

        binding.textSupermarketName.setText(supermarketsData.nome);
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent();
            i.putExtra("supermarketID", supermarketsData.id);
            ((SupermarketActivity)context).setResult(Activity.RESULT_OK, i);
            ((SupermarketActivity)context).finish();
        });
    }

    @Override
    public int getItemCount() { return supermarketsDataList.size(); }
}
