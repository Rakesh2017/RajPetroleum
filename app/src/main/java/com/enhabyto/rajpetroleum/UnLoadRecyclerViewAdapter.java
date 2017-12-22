package com.enhabyto.rajpetroleum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.List;
import util.android.textviews.FontTextView;

/**
 * Created by this on 28-Nov-17.
 */

public class UnLoadRecyclerViewAdapter extends RecyclerView.Adapter<UnLoadRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<LoadRecyclerInfo> MainImageUploadInfoList;
    String month;
    int position1;
    String day, year, hour, minute;


    public UnLoadRecyclerViewAdapter(Context context, List<LoadRecyclerInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_unload_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final LoadRecyclerInfo UploadInfo = MainImageUploadInfoList.get(position);


        position++;
        position1 = position;
        holder.header_tv.setText(holder.header_tv.getText()+" "+position);
        holder.oilunLoaded_tv.setText(UploadInfo.getOil_unloaded()+" litres");
        holder.oilLeft_tv.setText(UploadInfo.getOil_left()+" litres");
        holder.pumpName_tv.setText(UploadInfo.getPump_name());
        holder.address_tv.setText(UploadInfo.getLocation());
        holder.state_tv.setText(UploadInfo.getState_name());
        holder.going_tv.setText(UploadInfo.getNext_stoppage());
        holder.gpsLocation_tv.setText(UploadInfo.getGps_location());

        String date = UploadInfo.getDate_time();

        try {
            day = TextUtils.substring(date, 0, 2);
            month = TextUtils.substring(date, 3, 5);
             year = TextUtils.substring(date, 6, 10);
            hour = TextUtils.substring(date, 11, 13);
             minute = TextUtils.substring(date, 14, 16);
            conversion();

            SharedPreferences dataSave = context.getSharedPreferences("driverContact", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = dataSave.edit();
            editor.putString("startDate", day+" "+month+" "+year+", "+hour+":"+minute);
            editor.apply();

            holder.date_tv.setText(day+"-"+month+"-"+year+", "+hour+":"+minute);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }


        if (UploadInfo.getState_name().equals("")){
            holder.state_tv.setText("NA");
            holder.state_tv.setTextColor(Color.GRAY);
        }

        if (UploadInfo.getNext_stoppage().equals("")){
            holder.going_tv.setText("NA");
            holder.going_tv.setTextColor(Color.GRAY);
        }


        holder.gpsLocation_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences dataSave = context.getSharedPreferences("maps", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = dataSave.edit();
                editor.putString("gps_longitude", UploadInfo.getGps_longitude());
                editor.putString("gps_latitude", UploadInfo.getGps_latitude());
                editor.putString("gps_message", "unLoad "+ position1 +" ("+ day+"-"+month+"-"+year+", "+hour+":"+minute+")");

                editor.apply();

                try {
                    Intent intent = new Intent(context,MapsActivity.class);
                    context.startActivity(intent);
                }
                catch (IllegalStateException e){
                    e.printStackTrace();
                }


            }
        });


    }

    @Override
    public int getItemCount() {
        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        FontTextView oilunLoaded_tv, address_tv, state_tv, going_tv, date_tv, header_tv, pumpName_tv, oilLeft_tv, gpsLocation_tv;

        ImageView map_image;


        ViewHolder(View itemView) {
            super(itemView);

            oilunLoaded_tv = itemView.findViewById(R.id.unload_loadedTextView);
            address_tv = itemView.findViewById(R.id.unload_addressTextView);
            state_tv = itemView.findViewById(R.id.unload_stateTextView);
            going_tv = itemView.findViewById(R.id.unload_goingTextView);
            date_tv = itemView.findViewById(R.id.unload_dateTextView);
            header_tv = itemView.findViewById(R.id.unload_headerTextView);
            pumpName_tv = itemView.findViewById(R.id.unload_pumpNameTextView);
            oilLeft_tv = itemView.findViewById(R.id.unload_leftTextView);
            gpsLocation_tv = itemView.findViewById(R.id.unload_gpsLocationTextView);





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
