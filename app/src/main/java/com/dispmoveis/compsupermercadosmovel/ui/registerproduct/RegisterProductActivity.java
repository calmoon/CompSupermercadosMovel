package com.dispmoveis.compsupermercadosmovel.ui.registerproduct;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityRegisterProductBinding;

import java.text.DecimalFormat;

public class RegisterProductActivity extends AppCompatActivity {

    static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private ActivityRegisterProductBinding binding;

    private Double total;
    private Double productPrice;
    private String productName;
    private Integer productQty;

    private boolean checkEmptyFields() {
        productName = binding.editProductName.getText().toString();
        if (productName.isEmpty()) {
            return true;
        }
        if (productPrice == 0) {
            return true;
        }
        total = productPrice + total;
        return false;
    }

    private void updateTotals() {
        Double price;
        try {
            price = Double.parseDouble(binding.editProductPrice.getText().toString());
        } catch (NumberFormatException e) {
            price = 0.0;
        }
        try {
            productQty = Integer.parseInt(binding.editProductQty.getText().toString());
        } catch (NumberFormatException e) {
            productQty = 0;
        }

        productPrice = price * productQty;
        Double totalF = productPrice + total;

        String productTotal = "Total (produto x" + productQty.toString() + "): R$ " +
                decimalFormat.format(productPrice);
        binding.textProductTotal.setText(productTotal);

        String cartTotal = "No seu carrinho: R$ " + decimalFormat.format(totalF);
        binding.textPreviewCartTotal.setText(cartTotal);
    }

    private void addToQuantity(Integer num) {
        Integer result = Integer.parseInt(binding.editProductQty.getText().toString()) + num;
        if (result > 0) {
            binding.editProductQty.setText(result.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        total = getIntent().getDoubleExtra("total", 0.0);
        String cartTotal = "No seu carrinho: R$ " + decimalFormat.format(total);
        binding.textPreviewCartTotal.setText(cartTotal);

        binding.editProductPrice.setText("0.00");

        binding.editProductQty.setText("1");

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateTotals();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        binding.editProductPrice.addTextChangedListener(textWatcher);
        binding.editProductQty.addTextChangedListener(textWatcher);

        binding.buttonProductQtyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToQuantity(1);
                updateTotals();
            }
        });

        binding.buttonProductQtySub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToQuantity(-1);
                updateTotals();
            }
        });

        binding.buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmptyFields()){
                    return;
                }
                Intent i = new Intent();
                i.putExtra("productName", productName);
                i.putExtra("productPrice", productPrice);
                i.putExtra("productQty", productQty);
                i.putExtra("total", total);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });

        binding.buttonCancelProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(Activity.RESULT_CANCELED, i);
                finish();
            }
        });
    }

}