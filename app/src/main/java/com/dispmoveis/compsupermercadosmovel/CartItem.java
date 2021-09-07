package com.dispmoveis.compsupermercadosmovel;

import android.net.Uri;

public class CartItem {

    public Uri productImageUri;
    public String productName;
    public Double productPrice;
    public Integer productQty;

    public CartItem(Uri productImageUri, String productName, Double productPrice, Integer productQty) {
        this.productImageUri = productImageUri;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQty = productQty;
    }

}
