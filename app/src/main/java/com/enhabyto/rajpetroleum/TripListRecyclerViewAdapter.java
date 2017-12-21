package com.enhabyto.rajpetroleum;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import util.android.textviews.FontTextView;

/**
 * Created by this on 01-Dec-17.
 */

public class TripListRecyclerViewAdapter  extends RecyclerView.Adapter<TripListRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<AllTripsInfo> MainImageUploadInfoList;
    String month, contactUID_tx, date;


    public TripListRecyclerViewAdapter(Context context, List<AllTripsInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_triplist_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AllTripsInfo UploadInfo = MainImageUploadInfoList.get(position);

        String key = UploadInfo.getKey();

        SharedPreferences shared = context.getSharedPreferences("driverContact", Context.MODE_PRIVATE);
        try{
            contactUID_tx = shared.getString("contactUID_AllTrip", "");
        }
        catch (NullPointerException e){
            contactUID_tx  = "";
        }

        position++;
        holder.index_tv.setText(String.valueOf(position));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("trip_details").child(contactUID_tx).child(key);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.startLocation_tv.setText(dataSnapshot.child("start_trip").child("start_location").getValue(String.class));
                holder.endLocation_tv.setText(dataSnapshot.child("start_trip").child("end_location").getValue(String.class));

                date = dataSnapshot.child("start_trip").child("start_date").getValue(String.class);

            try {

            String day = TextUtils.substring(date, 0, 2);
            month = TextUtils.substring(date, 3, 5);
            String year = TextUtils.substring(date, 6, 10);
            String hour = TextUtils.substring(date, 11, 13);
            String minute = TextUtils.substring(date, 14, 16);
            conversion();
            holder.date_tv.setText(day+"-"+month+"-"+year+", "+hour+":"+minute);


                date = dataSnapshot.child("end_trip").child("end_date").getValue(String.class);
                day = TextUtils.substring(date, 0, 2);
                month = TextUtils.substring(date, 3, 5);
                year = TextUtils.substring(date, 6, 10);
                hour = TextUtils.substring(date, 11, 13);
                minute = TextUtils.substring(date, 14, 16);
                conversion();
                holder.endDate_tv.setText(day+"-"+month+"-"+year+", "+hour+":"+minute);

            SharedPreferences dataSave = context.getSharedPreferences("driverContact", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = dataSave.edit();
            editor.putString("startDate", day+" "+month+" "+year+", "+hour+":"+minute);
            editor.apply();


        }
        catch (NullPointerException | StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        holder.truckImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance().getReference().child("driver_profiles")
                        .child(contactUID_tx).child("name")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String name1 = dataSnapshot.getValue(String.class);

                                SharedPreferences dataSave = context.getSharedPreferences("driverContact", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = dataSave.edit();
                                editor.putString("contactUID", contactUID_tx);
                                editor.putString("TripSuperKey", UploadInfo.getKey());
                                editor.putString("driverName", name1);
                                editor.apply();

                                AppCompatActivity activity = (AppCompatActivity) context;
                                activity.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new ShowTripDetails()).addToBackStack("FragmentTripDetails").commit();




                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



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

        FontTextView startLocation_tv, endLocation_tv, date_tv, endDate_tv, index_tv;
        ImageView truckImage;



        ViewHolder(View itemView) {
            super(itemView);

            startLocation_tv = itemView.findViewById(R.id.list_startPointTextView);
            endLocation_tv = itemView.findViewById(R.id.list_endPointTextView);
            date_tv = itemView.findViewById(R.id.list_dateTextView);
            endDate_tv = itemView.findViewById(R.id.list_endDateTextView);
            truckImage = itemView.findViewById(R.id.list_truckImage);
            index_tv = itemView.findViewById(R.id.list_index);





        }
    }

    private void conversion() {

        switch (month) {

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