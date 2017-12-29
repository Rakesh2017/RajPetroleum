package com.enhabyto.rajpetroleum;

/**AllTripsRecyclerViewAdapter
 * Created by this on 30-Nov-17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.List;



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

        position++;
        holder.index_tv.setText(String.valueOf(position));

        holder.contact_tv.setText(UploadInfo.getKey());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("driver_profiles").child(UploadInfo.getKey())
                .child("name");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.name_tv.setText("("+dataSnapshot.getValue(String.class)+")");
                if (TextUtils.equals(dataSnapshot.getValue(String.class),null) || TextUtils.equals(dataSnapshot.getValue(String.class),"")){
                    holder.name_tv.setText(" (not known)");
                }
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



        holder.truckImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences dataSave = context.getSharedPreferences("driverContact", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = dataSave.edit();
                editor.putString("contactUID_AllTrip", UploadInfo.getKey());
                editor.apply();

                try {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    activity.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new TripList()).addToBackStack("FragmentTripDetails").commit();

                }
                catch (IllegalStateException e){
                    e.printStackTrace();
                }


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


        TextView name_tv, contact_tv, totalTrips_tv, index_tv;
        ImageView truckImage;


        ViewHolder(View itemView) {
            super(itemView);

            name_tv = itemView.findViewById(R.id.all_nameTextView);
            contact_tv = itemView.findViewById(R.id.all_contactTextView);
            totalTrips_tv = itemView.findViewById(R.id.all_totalTripsTextView);
            truckImage = itemView.findViewById(R.id.all_truckImage);
            index_tv = itemView.findViewById(R.id.all_index);

        }
    }



}
