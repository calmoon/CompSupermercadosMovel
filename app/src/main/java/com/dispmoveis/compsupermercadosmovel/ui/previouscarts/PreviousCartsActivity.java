package com.dispmoveis.compsupermercadosmovel.ui.previouscarts;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.R;
import com.dispmoveis.compsupermercadosmovel.databinding.ActivityPreviousCartsBinding;
import com.dispmoveis.compsupermercadosmovel.ui.cart.CartActivity;
import com.dispmoveis.compsupermercadosmovel.ui.login.LoginActivity;
import com.dispmoveis.compsupermercadosmovel.ui.supermarket.SupermarketActivity;
import com.dispmoveis.compsupermercadosmovel.util.Config;
import com.permissionx.guolindev.PermissionX;

import java.util.ArrayList;
import java.util.List;

public class PreviousCartsActivity extends AppCompatActivity {

    static  int NEW_SUPERMARKET_REQUEST = 1;
    static int NEW_ITEM_REQUEST = 2;

    private ActivityPreviousCartsBinding binding;

    private final List<PreviousCartsItemData> cartHistoryItems = new ArrayList<>();
    private final PreviousCartsAdapter CartHistoryAdapter = new PreviousCartsAdapter(cartHistoryItems);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviousCartsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //String login = Config.getLogin(PreviousCartsActivity.this);
        //binding.textWebData.setText("Olá " + login);

        binding.toolbarHome.setTitle("Seus carrinhos");
        setSupportActionBar(binding.toolbarHome);

        binding.recyclerCartHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerCartHistory.setAdapter(CartHistoryAdapter);
        binding.recyclerCartHistory.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.recyclerCartHistory.getContext(), DividerItemDecoration.VERTICAL);
        binding.recyclerCartHistory.addItemDecoration(dividerItemDecoration);

        binding.recyclerCartHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0){
                    binding.buttonCreateCart.hide();
                } else{
                    binding.buttonCreateCart.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        binding.buttonCreateCart.setOnClickListener(v -> {
            PermissionX.init(this)
                    .permissions(Manifest.permission.ACCESS_FINE_LOCATION)
                    .onExplainRequestReason((scope, deniedList) ->
                            scope.showRequestReasonDialog(deniedList, "Para usar essa funcionalidade " +
                                    "é preciso conceder a permissão ao app.", "OK")
                            )
                    .onForwardToSettings((scope, deniedList) ->
                            scope.showForwardToSettingsDialog(deniedList, "Você precisa conceder" +
                                    "essa permissão manualmente.", "OK", "Cancel")
                            )
                    .request( (allGranted, grantedList, deniedList) ->
                            {
                                if (allGranted) {
                                    Intent i = new Intent(PreviousCartsActivity.this, SupermarketActivity.class);
                                    startActivityForResult(i, NEW_SUPERMARKET_REQUEST);
                                }
                                else {
                                    Toast.makeText(this, "Não é possível acessar a " +
                                            "funcionalidade sem dar acesso a permissão", Toast.LENGTH_LONG).show();
                                }
                            }
                            );
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_previous_carts, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        Config.setLogin(PreviousCartsActivity.this, "");
        Config.setPassword(PreviousCartsActivity.this, "");
        Intent i = new Intent(PreviousCartsActivity.this, LoginActivity.class);
        startActivity(i);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == NEW_SUPERMARKET_REQUEST) {
                Intent i = new Intent(PreviousCartsActivity.this, CartActivity.class);
                Integer cartHistoryItemsSize = cartHistoryItems.size() + 1;
                i.putExtra("cartHistoryItemsSize", cartHistoryItemsSize.toString());
                startActivityForResult(i, NEW_ITEM_REQUEST);
            }
            if (requestCode == NEW_ITEM_REQUEST) {
                String cardName = data.getStringExtra("cardName");
                String cardTotal = data.getStringExtra("cardTotal");
                String cardSize = data.getStringExtra("cardSize");
                String cardDate = data.getStringExtra("cardDate");

                PreviousCartsItemData newCartHistoryItemData = new PreviousCartsItemData();
                newCartHistoryItemData.cartTitle = cardName;
                newCartHistoryItemData.cartTotal = "R$ " + cardTotal;
                newCartHistoryItemData.qtyOfItems = cardSize;
                newCartHistoryItemData.date = cardDate;

                cartHistoryItems.add(newCartHistoryItemData);
                CartHistoryAdapter.notifyItemInserted(cartHistoryItems.size()-1);

                binding.textNoCarts.setVisibility(View.GONE);
            }
        }
    }


}