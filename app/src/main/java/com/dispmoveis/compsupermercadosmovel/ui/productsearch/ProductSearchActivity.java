package com.dispmoveis.compsupermercadosmovel.ui.productsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.dispmoveis.compsupermercadosmovel.R;
import com.dispmoveis.compsupermercadosmovel.model.SupermarketItem;
import com.dispmoveis.compsupermercadosmovel.databinding.ActivityProductSearchBinding;
import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.dispmoveis.compsupermercadosmovel.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ProductSearchActivity extends AppCompatActivity {

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
        String supermarketId = getIntent().getStringExtra("supermarketId");
        loadSupermarketName(supermarketId);
        */
        loadSupermarketName("1");

        ProductSearchViewModel productSearchViewModel = new ViewModelProvider(this)
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

    private void loadSupermarketName(String supermarketId) {
        ServerClient.select("supermarketInfo", supermarketId, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int resultCode = response.getInt("result_code");

                    if (resultCode == 1) {
                        String supermarketName = response.getJSONArray("result")
                                .getJSONObject(0)
                                .getString("nome");
                        binding.textSelectedSupermarket.setText(supermarketName);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            //TODO: onFailure

        });
    }

}