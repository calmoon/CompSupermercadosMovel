package com.dispmoveis.compsupermercadosmovel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Toast;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityCreateAccountBinding;


public class CreateAccountActivity extends AppCompatActivity {

    private ActivityCreateAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        
        binding.buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newLogin = binding.editSignupUser.getText().toString();
                if(newLogin.isEmpty()) {
                    Toast.makeText(CreateAccountActivity.this, "Campo de login não preenchido", Toast.LENGTH_LONG).show();
                    return;
                }
                
                final String newPassword = binding.editSignupPass.getText().toString();
                if(newPassword.isEmpty()) {
                    Toast.makeText(CreateAccountActivity.this, "Campo de senha não preenchido", Toast.LENGTH_LONG).show();
                    return;
                }
                
                String newPasswordCheck = binding.editPasswordCheck.getText().toString();
                if(newPasswordCheck.isEmpty()) {
                    Toast.makeText(CreateAccountActivity.this, "Campo de checagem de senha não preenchido", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!newPassword.equals(newPasswordCheck)) {
                    Toast.makeText(CreateAccountActivity.this, "Senha não confere", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(CreateAccountActivity.this, "Novo usuario registrado com sucesso", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        binding.buttonCancelAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(Activity.RESULT_CANCELED, i);
                finish();
            }
        });
    }


}