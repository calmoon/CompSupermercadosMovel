package com.dispmoveis.compsupermercadosmovel;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_product);

        EditText etndPrice = findViewById(R.id.etndPrice);
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
                etnAmount.setText(add.toString());
            }
        });
        btnSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer subtract = Integer.parseInt(etnAmount.getText().toString()) - 1;
                if (subtract > 0) {
                    etnAmount.setText(subtract.toString());
                }
            }
        });
    }
}