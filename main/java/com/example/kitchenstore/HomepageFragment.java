package com.example.kitchenstore;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kitchenstore.fragments.InviteUserFragment;
import com.example.kitchenstore.services.ExpiryService;
import com.example.kitchenstore.services.NotificationService;
import com.example.kitchenstore.services.PushExpiryService;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomepageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomepageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomepageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomepageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomepageFragment newInstance(String param1, String param2) {
        HomepageFragment fragment = new HomepageFragment();
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


        return inflater.inflate(R.layout.homepage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //start listening for inventory changes
        final Intent intent=new Intent(getContext(), NotificationService.class);
        getActivity().startService(intent);


        Thread threadA=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                     Intent intentA=new Intent(getContext(), PushExpiryService.class);
                      getActivity().startService(intentA);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        getActivity().stopService(intentA);
                    }
                }
            }
        });
        threadA.setPriority(Thread.MAX_PRIORITY);
threadA.start();
        //assuming edit data daily
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Intent intentB = new Intent(getContext(), ExpiryService.class);
                    getActivity().startService(intentB);
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        getActivity().stopService(intentB);
                    }
                }
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
  thread.start();

        //...
        Button inventoryBtn, prepareMealBtn, expiredBtn, binBtn, inviteBtn, onlineStoreBtn;
        inventoryBtn=view.findViewById(R.id.inventory_button);
        expiredBtn=view.findViewById(R.id.expired_items_button_menu);
        binBtn=view.findViewById(R.id.bin_button_menu);
        prepareMealBtn=view.findViewById(R.id.prepare_meal_button_menu);
        inviteBtn=view.findViewById(R.id.invite_button_menu);
        onlineStoreBtn=view.findViewById(R.id.online_store_button_menu);

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.real_main_container, InviteUserFragment.newInstance("",""),"invite").addToBackStack("invite").commit();

            }
        });

        onlineStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(".store");
                startActivity(intent);
            }
        });
        inventoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(getContext(),InventoryActivity.class);
                startActivity(intent1);
            }
        });
        binBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                     Intent intent1=new Intent(getContext(),BinActivity.class);
                     startActivity(intent1);
            }
        });
        prepareMealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              getFragmentManager().beginTransaction().replace(R.id.real_main_container,PrepareAMealOptions.newInstance("",""),"prepare").addToBackStack("prepare").commit();
            }
        });
    }


}