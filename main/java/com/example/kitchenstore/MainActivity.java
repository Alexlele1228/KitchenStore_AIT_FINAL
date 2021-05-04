package com.example.kitchenstore;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kitchenstore.classes.Product;
import com.example.kitchenstore.classes.Users;
import com.example.kitchenstore.fragments.LoginFragment;
import com.example.kitchenstore.services.ExpiryService;
import com.example.kitchenstore.services.NotificationService;
import com.example.kitchenstore.services.PushExpiryService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getIntent().getStringExtra("FRAG")==null)
              getSupportFragmentManager().beginTransaction().add(R.id.real_main_container,LoginFragment.newInstance("",""),"").commit();
        else {
            switch (getIntent().getStringExtra("FRAG"))
            {
                case "sign_up":{
                    String current_email=getIntent().getStringExtra("EMAIL");
                    String kitchen_id=getIntent().getStringExtra("KITCHEN_ID");
                    getSupportFragmentManager().beginTransaction().add(R.id.real_main_container,SignUpFragment.newInstance(current_email,kitchen_id),"home").addToBackStack("signUp").commit();
                    break;
                }
                case "log_in": {
                    String current_email = getIntent().getStringExtra("EMAIL");
                    String kitchen_id = getIntent().getStringExtra("KITCHEN_ID");
                    getSupportFragmentManager().beginTransaction().add(R.id.real_main_container, LoginFragment.newInstance(current_email, kitchen_id), "home").addToBackStack("login").commit();
                    break;
                }
                case "inventory": {
                    final String current_email = getIntent().getStringExtra("EMAIL");
                    final String kitchen_id = getIntent().getStringExtra("KITCHEN_ID");
                    Log.d("123", current_email);
                    Log.d("123", kitchen_id);
                    final DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
                    mRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot:snapshot.getChildren())
                            {
                                if(dataSnapshot.child("email").getValue().equals(current_email)){
                                    mRef.child("Users").child(dataSnapshot.getKey()).child("kitchen_id").setValue(kitchen_id);
                                    Users.current_user.setKitchen_id(kitchen_id);
                                }
                                //..
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    mRef.child("Inventories").child(kitchen_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<String> member_list=(ArrayList<String>)snapshot.child("members").getValue();
                            member_list.add(current_email);
                            mRef.child("Inventories").child(kitchen_id).child("members").setValue(member_list);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    getSupportFragmentManager().beginTransaction().add(R.id.real_main_container, HomepageFragment.newInstance("home", "home"), "home").addToBackStack("home").commit();
                    break;
                }
            }
        }



    }



    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
       // super.onSaveInstanceState(outState, outPersistentState);
    }
}