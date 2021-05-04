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

import com.example.kitchenstore.classes.Product;
import com.example.kitchenstore.classes.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class EditInventoryActivity extends AppCompatActivity {
    private RecyclerView rv;
    private Button confirm;
    private ArrayList<Product> productList=new ArrayList<>();
    private static final FirebaseDatabase database= FirebaseDatabase.getInstance();
    private DatabaseReference mRef=database.getReference();
    public static HashMap<String,Integer> edit_inventory_map=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_inventory);
        rv =findViewById(R.id.edit_inventory_rv);
        confirm=findViewById(R.id.conrim_edit_btn);
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
                    RvAdapter adapter = new RvAdapter(EditInventoryActivity.this, 5, productList, productList.size());
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(EditInventoryActivity.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent intent=new Intent(EditInventoryActivity.this,ComfirmEditActivity.class);
                 startActivity(intent);
                 finish();

            }
        });
    }
}