package com.enhabyto.rajpetroleum;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import util.android.textviews.FontTextView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by this on 11-Dec-17.
 */

public class ScheduleListRecyclerViewAdapter extends RecyclerView.Adapter<ScheduleListRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<AllTripsInfo> MainImageUploadInfoList;
    String month;
    private String user_designation, sub_admin_contact, scheduler_contact;

    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();


    public ScheduleListRecyclerViewAdapter(Context context, List<AllTripsInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_schedule_list_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AllTripsInfo UploadInfo = MainImageUploadInfoList.get(position);


        position++;
        holder.index_tv.setText(String.valueOf(position));


        FirebaseDatabase.getInstance().getReference()
                .child("driver_profiles").child(UploadInfo.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String contact = dataSnapshot.child("contact").getValue(String.class);

                        holder.driverName_tv.setText(name);
                        holder.driverContact_tv.setText(contact);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        try{
            SharedPreferences shared = context.getSharedPreferences("firstLog", MODE_PRIVATE);
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
                    d_root.child("trip_schedules_admin").child(scheduler_contact).child(UploadInfo.getKey())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    holder.number.setText(String.valueOf(dataSnapshot.getChildrenCount()));
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


        }

        else if (user_designation.equals("subAdmin")){
            d_root.child("sub_admin_profiles").child(sub_admin_contact).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    scheduler_contact = dataSnapshot.child("contact").getValue(String.class);
                    d_root.child("trip_schedules_admin").child(scheduler_contact).child(UploadInfo.getKey())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    holder.number.setText(String.valueOf(dataSnapshot.getChildrenCount()));
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
        }


        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences dataSave = context.getSharedPreferences("driverContact", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = dataSave.edit();
                editor.putString("scheduleTripContactUID", UploadInfo.getKey());
                editor.apply();

                AppCompatActivity activity = (AppCompatActivity) context;
                activity.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new ScheduleTripDetails()).addToBackStack("FragmentTripDetails").commit();

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

        FontTextView driverName_tv, driverContact_tv, number, index_tv;
        ImageButton imageButton;


        ViewHolder(View itemView) {
            super(itemView);

            driverName_tv = itemView.findViewById(R.id.scheduleList_startPointTextView);
            driverContact_tv = itemView.findViewById(R.id.scheduleList_dateTextView);
            number = itemView.findViewById(R.id.scheduleList_totalTextView);
            imageButton = itemView.findViewById(R.id.scheduleList_truck);
            index_tv = itemView.findViewById(R.id.scheduleList_index);


        }
    }



}

