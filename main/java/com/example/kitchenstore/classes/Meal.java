package com.example.kitchenstore.classes;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kitchenstore.InventoryActivity;
import com.example.kitchenstore.RvAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class Meal implements Comparable<Meal>, Serializable {
    private HashMap<String,Product> current_inventory_map=new HashMap();
    private HashMap<Product,Integer> source;
    private String name;
    double weight;
    private String cooking_instruction;

    public Meal(HashMap<Product, Integer> source,String cooking_instruction) {
        this.source = source;
        this.cooking_instruction=cooking_instruction;
        calculateWeight(source);
    }


    public String getCooking_instruction() {
        return cooking_instruction;
    }

    public void setCooking_instruction(String cooking_instruction) {
        this.cooking_instruction = cooking_instruction;
    }

    private void calculateWeight(final HashMap<Product, Integer> source) {
        DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
        mRef= mRef.child("Inventories").child(Users.current_user.getKitchen_id()).child("stocking");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                current_inventory_map.clear();
                for(DataSnapshot date:snapshot.getChildren()){
                    for(DataSnapshot millstone: date.getChildren()) {
                        for (DataSnapshot productSnapshot : millstone.getChildren()) {
                            Product thisProduct = productSnapshot.getValue(Product.class);
                            thisProduct.setName(productSnapshot.getKey());
                            current_inventory_map.put(thisProduct.getName(),thisProduct);
                        }
                    }
                }
                Log.d("TAG", "Inventory: "+current_inventory_map.toString());
                Set<Product> product_requires=source.keySet();
                Iterator iterator=product_requires.iterator();
                while(iterator.hasNext())
                {
                    Product this_required_product=(Product) iterator.next();
                    if(!current_inventory_map.containsKey(this_required_product.getName()))
                    {
                        weight=0;
                        break;
                    }else
                    {
                        weight+=current_inventory_map.get(this_required_product.getName()).getExpiry();
                    }
                }

                Log.d("TAG", "weight: " + weight+"  source: " + source.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Meal() {
    }

    public HashMap<Product, Integer> getSource() {
        return source;
    }

    public void setSource(HashMap<Product, Integer> source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Meal o) {
        if(weight<o.weight)
            return 1;
        else if(weight>o.weight)
            return -1;
        else
            return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meal meal = (Meal) o;
        return name.equals(((Meal) o).name);
    }

    @Override
    public int hashCode() {
        return Objects.hash( source, name, weight);
    }

    @Override
    public String toString() {
        HashMap<String,Integer> map=new HashMap<>();
        Set<Product> set=source.keySet();
        Iterator iterator=set.iterator();
        while(iterator.hasNext())
        {
            Product thisProduct=(Product)iterator.next();
            map.put(thisProduct.getName(),source.get(thisProduct));
        }
        StringBuilder returnValue=new StringBuilder("Require food source:\n\n");
        Set<String> setA=map.keySet();
        Iterator iteratorA=setA.iterator();
        while(iteratorA.hasNext())
        {
            String currentName=iteratorA.next().toString();
            returnValue.append(currentName);
            returnValue.append("-->");
            returnValue.append(map.get(currentName).toString());
            if(iteratorA.hasNext())
            returnValue.append("\n\n");
        }
        return returnValue.toString();
    }
}
