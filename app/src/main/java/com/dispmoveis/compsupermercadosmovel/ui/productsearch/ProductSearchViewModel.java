package com.dispmoveis.compsupermercadosmovel.ui.productsearch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dispmoveis.compsupermercadosmovel.model.SupermarketItem;
import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ProductSearchViewModel extends ViewModel {

    private String supermarketId;
    private MutableLiveData<List<SupermarketItem>> mutableSupermarketItems;

    private Boolean sortAscending = false;
    private final Comparator<SupermarketItem> compareByPriceAscending =
            (SupermarketItem item1, SupermarketItem item2) -> item1.getPrice().compareTo(item2.getPrice());

    public void setSupermarketId(String supermarketId) {
        this.supermarketId = supermarketId;
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
                            String imageUrl = itemJSON.getString("imagem_url");

                            requestedSupermarketItems.add(
                                    new SupermarketItem(id, name, price, imageUrl)
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

    boolean toggleSortOrder() {
        sortAscending = !sortAscending;
        if (sortAscending)
            Collections.sort(mutableSupermarketItems.getValue(), compareByPriceAscending);
        else
            Collections.sort(mutableSupermarketItems.getValue(), Collections.reverseOrder(compareByPriceAscending));
        mutableSupermarketItems.postValue(mutableSupermarketItems.getValue());
        return sortAscending;
    }

    void reloadProductList() {
        loadSupermarketItems();
    }


}
