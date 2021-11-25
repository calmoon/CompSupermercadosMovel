package com.dispmoveis.compsupermercadosmovel.model;

public class SupermarketItem {

    private String id, productName, priceDate, supermarketName;
    private Double price;
    private String productImageUrl;

    // Usado na página de informações de um produto
    public SupermarketItem(String id, String productName, Double price, String priceDate, String supermarketName, String productImageUrl) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.priceDate = priceDate;
        this.supermarketName = supermarketName;
        this.productImageUrl = productImageUrl;
    }

    // Usado em listagem de produtos
    public SupermarketItem(String id, String productName, Double price, String productImageUrl) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.productImageUrl = productImageUrl;
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

    public String getProductImageUrl() {
        return productImageUrl;
    }
}
