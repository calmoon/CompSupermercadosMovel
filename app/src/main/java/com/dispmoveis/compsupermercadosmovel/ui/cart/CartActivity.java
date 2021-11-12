package com.dispmoveis.compsupermercadosmovel.ui.cart;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dispmoveis.compsupermercadosmovel.ui.registerproduct.RegisterProductActivity;
import com.dispmoveis.compsupermercadosmovel.databinding.ActivityCartBinding;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    static int NEW_BARCODE_REQUEST = 1;
    static int NEW_ITEM_REQUEST = 2;

    private ActivityCartBinding binding;

    private List<CartItemData> cartItems = new ArrayList<>();
    private CartAdapter cartAdapter = new CartAdapter(cartItems);

    private Double total = 0.0;

    // Register the launcher and result handler
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Toast.makeText(CartActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CartActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String cartName = "Seu carrinho #" + getIntent().getStringExtra("cartHistoryItemsSize");
        binding.editCartName.setText(cartName);

        binding.textTotal.setText("Total:\nR$ 0.00");

        binding.recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerCart.setAdapter(cartAdapter);
        binding.recyclerCart.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.recyclerCart.getContext(), DividerItemDecoration.VERTICAL);
        binding.recyclerCart.addItemDecoration(dividerItemDecoration);

        binding.recyclerCart.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0){
                    binding.buttonAddProduct.hide();
                } else{
                    binding.buttonAddProduct.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        // Register the launcher and result handler

        binding.optionBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanOptions options = new ScanOptions();
                options.setDesiredBarcodeFormats(ScanOptions.EAN_13);
                options.setPrompt("Scanning");
                options.setOrientationLocked(false);
                barcodeLauncher.launch(options);
            }
        });

        binding.optionCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CartActivity.this, RegisterProductActivity.class);
                i.putExtra("total", total);
                startActivityForResult(i, NEW_ITEM_REQUEST);
            }
        });

        binding.buttonSaveCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                Integer cardSize = cartItems.size();
                String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                i.putExtra("cardName", binding.editCartName.getText().toString());
                i.putExtra("cardTotal", decimalFormat.format(total));
                i.putExtra("cardSize", cardSize + " produtos");
                i.putExtra("cardDate", "Última modificação: " + date);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });

        binding.buttonCancelCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(Activity.RESULT_CANCELED, i);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == NEW_BARCODE_REQUEST) {
                Intent i = new Intent(CartActivity.this, RegisterProductActivity.class);
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
                binding.textTotal.setText("Total:\nR$ " + decimalFormat.format(total));

                CartItemData newItem = new CartItemData(productName, productPrice, productQty);
                cartItems.add(newItem);

                cartAdapter.notifyItemInserted(cartItems.size()-1);
            }

        }
    }

}