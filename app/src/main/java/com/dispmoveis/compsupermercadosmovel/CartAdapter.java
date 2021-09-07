package com.dispmoveis.compsupermercadosmovel;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.databinding.CartItemBinding;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter {

    ViewCartActivity viewCartActivity;
    List<CartItem> cartItems;

    public CartAdapter(ViewCartActivity viewCartActivity, List<CartItem> items) {
        this.viewCartActivity = viewCartActivity;
        this.cartItems = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewCartActivity);
        View v = inflater.inflate(R.layout.cart_item, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        View v = holder.itemView;
        CartItemBinding binding = CartItemBinding.inflate( ((Activity) v.getContext()).getWindow().getLayoutInflater() );

        binding.imageCartProduct.setImageURI(item.productImageUri);
        binding.textCartProductName.setText(item.productName);
        binding.textCartProductPrice.setText("R$ " + item.productPrice.toString());
        binding.editCartQty.setText(item.productQty.toString());
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

}
