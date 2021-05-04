package com.example.kitchenstore;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitchenstore.classes.Users;
import com.example.kitchenstore.fragments.CreateInventoryFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {
    private Button btn_sign_up;
    private EditText user_name,email,phone_number, password,confirm_password;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignUpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uiInit(view);

        //from invitation
        if(mParam1!=null && mParam2!=null) {
            email.setText(mParam1, TextView.BufferType.EDITABLE);
            email.setEnabled(false);
            btn_sign_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //field check
                    if(email.getText().toString().trim().equals("")|| password.getText().toString().trim().equals("")||user_name.getText().toString().trim().equals("")||phone_number.getText().toString().trim().equals(""))
                        Toast.makeText(getContext(),"Please fill up all fields above.",Toast.LENGTH_SHORT).show();
                    //password match check
                    else if(!password.getText().toString().trim().equals(confirm_password.getText().toString().trim()))
                        Toast.makeText(getContext(),"Password not match, please check again.",Toast.LENGTH_SHORT).show();
                    //good to register
                    else {
                        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
                        final Users user = new Users(mParam1, user_name.getText().toString().trim(), password.getText().toString().trim(), mParam2, phone_number.getText().toString().trim());
                        //add user in users table
                        mRef.child("Users").push().setValue(user);
                        //set global variable
                        Users.current_user = user;
                        //update inventory member list since this user is invited
                        mRef.child("Inventories").child(mParam2).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<String> member_list=(ArrayList<String>) snapshot.child("members").getValue();
                                member_list.add(user.getEmail());
                                mRef.child("Inventories").child(mParam2).child("members").setValue(member_list);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        getFragmentManager().beginTransaction().replace(R.id.real_main_container, HomepageFragment.newInstance("", ""), "home").commit();
                    }
                }
            });
        }else {
            //not from invitation
            btn_sign_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (email.getText().toString().trim().equals("") || password.getText().toString().trim().equals("") || user_name.getText().toString().trim().equals("") || phone_number.getText().toString().trim().equals("")) {
                        Toast.makeText(getContext(), "Please fill up all fields above.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!password.getText().toString().trim().equals(confirm_password.getText().toString().trim()))
                        Toast.makeText(getContext(), "Password not match, please check again.", Toast.LENGTH_SHORT).show();
                    else {
                        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("/Users/");
                        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean email_exits = false;
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Users user_in_db = new Users();
                                    user_in_db.setEmail(dataSnapshot.child("email").getValue().toString());
                                    if (user_in_db.getEmail().equals(email.getText().toString().trim())) {
                                        Toast.makeText(getContext(), "This email has already been registered.", Toast.LENGTH_SHORT).show();
                                        email_exits = true;
                                    }
                                }
                                if (!email_exits) {
                                    Users user = new Users(email.getText().toString().trim(), user_name.getText().toString().trim(), password.getText().toString().trim(), "null", phone_number.getText().toString().trim());
                                    mRef.push().setValue(user);

                                    Users.current_user = user;
                                    getFragmentManager().beginTransaction().replace(R.id.real_main_container, CreateInventoryFragment.newInstance("", ""), "home").commit();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            });
        }
    }

    private void uiInit(View v) {

        btn_sign_up=v.findViewById(R.id.btn_sign_up);
        user_name=v.findViewById(R.id.et_sign_up_user_name);
        email=v.findViewById(R.id.et_sign_up_email);
        phone_number=v.findViewById(R.id.et_sign_up_phone_number);
        password=v.findViewById(R.id.et_sign_up_password);
        confirm_password=v.findViewById(R.id.et_sign_up_confirm_password);
    }
}