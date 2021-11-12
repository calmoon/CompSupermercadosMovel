package com.dispmoveis.compsupermercadosmovel.ui.cart;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.model.CustomViewHolder;
import com.dispmoveis.compsupermercadosmovel.databinding.AdapterCartBinding;

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
        CartItemData itemData = cartItems.get(position);

        String productPrice = "R$ " + decimalFormat.format(itemData.productPrice);
        String productQty = itemData.productQty.toString();

        //binding.imageCartProduct.setImageURI(itemData.productImageUri);
        binding.textCartProductName.setText(itemData.productName);
        binding.textCartProductPrice.setText(productPrice);
        binding.editCartProductQty.setText(productQty);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

}
