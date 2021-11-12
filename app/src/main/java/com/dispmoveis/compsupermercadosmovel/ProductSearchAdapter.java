package com.dispmoveis.compsupermercadosmovel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.databinding.AdapterProductSearchBinding;

import java.util.List;


public class ProductSearchAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<SupermarketItem> supermarketItems;

    private AdapterProductSearchBinding binding;

    public ProductSearchAdapter(Context context, List<SupermarketItem> supermarketItems) {
        this.context = context;
        this.supermarketItems = supermarketItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = AdapterProductSearchBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CustomViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SupermarketItem item = supermarketItems.get(position);

        binding.textProductSearchName.setText(item.getName());
        binding.textProductSearchPrice.setText("R$ " + item.getPrice());
        binding.imageProductSearch.setImageBitmap(item.getImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: lógica para enviar este item pro carrinho
            }
        });
    }

    @Override
    public int getItemCount() {
        return supermarketItems.size();
    }
}
