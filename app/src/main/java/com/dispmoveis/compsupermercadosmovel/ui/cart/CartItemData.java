package com.dispmoveis.compsupermercadosmovel.ui.cart;

import com.dispmoveis.compsupermercadosmovel.model.SupermarketItem;

public class CartItemData extends SupermarketItem {

    Integer quantity;

    // TODO: imagem do produto
    public CartItemData(String itemId, String productName, Double itemTotal, Integer itemQty, String productImageUrl) {
        super(itemId, productName, itemTotal, productImageUrl);
        //this.imageUrl = imageUrl
        this.quantity = itemQty;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
