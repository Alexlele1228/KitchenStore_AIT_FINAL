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
import android.widget.Toast;

import com.example.kitchenstore.R;
import com.example.kitchenstore.classes.Users;
import com.example.kitchenstore.email.SendMailUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InviteUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InviteUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InviteUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InviteUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InviteUserFragment newInstance(String param1, String param2) {
        InviteUserFragment fragment = new InviteUserFragment();
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
        return inflater.inflate(R.layout.fragment_invite_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button invite_btn;
        final EditText et_email;
        invite_btn=view.findViewById(R.id.invite_user_btn);
        et_email=view.findViewById(R.id.invite_email_et);
        final String email_Title="[Kitchen Store]: An invitation from "+ Users.current_user.getUser_name();

        invite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_email.getText().toString().trim().equals(""))
                    return;
                String email_body="Dear "+et_email.getText().toString()+":\n"+"\nWe are very happy to tell you that you were invited By " + Users.current_user.getUser_name()+" to join his/her inventory.\n Simply clicking the link below you will confirm this invitation: \n"
                        + "https://kitchenstore-1bd6a.web.app/invite.html?param=" +Users.current_user.getKitchen_id()+"&email="+et_email.getText().toString().trim() +"\n\n\n Please ignore this email if you don't want to join.\n\nRegards\nKitchen Store";
                SendMailUtil.send(et_email.getText().toString().trim(),email_Title,email_body);
                et_email.setText("");
                Toast.makeText(getContext(),"Invitation has been sent.",Toast.LENGTH_LONG).show();
            }
        });

    }
}