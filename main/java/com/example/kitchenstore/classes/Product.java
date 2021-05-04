package com.example.kitchenstore.classes;

import java.util.Objects;

public class Product implements Comparable<Product> {
    private String name;
    private int amount;
    private double price;
    private int expiry;

    public Product(String name, int amount, double price, int expiry) {
        this.name = name;
        this.amount = amount;
        this.price = price;
        this.expiry = expiry;
    }

    public Product(int amount,  int expiry, double price) {

        this.amount = amount;
   this.price=price;
        this.expiry = expiry;
    }

    public Product(int amount, double price) {
        this.amount = amount;
        this.price = price;
    }

    public Product() {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getExpiry() {
        return expiry;
    }

    public void setExpiry(int expiry) {
        this.expiry = expiry;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                ", price=" + price +
                ", expiry=" + expiry +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;


        return amount == product.amount &&
                Double.compare(product.price, price) == 0 &&
                name.equals(product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, amount, price);
    }

    @Override
    public int compareTo(Product o) {
        if(expiry<o.expiry)
            return -1;
        else if(expiry>o.expiry)
            return 1;
        else
            return 0;
    }
}
