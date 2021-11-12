package com.dispmoveis.compsupermercadosmovel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityProductSearchBinding;

import java.util.List;

public class ProductSearchActivity extends AppCompatActivity {

    private ProductSearchViewModel productSearchViewModel;

    private ActivityProductSearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductSearchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        productSearchViewModel = new ViewModelProvider(this)
                .get(ProductSearchViewModel.class);

        productSearchViewModel.getSupermarketItems().observe(this, new Observer<List<SupermarketItem>>() {
            @Override
            public void onChanged(List<SupermarketItem> supermarketItems) {
                binding.recyclerProductSearch.setAdapter(
                        new ProductSearchAdapter(ProductSearchActivity.this, supermarketItems)
                );
            }
        });

        float imgSize = getResources().getDimension(R.dimen.productSearchImageSize);
        int numberOfColumns = Util.calculateNoOfColumns(ProductSearchActivity.this, imgSize);
        binding.recyclerProductSearch.setLayoutManager(
                new GridLayoutManager(ProductSearchActivity.this, numberOfColumns)
        );

        binding.recyclerProductSearch.setHasFixedSize(true);
    }
}