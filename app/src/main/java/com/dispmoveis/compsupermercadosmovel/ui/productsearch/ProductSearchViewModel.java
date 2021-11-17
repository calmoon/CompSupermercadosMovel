package com.dispmoveis.compsupermercadosmovel.ui.productsearch;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.dispmoveis.compsupermercadosmovel.model.SupermarketItem;
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

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                HttpRequest httpRequest = new HttpRequest(
                        Config.SERVER_URL_BASE + "server_select.php",
                        "GET",
                        "UTF-8"
                );
                httpRequest.addParam("queryType", "supermarketItems");
                httpRequest.addParam("id", supermarketId);

                try {
                    InputStream inputStream = httpRequest.execute();
                    String resultString = Util.inputStream2String(inputStream, "UTF-8");
                    JSONObject responseJSON = new JSONObject(resultString);
                    httpRequest.finish();

                    int resultCode = responseJSON.getInt("result_code");

                    if (resultCode == 0 || resultCode == -1) {
                        // TODO: tratar caso dar erro ou ter nenhum resultado
                    }

                    else if (resultCode == 1) {
                        JSONArray supermarketItemsJSON = responseJSON.getJSONArray("result");

                        List<SupermarketItem> requestedSupermarketItems = new ArrayList<>();

                        for (int i = 0; i < supermarketItemsJSON.length(); i++) {
                            JSONObject itemJSON = supermarketItemsJSON.getJSONObject(i);

                            String id = itemJSON.getString("id");
                            String name = itemJSON.getString("nome");

                            double priceVal = itemJSON.getDouble("preco_atual");
                            String price = new DecimalFormat("#.##").format(priceVal);

                            String imageBase64 = itemJSON.getString("imagem");
                            imageBase64 = imageBase64.substring(imageBase64.indexOf(",") + 1);
                            Bitmap image = Util.base642Bitmap(imageBase64);

                            requestedSupermarketItems.add(
                                    new SupermarketItem(id, name, price, image)
                            );
                        }

                        mutableSupermarketItems.postValue(requestedSupermarketItems);
                    }
                }

                catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    void reloadProductList() {
        loadSupermarketItems();
    }


}
