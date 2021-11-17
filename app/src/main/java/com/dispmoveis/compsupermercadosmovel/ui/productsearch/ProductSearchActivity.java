package com.dispmoveis.compsupermercadosmovel.ui.productsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.dispmoveis.compsupermercadosmovel.R;
import com.dispmoveis.compsupermercadosmovel.model.SupermarketItem;
import com.dispmoveis.compsupermercadosmovel.databinding.ActivityProductSearchBinding;
import com.dispmoveis.compsupermercadosmovel.util.Config;
import com.dispmoveis.compsupermercadosmovel.util.HttpRequest;
import com.dispmoveis.compsupermercadosmovel.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ProductSearchActivity extends AppCompatActivity {

    private String supermarketId;

    private ProductSearchViewModel productSearchViewModel;

    private ActivityProductSearchBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductSearchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //TODO: usar o código comentado quando quando não for mais
        //      necessário executar a activity de forma isolada
        /*
        Intent i = getIntent();
        this.supermarketId = i.getStringExtra("supermarketId");
        */
        this.supermarketId = "1";

        loadSupermarketName();

        productSearchViewModel = new ViewModelProvider(this)
                .get(ProductSearchViewModel.class);

        productSearchViewModel.getSupermarketItems().observe(this, new Observer<List<SupermarketItem>>() {
            @Override
            public void onChanged(List<SupermarketItem> supermarketItems) {
                binding.recyclerProductSearch.setAdapter(
                        new ProductSearchAdapter(ProductSearchActivity.this, supermarketItems)
                );
            }
        });

        float imgSize = getResources().getDimension(R.dimen.productSearchImageSize);
        int numberOfColumns = Util.calculateNoOfColumns(ProductSearchActivity.this, imgSize);
        binding.recyclerProductSearch.setLayoutManager(
                new GridLayoutManager(ProductSearchActivity.this, numberOfColumns)
        );

        binding.recyclerProductSearch.setHasFixedSize(true);
    }

    private void loadSupermarketName() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                HttpRequest httpRequest = new HttpRequest(
                        // TODO: colocar a url da api no config
                        Config.SERVER_URL_BASE + "server_select.php",
                        "GET",
                        "UTF-8"
                );
                httpRequest.addParam("queryType", "supermarketInfo");
                httpRequest.addParam("id", supermarketId);

                try {
                    InputStream inputStream = httpRequest.execute();
                    String resultString = Util.inputStream2String(inputStream, "UTF-8");
                    JSONObject responseJSON = new JSONObject(resultString);
                    httpRequest.finish();

                    int resultCode = responseJSON.getInt("result_code");

                    if (resultCode == 1) {
                        String supermarketName = responseJSON.getJSONArray("result")
                                .getJSONObject(0)
                                .getString("nome");
                        binding.textSelectedSupermarket.setText(supermarketName);
                    }
                }

                catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}