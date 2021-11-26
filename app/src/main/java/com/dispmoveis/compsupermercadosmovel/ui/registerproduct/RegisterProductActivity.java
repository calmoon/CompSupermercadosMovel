package com.dispmoveis.compsupermercadosmovel.ui.registerproduct;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityRegisterProductBinding;
import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.dispmoveis.compsupermercadosmovel.ui.cart.CartActivity;
import com.dispmoveis.compsupermercadosmovel.util.Config;
import com.dispmoveis.compsupermercadosmovel.util.MoneyInputWatcher;
import com.dispmoveis.compsupermercadosmovel.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegisterProductActivity extends AppCompatActivity {

    public static String EXTRA_PRODUCT_NAME = "productName";
    public static String EXTRA_PRODUCT_IMAGE = "productImage";
    public static String EXTRA_ITEM_ID = "itemId";
    public static String EXTRA_ITEM_PRICE = "itemPrice";
    public static String EXTRA_ITEM_QTY = "itemQty";

    private Double currentCartTotal;

    private String productImageUrl;
    private String productName;
    private String supermarketItemId;
    private Double itemPrice;
    private Integer itemQty;
    private Double itemTotal;

    private ActivityRegisterProductBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent i = getIntent();
        supermarketItemId = i.getStringExtra(CartActivity.EXTRA_BARCODE_ITEM_ID);
        currentCartTotal = i.getDoubleExtra(CartActivity.EXTRA_CURRENT_CART_TOTAL, 0.0);

        String textCartTotal = "No seu carrinho: R$ " + Config.currencyFormat.format(currentCartTotal);
        binding.textPreviewCartTotal.setText(textCartTotal);

        binding.editProductQty.setText("1");

        loadItemInfo(supermarketItemId);
        updateTotals();

        TextWatcher textWatcher = new TextWatcher() {
            boolean _ignore = false; // indicates if the change was made by the TextWatcher itself.
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (_ignore)
                    return;
                _ignore = true; // prevent infinite loop
                updateTotals();
                _ignore = false; // release, so the TextWatcher start to listen again.
            }
        };
        binding.editProductQty.addTextChangedListener(textWatcher);

        MoneyInputWatcher moneyInputWatcher = new MoneyInputWatcher(binding.editProductPrice, Config.currencyLocale, true) {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                updateTotals();
            }
        };
        binding.editProductPrice.addTextChangedListener(moneyInputWatcher);

        binding.buttonProductQtyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToQuantity(1);
                updateTotals();
            }
        });

        binding.buttonProductQtySub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToQuantity(-1);
                updateTotals();
            }
        });

        binding.buttonCancelProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(Activity.RESULT_CANCELED, i);
                finish();
            }
        });

        binding.buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkFieldsEmpty()) {
                    Intent i = new Intent()
                            .putExtra(EXTRA_ITEM_ID, supermarketItemId)
                            .putExtra(EXTRA_ITEM_PRICE, itemPrice)
                            .putExtra(EXTRA_ITEM_QTY, itemQty)
                            .putExtra(EXTRA_PRODUCT_IMAGE, productImageUrl)
                            .putExtra(EXTRA_PRODUCT_NAME, productName);
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }
            }
        });

        binding.buttonCancelProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(Activity.RESULT_CANCELED, i);
                finish();
            }
        });
    }

    private boolean checkFieldsEmpty() {
        productName = binding.editProductName.getText().toString();
        if (productName.isEmpty() || itemTotal == 0) {
            return true;
        }
        return false;
    }

    private void updateTotals() {
        String priceInput = binding.editProductPrice.getText().toString();
        try {
            itemPrice = Util.currencyToBigDecimal(priceInput, Config.currencyLocale).doubleValue();
        } catch (NumberFormatException | NullPointerException e) {
            itemPrice = 0.00;
        }

        try {
            itemQty = Integer.parseInt(binding.editProductQty.getText().toString());
        } catch (NumberFormatException e) {
            itemQty = 0;
        }

        if (itemQty == 0) {
            itemQty = 1;
            binding.editProductQty.setText("1");
        }

        itemTotal = itemPrice * itemQty;
        String textProductTotal = "Total (produto x" + itemQty.toString() + "): R$ " +
                Config.currencyFormat.format(this.itemTotal);
        binding.textProductTotal.setText(textProductTotal);

        Double cartTotalPreview = itemTotal + currentCartTotal;
        String textCartTotal = "No seu carrinho: R$ " + Config.currencyFormat.format(cartTotalPreview);
        binding.textPreviewCartTotal.setText(textCartTotal);
    }

    private void addToQuantity(Integer num) {
        int result = Integer.parseInt(binding.editProductQty.getText().toString()) + num;
        if (result > 0) {
            binding.editProductQty.setText(String.valueOf(result));
        }
    }

    private void loadItemInfo(String supermarketItemId) {
        if (supermarketItemId != null) {

            ServerClient.select("itemInfo", supermarketItemId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        int resultCode = response.getInt("result_code");

                        if (resultCode == 1) {
                            JSONObject itemJSON = response.getJSONArray("result").getJSONObject(0);

                            productImageUrl = itemJSON.getString("imagem_url");
                            Util.setBitmapFromURL(binding.imageProduct, productImageUrl);

                            binding.editProductPrice.setText(
                                    Double.toString(itemJSON.getDouble("preco_atual"))
                            );

                            binding.editProductName.setText(itemJSON.getString("nome"));
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