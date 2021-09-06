package com.dispmoveis.compsupermercadosmovel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GetDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_data);

        String login = Config.getLogin(GetDataActivity.this);

        TextView tvWebData = findViewById(R.id.tvWebData);
        tvWebData.setText("Olá " + login);

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.setLogin(GetDataActivity.this, "");
                Config.setPassword(GetDataActivity.this, "");
                Intent i = new Intent(GetDataActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }
}