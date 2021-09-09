package com.dispmoveis.compsupermercadosmovel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.dispmoveis.compsupermercadosmovel.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    static  int NEW_SUPERMARKET_REQUEST = 1;
    static int NEW_ITEM_REQUEST = 2;

    private ActivityHomeBinding binding;

    private List<CartHistoryItemData> cartHistoryItems = new ArrayList<>();
    private CartHistoryAdapter CartHistoryAdapter;

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
                Intent i = new Intent(HomeActivity.this, SupermarketActivity.class);
                startActivityForResult(i, NEW_SUPERMARKET_REQUEST);
            }
        });

        CartHistoryAdapter = new CartHistoryAdapter(cartHistoryItems);

        binding.recyclerCartHistory.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerCartHistory.setLayoutManager(layoutManager);
        
        binding.recyclerCartHistory.setAdapter(CartHistoryAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.recyclerCartHistory.getContext(), DividerItemDecoration.VERTICAL);
        binding.recyclerCartHistory.addItemDecoration(dividerItemDecoration);
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
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == NEW_SUPERMARKET_REQUEST) {
                Intent i = new Intent(HomeActivity.this, ViewCartActivity.class);
                Integer cartHistoryItemsSize = cartHistoryItems.size();
                i.putExtra("cartHistoryItemsSize", cartHistoryItemsSize.toString());
                startActivityForResult(i, NEW_ITEM_REQUEST);
            }
            if (requestCode == NEW_ITEM_REQUEST) {
                String cardName = data.getStringExtra("cardName");
                String cardTotal = data.getStringExtra("cardTotal");
                String cardSize = data.getStringExtra("cardSize");
                String cardDate = data.getStringExtra("cardDate");

                CartHistoryItemData newCartHistoryItemData = new CartHistoryItemData();
                newCartHistoryItemData.cartTitle = cardName;
                newCartHistoryItemData.cartTotal = cardTotal;
                newCartHistoryItemData.qtyOfItems = cardSize;
                newCartHistoryItemData.date = cardDate;

                cartHistoryItems.add(newCartHistoryItemData);

                CartHistoryAdapter.notifyItemInserted(cartHistoryItems.size()-1);
            }
        }
    }
}