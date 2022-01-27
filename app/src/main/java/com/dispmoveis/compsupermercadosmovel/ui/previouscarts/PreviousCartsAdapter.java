package com.dispmoveis.compsupermercadosmovel.ui.previouscarts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.R;
import com.dispmoveis.compsupermercadosmovel.databinding.AdapterPreviousCartsBinding;
import com.dispmoveis.compsupermercadosmovel.model.CustomViewHolder;
import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.dispmoveis.compsupermercadosmovel.ui.cart.CartActivity;
import com.dispmoveis.compsupermercadosmovel.util.Config;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PreviousCartsAdapter extends RecyclerView.Adapter{

    private final Context context;
    private final List<PreviousCartsItem> cartHistoryItems;

    public PreviousCartsAdapter(Context context, List<PreviousCartsItem> cartHistoryItems) {
        this.context = context;
        this.cartHistoryItems = cartHistoryItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_previous_carts,
                parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PreviousCartsItem itemData = this.cartHistoryItems.get(position);

        AdapterPreviousCartsBinding binding = AdapterPreviousCartsBinding.bind(holder.itemView);

        binding.textCartName.setText(itemData.getName());

        binding.textCartLastModified.setText(itemData.getDate());

        binding.textCartSupermarket.setText(itemData.getSupermarketName());

        String textCartItemQty = itemData.getQtdItems() + " itens";
        binding.textCartItemQty.setText(textCartItemQty);

        String textCartTotal = "R$ " + Config.getCurrencyFormat().format(itemData.getTotal());
        binding.textCartTotal.setText(textCartTotal);

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, CartActivity.class)
                    .putExtra(CartActivity.EXTRA_CART_ID, String.valueOf(itemData.getId()));
            ((PreviousCartsActivity) context).startActivityForResult(i, PreviousCartsActivity.NEW_ITEM_REQUEST);
        });

        binding.buttonDeleteCart.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_trash))
                .setTitle("Excluir carrinho?")
                .setMessage("Tem certeza que quer excluir '" + itemData.getName() + "'?")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    ServerClient.delete("carrinho", String.valueOf(itemData.getId()), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                int resultCode = response.getInt("result_code");
                                if (resultCode == 1) {
                                    ((PreviousCartsActivity) context).previousCartsViewModel.reloadCartList();
                                }
                                else {
                                    Toast.makeText(context,"Erro ao fazer consulta.", Toast.LENGTH_LONG).show();
                                    Log.e("Super", "Erro de conssulta - " + response.toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    dialog.dismiss();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel())
                .show()
        );

    }

    @Override
    public int getItemCount() {
        return cartHistoryItems.size();
    }

}
