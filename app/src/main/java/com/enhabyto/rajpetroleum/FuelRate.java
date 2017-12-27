package com.enhabyto.rajpetroleum;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tapadoo.alerter.Alerter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dmax.dialog.SpotsDialog;
import mehdi.sakout.fancybuttons.FancyButton;
import util.android.textviews.FontTextView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FuelRate extends Fragment {

    View view;

    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private AlertDialog dialog;

    EditText newPrice_et;
    String newPrice_tx, pumpName_tx, day, month, minute, year, hour, user_designation, sub_admin_contact, admin_name;

    FancyButton select_btn, submit_btn;

    AutoCompleteTextView pumpName_et;
    Spinner spinner;

    FontTextView previousRate_tv, updatedOn_tv, header_tv, setBy_tv;

    DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dataRef_spinner = d_root.child("pump_details");

    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    List<ImageUploadInfo> list = new ArrayList<>();



    public FuelRate() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fuel_rate, container, false);

        newPrice_et = view.findViewById(R.id.fr_NewRateEditText);
        pumpName_et = view.findViewById(R.id.fr_pumpNameEditText);
        spinner = view.findViewById(R.id.fr_spinner);
        previousRate_tv = view.findViewById(R.id.fr_previousRateTextView);
        updatedOn_tv = view.findViewById(R.id.fr_updatedOnTextView);
        select_btn = view.findViewById(R.id.fr_openCreatePumpButton);
        submit_btn = view.findViewById(R.id.fr_submitButton);
        header_tv = view.findViewById(R.id.fr_headerText);
        setBy_tv = view.findViewById(R.id.fr_updatedByTextView);

        recyclerView = view.findViewById(R.id.fr_recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);

        dialog = new SpotsDialog(getActivity(), R.style.dialog_updating);



//     spinner
        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {

                            String selected_val = spinner.getSelectedItem().toString();
                            if (!TextUtils.equals(selected_val,"Select Pump")){
                                pumpName_et.setText(selected_val);

                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                            // TODO Auto-generated method stub

                        }
                    });

                }
                return false;
            }
        });



        select_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
                pumpName_tx = pumpName_et.getText().toString().trim();

                if (pumpName_tx.isEmpty()){
                    Alerter.create(getActivity())
                            .setTitle("Enter Pump Name!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog.dismiss();
                    return;
                }

                root.child("pump_details")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(pumpName_tx)){

                                    root.child("fuel_rate").child(pumpName_tx)
                                            .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            view.findViewById(R.id.fr_relative).setVisibility(View.VISIBLE);

                                            header_tv.setText(pumpName_tx);

                                            String previousRate_tx = dataSnapshot.child("current_rate").getValue(String.class);
                                            String updatedOn_tx = dataSnapshot.child("updated_on").getValue(String.class);
                                            String name = dataSnapshot.child("set_by").getValue(String.class);


                                            setBy_tv.setText(name);
                                            if (TextUtils.isEmpty(name)){
                                                setBy_tv.setText("unknown");
                                                setBy_tv.setTextColor(getResources().getColor(R.color.lightRed));
                                            }

                                            if (TextUtils.isEmpty(previousRate_tx)){
                                                previousRate_tx = "unknown";
                                                previousRate_tv.setTextColor(getResources().getColor(R.color.lightRed));
                                                previousRate_tv.setText(previousRate_tx);
                                            }
                                            else previousRate_tv.setText("Rs "+previousRate_tx+" per litre");

                                            if (TextUtils.isEmpty(updatedOn_tx)){
                                                updatedOn_tx = "unknown";
                                                updatedOn_tv.setTextColor(getResources().getColor(R.color.lightRed));
                                                updatedOn_tv.setText(updatedOn_tx);
                                            }

                                            else {

                                                try {
                                                    day = TextUtils.substring(updatedOn_tx, 0, 2);
                                                    month = TextUtils.substring(updatedOn_tx, 3, 5);
                                                    year = TextUtils.substring(updatedOn_tx, 6, 10);
                                                    hour = TextUtils.substring(updatedOn_tx, 11, 13);
                                                    minute = TextUtils.substring(updatedOn_tx, 14, 16);
                                                    conversion();


                                                    updatedOn_tv.setText(day+"-"+month+"-"+year+", "+hour+":"+minute);
                                                }
                                                catch (NullPointerException e){
                                                    e.printStackTrace();
                                                }

                                            }

                                            newPrice_et.setText("");




                                            // setting adapter


                                            d_root.child("fuel_rate_history").child(pumpName_tx)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    if(list!=null) {
                                                        list.clear();  // v v v v important (eliminate duplication of data)
                                                    }


                                                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                                                        ImageUploadInfo otherFillingRecyclerInfo = postSnapshot.getValue(ImageUploadInfo.class);

                                                        list.add(otherFillingRecyclerInfo);

                                                    }
                                                    view.findViewById(R.id.fr_recyclerView).setVisibility(View.VISIBLE);

                                                    adapter = new FuelHistoryRecyclerViewAdapter(getActivity(), list);


                                                    //   Collections.reverse(list);
                                                    adapter.notifyDataSetChanged();
                                                    recyclerView.setAdapter(adapter);
                                                    if (adapter.getItemCount() == 0){
                                                        view.findViewById(R.id.fr_recyclerView).setVisibility(View.GONE);
                                                    }

                                                    dialog.dismiss();
                                                    // Hiding the progress dialog.

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                    // Hiding the progress dialog.
                                                    dialog.dismiss();

                                                }
                                            });











                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }


                                else {
                                    Alerter.create(getActivity())
                                            .setTitle("There is no such Pump!")
                                            .setContentGravity(1)
                                            .setBackgroundColorRes(R.color.black)
                                            .setIcon(R.drawable.error)
                                            .show();
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        });




        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newPrice_tx = newPrice_et.getText().toString().trim();

                connectedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        boolean connected = snapshot.getValue(Boolean.class);
                        if (connected) {

                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
                            SimpleDateFormat sdf1 = new SimpleDateFormat("HH_mm");
                            String actualDate = sdf.format(c.getTime());
                            String actualTime = sdf1.format(c.getTime());

                            d_root.child("fuel_rate").child(pumpName_tx).child("updated_on").setValue(actualDate+"_"+actualTime);
                            d_root.child("fuel_rate").child(pumpName_tx).child("current_rate").setValue(newPrice_tx);
                            d_root.child("fuel_rate").child(pumpName_tx).child("set_by").setValue(admin_name);

                            String key = d_root.push().getKey();

                            d_root.child("fuel_rate_history").child(pumpName_tx).child(key).child("current_rate").setValue(newPrice_tx);
                            d_root.child("fuel_rate_history").child(pumpName_tx).child(key).child("updated_on").setValue(actualDate+"_"+actualTime);
                            d_root.child("fuel_rate_history").child(pumpName_tx).child(key).child("set_by").setValue(admin_name);

                        } else {
                            Alerter.create(getActivity())
                                    .setTitle("Unable to update!")
                                    .setText("No internet Connection, try again")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.error)
                                    .show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });






            }
        });




        return view;
    }

    public void onStart(){
        super.onStart();

        try{
            SharedPreferences shared = getContext().getSharedPreferences("firstLog", MODE_PRIVATE);
            user_designation = (shared.getString("user_designation", ""));
            sub_admin_contact = (shared.getString("subAdmin_contact", ""));
        }
        catch (NullPointerException e){

            e.printStackTrace();
        }


        if (user_designation.equals("admin")){

            d_root.child("admin").child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    admin_name = dataSnapshot.child("name").getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        else if (user_designation.equals("subAdmin")){
            d_root.child("sub_admin_profiles").child(sub_admin_contact).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    admin_name = dataSnapshot.child("name").getValue(String.class);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }








        dataRef_spinner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    List<String> areas = new ArrayList<>();
                    areas.add("Select Pump");
                    for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                        String areaName = areaSnapshot.child("pump_name").getValue(String.class);
                        areas.add(areaName);


                    }


                    ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areas);
                    areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(areasAdapter);
                    pumpName_et.setAdapter(areasAdapter);
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });





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





}
