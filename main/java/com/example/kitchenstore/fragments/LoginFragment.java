package com.example.kitchenstore.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitchenstore.HomepageFragment;
import com.example.kitchenstore.R;
import com.example.kitchenstore.SignUpFragment;
import com.example.kitchenstore.classes.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    private Button btn_login,btn_to_sign_up;
    private EditText email, password;
    private boolean user_exits=false;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        email=view.findViewById(R.id.et_login_email);
        password=view.findViewById(R.id.et_login_password);
        btn_login=view.findViewById(R.id.btn_login);
        btn_to_sign_up=view.findViewById(R.id.btn_to_sign_up);

        btn_to_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.real_main_container, SignUpFragment.newInstance(null,null),"sign_up").addToBackStack("login").commit();
            }
        });

        if(mParam1!=null && mParam2!=null) {
            email.setText(mParam1, TextView.BufferType.EDITABLE);
            password.requestFocus();
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("/Users/");
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            //if find the email user gave in db, set user_exits=true
                            if (email.getText().toString().trim().equals(dataSnapshot.child("email").getValue().toString()) &&
                                    password.getText().toString().trim().equals(dataSnapshot.child("password").getValue().toString())) {
                                user_exits=true;

                                //initialize this login user from db
                                Users user_in_db = new Users();
                                user_in_db.setEmail(dataSnapshot.child("email").getValue().toString());
                                user_in_db.setPassword(dataSnapshot.child("password").getValue().toString());
                                user_in_db.setPhone_number(dataSnapshot.child("phone_number").getValue().toString());
                                user_in_db.setUser_name(dataSnapshot.child("user_name").getValue().toString());
                                user_in_db.setKitchen_id(dataSnapshot.child("kitchen_id").getValue().toString());

                                //if mParam2 is not null then it means user come from invitation
                                //if its come from invitation, set kitchen id of this user in db because it hasn't been updated in InvitationCompleteActivity
                                //check if the login email equals to the one in intent data as well, in case user login another account
                                if(mParam2!=null && user_in_db.getEmail().equals(mParam1)) {
                                    user_in_db.setKitchen_id(mParam2);
                                    mRef.child(dataSnapshot.getKey()).child("kitchen_id").setValue(user_in_db.getKitchen_id());
                                }

                                //set global variable
                                Users.current_user = user_in_db;

                                //if user have no inventory, in default kitchen_id would be set as 'null' in db
                                if(!user_in_db.getKitchen_id().equals("null"))
                                getFragmentManager().beginTransaction().replace(R.id.real_main_container, HomepageFragment.newInstance("", ""), "home").commit();
                                else
                                    getFragmentManager().beginTransaction().replace(R.id.real_main_container,CreateInventoryFragment.newInstance("",""),"create_inventory").commit();
                            }
                        }
                        if(!user_exits)
                            Toast.makeText(getContext(), "Login failed, please try again.", Toast.LENGTH_LONG).show();


                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

}