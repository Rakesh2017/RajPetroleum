package com.enhabyto.rajpetroleum;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tapadoo.alerter.Alerter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateTruck extends Fragment implements View.OnClickListener {

    private View view;
    EditText truckNumber_et1, truckNumber_et2, chsssis_et, engine_et, ownerName_et, attached_et
            , chamber_et, capacity_et, rc_et, rcValid_et, rcRenew_et
            , fit_et, fitValid_et, fitrenew_et, pollution_et, pollutionValid_et
            , pollutionRenew_et, insuranceName_et, insuranceAmount_et, insuranceValid_et
            , insuranceRenew_et, hydro_et, hydroValid_et, hydroRenew_et, road_et
            ,roadValid_et, roadRenew_et, explosive_et, explosiveValid_et, explosiveRenew_et
            , nationalPermit_et, statePermit_et, labourPermit_et, calibration_et, calibrationValid_et, calibrationRenew_et;

    String truckNumber_tx1, truckNumber_tx2,  chsssis_tx, engine_tx, ownerName_tx, attached_tx
            , chamber_tx, capacity_tx, rc_tx, rcValid_tx, rcRenew_tx
            , fit_tx, fitValid_tx, fitrenew_tx, pollution_tx, pollutionValid_tx
            , pollutionRenew_tx, insuranceName_tx, insuranceAmount_tx, insuranceValid_tx
            , insuranceRenew_tx, hydro_tx, hydroValid_tx, hydroRenew_tx, road_tx
            ,roadValid_tx, roadRenew_tx, explosive_tx, explosiveValid_tx, explosiveRenew_tx
            , nationalPermit_tx, statePermit_tx, labourPermit_tx, connected, decider
            , calibration_tx, calibrationValid_tx, calibrationRenew_tx;

    RelativeLayout relativeLayout;

    DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dataRef_spinner = d_root.child("truck_details");

    DatabaseReference d_parent = FirebaseDatabase.getInstance().getReference().child("checkNetwork").child("isConnected");

    Calendar myCalendar = Calendar.getInstance();
    String myFormat = "dd/MM/yyyy"; // your format
    DatePickerDialog.OnDateSetListener date_birth;
    Spinner spinner;
    String selected_val;


    FancyButton submit_btn, loadData_btn;

    AlertDialog dialog_updateTruck, dialog_loading;


    public CreateTruck() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_truck, container, false);

        truckNumber_et1 = view.findViewById(R.id.ct_truckNumberEditText1);
        truckNumber_et2 = view.findViewById(R.id.ct_tankerNumberEditText2);
        chamber_et = view.findViewById(R.id.ct_chambersEditText);
        engine_et = view.findViewById(R.id.ct_engineNumberEditText);
        ownerName_et = view.findViewById(R.id.ct_ownerNameEditText);
        attached_et = view.findViewById(R.id.ct_attachedWithEditText);
        capacity_et = view.findViewById(R.id.ct_capacityEditText);
        rc_et = view.findViewById(R.id.ct_rcEditText);
        rcValid_et = view.findViewById(R.id.ct_rcValidEditText);
        rcRenew_et = view.findViewById(R.id.ct_rcRenewEditText);
        fit_et = view.findViewById(R.id.ct_fitEditText);
        fitValid_et = view.findViewById(R.id.ct_fitValidEditText);
        fitrenew_et = view.findViewById(R.id.ct_fitRenewEditText);
        chsssis_et = view.findViewById(R.id.ct_chassisEditText);
        pollution_et = view.findViewById(R.id.ct_pollutionEditText);
        pollutionRenew_et = view.findViewById(R.id.ct_pollutionRenewEditText);
        pollutionValid_et = view.findViewById(R.id.ct_pollutionValidEditText);
        insuranceAmount_et = view.findViewById(R.id.ct_insuranceAmountEditText);
        insuranceName_et = view.findViewById(R.id.ct_insuranceNameEditText);
        insuranceRenew_et = view.findViewById(R.id.ct_insuranceRenewEditText);
        insuranceValid_et = view.findViewById(R.id.ct_insuranceValidEditText);
        labourPermit_et = view.findViewById(R.id.ct_labEditText);
        statePermit_et = view.findViewById(R.id.ct_spEditText);
        nationalPermit_et = view.findViewById(R.id.ct_npEditText);
        hydro_et = view.findViewById(R.id.ct_hydroEditText);
        hydroRenew_et = view.findViewById(R.id.ct_hydroRenewEditText);
        hydroValid_et = view.findViewById(R.id.ct_hydroValidEditText);
        road_et = view.findViewById(R.id.ct_roadEditText);
        roadRenew_et = view.findViewById(R.id.ct_roadRenewEditText);
        roadValid_et = view.findViewById(R.id.ct_roadValidEditText);
        rcRenew_et = view.findViewById(R.id.ct_rcRenewEditText);
        explosive_et = view.findViewById(R.id.ct_explosiveEditText);
        explosiveRenew_et = view.findViewById(R.id.ct_explosiveRenewEditText);
        explosiveValid_et = view.findViewById(R.id.ct_explosiveValidEditText);
        calibration_et = view.findViewById(R.id.ct_caliEditText);
        calibrationValid_et = view.findViewById(R.id.ct_caliValidEditText);
        calibrationRenew_et = view.findViewById(R.id.ct_caliRenewEditText);
        relativeLayout = view.findViewById(R.id.ct_RelativeLayout2);
        spinner = view.findViewById(R.id.ct_spinner);

        submit_btn = view.findViewById(R.id.ct_submitTruckButton);
        loadData_btn = view.findViewById(R.id.ct_openCreateTruckButton);

        rcValid_et.setKeyListener(null);
        rcRenew_et.setKeyListener(null);
        calibration_et.setKeyListener(null);
        calibrationRenew_et.setKeyListener(null);
        calibrationValid_et.setKeyListener(null);
        fitrenew_et.setKeyListener(null);
        fitValid_et.setKeyListener(null);
        fit_et.setKeyListener(null);
        hydroRenew_et.setKeyListener(null);
        hydroValid_et.setKeyListener(null);
        hydro_et.setKeyListener(null);
        explosiveRenew_et.setKeyListener(null);
        explosiveValid_et.setKeyListener(null);
        explosive_et.setOnClickListener(this);
        pollutionRenew_et.setKeyListener(null);
        pollutionValid_et.setKeyListener(null);
        pollution_et.setKeyListener(null);
        rcRenew_et.setKeyListener(null);
        roadValid_et.setKeyListener(null);
        road_et.setKeyListener(null);
        roadRenew_et.setKeyListener(null);
        insuranceValid_et.setKeyListener(null);
        insuranceRenew_et.setKeyListener(null);



        submit_btn.setOnClickListener(this);
        loadData_btn.setOnClickListener(this);
        rcValid_et.setOnClickListener(this);
        rcRenew_et.setOnClickListener(this);
        calibration_et.setOnClickListener(this);
        calibrationRenew_et.setOnClickListener(this);
        calibrationValid_et.setOnClickListener(this);
        fitrenew_et.setOnClickListener(this);
        fitValid_et.setOnClickListener(this);
        fit_et.setOnClickListener(this);
        hydroRenew_et.setOnClickListener(this);
        hydroValid_et.setOnClickListener(this);
        hydro_et.setOnClickListener(this);
        explosiveRenew_et.setOnClickListener(this);
        explosiveValid_et.setOnClickListener(this);
        explosive_et.setOnClickListener(this);
        pollutionRenew_et.setOnClickListener(this);
        pollutionValid_et.setOnClickListener(this);
        pollution_et.setOnClickListener(this);
        rcRenew_et.setOnClickListener(this);
        roadValid_et.setOnClickListener(this);
        road_et.setOnClickListener(this);
        roadRenew_et.setOnClickListener(this);
        insuranceValid_et.setOnClickListener(this);
        insuranceRenew_et.setOnClickListener(this);

        dialog_updateTruck = new SpotsDialog(getActivity(),R.style.dialog_updatingTruck);
        dialog_loading = new SpotsDialog(getActivity(),R.style.loadingData);

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {

                            selected_val = spinner.getSelectedItem().toString();
                            if (!TextUtils.equals(selected_val,"Select Truck")){
                                truckNumber_et1.setText(selected_val);

                            }



                            // Toast.makeText(getApplicationContext(), selected_val ,
                            //      Toast.LENGTH_SHORT).show();
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







        return view;
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id){

            case R.id.ct_openCreateTruckButton:


                truckNumber_tx1 = truckNumber_et1.getText().toString().trim();
                if (truckNumber_tx1.length()<13){
                    Alerter.create(getActivity())
                            .setTitle("Invalid truck Number!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    return;
                }

                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();

                    return;
                }
                dialog_loading.show();
                d_parent.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        connected = dataSnapshot.getValue(String.class);

                        if (!TextUtils.equals(connected, "connected")){
                            Alerter.create(getActivity())
                                    .setTitle("Unable to Connect to Server!")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.no_internet)
                                    .show();
                            //    Log.w("123", connected);
                            dialog_loading.dismiss();
                            return;
                        }


                        DatabaseReference dataRef_truckDetails = d_root.child("truck_details").child(truckNumber_tx1);
                        dataRef_truckDetails.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                relativeLayout.setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.FadeIn)
                                        .duration(700)
                                        .repeat(0)
                                        .playOn(relativeLayout);


                                chsssis_tx = dataSnapshot.child("chassis_number").getValue(String.class);
                                engine_tx = dataSnapshot.child("engine_number").getValue(String.class);
                                ownerName_tx = dataSnapshot.child("owner_name").getValue(String.class);
                                attached_tx = dataSnapshot.child("attached_with").getValue(String.class);
                                chamber_tx = dataSnapshot.child("chambers").getValue(String.class);
                                capacity_tx = dataSnapshot.child("capacity").getValue(String.class);
                                rc_tx = dataSnapshot.child("rc").getValue(String.class);
                                rcValid_tx = dataSnapshot.child("rc_valid").getValue(String.class);
                                rcRenew_tx = dataSnapshot.child("rc_renew").getValue(String.class);
                                calibration_tx = dataSnapshot.child("calibration_created").getValue(String.class);
                                calibrationValid_tx = dataSnapshot.child("calibration_valid").getValue(String.class);
                                calibrationRenew_tx = dataSnapshot.child("calibration_renew").getValue(String.class);
                                fit_tx = dataSnapshot.child("fit").getValue(String.class);
                                fitValid_tx = dataSnapshot.child("fit_valid").getValue(String.class);
                                fitrenew_tx = dataSnapshot.child("fit_renew").getValue(String.class);
                                pollution_tx = dataSnapshot.child("pollution_created").getValue(String.class);
                                pollutionValid_tx = dataSnapshot.child("pollution_valid").getValue(String.class);
                                pollutionRenew_tx = dataSnapshot.child("pollution_renew").getValue(String.class);
                                insuranceName_tx = dataSnapshot.child("insurance_name").getValue(String.class);
                                insuranceAmount_tx = dataSnapshot.child("insurance_amount").getValue(String.class);
                                insuranceRenew_tx = dataSnapshot.child("insurance_renew").getValue(String.class);
                                insuranceValid_tx = dataSnapshot.child("insurance_valid").getValue(String.class);
                                hydro_tx = dataSnapshot.child("hydro_created").getValue(String.class);
                                hydroValid_tx = dataSnapshot.child("hydro_valid").getValue(String.class);
                                hydroRenew_tx = dataSnapshot.child("hydro_renew").getValue(String.class);
                                road_tx = dataSnapshot.child("road_created").getValue(String.class);
                                roadValid_tx = dataSnapshot.child("road_valid").getValue(String.class);
                                roadRenew_tx = dataSnapshot.child("road_renew").getValue(String.class);
                                explosive_tx = dataSnapshot.child("explosive_created").getValue(String.class);
                                explosiveValid_tx = dataSnapshot.child("explosive_valid").getValue(String.class);
                                explosiveRenew_tx = dataSnapshot.child("explosive_renew").getValue(String.class);
                                nationalPermit_tx = dataSnapshot.child("national_permit").getValue(String.class);
                                statePermit_tx = dataSnapshot.child("state_permit").getValue(String.class);
                                labourPermit_tx = dataSnapshot.child("labour_permit").getValue(String.class);


                                truckNumber_et2.setText(truckNumber_tx1);
                                chsssis_et.setText(chsssis_tx);
                                engine_et.setText(engine_tx);
                                ownerName_et.setText(ownerName_tx);
                                attached_et.setText(attached_tx);
                                chamber_et.setText(chamber_tx);
                                capacity_et.setText(capacity_tx);
                                rc_et.setText(rc_tx);
                                rcValid_et.setText(rcValid_tx);
                                rcRenew_et.setText(rcRenew_tx);
                                calibration_et.setText(calibration_tx);
                                calibrationValid_et.setText(calibrationValid_tx);
                                calibrationRenew_et.setText(calibrationRenew_tx);
                                fit_et.setText(fit_tx);
                                fitValid_et.setText(fitValid_tx);
                                fitrenew_et.setText(fitrenew_tx);
                                pollution_et.setText(pollution_tx);
                                pollutionValid_et.setText(pollutionValid_tx);
                                pollutionRenew_et.setText(pollutionRenew_tx);
                                insuranceName_et.setText(insuranceName_tx);
                                insuranceAmount_et.setText(insuranceAmount_tx);
                                insuranceValid_et.setText(insuranceValid_tx);
                                insuranceRenew_et.setText(insuranceRenew_tx);
                                hydro_et.setText(hydro_tx);
                                hydroValid_et.setText(hydroValid_tx);
                                hydroRenew_et.setText(hydroRenew_tx);
                                road_et.setText(road_tx);
                                roadRenew_et.setText(roadRenew_tx);
                                roadValid_et.setText(roadValid_tx);
                                explosive_et.setText(explosive_tx);
                                explosiveValid_et.setText(explosiveValid_tx);
                                explosiveRenew_et.setText(explosiveRenew_tx);
                                nationalPermit_et.setText(nationalPermit_tx);
                                statePermit_et.setText(statePermit_tx);
                                labourPermit_et.setText(labourPermit_tx);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        dialog_loading.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




            case R.id.ct_submitTruckButton:
                dialog_updateTruck.show();
                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                    dialog_updateTruck.dismiss();
                    return;
                }


                d_parent.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        connected = dataSnapshot.getValue(String.class);

                        if (!TextUtils.equals(connected, "connected")){
                            Alerter.create(getActivity())
                                    .setTitle("Unable to Connect to Server!")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.no_internet)
                                    .show();
                            dialog_updateTruck.dismiss();
                            return;
                        }

                        truckNumber_tx2 = truckNumber_et2.getText().toString().trim();

                        DatabaseReference dataRef_truckDetails = d_root.child("truck_details").child(truckNumber_tx2);

                        chsssis_tx = chsssis_et.getText().toString().trim();
                        engine_tx = engine_et.getText().toString().trim();
                        ownerName_tx = ownerName_et.getText().toString().trim();
                        attached_tx = attached_et.getText().toString().trim();
                        chamber_tx = chamber_et.getText().toString().trim();
                        capacity_tx = capacity_et.getText().toString().trim();
                        rc_tx = rc_et.getText().toString().trim();
                        rcValid_tx = rcValid_et.getText().toString().trim();
                        rcRenew_tx = rcRenew_et.getText().toString().trim();
                        calibration_tx = calibration_et.getText().toString().trim();
                        calibrationValid_tx = calibrationValid_et.getText().toString().trim();
                        calibrationRenew_tx = calibrationRenew_et.getText().toString().trim();
                        fit_tx = fit_et.getText().toString().trim();
                        fitValid_tx = fitValid_et.getText().toString().trim();
                        fitrenew_tx = fitrenew_et.getText().toString().trim();
                        pollution_tx = pollution_et.getText().toString().trim();
                        pollutionValid_tx = pollutionValid_et.getText().toString().trim();
                        pollutionRenew_tx = pollutionRenew_et.getText().toString().trim();
                        insuranceName_tx  = insuranceName_et.getText().toString().trim();
                        insuranceAmount_tx = insuranceAmount_et.getText().toString().trim();
                        insuranceValid_tx = insuranceValid_et.getText().toString().trim();
                        insuranceRenew_tx = insuranceRenew_et.getText().toString().trim();
                        hydro_tx = hydro_et.getText().toString().trim();
                        hydroValid_tx = hydroValid_et.getText().toString().trim();
                        hydroRenew_tx = hydroRenew_et.getText().toString().trim();
                        road_tx = road_et.getText().toString().trim();
                        roadRenew_tx = roadRenew_et.getText().toString().trim();
                        roadValid_tx = roadValid_et.getText().toString().trim();
                        explosive_tx = explosive_et.getText().toString().trim();
                        explosiveValid_tx = explosiveValid_et.getText().toString().trim();
                        explosiveRenew_tx = explosiveRenew_et.getText().toString().trim();
                        nationalPermit_tx = nationalPermit_et.getText().toString().trim();
                        statePermit_tx = statePermit_et.getText().toString().trim();
                        labourPermit_tx = labourPermit_et.getText().toString().trim();


                        dataRef_truckDetails.child("truck_number").setValue(truckNumber_tx2);
                        dataRef_truckDetails.child("chassis_number").setValue(chsssis_tx);
                        dataRef_truckDetails.child("engine_number").setValue(engine_tx);
                        dataRef_truckDetails.child("owner_name").setValue(ownerName_tx);
                        dataRef_truckDetails.child("attached_with").setValue(attached_tx);
                        dataRef_truckDetails.child("chambers").setValue(chamber_tx);
                        dataRef_truckDetails.child("capacity").setValue(capacity_tx);
                        dataRef_truckDetails.child("rc").setValue(rc_tx);
                        dataRef_truckDetails.child("rc_valid").setValue(rcValid_tx);
                        dataRef_truckDetails.child("rc_renew").setValue(rcRenew_tx);
                        dataRef_truckDetails.child("calibration_created").setValue(calibration_tx);
                        dataRef_truckDetails.child("calibration_valid").setValue(calibrationValid_tx);
                        dataRef_truckDetails.child("calibration_renew").setValue(calibrationRenew_tx);
                        dataRef_truckDetails.child("fit").setValue(fit_tx);
                        dataRef_truckDetails.child("fit_valid").setValue(fitValid_tx);
                        dataRef_truckDetails.child("fit_renew").setValue(fitrenew_tx);
                        dataRef_truckDetails.child("pollution_created").setValue(pollution_tx);
                        dataRef_truckDetails.child("pollution_valid").setValue(pollutionValid_tx);
                        dataRef_truckDetails.child("pollution_renew").setValue(pollutionRenew_tx);
                        dataRef_truckDetails.child("insurance_name").setValue(insuranceName_tx);
                        dataRef_truckDetails.child("insurance_amount").setValue(insuranceAmount_tx);
                        dataRef_truckDetails.child("insurance_renew").setValue(insuranceRenew_tx);
                        dataRef_truckDetails.child("insurance_valid").setValue(insuranceValid_tx);
                        dataRef_truckDetails.child("hydro_created").setValue(hydro_tx);
                        dataRef_truckDetails.child("hydro_valid").setValue(hydroValid_tx);
                        dataRef_truckDetails.child("hydro_renew").setValue(hydroRenew_tx);
                        dataRef_truckDetails.child("road_created").setValue(road_tx);
                        dataRef_truckDetails.child("road_valid").setValue(roadValid_tx);
                        dataRef_truckDetails.child("road_renew").setValue(roadRenew_tx);
                        dataRef_truckDetails.child("explosive_created").setValue(explosive_tx);
                        dataRef_truckDetails.child("explosive_valid").setValue(explosiveValid_tx);
                        dataRef_truckDetails.child("explosive_renew").setValue(explosiveRenew_tx);
                        dataRef_truckDetails.child("national_permit").setValue(nationalPermit_tx);
                        dataRef_truckDetails.child("state_permit").setValue(statePermit_tx);
                        dataRef_truckDetails.child("labour_permit").setValue(labourPermit_tx);
                        Alerter.create(getActivity())
                                .setTitle("Truck Details Updated")
                                .setContentGravity(1)
                                .setBackgroundColorRes(R.color.black)
                                .setIcon(R.drawable.success_icon)
                                .show();
                        dialog_updateTruck.dismiss();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            case R.id.ct_rcValidEditText:
                    date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        rcValid_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.ct_caliEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        calibration_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


            case R.id.ct_caliValidEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        calibrationValid_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


            case R.id.ct_caliRenewEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        calibrationRenew_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


            case R.id.ct_fitEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        fit_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


            case R.id.ct_fitRenewEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        fitrenew_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


            case R.id.ct_fitValidEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        fitValid_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


            case R.id.ct_explosiveEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        explosive_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


            case R.id.ct_explosiveRenewEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        explosiveRenew_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


            case R.id.ct_explosiveValidEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        explosiveValid_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


            case R.id.ct_roadEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        road_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


            case R.id.ct_roadRenewEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        roadRenew_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.ct_roadValidEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        roadValid_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


            case R.id.ct_hydroEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        hydro_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


            case R.id.ct_hydroRenewEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        hydroRenew_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;


            case R.id.ct_hydroValidEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        hydroValid_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.ct_insuranceRenewEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        insuranceRenew_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.ct_insuranceValidEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        insuranceValid_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.ct_pollutionRenewEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        pollutionRenew_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.ct_pollutionValidEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        pollutionValid_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.ct_pollutionEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        pollution_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;

            case R.id.ct_rcRenewEditText:
                date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        rcRenew_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date_birth, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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

    public void onStart(){
        super.onStart();


        dataRef_spinner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                try {
                    final List<String> areas = new ArrayList<String>();
                    areas.add("Select Truck");
                    for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                        String areaName = areaSnapshot.child("truck_number").getValue(String.class);
                        areas.add(areaName);


                    }

                    ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areas);
                    areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(areasAdapter);
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
    //end
}
