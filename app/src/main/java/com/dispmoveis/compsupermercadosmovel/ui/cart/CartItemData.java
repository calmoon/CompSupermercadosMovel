package com.dispmoveis.compsupermercadosmovel.ui.cart;

import android.net.Uri;

public class CartItemData {

    public Uri productImageUri;
    public String productName;
    public Double productPrice;
    public Integer productQty;

    public CartItemData(String productName, Double productPrice, Integer productQty) {
        //this.productImageUri = productImageUri;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQty = productQty;
    }

}
