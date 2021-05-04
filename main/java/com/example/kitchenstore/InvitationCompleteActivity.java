package com.example.kitchenstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kitchenstore.classes.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InvitationCompleteActivity extends AppCompatActivity {

    private Button to_sign_up_btn,to_inventory_btn;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation_complete2);
        textView=findViewById(R.id.invitation_entrance_tv);
        to_sign_up_btn=findViewById(R.id.invitation_to_sign_up_btn);
        to_inventory_btn=findViewById(R.id.invitation_to_inventory_btn);
        Uri uri = getIntent().getData();

        final String kitchen_id= uri.getQueryParameter("kitchen_id");
        final String current_email= uri.getQueryParameter("email");

        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference mRef=database.getReference();
        mRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    if(dataSnapshot.child("email").getValue()!=null && dataSnapshot.child("email").getValue().equals(current_email)){
                        if(Users.current_user.getEmail()==null)
                            suggestToLogin(kitchen_id,current_email);
                        else
                            suggestToCheckInventory(kitchen_id,current_email);
                    }else
                        suggestToSignUp(kitchen_id,current_email);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void suggestToSignUp(final String kitchen_id, final String current_email) {
        textView.setText("Looks like you don't have an account with this email yet, want to sign up now?");
        to_sign_up_btn.setClickable(true);
        to_sign_up_btn.setVisibility(View.VISIBLE);
        to_inventory_btn.setClickable(false);
        to_inventory_btn.setVisibility(View.INVISIBLE);
        to_sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InvitationCompleteActivity.this,MainActivity.class);
                intent.putExtra("FRAG","sign_up");
                intent.putExtra("EMAIL",current_email);
                intent.putExtra("KITCHEN_ID", kitchen_id);
                startActivity(intent);
                finish();
            }
        });
    }

    private void suggestToCheckInventory(final String kitchen_id, final String current_email) {
        textView.setText("Welcome back "+ Users.current_user.getUser_name()+", let's check the inventory you just joined!");
        to_inventory_btn.setClickable(true);
        to_inventory_btn.setVisibility(View.VISIBLE);
        to_sign_up_btn.setVisibility(View.INVISIBLE);
        to_sign_up_btn.setClickable(false);

        to_inventory_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InvitationCompleteActivity.this,MainActivity.class);
                intent.putExtra("FRAG","inventory");
                intent.putExtra("EMAIL",current_email);
                intent.putExtra("KITCHEN_ID",kitchen_id);
                startActivity(intent);
                finish();
            }
        });
    }

    private void suggestToLogin(final String kitchen_id, final String email) {
        //update inventories member list coz this user is the new member
        final DatabaseReference mRef=FirebaseDatabase.getInstance().getReference();
        mRef.child("Inventories").child(kitchen_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> member_list=(ArrayList<String>) snapshot.child("members").getValue();
                member_list.add(email);
                mRef.child("Inventories").child(kitchen_id).child("members").setValue(member_list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       //get the user to the login page with
       Intent intent=new Intent(InvitationCompleteActivity.this,MainActivity.class);
       intent.putExtra("FRAG","log_in");
       intent.putExtra("EMAIL",email);
       intent.putExtra("KITCHEN_ID",kitchen_id);
       startActivity(intent);
       finish();
    }
}