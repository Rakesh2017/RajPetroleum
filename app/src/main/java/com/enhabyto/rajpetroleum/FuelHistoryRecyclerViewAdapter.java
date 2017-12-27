package com.enhabyto.rajpetroleum;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import util.android.textviews.FontTextView;

/**
 * Created by this on 27-Dec-17.
 */

public class FuelHistoryRecyclerViewAdapter  extends RecyclerView.Adapter<FuelHistoryRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<ImageUploadInfo> MainImageUploadInfoList;
    String month;
    int position1;
    String day, year, hour, minute;

    public FuelHistoryRecyclerViewAdapter(Context context, List<ImageUploadInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_fuel_history_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ImageUploadInfo UploadInfo = MainImageUploadInfoList.get(position);


        holder.index_tv.setText(String.valueOf(holder.getAdapterPosition()));

        holder.setBy_tv.setText(UploadInfo.getSet_by());
        holder.fuelPrice_tv.setText("Rs "+UploadInfo.getCurrent_rate()+" per litre");





        String date = UploadInfo.getUpdated_on();

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

            holder.updatedOn_tv.setText(day+"-"+month+"-"+year+", "+hour+":"+minute);
        }
        catch (NullPointerException e){
            e.printStackTrace();
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


        FontTextView index_tv, fuelPrice_tv, updatedOn_tv, setBy_tv;

        ViewHolder(View itemView) {
            super(itemView);

            fuelPrice_tv = itemView.findViewById(R.id.fr_priceTextView);
            index_tv = itemView.findViewById(R.id.fr_indexTextView);
            updatedOn_tv = itemView.findViewById(R.id.fr_updatedOnTextView);
            setBy_tv = itemView.findViewById(R.id.fr_setByTextView);



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
