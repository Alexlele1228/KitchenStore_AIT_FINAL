package com.example.kitchenstore.fragments;

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

import com.example.kitchenstore.HomepageFragment;
import com.example.kitchenstore.R;
import com.example.kitchenstore.classes.Inventories;
import com.example.kitchenstore.classes.ProductInDB;
import com.example.kitchenstore.classes.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateInventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateInventoryFragment extends Fragment {

    private Button create,join,request;
    private TextView title,body;
    private EditText editText;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateInventoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateInventoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateInventoryFragment newInstance(String param1, String param2) {
        CreateInventoryFragment fragment = new CreateInventoryFragment();
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
        return inflater.inflate(R.layout.fragment_create_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        title.setText("Welcome "+ Users.current_user.getUser_name()+":");
        final DatabaseReference mRef= FirebaseDatabase.getInstance().getReference();
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create new table in inventories table in db
                String id=mRef.child("Inventories").push().getKey();

                //update memberList
                ArrayList<String> memberList=new ArrayList<>();
                memberList.add(Users.current_user.getEmail());

                //add place holder for request table
                ArrayList<String> request=new ArrayList<>();
                request.add("requestUser@demo.com");

                //add place holder for stocking
                ProductInDB productInDB=new ProductInDB("text",-999,999);
                Map<String,ProductInDB> outMap=new HashMap<>();
                outMap.put(productInDB.getName(),productInDB);

                //initialize this inventory and push it into db
                Inventories inventory=new Inventories(id,memberList,request,outMap);
                mRef.child("Inventories").child(id).setValue(inventory);

                //update current user's kitchen id
                Users.current_user.setKitchen_id(id);

                //update kitchen id of current user in db
                mRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot snapshot1:snapshot.getChildren())
                        {
                            if(snapshot1.child("email").getValue()!=null &&snapshot1.child("email").getValue().equals(Users.current_user.getEmail()))
                              mRef.child("Users").child(snapshot1.getKey()).setValue(Users.current_user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                getFragmentManager().beginTransaction().replace(R.id.real_main_container, HomepageFragment.newInstance("",""),"home").commit();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create.setClickable(false);
                create.setVisibility(View.INVISIBLE);
                request.setVisibility(View.VISIBLE);
                request.setClickable(true);
                editText.setVisibility(View.VISIBLE);
                editText.setEnabled(true);
                join.setClickable(false);
                join.setVisibility(View.INVISIBLE);
                body.setText("Join others kitchen by entering specific kitchen ID below:");
            }
        });

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRef.child("Inventories").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean exits=true;
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            if(dataSnapshot.getKey()!=null && dataSnapshot.getKey().equals(editText.getText().toString().trim())) {
                               ArrayList<String> requestList=(ArrayList<String>) dataSnapshot.child("requestList").getValue();
                               if(requestList.contains(Users.current_user.getEmail()))
                                   Toast.makeText(getContext(),"You had already sent the request, please be patient.",Toast.LENGTH_SHORT).show();
                               else {
                                   requestList.add(Users.current_user.getEmail());
                                   mRef.child("Inventories").child(dataSnapshot.getKey()).child("requestList").setValue(requestList);
                                   Toast.makeText(getContext(), "Request sent.", Toast.LENGTH_SHORT).show();
                               }
                               exits=true;
                               break;
                            }else
                                exits=false;
                        }
                        if(!exits)
                            Toast.makeText(getContext(),"Kitchen does not exits with this ID.",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    private void initUI(View view){
        title=view.findViewById(R.id.tv_title);
        body=view.findViewById(R.id.tv_body);
        editText=view.findViewById(R.id.et_join_kitchen_id);
        request=view.findViewById(R.id.send_request_to_join_btn);
        join=view.findViewById(R.id.join_inventory_btn);
        create=view.findViewById(R.id.create_inventory_btn);
    }
}