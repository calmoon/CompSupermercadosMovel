package com.dispmoveis.compsupermercadosmovel;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.databinding.CartItemBinding;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter {

    private CartItemBinding binding;

    ViewCartActivity viewCartActivity;
    List<CartItemData> cartItems;

    public CartAdapter(ViewCartActivity viewCartActivity, List<CartItemData> cartItems) {
        this.viewCartActivity = viewCartActivity;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = CartItemBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CustomViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CartItemData itemData = cartItems.get(position);

        binding.imageCartProduct.setImageURI(itemData.productImageUri);
        binding.textCartProductName.setText(itemData.productName);
        binding.textCartProductPrice.setText("R$ " + itemData.productPrice.toString());
        binding.editCartQty.setText(itemData.productQty.toString());
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

}
