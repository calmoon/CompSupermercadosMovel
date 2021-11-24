package com.dispmoveis.compsupermercadosmovel.ui.cart;

import android.graphics.Bitmap;

import com.dispmoveis.compsupermercadosmovel.model.SupermarketItem;

public class CartItemData extends SupermarketItem {

    Integer quantity;

    // TODO: imagem do produto
    public CartItemData(String itemId, String productName, Double itemTotal, Integer itemQty, Bitmap productImage) {
        super(itemId, productName, itemTotal, productImage);
        //this.imageUrl = imageUrl
        this.quantity = itemQty;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
