package com.dispmoveis.compsupermercadosmovel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

public class RegisterProductActivity extends AppCompatActivity {

    static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_product);

        EditText etndPrice = findViewById(R.id.edit_product_price);
        etndPrice.setText("0.00");
        EditText etnAmount = findViewById(R.id.edit_product_amount);
        etnAmount.setText("1");

        Button btnAdd = findViewById(R.id.button_add);
        Button btnSubtract = findViewById(R.id.button_subtract);

        TextView tvProductTotal = findViewById(R.id.text_product_total);
        TextView tvCartTotal = findViewById(R.id.text_cart_total);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToAmount(1);
                updateTotals();
            }
        });

        btnSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToAmount(-1);
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

        etndPrice.addTextChangedListener(textWatcher);
        etnAmount.addTextChangedListener(textWatcher);
    }

    private void updateTotals() {
        TextView tvProductTotal = findViewById(R.id.text_product_total);
        //TextView tvCartTotal = findViewById(R.id.tvCartTotal);
        EditText etndPrice = findViewById(R.id.edit_product_price);
        EditText etnAmount = findViewById(R.id.edit_product_amount);

        Double price;
        try {
            price = Double.parseDouble(etndPrice.getText().toString());
        } catch (NumberFormatException e) {
            price = 0.0;
        }

        Integer amount;
        try {
            amount = Integer.parseInt(etnAmount.getText().toString());
        } catch (NumberFormatException e) {
            amount = 0;
        }

        Double totalPrice = price * amount;

        String productTotal = "Total (produto x" + amount.toString() + "): R$ " +
                decimalFormat.format(totalPrice);
        tvProductTotal.setText(productTotal);

        //tvCartTotal.setText(cartTotal);
    }

    private void addToAmount(Integer num) {
        EditText etnAmount = findViewById(R.id.edit_product_amount);
        Integer result = Integer.parseInt(etnAmount.getText().toString()) + num;
        if (result > 0) {
            etnAmount.setText(result.toString());
        }
    }

}