package com.example.kitchenstore;

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

import com.example.kitchenstore.classes.Meal;
import com.example.kitchenstore.classes.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeFinderMealsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeFinderMealsFragment extends Fragment {
   public static TreeSet<Meal> meal_set=new TreeSet<>();
    public static ArrayList<Meal> list=null;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecipeFinderMealsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecipeFinderMealsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeFinderMealsFragment newInstance(String param1, String param2) {
        RecipeFinderMealsFragment fragment = new RecipeFinderMealsFragment();
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
        return inflater.inflate(R.layout.recipe_finder_meals, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView rv;
        rv =view.findViewById(R.id.recipe_rv);
        rv.setNestedScrollingEnabled(false);
        DatabaseReference mRef= FirebaseDatabase.getInstance().getReference("/Exits_Recipes");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                meal_set.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    HashMap<Product,Integer> source=new HashMap<>();
                    String instruction="";
                    for(DataSnapshot dataSnapshot:snapshot1.getChildren())
                    {
                        if(!dataSnapshot.getKey().equals("instruction")) {
                            Product product = new Product();
                            product.setName(dataSnapshot.getKey());
                            source.put(product, dataSnapshot.getValue(Integer.class));
                        }else
                            instruction=dataSnapshot.getValue(String.class);
                    }
                   Meal thisMeal=new Meal(source, instruction);
                    thisMeal.setName(snapshot1.getKey());
                    meal_set.add(thisMeal);
                    list=new ArrayList<>(meal_set);
                }

                if(meal_set.size()>0) {

                    RvAdapter adapter = new RvAdapter(getContext(), 7, null, meal_set.size());
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}