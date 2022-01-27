package com.dispmoveis.compsupermercadosmovel.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityLoginBinding;
import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.dispmoveis.compsupermercadosmovel.ui.createaccount.CreateAccountActivity;
import com.dispmoveis.compsupermercadosmovel.ui.previouscarts.PreviousCartsActivity;
import com.dispmoveis.compsupermercadosmovel.util.Config;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        
        binding.buttonSignIn.setOnClickListener(v -> {
            final String login = binding.editUsername.getText().toString();
            final String password = binding.editPassword.getText().toString();

            ServerClient.login(login, password, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                int resultCode = response.getInt("result_code");
                                if (resultCode == 1) {
                                    JSONArray usuarioJSON = response.getJSONArray("result");
                                    JSONObject itemJSON = usuarioJSON.getJSONObject(0);

                                    Config.setUserId(LoginActivity.this, itemJSON.getInt("id"));
                                    Config.setUserName(LoginActivity.this, itemJSON.getString("nome"));
                                    Config.setLogin(LoginActivity.this, itemJSON.getString("email"));
                                    Config.setPassword(LoginActivity.this, itemJSON.getString("senha"));

                                    Toast.makeText(LoginActivity.this, "Login realizado com sucesso", Toast.LENGTH_LONG).show();

                                    Intent i = new Intent(LoginActivity.this, PreviousCartsActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                                else if (resultCode == 0) {
                                    binding.editPassword.setText("");
                                    Toast.makeText(LoginActivity.this, "Login ou senha incorretos", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "Erro na conexão com o banco.", Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        });
        
        binding.buttonSignUp.setOnClickListener(v -> {
            Intent i = new Intent(LoginActivity.this, CreateAccountActivity.class);
            startActivity(i);
        });

    }


}