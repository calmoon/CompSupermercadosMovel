package com.dispmoveis.compsupermercadosmovel.ui.supermarket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivitySupermarketBinding;
import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SupermarketActivity extends AppCompatActivity {
    private ActivitySupermarketBinding binding;

    private FusedLocationProviderClient client;
    private String deviceLocation;

    private int id;
    private String nome;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySupermarketBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        client = LocationServices.getFusedLocationProviderClient(this);
        client.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        deviceLocation = "(" + location.getLatitude() + "," +
                                location.getLongitude() + ")";

                        ServerClient.select("nearestSupermarkets", deviceLocation, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                try {
                                    int resultCode = response.getInt("result_code");

                                    if (resultCode == 1) {
                                        JSONArray supermarketsJSON = response.getJSONArray("result");
                                        JSONObject itemJSON = supermarketsJSON.getJSONObject(0);
                                        id = itemJSON.getInt("id");
                                        nome = itemJSON.getString("nome");
                                        binding.editSupermarketName.setText(nome);
                                    }
                                    else if (resultCode == 0) {
                                        Toast.makeText(SupermarketActivity.this,
                                                "Não há supermercados próximos registrados.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(SupermarketActivity.this,
                                                "não foi possivel consultar", Toast.LENGTH_LONG).show();
                                        Log.e("Super", "Erro de conssulta - " + response.toString());
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                });

        binding.buttonSaveSupermarket.setOnClickListener(v -> {
            if (binding.editSupermarketName.getText().toString() == nome) {
                Intent i = new Intent();
                i.putExtra("supermarketID", id);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
            /*else {
                RequestParams values = new RequestParams();
                values.put("nome", binding.editSupermarketName.getText().toString());
                values.put("localizacao", deviceLocation);
                ServerClient.insert("supermercado", values, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            int resultCode = response.getInt("result_code");
                            if (resultCode == 1) {
                                ServerClient.select();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }*/
        });

        binding.buttonCancelSupermarket.setOnClickListener(v -> {
            Intent i = new Intent();
            setResult(Activity.RESULT_CANCELED, i);
            finish();
        });
    }
}