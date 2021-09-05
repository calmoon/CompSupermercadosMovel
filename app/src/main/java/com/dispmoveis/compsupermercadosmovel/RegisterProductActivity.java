package com.dispmoveis.compsupermercadosmovel;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
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
                Integer add = Integer.parseInt(etnAmount.getText().toString()) + 1;
                Double price = Double.parseDouble(etndPrice.getText().toString());
                Double totalPrice = price * add;
                String ProductTotal = "Total (produto x" + add.toString() + "): R$ " +
                        decimalFormat.format(totalPrice);
                etnAmount.setText(add.toString());
                if (totalPrice > 0){
                    tvProductTotal.setText(ProductTotal);
                }
            }
        });

        btnSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer subtract = Integer.parseInt(etnAmount.getText().toString()) - 1;
                if (subtract > 0) {
                    Double price = Double.parseDouble(etndPrice.getText().toString());
                    Double totalPrice = price * subtract;
                    String ProductTotal = "Total (produto x" + subtract.toString() + "): R$ " +
                            decimalFormat.format(totalPrice);
                    etnAmount.setText(subtract.toString());
                    if (totalPrice > 0){
                        tvProductTotal.setText(ProductTotal);
                    }
                }
            }
        });

        etndPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Double price = Double.parseDouble(etndPrice.getText().toString());
                Integer amount = Integer.parseInt(etnAmount.getText().toString());
                Double totalPrice = price * amount;
                String ProductTotal = "Total (produto x" + amount.toString() + "): R$ " +
                        decimalFormat.format(totalPrice);
                if (totalPrice > 0){
                    tvProductTotal.setText(ProductTotal);
                }
            }
        });
    }
}