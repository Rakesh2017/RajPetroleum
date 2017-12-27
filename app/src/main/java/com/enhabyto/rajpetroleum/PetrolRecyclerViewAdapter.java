package com.enhabyto.rajpetroleum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import util.android.textviews.FontTextView;

/**
 * Created by this on 27-Nov-17.
 */

public class PetrolRecyclerViewAdapter  extends RecyclerView.Adapter<PetrolRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<PetrolFillingRecyclerInfo> MainImageUploadInfoList;
    String month, date;
    int position1;
    String day, year, hour, minute;

    public PetrolRecyclerViewAdapter(Context context, List<PetrolFillingRecyclerInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_petrol_filling_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PetrolFillingRecyclerInfo UploadInfo = MainImageUploadInfoList.get(position);


        position++;
        position1 = position;
        holder.header_tv.setText("");
        holder.header_tv.setText("Petrol Filling "+position);
        holder.pumpName_tv.setText(UploadInfo.getName());
        holder.tokenNumber_tv.setText(UploadInfo.getToken_number());
        holder.pumpAddress_tv.setText(UploadInfo.getAddress());
        holder.stateName_tv.setText(UploadInfo.getState());
        holder.moneyPaid_tv.setText("Rs "+UploadInfo.getMoney_paid());
        holder.total_tv.setText("Rs "+UploadInfo.getTotal_money());
        holder.litres_tv.setText(UploadInfo.getPetrol_filled()+" litres");
        holder.gpsLocation_tv.setText(UploadInfo.getGps_location());
        holder.pumpRate_tv.setText("Rs "+UploadInfo.getPump_fuel_rate()+"/litre");


        if (TextUtils.isEmpty(UploadInfo.getPump_fuel_rate())){
            holder.pumpRate_tv.setText("NA");
            holder.pumpRate_tv.setTextColor(Color.GRAY);
        }

        date = UploadInfo.getDate_time();
        dateTime();
        holder.date_time_tv.setText(day+"-"+month+"-"+year+", "+hour+":"+minute);

        date = UploadInfo.getPump_fuel_rate_date();
        dateTime();
        holder.pumpRateDate_tv.setText(day+"-"+month+"-"+year+", "+hour+":"+minute);



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

        if (UploadInfo.getPetrol_filled().equals("")){
            holder.litres_tv.setText("NA");
            holder.litres_tv.setTextColor(Color.GRAY);
        }

        if (UploadInfo.getAddress().equals("")){
            holder.pumpAddress_tv.setText("NA");
            holder.pumpAddress_tv.setTextColor(Color.GRAY);
        }

        if (UploadInfo.getState().equals("")){
            holder.stateName_tv.setText("NA");
            holder.stateName_tv.setTextColor(Color.GRAY);
        }


        holder.gpsLocation_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences dataSave = context.getSharedPreferences("maps", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = dataSave.edit();
                editor.putString("gps_longitude", UploadInfo.getGps_longitude());
                editor.putString("gps_latitude", UploadInfo.getGps_latitude());
                editor.putString("gps_message", "Petrol Filling "+ position1 +" ("+ day+"-"+month+"-"+year+", "+hour+":"+minute+")");
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

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(UploadInfo.getImageURL()), "image/*");
                context.startActivity(intent);
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

        FontTextView pumpName_tv, tokenNumber_tv, pumpAddress_tv, stateName_tv, moneyPaid_tv, litres_tv, bill_tv, date_time_tv
                ,header_tv, gpsLocation_tv, total_tv, pumpRate_tv, pumpRateDate_tv;
        ImageView image, map_image;


        ViewHolder(View itemView) {
            super(itemView);

            pumpName_tv = itemView.findViewById(R.id.petrol_pumpNameTextView);
            tokenNumber_tv = itemView.findViewById(R.id.petrol_tokenTextView);
            pumpAddress_tv = itemView.findViewById(R.id.petrol_pumpAddressTextView);
            stateName_tv = itemView.findViewById(R.id.petrol_stateNameTextView);
            moneyPaid_tv = itemView.findViewById(R.id.petrol_moneyPaidTextView);
            litres_tv = itemView.findViewById(R.id.petrol_litresTextView);
            bill_tv = itemView.findViewById(R.id.petrol_billPhotoTextView);
            date_time_tv = itemView.findViewById(R.id.petrol_dateTextView);
            header_tv = itemView.findViewById(R.id.petrol_headerTextView);
            gpsLocation_tv = itemView.findViewById(R.id.petrol_gpsLocationTextView);
            total_tv = itemView.findViewById(R.id.petrol_totalTextView);
            pumpRate_tv = itemView.findViewById(R.id.petrol_pumpRateTextView);
            pumpRateDate_tv = itemView.findViewById(R.id.petrol_pumpRateDateTextView);

            image = itemView.findViewById(R.id.petrol_billImage);




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

      public void dateTime(){
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


          }
          catch (NullPointerException e){
              e.printStackTrace();
          }

      }

}


    //end

