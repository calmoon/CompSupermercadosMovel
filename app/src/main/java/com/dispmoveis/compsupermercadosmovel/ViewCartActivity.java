package com.dispmoveis.compsupermercadosmovel;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityViewCartBinding;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewCartActivity extends AppCompatActivity {

    static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    static int NEW_BARCODE_REQUEST = 1;
    static int NEW_ITEM_REQUEST = 2;

    private ActivityViewCartBinding binding;

    private List<CartItemData> cartItems = new ArrayList<>();
    private CartAdapter cartAdapter = new CartAdapter(cartItems);

    private Double total = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewCartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String cartName = "Seu carrinho #" + getIntent().getStringExtra("cartHistoryItemsSize");

        binding.editTextTextPersonName.setText(cartName);
        binding.textTotal.setText("0.00");
        binding.recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerCart.setAdapter(cartAdapter);
        binding.recyclerCart.setHasFixedSize(true);

        binding.optionBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, NEW_BARCODE_REQUEST);
            }
        });

        binding.optionCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                Integer cardSize = cartItems.size() + 1;
                String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                i.putExtra("cardName", binding.editTextTextPersonName.getText().toString());
                i.putExtra("cardTotal", decimalFormat.format(total));
                i.putExtra("cardSize", cardSize + " produtos");
                i.putExtra("cardDate", "Última modificação: " + date);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == NEW_BARCODE_REQUEST) {
                Intent i = new Intent(ViewCartActivity.this, RegisterProductActivity.class);
                i.putExtra("total", total);
                startActivityForResult(i, NEW_ITEM_REQUEST);
            }

            if (requestCode == NEW_ITEM_REQUEST) {
                //Uri productImageUri = data.getData();
                String productName = data.getStringExtra("productName");
                Double productPrice = data.getDoubleExtra("productPrice", 0);
                Integer productQty = data.getIntExtra("productQty", 1);
                Double total = data.getDoubleExtra("total", 0);

                this.total = total;
                binding.textTotal.setText(decimalFormat.format(total));

                CartItemData newItem = new CartItemData(productName, productPrice, productQty);
                cartItems.add(newItem);

                cartAdapter.notifyItemInserted(cartItems.size()-1);
            }

        }
    }

}