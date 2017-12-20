package com.enhabyto.rajpetroleum;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleList extends Fragment {

    View view;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    List<AllTripsInfo> list = new ArrayList<>();
    ProgressDialog progressDialog;


    String key;

    DatabaseReference databaseReference;

    private String user_designation, sub_admin_contact, scheduler_contact;
    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();


    public ScheduleList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_schedule_list, container, false);

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

        recyclerView = view.findViewById(R.id.scheduleList_recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(getContext());

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Data...");

        // Showing progress dialog.
        progressDialog.show();


        return view;
    }

    public void setAdapter1(){

        databaseReference = d_root.child("trip_schedules_admin").child(scheduler_contact);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }


                for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                    list.add(new AllTripsInfo(postSnapshot.getRef().getKey()));
                }

                adapter = new ScheduleListRecyclerViewAdapter(getActivity(), list);
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


