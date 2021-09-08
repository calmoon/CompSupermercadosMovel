package com.dispmoveis.compsupermercadosmovel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityViewCartBinding;

import java.util.ArrayList;
import java.util.List;

public class ViewCartActivity extends AppCompatActivity {

    static int NEW_ITEM_REQUEST = 1;

    private ActivityViewCartBinding binding;

    private List<CartItemData> cartItems = new ArrayList<>();
    private CartAdapter cartAdapter = new CartAdapter(cartItems);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewCartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerCart.setAdapter(cartAdapter);
        binding.recyclerCart.setHasFixedSize(true);

        binding.optionBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                //Intent i = new Intent( );
                //ActivityResultContracts.StartActivityForResult( );
            }
        });

        binding.optionCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
                //Intent i = new Intent( );
                //ActivityResultContracts.StartActivityForResult( );
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_ITEM_REQUEST) {
            Uri productImageUri = data.getData();
            String productName = data.getStringExtra("productName");
            double productPrice = data.getDoubleExtra("productPrice", 0);
            Integer productQty = data.getIntExtra("productQty", 1);

            CartItemData newItem = new CartItemData(productImageUri, productName, productPrice, productQty);
            cartItems.add(newItem);

            cartAdapter.notifyItemInserted(cartItems.size()-1);
        }
    }

}