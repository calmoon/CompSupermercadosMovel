package com.dispmoveis.compsupermercadosmovel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityViewCartBinding;

public class ViewCartActivity extends AppCompatActivity {

    private ActivityViewCartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewCartBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


    }


}