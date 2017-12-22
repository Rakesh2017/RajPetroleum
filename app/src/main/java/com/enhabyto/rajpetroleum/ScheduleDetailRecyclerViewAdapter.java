package com.enhabyto.rajpetroleum;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import util.android.textviews.FontTextView;

/**
 * Created by this on 09-Dec-17.
 */

public class ScheduleDetailRecyclerViewAdapter  extends RecyclerView.Adapter<ScheduleDetailRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<ScheduleDetailRecyclerViewInfo> MainImageUploadInfoList;
    String month;


    public ScheduleDetailRecyclerViewAdapter(Context context, List<ScheduleDetailRecyclerViewInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_scheduledetails_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ScheduleDetailRecyclerViewInfo UploadInfo = MainImageUploadInfoList.get(position);


        position++;
        String status = UploadInfo.getStatus();
        holder.status_tv.setText(status);
        holder.truckNumber_tv.setText(UploadInfo.getTruck_number());
        holder.index_tv.setText(String.valueOf(position));

        if (TextUtils.equals(status, "accepted")){
            holder.imageView.setBackgroundResource(R.drawable.ic_check_black);
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.lightGreen));
        }
        else if (TextUtils.equals(status, "rejected")){
            holder.imageView.setBackgroundResource(R.drawable.ic_close_black);
            holder.status_tv.setTextColor(context.getResources().getColor(R.color.lightRed));
        }
        else if (TextUtils.equals(status, "waiting")){
            holder.imageView.setBackgroundResource(R.drawable.ic_help_outline_black);
        }



        String date = UploadInfo.getDate();
       try {

            String day = TextUtils.substring(date, 0, 2);
            String year = TextUtils.substring(date, 6, 10);
            String hour = TextUtils.substring(date, 11, 13);
            String minute = TextUtils.substring(date, 14, 16);
            month = TextUtils.substring(date, 3, 5);
            conversion();

            holder.date_tv.setText(day+"-"+month+"-"+year+", "+hour+":"+minute);
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

        FontTextView status_tv, date_tv, truckNumber_tv, index_tv;
        ImageView imageView;


        ViewHolder(View itemView) {
            super(itemView);

            status_tv = itemView.findViewById(R.id.scheduleDetails_endDateTextView);
            truckNumber_tv = itemView.findViewById(R.id.scheduleDetails_dateTextView);
            date_tv = itemView.findViewById(R.id.scheduleDetails_endPointTextView);
            index_tv = itemView.findViewById(R.id.scheduleDetails_index1);

            imageView = itemView.findViewById(R.id.scheduleDetails_image);






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
