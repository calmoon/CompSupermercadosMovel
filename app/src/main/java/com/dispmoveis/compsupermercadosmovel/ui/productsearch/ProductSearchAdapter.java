package com.dispmoveis.compsupermercadosmovel.ui.productsearch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.databinding.AdapterProductSearchBinding;
import com.dispmoveis.compsupermercadosmovel.model.CustomViewHolder;
import com.dispmoveis.compsupermercadosmovel.model.SupermarketItem;
import com.dispmoveis.compsupermercadosmovel.util.Config;
import com.dispmoveis.compsupermercadosmovel.util.Util;

import java.util.List;


public class ProductSearchAdapter extends RecyclerView.Adapter {

    private final Context context;
    private final List<SupermarketItem> supermarketItems;

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

        Util.setBitmapFromURL(binding.imageProductSearch, item.getProductImageUrl());

        String textItemPrice = "R$ " + Config.currencyFormat.format(item.getPrice());
        binding.textItemPriceSearch.setText(textItemPrice);

        binding.textProductNameSearch.setText(item.getProductName());

        holder.itemView.setOnClickListener(v -> {
            //TODO: lógica para enviar este item pro carrinho
        });
    }

    @Override
    public int getItemCount() {
        return supermarketItems.size();
    }
}
