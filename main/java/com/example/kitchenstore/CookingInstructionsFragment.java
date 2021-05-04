package com.example.kitchenstore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kitchenstore.classes.Meal;
import com.example.kitchenstore.classes.Product;
import com.example.kitchenstore.classes.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CookingInstructionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CookingInstructionsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private Meal mParam1;


    public CookingInstructionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment CookingInstructionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CookingInstructionsFragment newInstance(Meal param1) {
        CookingInstructionsFragment fragment = new CookingInstructionsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (Meal) getArguments().getSerializable(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.cooking_instructions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView title,instruction;
        final Button finish;
        final ImageView imageView;
        title=view.findViewById(R.id.instruction_recipe_title);
        instruction=view.findViewById(R.id.instruction_recipe_instruction);
        finish=view.findViewById(R.id.instruction_finish_btn);
        imageView=view.findViewById(R.id.instruction_recipe_iv);
        title.setText(mParam1.getName());
        instruction.setText(mParam1.getCooking_instruction());


        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(mParam1.getName() + ".jpeg");
        GlideApp.with(getContext())
                .load(mStorageRef)
                .into(imageView);

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<Product,Integer> bad_map=mParam1.getSource();
                final HashMap<String,Integer> good_map=new HashMap<>();
                Set<Product> set=bad_map.keySet();
                Iterator iterator=set.iterator();
                while(iterator.hasNext())
                {
                    Product product=(Product) iterator.next();
                    good_map.put(product.getName(),bad_map.get(product));
                }

                final DatabaseReference mRef= FirebaseDatabase.getInstance().getReference("/Inventories/"+ Users.current_user.getKitchen_id()+"/stocking");
                mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            //timemillstone
                            for (DataSnapshot snapshot1 : postSnapshot.getChildren()) {
                                //stocking
                                for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {
                                    if (good_map.containsKey(dataSnapshot1.getKey())) {

                                        int value=-1*good_map.get(dataSnapshot1.getKey());
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
    }
}