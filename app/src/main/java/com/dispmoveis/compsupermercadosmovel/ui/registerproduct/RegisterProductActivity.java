package com.dispmoveis.compsupermercadosmovel.ui.registerproduct;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import cz.msebera.android.httpclient.Header;

public class RegisterProductActivity extends AppCompatActivity {

    public static final String EXTRA_CURRENT_CART_TOTAL = "cartTotal";
    public static final String EXTRA_SELECTED_ITEM_ID = "supermarketItemId";

    private String itemId;
    private Integer itemQty;
    private Double itemPrice;
    private String productImageUrl;

    private Double dbItemPrice, currentCartTotal;
    private String capturedImagePath = null;

    private ActivityRegisterProductBinding binding;

    private final ActivityResultLauncher changeImageLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            success -> {
                if (success) {
                    loadCapturedImage();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterProductBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent i = getIntent();
        itemId = i.getStringExtra(EXTRA_SELECTED_ITEM_ID);
        currentCartTotal = i.getDoubleExtra(EXTRA_CURRENT_CART_TOTAL, 0.0);

        String textCartTotal = "No seu carrinho: R$ " + Config.getCurrencyFormat().format(currentCartTotal);
        binding.textPreviewCartTotal.setText(textCartTotal);

        binding.editProductQty.setText("1");

        loadItemInfo(itemId);
        updateTotals();

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
            if (!productName.isEmpty() && itemPrice != 0) {
                submitUserChanges();

                Intent resultIntent = new Intent()
                        .putExtra(CartActivity.EXTRA_ITEM_ID, itemId)
                        .putExtra(CartActivity.EXTRA_ITEM_QTY, itemQty);

                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private File createTempImageFile() {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp-image-" + itemId, ".png", getCacheDir());
            tempFile.delete();
            tempFile.createNewFile();
            tempFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempFile;
    }

    private void launchChangeImageContract() {
        File tempFile = createTempImageFile();

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
            Bitmap capturedImage = BitmapFactory.decodeFile(capturedImagePath);
            Log.d("SET_CAPTURED_IMAGE_PATH", capturedImagePath);

            ExifInterface ei = new ExifInterface(capturedImagePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            int rotationAngle;

            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationAngle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationAngle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationAngle = 270;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotationAngle = 0;
            }

            binding.imageProduct.setImageBitmap(
                    Util.rotateImage(capturedImage, rotationAngle)
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
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

        Double itemTotal = itemPrice * itemQty;

        String textProductTotal = "Total (produto x" + itemQty.toString() + "): R$ " +
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
        if (!itemPrice.equals(dbItemPrice)) {
            submitItemPrice();
        }
    }

    private void submitProductImage() {
        File f = new File(capturedImagePath);
        if (f.exists()) {
            ServerClient.s3ImageUpload(itemId, f, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getInt("result_code") != 1) {
                            onFailure(statusCode, headers, new InternalError(), response);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e("S3_IMAGE_UPLOAD_FAIL", errorResponse.toString());
                    Toast.makeText(RegisterProductActivity.this,
                            "Falha ao atualizar a imagem do produto.",
                            Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            Log.e("S3_IMAGE_UPLOAD_FAIL", f.toString());
            Toast.makeText(RegisterProductActivity.this,
                    "Falha ao atualizar a imagem do produto.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void submitItemPrice() {
        RequestParams values = new RequestParams();
        values.put("preco_atual", itemPrice.toString());

        ServerClient.update("item", itemId, values, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getInt("result_code") != 1) {
                        onFailure(statusCode, headers, new InternalError(), response);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(RegisterProductActivity.this,
                        "Falha ao atualizar o preço do produto",
                        Toast.LENGTH_LONG).show();
            }
        });
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

                            binding.editProductName.setText(itemJSON.getString("nome"));

                            dbItemPrice = itemJSON.getDouble("preco_atual");
                            binding.editProductPrice.setText(Config.getCurrencyFormat().format(dbItemPrice));

                            productImageUrl = itemJSON.getString("imagem_url");
                            Glide.with(RegisterProductActivity.this)
                                .load(productImageUrl)
                                .into(binding.imageProduct);

                            binding.editProductName.setEnabled(false);
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