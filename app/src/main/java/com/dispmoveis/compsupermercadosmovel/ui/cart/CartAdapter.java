package com.dispmoveis.compsupermercadosmovel.ui.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dispmoveis.compsupermercadosmovel.databinding.AdapterCartBinding;
import com.dispmoveis.compsupermercadosmovel.model.CustomViewHolder;
import com.dispmoveis.compsupermercadosmovel.util.Config;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter {

    private final List<CartItemData> cartItems;

    private Context context;
    private AdapterCartBinding binding;

    public CartAdapter(List<CartItemData> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        binding = AdapterCartBinding
                .inflate(LayoutInflater.from(context), parent, false);
        return new CustomViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CartItemData newCartItem = cartItems.get(position);

        Double itemTotal = newCartItem.getPrice() * newCartItem.getQuantity();

        String textItemTotal = "R$ " + Config.getCurrencyFormat().format(itemTotal);
        String textItemQty = newCartItem.getQuantity().toString();

        Glide.with(context)
            .load(newCartItem.getProductImageUrl())
            .into(binding.imageProductCart);

        binding.textProductNameCart.setText( newCartItem.getProductName() );
        binding.textItemTotalCart.setText( textItemTotal );
        binding.editItemQtyCart.setText( textItemQty );
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

}
