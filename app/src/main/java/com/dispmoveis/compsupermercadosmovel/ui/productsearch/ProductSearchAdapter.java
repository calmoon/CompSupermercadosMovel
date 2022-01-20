package com.dispmoveis.compsupermercadosmovel.ui.productsearch;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.databinding.AdapterProductSearchBinding;
import com.dispmoveis.compsupermercadosmovel.model.CustomViewHolder;
import com.dispmoveis.compsupermercadosmovel.model.SupermarketItem;
import com.dispmoveis.compsupermercadosmovel.ui.registerproduct.RegisterProductActivity;
import com.dispmoveis.compsupermercadosmovel.util.Config;
import com.dispmoveis.compsupermercadosmovel.util.Util;

import java.util.List;


public class ProductSearchAdapter extends RecyclerView.Adapter {

    private Context context;
    private final List<SupermarketItem> supermarketItems;

    private AdapterProductSearchBinding binding;

    public ProductSearchAdapter(Context context, List<SupermarketItem> supermarketItems) {
        this.context = context;
        this.supermarketItems = supermarketItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        binding = AdapterProductSearchBinding
                .inflate(LayoutInflater.from(context), parent, false);
        return new CustomViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SupermarketItem item = supermarketItems.get(position);

        Util.setBitmapFromURL(binding.imageProductSearch, item.getProductImageUrl());

        String textItemPrice = "R$ " + Config.getCurrencyFormat().format(item.getPrice());
        binding.textItemPriceSearch.setText(textItemPrice);

        binding.textProductNameSearch.setText(item.getProductName());

        Double currentCartTotal = ((ProductSearchActivity) context).currentCartTotal;

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, RegisterProductActivity.class)
                    .putExtra(RegisterProductActivity.EXTRA_SELECTED_ITEM_ID, item.getId())
                    .putExtra(RegisterProductActivity.EXTRA_CURRENT_CART_TOTAL, currentCartTotal);
            ((ProductSearchActivity) context).editSelectedProductLauncher.launch(i);
        });
    }

    @Override
    public int getItemCount() {
        return supermarketItems.size();
    }
}