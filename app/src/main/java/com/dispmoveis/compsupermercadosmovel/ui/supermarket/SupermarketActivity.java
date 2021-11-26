package com.dispmoveis.compsupermercadosmovel.ui.supermarket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivitySupermarketBinding;

public class SupermarketActivity extends AppCompatActivity {
    private ActivitySupermarketBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySupermarketBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.buttonSaveSupermarket.setOnClickListener(v -> {
            Intent i = new Intent();
            setResult(Activity.RESULT_OK, i);
            finish();
        });

        binding.buttonCancelSupermarket.setOnClickListener(v -> {
            Intent i = new Intent();
            setResult(Activity.RESULT_CANCELED, i);
            finish();
        });
    }
}