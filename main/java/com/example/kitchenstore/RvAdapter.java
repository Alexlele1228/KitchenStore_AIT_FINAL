package com.example.kitchenstore;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchenstore.classes.Meal;
import com.example.kitchenstore.classes.Product;
import com.example.kitchenstore.fragments.CustomCookingFragment;
import com.example.kitchenstore.fragments.LoginFragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.MyViewHolder> {
    private Context context;
    private LayoutInflater mLayoutInflater;
    private  ArrayList<Product> productList;
    private StorageReference mStorageRef;
    private int listSize;
    private int whichContext;
    static HashMap<String,HashMap<String, Double>> cartList=new HashMap<>();
    private HashMap<String, Double> singleItem;
    Iterator iterator=cartList.keySet().iterator();
    Set<String> keySet=EditInventoryActivity.edit_inventory_map.keySet();
    Iterator iterator1=keySet.iterator();



    public RvAdapter(Context context, int whichContext, @Nullable ArrayList<Product> arrayList, @Nullable int listSize) {
        this.context = context;
        this.whichContext=whichContext;
        mLayoutInflater = LayoutInflater.from(context);
        this.productList=arrayList;
        this.listSize=listSize;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(mLayoutInflater.inflate(R.layout.rv_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        if(whichContext==5) {
            EditInventoryActivity.edit_inventory_map.clear();
            holder.name.setText(productList.get(position).getName());
            holder.price.setVisibility(View.INVISIBLE);
            holder.amount.setText("Amount Left: " + String.valueOf(productList.get(position).getAmount()));
            holder.amount_purchased.setText("0");
            holder.expiry.setVisibility(View.INVISIBLE);
            holder.btn_increase_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Integer.parseInt((String) holder.amount_purchased.getText())<productList.get(position).getAmount())
                    holder.amount_purchased.setText(String.valueOf(Integer.parseInt(holder.amount_purchased.getText().toString()) + 1));
                      EditInventoryActivity.edit_inventory_map.put(productList.get(position).getName(), Integer.parseInt((String) holder.amount_purchased.getText()));
                }
            });
            holder.btn_decrease_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(holder.amount_purchased.getText().toString()) > 0) {
                        holder.amount_purchased.setText(String.valueOf(Integer.parseInt(holder.amount_purchased.getText().toString()) - 1));
                        EditInventoryActivity.edit_inventory_map.put(productList.get(position).getName(), Integer.parseInt((String) holder.amount_purchased.getText()));

                        if(Integer.parseInt(holder.amount_purchased.getText().toString()) == 0)
                            EditInventoryActivity.edit_inventory_map.remove(productList.get(position).getName());
                    }

                }
            });


            mStorageRef = FirebaseStorage.getInstance().getReference(productList.get(position).getName() + ".jpeg");
            GlideApp.with(this.context)
                    .load(mStorageRef)
                    .into(holder.image);

            if ((position + 1) % 2 == 0) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFCCF5"));
            } else
                holder.cardView.setCardBackgroundColor(Color.parseColor("#CCFFFF"));
        }

        if(whichContext==1) {
            holder.name.setText(productList.get(position).getName());
            holder.price.setText("Price: $" + productList.get(position).getPrice());
            holder.amount.setText("Amount Left: " + String.valueOf(productList.get(position).getAmount()));
            holder.expiry.setText("Expiring in: " + String.valueOf(productList.get(position).getExpiry()) + " days");
            holder.btn_increase_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(productList.get(position).getAmount()==0)
                        return;
                    holder.amount_purchased.setText(String.valueOf(Integer.parseInt(holder.amount_purchased.getText().toString()) + 1));
                    if (cartList.get(productList.get(position).getName()) == null)
                    {
                        singleItem=new HashMap<>();
                        singleItem.put("amount",1.00);
                        singleItem.put("expiry", (double) productList.get(position).getExpiry());
                        singleItem.put("price",productList.get(position).getPrice());
                        cartList.put(productList.get(position).getName(), singleItem);
                    }
                    else {
                        singleItem=  cartList.get(productList.get(position).getName());
                        if(singleItem!=null)
                            singleItem.put("amount",singleItem.get("amount")+1);
                    }

                }
            });
            holder.btn_decrease_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(holder.amount_purchased.getText().toString()) > 0) {
                        holder.amount_purchased.setText(String.valueOf(Integer.parseInt(holder.amount_purchased.getText().toString()) - 1));
                        singleItem=  cartList.get(productList.get(position).getName());
                        singleItem.put("amount",singleItem.get("amount")-1);
                        if(Integer.parseInt(holder.amount_purchased.getText().toString()) == 0)
                            cartList.remove(productList.get(position).getName());
                    }

                }
            });


            mStorageRef = FirebaseStorage.getInstance().getReference(productList.get(position).getName() + ".jpeg");
            GlideApp.with(this.context)
                    .load(mStorageRef)
                    .into(holder.image);

            if ((position + 1) % 2 == 0) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFCCF5"));
            } else
                holder.cardView.setCardBackgroundColor(Color.parseColor("#CCFFFF"));
        }
        else if(whichContext==2){
            String currentName=null;

            if(iterator.hasNext()){
                currentName=iterator.next().toString();
                holder.name.setText(currentName);
                holder.price.setVisibility(View.INVISIBLE);
                holder.amount.setVisibility(View.INVISIBLE);
                holder.expiry.setVisibility(View.INVISIBLE);
                holder.btn_increase_cart.setVisibility(View.INVISIBLE);
                holder.btn_decrease_cart.setVisibility(View.INVISIBLE);
                holder.amount_purchased.setText(String.valueOf(cartList.get(currentName).get("amount").intValue()));
            }


            mStorageRef = FirebaseStorage.getInstance().getReference(currentName + ".jpeg");
            GlideApp.with(this.context)
                    .load(mStorageRef)
                    .into(holder.image);

            if ((position + 1) % 2 == 0) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFCCF5"));
            } else
                holder.cardView.setCardBackgroundColor(Color.parseColor("#CCFFFF"));
        }
        else if(whichContext==6){
            String currentName=null;


            if(iterator1.hasNext()){
                currentName=iterator1.next().toString();
                holder.name.setText(currentName);
                holder.price.setVisibility(View.INVISIBLE);
                holder.amount.setVisibility(View.INVISIBLE);
                holder.expiry.setVisibility(View.INVISIBLE);
                holder.btn_increase_cart.setVisibility(View.INVISIBLE);
                holder.btn_decrease_cart.setVisibility(View.INVISIBLE);
                holder.amount_purchased.setText(String.valueOf(EditInventoryActivity.edit_inventory_map.get(currentName)));
            }


            mStorageRef = FirebaseStorage.getInstance().getReference(currentName + ".jpeg");
            GlideApp.with(this.context)
                    .load(mStorageRef)
                    .into(holder.image);

            if ((position + 1) % 2 == 0) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFCCF5"));
            } else
                holder.cardView.setCardBackgroundColor(Color.parseColor("#CCFFFF"));
        }

        else if(whichContext==3){
            if(productList.size()>0){

                holder.name.setText(productList.get(position).getName());
                double amount=productList.get(position).getAmount();
                holder.price.setText("$"+String.valueOf(amount*productList.get(position).getPrice()));
                holder.amount.setText(String.valueOf(amount));
                holder.expiry.setVisibility(View.INVISIBLE);
                holder.btn_increase_cart.setVisibility(View.INVISIBLE);
                holder.btn_decrease_cart.setVisibility(View.INVISIBLE);
                holder.amount_purchased.setVisibility(View.INVISIBLE);;
            }


            mStorageRef = FirebaseStorage.getInstance().getReference(productList.get(position).getName() + ".jpeg");
            GlideApp.with(this.context)
                    .load(mStorageRef)
                    .into(holder.image);

            if ((position + 1) % 2 == 0) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFCCF5"));
            } else
                holder.cardView.setCardBackgroundColor(Color.parseColor("#CCFFFF"));
        }
        else if(whichContext==4){
            if(productList.size()>0){

                holder.name.setText(productList.get(position).getName());
                double amount=productList.get(position).getAmount();
                holder.amount_purchased.setText(String.valueOf(productList.get(position).getExpiry()));
                holder.price.setText("amount left: "+ amount);
                holder.amount.setVisibility(View.INVISIBLE);
                holder.expiry.setVisibility(View.INVISIBLE);
                holder.btn_increase_cart.setVisibility(View.INVISIBLE);
                holder.btn_decrease_cart.setVisibility(View.INVISIBLE);

            }


            mStorageRef = FirebaseStorage.getInstance().getReference(productList.get(position).getName() + ".jpeg");
            GlideApp.with(this.context)
                    .load(mStorageRef)
                    .into(holder.image);

            if ((position + 1) % 2 == 0) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFCCF5"));
            } else
                holder.cardView.setCardBackgroundColor(Color.parseColor("#CCFFFF"));
        }
        else if(whichContext==7){
            if(RecipeFinderMealsFragment.list.size()>0){
                String recipe_name=(RecipeFinderMealsFragment.list.get(position)).getName();
                recipe_name=recipe_name.replace('_',' ');
                holder.name.setText(recipe_name);
                holder.amount.setText((RecipeFinderMealsFragment.list.get(position)).toString());
                holder.btn_decrease_cart.setVisibility(View.INVISIBLE);
                holder.btn_decrease_cart.setEnabled(false);
                holder.amount_purchased.setVisibility(View.INVISIBLE);
                holder.btn_increase_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.real_main_container, CookingInstructionsFragment.newInstance(RecipeFinderMealsFragment.list.get(position)),"").addToBackStack("cooking").commit();
                    }
                });

                mStorageRef = FirebaseStorage.getInstance().getReference( recipe_name+ ".jpeg");

                GlideApp.with(this.context)
                        .load(mStorageRef)
                        .into(holder.image);
            }

            if (RecipeFinderMealsFragment.list.get(position).getWeight()<10 &&RecipeFinderMealsFragment.list.get(position).getWeight()!=0) {

                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFCCF5"));
            } else
                holder.cardView.setCardBackgroundColor(Color.parseColor("#CCFFFF"));
        }
        else if(whichContext==8) {
            CustomCookingFragment.edit_custom.clear();
            holder.name.setText(productList.get(position).getName());
            holder.price.setVisibility(View.INVISIBLE);
            holder.amount.setText("Amount Left: " + String.valueOf(productList.get(position).getAmount()));
            holder.amount_purchased.setText("0");
            holder.expiry.setVisibility(View.INVISIBLE);
            holder.btn_increase_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Integer.parseInt((String) holder.amount_purchased.getText())<productList.get(position).getAmount())
                        holder.amount_purchased.setText(String.valueOf(Integer.parseInt(holder.amount_purchased.getText().toString()) + 1));
                    CustomCookingFragment.edit_custom.put(productList.get(position).getName(), Integer.parseInt((String) holder.amount_purchased.getText()));
                }
            });
            holder.btn_decrease_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Integer.parseInt(holder.amount_purchased.getText().toString()) > 0) {
                        holder.amount_purchased.setText(String.valueOf(Integer.parseInt(holder.amount_purchased.getText().toString()) - 1));
                        CustomCookingFragment.edit_custom.put(productList.get(position).getName(), Integer.parseInt((String) holder.amount_purchased.getText()));

                        if(Integer.parseInt(holder.amount_purchased.getText().toString()) == 0)
                            CustomCookingFragment.edit_custom.remove(productList.get(position).getName());
                    }

                }
            });


            mStorageRef = FirebaseStorage.getInstance().getReference(productList.get(position).getName() + ".jpeg");
            GlideApp.with(this.context)
                    .load(mStorageRef)
                    .into(holder.image);

            if ((position + 1) % 2 == 0) {
                holder.cardView.setCardBackgroundColor(Color.parseColor("#FFCCF5"));
            } else
                holder.cardView.setCardBackgroundColor(Color.parseColor("#CCFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return this.listSize;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name,price,amount,amount_purchased,expiry;
        private ImageButton btn_increase_cart,btn_decrease_cart;
        private ImageView image;
        private CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            amount = itemView.findViewById(R.id.amount);
            expiry=itemView.findViewById(R.id.expriy);

            amount_purchased = itemView.findViewById(R.id.amount_purchased);
            btn_increase_cart = itemView.findViewById(R.id.btn_increase_cart);
            btn_decrease_cart = itemView.findViewById(R.id.btn_decrease_cart);
            image = itemView.findViewById(R.id.imageView);
            cardView = itemView.findViewById(R.id.cardview);


        }
    }
}
