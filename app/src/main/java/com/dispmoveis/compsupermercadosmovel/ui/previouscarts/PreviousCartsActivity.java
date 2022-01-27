package com.dispmoveis.compsupermercadosmovel.ui.previouscarts;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.R;
import com.dispmoveis.compsupermercadosmovel.databinding.ActivityPreviousCartsBinding;
import com.dispmoveis.compsupermercadosmovel.network.ServerClient;
import com.dispmoveis.compsupermercadosmovel.ui.cart.CartActivity;
import com.dispmoveis.compsupermercadosmovel.ui.login.LoginActivity;
import com.dispmoveis.compsupermercadosmovel.ui.supermarket.SupermarketActivity;
import com.dispmoveis.compsupermercadosmovel.util.Config;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.permissionx.guolindev.PermissionX;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PreviousCartsActivity extends AppCompatActivity {

    public static final int NEW_SUPERMARKET_REQUEST = 1;
    public static final int NEW_ITEM_REQUEST = 2;
    public static final String EXTRA_SUPERMARKET_ID = "PreviousCartsActivity_supermarketId";

    public PreviousCartsViewModel previousCartsViewModel;

    private ActivityPreviousCartsBinding binding;

    private int userID;
    private int nextPreviousCartsQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviousCartsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        userID = Config.getUserId(PreviousCartsActivity.this);

        String userName = Config.getUserName(PreviousCartsActivity.this).split(" ")[0];
        binding.toolbarHome.setTitle("Bem-vindo(a), " + userName);
        setSupportActionBar(binding.toolbarHome);

        previousCartsViewModel = new ViewModelProvider(this)
                .get(PreviousCartsViewModel.class);
        previousCartsViewModel.setUserId(String.valueOf(userID));

        previousCartsViewModel.getPreviousCartsItems().observe(this, new Observer<List<PreviousCartsItem>>() {
            @Override
            public void onChanged(List<PreviousCartsItem> previousCartsItems) {
                binding.recyclerCartHistory.setAdapter(
                        new PreviousCartsAdapter(PreviousCartsActivity.this, previousCartsItems)
                );
                nextPreviousCartsQty = previousCartsItems.size() + 1;
                if (previousCartsItems.isEmpty()) {
                    binding.textNoCarts.setVisibility(View.VISIBLE);
                }
                else { binding.textNoCarts.setVisibility(View.GONE); }
            }
        });

        binding.recyclerCartHistory.setLayoutManager(new LinearLayoutManager(this));
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
                int supermarketID = data.getIntExtra(EXTRA_SUPERMARKET_ID, 0);
                String cartName = "Seu carrinho #" + nextPreviousCartsQty;

                RequestParams params = new RequestParams();
                params.put("nome", cartName);
                params.put("id_usuario", userID);
                params.put("id_supermercado", supermarketID);

                ServerClient.insert("carrinho", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            int resultCode = response.getInt("result_code");
                            if (resultCode == 1) {
                                ServerClient.select("lastId", "carrinho", new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        try {
                                            int resultCode = response.getInt("result_code");
                                            if (resultCode == 1) {
                                                JSONArray carrinhoIDJSON = response.getJSONArray("result");
                                                JSONObject itemJSON = carrinhoIDJSON.getJSONObject(0);

                                                Intent i = new Intent(PreviousCartsActivity.this, CartActivity.class)
                                                        .putExtra(CartActivity.EXTRA_CART_ID, String.valueOf(itemJSON.getInt("max")));
                                                startActivityForResult(i, NEW_ITEM_REQUEST);
                                                previousCartsViewModel.reloadCartList();
                                            }
                                            else {
                                                Toast.makeText(PreviousCartsActivity.this,
                                                        "Erro ao fazer consulta.", Toast.LENGTH_LONG).show();
                                                Log.e("Super", "Erro de conssulta - " + response.toString());
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                            else {
                                Toast.makeText(PreviousCartsActivity.this,
                                        "Erro ao fazer inserção.", Toast.LENGTH_LONG).show();
                                Log.e("Super", "Erro de conssulta - " + response.toString());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if (requestCode == NEW_ITEM_REQUEST) {
                previousCartsViewModel.reloadCartList();
                binding.textNoCarts.setVisibility(View.GONE);
            }
        }
    }


}