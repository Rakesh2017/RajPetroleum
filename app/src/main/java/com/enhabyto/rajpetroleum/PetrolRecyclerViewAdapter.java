package com.enhabyto.rajpetroleum;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    String month;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReferenceFromUrl("gs://rajpetroleum-4d3fa.appspot.com");

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


        holder.header_tv.setText(holder.header_tv.getText()+""+position+1);
        holder.pumpName_tv.setText(UploadInfo.getName());
        holder.tokenNumber_tv.setText(UploadInfo.getToken_number());
        holder.pumpAddress_tv.setText(UploadInfo.getAddress());
        holder.stateName_tv.setText(UploadInfo.getState());
        holder.moneyPaid_tv.setText(UploadInfo.getMoney_paid());
        holder.litres_tv.setText(UploadInfo.getPetrol_filled());

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

    }

    @Override
    public int getItemCount() {
        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        FontTextView pumpName_tv, tokenNumber_tv, pumpAddress_tv, stateName_tv, moneyPaid_tv, litres_tv, bill_tv, date_time_tv
                ,header_tv;
        ImageView image;


        ViewHolder(View itemView) {
            super(itemView);

            pumpName_tv = itemView.findViewById(R.id.petrol_pumpNameTextView);
            tokenNumber_tv = itemView.findViewById(R.id.petrol_tokenTextView);
            pumpAddress_tv = itemView.findViewById(R.id.petrol_pumpAddress1TextView);
            stateName_tv = itemView.findViewById(R.id.petrol_stateNameTextView);
            moneyPaid_tv = itemView.findViewById(R.id.petrol_moneyPaidTextView);
            litres_tv = itemView.findViewById(R.id.petrol_litresTextView);
            bill_tv = itemView.findViewById(R.id.petrol_billPhotoTextView);
            date_time_tv = itemView.findViewById(R.id.petrol_dateTextView);
            header_tv = itemView.findViewById(R.id.petrol_headerTextView);

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

}


    //end

