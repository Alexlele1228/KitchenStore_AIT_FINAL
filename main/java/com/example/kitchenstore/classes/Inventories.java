package com.example.kitchenstore.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Inventories {
    private String id;
    private ArrayList<String> members;
    private ArrayList<String> requestList;
    //Inner value representing amount of current product
    //The outside key representing name of Product
    private Map<String,ProductInDB> stocking;

    public static HashSet<Product> expiringProduct=new HashSet<>();
    //public static HashSet<Product> binProduct=new HashSet<>();

    public Inventories(String id, ArrayList<String> members, ArrayList<String> requestList, Map<String, ProductInDB> stocking) {
        this.id = id;
        this.members = members;
        this.requestList = requestList;
        this.stocking = stocking;
    }

    public Inventories() {
    }

    public ArrayList<String> getRequestList() {
        return requestList;
    }

    public void setRequestList(ArrayList<String> requestList) {
        this.requestList = requestList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public Map<String, ProductInDB> getStocking() {
        return stocking;
    }

    public void setStocking(Map<String, ProductInDB> stocking) {
        this.stocking = stocking;
    }
}
