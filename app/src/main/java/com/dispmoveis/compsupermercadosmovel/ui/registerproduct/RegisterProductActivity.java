package com.dispmoveis.compsupermercadosmovel.ui.registerproduct;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.dispmoveis.compsupermercadosmovel.databinding.ActivityRegisterProductBinding;
import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.dispmoveis.compsupermercadosmovel.ui.cart.CartActivity;
import com.dispmoveis.compsupermercadosmovel.util.Config;
import com.dispmoveis.compsupermercadosmovel.util.MoneyInputWatcher;
import com.dispmoveis.compsupermercadosmovel.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class RegisterProductActivity extends AppCompatActivity {

    public static final String EXTRA_CURRENT_CART_TOTAL = "cartTotal";
    public static final String EXTRA_SELECTED_ITEM_ID = "supermarketItemId";

    // Form data
    private String capturedImagePath = null;
    private String formProductName;
    private Integer formItemQty;
    private Double formItemPrice = null;

    // External data
    private String itemId;
    private Double dbItemPrice, currentCartTotal;

    private ActivityRegisterProductBinding binding;

    private final ActivityResultLauncher<Uri> changeImageLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success) {
                    loadCapturedImage();
                } else {
                    capturedImagePath = null;
                }
            }
    );

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (capturedImagePath != null)
            outState.putString("capturedImagePath", capturedImagePath);

        if (binding.editProductName.isFocusable()) {
            outState.putString("productName", binding.editProductName.getText().toString());
        }

        if (formItemPrice != null && !formItemPrice.equals(dbItemPrice)) {
            outState.putDouble("itemPrice", formItemPrice);
        }

        outState.putInt("itemQty", formItemQty);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState.containsKey("capturedImagePath"))
            capturedImagePath = savedInstanceState.getString("capturedImagePath");

        if (savedInstanceState.containsKey("productName"))
            formProductName = savedInstanceState.getString("productName");

        if (savedInstanceState.containsKey("itemPrice"))
            formItemPrice = savedInstanceState.getDouble("itemPrice");

        formItemQty = savedInstanceState.getInt("itemQty");

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent i = getIntent();
        itemId = i.getStringExtra(EXTRA_SELECTED_ITEM_ID);
        currentCartTotal = i.getDoubleExtra(EXTRA_CURRENT_CART_TOTAL, 0.0);

        loadItemInfo();

        // Só mostra o botão de alterar imagem depois dela carregar
        binding.buttonChangeImage.setVisibility(View.INVISIBLE);
        binding.imageProduct.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (binding.imageProduct.getDrawable() != null) {
                    binding.buttonChangeImage.setVisibility(View.VISIBLE);
                    v.removeOnLayoutChangeListener(this);
                }
            }
        });

        binding.editProductQty.addTextChangedListener(new TextWatcher() {
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
        });

        binding.editProductPrice.addTextChangedListener(new MoneyInputWatcher(binding.editProductPrice, Config.currencyLocale, true) {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                updateTotals();
            }
        });

        binding.buttonProductQtyAdd.setOnClickListener(v -> addToQuantity(1));

        binding.buttonProductQtySub.setOnClickListener(v -> addToQuantity(-1));

        binding.buttonCancelProduct.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });

        binding.buttonChangeImage.setOnClickListener(v -> launchChangeImageContract());

        binding.buttonAddToCart.setOnClickListener(v -> {
            String productName = binding.editProductName.getText().toString();
            if (!productName.isEmpty() && formItemPrice != 0) {
                submitUserChanges();

                Intent resultIntent = new Intent()
                        .putExtra(CartActivity.EXTRA_NEW_ITEM_ID, itemId)
                        .putExtra(CartActivity.EXTRA_NEW_ITEM_QTY, formItemQty);

                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private File createTempImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File tempImageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        tempImageFile.deleteOnExit();
        return tempImageFile;
    }

    private void launchChangeImageContract() {
        File tempFile = null;
        try {
            tempFile = createTempImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (tempFile == null) {
            Toast.makeText(RegisterProductActivity.this,
                    "Falha ao criar arquivo temporário.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        capturedImagePath = tempFile.getAbsolutePath();

        Uri inputImageURI = FileProvider.getUriForFile(
                this,
                "com.dispmoveis.compsupermercadosmovel.fileprovider",
                tempFile
        );

        changeImageLauncher.launch(inputImageURI);
    }

    private void loadCapturedImage() {
        try {
            File file = new File(capturedImagePath);
            Uri imageUri = Uri.fromFile(file);
            Glide.with(this)
                    .load(imageUri)
                    .into(binding.imageProduct);
        } catch (Exception e) {
            Log.w("LAYOUT_STATE_CHANGE", "Layout state change. " + e.getMessage());
        }
    }

    private void updateTotals() {
        String priceInput = binding.editProductPrice.getText().toString();
        try {
            formItemPrice = Util.currencyToBigDecimal(priceInput, Config.currencyLocale).doubleValue();
        } catch (NumberFormatException | NullPointerException e) {
            formItemPrice = 0.00;
        }

        try {
            formItemQty = Integer.parseInt(binding.editProductQty.getText().toString());
        } catch (NumberFormatException e) {
            formItemQty = 1;
        }

        if (formItemQty <= 0) {
            formItemQty = 1;
            binding.editProductQty.setText("1");
        }

        Double itemTotal = formItemPrice * formItemQty;

        String textProductTotal = "Total (produto x" + formItemQty.toString() + "): R$ " +
                Config.getCurrencyFormat().format(itemTotal);
        binding.textProductTotal.setText(textProductTotal);

        Double cartTotalPreview = itemTotal + currentCartTotal;
        String textCartTotal = "No seu carrinho: R$ " + Config.getCurrencyFormat().format(cartTotalPreview);
        binding.textPreviewCartTotal.setText(textCartTotal);
    }

    private void addToQuantity(Integer num) {
        int result = Integer.parseInt(binding.editProductQty.getText().toString()) + num;
        if (result > 0) {
            binding.editProductQty.setText(String.valueOf(result));
        }
        updateTotals();
    }

    private void submitUserChanges() {
        if (capturedImagePath != null) {
            submitProductImage();
        }
        if (!formItemPrice.equals(dbItemPrice)) {
            submitItemPrice();
        }
    }

    private void submitProductImage() {
        File f = new File(capturedImagePath);
        if (f.exists() && !f.isDirectory()) {
            ServerClient.s3ImageUpload(itemId, f, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getInt("result_code") != 1) {
                            onFailure(statusCode, headers, response.toString(), new InternalError());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("S3_IMAGE_UPLOAD_FAIL", "Response: " + responseString);
                    Toast.makeText(RegisterProductActivity.this,
                            "Falha ao atualizar a imagem do produto.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            Log.e("S3_IMAGE_UPLOAD_FAIL", f.toString());
            Toast.makeText(RegisterProductActivity.this,
                    "Falha ao atualizar a imagem do produto: arquivo inválido.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void submitItemPrice() {
        RequestParams values = new RequestParams();
        values.put("preco_atual", formItemPrice.toString());

        ServerClient.update("item", itemId, values, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getInt("result_code") != 1) {
                        onFailure(statusCode, headers, response.toString(), new InternalError());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("ALTER_PRICE_FAIL", "Response: " + responseString);
                Toast.makeText(RegisterProductActivity.this,
                        "Falha ao atualizar o preço do produto.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadItemInfo() {
        if (itemId != null) {

            ServerClient.select("itemInfo", itemId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        int resultCode = response.getInt("result_code");

                        if (resultCode == 1) {
                            JSONObject itemJSON = response.getJSONArray("result").getJSONObject(0);

                            String dbProductName = itemJSON.getString("nome");
                            if (formProductName != null) {
                                binding.editProductName.setText(formProductName);
                            } else {
                                binding.editProductName.setText(dbProductName);
                            }

                            dbItemPrice = itemJSON.getDouble("preco_atual");
                            if (formItemPrice == null)
                                formItemPrice = dbItemPrice;
                            binding.editProductPrice.setText(Config.getCurrencyFormat().format(formItemPrice));

                            if (capturedImagePath != null) {
                                loadCapturedImage();
                            } else {
                                String productImageUrl = itemJSON.getString("imagem_url");
                                Glide.with(RegisterProductActivity.this)
                                        .load(productImageUrl)
                                        .into(binding.imageProduct);
                            }

                            if (dbProductName.equals("")) {
                                binding.editProductPrice.setHint("Produto sem nome! Defina um.");
                            } else {
                                binding.editProductName.setKeyListener(null);
                                binding.editProductName.setFocusable(false);
                                binding.editProductName.clearFocus();
                            }
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
                    Log.e("HTTP_LOAD_PRODUCT_FAIL", "Response: " + responseString);
                    Toast.makeText(RegisterProductActivity.this,
                            "Falha ao carregar o produto.",
                            Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            });

        }
    }

}