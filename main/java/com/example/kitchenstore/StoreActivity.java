package com.example.kitchenstore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchenstore.classes.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity {
    private RecyclerView rv;
    private Button go_to_cart_btn;
    private ArrayList<Product> productList=new ArrayList<>();
    private static final FirebaseDatabase database= FirebaseDatabase.getInstance();
    private DatabaseReference mRef=database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_acticity);
        rv =findViewById(R.id.the_rv);
        go_to_cart_btn=findViewById(R.id.btn_go_to_cart);
        rv.setNestedScrollingEnabled(false);
        mRef= mRef.child("OnlineStore").child("StoreStocking");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Product product=new Product();
                    product.setAmount(dataSnapshot.child("amount").getValue(Integer.class));
                    product.setExpiry(dataSnapshot.child("expiryDays").getValue(Integer.class));
                    product.setName(dataSnapshot.child("name").getValue(String.class));
                    product.setPrice(dataSnapshot.child("price").getValue(Double.class));
                    productList.add(product);
                }
                RvAdapter adapter=new RvAdapter(StoreActivity.this,1,productList,productList.size());
                rv.setAdapter(adapter);
                rv.setLayoutManager(new LinearLayoutManager(StoreActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        go_to_cart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StoreActivity.this, CartActivity.class);
                startActivity(intent);

            }
        });
    }
}