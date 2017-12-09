package com.enhabyto.rajpetroleum;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    DatabaseReference databaseReference, databaseReference1;

    private String contactUID_tx;
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





        databaseReference = d_root.child("trip_schedules");
        databaseReference1 = d_root.child("trip_schedules");


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String key = postSnapshot.getKey();

                    databaseReference1.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()){
                                ScheduleDetailRecyclerViewInfo petrolFillingRecyclerInfo = child.getValue(ScheduleDetailRecyclerViewInfo.class);

                                list.add(petrolFillingRecyclerInfo);
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

                        }
                    });



                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
                progressDialog.dismiss();

            }
        });


        return view;
    }

}
