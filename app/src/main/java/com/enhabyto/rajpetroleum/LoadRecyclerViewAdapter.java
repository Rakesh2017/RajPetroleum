package com.enhabyto.rajpetroleum;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import util.android.textviews.FontTextView;

/**
 * Created by this on 28-Nov-17.
 */

public class LoadRecyclerViewAdapter extends RecyclerView.Adapter<LoadRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<LoadRecyclerInfo> MainImageUploadInfoList;
    String month;


    public LoadRecyclerViewAdapter(Context context, List<LoadRecyclerInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_load_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final LoadRecyclerInfo UploadInfo = MainImageUploadInfoList.get(position);


        position++;
        holder.header_tv.setText("");
        holder.header_tv.setText("Load "+position);

        holder.oilLoaded_tv.setText(UploadInfo.getOil_loaded()+" litres");
        holder.address_tv.setText(UploadInfo.getLocation());
        holder.state_tv.setText(UploadInfo.getState_name());
        holder.going_tv.setText(UploadInfo.getNext_stoppage());



        String date = UploadInfo.getDate_time();

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

        FontTextView oilLoaded_tv, address_tv, state_tv, going_tv, date_tv, header_tv;



        ViewHolder(View itemView) {
            super(itemView);

           oilLoaded_tv = itemView.findViewById(R.id.load_loadedTextView);
           address_tv = itemView.findViewById(R.id.load_addressTextView);
           state_tv = itemView.findViewById(R.id.load_stateTextView);
           going_tv = itemView.findViewById(R.id.load_goingTextView);
           date_tv = itemView.findViewById(R.id.load_dateTextView);
           header_tv = itemView.findViewById(R.id.load_headerTextView);





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
