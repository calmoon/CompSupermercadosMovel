package com.dispmoveis.compsupermercadosmovel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.button_signin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etLogin = findViewById(R.id.edit_username);
                final String login = etLogin.getText().toString();

                EditText etPassword = findViewById(R.id.edit_password);
                final String password = etPassword.getText().toString();

                Config.setLogin(LoginActivity.this, login);
                Config.setPassword(LoginActivity.this, password);
                Toast.makeText(LoginActivity.this, "Login realizado com sucesso", Toast.LENGTH_LONG).show();
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(i);

            }
        });

        Button btnRegisterNewUser = findViewById(R.id.button_signup);
        btnRegisterNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });
    }
}