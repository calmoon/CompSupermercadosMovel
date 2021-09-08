package com.dispmoveis.compsupermercadosmovel;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.databinding.CartHistoryItemBinding;

import java.util.List;

public class CartHistoryAdapter extends RecyclerView.Adapter{

    private CartHistoryItemBinding binding;

    List<CartHistoryItemData> cartHistoryItems;

    public CartHistoryAdapter(List<CartHistoryItemData> cartHistoryItems){
        this.cartHistoryItems = cartHistoryItems;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = CartHistoryItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CustomViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CartHistoryItemData itemData = this.cartHistoryItems.get(position);

        binding.textTitle.setText(itemData.cartTitle);
        binding.textTotal.setText(itemData.cartTotal);
        binding.textQuantity.setText(itemData.qtyOfItems);
        binding.textDate.setText(itemData.date);
    }

    @Override
    public int getItemCount() {
        return cartHistoryItems.size();
    }
}
