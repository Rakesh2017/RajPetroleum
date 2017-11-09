package com.enhabyto.rajpetroleum;


import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tapadoo.alerter.Alerter;

import java.util.Calendar;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import dmax.dialog.SpotsDialog;
import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateSubAdmin extends Fragment implements View.OnClickListener {

    private View view;
    FancyButton openCreateSubAdmin_btn, openUpdateSubAdmin_btn, load_btn, submit_btn, createSubAdmin_btn;
    EditText contact1_et, contact2_et, password_et, name_et, address_et, birth_et;
    String contact1_tx, contact2_tx, password_tx, name_tx, address_tx, birth_tx;
    RadioRealButtonGroup driver_group, pump_group, truck_group;
    String driver_permission, pump_permission, truck_permission;

    AlertDialog dialogCreatingSubAdmin ,dialog_loading_data, dialog_updating_data;

    DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    DatabaseReference d_SubAdminCredentials = d_root.child("sub_admin_credentials");
    DatabaseReference d_subAdminProfiles = d_root.child("sub_admin_profiles");
    DatabaseReference d_parent = FirebaseDatabase.getInstance().getReference().child("checkNetwork").child("isConnected");
    String connected, firebase_identity;


    public CreateSubAdmin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_sub_admin, container, false);

        contact1_et = view.findViewById(R.id.cs_contactEditText);
        contact2_et = view.findViewById(R.id.cs_contact1EditText);
        password_et = view.findViewById(R.id.cs_passwordEditText);
        name_et = view.findViewById(R.id.cs_nameEditText);
        address_et = view.findViewById(R.id.cs_addressEditText);
        birth_et = view.findViewById(R.id.cs_birthEditText);

        openCreateSubAdmin_btn = view.findViewById(R.id.cs_openCreateSubButton);
        openUpdateSubAdmin_btn = view.findViewById(R.id.cs_openUpdateSubProfileButton);
        load_btn = view.findViewById(R.id.cs_checkInfoButton);
        submit_btn = view.findViewById(R.id.cs_submitButton);
        createSubAdmin_btn = view.findViewById(R.id.cs_createSubAdminButton);

        driver_group = view.findViewById(R.id.cs_driverSelector);
        pump_group = view.findViewById(R.id.cs_pumpSelector);
        truck_group = view.findViewById(R.id.cs_truckSelector);


        openCreateSubAdmin_btn.setOnClickListener(this);
        openUpdateSubAdmin_btn.setOnClickListener(this);
        load_btn.setOnClickListener(this);
        submit_btn.setOnClickListener(this);
        createSubAdmin_btn.setOnClickListener(this);

        dialogCreatingSubAdmin = new SpotsDialog(getActivity(),R.style.creatingSubAdminAccount);
        dialog_loading_data = new SpotsDialog(getActivity(),R.style.loadingData);
        dialog_updating_data = new SpotsDialog(getActivity(),R.style.Updating);

        // onClickButton listener detects any click performed on buttons by touch
        driver_group.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                if (position == 0){
                    driver_permission = "denied";
                }
                else {
                    driver_permission = "granted";

                }
            }
        });


        // onClickButton listener detects any click performed on buttons by touch
        pump_group.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                if (position == 1){
                    pump_permission = "denied";
                }
                else {
                    pump_permission = "granted";

                }
            }
        });



        // onClickButton listener detects any click performed on buttons by touch
        truck_group.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                if (position == 0){
                    truck_permission = "denied";
                }
                else {
                    truck_permission = "granted";

                }
            }
        });



        return view;
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id){

            case R.id.cs_openCreateSubButton:
                view.findViewById(R.id.cs_CreateProfileRelativeLayout).setVisibility(View.VISIBLE);
                //  view.findViewById(R.id.cd_InfoRelativeLayout).setVisibility(View.GONE);
                YoYo.with(Techniques.FadeInLeft)
                        .duration(1000)
                        .repeat(0)
                        .playOn( view.findViewById(R.id.cs_CreateProfileRelativeLayout));
                YoYo.with(Techniques.FadeOutRight)
                        .duration(400)
                        .repeat(0)
                        .playOn( view.findViewById(R.id.cs_loadInfoRelativeLayout));
                YoYo.with(Techniques.FadeOutRight)
                        .duration(400)
                        .repeat(0)
                        .playOn( view.findViewById(R.id.cs_loadInfoRelativeLayout));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        contact1_et.setText("");
                        view.findViewById(R.id.cs_loadInfoRelativeLayout).setVisibility(View.GONE);
                        view.findViewById(R.id.cs_loadInfoRelativeLayout).setVisibility(View.GONE);
                    }
                },400);
                break;


            case R.id.cs_openUpdateSubProfileButton:

                // view.findViewById(R.id.cd_profileRelativeLayout).setVisibility(View.GONE);
                view.findViewById(R.id.cs_loadInfoRelativeLayout).setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInRight)
                        .duration(1000)
                        .repeat(0)
                        .playOn( view.findViewById(R.id.cs_loadInfoRelativeLayout));
                YoYo.with(Techniques.FadeOutLeft)
                        .duration(500)
                        .repeat(0)
                        .playOn( view.findViewById(R.id.cs_CreateProfileRelativeLayout));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.findViewById(R.id.cs_CreateProfileRelativeLayout).setVisibility(View.GONE);
                    }
                },400);

                break;

//                create sub admin

            case R.id.cs_createSubAdminButton:

                dialogCreatingSubAdmin.show();
                contact1_tx = contact1_et.getText().toString().trim();
                password_tx = password_et.getText().toString().trim();


                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                    dialogCreatingSubAdmin.dismiss();
                    return;
                }
                if (!TextUtils.isDigitsOnly(contact1_tx)){
                    Alerter.create(getActivity())
                            .setTitle("Only digits are allowed!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialogCreatingSubAdmin.dismiss();
                    return;
                }

                if (contact1_tx.length()!=10){
                    Alerter.create(getActivity())
                            .setTitle("Invalid Mobile number!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialogCreatingSubAdmin.dismiss();

                    return;
                }

                if (password_tx.length()<6){
                    Alerter.create(getActivity())
                            .setTitle("Length of password should be at least 6")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialogCreatingSubAdmin.dismiss();

                    return;
                }

                d_parent.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        connected = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                d_SubAdminCredentials.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        firebase_identity = dataSnapshot.child(contact1_tx).child("identity").getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (!TextUtils.equals(connected, "connected")){
                            Alerter.create(getActivity())
                                    .setTitle("Unable to Connect to Server!")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.no_internet)
                                    .show();
                            //    Log.w("123", connected);
                            dialogCreatingSubAdmin.dismiss();
                            return;
                        }

                        if (contact1_tx.equals(firebase_identity)){


                            Alerter.create(getActivity())
                                    .setTitle("Sub-admin Account with "+firebase_identity+ " already exist!")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.error)
                                    .show();
                            dialogCreatingSubAdmin.dismiss();
                            return;
                        }


                        d_SubAdminCredentials.child(contact1_tx).child("identity").setValue(contact1_tx);
                        d_SubAdminCredentials.child(contact1_tx).child("password").setValue(password_tx);

                        d_subAdminProfiles.child(contact1_tx).child("permissions").child("truck_permission").setValue("denied");
                        d_subAdminProfiles.child(contact1_tx).child("permissions").child("pump_permission").setValue("denied");
                        d_subAdminProfiles.child(contact1_tx).child("permissions").child("driver_permission").setValue("denied");


                        Alerter.create(getActivity())
                                .setTitle("Sub-Admin Account Created")
                                .setContentGravity(1)
                                .setBackgroundColorRes(R.color.black)
                                .setIcon(R.drawable.success_icon)
                                .show();
                       dialogCreatingSubAdmin.dismiss();

                        view.findViewById(R.id.cs_loadInfoRelativeLayout).setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInRight)
                                .duration(1000)
                                .repeat(0)
                                .playOn( view.findViewById(R.id.cs_loadInfoRelativeLayout));
                        YoYo.with(Techniques.FadeOutLeft)
                                .duration(500)
                                .repeat(0)
                                .playOn( view.findViewById(R.id.cs_CreateProfileRelativeLayout));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.findViewById(R.id.cs_CreateProfileRelativeLayout).setVisibility(View.GONE);
                            }
                        },400);
                        password_et.setText("");
                        contact1_et.setText("");
                        contact2_et.setText(contact1_tx);

                    }
                },3000);
                break;


//                LoadingInfoButton

            case R.id.cs_checkInfoButton:

                dialog_loading_data.show();
                contact2_tx = contact2_et.getText().toString().trim();

                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                    dialog_loading_data.dismiss();
                    return;
                }
                if (!TextUtils.isDigitsOnly(contact2_tx)){
                    Alerter.create(getActivity())
                            .setTitle("Only digits are allowed!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog_loading_data.dismiss();
                    return;
                }

                if (contact2_tx.length()!=10){
                    Alerter.create(getActivity())
                            .setTitle("Invalid Mobile number!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog_loading_data.dismiss();

                    return;
                }
                d_parent.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        connected = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                d_SubAdminCredentials.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        firebase_identity = dataSnapshot.child(contact2_tx).child("identity").getValue(String.class);
                        d_subAdminProfiles.child(contact2_tx).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                name_tx = dataSnapshot.child("name").getValue(String.class);
                                address_tx = dataSnapshot.child("address").getValue(String.class);
                                birth_tx = dataSnapshot.child("birth").getValue(String.class);
                                truck_permission = dataSnapshot.child("permissions").child("truck_permission").getValue(String.class);
                                pump_permission = dataSnapshot.child("permissions").child("truck_permission").getValue(String.class);
                                truck_permission = dataSnapshot.child("permissions").child("truck_permission").getValue(String.class);

                                if (TextUtils.equals(truck_permission, "granted"))
                                    truck_group.setPosition(0);
                                else
                                    truck_group.setPosition(0);

                                if (TextUtils.equals(pump_permission, "granted"))
                                    pump_group.setPosition(0);
                                else
                                    pump_group.setPosition(0);

                                if (TextUtils.equals(driver_permission, "granted"))
                                    driver_group.setPosition(0);
                                else
                                    driver_group.setPosition(0);



                                name_et.setText(name_tx);
                                address_et.setText(address_tx);
                                birth_et.setText(birth_tx);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Alerter.create(getActivity())
                                .setTitle(databaseError.toException().getMessage())
                                .setContentGravity(1)
                                .setBackgroundColorRes(R.color.black)
                                .setIcon(R.drawable.error)
                                .show();
                        dialog_loading_data.dismiss();


                    }
                });


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //  Toast.makeText(getContext(), ""+firebase_identity, Toast.LENGTH_SHORT).show();
                        if (!TextUtils.equals(connected, "connected")){
                            Alerter.create(getActivity())
                                    .setTitle("Unable to Connect to Server!")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.no_internet)
                                    .show();
                            //    Log.w("123", connected);
                            dialog_loading_data.dismiss();
                            return;
                        }

                        try {

                            if (TextUtils.isEmpty(firebase_identity) && !contact2_tx.equals(firebase_identity)){
                                Alerter.create(getActivity())
                                        .setTitle("Sub-Admin with "+contact2_tx+ " does not exist!")
                                        .setContentGravity(1)
                                        .setBackgroundColorRes(R.color.black)
                                        .setIcon(R.drawable.error)
                                        .show();
                                dialog_loading_data.dismiss();

                            }
                            else {
                                view.findViewById(R.id.cs_subAdminDataRelativeLayout).setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.SlideInDown)
                                        .duration(1000)
                                        .repeat(0)
                                        .playOn( view.findViewById(R.id.cs_subAdminDataRelativeLayout));
                            }




                            dialog_loading_data.dismiss();


                        }
                        catch (NullPointerException e){
                            e.printStackTrace();
                        }



                    }
                },3000);
                break;


//                update profile

            case R.id.cd_SubmitButton:
                dialog_updating_data.show();

                contact2_tx = contact2_et.getText().toString().trim();
                name_tx = name_et.getText().toString().trim();
                address_tx = address_et.getText().toString().trim();
                birth_tx = birth_et.getText().toString().trim();

                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                    dialog_loading_data.dismiss();
                    return;
                }


                d_parent.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        connected = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (!TextUtils.equals(connected, "connected")){
                            Alerter.create(getActivity())
                                    .setTitle("Unable to Connect to Server!")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.no_internet)
                                    .show();
                            //    Log.w("123", connected);
                            dialog_loading_data.dismiss();
                            return;
                        }


                        DatabaseReference d_SubAdminProfile = d_root.child("sub_admin_profiles").child(contact2_tx);
                        //  Toast.makeText(getContext(), ""+contact1_tx, Toast.LENGTH_SHORT).show();

                        d_SubAdminProfile.child("contact").setValue(contact2_tx);
                        d_SubAdminProfile.child("name").setValue(name_et.getText().toString());
                        d_SubAdminProfile.child("address").setValue(address_tx);
                        d_SubAdminProfile.child("birth").setValue(birth_tx);

                        d_SubAdminProfile.child("permissions").child("pump_permission").setValue(pump_permission);
                        d_SubAdminProfile.child("permissions").child("truck_permission").setValue(truck_permission);
                        d_SubAdminProfile.child("permissions").child("driver_permission").setValue(driver_permission);

                        Alerter.create(getActivity())
                                .setTitle("Profile Updated")
                                .setContentGravity(1)
                                .setBackgroundColorRes(R.color.black)
                                .setIcon(R.drawable.success_icon)
                                .show();
                        dialog_updating_data.dismiss();

                    }
                },2000);

                break;




        }

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //end
}
