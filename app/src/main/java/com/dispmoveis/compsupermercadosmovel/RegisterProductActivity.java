package com.dispmoveis.compsupermercadosmovel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class RegisterProductActivity extends AppCompatActivity {

    static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_product);

        EditText etndPrice = findViewById(R.id.etndPrice);
        etndPrice.setText("0.00");
        EditText etnAmount = findViewById(R.id.etnAmount);
        etnAmount.setText("1");

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnSubtract = findViewById(R.id.btnSubtract);

        TextView tvProductTotal = findViewById(R.id.tvProductTotal);
        TextView tvCartTotal = findViewById(R.id.tvCartTotal);

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
        TextView tvProductTotal = findViewById(R.id.tvProductTotal);
        //TextView tvCartTotal = findViewById(R.id.tvCartTotal);
        EditText etndPrice = findViewById(R.id.etndPrice);
        EditText etnAmount = findViewById(R.id.etnAmount);

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
        EditText etnAmount = findViewById(R.id.etnAmount);
        Integer result = Integer.parseInt(etnAmount.getText().toString()) + num;
        if (result > 0) {
            etnAmount.setText(result.toString());
        }
    }

}