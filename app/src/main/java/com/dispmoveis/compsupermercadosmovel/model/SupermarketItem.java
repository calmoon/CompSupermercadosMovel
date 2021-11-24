package com.dispmoveis.compsupermercadosmovel.model;

import android.graphics.Bitmap;

public class SupermarketItem {

    private String id, productName, priceDate, supermarketName;
    private Double price;
    private Bitmap productImage;

    // Usado na página de informações de um produto
    public SupermarketItem(String id, String productName, Double price, String priceDate, String supermarketName, Bitmap productImage) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.priceDate = priceDate;
        this.supermarketName = supermarketName;
        this.productImage = productImage;
    }

    // Usado em listagem de produtos
    public SupermarketItem(String id, String productName, Double price, Bitmap productImage) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.productImage = productImage;
    }

    public String getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public Double getPrice() {
        return price;
    }

    public String getPriceDate() {
        return priceDate;
    }

    public String getSupermarketName() {
        return supermarketName;
    }

    public Bitmap getProductImage() {
        return productImage;
    }
}
