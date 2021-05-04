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

import com.example.kitchenstore.classes.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

public class ComfirmEditActivity extends AppCompatActivity {

    private RecyclerView edit_rv;
    private Button btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comfirm_edit);

        btn_confirm=findViewById(R.id.confirm_edit_btn);
        edit_rv=findViewById(R.id.confirm_edit_rv);
        edit_rv.setNestedScrollingEnabled(false);
        edit_rv.setLayoutManager(new LinearLayoutManager(this));
        edit_rv.setAdapter(new RvAdapter(this,6,null, EditInventoryActivity.edit_inventory_map.size()));
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference mRef= FirebaseDatabase.getInstance().getReference("/Inventories/"+ Users.current_user.getKitchen_id()+"/stocking");
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            //timemillstone
                            for (DataSnapshot snapshot1 : postSnapshot.getChildren()) {
                                //stocking
                                for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {
                                    if (EditInventoryActivity.edit_inventory_map.containsKey(dataSnapshot1.getKey())) {
                                        int value=-1*EditInventoryActivity.edit_inventory_map.get(dataSnapshot1.getKey());
                                        if(dataSnapshot1.child("amount").getValue(Integer.class)+value>0)
                                            mRef.child(postSnapshot.getKey()).child(snapshot1.getKey()).child(dataSnapshot1.getKey()).child("amount").setValue(ServerValue.increment(value));
                                        else
                                            mRef.child(postSnapshot.getKey()).child(snapshot1.getKey()).child(dataSnapshot1.getKey()).removeValue();
                                    }
                                }
                            }
                        }
                        EditInventoryActivity.edit_inventory_map.clear();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                    finish();
            }
        });


    }
}