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
import com.google.firebase.database.core.utilities.Tree;

import java.util.ArrayList;
import java.util.TreeSet;

public class InventoryActivity extends AppCompatActivity {
    private RecyclerView rv;

    private Button back,edit;
    private TreeSet<Product> productList=new TreeSet<>();
    private static final FirebaseDatabase database= FirebaseDatabase.getInstance();
    private DatabaseReference mRef=database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_page);

        rv =findViewById(R.id.inventory_rv);
        edit=findViewById(R.id.inventory_edit_btn);
        back=findViewById(R.id.invewntory_go_back_btn);
        rv.setNestedScrollingEnabled(false);
        mRef= mRef.child("Inventories").child(Users.current_user.getKitchen_id()).child("stocking");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for(DataSnapshot date:snapshot.getChildren()){
                    for(DataSnapshot millstone: date.getChildren()) {
                        for (DataSnapshot productSnapshot : millstone.getChildren()) {
                            Product thisProduct = productSnapshot.getValue(Product.class);
                            thisProduct.setName(productSnapshot.getKey());
                            productList.add(thisProduct);

                        }
                    }
                }

                if(productList.size()>0) {
                    Log.d("TAG", "onDataChange: "+productList.size());
                    ArrayList<Product> thisList=new ArrayList<>(productList);
                    RvAdapter adapter = new RvAdapter(InventoryActivity.this, 4, thisList, productList.size());
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(InventoryActivity.this));
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

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InventoryActivity.this,EditInventoryActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }
}