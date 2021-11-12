package com.dispmoveis.compsupermercadosmovel;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Toast;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityCreateAccountBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        HttpRequest httpRequest = new HttpRequest(Config.SERVER_URL_BASE + "server_insert.php", "POST", "UTF-8");
                        httpRequest.addParam("table", "usuario");
                        httpRequest.addParam("email", newLogin);
                        httpRequest.addParam("senha", newPassword);

                        try {
                            InputStream is = httpRequest.execute();
                            String result = Util.inputStream2String(is, "UTF-8");
                            httpRequest.finish();

                            JSONObject jsonObject = new JSONObject(result);
                            final int result_code = jsonObject.getInt("result_code");
                            if(result_code == 1) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CreateAccountActivity.this, "Novo usuario registrado com sucesso", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                            }
                            else if (result_code == 0){
                                final String error = jsonObject.getString("result");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CreateAccountActivity.this, error, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else {
                                final String error = jsonObject.getString("result");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CreateAccountActivity.this, error, Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
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