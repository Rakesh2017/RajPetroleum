package com.enhabyto.rajpetroleum;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import util.android.textviews.FontTextView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleTripDetails extends Fragment {

    View view;
    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    List<ScheduleDetailRecyclerViewInfo> list = new ArrayList<>();
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    private String user_designation, sub_admin_contact, scheduler_contact;

    FontTextView header;
    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    String key;


    public ScheduleTripDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_schedule_trip_details, container, false);

        recyclerView = view.findViewById(R.id.scheduleDetails_recyclerView);
        header = view.findViewById(R.id.scheduleDetail_header);


        recyclerView.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(getContext());

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Data...");

        // Showing progress dialog.
        progressDialog.show();


        try{
            SharedPreferences shared = getActivity().getSharedPreferences("firstLog", MODE_PRIVATE);
            user_designation = (shared.getString("user_designation", ""));
            sub_admin_contact = (shared.getString("subAdmin_contact", ""));


        }
        catch (NullPointerException e){

            e.printStackTrace();
        }



        try{
            SharedPreferences shared = getActivity().getSharedPreferences("driverContact", MODE_PRIVATE);
            key = (shared.getString("scheduleTripContactUID", ""));

            d_root.child("driver_profiles").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String contact = dataSnapshot.child("contact").getValue(String.class);

                    header.setText(contact+" ("+name+")");
                    if (TextUtils.equals(name,"")){
                        header.setText(contact+" (unknown)");
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



        if (user_designation.equals("admin")){

            d_root.child("admin").child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    scheduler_contact = dataSnapshot.child("contact").getValue(String.class);

                    setAdapter1();
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

                    scheduler_contact = dataSnapshot.child("contact").getValue(String.class);

                    setAdapter1();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }




        return view;
    }


    public void setAdapter1(){

        databaseReference = d_root.child("trip_schedules_admin").child(scheduler_contact)
                .child(key);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }


                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    ScheduleDetailRecyclerViewInfo otherFillingRecyclerInfo = postSnapshot.getValue(ScheduleDetailRecyclerViewInfo.class);

                    list.add(otherFillingRecyclerInfo);

                }

                adapter = new ScheduleDetailRecyclerViewAdapter(getActivity(), list);


                //   Collections.reverse(list);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

                // Hiding the progress dialog.
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
                progressDialog.dismiss();

            }
        });



    }

}
