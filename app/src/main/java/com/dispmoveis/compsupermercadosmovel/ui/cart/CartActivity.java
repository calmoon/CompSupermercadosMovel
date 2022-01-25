package com.dispmoveis.compsupermercadosmovel.ui.cart;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityCartBinding;
import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.dispmoveis.compsupermercadosmovel.ui.productsearch.ProductSearchActivity;
import com.dispmoveis.compsupermercadosmovel.ui.registerproduct.RegisterProductActivity;
import com.dispmoveis.compsupermercadosmovel.util.Config;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.permissionx.guolindev.PermissionX;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class CartActivity extends AppCompatActivity {

    public static final String EXTRA_CART_ID = "CartActivity_cartId";
    public static final String EXTRA_NEW_ITEM_ID = "itemId";
    public static final String EXTRA_NEW_ITEM_QTY = "itemQty";

    private final CartAdapter cartAdapter = new CartAdapter();

    private Double total = 0.0;
    private String supermarketId;

    private ActivityCartBinding binding;

    private final ActivityResultLauncher addProductLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    String itemId = data.getStringExtra(EXTRA_NEW_ITEM_ID);
                    int itemQty = data.getIntExtra(EXTRA_NEW_ITEM_QTY, 1 );

                    addItemToCart(itemId, itemQty);
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
        // TODO: usar o código comentado quando se tornar cabível
        //supermarketId = i.getStringExtra("supermarketId");
        //String cartId = i.getStringExtra(EXTRA_CART_ID);
        supermarketId = "1";

        String cartName = "Seu carrinho #" + getIntent().getStringExtra("cartHistoryItemsSize");
        binding.editCartName.setText(cartName);

        binding.textTotal.setText("Total:\nR$ " + Config.getCurrencyFormat().format(total));

        binding.recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerCart.setAdapter(cartAdapter);
        binding.recyclerCart.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.recyclerCart.getContext(), DividerItemDecoration.VERTICAL);
        binding.recyclerCart.addItemDecoration(dividerItemDecoration);

        binding.recyclerCart.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0){
                    binding.buttonAddItem.hide();
                } else{
                    binding.buttonAddItem.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        // Register the launcher and result handler
        binding.buttonOptionBarcode.setOnClickListener(v -> {
            PermissionX.init(this)
                    .permissions(Manifest.permission.CAMERA)
                    .onExplainRequestReason((scope, deniedList) ->
                            scope.showRequestReasonDialog(deniedList, "Para usar essa funcionalidade " +
                                    "é preciso conceder a permissão ao app.", "OK")
                    )
                    .onForwardToSettings((scope, deniedList) ->
                            scope.showForwardToSettingsDialog(deniedList, "Você precisa conceder" +
                                    "essa permissão manualmente.", "OK", "Cancel")
                    )
                    .request( (allGranted, grantedList, deniedList) ->
                            {
                                if (allGranted) {
                                    ScanOptions options = new ScanOptions()
                                            .setDesiredBarcodeFormats(ScanOptions.EAN_13)
                                            .setPrompt("Aponte para um código de barras")
                                            .setOrientationLocked(false)
                                            .setBeepEnabled(false);
                                    barcodeLauncher.launch(options);
                                }
                                else {
                                    Toast.makeText(this, "Não é possível acessar a " +
                                            "funcionalidade sem dar acesso a permissão", Toast.LENGTH_LONG).show();
                                }
                            }
                    );
        });

        binding.buttonOptionCatalog.setOnClickListener(v -> {
            addProductLauncher.launch(
                    new Intent(CartActivity.this, ProductSearchActivity.class)
                            .putExtra(RegisterProductActivity.EXTRA_CURRENT_CART_TOTAL, total)
                            .putExtra(ProductSearchActivity.EXTRA_SUPERMARKET_ID, supermarketId)
            );
        });

        binding.buttonSaveCart.setOnClickListener(v -> {
            Integer cardSize = cartAdapter.getItemCount();
            String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            Intent resultIntent = new Intent()
                    .putExtra("cardName", binding.editCartName.getText().toString())
                    .putExtra("cardTotal", Config.getCurrencyFormat().format(total))
                    .putExtra("cardSize", cardSize + " produtos")
                    .putExtra("cardDate", "Última modificação: " + date);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });

        binding.buttonCancelCart.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED, new Intent());
            finish();
        });
    }

    void reflectItemQtyChange(Double oldItemPrice, Double newItemPrice) {
        total -= oldItemPrice;
        total += newItemPrice;
        binding.textTotal.setText("Total:\nR$ " + Config.getCurrencyFormat().format(total));
    }

    private void addItemToCart(String supermarketItemId, int itemQty) {
        if (supermarketItemId != null) {

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
    }

}