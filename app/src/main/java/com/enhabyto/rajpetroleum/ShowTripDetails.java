package com.enhabyto.rajpetroleum;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.tapadoo.alerter.Alerter;
import dmax.dialog.SpotsDialog;
import mehdi.sakout.fancybuttons.FancyButton;
import util.android.textviews.FontTextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowTripDetails extends Fragment implements View.OnClickListener {

    private View view;


    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();

    private String contactUID_tx, startDate_tx, pumpName_tx, stateName_tx, cityName_tx, truckLocation_tx, moneyTaken_tx, petrolPrice_tx;
    FontTextView contact_tv, name_tv, truckNumber_tv, startDate_tv, pumpName_tv, stateName_tv
            , cityName_tv, truckLocation_tv, moneyTaken_tv, petrolPrice_tv, fuelTaken_tv, sideHeader_tv
            , totalPetrolFilled_tv, petrolLeft_tv, endLocation_tv, endPumpName_tv, endStateName_tv, tripEndDate_tv, fuelLeft_tv, allocatedTo_tv;

    String name_tx, truckNumber_tx, totalPetrolFilled_tx;

    TextView stoppage_tv, petrolFilling_tv, otherFilling_tv, load_tv, breakDown_tv;
    FancyButton stoppage_btn, petrolFilling_btn, otherFilling_btn, load_btn, breakDown_btn;

    TextView stoppageMoney_tv, petrolMoney_tv, otherMoney_tv, breakDownMoney_tv, total_tv;
    String stoppageMoney_tx, petrolMoney_tx, otherMoney_tx, breakDownMoney1_tx, breakDownMoney2_tx
            , breakDownMoney3_tx, breakDownMoney4_tx, breakDownMoney5_tx, breakDownMoney6_tx
            , breakDownMoney7_tx, breakDownMoney8_tx, fuelTaken_tx;
    int i = 0, j=0, k=0, l=0, m=0;

    int total = 0;
    String index_key;


    AlertDialog dialog_loading;



    String petrolkey, month;
    String connected = "no";

    int petrolSize, stoppageSize, loadSize, unLoadSize, otherSize, failureSize;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public ShowTripDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_show_trip_details, container, false);
        dialog_loading = new SpotsDialog(getActivity(), R.style.loadingData);

        SharedPreferences shared = getActivity().getSharedPreferences("driverContact", Context.MODE_PRIVATE);
        try {
            contactUID_tx = (shared.getString("contactUID", ""));
            petrolkey = (shared.getString("TripSuperKey", ""));
            name_tx = (shared.getString("driverName", ""));

        } catch (NullPointerException e) {
            contactUID_tx = "";
        }

       InternetChecker();


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
        fuelTaken_tv = view.findViewById(R.id.detail_fuelTakenTextView);
        sideHeader_tv = view.findViewById(R.id.detail_sideHeader);
        totalPetrolFilled_tv = view.findViewById(R.id.detail_totalPetrolTextView);
        endLocation_tv = view.findViewById(R.id.detail_endLocationTextView);
        endPumpName_tv = view.findViewById(R.id.detail_endPumpNameTextView);
        endStateName_tv = view.findViewById(R.id.detail_endStateNameTextView);
        tripEndDate_tv = view.findViewById(R.id.detail_tripEndTextView);
        fuelLeft_tv = view.findViewById(R.id.detail_petrolLeftTextView);
        allocatedTo_tv = view.findViewById(R.id.detail_allocated_toTextView);

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

        stoppageMoney_tv = view.findViewById(R.id.detail_text11);
        petrolMoney_tv = view.findViewById(R.id.detail_text12);
        otherMoney_tv = view.findViewById(R.id.detail_text13);
        breakDownMoney_tv = view.findViewById(R.id.detail_text15);
        total_tv = view.findViewById(R.id.detail_text16);



       // profileImage = view.findViewById(R.id.detail_profileImage);

        contact_tv.setText(contactUID_tx);

        name_tv.setText("(" + name_tx + ")");

        if (TextUtils.equals(name_tx, "")){
            name_tv.setText("(not known)");
        }
        mSwipeRefreshLayout = view.findViewById(R.id.showDetailSwipe);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                Refresher();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);



            }
        });

        petrolFilling_btn.setOnClickListener(this);
        stoppage_btn.setOnClickListener(this);
        load_btn.setOnClickListener(this);
        otherFilling_btn.setOnClickListener(this);
        breakDown_btn.setOnClickListener(this);


        dialog_loading.show();

        FirebaseDatabase.getInstance().getReference().child("trip_details").child(contactUID_tx).child(petrolkey)
                .child("start_trip").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                truckNumber_tx = dataSnapshot.child("truck_number").getValue(String.class);

                pumpName_tx = dataSnapshot.child("start_location").getValue(String.class);
                stateName_tx = dataSnapshot.child("state_name").getValue(String.class);
                cityName_tx = dataSnapshot.child("city_name").getValue(String.class);
                truckLocation_tx = dataSnapshot.child("truck_location").getValue(String.class);
                moneyTaken_tx = dataSnapshot.child("expenses_taken").getValue(String.class);
                petrolPrice_tx = dataSnapshot.child("fuel_price").getValue(String.class);
                startDate_tx = dataSnapshot.child("start_date").getValue(String.class);
                fuelTaken_tx = dataSnapshot.child("fuel_taken").getValue(String.class);
                String fuel_left_key = dataSnapshot.child("fuel_left_key").getValue(String.class);


                if (!TextUtils.isEmpty(fuel_left_key)){
                    FirebaseDatabase.getInstance().getReference().child("truck_trips")
                            .child(truckNumber_tx).child(fuel_left_key).child("fuel_left")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String fuel_left = dataSnapshot.getValue(String.class);
                                    if (!TextUtils.isEmpty(fuel_left)){
                                        fuelLeft_tv.setText(fuel_left+" Litres");
                                    }
                                    else {
                                        fuelLeft_tv.setText("NA");
                                        fuelLeft_tv.setTextColor(Color.GRAY);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }



//                        setting values

                truckNumber_tv.setText(truckNumber_tx);
                pumpName_tv.setText(pumpName_tx);
                stateName_tv.setText(stateName_tx);
                cityName_tv.setText(cityName_tx);
                truckLocation_tv.setText(truckLocation_tx);
                moneyTaken_tv.setText("Rs " + moneyTaken_tx);
                petrolPrice_tv.setText("Rs " + petrolPrice_tx + "/lit");
                fuelTaken_tv.setText(fuelTaken_tx + " Litres");

                final String date = startDate_tx;

                try {
                    String day = TextUtils.substring(date, 0, 2);
                    month = TextUtils.substring(date, 3, 5);
                    String year = TextUtils.substring(date, 6, 10);
                    String hour = TextUtils.substring(date, 11, 13);
                    String minute = TextUtils.substring(date, 14, 16);
                    conversion();

                    startDate_tv.setText(day+"-"+month+"-"+year+", "+hour+":"+minute);



                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }

                try {
                    if (stateName_tx.equals("")){
                        stateName_tv.setText("NA");
                        stateName_tv.setTextColor(Color.GRAY);
                    }

                    if (cityName_tx.equals("")){
                        cityName_tv.setText("NA");
                        cityName_tv.setTextColor(Color.GRAY);
                    }
                    if (TextUtils.equals(fuelTaken_tx,null) || fuelTaken_tx.equals("") || fuelTaken_tx.isEmpty()){
                        fuelTaken_tv.setText("NA");
                        fuelTaken_tv.setTextColor(Color.GRAY);
                    }
                    if (moneyTaken_tx.equals("")){
                        moneyTaken_tv.setText("NA");
                        moneyTaken_tv.setTextColor(Color.GRAY);
                    }
                    if (pumpName_tx.equals("")){
                        pumpName_tv.setText("NA");
                        pumpName_tv.setTextColor(Color.GRAY);
                    }


                    Query query = d_root.child("truck_trips").child(truckNumber_tx);
                    query.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {



                           for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                 index_key = snapshot.child("index_key").getValue(String.class);
                           }


                                if (!TextUtils.isEmpty(index_key)){
                                    d_root.child("truck_trips").child(truckNumber_tx).child(index_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String driverName = dataSnapshot.child("driver_name").getValue(String.class);



                                            if (!TextUtils.isEmpty(driverName)){
                                                allocatedTo_tv.setText("Truck Number "+truckNumber_tx +" was allocated to "+driverName+
                                                        " in last Trip");
                                            }





                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Refresher();



//        profile Image

     /*   try{

            storageRef.child("driver_profiles").child(contactUID_tx)
                    .child("/profile_image/image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'

                    if (uri!=null  && getActivity().getIntent() != null){
                        Glide.with(getActivity())
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

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Glide.with(getActivity())
                            .load(R.drawable.driver_default_image_icon)
                            .fitCenter()
                            .centerCrop()
                            .crossFade(1200)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(profileImage);
                }
            });

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }*/



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
                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setTitle("Device is not connected to internet.")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                }

                if (petrolSize == 0){
                    Alerter.create(getActivity())
                            .setTitle("There is no Petrol filling!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    return;
                }



                else
                    try {
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new PetrolFillingDetail()).addToBackStack("petrol").commit();

                    }
                    catch (IllegalStateException e){
                        e.printStackTrace();
                    }

                break;


            case R.id.detail_stoppageButton:

                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setTitle("Device is not connected to internet.")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                }
                if (stoppageSize == 0){
                    Alerter.create(getActivity())
                            .setTitle("There is no stoppage!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    return;
                }


                else
                    try {
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new StoppageDetail()).addToBackStack("stoppage").commit();

                    }
                    catch (IllegalStateException | NullPointerException e){
                        e.printStackTrace();
                    }

                break;

            case R.id.detail_loadButton:

                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setTitle("Device is not connected to internet.")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                }
                if (loadSize+unLoadSize == 0){
                    Alerter.create(getActivity())
                            .setTitle("There is no Load/UnLoad!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    return;
                }


                else

                    try {
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new LoadDetail()).addToBackStack("load").commit();

                    }
                    catch (IllegalStateException | NullPointerException e){
                        e.printStackTrace();
                    }
                break;


            case R.id.detail_otherFillingButton:

                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setTitle("Device is not connected to internet.")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                }
                if (otherSize == 0){
                    Alerter.create(getActivity())
                            .setTitle("There is no other filling!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    return;
                }

                else
                    try {
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new OtherFillingDetail()).addToBackStack("load").commit();

                    }
                    catch (IllegalStateException | NullPointerException e){
                        e.printStackTrace();
                    }


                break;


            case R.id.detail_breakDownButton:
                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setTitle("Device is not connected to internet.")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                }

                if (failureSize == 0){
                    Alerter.create(getActivity())
                            .setTitle("There is no breakage!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    return;

                }


                else
                    try {
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new BreakDownDetail()).addToBackStack("load").commit();

                    }
                    catch (IllegalStateException | NullPointerException e){
                        e.printStackTrace();
                    }

                break;


        }
    }



    public void Refresher(){

        //        petrol filling module


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


                                try {
                                    dialog_loading.dismiss();
                                }
                                catch (IllegalStateException | NullPointerException e){
                                    e.printStackTrace();
                                }

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




                // Money Calculations

        total = 0;
        FirebaseDatabase.getInstance().getReference().child("trip_details").child(contactUID_tx).child(petrolkey).child("stoppage")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            stoppageMoney_tx = snapshot.child("money_paid").getValue(String.class);
                            if (TextUtils.equals(stoppageMoney_tx,"")){
                                stoppageMoney_tx = "0";
                            }
                            i = i + Integer.parseInt(stoppageMoney_tx);
                        }
                        stoppageMoney_tv.setText("0");
                        stoppageMoney_tv.setText("Rs "+String.valueOf(i));
                        total = total + i;

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


        d_root.child("trip_details").child(contactUID_tx).child(petrolkey).child("start_trip")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        totalPetrolFilled_tx = dataSnapshot.child("fuel_taken").getValue(String.class);


        FirebaseDatabase.getInstance().getReference().child("trip_details").child(contactUID_tx).child(petrolkey).child("petrol_filled")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        j = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            petrolMoney_tx = snapshot.child("total_money").getValue(String.class);

                            if (TextUtils.equals(petrolMoney_tx,"")){
                                petrolMoney_tx = "0";
                            }
                            j = j + Integer.parseInt(petrolMoney_tx);
                        }
                        petrolMoney_tv.setText("Rs "+String.valueOf(j));
                        total = total + j;


                        m = 0;
                        if (TextUtils.equals(totalPetrolFilled_tx, "") || TextUtils.equals(totalPetrolFilled_tx, null)){
                            totalPetrolFilled_tx = "0";
                        }
                        m = Integer.parseInt(totalPetrolFilled_tx);
                        for (DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                            totalPetrolFilled_tx = snapshot1.child("petrol_filled").getValue(String.class);
                            m = m + Integer.parseInt(totalPetrolFilled_tx);
                        }
                        totalPetrolFilled_tv.setText(String.valueOf(m)+" Litres");

                        FirebaseDatabase.getInstance().getReference().child("trip_details").child(contactUID_tx).child(petrolkey).child("other_filling")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        k = 0;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            otherMoney_tx = snapshot.child("money_paid").getValue(String.class);
                                            if (TextUtils.equals(otherMoney_tx,"")){
                                                otherMoney_tx = "0";
                                            }
                                            k = k + Integer.parseInt(otherMoney_tx);
                                        }
                                        otherMoney_tv.setText("Rs "+String.valueOf(k));
                                        total = total + k;

                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });






                        FirebaseDatabase.getInstance().getReference().child("trip_details").child(contactUID_tx).child(petrolkey).child("failure")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        l = 0;
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            breakDownMoney1_tx = snapshot.child("bill_paid1").getValue(String.class);
                                            breakDownMoney2_tx = snapshot.child("bill_paid2").getValue(String.class);
                                            breakDownMoney3_tx = snapshot.child("bill_paid3").getValue(String.class);
                                            breakDownMoney4_tx = snapshot.child("resource_price1").getValue(String.class);
                                            breakDownMoney5_tx = snapshot.child("resource_price2").getValue(String.class);
                                            breakDownMoney6_tx = snapshot.child("resource_price3").getValue(String.class);
                                            breakDownMoney7_tx = snapshot.child("resource_price4").getValue(String.class);
                                            breakDownMoney8_tx = snapshot.child("resource_price5").getValue(String.class);

                                            if (TextUtils.equals(breakDownMoney1_tx,"")){
                                                breakDownMoney1_tx = "0";
                                            }

                                            if (TextUtils.equals(breakDownMoney2_tx,"")){
                                                breakDownMoney2_tx = "0";
                                            }

                                            if (TextUtils.equals(breakDownMoney3_tx,"")){
                                                breakDownMoney3_tx = "0";
                                            }

                                            if (TextUtils.equals(breakDownMoney4_tx,"")){
                                                breakDownMoney4_tx = "0";
                                            }

                                            if (TextUtils.equals(breakDownMoney5_tx,"")){
                                                breakDownMoney5_tx = "0";
                                            }

                                            if (TextUtils.equals(breakDownMoney6_tx,"")){
                                                breakDownMoney6_tx = "0";
                                            }

                                            if (TextUtils.equals(breakDownMoney7_tx,"")){
                                                breakDownMoney7_tx = "0";
                                            }

                                            if (TextUtils.equals(breakDownMoney8_tx,"")){
                                                breakDownMoney8_tx = "0";
                                            }
                                            try{
                                                l = l + Integer.parseInt(breakDownMoney1_tx) + Integer.parseInt(breakDownMoney2_tx)+  Integer.parseInt(breakDownMoney3_tx)+
                                                        Integer.parseInt(breakDownMoney4_tx)+  Integer.parseInt(breakDownMoney5_tx)+  Integer.parseInt(breakDownMoney6_tx)+
                                                        Integer.parseInt(breakDownMoney7_tx)+  Integer.parseInt(breakDownMoney8_tx);
                                            }
                                            catch (NumberFormatException e){
                                                e.printStackTrace();
                                            }


                                        }

                                        breakDownMoney_tv.setText("Rs "+String.valueOf(l));
                                        total = total + l;
                                        total_tv.setText(String.valueOf("Rs "+total));

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


    }




    //internet checker
    public void InternetChecker(){
        DatabaseReference d_networkStatus = FirebaseDatabase.getInstance().getReference().child("checkNetwork").child("isConnected");

        d_networkStatus.addListenerForSingleValueEvent(new ValueEventListener() {
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
                if (!connected.equals("connected")){


                    Alerter.create(getActivity())
                            .setTitle("Internet Connectivity Problem!")
                            .setText("App needs internet connection. Some values may not be loaded due to no internet connection")
                            .setContentGravity(1)
                            .setDuration(6000)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                    dialog_loading.dismiss();


                }
            }
        },8000);
    }


    private void conversion() {

        switch (month) {

            case "1":
                month = "Jan";
                break;

            case "2":
                month = "Feb";
                break;

            case "3":
                month = "Mar";
                break;

            case "4":
                month = "Apr";
                break;

            case "5":
                month = "May";
                break;

            case "6":
                month = "Jun";
                break;

            case "7":
                month = "Jul";
                break;

            case "8":
                month = "Aug";
                break;

            case "9":
                month = "Sep";
                break;

            case "10":
                month = "Oct";
                break;

            case "11":
                month = "Nov";
                break;

            case "12":
                month = "Dec";
                break;


        }
    }


    public void onStart(){
        super.onStart();

        d_root.child("trip_details").child(contactUID_tx).child(petrolkey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String status = dataSnapshot.child("start_trip").child("status").getValue(String.class);
                        String endLocation = dataSnapshot.child("end_trip").child("address").getValue(String.class);
                        String endPumpName = dataSnapshot.child("end_trip").child("pump_name").getValue(String.class);
                        String endStateName = dataSnapshot.child("end_trip").child("state_name").getValue(String.class);
                        String endDate = dataSnapshot.child("end_trip").child("end_date").getValue(String.class);



                        if (TextUtils.equals(endDate,"") || TextUtils.equals(endDate,null)){
                            tripEndDate_tv.setTextColor(getResources().getColor(R.color.lightGreen));
                            tripEndDate_tv.setText("Trip is Active");
                        }
                        else {
                            try {
                                String day = TextUtils.substring(endDate, 0, 2);
                                month = TextUtils.substring(endDate, 3, 5);
                                String year = TextUtils.substring(endDate, 6, 10);
                                String hour = TextUtils.substring(endDate, 11, 13);
                                String minute = TextUtils.substring(endDate, 14, 16);
                                conversion();

                                tripEndDate_tv.setText(day+"-"+month+"-"+year+", "+hour+":"+minute);



                            }
                            catch (NullPointerException | StringIndexOutOfBoundsException e){
                                e.printStackTrace();
                            }
                        }



                        endLocation_tv.setText(endLocation);
                        endPumpName_tv.setText(endPumpName);
                        endStateName_tv.setText(endStateName);

                        sideHeader_tv.setText(status);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




    }


    public void onDestroy(){
        super.onDestroy();
        if (dialog_loading.isShowing()){
            dialog_loading.dismiss();
        }
    }



    //end
}
