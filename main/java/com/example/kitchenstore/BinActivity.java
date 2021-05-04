package com.example.kitchenstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kitchenstore.classes.Product;
import com.example.kitchenstore.classes.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BinActivity extends AppCompatActivity {
    private RecyclerView rv;
    private TextView tv;
    private Button back;
    private ArrayList<Product> productList=new ArrayList<>();
    private static final FirebaseDatabase database= FirebaseDatabase.getInstance();
    private DatabaseReference mRef=database.getReference();
    private double waste_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.bin);
        rv =findViewById(R.id.binList_rv);
        tv=findViewById(R.id.bin_tv);
        back=findViewById(R.id.bin_back);
        rv.setNestedScrollingEnabled(false);
        mRef= mRef.child("Inventories").child(Users.current_user.getKitchen_id()).child("bin");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Product product=dataSnapshot.getValue(Product.class);
                    productList.add(product);
                    waste_money+=product.getAmount()*product.getPrice();
                }
                if(productList.size()>0) {
                    tv.setText("$"+waste_money+" had been wasted.");
                    RvAdapter adapter = new RvAdapter(BinActivity.this, 3, productList, productList.size());
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(BinActivity.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}