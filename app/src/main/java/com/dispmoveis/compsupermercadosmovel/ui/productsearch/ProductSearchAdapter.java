package com.dispmoveis.compsupermercadosmovel.ui.productsearch;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dispmoveis.compsupermercadosmovel.R;
import com.dispmoveis.compsupermercadosmovel.databinding.AdapterProductSearchBinding;
import com.dispmoveis.compsupermercadosmovel.model.CustomViewHolder;
import com.dispmoveis.compsupermercadosmovel.model.SupermarketItem;
import com.dispmoveis.compsupermercadosmovel.ui.registerproduct.RegisterProductActivity;
import com.dispmoveis.compsupermercadosmovel.util.Config;

import java.util.List;


public class ProductSearchAdapter extends RecyclerView.Adapter {

    private final List<SupermarketItem> supermarketItems;

    private final Context context;

    public ProductSearchAdapter(Context context, List<SupermarketItem> supermarketItems) {
        this.context = context;
        this.supermarketItems = supermarketItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_product_search, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AdapterProductSearchBinding binding = AdapterProductSearchBinding.bind(holder.itemView);

        SupermarketItem item = supermarketItems.get(position);

        Glide.with(context)
                .load(item.getProductImageUrl())
                .into(binding.imageProductSearch);

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

    /*
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
     */

}