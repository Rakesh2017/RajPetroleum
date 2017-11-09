package com.enhabyto.rajpetroleum;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.util.Log;
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
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.tapadoo.alerter.Alerter;

import java.util.List;

import dmax.dialog.SpotsDialog;
import util.android.textviews.FontTextView;

import static android.app.FragmentTransaction.TRANSIT_ENTER_MASK;

public class DashBoard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ImageView nav_profileImageView;
    String tag123;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
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



        ImageButton logout_btn = findViewById(R.id.app_bar_dash_logoutButton);
        nav_profileImageView = navigationView.getHeaderView(0).findViewById(R.id.nav1_profile_image);



        Glide.with(getApplication().getApplicationContext())
                .load(R.drawable.profile_image)
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


        logout_btn.setOnClickListener(this);


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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            Intent intent = new Intent(DashBoard.this, DashBoard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
          //  overridePendingTransition(R.anim.);

            // Handle the camera action
        } else if (id == R.id.nav_CreateDriver) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard,new CreateDriver()).addToBackStack("DriverFragment").commit();


        } else if (id == R.id.nav_CreateAdmin) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard,new CreateSubAdmin()).addToBackStack("AdminFragment").commit();


        } else if (id == R.id.nav_TruckDetails) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard,new CreateTruck()).addToBackStack("TruckFragments").commit();


        } else if (id == R.id.nav_pumpDetails) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard,new CreatePump()).addToBackStack("PumpFragments").commit();


        } else if (id == R.id.nav_FuelRate) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){

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
                    SharedPreferences dataSave = getSharedPreferences("firstLog", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = dataSave.edit();
                    editor.putString("LaunchApplication", "Login");
                    editor.commit();



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


                    } catch (IllegalStateException e) {
                        Alerter.create(DashBoard.this)
                                .setTitle("Server Error Try Again!")
                                .setContentGravity(1)
                                .setBackgroundColorRes(R.color.black)
                                .setIcon(R.drawable.no_internet)
                                .show();
                                         }

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            SharedPreferences.Editor editor1;
                            SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
                            editor1 = sharedPreferences.edit();
                            editor1.putString("A","").commit();
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

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) (getApplication()).getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //end
}
