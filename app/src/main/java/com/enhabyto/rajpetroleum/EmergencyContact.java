package com.enhabyto.rajpetroleum;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;
import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmergencyContact extends Fragment {

    View view;

    EditText contact1_et,contact2_et,contact3_et;
    String contact1_tx,contact2_tx,contact3_tx;
    Dialog dialog;

    FancyButton button;

    public EmergencyContact() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_emergency_contact, container, false);

        contact1_et = view.findViewById(R.id.emergency_contact1EditText);
        contact2_et = view.findViewById(R.id.emergency_contact2EditText);
        contact3_et = view.findViewById(R.id.emergency_contact3EditText);

        dialog = new SpotsDialog(getActivity(),R.style.dialog_updating);

        button = view.findViewById(R.id.emergency_submitButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                contact1_tx = contact1_et.getText().toString().trim();
                contact2_tx = contact2_et.getText().toString().trim();
                contact3_tx = contact3_et.getText().toString().trim();



                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseReference root_contact = FirebaseDatabase.getInstance().getReference().child("emergency_contacts");
                        root_contact.child("contact1").setValue(contact1_tx);
                        root_contact.child("contact2").setValue(contact2_tx);
                        root_contact.child("contact3").setValue(contact3_tx);
                        dialog.dismiss();
                    }
                },2000);

            }
        });

        return view;
    }


    public void onStart(){
        super.onStart();
        DatabaseReference root_contact = FirebaseDatabase.getInstance().getReference().child("emergency_contacts");

        root_contact.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contact1_tx = dataSnapshot.child("contact1").getValue(String.class);
                contact2_tx = dataSnapshot.child("contact2").getValue(String.class);
                contact3_tx = dataSnapshot.child("contact3").getValue(String.class);

                contact1_et.setText(contact1_tx);
                contact2_et.setText(contact2_tx);
                contact3_et.setText(contact3_tx);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

}
