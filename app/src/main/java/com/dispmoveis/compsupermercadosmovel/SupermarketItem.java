package com.dispmoveis.compsupermercadosmovel;

import android.graphics.Bitmap;

public class SupermarketItem {

    private String id, name, price, priceDate, supermarketName;
    private Bitmap image;

    // Usado na página de informações de um produto
    public SupermarketItem(String id, String name, String price, String priceDate, String supermarketName, Bitmap image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.priceDate = priceDate;
        this.supermarketName = supermarketName;
        this.image = image;
    }

    // Usado em listagem de produtos
    public SupermarketItem(String id, String name, String price, Bitmap image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getPriceDate() {
        return priceDate;
    }

    public String getSupermarketName() {
        return supermarketName;
    }

    public Bitmap getImage() {
        return image;
    }
}
