package com.enhabyto.rajpetroleum;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

import java.util.List;

import util.android.textviews.FontTextView;

/**
 * Created by this on 24-Nov-17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<TripRecyclerInfo> MainImageUploadInfoList;
    String month, key;
    ProgressDialog progressDialog;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://rajpetroleum-4d3fa.appspot.com");
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    public RecyclerViewAdapter(Context context, List<TripRecyclerInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final  ViewHolder holder, final int position) {
        final TripRecyclerInfo UploadInfo = MainImageUploadInfoList.get(position);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("driver_profiles").child(UploadInfo.getContact_tx())
                .child("name");
        progressDialog = new ProgressDialog(context);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Data...");

        // Showing progress dialog.


        holder.contact_tx.setText(UploadInfo.getContact_tx());

        holder.truckNumber_tx.setText(UploadInfo.getTruckNumber());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.name_tx.setText("("+dataSnapshot.getValue(String.class)+")");
                if (TextUtils.equals(dataSnapshot.getValue(String.class),null) || TextUtils.equals(dataSnapshot.getValue(String.class),"")){
                    holder.name_tx.setText(" (not known)");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        storageRef.child("driver_profiles").child(UploadInfo.getContact_tx())
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
                        .into(new BitmapImageViewTarget(holder.driverProfileImage) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                holder.driverProfileImage.setImageDrawable(circularBitmapDrawable);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Glide.with(context)
                        .load(R.drawable.driver_default_image_icon)
                        .fitCenter()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.driverProfileImage);
            }
        });


        String date = UploadInfo.getTripStarted();

        try {
            String day = TextUtils.substring(date, 0, 2);
            month = TextUtils.substring(date, 3, 5);
            String year = TextUtils.substring(date, 6, 10);
            String hour = TextUtils.substring(date, 11, 13);
            String minute = TextUtils.substring(date, 14, 16);
            conversion();

            SharedPreferences dataSave = context.getSharedPreferences("driverContact", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = dataSave.edit();
            editor.putString("startDate", day+" "+month+" "+year+", "+hour+":"+minute);
            editor.apply();

            holder.tripStarted_tx.setText(day+"-"+month+"-"+year+", "+hour+":"+minute);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }


        holder.truckImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                Query queryPetrolNumber = root.child("trip_details").child(UploadInfo.getContact_tx()).orderByKey().limitToLast(1);

                queryPetrolNumber.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            key = child.getKey();

                            FirebaseDatabase.getInstance().getReference().child("driver_profiles")
                                    .child(UploadInfo.getContact_tx()).child("name")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            String name1 = dataSnapshot.getValue(String.class);
                                            SharedPreferences dataSave = context.getSharedPreferences("driverContact", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = dataSave.edit();
                                            editor.putString("contactUID", UploadInfo.getContact_tx());
                                            editor.putString("TripSuperKey", key);
                                            editor.putString("driverName", name1);
                                            editor.apply();
                                            AppCompatActivity activity = (AppCompatActivity) context;
                                            activity.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new ShowTripDetails()).addToBackStack("FragmentTripDetail").commit();
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

                    }
                });




            }
        });







        //Loading image from Glide library.
       // Glide.with(context).load(UploadInfo.getImageURL()).into(holder.imageView);
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

         ImageView driverProfileImage;
         FontTextView contact_tx, tripStarted_tx, name_tx, truckNumber_tx, text;
         ImageButton truckImage;

         ViewHolder(View itemView) {
            super(itemView);

            driverProfileImage = itemView.findViewById(R.id.dash_profileImage);
            contact_tx =  itemView.findViewById(R.id.dash_contactTextView);
            tripStarted_tx =  itemView.findViewById(R.id.dash_tripStartTextView);
            name_tx =  itemView.findViewById(R.id.dash_nameTextView);
            truckNumber_tx =  itemView.findViewById(R.id.dash_truckNumberTextView);
            truckImage =  itemView.findViewById(R.id.dash_truckImage);


        }


    }


    private void conversion(){

        switch (month){

            case "1":
                month = "Jan";
                break;

            case "2":
                month = "Feb";
                break;

            case "3":
                month = "Mar";
                break;

            case "4":
                month = "Apr";
                break;

            case "5":
                month = "May";
                break;

            case "6":
                month = "Jun";
                break;

            case "7":
                month = "Jul";
                break;

            case "8":
                month = "Aug";
                break;

            case "9":
                month = "Sep";
                break;

            case "10":
                month = "Oct";
                break;

            case "11":
                month = "Nov";
                break;

            case "12":
                month = "Dec";
                break;



        }



    }
}