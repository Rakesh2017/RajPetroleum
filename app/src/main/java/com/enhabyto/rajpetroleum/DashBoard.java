package com.enhabyto.rajpetroleum;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tapadoo.alerter.Alerter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import dmax.dialog.SpotsDialog;
import util.android.textviews.FontTextView;


public class DashBoard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ImageView nav_profileImageView;
    String user_designation, sub_admin_contact;

    FontTextView user_uid, user_name;
    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    DatabaseReference d_subProfile;
    ImageButton companyImage_btn;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    List<TripRecyclerInfo> list = new ArrayList<>();
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;

    String connected = "no";
    TextView noInternet;
    private Context context;


    String allTrips_permission, createDriver_permission, createTruck_permission, createPump_permission
            , fuelRate_permission, appDateStart, appDateEnd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);


        // product key implementation
        if (isNetworkAvailable()){
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
                                int timeLeft = (int)(diffDays);

                                if (timeLeft <= 0 ){
                                    new MaterialDialog.Builder(DashBoard.this)
                                            .title("Validity Expired")
                                            .content("Please Enter the 14 digit Product Key to continue using Application.\nThank you.")
                                            .positiveText("ProductKey")
                                            .positiveColor(getResources().getColor(R.color.lightGreen))
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                                    Intent intent = new Intent(DashBoard.this, AppActivation.class);
                                                    startActivity(intent);
                                                    DashBoard.this.finish();
                                                }
                                            })
                                            .negativeText("Exit")
                                            .negativeColor(getResources().getColor(R.color.lightRed))
                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                                    DashBoard.this.finish();
                                                }
                                            })
                                            .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                                    DashBoard.this.finish();
                                                }
                                            })
                                            .cancelable(false)
                                            .dividerColor(Color.GRAY)
                                            .show();



                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        }


        FirebaseDatabase.getInstance().getReference()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       // if (dataSnapshot.hasChild())
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });






        this.context = this;
        Intent alarm = new Intent(this.context, AlarmReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if(!alarmRunning) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 281234, alarm, PendingIntent.FLAG_CANCEL_CURRENT);

            Date dat  = new Date(); //initializes to now
            Calendar cal_alarm = Calendar.getInstance();
            Calendar cal_now = Calendar.getInstance();
            cal_now.setTime(dat);
            cal_alarm.setTime(dat);
            cal_alarm.set(Calendar.HOUR_OF_DAY, 13); //set the alarm time
            cal_alarm.set(Calendar.MINUTE, 30);
            cal_alarm.set(Calendar.SECOND, 0);
            if(cal_alarm.before(cal_now)){ //if its in the past increment
                cal_alarm.add(Calendar.DATE,1);
            }

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(),AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Admin Dashboard");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        InternetChecker();


        ImageButton logout_btn = findViewById(R.id.app_bar_dash_logoutButton);
        nav_profileImageView = navigationView.getHeaderView(0).findViewById(R.id.nav1_profile_image);
        user_uid = navigationView.getHeaderView(0).findViewById(R.id.nav1_uid);
        user_name = navigationView.getHeaderView(0).findViewById(R.id.nav1_name);
        companyImage_btn = navigationView.getHeaderView(0).findViewById(R.id.header_companyIconImageView);
        noInternet = findViewById(R.id.no_internet);


        SharedPreferences shared = getSharedPreferences("firstLog", MODE_PRIVATE);

        user_designation = (shared.getString("user_designation", ""));
        String profileName = (shared.getString("profileName", ""));
        user_name.setText(profileName);


        if (TextUtils.equals(user_designation, "admin")){
            Glide.with(DashBoard.this)
                    .load(R.drawable.admin_profile_pic)
                    .asBitmap()
                    .fitCenter()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(nav_profileImageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getApplication().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            nav_profileImageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String email = user.getEmail();
                user_uid.setText(email);
                d_subProfile = d_root.child("admin").child("profile");
                d_subProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        SharedPreferences dataSave = getSharedPreferences("firstLog", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = dataSave.edit();
                        editor.putString("profileName", name);
                        editor.apply();
                        user_name.setText(name);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });

            }


        }
        else {
            if (!DashBoard.this.isDestroyed()){
            Glide.with(this)
                    .load(R.drawable.sub_admin_profile_pic)
                    .asBitmap()
                    .fitCenter()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(nav_profileImageView) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getApplication().getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            nav_profileImageView.setImageDrawable(circularBitmapDrawable);
                        }
                    });}
            sub_admin_contact = (shared.getString("subAdmin_contact", ""));
            user_uid.setText(sub_admin_contact);

            d_subProfile = d_root.child("sub_admin_profiles").child(sub_admin_contact);
            d_subProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    SharedPreferences dataSave = getSharedPreferences("firstLog", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = dataSave.edit();
                    editor.putString("profileName", name);
                    editor.apply();
                    user_name.setText(name);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });

        }


        noInternet.setOnClickListener(this);
        companyImage_btn.setOnClickListener(this);
        logout_btn.setOnClickListener(this);

        recyclerView = findViewById(R.id.dash_recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(DashBoard.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(DashBoard.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Data...");

        // Showing progress dialog.
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("trip_status");


        // Adding Add Value Event Listener to databaseReference.
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if(list!=null) {
                        list.clear();  // v v v v important (eliminate duplication of data)
                    }


                    adapter = null;
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                        TripRecyclerInfo imageUploadInfo = postSnapshot.getValue(TripRecyclerInfo.class);

                        list.add(imageUploadInfo);
                    }

                    adapter = new RecyclerViewAdapter(DashBoard.this, list);
                    //   Collections.reverse(list);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                    noInternet.setVisibility(View.GONE);
                    if (adapter.getItemCount() == 0){
                        findViewById(R.id.dash_text).setVisibility(View.VISIBLE);
                        findViewById(R.id.shana_pandu).setVisibility(View.GONE);
                    }
                    else {
                        findViewById(R.id.dash_text).setVisibility(View.GONE);
                        findViewById(R.id.shana_pandu).setVisibility(View.VISIBLE);
                        FontTextView text = findViewById(R.id.shana_pandu);
                        text.setText("");
                        text.setText("ACTIVE TRIPS"+" ("+String.valueOf(adapter.getItemCount()+")"));
                       }


                    // Hiding the progress dialog.
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Hiding the progress dialog.
                    progressDialog.dismiss();
                    throw databaseError.toException();

                }
            });
        findViewById(R.id.dash_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Tada)
                        .duration(1000)
                        .playOn(findViewById(R.id.dash_text));
            }
        });

        }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {


            FragmentManager fragmentManager = this.getSupportFragmentManager();
            int s =fragmentManager.getBackStackEntryCount() - 1;
            if (s >= 0){
                super.onBackPressed();

            }
            else {
                super.onBackPressed();
                moveTaskToBack(true);
                finish();
            }

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // navigation drawer items
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        SharedPreferences dataSave = getSharedPreferences("permissions", MODE_PRIVATE);

        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            Intent intent = new Intent(DashBoard.this, DashBoard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
          //  overridePendingTransition(R.anim.);

            // Handle the camera action
        } else if (id == R.id.nav_CreateDriver) {

            if (TextUtils.equals(user_designation, "admin")){
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard,new CreateDriver()).addToBackStack("DriverFragment").commit();
            }
            else {


                createDriver_permission = dataSave.getString("driver_permission","");
                                if (TextUtils.equals(createDriver_permission, "granted")) {
                                    try{
                                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard,new CreateDriver()).addToBackStack("TruckFragments").commit();

                                    }
                                    catch (IllegalStateException e){
                                        e.printStackTrace();
                                    }



                                } else {
                                    Alerter.create(DashBoard.this)
                                            .setTitle("Access Denied!")
                                            .setText("You do not have permission to Create Driver Profile")
                                            .setContentGravity(1)
                                            .setBackgroundColorRes(R.color.black)
                                            .setIcon(R.drawable.error)
                                            .show();
                                }


                            }




        } else if (id == R.id.nav_CreateAdmin) {

            if (TextUtils.equals(user_designation, "admin")) {
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new CreateSubAdmin()).addToBackStack("AdminFragment").commit();

            }
            else {
                Alerter.create(DashBoard.this)
                        .setTitle("Access Denied!")
                        .setText("Only Admin can create sub-Admin")
                        .setContentGravity(1)
                        .setContentGravity(1)
                        .setBackgroundColorRes(R.color.black)
                        .setIcon(R.drawable.error)
                        .show();
            }


        } else if (id == R.id.nav_TruckDetails) {

            if (TextUtils.equals(user_designation, "admin")){
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard,new CreateTruck()).addToBackStack("TruckFragments").commit();
            }
            else {

                createTruck_permission = dataSave.getString("truck_permission","");

                        if (TextUtils.equals(createTruck_permission, "granted")) {
                            try{
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard,new CreateTruck()).addToBackStack("TruckFragments").commit();

                            }
                            catch (IllegalStateException | NullPointerException e){
                                e.printStackTrace();
                            }


                        } else {
                            Alerter.create(DashBoard.this)
                                    .setTitle("Access Denied!")
                                    .setText("You do not have permission to Create Truck")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.error)
                                    .show();
                        }



            }


        } else if (id == R.id.nav_pumpDetails) {

            if (TextUtils.equals(user_designation, "admin")){
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard,new CreatePump()).addToBackStack("PumpFragments").commit();
            }
            else {

                createPump_permission = dataSave.getString("pump_permission","");
                if (TextUtils.equals(createPump_permission, "granted")) {
                    try{
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard, new CreatePump()).addToBackStack("PumpFragments").commit();

                    }
                    catch (IllegalStateException | NullPointerException e){
                        e.printStackTrace();
                    }



                } else {
                    Alerter.create(DashBoard.this)
                            .setTitle("Access Denied!")
                            .setText("You do not have permission to Create Pump")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                }
            }



        } else if (id == R.id.nav_FuelRate) {

            if (TextUtils.equals(user_designation, "admin")){
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new FuelRate()).addToBackStack("AdminFragment").commit();
            }
            else {

                fuelRate_permission = dataSave.getString("fuel_rate_permission","");
                        if (TextUtils.equals(fuelRate_permission, "granted")) {
                            try{
                                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new FuelRate()).addToBackStack("AdminFragment").commit();

                            }
                            catch (IllegalStateException | NullPointerException e){
                                e.printStackTrace();
                            }



                        } else {
                            Alerter.create(DashBoard.this)
                                    .setTitle("Access Denied!")
                                    .setText("You do not have permission to update Fuel Price")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.error)
                                    .show();
                        }

            }





        }
        else if (id == R.id.nav_allocateTruck){


            if (TextUtils.equals(user_designation, "admin")){
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new AllocateTruck()).addToBackStack("AdminFragment").commit();
            }
            else {

                fuelRate_permission = dataSave.getString("schedule_trip_permission","");
                        if (TextUtils.equals(fuelRate_permission, "granted")) {
                            try{
                                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new AllocateTruck()).addToBackStack("AdminFragment").commit();

                            }
                            catch (IllegalStateException | NullPointerException e){
                                e.printStackTrace();
                            }



                        } else {
                            Alerter.create(DashBoard.this)
                                    .setTitle("Access Denied!")
                                    .setText("You do not have permission to Schedule trip")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.error)
                                    .show();
                        }



            }


        }

        else if (id == R.id.nav_AllTrips){


            if (TextUtils.equals(user_designation, "admin")){
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new AllTrips()).addToBackStack("AdminFragment").commit();
            }
            else {

                allTrips_permission = dataSave.getString("all_trips_permission","");

                        if (TextUtils.equals(allTrips_permission, "granted")) {
                            try{
                                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new AllTrips()).addToBackStack("AdminFragment").commit();

                            }
                            catch (IllegalStateException | NullPointerException e){
                                e.printStackTrace();
                            }

                        } else {
                            Alerter.create(DashBoard.this)
                                    .setTitle("Access Denied!")
                                    .setText("You do not have permission to check driver's trip details")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.error)
                                    .show();
                        }

            }




        }
        else if (id == R.id.nav_emergencyContact){
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new EmergencyContact()).addToBackStack("AdminFragment").commit();

        }

        else if (id == R.id.nav_scheduleTripDetails){
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container_DashBoard, new ScheduleList()).addToBackStack("AdminFragment").commit();

        }

        else if (id == R.id.nav_terms){
             Intent intent = new Intent(DashBoard.this, TermAndCondition.class);
             startActivity(intent);
        }

        else if (id == R.id.nav_activation){
            if (!isNetworkAvailable()){
                Alerter.create(DashBoard.this)
                        .setText("This needs active internet Connection!")
                        .setContentGravity(1)
                        .setBackgroundColorRes(R.color.black)
                        .setIcon(R.drawable.no_internet)
                        .show();
            }
            else {
                Intent intent = new Intent(DashBoard.this, AppActivation.class);
                startActivity(intent);
            }

        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){

            case R.id.header_companyIconImageView:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.enhabyto.com"));
                startActivity(browserIntent);
                break;

            case R.id.app_bar_dash_logoutButton:

                new MaterialDialog.Builder(this)
                        .title("Logout")
                        .content("Are You Sure to Logout?")
                        .positiveText("Yes")
                        .positiveColor(getResources().getColor(R.color.lightRed))
                        .negativeText("No")
                        .negativeColor(getResources().getColor(R.color.lightGreen))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                    final AlertDialog dialog1 = new SpotsDialog(DashBoard.this,R.style.LoggingOut);
                    dialog1.show();


                    try {
                        if(!isNetworkAvailable()){
                            Alerter.create(DashBoard.this)
                                    .setTitle("No Internet Connection!")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.no_internet)
                                    .show();
                            dialog1.dismiss();
                            return;
                        }
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();


                    } catch (IllegalStateException | NullPointerException e) {
                        Alerter.create(DashBoard.this)
                                .setTitle("Server Error Try Again!")
                                .setContentGravity(1)
                                .setBackgroundColorRes(R.color.black)
                                .setIcon(R.drawable.no_internet)
                                .show();
                                         }

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            SharedPreferences dataSave = getSharedPreferences("firstLog", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = dataSave.edit();
                            editor.putString("LaunchApplication", "Login");
                            editor.apply();


                            SharedPreferences.Editor editor1;
                            SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
                            editor1 = sharedPreferences.edit();
                            editor1.putString("A","").apply();
                            dialog1.dismiss();
                            moveTaskToBack(true);
                            finish();

                        }

                    }, 2000);


                    // TODO
                }
            })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // TODO
                            dialog.dismiss();
                            }
                        }) .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                        dialog.dismiss();
                    }
                })
                        .show();

                break;


            case R.id.no_internet:
                if (!isNetworkAvailable()) {

                    YoYo.with(Techniques.Shake)
                            .duration(500)
                            .repeat(0)
                            .playOn(noInternet);

                }
                else {
                    noInternet.setVisibility(View.GONE);
                    DashBoard.this.recreate();
                }

                break;

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) (getApplication()).getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting() && activeNetworkInfo.isAvailable();
    }

    //internet checker
    public void InternetChecker(){
        DatabaseReference d_networkStatus = FirebaseDatabase.getInstance().getReference().child("checkNetwork").child("isConnected");

        d_networkStatus.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                connected = dataSnapshot.getValue(String.class);
                noInternet.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!connected.equals("connected")){
                    progressDialog.dismiss();
                    noInternet.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(3000)
                            .repeat(0)
                            .playOn(noInternet);

                }
            }
        },15000);
    }





    public void onStart() {
        super.onStart();


        SharedPreferences dataSave = getSharedPreferences("permissions", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = dataSave.edit();

        SharedPreferences shared = getSharedPreferences("firstLog", MODE_PRIVATE);
        user_designation = (shared.getString("user_designation", ""));


        if (TextUtils.equals(user_designation, "subAdmin")) {

            d_subProfile = d_root.child("sub_admin_profiles").child(sub_admin_contact).child("permissions");

//        driver permission
            d_subProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String permission = dataSnapshot.child("driver_permission").getValue(String.class);
                    editor.putString("driver_permission", permission);
                    editor.apply();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });


            //        all trips
            d_subProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String permission = dataSnapshot.child("all_trips_permission").getValue(String.class);
                    editor.putString("all_trips_permission", permission);
                    editor.apply();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });


            //        schedule trip
            d_subProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String permission = dataSnapshot.child("schedule_trip_permission").getValue(String.class);
                    editor.putString("schedule_trip_permission", permission);
                    editor.apply();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });


            //       create truck
            d_subProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String permission = dataSnapshot.child("truck_permission").getValue(String.class);
                    editor.putString("truck_permission", permission);
                    editor.apply();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });


            //       pump
            d_subProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String permission = dataSnapshot.child("pump_permission").getValue(String.class);
                    editor.putString("pump_permission", permission);
                    editor.apply();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });


            //       fuel rate
            d_subProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String permission = dataSnapshot.child("fuel_rate_permission").getValue(String.class);
                    editor.putString("fuel_rate_permission", permission);
                    editor.apply();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    throw databaseError.toException();
                }
            });
        }


    }

    public void onDestroy(){
        super.onDestroy();
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    //end
}
