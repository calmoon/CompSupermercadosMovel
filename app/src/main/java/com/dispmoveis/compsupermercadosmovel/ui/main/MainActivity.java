package com.dispmoveis.compsupermercadosmovel.ui.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.dispmoveis.compsupermercadosmovel.ui.login.LoginActivity;
import com.dispmoveis.compsupermercadosmovel.ui.previouscarts.PreviousCartsActivity;
import com.dispmoveis.compsupermercadosmovel.util.Config;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startApp();
    }

    private void startApp(){
        ServerClient.login(Config.getLogin(MainActivity.this), Config.getPassword(MainActivity.this),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            int resultCode = response.getInt("result_code");
                            if (resultCode == 1) {
                                JSONArray usuarioJSON = response.getJSONArray("result");
                                JSONObject itemJSON = usuarioJSON.getJSONObject(0);

                                Config.setUserId(MainActivity.this, itemJSON.getInt("id"));
                                Config.setLogin(MainActivity.this, itemJSON.getString("email"));
                                Config.setPassword(MainActivity.this, itemJSON.getString("senha"));

                                Intent i = new Intent(MainActivity.this, PreviousCartsActivity.class);
                                startActivity(i);
                            }
                            else {
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(i);
                            }
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}