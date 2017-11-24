package com.enhabyto.rajpetroleum;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    String month;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://rajpetroleum-4d3fa.appspot.com");

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
    public void onBindViewHolder(final  ViewHolder holder, int position) {
        TripRecyclerInfo UploadInfo = MainImageUploadInfoList.get(position);

        holder.contact_tx.setText(UploadInfo.getContact_tx());
        holder.name_tx.setText("( "+UploadInfo.getDriverName()+" )");

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
            holder.tripStarted_tx.setText(day+" "+month+" "+year+", "+hour+":"+minute);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }








        //Loading image from Glide library.
       // Glide.with(context).load(UploadInfo.getImageURL()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

         ImageView driverProfileImage;
         FontTextView contact_tx, tripStarted_tx, name_tx;

         ViewHolder(View itemView) {
            super(itemView);

            driverProfileImage = itemView.findViewById(R.id.dash_profileImage);
            contact_tx =  itemView.findViewById(R.id.dash_contactTextView);
            tripStarted_tx =  itemView.findViewById(R.id.das_tripStartTextView);
            name_tx =  itemView.findViewById(R.id.dash_nameTextView);
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