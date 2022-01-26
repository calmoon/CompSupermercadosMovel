package com.dispmoveis.compsupermercadosmovel.ui.previouscarts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.R;
import com.dispmoveis.compsupermercadosmovel.databinding.AdapterPreviousCartsBinding;
import com.dispmoveis.compsupermercadosmovel.model.CustomViewHolder;
import com.dispmoveis.compsupermercadosmovel.ui.cart.CartActivity;
import com.dispmoveis.compsupermercadosmovel.util.Config;

import java.util.List;

public class PreviousCartsAdapter extends RecyclerView.Adapter{

    private final Context context;
    private List<PreviousCartsItem> cartHistoryItems;

    public PreviousCartsAdapter(Context context, List<PreviousCartsItem> cartHistoryItems) {
        this.context = context;
        this.cartHistoryItems = cartHistoryItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_previous_carts,
                parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PreviousCartsItem itemData = this.cartHistoryItems.get(position);

        AdapterPreviousCartsBinding binding = AdapterPreviousCartsBinding.bind(holder.itemView);

        binding.textCartName.setText(itemData.getName());
        binding.textCartLastModified.setText(itemData.getDate());
        binding.textCartItemQty.setText(String.valueOf(itemData.getQtdItems()));

        String textCartTotal = "R$ " + Config.getCurrencyFormat().format(itemData.getTotal());
        binding.textCartTotal.setText(textCartTotal);

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, CartActivity.class)
                    .putExtra(CartActivity.EXTRA_CART_ID, String.valueOf(itemData.getId()));
            ((PreviousCartsActivity) context).startActivityForResult(i, PreviousCartsActivity.NEW_ITEM_REQUEST);
        });

        binding.buttonDeleteCart.setOnClickListener(v -> {
        });
    }

    @Override
    public int getItemCount() {
        return cartHistoryItems.size();
    }

}
