package com.dispmoveis.compsupermercadosmovel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityPreviousCartsBinding;
import com.dispmoveis.compsupermercadosmovel.databinding.ActivityProductSearchBinding;
import com.dispmoveis.compsupermercadosmovel.databinding.ActivitySupermarketBinding;

public class ProductSearchActivity extends AppCompatActivity {

    private ActivityProductSearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductSearchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}