package com.dispmoveis.compsupermercadosmovel.ui.productsearch;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.dispmoveis.compsupermercadosmovel.model.SupermarketItem;
import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.dispmoveis.compsupermercadosmovel.util.Util;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ProductSearchViewModel extends ViewModel {

    static public class ProductSearchViewModelFactory implements ViewModelProvider.Factory {

        String supermarketId;

        public ProductSearchViewModelFactory(String supermarketId) {
            this.supermarketId = supermarketId;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //TODO: usar o return comentado quando quando não for mais necessário
            //      executar a activity de forma isolada
            //return (T) new ProductSearchViewModel(supermarketId);
            return (T) new ProductSearchViewModel("1");
        }

    }

    private String supermarketId;
    private MutableLiveData<List<SupermarketItem>> mutableSupermarketItems;

    public ProductSearchViewModel(String supermarketId) {
        this.supermarketId = supermarketId;
    }

    //TODO: remover esse construtor quando não for mais necessário
    //      executar a activity de forma isolada
    public ProductSearchViewModel() {
        this.supermarketId = "1";
    }

    public LiveData<List<SupermarketItem>> getSupermarketItems() {
        if (mutableSupermarketItems == null) {
            mutableSupermarketItems = new MutableLiveData<>();
            loadSupermarketItems();
        }
        return mutableSupermarketItems;
    }

    public void loadSupermarketItems() {
        ServerClient.select("supermarketItems", supermarketId, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int resultCode = response.getInt("result_code");

                    if (resultCode == 1) {
                        JSONArray supermarketItemsJSON = response.getJSONArray("result");

                        List<SupermarketItem> requestedSupermarketItems = new ArrayList<>();
                        for (int i = 0; i < supermarketItemsJSON.length(); i++) {
                            JSONObject itemJSON = supermarketItemsJSON.getJSONObject(i);

                            String id = itemJSON.getString("id");

                            String name = itemJSON.getString("nome");

                            double price = itemJSON.getDouble("preco_atual");

                            String imageUrl = itemJSON.getString("imagem");

                            requestedSupermarketItems.add(
                                    new SupermarketItem(id, name, price, null)
                            );
                        }

                        mutableSupermarketItems.postValue(requestedSupermarketItems);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //TODO: onFailure

        });
    }

    void reloadProductList() {
        loadSupermarketItems();
    }


}
