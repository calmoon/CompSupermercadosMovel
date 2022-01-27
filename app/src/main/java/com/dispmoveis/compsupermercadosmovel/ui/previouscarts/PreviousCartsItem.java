package com.dispmoveis.compsupermercadosmovel.ui.previouscarts;

public class PreviousCartsItem {

    private final int id;
    private final String name;
    private final String date;
    private final String supermarketName;
    private final int qtdItems;
    private final double total;

    public PreviousCartsItem(int id, String name, String supermarketName, String date, int qtdItems, double total) {
        this.id = id;
        this.name = name;
        this.supermarketName = supermarketName;
        this.date = date;
        this.qtdItems = qtdItems;
        this.total = total;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public String getDate() { return date; }

    public int getQtdItems() { return qtdItems; }

    public double getTotal() { return total; }

    public String getSupermarketName() {
        return supermarketName;
    }
}
