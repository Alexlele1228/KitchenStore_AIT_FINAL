package com.example.kitchenstore.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kitchenstore.EditInventoryActivity;
import com.example.kitchenstore.PrepareAMealOptions;
import com.example.kitchenstore.PublishActivity;
import com.example.kitchenstore.R;
import com.example.kitchenstore.RvAdapter;
import com.example.kitchenstore.classes.Meal;
import com.example.kitchenstore.classes.Product;
import com.example.kitchenstore.classes.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomCookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomCookingFragment extends Fragment implements Serializable {
    public static HashMap<String,Integer> edit_custom=new HashMap<>();
    private ArrayList<Product> productList=new ArrayList<>();
    public static Meal meal_to_publish=null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomCookingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomCookingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomCookingFragment newInstance(String param1, String param2) {
        CustomCookingFragment fragment = new CustomCookingFragment();
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
        return inflater.inflate(R.layout.fragment_custom_cooking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button publish, leave;
        final RecyclerView rv;
        rv=view.findViewById(R.id.custom_use_rv);
        publish=view.findViewById(R.id.publish_btn);
        leave=view.findViewById(R.id.confrim_and_leave_btn);
        rv.setNestedScrollingEnabled(false);
        DatabaseReference mRef= FirebaseDatabase.getInstance().getReference().child("Inventories").child(Users.current_user.getKitchen_id()).child("stocking");
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
                    RvAdapter adapter = new RvAdapter(getContext(), 8, productList, productList.size());
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        leave.setOnClickListener(new View.OnClickListener() {
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
                                    if (edit_custom.containsKey(dataSnapshot1.getKey())) {

                                        int value=-1*edit_custom.get(dataSnapshot1.getKey());
                                        if(dataSnapshot1.child("amount").getValue(Integer.class)+value>0)
                                            mRef.child(postSnapshot.getKey()).child(snapshot1.getKey()).child(dataSnapshot1.getKey()).child("amount").setValue(ServerValue.increment(value));
                                        else
                                            mRef.child(postSnapshot.getKey()).child(snapshot1.getKey()).child(dataSnapshot1.getKey()).removeValue();
                                    }
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                getFragmentManager().popBackStack();
            }
        });

        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit_custom.size()<1)
                    return;
                meal_to_publish=new Meal();
                HashMap<Product,Integer> source=new HashMap<>();
                Set<String> set=edit_custom.keySet();
                Iterator iterator=set.iterator();
                while(iterator.hasNext())
                {
                    Product product=new Product();
                    product.setName(iterator.next().toString());
                    source.put(product,edit_custom.get(product.getName()));
                }
                meal_to_publish.setSource(source);
                Intent intent=new Intent(getContext(), PublishActivity.class);
                startActivity(intent);
            }
        });
    }
}