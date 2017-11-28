package com.enhabyto.rajpetroleum;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import util.android.textviews.FontTextView;

/**
 * Created by this on 28-Nov-17.
 */

public class StoppageRecyclerViewAdapter  extends RecyclerView.Adapter<StoppageRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<StoppageRecyclerInfo> MainImageUploadInfoList;
    String month;


    public StoppageRecyclerViewAdapter(Context context, List<StoppageRecyclerInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_stoppage_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final StoppageRecyclerInfo UploadInfo = MainImageUploadInfoList.get(position);


        position++;
        holder.header_tv.setText(holder.header_tv.getText()+""+position);
        holder.moneyPaidFor_tv.setText(UploadInfo.getMoney_paid_for());
        holder.moneyPaid_tv.setText("Rs "+UploadInfo.getMoney_paid());
        holder.description_tv.setText(UploadInfo.getDescription());
        holder.placeName_tv.setText(UploadInfo.getPlace_name());

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

            holder.dateTime_tv.setText(day+"-"+month+"-"+year+", "+hour+":"+minute);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }


        if (UploadInfo.getDescription().equals("")){
            holder.description_tv.setText("NA");
            holder.description_tv.setTextColor(Color.GRAY);
        }

        if (UploadInfo.getMoney_paid().equals("")){
            holder.moneyPaid_tv.setText("NA");
            holder.moneyPaid_tv.setTextColor(Color.GRAY);
        }

        if (UploadInfo.getMoney_paid_for().equals("")){
            holder.moneyPaidFor_tv.setText("NA");
            holder.moneyPaidFor_tv.setTextColor(Color.GRAY);
        }



    }

    @Override
    public int getItemCount() {
        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        FontTextView placeName_tv, description_tv, moneyPaid_tv, moneyPaidFor_tv, dateTime_tv, header_tv;



        ViewHolder(View itemView) {
            super(itemView);

            placeName_tv = itemView.findViewById(R.id.stop_placeNameTextView);
            description_tv = itemView.findViewById(R.id.stop_descriptionTextView);
            moneyPaid_tv = itemView.findViewById(R.id.stop_moneyPaidTextView);
            moneyPaidFor_tv = itemView.findViewById(R.id.stop_paidForTextView);
            dateTime_tv = itemView.findViewById(R.id.stop_dateTextView);
            header_tv = itemView.findViewById(R.id.stop_headerTextView);




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

