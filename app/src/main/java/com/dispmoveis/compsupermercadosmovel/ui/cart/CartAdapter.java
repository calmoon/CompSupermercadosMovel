package com.dispmoveis.compsupermercadosmovel.ui.cart;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.databinding.AdapterCartBinding;
import com.dispmoveis.compsupermercadosmovel.model.CustomViewHolder;
import com.dispmoveis.compsupermercadosmovel.util.Util;

import java.text.DecimalFormat;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter {

    static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private AdapterCartBinding binding;

    private List<CartItemData> cartItems;

    public CartAdapter(List<CartItemData> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = AdapterCartBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CustomViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CartItemData newCartItem = cartItems.get(position);

        Double itemTotal = newCartItem.getPrice() * newCartItem.getQuantity();

        String textItemTotal = "R$ " + decimalFormat.format(itemTotal);
        String textItemQty = newCartItem.getQuantity().toString();

        Util.setBitmapFromURL(binding.imageProductCart, newCartItem.getProductImageUrl());
        binding.textProductNameCart.setText( newCartItem.getProductName() );
        binding.textItemTotalCart.setText( textItemTotal );
        binding.editItemQtyCart.setText( textItemQty );
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

}
