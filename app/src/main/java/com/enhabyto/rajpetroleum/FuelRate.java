package com.enhabyto.rajpetroleum;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import util.android.textviews.FontTextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FuelRate extends Fragment {

    View view;

    EditText newPrice_et;
    String newPrice_tx;

    AutoCompleteTextView pumpName_et;
    Spinner spinner;

    FontTextView previousRate_tv, updatedOn_tv;

    DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dataRef_spinner = d_root.child("pump_details");


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




        return view;
    }

    public void onStart(){
        super.onStart();


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

}
