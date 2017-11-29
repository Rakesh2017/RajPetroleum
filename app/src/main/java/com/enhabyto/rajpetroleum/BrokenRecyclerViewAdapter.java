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
 * Created by this on 29-Nov-17.
 */

public class BrokenRecyclerViewAdapter  extends RecyclerView.Adapter<BrokenRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<BrokenRecyclerInfo> MainImageUploadInfoList;
    String month;


    public BrokenRecyclerViewAdapter(Context context, List<BrokenRecyclerInfo> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_breakdown_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BrokenRecyclerInfo UploadInfo = MainImageUploadInfoList.get(position);


        position++;
        holder.header_tv.setText(holder.header_tv.getText()+""+position);
        holder.failureName_tv.setText(UploadInfo.getFailure_name());
        holder.address_tv.setText(UploadInfo.getAddress());
        holder.state_tv.setText(UploadInfo.getState_name());

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


        try {
            if (!UploadInfo.imageURL1.equals("")){
                holder.billPhotoMainHeader.setVisibility(View.VISIBLE);
                holder.billPaid1_tv.setVisibility(View.VISIBLE);
                holder.billHeader1.setVisibility(View.VISIBLE);
                holder.bill1_tv.setVisibility(View.VISIBLE);
                holder.image1.setVisibility(View.VISIBLE);

                holder.billPaid1_tv.setText("Rs "+UploadInfo.getBill_paid1());
                if (UploadInfo.getBill_paid1().equals("")){
                    holder.billPaid1_tv.setText("NA");
                    holder.billPaid1_tv.setTextColor(Color.GRAY);
                }

                Glide.with(context.getApplicationContext())
                        .load(UploadInfo.getImageURL1())
                        .asBitmap()
                        .fitCenter()
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(holder.image1);
            }

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }






        try {
            if (!UploadInfo.imageURL2.equals("")){
                holder.billHeader2.setVisibility(View.VISIBLE);
                holder.bill2_tv.setVisibility(View.VISIBLE);
                holder.image2.setVisibility(View.VISIBLE);
                holder.billPaid2_tv.setVisibility(View.VISIBLE);
                holder.billPaid2_tv.setText("Rs "+UploadInfo.getBill_paid2());
                if (UploadInfo.getBill_paid2().equals("")){
                    holder.billPaid2_tv.setText("NA");
                    holder.billPaid2_tv.setTextColor(Color.GRAY);
                }

                Glide.with(context.getApplicationContext())
                        .load(UploadInfo.getImageURL2())
                        .asBitmap()
                        .fitCenter()
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(holder.image2);
            }

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }



        try {

            if (!UploadInfo.imageURL3.equals("")){
                holder.billHeader3.setVisibility(View.VISIBLE);
                holder.bill3_tv.setVisibility(View.VISIBLE);
                holder.image3.setVisibility(View.VISIBLE);
                holder.billPaid3_tv.setVisibility(View.VISIBLE);
                holder.billPaid3_tv.setText("Rs "+UploadInfo.getBill_paid3());
                if (UploadInfo.getBill_paid3().equals("")){
                    holder.billPaid3_tv.setText("NA");
                    holder.billPaid3_tv.setTextColor(Color.GRAY);
                }

                Glide.with(context.getApplicationContext())
                        .load(UploadInfo.getImageURL2())
                        .asBitmap()
                        .fitCenter()
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(holder.image3);
            }

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        try {
            if (!UploadInfo.getImageURL4().equals("")){
                holder.brokenPhotoMainHeader_tv.setVisibility(View.VISIBLE);
                holder.brokenHeader1.setVisibility(View.VISIBLE);
                holder.image4.setVisibility(View.VISIBLE);

                Glide.with(context.getApplicationContext())
                        .load(UploadInfo.getImageURL4())
                        .asBitmap()
                        .fitCenter()
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(holder.image4);

            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }


        try {
            if (!UploadInfo.getImageURL5().equals("")){
                holder.brokenPhotoMainHeader_tv.setVisibility(View.VISIBLE);
                holder.brokenHeader2.setVisibility(View.VISIBLE);
                holder.image5.setVisibility(View.VISIBLE);

                Glide.with(context.getApplicationContext())
                        .load(UploadInfo.getImageURL5())
                        .asBitmap()
                        .fitCenter()
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(holder.image5);

            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }


        try {
            if (!UploadInfo.getImageURL6().equals("")){
                holder.brokenPhotoMainHeader_tv.setVisibility(View.VISIBLE);
                holder.brokenHeader3.setVisibility(View.VISIBLE);
                holder.image6.setVisibility(View.VISIBLE);

                Glide.with(context.getApplicationContext())
                        .load(UploadInfo.getImageURL4())
                        .asBitmap()
                        .fitCenter()
                        .diskCacheStrategy( DiskCacheStrategy.ALL )
                        .into(holder.image6);

            }
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

//        resources 1

        try {
            if (!UploadInfo.getResource_name1().equals("")){
                holder.resourceMainHeader.setVisibility(View.VISIBLE);

                holder.resourceUsed1_tv.setVisibility(View.VISIBLE);
                holder.resourcePrice1_tv.setVisibility(View.VISIBLE);
                holder.quantity1_tv.setVisibility(View.VISIBLE);
                holder.price1.setVisibility(View.VISIBLE);
                holder.quantity1.setVisibility(View.VISIBLE);
                holder.resourceHeader1.setVisibility(View.VISIBLE);

                holder.resourcePrice1_tv.setText("Rs "+UploadInfo.getResource_price1());
                holder.resourceUsed1_tv.setText(UploadInfo.getResource_name1());

                holder.quantity1_tv.setText(UploadInfo.getResource_quantity1());
                if (UploadInfo.getResource_quantity1().equals("")){
                    holder.quantity1_tv.setText("NA");
                    holder.quantity1_tv.setTextColor(Color.GRAY);


                }
            }

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }


       try {
            if (!UploadInfo.getResource_name2().equals("")){

                holder.resourceUsed2_tv.setVisibility(View.VISIBLE);
                holder.resourcePrice2_tv.setVisibility(View.VISIBLE);
                holder.quantity2_tv.setVisibility(View.VISIBLE);
                holder.price2.setVisibility(View.VISIBLE);
                holder.quantity2.setVisibility(View.VISIBLE);
                holder.resourceHeader2.setVisibility(View.VISIBLE);

                holder.resourcePrice2_tv.setText("Rs "+UploadInfo.getResource_price2());
                holder.resourceUsed2_tv.setText(UploadInfo.getResource_name2());
                holder.quantity2_tv.setText(UploadInfo.getResource_quantity2());
                if (UploadInfo.getResource_quantity1().equals("")){
                    holder.quantity2_tv.setText("NA");
                    holder.quantity2_tv.setTextColor(Color.GRAY);


                }
            }

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }


        try {
            if (!UploadInfo.getResource_name3().equals("")){

                holder.resourceUsed3_tv.setVisibility(View.VISIBLE);
                holder.resourcePrice3_tv.setVisibility(View.VISIBLE);
                holder.quantity3_tv.setVisibility(View.VISIBLE);
                holder.price3.setVisibility(View.VISIBLE);
                holder.quantity3.setVisibility(View.VISIBLE);
                holder.resourceHeader3.setVisibility(View.VISIBLE);

                holder.resourcePrice3_tv.setText("Rs "+UploadInfo.getResource_price3());
                holder.resourceUsed3_tv.setText(UploadInfo.getResource_name3());
                holder.quantity3_tv.setText(UploadInfo.getResource_quantity3());
                if (UploadInfo.getResource_quantity1().equals("")){
                    holder.quantity3_tv.setText("NA");
                    holder.quantity3_tv.setTextColor(Color.GRAY);


                }
            }

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        try {
            if (!UploadInfo.getResource_name4().equals("")){

                holder.resourceUsed4_tv.setVisibility(View.VISIBLE);
                holder.resourcePrice4_tv.setVisibility(View.VISIBLE);
                holder.quantity4_tv.setVisibility(View.VISIBLE);
                holder.price4.setVisibility(View.VISIBLE);
                holder.quantity4.setVisibility(View.VISIBLE);
                holder.resourceHeader4.setVisibility(View.VISIBLE);

                holder.resourcePrice4_tv.setText("Rs "+UploadInfo.getResource_price4());
                holder.resourceUsed4_tv.setText(UploadInfo.getResource_name4());
                holder.quantity4_tv.setText(UploadInfo.getResource_quantity4());
                if (UploadInfo.getResource_quantity1().equals("")){
                    holder.quantity4_tv.setText("NA");
                    holder.quantity4_tv.setTextColor(Color.GRAY);


                }
            }

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }


        try {
            if (!UploadInfo.getResource_name5().equals("")){
                holder.resourceUsed5_tv.setVisibility(View.VISIBLE);
                holder.resourcePrice5_tv.setVisibility(View.VISIBLE);
                holder.quantity5_tv.setVisibility(View.VISIBLE);
                holder.price5.setVisibility(View.VISIBLE);
                holder.quantity5.setVisibility(View.VISIBLE);
                holder.resourceHeader5.setVisibility(View.VISIBLE);

                holder.resourcePrice5_tv.setText("Rs "+UploadInfo.getResource_price5());
                holder.resourceUsed5_tv.setText(UploadInfo.getResource_name5());
                holder.quantity5_tv.setText(UploadInfo.getResource_quantity5());
                if (UploadInfo.getResource_quantity1().equals("")){
                    holder.quantity5_tv.setText("NA");
                    holder.quantity5_tv.setTextColor(Color.GRAY);


                }
            }

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        FontTextView failureName_tv, address_tv, state_tv, date_tv, header_tv
                ,resourceUsed1_tv ,resourceUsed2_tv ,resourceUsed3_tv ,resourceUsed4_tv ,resourceUsed5_tv
                ,resourcePrice1_tv ,resourcePrice2_tv ,resourcePrice3_tv ,resourcePrice4_tv ,resourcePrice5_tv
                ,quantity1_tv  ,quantity2_tv  ,quantity3_tv  ,quantity4_tv  ,quantity5_tv
                ,billPaid1_tv ,billPaid2_tv ,billPaid3_tv
                ,billPhotoMainHeader ,billHeader1 ,billHeader2 ,billHeader3
                ,bill1_tv ,bill2_tv ,bill3_tv
                ,brokenPhotoMainHeader_tv, brokenHeader1, brokenHeader2, brokenHeader3
                ,resourceMainHeader, resourceHeader1,resourceHeader2,resourceHeader3,resourceHeader4,resourceHeader5
                ,price1 ,price2 ,price3 ,price4 ,price5
                ,quantity1 ,quantity2 ,quantity3 ,quantity4 ,quantity5 ;

        ImageView image1 ,image2 ,image3 ,image4 ,image5 ,image6;



        ViewHolder(View itemView) {
            super(itemView);

            header_tv = itemView.findViewById(R.id.break_headerTextView);
            failureName_tv = itemView.findViewById(R.id.break_failureTextView);
            address_tv = itemView.findViewById(R.id.break_addressTextView);
            state_tv = itemView.findViewById(R.id.break_stateNameTextView);
            date_tv = itemView.findViewById(R.id.break_dateTextView);
            resourcePrice1_tv = itemView.findViewById(R.id.break_priceTextView1);
            resourcePrice2_tv = itemView.findViewById(R.id.break_priceTextView2);
            resourcePrice3_tv = itemView.findViewById(R.id.break_priceTextView3);
            resourcePrice4_tv = itemView.findViewById(R.id.break_priceTextView4);
            resourcePrice5_tv = itemView.findViewById(R.id.break_priceTextView5);

            resourceUsed1_tv = itemView.findViewById(R.id.break_resourceUsedTextView1);
            resourceUsed2_tv = itemView.findViewById(R.id.break_resourceUsedTextView2);
            resourceUsed3_tv = itemView.findViewById(R.id.break_resourceUsedTextView3);
            resourceUsed4_tv = itemView.findViewById(R.id.break_resourceUsedTextView4);
            resourceUsed5_tv = itemView.findViewById(R.id.break_resourceUsedTextView5);

            quantity1_tv = itemView.findViewById(R.id.break_quantityTextView1);
            quantity2_tv = itemView.findViewById(R.id.break_quantityTextView2);
            quantity3_tv = itemView.findViewById(R.id.break_quantityTextView3);
            quantity4_tv = itemView.findViewById(R.id.break_quantityTextView4);
            quantity5_tv = itemView.findViewById(R.id.break_quantityTextView5);
            billPaid1_tv = itemView.findViewById(R.id.break_billPrice1TextView1);
            billPaid2_tv = itemView.findViewById(R.id.break_billPrice1TextView2);
            billPaid3_tv = itemView.findViewById(R.id.break_billPrice1TextView3);
            image1 = itemView.findViewById(R.id.billPhoto1);
            image2 = itemView.findViewById(R.id.billPhoto2);
            image3 = itemView.findViewById(R.id.billPhoto3);
            image4 = itemView.findViewById(R.id.brokenPhoto1);
            image5 = itemView.findViewById(R.id.brokenPhoto2);
            image6 = itemView.findViewById(R.id.brokenPhoto3);

            billPhotoMainHeader = itemView.findViewById(R.id.break_billHeaderTextView);
            billHeader1 = itemView.findViewById(R.id.break_billHeadTextView1);
            billHeader2 = itemView.findViewById(R.id.break_billHeadTextView2);
            billHeader3 = itemView.findViewById(R.id.break_billHeadTextView3);
            bill1_tv = itemView.findViewById(R.id.break_billPriceTextView1);
            bill2_tv = itemView.findViewById(R.id.break_billPriceTextView2);
            bill3_tv = itemView.findViewById(R.id.break_billPriceTextView3);

            brokenPhotoMainHeader_tv = itemView.findViewById(R.id.break_brokenPartsHeaderTextView);
            brokenHeader1 = itemView.findViewById(R.id.break_brokenPartHead1);
            brokenHeader2 = itemView.findViewById(R.id.break_brokenPartHead2);
            brokenHeader3 = itemView.findViewById(R.id.break_brokenPartHead3);

            resourceMainHeader = itemView.findViewById(R.id.break_resourceTextView);
            resourceHeader1 = itemView.findViewById(R.id.break_resourceUsed1TextView1);
            resourceHeader2 = itemView.findViewById(R.id.break_resourceUsed1TextView2);
            resourceHeader3 = itemView.findViewById(R.id.break_resourceUsed1TextView3);
            resourceHeader4 = itemView.findViewById(R.id.break_resourceUsed1TextView4);
            resourceHeader5 = itemView.findViewById(R.id.break_resourceUsed1TextView5);
            price1 = itemView.findViewById(R.id.break_price1TextView1);
            price2 = itemView.findViewById(R.id.break_price1TextView2);
            price3 = itemView.findViewById(R.id.break_price1TextView3);
            price4 = itemView.findViewById(R.id.break_price1TextView4);
            price5 = itemView.findViewById(R.id.break_price1TextView5);
            quantity1 = itemView.findViewById(R.id.break_quantity1TextView1);
            quantity2 = itemView.findViewById(R.id.break_quantity1TextView2);
            quantity3 = itemView.findViewById(R.id.break_quantity1TextView3);
            quantity4 = itemView.findViewById(R.id.break_quantity1TextView4);
            quantity5 = itemView.findViewById(R.id.break_quantity1TextView5);

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
