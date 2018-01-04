package com.enhabyto.rajpetroleum;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tapadoo.alerter.Alerter;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import mehdi.sakout.fancybuttons.FancyButton;
import util.android.textviews.FontTextView;

public class AppActivation extends AppCompatActivity {

    String appDateStart, appDateEnd, text, actualDate, extendDate;
    FontTextView daysLeft_tv, textViewDaysLeft;
    FancyButton activationButton;
    int timeLeft;
    EditText editText;
    ProgressDialog progressDialog;
    Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_activation);

        daysLeft_tv = findViewById(R.id.act_dayLeft);
        textViewDaysLeft = findViewById(R.id.act_daysLeftTextView);
        activationButton = findViewById(R.id.act_activationButton);
        editText = findViewById(R.id.act_editText);

        progressDialog = new ProgressDialog(AppActivation.this);


        // Setting up message in Progress dialog.
        progressDialog.setMessage("Activating...");

        activationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                text = editText.getText().toString().trim();
                progressDialog.show();

                if (!isNetworkAvailable()){
                    Alerter.create(AppActivation.this)
                            .setTitle("No Internet Connection!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                    progressDialog.dismiss();
                    return;
                }

                if (TextUtils.isEmpty(text) || text.length()<14){
                    Alerter.create(AppActivation.this)
                            .setTitle("Invalid Product Key!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    progressDialog.dismiss();
                    return;
                }

                thread = new Thread(){
                    public void run(){
                        String timeServer = "time-a.nist.gov";
                        NTPUDPClient timeClient = new NTPUDPClient();
                        InetAddress inetAddress = null;
                        try {
                            inetAddress = InetAddress.getByName(timeServer);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        TimeInfo timeInfo = null;
                        try {
                            timeInfo = timeClient.getTime(inetAddress);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        assert timeInfo != null;
                        long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                        final DateFormat date = new SimpleDateFormat("dd_MM_yyyy");
                        Date t = new Date(returnTime);
                        actualDate = date.format(t);


                        Calendar c = Calendar.getInstance();
                        c.setTime(t);
                        c.add(Calendar.YEAR, 1);  // number of year to add
                        extendDate = date.format(c.getTime());
                        Log.w("rakesh123", extendDate);


                        stopThread();


                    }
                };
                thread.start();


               new Handler().postDelayed(new Runnable() {
                   @Override
                   public void run() {

                       if (!TextUtils.isEmpty(actualDate) && !TextUtils.isEmpty(extendDate)){

                           FirebaseDatabase.getInstance().getReference()
                                   .addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                           final String productKey = dataSnapshot.child("product_key").child("licence").getValue(String.class);

                                           if (TextUtils.equals(productKey, text)){

                                               String dateStart = dataSnapshot.child("validity_left").child("app_start_date").getValue(String.class);
                                               String dateEnd = dataSnapshot.child("validity_left").child("app_end_date").getValue(String.class);

                                               String key = FirebaseDatabase.getInstance().getReference().push().getKey();


                                               FirebaseDatabase.getInstance().getReference().child("validity_logs").child(key).child("app_start_date").setValue(dateStart);
                                               FirebaseDatabase.getInstance().getReference().child("validity_logs").child(key).child("app_end_date").setValue(dateEnd);
                                               FirebaseDatabase.getInstance().getReference().child("validity_logs").child(key).child("date").setValue(actualDate);
                                               FirebaseDatabase.getInstance().getReference().child("validity_logs").child(key).child("product_key_used").setValue(text);

                                               FirebaseDatabase.getInstance().getReference().child("validity_left").child("app_start_date").setValue(actualDate);
                                               FirebaseDatabase.getInstance().getReference().child("validity_left").child("app_end_date").setValue(extendDate);


                                               String id = UUID.randomUUID().toString();
                                               String newProductKey = TextUtils.substring(id, 0, 14);
                                               FirebaseDatabase.getInstance().getReference().child("product_key").child("licence").setValue(newProductKey);

                                              // AppActivation.this.finish();
                                             //  Intent intent = new Intent(AppActivation.this, AppActivation.class);
                                             //  startActivity(intent);
                                               new MaterialDialog.Builder(AppActivation.this)
                                                       .title("Congratulations")
                                                       .titleColor(getResources().getColor(R.color.lightRed))
                                                       .content("You can avail our services seamlessly for one year.\nThank you for choosing us.")
                                                       .contentColor(Color.BLACK)
                                                       .positiveText("okay")
                                                       .positiveColor(getResources().getColor(R.color.lightGreen))
                                                       .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                           @Override
                                                           public void onClick(MaterialDialog dialog, DialogAction which) {
                                                               Intent intent = new Intent(AppActivation.this, DashBoard.class);
                                                               startActivity(intent);
                                                               AppActivation.this.finish();
                                                           }
                                                       })
                                                       .cancelable(false)
                                                       .dividerColor(Color.GRAY)
                                                       .show();


                                           }
                                           else {

                                               Alerter.create(AppActivation.this)
                                                       .setTitle("Wrong Product Key!")
                                                       .setContentGravity(1)
                                                       .setBackgroundColorRes(R.color.black)
                                                       .setIcon(R.drawable.error)
                                                       .show();

                                           }

                                           progressDialog.dismiss();


                                       }

                                       @Override
                                       public void onCancelled(DatabaseError databaseError) {
                                           progressDialog.dismiss();


                                       }
                                   });

                       }//if end
                       else {
                           Toast.makeText(AppActivation.this, "Unable to connect to server due to slow internet connection. Try Again", Toast.LENGTH_LONG).show();
                           progressDialog.dismiss();
                       }





                   }
               },7000);




            }
        });



        }





    public void onBackPressed(){
        super.onBackPressed();
        AppActivation.this.finish();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) (getApplication()).getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting() && activeNetworkInfo.isAvailable();
    }

    public void onStart() {
        super.onStart();








        // product key implementation
        if (isNetworkAvailable()) {
            FirebaseDatabase.getInstance().getReference().child("validity_left")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            appDateStart = dataSnapshot.child("app_start_date").getValue(String.class);
                            appDateEnd = dataSnapshot.child("app_end_date").getValue(String.class);

                            SimpleDateFormat format = new SimpleDateFormat("dd_MM_yyyy");

                            try {
                                Date dateStart = format.parse(appDateStart);
                                Date dateEnd = format.parse(appDateEnd);
                                long diff = dateEnd.getTime() - dateStart.getTime();
                                long diffDays = diff / (24 * 60 * 60 * 1000);
                                int timeLeft = (int) (diffDays);
                                SharedPreferences dataSave = getSharedPreferences("activation", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = dataSave.edit();
                                editor.putString("days", String.valueOf(timeLeft));
                                editor.apply();
                                daysLeft_tv.setVisibility(View.VISIBLE);

                                if (timeLeft <= 0) {
                                    findViewById(R.id.act_relative2).setVisibility(View.VISIBLE);
                                    daysLeft_tv.setTextSize(50);
                                    daysLeft_tv.setText("EXPIRED");
                                    YoYo.with(Techniques.FlipInX)
                                            .duration(1500)
                                            .playOn(daysLeft_tv);
                                    return;


                                }


                                textViewDaysLeft.setVisibility(View.VISIBLE);
                                daysLeft_tv.setText(String.valueOf(timeLeft));

                                YoYo.with(Techniques.FlipInX)
                                        .duration(1500)
                                        .playOn(daysLeft_tv);

                                YoYo.with(Techniques.DropOut)
                                        .duration(1500)
                                        .playOn(textViewDaysLeft);




                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
           }

           else {
            SharedPreferences dataSave = getSharedPreferences("activation", Context.MODE_PRIVATE);
            try{
                timeLeft = Integer.parseInt(dataSave.getString("days", "-1000000"));
                if (TextUtils.equals(String.valueOf(timeLeft), "-1000000")){
                    Toast.makeText(this, "Unexpected Error Occur", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (timeLeft <= 0) {

                    daysLeft_tv.setTextSize(50);
                    daysLeft_tv.setText("EXPIRED");
                    YoYo.with(Techniques.FlipInX)
                            .duration(1500)
                            .playOn(daysLeft_tv);

                    return;


                }


                textViewDaysLeft.setVisibility(View.VISIBLE);
                daysLeft_tv.setText(String.valueOf(timeLeft));

                YoYo.with(Techniques.FlipInX)
                        .duration(1500)
                        .playOn(daysLeft_tv);

                YoYo.with(Techniques.DropOut)
                        .duration(1500)
                        .playOn(textViewDaysLeft);
            }
            catch (NumberFormatException e){
                e.printStackTrace();
            }
        }


    }

    private synchronized void stopThread()
    {
        if (thread != null)
        {
            thread = null;
        }
    }



}



