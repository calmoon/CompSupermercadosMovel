package com.dispmoveis.compsupermercadosmovel.ui.cart;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dispmoveis.compsupermercadosmovel.R;
import com.dispmoveis.compsupermercadosmovel.databinding.AdapterCartBinding;
import com.dispmoveis.compsupermercadosmovel.model.CustomViewHolder;
import com.dispmoveis.compsupermercadosmovel.util.Config;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter {

    private final List<CartItemData> cartItems = new ArrayList<>();

    Context context;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_cart, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AdapterCartBinding binding = AdapterCartBinding.bind(holder.itemView);

        CartItemData aCartItem = cartItems.get(position);

        Double itemTotal = aCartItem.getPrice() * aCartItem.getQuantity();

        String textItemTotal = "R$ " + Config.getCurrencyFormat().format(itemTotal);
        String textItemQty = aCartItem.getQuantity().toString();

        Glide.with(context)
            .load(aCartItem.getProductImageUrl())
            .into(binding.imageProductCart);

        binding.textProductNameCart.setText( aCartItem.getProductName() );
        binding.textItemTotalCart.setText( textItemTotal );
        binding.editItemQtyCart.setText( textItemQty );

        /*
        binding.editItemQtyCart.addTextChangedListener(new TextWatcher() {
            boolean _ignore = false; // indicates if the change was made by the TextWatcher itself.
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (_ignore)
                    return;
                _ignore = true; // prevent infinite loop

                int newQty;

                try {
                    newQty = Integer.parseInt(binding.editItemQtyCart.getText().toString());
                } catch (NumberFormatException e) {
                    newQty = 1;
                }

                if (newQty <= 0) {
                    newQty = 1;
                    binding.editItemQtyCart.setText("1");
                }

                Double oldItemTotal = aCartItem.getPrice() * aCartItem.getQuantity();
                Double newItemTotal = aCartItem.getPrice() * newQty;

                aCartItem.setQuantity(newQty);

                binding.textItemTotalCart.setText("R$ " +  Config.getCurrencyFormat().format(newItemTotal) );

                ((CartActivity) context).reflectItemQtyChange(oldItemTotal, newItemTotal);

                _ignore = false; // release, so the TextWatcher start to listen again.
            }
        });
         */

        binding.buttonRemoveFromCart.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_trash))
                    .setTitle("Remover do carrinho?")
                    .setMessage(aCartItem.getProductName())
                    .setPositiveButton("Remover", (dialog, which) -> {
                        cartItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartItems.size());
                        ((CartActivity) context).reflectItemQtyChange(
                                aCartItem.getQuantity()*aCartItem.getPrice(),
                                0.0
                        );
                        if (cartItems.size() == 0) {
                            cartItems.clear();
                            notifyItemRangeChanged(0, cartItems.size());
                        }
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                        dialog.cancel();
                    })
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    void additem(CartItemData newItem) {
        cartItems.add(newItem);
        notifyItemInserted(cartItems.size()-1);
        notifyItemRangeChanged(cartItems.size()-1, cartItems.size());
    }
}
