package com.dispmoveis.compsupermercadosmovel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    static int NEW_ITEM_REQUEST = 1;

    List<CartHistoryItemData> cartHistoryItems = new ArrayList<>();

    CartHistoryAdapter CartHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        
        setSupportActionBar(binding.toolbarHome);

        String login = Config.getLogin(HomeActivity.this);
        //binding.textWebData.setText("Olá " + login);

        binding.buttonCreateCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, RegisterProductActivity.class);
                startActivity(i);
            }
        });

        CartHistoryAdapter = new CartHistoryAdapter(cartHistoryItems);

        binding.recyclerCarts.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerCarts.setLayoutManager(layoutManager);
        
        binding.recyclerCarts.setAdapter(CartHistoryAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.recyclerCarts.getContext(), DividerItemDecoration.VERTICAL);
        binding.recyclerCarts.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_toolbar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        Config.setLogin(HomeActivity.this, "");
        Config.setPassword(HomeActivity.this, "");
        Intent i = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(i);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_ITEM_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String title = data.getStringExtra("title");
                String total = data.getStringExtra("description");
                String date = data.getStringExtra("date");
                String quantity = data.getStringExtra("quantity");

                CartHistoryItemData newCartHistoryItemData = new CartHistoryItemData();
                newCartHistoryItemData.cartTitle = title;
                newCartHistoryItemData.cartTotal = total;
                newCartHistoryItemData.date = date;
                newCartHistoryItemData.qtyOfItems = quantity;

                cartHistoryItems.add(newCartHistoryItemData);

                CartHistoryAdapter.notifyItemInserted(cartHistoryItems.size()-1);
            }
        }
    }
}