package com.enhabyto.rajpetroleum;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tapadoo.alerter.Alerter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import br.com.goncalves.pugnotification.notification.PugNotification;

/**
 * Created by this on 14-Dec-17.
 */

public class BackgroundService extends Service {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    String day, year, month, dateLeft, truck_number;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        public void run() {

            if(!isNetworkAvailable()){
                Alerter.create((Activity) context)
                        .setTitle("No Internet Connection!")
                        .setContentGravity(1)
                        .setBackgroundColorRes(R.color.black)
                        .setIcon(R.drawable.no_internet)
                        .show();
                return;
            }

            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {




                        final DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("truck_details");

                        final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Intent showIntent = new Intent(context, DashBoard.class);
                        final PendingIntent pIntent = PendingIntent.getActivity(context
                                , 0, showIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                        root.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    final String key = snapshot.getKey();

                                    final String date = new SimpleDateFormat("dd_MM_yyyy").format(new Date());

                                    root.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            dateLeft = dataSnapshot.child("rc_valid").getValue(String.class);
                                            truck_number = dataSnapshot.child("truck_number").getValue(String.class);
                                            if (!TextUtils.equals(dateLeft,"")){
                                                try{
                                                    dateLeft = TextUtils.substring(dateLeft, 0, 10);
                                                    long time = DayBetween(date, dateLeft, "dd_MM_yyyy");
                                                    int timeLeft = (int)(time);
                                                    conversion();
                                                    assert truck_number != null;

                                                    if (timeLeft > 1 && timeLeft < 8){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("RC will expire in "+timeLeft+" days "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " RC will expire on \n"+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"RC")
                                                                .simple()
                                                                .build();
                                                    } // if week check
                                                    else if (timeLeft < 1 ){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("RC Expired "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " RC had expired on \n"+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"RC")
                                                                .simple()
                                                                .build();
                                                    }



                                                } // try ended
                                                catch (StringIndexOutOfBoundsException e){
                                                    e.printStackTrace();
                                                } // catch ended

                                            } // rc if ended




                                            // calibration

                                            dateLeft = dataSnapshot.child("calibration_valid").getValue(String.class);
                                            truck_number = dataSnapshot.child("truck_number").getValue(String.class);
                                            if (!TextUtils.equals(dateLeft,"")){
                                                try{
                                                    dateLeft = TextUtils.substring(dateLeft, 0, 10);
                                                    long time = DayBetween(date, dateLeft, "dd_MM_yyyy");
                                                    int timeLeft = (int)(time);
                                                    conversion();
                                                    assert truck_number != null;

                                                    if (timeLeft > 1 && timeLeft < 8){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("Calibration will expire in "+timeLeft+" days "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " Calibration test will expire on "+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"CV")
                                                                .simple()
                                                                .build();
                                                    } // if week check
                                                    else if (timeLeft < 1 ){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("Calibration Expired "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " Calibration test had expired on "+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"CV")
                                                                .simple()
                                                                .build();
                                                    }



                                                } // try ended
                                                catch (StringIndexOutOfBoundsException e){
                                                    e.printStackTrace();
                                                } // catch ended

                                            } // calibration if ended



                                            // fitness test

                                            dateLeft = dataSnapshot.child("fit_valid").getValue(String.class);
                                            truck_number = dataSnapshot.child("truck_number").getValue(String.class);
                                            if (!TextUtils.equals(dateLeft,"")){
                                                try{
                                                    dateLeft = TextUtils.substring(dateLeft, 0, 10);
                                                    long time = DayBetween(date, dateLeft, "dd_MM_yyyy");
                                                    int timeLeft = (int)(time);
                                                    conversion();
                                                    assert truck_number != null;

                                                    if (timeLeft > 1 && timeLeft < 8){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("Fitness test will expire in "+timeLeft+" days "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " Fitness test will expire on "+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"FT")
                                                                .simple()
                                                                .build();
                                                    } // if week check
                                                    else if (timeLeft < 1 ){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("Fitness test Expired "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " Fitness test had expired on "+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"FT")
                                                                .simple()
                                                                .build();
                                                    }



                                                } // try ended
                                                catch (StringIndexOutOfBoundsException e){
                                                    e.printStackTrace();
                                                } // catch ended

                                            } // Fitness if ended




                                            // pollution test

                                            dateLeft = dataSnapshot.child("pollution_valid").getValue(String.class);
                                            truck_number = dataSnapshot.child("truck_number").getValue(String.class);
                                            if (!TextUtils.equals(dateLeft,"")){
                                                try{
                                                    dateLeft = TextUtils.substring(dateLeft, 0, 10);
                                                    long time = DayBetween(date, dateLeft, "dd_MM_yyyy");
                                                    int timeLeft = (int)(time);
                                                    conversion();
                                                    assert truck_number != null;

                                                    if (timeLeft > 1 && timeLeft < 8){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("Pollution test will expire in "+timeLeft+" days "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " Pollution test will expire on "+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"PT")
                                                                .simple()
                                                                .build();
                                                    } // if week check
                                                    else if (timeLeft < 1 ){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("Pollution test Expired "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " Pollution test had expired on "+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"PT")
                                                                .simple()
                                                                .build();
                                                    }



                                                } // try ended
                                                catch (StringIndexOutOfBoundsException e){
                                                    e.printStackTrace();
                                                } // catch ended

                                            } // Pollution if ended



                                            // insurance test

                                            dateLeft = dataSnapshot.child("insurance_valid").getValue(String.class);
                                            truck_number = dataSnapshot.child("truck_number").getValue(String.class);
                                            if (!TextUtils.equals(dateLeft,"")){
                                                try{
                                                    dateLeft = TextUtils.substring(dateLeft, 0, 10);
                                                    long time = DayBetween(date, dateLeft, "dd_MM_yyyy");
                                                    int timeLeft = (int)(time);
                                                    conversion();
                                                    assert truck_number != null;

                                                    if (timeLeft > 1 && timeLeft < 8){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("Insurance will expire in "+timeLeft+" days "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " Insurance will expire on "+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"IT")
                                                                .simple()
                                                                .build();
                                                    } // if week check
                                                    else if (timeLeft < 1 ){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("Insurance Expired "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " Insurance had expired on "+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"IT")
                                                                .simple()
                                                                .build();
                                                    }



                                                } // try ended
                                                catch (StringIndexOutOfBoundsException e){
                                                    e.printStackTrace();
                                                } // catch ended

                                            } // Insurance if ended



                                            // hydro test

                                            dateLeft = dataSnapshot.child("hydro_valid").getValue(String.class);
                                            truck_number = dataSnapshot.child("truck_number").getValue(String.class);
                                            if (!TextUtils.equals(dateLeft,"")){
                                                try{
                                                    dateLeft = TextUtils.substring(dateLeft, 0, 10);
                                                    long time = DayBetween(date, dateLeft, "dd_MM_yyyy");
                                                    int timeLeft = (int)(time);
                                                    conversion();
                                                    assert truck_number != null;

                                                    if (timeLeft > 1 && timeLeft < 8){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("Hydro will expire in "+timeLeft+" days "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " Hydro test will expire on "+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"HT")
                                                                .simple()
                                                                .build();
                                                    } // if week check
                                                    else if (timeLeft < 1 ){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("Hydro Test Expired "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " Hydro test had expired on "+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"HT")
                                                                .simple()
                                                                .build();
                                                    }



                                                } // try ended
                                                catch (StringIndexOutOfBoundsException e){
                                                    e.printStackTrace();
                                                } // catch ended

                                            } // Hydro if ended



                                            // Road test

                                            dateLeft = dataSnapshot.child("road_valid").getValue(String.class);
                                            truck_number = dataSnapshot.child("truck_number").getValue(String.class);
                                            if (!TextUtils.equals(dateLeft,"")){
                                                try{
                                                    dateLeft = TextUtils.substring(dateLeft, 0, 10);
                                                    long time = DayBetween(date, dateLeft, "dd_MM_yyyy");
                                                    int timeLeft = (int)(time);
                                                    conversion();
                                                    assert truck_number != null;

                                                    if (timeLeft > 1 && timeLeft < 8){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("Road will expire in "+timeLeft+" days "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " Road test will expire on "+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"RT")
                                                                .simple()
                                                                .build();
                                                    } // if week check
                                                    else if (timeLeft < 1 ){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("Road Test Expired "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " Road test had expired on "+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"RT")
                                                                .simple()
                                                                .build();
                                                    }



                                                } // try ended
                                                catch (StringIndexOutOfBoundsException e){
                                                    e.printStackTrace();
                                                } // catch ended

                                            } // road if ended



                                            // explosive test

                                            dateLeft = dataSnapshot.child("explosive_valid").getValue(String.class);
                                            truck_number = dataSnapshot.child("truck_number").getValue(String.class);
                                            if (!TextUtils.equals(dateLeft,"")){
                                                try{
                                                    dateLeft = TextUtils.substring(dateLeft, 0, 10);
                                                    long time = DayBetween(date, dateLeft, "dd_MM_yyyy");
                                                    int timeLeft = (int)(time);
                                                    conversion();
                                                    assert truck_number != null;

                                                    if (timeLeft > 1 && timeLeft < 8){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("Explosive will expire in "+timeLeft+" days "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " Explosive test will expire on "+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"ET")
                                                                .simple()
                                                                .build();
                                                    } // if week check
                                                    else if (timeLeft < 1 ){
                                                        PugNotification.with(context)
                                                                .load()
                                                                .title("Explosive Test Expired "+ "("+truck_number+")")
                                                                .sound(soundUri)
                                                                .smallIcon(R.drawable.ic_local_shipping_black)
                                                                .autoCancel(true)
                                                                .largeIcon(R.drawable.app_logo)
                                                                .flags(Notification.FLAG_AUTO_CANCEL)
                                                                .bigTextStyle("Truck Number " + truck_number
                                                                        + " Explosive test had expired on "+day+"-"+month+"-"+year
                                                                        + ", if renewed please update it")
                                                                .click(pIntent)
                                                                .tag(truck_number+"ET")
                                                                .simple()
                                                                .build();
                                                    }



                                                } // try ended
                                                catch (StringIndexOutOfBoundsException e){
                                                    e.printStackTrace();
                                                } // catch ended

                                            } // Explosive if ended


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                        /*  PugNotification.with(context)
                                .load()
                                .title(key)
                              //  .sound(soundUri)
                                .smallIcon(R.drawable.ic_local_shipping_black)
                                .autoCancel(true)
                                .largeIcon(R.drawable.app_logo)
                                .flags(Notification.FLAG_AUTO_CANCEL)
                              .bigTextStyle("Truck Number " + truck_number
                                        + " is allocated to you."
                                        +"Trip is expected to be start on " +start_date)
                                .click(pIntent)

                              //  .number(Integer.parseInt(key))
                                .identifier(i)
                                .simple()
                                .build();*/

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });










                    } // if connected ended
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });




            // Do something here
            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }


    public long DayBetween(String date1,String date2,String pattern)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        Date Date1 = null,Date2 = null;
        try{
            Date1 = sdf.parse(date1);
            Date2 = sdf.parse(date2);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return (Date2.getTime() - Date1.getTime())/(24*60*60*1000);
    }


    private void conversion() {

        try {
            day = TextUtils.substring(dateLeft, 0, 2);
            month = TextUtils.substring(dateLeft, 3, 5);
            year = TextUtils.substring(dateLeft, 6, 10);



        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) (getApplication()).getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}