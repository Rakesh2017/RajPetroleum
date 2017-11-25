package com.enhabyto.rajpetroleum;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tapadoo.alerter.Alerter;

import util.android.textviews.FontTextView;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowTripDetails extends Fragment {

    private View view;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://rajpetroleum-4d3fa.appspot.com/");

    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference databaseReference;

    private String contactUID_tx, startDate_tx;
    FontTextView contact_tv, name_tv, truckNumber_tv, startDate_tv;
    String contact_tx, name_tx, truckNumber_tx;

    ImageView profileImage;



    public ShowTripDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_show_trip_details, container, false);

        SharedPreferences shared = getActivity().getSharedPreferences("driverContact", Context.MODE_PRIVATE);
        try{
            contactUID_tx = (shared.getString("contactUID", ""));
            startDate_tx = (shared.getString("startDate", ""));
        }
        catch (NullPointerException e){
            contactUID_tx  = "";
        }

        contact_tv = view.findViewById(R.id.detail_contactTextView);
        name_tv = view.findViewById(R.id.detail_nameTextView);
        truckNumber_tv = view.findViewById(R.id.detail_truckNumberTextView);
        startDate_tv = view.findViewById(R.id.detail_tripStartTextView);

        profileImage = view.findViewById(R.id.detail_profileImage);

        storageRef.child("driver_profiles").child(contactUID_tx)
                .child("/profile_image/image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

                Glide.with(getContext())
                        .load(uri)
                        .asBitmap()
                        .fitCenter()
                        .centerCrop()
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(new BitmapImageViewTarget(profileImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                profileImage.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });



        contact_tv.setText(contactUID_tx);
        startDate_tv.setText(startDate_tx);

        databaseReference = FirebaseDatabase.getInstance().getReference("driver_profiles").child(contactUID_tx);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name_tv.setText("( "+dataSnapshot.child("name").getValue(String.class)+" )");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        final Query query = d_root.child("trip_details").child(contactUID_tx).orderByKey().limitToLast(1);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!isNetworkAvailable()) {
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setText("Need a decent internet connection, please try again")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();

                }

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            truckNumber_tx = child.child("start_trip").child("truck_number").getValue(String.class);
                            Toast.makeText(getActivity(), ""+truckNumber_tx, Toast.LENGTH_SHORT).show();

                        }


//                        setting values

                        truckNumber_tv.setText(truckNumber_tx);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;
    }

    //    internet check
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }




    //end
}
