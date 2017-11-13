package com.enhabyto.rajpetroleum;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import java.util.Locale;
import dmax.dialog.SpotsDialog;
import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllocateTruck extends Fragment {

    private View view;
    Spinner spinner_truck, spinner_driver, spinner_truckLocation, spinner_startPoint, spinner_endPoint;
    DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    DatabaseReference d_networkStatus = FirebaseDatabase.getInstance().getReference().child("checkNetwork").child("isConnected");
    DatabaseReference dataRef_spinner = d_root;
    DatabaseReference dataRef_trip_schedule = d_root.child("trip_schedules");
    String selected_val;
    FancyButton allocate_btn;
    String selected_contact_tx, selected_truck_tx, selected_truckLocation_tx, selected_startPoint_tx, selected_endPoint_tx;
    String truckLocation_tx, startPoint_tx, endPoint_tx, startDate_tx, endDate_tx, startTime_tx, endTime_tx;
    EditText startDate_et, startTime_et, endDate_et, endTime_et;
    AutoCompleteTextView truckNumber_et, contact_et, truckLocation_et, startPoint_et, endPoint_et;
    String truckNumber_tx, contact_tx, connected;


    Calendar myCalendar = Calendar.getInstance();
    String myFormat = "dd/MM/yyyy"; // your format
    String timeFormat = "HH:mm";
    DatePickerDialog.OnDateSetListener date;
    TimePickerDialog.OnTimeSetListener time;

    AlertDialog dialog_scheduleTrip;

    public AllocateTruck() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_allocate_truck, container, false);
        spinner_truck = view.findViewById(R.id.at_spinnerTruck);
        spinner_driver = view.findViewById(R.id.at_spinnerContact);
        spinner_truckLocation = view.findViewById(R.id.at_truckLocation);
        spinner_startPoint = view.findViewById(R.id.at_spinnerStartPoint);
        spinner_endPoint = view.findViewById(R.id.at_spinnerEndPoint);
        truckNumber_et = view.findViewById(R.id.at_truckNumberEditText);
        contact_et = view.findViewById(R.id.at_contactEditText);
        allocate_btn = view.findViewById(R.id.at_allocateTripButton);

        truckLocation_et = view.findViewById(R.id.at_truckLocationEditText);
        startPoint_et = view.findViewById(R.id.at_startPointEditText);
        endPoint_et = view.findViewById(R.id.at_endPointEditText);

        startDate_et = view.findViewById(R.id.at_startDateEditText);
        endDate_et = view.findViewById(R.id.at_endDateEditText);
        startTime_et = view.findViewById(R.id.at_startTimeEditText);
        endTime_et = view.findViewById(R.id.at_endTimeEditText);

        dialog_scheduleTrip = new SpotsDialog(getActivity(), R.style.dialog_schedulingTrip);


        startDate_et.setKeyListener(null);
        endTime_et.setKeyListener(null);
        endDate_et.setKeyListener(null);
        startTime_et.setKeyListener(null);


        spinner_truck.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    spinner_truck.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {

                            selected_truck_tx = spinner_truck.getSelectedItem().toString();
                            if (!TextUtils.equals(selected_val,"Select Truck")){
                                truckNumber_et.setText(selected_truck_tx);

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


        spinner_driver.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    spinner_driver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {

                            selected_contact_tx = spinner_driver.getSelectedItem().toString();
                            if (!TextUtils.equals(selected_val,"Select Contact")){
                                contact_et.setText(selected_contact_tx);

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


        spinner_truckLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    spinner_truckLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {

                            selected_truckLocation_tx = spinner_truckLocation.getSelectedItem().toString();
                            if (!TextUtils.equals(selected_truckLocation_tx,"Select Location")){
                                truckLocation_et.setText(selected_truckLocation_tx);

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


        spinner_startPoint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    spinner_startPoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {

                            selected_startPoint_tx = spinner_startPoint.getSelectedItem().toString();
                            if (!TextUtils.equals(selected_startPoint_tx,"Select Location")){
                                startPoint_et.setText(selected_startPoint_tx);

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



        spinner_endPoint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    spinner_endPoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {

                            selected_endPoint_tx = spinner_endPoint.getSelectedItem().toString();
                            if (!TextUtils.equals(selected_endPoint_tx,"Select Location")){
                                endPoint_et.setText(selected_endPoint_tx);

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


        startDate_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        startDate_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();


            }
        });


        endDate_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                date = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                        endDate_et.setText(sdf.format(myCalendar.getTime()));
                    }
                };
                new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();


            }
        });



        startTime_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                time = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        myCalendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
                        startTime_et.setText(sdf.format(myCalendar.getTime()));


                    }
                };
               new TimePickerDialog(getContext(), time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), false).show();


            }
        });




        endTime_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                time = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        myCalendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
                        endTime_et.setText(sdf.format(myCalendar.getTime()));


                    }
                };
                new TimePickerDialog(getContext(), time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), false).show();


            }
        });




        allocate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contact_tx = contact_et.getText().toString().trim();
                truckNumber_tx = truckNumber_et.getText().toString().trim();
                truckLocation_tx = truckLocation_et.getText().toString().trim();
                startPoint_tx = startTime_et.getText().toString().trim();
                endPoint_tx = endPoint_et.getText().toString().trim();
                startDate_tx = startDate_et.getText().toString().trim();
                startTime_tx = startTime_et.getText().toString().trim();
                endDate_tx = endDate_et.getText().toString().trim();
                endTime_tx = endTime_et.getText().toString().trim();


                truckNumber_tx = truckNumber_et.getText().toString().trim();
                contact_tx = contact_et.getText().toString().trim();

                if (TextUtils.equals(truckNumber_tx, "")){
                    Alerter.create(getActivity())
                            .setTitle("Enter truck Number")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    return;
                }

                if (contact_tx.length()<10){
                    Alerter.create(getActivity())
                            .setTitle("Invalid Mobile Number!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    return;
                }


                new MaterialDialog.Builder(getActivity())
                        .title("Schedule Trip")
                        .content("Are You Sure to Allocate Truck Number\n"+truckNumber_tx
                                + " to " + contact_tx+" ?"
                                + "\n\nStart Point: "+ startPoint_tx
                                + "\n\nEnd Point: " + endPoint_tx
                                + "\n\nExpected Start Date: "+ startPoint_tx
                                + "\n\nExpected Start Time: "+ startTime_tx
                                + "\n\nExpected End Date: "+ endDate_tx
                                + "\n\nExpected End Time: "+ endTime_tx
                        )
                        .positiveText("Yes")
                        .contentColor(getResources().getColor(R.color.blackNinety))
                        .positiveColor(getResources().getColor(R.color.lightGreen))
                        .negativeText("No")
                        .negativeColor(getResources().getColor(R.color.lightRed))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog_scheduleTrip.show();

                                    if(!isNetworkAvailable()){
                                        Alerter.create(getActivity())
                                                .setTitle("No Internet Connection!")
                                                .setContentGravity(1)
                                                .setBackgroundColorRes(R.color.black)
                                                .setIcon(R.drawable.no_internet)
                                                .show();
                                         dialog_scheduleTrip.dismiss();
                                        return;
                                    }

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
                                    public void run() {




                                    dialog_scheduleTrip.dismiss();
                                    }

                                }, 2000);


                                // TODO
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // TODO

                            }
                        }) .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                        dialog_scheduleTrip.dismiss();
                    }
                })
                        .show();
            }
        });


        return view;
    }

    public void onStart(){
        super.onStart();

        dataRef_spinner.child("truck_details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    final List<String> areas = new ArrayList<String>();
                    areas.add("Select Truck");
                    for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                        String areaName = areaSnapshot.child("truck_number").getValue(String.class);
                        areas.add(areaName);


                    }

                    ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areas);
                    ArrayAdapter<String> areasAdapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, areas);

                    areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_truck.setAdapter(areasAdapter);
                    truckNumber_et.setAdapter(areasAdapter1);
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


        dataRef_spinner.child("driver_profiles").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    final List<String> areas = new ArrayList<>();
                    areas.add("Select Contact");
                    for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                        String contact = areaSnapshot.child("contact").getValue(String.class);
                        String name = areaSnapshot.child("name").getValue(String.class);

                        if (TextUtils.equals(name, null)){
                            areas.add(contact+" (Not Available)");
                        }
                        else areas.add(contact+" ("+name+")");


                    }

                    ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areas);
                    ArrayAdapter<String> areasAdapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, areas);

                    areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_driver.setAdapter(areasAdapter);
                    contact_et.setAdapter(areasAdapter1);

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


        dataRef_spinner.child("pump_details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    final List<String> areas = new ArrayList<>();
                    areas.add("Select Location");
                    for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                        String pump_name = areaSnapshot.child("pump_name").getValue(String.class);

                        areas.add(pump_name);


                    }

                    ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areas);
                    ArrayAdapter<String> areasAdapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, areas);

                    areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_truckLocation.setAdapter(areasAdapter);
                    truckLocation_et.setAdapter(areasAdapter1);
                    spinner_startPoint.setAdapter(areasAdapter);
                    startPoint_et.setAdapter(areasAdapter1);
                    spinner_endPoint.setAdapter(areasAdapter);
                    endPoint_et.setAdapter(areasAdapter1);

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //end
}
