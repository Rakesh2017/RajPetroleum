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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoadDetail extends Fragment {

    View view;


    // Creating RecyclerView.
    RecyclerView recyclerView, recyclerView1;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    RecyclerView.Adapter adapter1 ;
    List<LoadRecyclerInfo> list = new ArrayList<>();
    List<LoadRecyclerInfo> list1 = new ArrayList<>();
    ProgressDialog progressDialog;
    DatabaseReference databaseReference,databaseReference1;



    private String contactUID_tx;
    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    String key;



    public LoadDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_load_detail, container, false);

        SharedPreferences shared = getActivity().getSharedPreferences("driverContact", Context.MODE_PRIVATE);
        try{
            contactUID_tx = (shared.getString("contactUID", ""));
            key = (shared.getString("TripSuperKey", ""));
        }
        catch (NullPointerException e){
            contactUID_tx  = "";
        }

        recyclerView = view.findViewById(R.id.load_recyclerView);
        recyclerView1 = view.findViewById(R.id.unLoad_recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView1.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();
        recyclerView1.isDuplicateParentStateEnabled();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager1.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager1.setStackFromEnd(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView1.setLayoutManager(mLayoutManager1);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(getContext());

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Data...");

        // Showing progress dialog.
        progressDialog.show();




                databaseReference = d_root.child("trip_details").child(contactUID_tx)
                        .child(key).child("load");

                databaseReference1 = d_root.child("trip_details").child(contactUID_tx)
                        .child(key).child("unload");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(list!=null) {
                            list.clear();  // v v v v important (eliminate duplication of data)
                        }


                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                            LoadRecyclerInfo loadRecyclerInfo = postSnapshot.getValue(LoadRecyclerInfo.class);

                            list.add(loadRecyclerInfo);
                        }

                        adapter = new LoadRecyclerViewAdapter(getActivity(), list);
                        //   Collections.reverse(list);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);

                        // Hiding the progress dialog.

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        // Hiding the progress dialog.
                        progressDialog.dismiss();

                    }
                });


                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(list1!=null) {
                            list1.clear();  // v v v v important (eliminate duplication of data)
                        }

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                            LoadRecyclerInfo loadRecyclerInfo = postSnapshot.getValue(LoadRecyclerInfo.class);

                            list1.add(loadRecyclerInfo);
                        }

                        adapter1 = new UnLoadRecyclerViewAdapter(getActivity(), list1);
                        //   Collections.reverse(list);
                        adapter1.notifyDataSetChanged();
                        recyclerView1.setAdapter(adapter1);

                        // Hiding the progress dialog.
                        progressDialog.dismiss();
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
