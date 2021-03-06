package com.dispmoveis.compsupermercadosmovel.ui.cart;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.R;
import com.dispmoveis.compsupermercadosmovel.databinding.ActivityCartBinding;
import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.dispmoveis.compsupermercadosmovel.ui.productsearch.ProductSearchActivity;
import com.dispmoveis.compsupermercadosmovel.ui.registerproduct.RegisterProductActivity;
import com.dispmoveis.compsupermercadosmovel.util.Config;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.permissionx.guolindev.PermissionX;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class CartActivity extends AppCompatActivity {
    // Received from PreviousCartsActivity:
    public static final String EXTRA_CART_ID = "CartActivity_cartId";
    // Received from RegisterProductActivity (addProductLauncher):
    public static final String EXTRA_NEW_ITEM_ID = "CartActivity_itemId";
    public static final String EXTRA_NEW_ITEM_QTY = "CartActivity_itemQty";

    // DB Stuff
    private String cartId;
    private String supermarketId;
    private final List<String> dbCartItemIds = new ArrayList<>();

    // Activity stuff
    private Double total = 0.0;
    private final CartAdapter cartAdapter = new CartAdapter(this);

    private ActivityCartBinding binding;

    private final ActivityResultLauncher<Intent> addProductLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    assert data != null;
                    String itemId = data.getStringExtra(EXTRA_NEW_ITEM_ID);
                    int itemQty = data.getIntExtra(EXTRA_NEW_ITEM_QTY, 1);

                    if (!cartAdapter.containsItem(itemId) && itemId != null)
                        addItemToCart(itemId, itemQty);
                    else
                        Toast.makeText(CartActivity.this,
                                "Este item j?? est?? no carrinho!",
                                Toast.LENGTH_LONG).show();

                }
            }
        );

    // Register the launcher and result handler
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                String barcode = result.getContents();

                if(barcode == null) {
                    Toast.makeText(CartActivity.this,
                            "Falha ao escanear.",
                            Toast.LENGTH_LONG).show();
                }

                else {
                    Toast.makeText(CartActivity.this,
                            "Escaneado: " + barcode,
                            Toast.LENGTH_LONG).show();

                    ServerClient.bluesoftProductInfo(barcode, supermarketId, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                int resultCode = response.getInt("result_code");

                                if (resultCode == 1) {
                                    String itemId = response.getJSONObject("result")
                                            .getString("itemId");
                                    Intent i = new Intent(CartActivity.this, RegisterProductActivity.class)
                                            .putExtra(RegisterProductActivity.EXTRA_CURRENT_CART_TOTAL, total)
                                            .putExtra(RegisterProductActivity.EXTRA_SELECTED_ITEM_ID, itemId);
                                    addProductLauncher.launch(i);
                                }
                                else {
                                    onFailure(statusCode, headers, response.toString(), new InternalError());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Log.e("HTTP_BARCODE_SCAN_FAIL", "Barcode scan response error - " + responseString);
                            Toast.makeText(CartActivity.this,
                                    "Falha na resposta do servidor ao escanear.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent i = getIntent();
        cartId = i.getStringExtra(EXTRA_CART_ID);

        if (cartId != null) {
            loadCart(cartId);
        }

        binding.textTotal.setText("Total:\nR$ " + Config.getCurrencyFormat().format(total));

        binding.recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerCart.setAdapter(cartAdapter);
        binding.recyclerCart.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.recyclerCart.getContext(), DividerItemDecoration.VERTICAL);
        binding.recyclerCart.addItemDecoration(dividerItemDecoration);

        binding.recyclerCart.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0){
                    binding.buttonAddItem.hide();
                } else{
                    binding.buttonAddItem.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        binding.buttonSaveCart.setOnClickListener(v -> submitCart());

        binding.buttonCancelCart.setOnClickListener(v -> showDiscardConfirmationAlert());

        binding.buttonOptionCatalog.setOnClickListener(v -> {
            addProductLauncher.launch(
                    new Intent(CartActivity.this, ProductSearchActivity.class)
                            .putExtra(RegisterProductActivity.EXTRA_CURRENT_CART_TOTAL, total)
                            .putExtra(ProductSearchActivity.EXTRA_SUPERMARKET_ID, supermarketId)
            );
        });

        // Register the launcher and result handler
        binding.buttonOptionBarcode.setOnClickListener(v -> {
            PermissionX.init(this)
                    .permissions(Manifest.permission.CAMERA)
                    .onExplainRequestReason((scope, deniedList) ->
                            scope.showRequestReasonDialog(deniedList, "Para usar essa funcionalidade " +
                                    "?? preciso conceder a permiss??o ao app.", "OK")
                    )
                    .onForwardToSettings((scope, deniedList) ->
                            scope.showForwardToSettingsDialog(deniedList, "Voc?? precisa conceder" +
                                    "essa permiss??o manualmente.", "OK", "Cancel")
                    )
                    .request( (allGranted, grantedList, deniedList) ->
                            {
                                if (allGranted) {
                                    ScanOptions options = new ScanOptions()
                                            .setDesiredBarcodeFormats(ScanOptions.EAN_13)
                                            .setPrompt("Aponte para um c??digo de barras")
                                            .setOrientationLocked(false)
                                            .setBeepEnabled(false);
                                    barcodeLauncher.launch(options);
                                }
                                else {
                                    Toast.makeText(this, "N??o ?? poss??vel acessar a " +
                                            "funcionalidade sem dar acesso a permiss??o", Toast.LENGTH_LONG).show();
                                }
                            }
                    );
        });
    }

    @Override
    public void onBackPressed() {
        showDiscardConfirmationAlert();
    }

    private void showDiscardConfirmationAlert() {
        new AlertDialog.Builder(this)
            .setIcon(ContextCompat.getDrawable(this, R.drawable.ic_close))
            .setTitle("Descartar altera????es?")
            .setMessage("Se tiver feito altera????es, elas ser??o perdidas. Tem certeza?")
            .setPositiveButton("Descartar", (dialog, which) -> {
                setResult(Activity.RESULT_CANCELED);
                finish();
            })
            .setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel())
            .show();
    }

    void reflectItemQtyChange(Double oldItemPrice, Double newItemPrice) {
        total -= oldItemPrice;
        total += newItemPrice;
        binding.textTotal.setText("Total:\nR$ " + Config.getCurrencyFormat().format(total));
    }

    private void loadCart(@NonNull String cartId) {
        // Load cart info:
        ServerClient.select("cartInfo", cartId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getInt("result_code") == 1) {
                        JSONObject cartInfoJSON = response.getJSONArray("result").getJSONObject(0);
                        CartActivity.this.supermarketId = cartInfoJSON.getString("id_supermercado");
                        binding.editCartName.setText( cartInfoJSON.getString("nome") );
                    } else
                        onFailure(statusCode, headers, response.toString(), new InternalError());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("CART_LOAD_INFO", "Failed to load cart info - " + responseString);
            }
        });

        // Load cart items:
        ServerClient.select("cartItems", cartId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int resultCode = response.getInt("result_code");
                    if (resultCode == 1) {
                        JSONArray supermarketItemsJSON = response.getJSONArray("result");

                        for (int i = 0; i < supermarketItemsJSON.length(); i++) {
                            JSONObject itemJSON = supermarketItemsJSON.getJSONObject(i);

                            String itemId = itemJSON.getString("id");
                            String productName = itemJSON.getString("nome");
                            Double itemPrice = itemJSON.getDouble("preco_atual");
                            String imageUrl = itemJSON.getString("imagem_url");
                            int itemQty = itemJSON.getInt("quantidade");

                            total += itemPrice * itemQty;
                            if (i == supermarketItemsJSON.length()-1)
                                binding.textTotal.setText("Total:\nR$ " + Config.getCurrencyFormat().format(total));

                            dbCartItemIds.add(itemId);

                            cartAdapter.additem( new CartItemData(itemId, productName, itemPrice, itemQty, imageUrl) );
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("CART_LOAD_ITEMS", "Failed to load cart items - " + responseString);
            }
        });
    }

    private void addItemToCart(@NonNull String supermarketItemId, int itemQty) {
        ServerClient.select("itemInfo", supermarketItemId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int resultCode = response.getInt("result_code");

                    if (resultCode == 1) {
                        JSONObject itemJSON = response.getJSONArray("result").getJSONObject(0);

                        String productName = itemJSON.getString("nome");
                        Double itemPrice = itemJSON.getDouble("preco_atual");
                        String imageUrl = itemJSON.getString("imagem_url");

                        total += itemPrice * itemQty;

                        binding.textTotal.setText("Total:\nR$ " + Config.getCurrencyFormat().format(total));

                        cartAdapter.additem( new CartItemData(supermarketItemId, productName, itemPrice, itemQty, imageUrl) );
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //TODO: onFailure
        });
    }

    private void submitCart() {
        List<CartItemData> submittedItems = cartAdapter.getCartItems();
        List<String> deletedItemIds = cartAdapter.getRemovedItemIds();

        // Delete removed items
        for (String removedId : deletedItemIds) {
            Map<String, String> whereConditions = new HashMap<>();
            whereConditions.put("id_carrinho", cartId);
            whereConditions.put("id_item", removedId);
            ServerClient.delete("carrinho_item", whereConditions, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getInt("result_code") <= 0)
                            onFailure(statusCode, headers, response.toString(), new InternalError());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("CART_REMOVE_ITEM_FAIL", "Failed to update item quantity - " + responseString);
                }
            });
        }

        // Per-item changes
        for (CartItemData item : submittedItems) {
            String itemId = item.getId();
            RequestParams values = new RequestParams();

            // If item already in DB cart, update:
            if (dbCartItemIds.contains(itemId)) {
                Map<String, String> whereConditions = new HashMap<>();
                whereConditions.put("id_carrinho", cartId);
                whereConditions.put("id_item", itemId);

                values.put("quantidade", item.getQuantity());

                ServerClient.update("carrinho_item", whereConditions, values, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getInt("result_code") <= 0)
                                onFailure(statusCode, headers, response.toString(), new InternalError());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("CART_QTY_UPDATE_FAIL", "Failed to update item quantity - " + responseString);
                    }
                });
            }
            // Else if item not in DB cart, insert:
            else {
                values.put("id_carrinho", cartId);
                values.put("id_item", item.getId());
                values.put("quantidade", item.getQuantity());
                ServerClient.insert("carrinho_item", values, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            if (response.getInt("result_code") <= 0)
                                onFailure(statusCode, headers, response.toString(), new InternalError());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("CART_ITEM_INSERT_FAIL", "Failed to insert cart item - " + responseString);
                    }
                });
            }

        }

        // Update cart title/date
        String submittedCartTitle = binding.editCartName.getText().toString();

        RequestParams values = new RequestParams();
        values.put("nome", submittedCartTitle);
        values.put("data", "now()");

        ServerClient.update("carrinho", cartId, values, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getInt("result_code") <= 0)
                        onFailure(statusCode, headers, response.toString(), new InternalError());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("CART_TITLE_UPDATE_FAIL", "Failed to update cart title - " + responseString);
            }
        });

        // Finish activty
        setResult(Activity.RESULT_OK);
        finish();
    }

}