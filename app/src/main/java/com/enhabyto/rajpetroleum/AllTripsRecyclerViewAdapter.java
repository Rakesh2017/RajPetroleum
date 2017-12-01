package com.enhabyto.rajpetroleum;

/**AllTripsRecyclerViewAdapter
 * Created by this on 30-Nov-17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import util.android.textviews.FontTextView;


/**
 * Created by this on 29-Nov-17.
 */

public class AllTripsRecyclerViewAdapter  extends RecyclerView.Adapter<AllTripsRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<AllTripsInfo> MainImageUploadInfoList;
    String month;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://rajpetroleum-4d3fa.appspot.com");



    public AllTripsRecyclerViewAdapter(Context context, List<AllTripsInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_alltrips_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AllTripsInfo UploadInfo = MainImageUploadInfoList.get(position);


        holder.contact_tv.setText(UploadInfo.getKey());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("driver_profiles").child(UploadInfo.getKey())
                .child("name");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.name_tv.setText("("+dataSnapshot.getValue(String.class)+")");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("trip_details").child(UploadInfo.getKey());

        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 holder.totalTrips_tv.setText(String.valueOf(dataSnapshot.getChildrenCount())); dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        storageRef.child("driver_profiles").child(UploadInfo.getKey())
                .child("/profile_image/image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'

                Glide.with(context)
                        .load(uri)
                        .asBitmap()
                        .fitCenter()
                        .centerCrop()
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(new BitmapImageViewTarget(holder.imageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                holder.imageView.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });


        holder.truckImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences dataSave = context.getSharedPreferences("driverContact", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = dataSave.edit();
                editor.putString("contactUID_AllTrip", UploadInfo.getKey());
                editor.apply();

                AppCompatActivity activity = (AppCompatActivity) context;
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard, new TripList()).addToBackStack("FragmentTripDetails").commit();

            }
        });



    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        TextView name_tv, contact_tv, totalTrips_tv;
        ImageView imageView, truckImage;


        ViewHolder(View itemView) {
            super(itemView);

            name_tv = itemView.findViewById(R.id.all_nameTextView);
            contact_tv = itemView.findViewById(R.id.all_contactTextView);
            totalTrips_tv = itemView.findViewById(R.id.all_totalTripsTextView);
            imageView = itemView.findViewById(R.id.all_profileImage);
            truckImage = itemView.findViewById(R.id.all_truckImage);

        }
    }



}
