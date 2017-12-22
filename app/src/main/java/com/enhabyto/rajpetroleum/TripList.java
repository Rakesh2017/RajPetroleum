package com.enhabyto.rajpetroleum;


import android.app.ProgressDialog;
import android.content.Context;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class TripList extends Fragment {

    View view;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    List<AllTripsInfo> list = new ArrayList<>();
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;

    private String contactUID_tx;
    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    String key, name;

    public TripList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_trip_list, container, false);

        SharedPreferences shared = getActivity().getSharedPreferences("driverContact", Context.MODE_PRIVATE);
        try{
            contactUID_tx = (shared.getString("contactUID_AllTrip", ""));
        }
        catch (NullPointerException e){
            contactUID_tx  = "";
        }

        recyclerView = view.findViewById(R.id.list_recyclerView);

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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("driver_profiles").child(contactUID_tx);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);
                FontTextView text;
                text = view.findViewById(R.id.all_text);

                String contact = dataSnapshot.child("contact").getValue(String.class);
                    text.setText(text.getText().toString()+contact+" ("+name+")");

                if (TextUtils.equals(name,null) || TextUtils.equals(name,"")){
                    text.setText(text.getText().toString()+contact+" (not known)");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        databaseReference = d_root.child("trip_details").child(contactUID_tx);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(list!=null) {
                    list.clear();  // v v v v important (eliminate duplication of data)
                }


                for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                    //    AllTripsInfo otherFillingRecyclerInfo = postSnapshot.getValue(AllTripsInfo.class);
                    list.add(new AllTripsInfo(postSnapshot.getRef().getKey()));
                    //     list.add(otherFillingRecyclerInfo);

                }

                adapter = new TripListRecyclerViewAdapter(getActivity(), list);


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



        return view;
    }

}
