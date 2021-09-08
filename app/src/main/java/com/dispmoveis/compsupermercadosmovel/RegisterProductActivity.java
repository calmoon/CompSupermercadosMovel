package com.dispmoveis.compsupermercadosmovel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityRegisterProductBinding;

import java.text.DecimalFormat;

public class RegisterProductActivity extends AppCompatActivity {

    static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private ActivityRegisterProductBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.editProductPrice.setText("0.00");
        binding.editProductQty.setText("1");

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
    }

    private void updateTotals() {
        Double price;
        try {
            price = Double.parseDouble(binding.editProductPrice.getText().toString());
        } catch (NumberFormatException e) {
            price = 0.0;
        }

        Integer amount;
        try {
            amount = Integer.parseInt(binding.editProductQty.getText().toString());
        } catch (NumberFormatException e) {
            amount = 0;
        }

        Double totalPrice = price * amount;

        String productTotal = "Total (produto x" + amount.toString() + "): R$ " +
                decimalFormat.format(totalPrice);
        binding.textProductTotal.setText(productTotal);

        //String cartTotal = ""
        //binding.textCartTotal.setText(cartTotal);
    }

    private void addToQuantity(Integer num) {
        Integer result = Integer.parseInt(binding.editProductQty.getText().toString()) + num;
        if (result > 0) {
            binding.editProductQty.setText(result.toString());
        }
    }

}