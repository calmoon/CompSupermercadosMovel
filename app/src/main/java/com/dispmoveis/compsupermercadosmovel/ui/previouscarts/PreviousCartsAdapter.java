package com.dispmoveis.compsupermercadosmovel.ui.previouscarts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.R;
import com.dispmoveis.compsupermercadosmovel.databinding.AdapterPreviousCartsBinding;
import com.dispmoveis.compsupermercadosmovel.model.CustomViewHolder;

import java.util.List;

public class PreviousCartsAdapter extends RecyclerView.Adapter{

    private final List<PreviousCartsItemData> cartHistoryItems;

    private final Context context;

    public PreviousCartsAdapter(Context context, List<PreviousCartsItemData> cartHistoryItems){
        this.context = context;
        this.cartHistoryItems = cartHistoryItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_previous_carts, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AdapterPreviousCartsBinding binding = AdapterPreviousCartsBinding.bind(holder.itemView);

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
