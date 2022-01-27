package com.dispmoveis.compsupermercadosmovel.ui.previouscarts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PreviousCartsViewModel extends ViewModel {

    private String userId;
    private MutableLiveData<List<PreviousCartsItem>> mutablePreviousCartsItems;

    public void setUserId(String userId) { this.userId = userId; }

    public LiveData<List<PreviousCartsItem>> getPreviousCartsItems() {
        if (mutablePreviousCartsItems == null) {
            mutablePreviousCartsItems = new MutableLiveData<>();
            loadPreviousCartsItems();
        }
        return mutablePreviousCartsItems;
    }

    private void loadPreviousCartsItems() {
        ServerClient.select("cartsHistory", userId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int resultCode = response.getInt("result_code");

                    if (resultCode == 0) {
                        mutablePreviousCartsItems.postValue(new ArrayList<>());
                    }

                    else if (resultCode == 1) {
                        JSONArray previousCartsItemsJSON = response.getJSONArray("result");

                        List<PreviousCartsItem> requestedPreviousCartsItems = new ArrayList<>();
                        for (int i = 0; i < previousCartsItemsJSON.length(); i++) {
                            JSONObject itemJSON = previousCartsItemsJSON.getJSONObject(i);

                            int id = itemJSON.getInt("id");
                            String name = itemJSON.getString("nome");
                            String supermarketName = itemJSON.getString("nome_supermercado");
                            String date = itemJSON.getString("data");
                            int  qtdItems = itemJSON.getInt("qtd_itens");
                            double total = itemJSON.getDouble("total");

                            requestedPreviousCartsItems.add(
                                    new PreviousCartsItem(id, name, supermarketName, date, qtdItems, total)
                            );
                        }

                        mutablePreviousCartsItems.postValue(requestedPreviousCartsItems);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void reloadCartList() { loadPreviousCartsItems(); }
}
