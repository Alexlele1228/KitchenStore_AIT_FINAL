package com.example.kitchenstore.classes;

public class ProductInDB {
    private String name;
    private int amount;
    private int expiry;

    public ProductInDB() {
    }

    public ProductInDB(String name, int amount, int expiry) {
        this.name = name;
        this.amount = amount;
        this.expiry = expiry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getExpiry() {
        return expiry;
    }

    public void setExpiry(int expiry) {
        this.expiry = expiry;
    }
}
