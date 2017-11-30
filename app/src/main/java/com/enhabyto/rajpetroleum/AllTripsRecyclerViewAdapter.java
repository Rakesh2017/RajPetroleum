package com.enhabyto.rajpetroleum;

/**AllTripsRecyclerViewAdapter
 * Created by this on 30-Nov-17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import util.android.textviews.FontTextView;


/**
 * Created by this on 29-Nov-17.
 */

public class AllTripsRecyclerViewAdapter  extends RecyclerView.Adapter<AllTripsRecyclerViewAdapter.ViewHolder> {

    Context context;
    List<AllTripsInfo> MainImageUploadInfoList;
    String month;


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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final AllTripsInfo UploadInfo = MainImageUploadInfoList.get(position);


        holder.text.setText(UploadInfo.getKey());





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


        TextView text;


        ViewHolder(View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.all_mobileNumber);

        }
    }



}
