package com.dispmoveis.compsupermercadosmovel.ui.productsearch;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.dispmoveis.compsupermercadosmovel.model.SupermarketItem;
import com.dispmoveis.compsupermercadosmovel.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ProductSearchViewModel extends ViewModel {

    static public class ProductSearchViewModelFactory implements ViewModelProvider.Factory {

        String supermarketId;
        public ProductSearchViewModelFactory(String pid) {
            this.supermarketId = pid;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //TODO: usar o return comentado quando quando não for mais necessário
            //      executar a activity de forma isolada
            //return (T) new ProductSearchViewModel(supermarketId);
            return (T) new ProductSearchViewModel("0");
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
    }

    public LiveData<List<SupermarketItem>> getSupermarketItems() {
        if (mutableSupermarketItems == null) {
            mutableSupermarketItems = new MutableLiveData<>();
            loadSupermarketItems();
        }
        return mutableSupermarketItems;
    }


    //TODO: usar o código comentado para obter do servidor a lista de produtos
    public void loadSupermarketItems() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<SupermarketItem> requestedSupermarketItems = new ArrayList<>();

                requestedSupermarketItems.add(new SupermarketItem(
                        "1",
                        "Leite condensado Nestlé Moça lata 395g",
                        "6,00",
                        Util.getBitmapFromURL("https://docemalu.vteximg.com.br/arquivos/ids/188006-1000-1000/27012-1.jpg?v=637440056389270000")
                ));

                requestedSupermarketItems.add(new SupermarketItem(
                        "2",
                        "Sobremesa Nestlé Moça de Passar Avelã pote 215g",
                        "12,65",
                        Util.getBitmapFromURL("https://www.nestle.com.br/images/default-source/produtos/moca-de-passar-avela.png?sfvrsn=9b164465_6")
                       ));

                requestedSupermarketItems.add(new SupermarketItem(
                        "3",
                        "Sobremesa Nestlé Moça de Colher lata 395g ",
                        "10,99",
                        Util.getBitmapFromURL("https://atacadistasuperadega.vteximg.com.br/arquivos/ids/203488-1000-1000/LEITE_CONDENSADO_MOCA_DE_COLHER_395G.jpg?v=637521246299870000")
                       ));

                mutableSupermarketItems.postValue(requestedSupermarketItems);
            }
        });

        /*
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                List<SupermarketItem> requestedSupermarketItems = new ArrayList<>();

                HttpRequest httpRequest = new HttpRequest(
                        // TODO: colocar a url da api no config
                        "Config.APP_URL" + "server_select.php",
                        "GET",
                        "UTF-8"
                );
                httpRequest.addParam("queryType", "supermarketProductList");
                httpRequest.addParam("id", supermarketId);

                try {
                    InputStream inputStream = httpRequest.execute();
                    String resultString = Util.inputStream2String(inputStream, "UTF-8");
                    JSONObject responseJSON = new JSONObject(resultString);
                    httpRequest.finish();

                    if (responseJSON.getInt("result_code") == 1) {
                        JSONArray productJSONList = responseJSON.getJSONArray("result");

                        for (int i = 0; i < productJSONList.length(); i++) {
                            //TODO: parsear resposta JSON
                            //requestedSupermarketItems.add( new SupermarketItem(id, name, price, image) );
                        }

                        mutableSupermarketItems.postValue(requestedSupermarketItems);
                    }
                }

                catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            }
        });
         */
    }

    void reloadProductList() {
        loadSupermarketItems();
    }


}
