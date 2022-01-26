package com.dispmoveis.compsupermercadosmovel.ui.previouscarts;

public class PreviousCartsItem {

    private int id;
    private String name;
    private String date;
    private int qtdItems;
    private double total;

    public PreviousCartsItem(int id, String name, String date, int qtdItems, double total) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.qtdItems = qtdItems;
        this.total = total;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public String getDate() { return date; }

    public int getQtdItems() { return qtdItems; }

    public double getTotal() { return total; }
}
