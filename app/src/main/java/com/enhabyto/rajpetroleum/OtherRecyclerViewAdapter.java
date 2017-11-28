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

public class OtherRecyclerViewAdapter  extends RecyclerView.Adapter<OtherRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<OtherRecyclerInfo> MainImageUploadInfoList;
    String month;


    public OtherRecyclerViewAdapter(Context context, List<OtherRecyclerInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_other_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final OtherRecyclerInfo UploadInfo = MainImageUploadInfoList.get(position);

        position++;
        holder.header_tv.setText(holder.header_tv.getText()+""+position);
        holder.description_tv.setText(UploadInfo.getDescription());
        holder.fillingName_tv.setText(UploadInfo.getFilling_name());
        holder.quantity_tv.setText(UploadInfo.getQuantity());
        holder.moneyPaid_tv.setText("Rs "+UploadInfo.getMoney_paid());


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

            holder.date_time_tv.setText(day+"-"+month+"-"+year+", "+hour+":"+minute);
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        String url = UploadInfo.getImageURL();

        if (url != null){

            holder.image.setVisibility(View.VISIBLE);
            holder.bill_tv.setVisibility(View.VISIBLE);

            Glide.with(context.getApplicationContext())
                    .load(url)
                    .asBitmap()
                    .fitCenter()
                    .diskCacheStrategy( DiskCacheStrategy.ALL )
                    .into(holder.image);
        }

        if (UploadInfo.getQuantity().equals("")){
            holder.quantity_tv.setText("NA");
            holder.quantity_tv.setTextColor(Color.GRAY);
        }

        if (UploadInfo.getDescription().equals("")){
            holder.description_tv.setText("NA");
            holder.description_tv.setTextColor(Color.GRAY);
        }




    }

    @Override
    public int getItemCount() {
        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        FontTextView description_tv, fillingName_tv, imageURL, moneyPaid_tv, quantity_tv, date_time_tv
                ,header_tv, bill_tv;
        ImageView image;


        ViewHolder(View itemView) {
            super(itemView);

            description_tv = itemView.findViewById(R.id.other_descriptionTextView);
            fillingName_tv = itemView.findViewById(R.id.other_fillingNameTextView);
            moneyPaid_tv = itemView.findViewById(R.id.other_moneyPaidTextView);
            quantity_tv = itemView.findViewById(R.id.other_quantityFilledTextView);
            date_time_tv = itemView.findViewById(R.id.other_dateTextView);
            header_tv = itemView.findViewById(R.id.other_headerTextView);
            bill_tv = itemView.findViewById(R.id.other_billPhotoTextView);


            image = itemView.findViewById(R.id.other_billImage);



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