package com.dispmoveis.compsupermercadosmovel.ui.productsearch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.dispmoveis.compsupermercadosmovel.R;
import com.dispmoveis.compsupermercadosmovel.databinding.ActivityProductSearchBinding;
import com.dispmoveis.compsupermercadosmovel.model.SupermarketItem;
import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.dispmoveis.compsupermercadosmovel.ui.registerproduct.RegisterProductActivity;
import com.dispmoveis.compsupermercadosmovel.util.Util;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ProductSearchActivity extends AppCompatActivity {

    public static final String EXTRA_SUPERMARKET_ID = "ProductSearchActivity_SupermarketId";

    double currentCartTotal;

    private ProductSearchAdapter productSearchAdapter;
    private ActivityProductSearchBinding binding;

    final ActivityResultLauncher editSelectedProductLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    setResult(Activity.RESULT_OK, result.getData());
                    finish();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductSearchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent i = getIntent();
        String supermarketId = i.getStringExtra(EXTRA_SUPERMARKET_ID);
        currentCartTotal = i.getDoubleExtra(RegisterProductActivity.EXTRA_CURRENT_CART_TOTAL, 0.0);

        loadSupermarketName(supermarketId);

        ProductSearchViewModel productSearchViewModel = new ViewModelProvider(this)
                .get(ProductSearchViewModel.class);

        productSearchViewModel.setSupermarketId(supermarketId);

        productSearchViewModel.getSupermarketItems().observe(this, new Observer<List<SupermarketItem>>() {
            @Override
            public void onChanged(List<SupermarketItem> supermarketItems) {
                productSearchAdapter = new ProductSearchAdapter(ProductSearchActivity.this, supermarketItems);
                binding.recyclerProductSearch.setAdapter(productSearchAdapter);
            }
        });

        float imgSize = getResources().getDimension(R.dimen.productSearchImageSize);
        int numberOfColumns = Util.calculateNoOfColumns(ProductSearchActivity.this, imgSize);
        binding.recyclerProductSearch.setLayoutManager(
                new GridLayoutManager(ProductSearchActivity.this, numberOfColumns)
        );
        binding.recyclerProductSearch.setHasFixedSize(true);

        binding.buttonProductSearch.setOnClickListener(v -> {
            // Set focus on search box and show keyboard
            binding.editProductSearch.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0));
            binding.editProductSearch.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0f, 0f, 0));
        });

        binding.editProductSearch.addTextChangedListener(new TextWatcher() {
            boolean _ignore = false; // indicates if the change was made by the TextWatcher itself.
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (_ignore)
                    return;
                _ignore = true; // prevent infinite loop
                productSearchAdapter.filter(s.toString());
                _ignore = false; // release, so the TextWatcher start to listen again.
            }
        });

        binding.buttonCancelProductSearch.setOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });

        binding.buttonSortPrice.setOnClickListener(v -> {
            boolean ascending = productSearchViewModel.toggleSortOrder();
            if (ascending) {
                binding.buttonSortPrice.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_arrow_up_compact, 0);
            } else {
                binding.buttonSortPrice.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ic_arrow_down_compact, 0);
            }
            binding.buttonSortPrice.setTextColor(Color.parseColor("#3F51B5"));
        });
    }

    private void loadSupermarketName(String supermarketId) {
        ServerClient.select("supermarketInfo", supermarketId, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    int resultCode = response.getInt("result_code");

                    if (resultCode == 1) {
                        String supermarketName = response.getJSONArray("result")
                                .getJSONObject(0)
                                .getString("nome");
                        binding.textSelectedSupermarket.setText(supermarketName);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            //TODO: onFailure

        });
    }

}