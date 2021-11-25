package com.dispmoveis.compsupermercadosmovel.ui.cart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.dispmoveis.compsupermercadosmovel.ui.registerproduct.RegisterProductActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class CartActivity extends AppCompatActivity {

    public static String EXTRA_CURRENT_CART_TOTAL = "cartTotal";
    public static String EXTRA_CURRENT_ITEM_ID = "supermarketItemId";

    private Double total = 0.0;
    private String supermarketId;

    static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private List<CartItemData> cartItems = new ArrayList<>();
    private CartAdapter cartAdapter = new CartAdapter(cartItems);

    private ActivityCartBinding binding;

    private final ActivityResultLauncher registerProductLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    String itemId = data.getStringExtra( RegisterProductActivity.EXTRA_ITEM_ID );
                    String productName = data.getStringExtra( RegisterProductActivity.EXTRA_PRODUCT_NAME );
                    Double itemPrice = data.getDoubleExtra( RegisterProductActivity.EXTRA_ITEM_PRICE, 0 );
                    Integer itemQty = data.getIntExtra( RegisterProductActivity.EXTRA_ITEM_QTY, 1 );
                    String imageUrl = data.getStringExtra( RegisterProductActivity.EXTRA_PRODUCT_IMAGE );

                    this.total += itemPrice * itemQty;

                    binding.textTotal.setText("Total:\nR$ " + decimalFormat.format(this.total));

                    cartItems.add( new CartItemData(itemId, productName, itemPrice, itemQty, imageUrl) );
                    cartAdapter.notifyItemInserted(cartItems.size()-1);
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
                                            .putExtra(EXTRA_CURRENT_CART_TOTAL, total)
                                            .putExtra(EXTRA_CURRENT_ITEM_ID, itemId);
                                    registerProductLauncher.launch(i);
                                }

                                else {
                                    Toast.makeText(CartActivity.this,
                                            "Falha no servidor ao escanear.",
                                            Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        //TODO: onFailure
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
        supermarketId = "1";

        String cartName = "Seu carrinho #" + getIntent().getStringExtra("cartHistoryItemsSize");
        binding.editCartName.setText(cartName);

        binding.textTotal.setText("Total:\nR$ 0.00");

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
        binding.buttonOptionBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanOptions options = new ScanOptions()
                        .setDesiredBarcodeFormats(ScanOptions.EAN_13)
                        .setPrompt("Aponte para um código de barras")
                        .setOrientationLocked(false)
                        .setBeepEnabled(false);
                barcodeLauncher.launch(options);
            }
        });

        binding.buttonOptionCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CartActivity.this, RegisterProductActivity.class)
                        .putExtra(EXTRA_CURRENT_CART_TOTAL, total);
                registerProductLauncher.launch(i);
            }
        });

        binding.buttonSaveCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer cardSize = cartItems.size();
                String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                Intent i = new Intent()
                        .putExtra("cardName", binding.editCartName.getText().toString())
                        .putExtra("cardTotal", decimalFormat.format(total))
                        .putExtra("cardSize", cardSize + " produtos")
                        .putExtra("cardDate", "Última modificação: " + date);
                setResult(Activity.RESULT_OK, i);
                finish();
            }
        });

        binding.buttonCancelCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED, new Intent());
                finish();
            }
        });
    }

}