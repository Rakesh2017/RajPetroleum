package com.enhabyto.rajpetroleum;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tapadoo.alerter.Alerter;
import dmax.dialog.SpotsDialog;
import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class BasicInformation extends Fragment {

    View view;
    private EditText name_et, designation_et, contact_et, age_et;
    private String name_tx, designation_tx, contact_tx, age_tx;

    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();


    private AlertDialog dialog;
    private FirebaseUser user;

    private int count = 1;


    public BasicInformation() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_basic_information, container, false);
        name_et = view.findViewById(R.id.bi_NameEditText);
        designation_et = view.findViewById(R.id.bi_DesignationEditText);
        contact_et = view.findViewById(R.id.bi_ContactEditText);
        age_et = view.findViewById(R.id.bi_AgeEditText);
        FancyButton submit_btn = view.findViewById(R.id.bi_Button);
        dialog = new SpotsDialog(getActivity(),R.style.Updating);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                name_tx = name_et.getText().toString().trim();
                designation_tx = designation_et.getText().toString().trim();
                contact_tx = contact_et.getText().toString().trim();
                age_tx = age_et.getText().toString().trim();

                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                    dialog.dismiss();
                    return;
                }

                if (TextUtils.isDigitsOnly(name_tx)){
                    Alerter.create(getActivity())
                            .setTitle("Digits in Name Not Allowed!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog.dismiss();
                    return;
                }

                if (name_tx.length()<2){
                    Alerter.create(getActivity())
                            .setTitle("Name too Short!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog.dismiss();
                    return;
                }

                if (designation_tx.length()<2){
                    Alerter.create(getActivity())
                            .setTitle("Please Provide full-Form of Designation!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog.dismiss();
                    return;
                }

                if (!TextUtils.isDigitsOnly(contact_tx)){
                    Alerter.create(getActivity())
                            .setTitle("Only digits are allowed!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog.dismiss();
                    return;
                }

                if (contact_tx.length()!=10){
                    Alerter.create(getActivity())
                            .setTitle("Invalid Mobile number!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(String.valueOf(age_tx))){
                    Alerter.create(getActivity())
                            .setTitle("Please Enter Age!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog.dismiss();
                    return;
                }

                DatabaseReference d_refAdminProfile;
                assert user != null;
                d_refAdminProfile = d_root.child("admin").child("profile");

                d_refAdminProfile.child("name").setValue(name_tx);
                d_refAdminProfile.child("designation").setValue(designation_tx);
                d_refAdminProfile.child("contact").setValue(contact_tx);
                d_refAdminProfile.child("age").setValue(age_tx);
                SharedPreferences dataSave = getActivity().getSharedPreferences("firstLog", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = dataSave.edit();
                editor.putString("LaunchApplication", "DashBoard");
                editor.apply();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(getContext(), DashBoard.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                },1500);


            }
        });


        return view;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void onStart(){
        super.onStart();

        DatabaseReference d_refAdminProfile;
        assert user != null;
        d_refAdminProfile = d_root.child("admin").child(user.getUid()).child("profile");

        if (count == 1){

            d_refAdminProfile.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    name_tx = dataSnapshot.child("name").getValue(String.class);
                    designation_tx = dataSnapshot.child("designation").getValue(String.class);
                    contact_tx = dataSnapshot.child("contact").getValue(String.class);
                    age_tx = dataSnapshot.child("age").getValue(String.class);

                        name_et.setText(name_tx);
                        designation_et.setText(designation_tx);
                        contact_et.setText(contact_tx);
                        age_et.setText(age_tx);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }
}
