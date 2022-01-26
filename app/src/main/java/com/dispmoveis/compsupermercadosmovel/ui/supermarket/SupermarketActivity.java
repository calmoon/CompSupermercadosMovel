package com.dispmoveis.compsupermercadosmovel.ui.supermarket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivitySupermarketBinding;
import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SupermarketActivity extends AppCompatActivity {
    private ActivitySupermarketBinding binding;

    private FusedLocationProviderClient client;
    private String deviceLocation;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySupermarketBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        client = LocationServices.getFusedLocationProviderClient(this);
        CancellationTokenSource source = new CancellationTokenSource();
        client.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, source.getToken())
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
                                        List<SupermarketsData> supermarketsDataList = new ArrayList<>();

                                        JSONArray supermarketsJSON = response.getJSONArray("result");
                                        for (int i = 0; i < supermarketsJSON.length(); i++) {
                                            JSONObject itemJSON = supermarketsJSON.getJSONObject(i);

                                            SupermarketsData supermarketsData = new SupermarketsData();
                                            supermarketsData.id = itemJSON.getInt("id");
                                            supermarketsData.nome = itemJSON.getString("nome");

                                            supermarketsDataList.add(supermarketsData);
                                        }
                                        SupermarketAdapter supermarketAdapter =
                                                new SupermarketAdapter(SupermarketActivity.this, supermarketsDataList);
                                        binding.recyclerSupermarkets.setLayoutManager(new LinearLayoutManager(SupermarketActivity.this));
                                        binding.recyclerSupermarkets.setAdapter(supermarketAdapter);
                                        binding.recyclerSupermarkets.setVisibility(View.VISIBLE);

                                    }
                                    else if (resultCode == 0) {
                                        binding.recyclerSupermarkets.setVisibility(View.GONE);
                                        Toast.makeText(SupermarketActivity.this,
                                                "Não há supermercados próximos registrados.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Toast.makeText(SupermarketActivity.this,
                                                "Erro ao fazer consulta.", Toast.LENGTH_LONG).show();
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
            RequestParams values = new RequestParams();
            values.put("nome", binding.editSupermarketName.getText().toString());
            values.put("localizacao", deviceLocation);
            ServerClient.insert("supermercado", values, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        int resultCode = response.getInt("result_code");
                        if (resultCode == 1) {
                            ServerClient.select("lastId", "supermercado", new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        int resultCode = response.getInt("result_code");

                                        if (resultCode == 1) {
                                            JSONArray supermarketIdJSON = response.getJSONArray("result");
                                            JSONObject itemJSON = supermarketIdJSON.getJSONObject(0);

                                            Intent i = new Intent();
                                            i.putExtra("supermarketId", itemJSON.getInt("max"));
                                            setResult(Activity.RESULT_OK, i);
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(SupermarketActivity.this,
                                                    "Erro ao fazer consulta.", Toast.LENGTH_LONG).show();
                                            Log.e("Super", "Erro de conssulta - " + response.toString());
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(SupermarketActivity.this,
                                    "Erro ao fazer inserção.", Toast.LENGTH_LONG).show();
                            Log.e("Super", "Erro de conssulta - " + response.toString());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        });

        binding.buttonCancelSupermarket.setOnClickListener(v -> {
            Intent i = new Intent();
            setResult(Activity.RESULT_CANCELED, i);
            finish();
        });
    }
}