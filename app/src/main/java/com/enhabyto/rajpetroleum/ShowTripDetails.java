package com.enhabyto.rajpetroleum;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tapadoo.alerter.Alerter;

import dmax.dialog.SpotsDialog;
import mehdi.sakout.fancybuttons.FancyButton;
import util.android.textviews.FontTextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowTripDetails extends Fragment implements View.OnClickListener {

    private View view;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://rajpetroleum-4d3fa.appspot.com/");

    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference databaseReference;

    private String contactUID_tx, startDate_tx, pumpName_tx, stateName_tx, cityName_tx, truckLocation_tx, moneyTaken_tx, petrolPrice_tx;
    FontTextView contact_tv, name_tv, truckNumber_tv, startDate_tv, pumpName_tv, stateName_tv, cityName_tv, truckLocation_tv, moneyTaken_tv, petrolPrice_tv;
    String contact_tx, name_tx, truckNumber_tx;

    TextView stoppage_tv, petrolFilling_tv, otherFilling_tv, load_tv, breakDown_tv;
    FancyButton stoppage_btn, petrolFilling_btn, otherFilling_btn, load_btn, breakDown_btn;

    ImageView profileImage;
    AlertDialog dialog_loading;

    String petrolkey;

    int petrolSize, stoppageSize, loadSize, unLoadSize, otherSize, failureSize;


    public ShowTripDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_show_trip_details, container, false);
        dialog_loading = new SpotsDialog(getActivity(), R.style.loadingData);
        dialog_loading.show();

        SharedPreferences shared = getActivity().getSharedPreferences("driverContact", Context.MODE_PRIVATE);
        try {
            contactUID_tx = (shared.getString("contactUID", ""));
            startDate_tx = (shared.getString("startDate", ""));
        } catch (NullPointerException e) {
            contactUID_tx = "";
        }

        contact_tv = view.findViewById(R.id.detail_contactTextView);
        name_tv = view.findViewById(R.id.detail_nameTextView);
        truckNumber_tv = view.findViewById(R.id.detail_truckNumberTextView);
        startDate_tv = view.findViewById(R.id.detail_tripStartTextView);
        pumpName_tv = view.findViewById(R.id.detail_pumpNameTextView);
        stateName_tv = view.findViewById(R.id.detail_stateNameTextView);
        cityName_tv = view.findViewById(R.id.detail_cityNameTextView);
        truckLocation_tv = view.findViewById(R.id.detail_pickedFromTextView);
        moneyTaken_tv = view.findViewById(R.id.detail_moneyTakenTextView);
        petrolPrice_tv = view.findViewById(R.id.detail_petrolPriceTextView);

        stoppage_tv = view.findViewById(R.id.detail_text1);
        petrolFilling_tv = view.findViewById(R.id.detail_text2);
        otherFilling_tv = view.findViewById(R.id.detail_text3);
        load_tv = view.findViewById(R.id.detail_text4);
        breakDown_tv = view.findViewById(R.id.detail_text5);

        stoppage_btn = view.findViewById(R.id.detail_stoppageButton);
        petrolFilling_btn = view.findViewById(R.id.detail_petrolFillingButton);
        otherFilling_btn = view.findViewById(R.id.detail_otherFillingButton);
        load_btn = view.findViewById(R.id.detail_loadButton);
        breakDown_btn = view.findViewById(R.id.detail_breakDownButton);


        profileImage = view.findViewById(R.id.detail_profileImage);

        contact_tv.setText(contactUID_tx);
        startDate_tv.setText(startDate_tx);

        databaseReference = FirebaseDatabase.getInstance().getReference("driver_profiles").child(contactUID_tx);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name_tv.setText("(" + dataSnapshot.child("name").getValue(String.class) + ")");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final Query query = d_root.child("trip_details").child(contactUID_tx).orderByKey().limitToLast(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!isNetworkAvailable()) {
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setText("Need a decent internet connection, please try again")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();

                }

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            truckNumber_tx = child.child("start_trip").child("truck_number").getValue(String.class);

                            pumpName_tx = child.child("start_trip").child("start_location").getValue(String.class);
                            stateName_tx = child.child("start_trip").child("state_name").getValue(String.class);
                            cityName_tx = child.child("start_trip").child("city_name").getValue(String.class);
                            truckLocation_tx = child.child("start_trip").child("truck_location").getValue(String.class);
                            moneyTaken_tx = child.child("start_trip").child("expenses_taken").getValue(String.class);
                            petrolPrice_tx = child.child("start_trip").child("fuel_price").getValue(String.class);

                        }


//                        setting values

                        truckNumber_tv.setText(truckNumber_tx);
                        pumpName_tv.setText(pumpName_tx);
                        stateName_tv.setText(stateName_tx);
                        cityName_tv.setText(cityName_tx);
                        truckLocation_tv.setText(truckLocation_tx);
                        moneyTaken_tv.setText("Rs " + moneyTaken_tx);
                        petrolPrice_tv.setText("Rs " + petrolPrice_tx + "/Litres");


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        petrol filling module

        Query queryPetrolNumber = d_root.child("trip_details").child(contactUID_tx).orderByKey().limitToLast(1);

        queryPetrolNumber.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    petrolkey = child.getKey();

                }

                DatabaseReference d_child = d_root.child("trip_details").child(contactUID_tx)
                        .child(petrolkey).child("petrol_filled");

                d_child.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        petrolSize = (int) (dataSnapshot.getChildrenCount());
                        petrolFilling_tv.setText(String.valueOf(petrolSize));

                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                d_child = d_root.child("trip_details").child(contactUID_tx)
                        .child(petrolkey).child("stoppage");

                d_child.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        stoppageSize = (int) (dataSnapshot.getChildrenCount());
                        stoppage_tv.setText(String.valueOf(stoppageSize));

                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                d_child = d_root.child("trip_details").child(contactUID_tx)
                        .child(petrolkey).child("load");

                d_child.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        loadSize = (int) (dataSnapshot.getChildrenCount());

                        DatabaseReference d_child = d_root.child("trip_details").child(contactUID_tx)
                                .child(petrolkey).child("unload");

                        d_child.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                unLoadSize = (int) (dataSnapshot.getChildrenCount());

                                load_tv.setText(String.valueOf(loadSize+unLoadSize));

                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        d_child = d_root.child("trip_details").child(contactUID_tx)
                                .child(petrolkey).child("other_filling");

                        d_child.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                otherSize = (int) (dataSnapshot.getChildrenCount());
                                otherFilling_tv.setText(String.valueOf(otherSize));
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        d_child = d_root.child("trip_details").child(contactUID_tx)
                                .child(petrolkey).child("failure");

                        d_child.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                failureSize = (int) (dataSnapshot.getChildrenCount());
                                breakDown_tv.setText(String.valueOf(failureSize));
                                dialog_loading.dismiss();
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });








//        profile Image

        storageRef.child("driver_profiles").child(contactUID_tx)
                .child("/profile_image/image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

                Glide.with(getContext())
                        .load(uri)
                        .asBitmap()
                        .fitCenter()
                        .centerCrop()
                        .error(R.drawable.error)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new BitmapImageViewTarget(profileImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                profileImage.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


        petrolFilling_btn.setOnClickListener(this);
        stoppage_btn.setOnClickListener(this);
        load_btn.setOnClickListener(this);
        otherFilling_btn.setOnClickListener(this);
        breakDown_btn.setOnClickListener(this);



        return view;
    }

    //    internet check
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id){

            case R.id.detail_petrolFillingButton:
                if (petrolSize == 0){
                    Alerter.create(getActivity())
                            .setTitle("there is no Petrol filling yet!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();

                }
                else
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard, new PetrolFillingDetail()).addToBackStack("petrol").commit();
                break;


            case R.id.detail_stoppageButton:
                if (stoppageSize == 0){
                    Alerter.create(getActivity())
                            .setTitle("there is no stoppage yet!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();

                }
                else
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard, new StoppageDetail()).addToBackStack("stoppage").commit();
                break;

            case R.id.detail_loadButton:
                if (loadSize+unLoadSize == 0){
                    Alerter.create(getActivity())
                            .setTitle("there is no Load/UnLoad yet!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();

                }
                else
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard, new LoadDetail()).addToBackStack("load").commit();
                break;


            case R.id.detail_otherFillingButton:
                if (otherSize == 0){
                    Alerter.create(getActivity())
                            .setTitle("there is no other filling yet!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();

                }
                else
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard, new OtherFillingDetail()).addToBackStack("load").commit();
                break;


            case R.id.detail_breakDownButton:
                if (failureSize == 0){
                    Alerter.create(getActivity())
                            .setTitle("there is no other filling yet!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();

                }
                else
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard, new BreakDownDetail()).addToBackStack("load").commit();
                break;


        }
    }


    //end
}
