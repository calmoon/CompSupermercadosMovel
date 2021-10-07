package com.dispmoveis.compsupermercadosmovel;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.databinding.PreviousCartsItemBinding;

import java.util.List;

public class PreviousCartsAdapter extends RecyclerView.Adapter{

    private PreviousCartsItemBinding binding;

    private List<PreviousCartsItemData> cartHistoryItems;

    public PreviousCartsAdapter(List<PreviousCartsItemData> cartHistoryItems){
        this.cartHistoryItems = cartHistoryItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = PreviousCartsItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CustomViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PreviousCartsItemData itemData = this.cartHistoryItems.get(position);

        binding.textCartName.setText(itemData.cartTitle);
        binding.textCartTotal.setText(itemData.cartTotal);
        binding.textCartItemQty.setText(itemData.qtyOfItems);
        binding.textCartLastModified.setText(itemData.date);
    }

    @Override
    public int getItemCount() {
        return cartHistoryItems.size();
    }

}
