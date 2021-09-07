package com.dispmoveis.compsupermercadosmovel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.databinding.CartHistoryItemBinding;

import java.util.List;

public class CartHistoryAdapter extends RecyclerView.Adapter{

    private CartHistoryItemBinding binding;

    HomeActivity homeActivity;
    List<CartHistoryItemData> cartHistoryItemData;

    public CartHistoryAdapter(HomeActivity homeActivity, List<CartHistoryItemData> cartHistoryItemData){
        this.homeActivity = homeActivity;
        this.cartHistoryItemData = cartHistoryItemData;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(homeActivity);
        View view = layoutInflater.inflate(R.layout.cart_history_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CartHistoryItemData cartHistoryItemData = this.cartHistoryItemData.get(position);

        View view = holder.itemView;

        binding.textTitle.setText(cartHistoryItemData.cartTitle);
        binding.textTotal.setText(cartHistoryItemData.cartTotal);
        binding.textQuantity.setText(cartHistoryItemData.qtyOfItems);
        binding.textDate.setText(cartHistoryItemData.date);
    }

    @Override
    public int getItemCount() {
        return cartHistoryItemData.size();
    }
}
