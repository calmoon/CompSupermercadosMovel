package com.dispmoveis.compsupermercadosmovel.ui.createaccount;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityCreateAccountBinding;
import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class CreateAccountActivity extends AppCompatActivity {

    private ActivityCreateAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        
        binding.buttonCreateAccount.setOnClickListener(v -> {
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

            RequestParams params = new RequestParams();
            params.put("email", newLogin);
            params.put("senha", newPassword);
            ServerClient.insert("usuario", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        int resultCode = response.getInt("result_code");
                        if (resultCode == 1) {
                            Toast.makeText(CreateAccountActivity.this, "Novo usuário registrado com sucesso.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {
                            Toast.makeText(CreateAccountActivity.this, "Erro ao criar novo usuário", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });

        binding.buttonCancelAccount.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });
    }


}