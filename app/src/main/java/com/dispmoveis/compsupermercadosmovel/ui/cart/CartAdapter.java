package com.dispmoveis.compsupermercadosmovel.ui.cart;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

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
    private final List<String> removedItemIds = new ArrayList<>();

    private final Context context;

    public CartAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

        binding.editItemQtyCart.setKeyListener(null);
        binding.editItemQtyCart.setFocusable(false);
        binding.editItemQtyCart.setOnClickListener(v -> {
            int oldQty = aCartItem.getQuantity();
            View pickerView = LayoutInflater.from(context).inflate(R.layout.dialog_number_picker, null);
            NumberPicker np = pickerView.findViewById(R.id.number_picker);
            np.setWrapSelectorWheel(false);
            np.setMinValue(1);
            np.setMaxValue(100);
            np.setValue(oldQty);
            new AlertDialog.Builder(context)
                    .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_edit))
                    .setTitle(aCartItem.getProductName())
                    .setView(pickerView)
                    .setPositiveButton("Ok", (dialog, which) -> {
                        int newQty = np.getValue();
                        ((CartActivity) context).reflectItemQtyChange(
                                aCartItem.getPrice() * oldQty,
                                aCartItem.getPrice() * newQty
                        );
                        aCartItem.setQuantity(newQty);
                        notifyItemChanged(position);
                        dialog.dismiss();
                    })
                    .show();
        });

        binding.buttonRemoveFromCart.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_trash))
                .setTitle("Remover do carrinho?")
                .setMessage(aCartItem.getProductName())
                .setPositiveButton("Remover", (dialog, which) -> removeItem(position))
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel())
                .show()
        );
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    List<CartItemData> getCartItems() {
        return cartItems;
    }

    List<String> getRemovedItemIds() {
        return removedItemIds;
    }

    void additem(CartItemData newItem) {
        cartItems.add(newItem);
        notifyItemInserted(cartItems.size()-1);
        notifyItemRangeChanged(cartItems.size()-1, cartItems.size());

        removedItemIds.remove(newItem.getId());
    }

    private void removeItem(int position) {
        CartItemData removedItem = cartItems.get(position);

        // Update cart total
        ((CartActivity) context).reflectItemQtyChange(
                removedItem.getQuantity()*removedItem.getPrice(),
                0.0
        );

        cartItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartItems.size());

        removedItemIds.add(removedItem.getId());
    }

    boolean containsItem(String itemId) {
        for(CartItemData checkedItem : cartItems) {
            if(checkedItem != null && checkedItem.getId().equals(itemId)) {
                return true;
            }
        }
        return false;
    }
}
