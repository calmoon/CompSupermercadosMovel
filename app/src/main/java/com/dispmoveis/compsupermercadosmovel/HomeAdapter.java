package com.dispmoveis.compsupermercadosmovel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dispmoveis.compsupermercadosmovel.databinding.CardListBinding;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter{

    private CardListBinding binding;

    HomeActivity homeActivity;
    List<Cart> carts;

    public HomeAdapter(HomeActivity homeActivity, List<Cart> carts){
        this.homeActivity = homeActivity;
        this.carts = carts;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(homeActivity);
        View view = layoutInflater.inflate(R.layout.card_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Cart cart = carts.get(position);

        View view = holder.itemView;

        binding.textTitle.setText(cart.title);
        binding.textTotal.setText(cart.total);
        binding.textQuantity.setText(cart.quantity);
        binding.textDate.setText(cart.date);
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }
}
