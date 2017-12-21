package com.enhabyto.rajpetroleum;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoadDetail extends Fragment {

    View view;


    // Creating RecyclerView.
    RecyclerView recyclerView, recyclerView1;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    RecyclerView.Adapter adapter1 ;
    List<LoadRecyclerInfo> list = new ArrayList<>();
    List<LoadRecyclerInfo> list1 = new ArrayList<>();
    ProgressDialog progressDialog, excelDialog;
    DatabaseReference databaseReference,databaseReference1;

    ImageButton excel_export, excel_export1;



    private String contactUID_tx,userNameUID;
    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    String key;



    public LoadDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_load_detail, container, false);

        SharedPreferences shared = getActivity().getSharedPreferences("driverContact", Context.MODE_PRIVATE);
        try{
            contactUID_tx = (shared.getString("contactUID", ""));
            key = (shared.getString("TripSuperKey", ""));
        }
        catch (NullPointerException e){
            contactUID_tx  = "";
        }

        recyclerView = view.findViewById(R.id.load_recyclerView);
        recyclerView1 = view.findViewById(R.id.unLoad_recyclerView);
        excel_export = view.findViewById(R.id.load_excel);
        excel_export1 = view.findViewById(R.id.unload_excel);

        recyclerView.setHasFixedSize(true);
        recyclerView1.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();
        recyclerView1.isDuplicateParentStateEnabled();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager1.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mLayoutManager1.setStackFromEnd(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView1.setLayoutManager(mLayoutManager1);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(getContext());
        excelDialog = new ProgressDialog(getContext());
        excelDialog.setMessage("Exporting Data into Excel...");
        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Data...");

        // Showing progress dialog.
        progressDialog.show();




                databaseReference = d_root.child("trip_details").child(contactUID_tx)
                        .child(key).child("load");

                databaseReference1 = d_root.child("trip_details").child(contactUID_tx)
                        .child(key).child("unload");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(list!=null) {
                            list.clear();  // v v v v important (eliminate duplication of data)
                        }


                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                            LoadRecyclerInfo loadRecyclerInfo = postSnapshot.getValue(LoadRecyclerInfo.class);

                            list.add(loadRecyclerInfo);
                        }

                        adapter = new LoadRecyclerViewAdapter(getActivity(), list);
                        //   Collections.reverse(list);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);

                        // Hiding the progress dialog.

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        // Hiding the progress dialog.
                        progressDialog.dismiss();

                    }
                });


                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(list1!=null) {
                            list1.clear();  // v v v v important (eliminate duplication of data)
                        }

                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                            LoadRecyclerInfo loadRecyclerInfo = postSnapshot.getValue(LoadRecyclerInfo.class);

                            list1.add(loadRecyclerInfo);
                        }

                        adapter1 = new UnLoadRecyclerViewAdapter(getActivity(), list1);
                        //   Collections.reverse(list);
                        adapter1.notifyDataSetChanged();
                        recyclerView1.setAdapter(adapter1);

                        // Hiding the progress dialog.
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        // Hiding the progress dialog.
                        progressDialog.dismiss();

                    }
                });




                //              load

        d_root.child("driver_profiles").child(contactUID_tx).child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        userNameUID = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




        // export excel
        excel_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excelDialog.show();

                databaseReference = d_root.child("trip_details").child(contactUID_tx)
                        .child(key).child("load");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {


                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
                        SimpleDateFormat sdf1 = new SimpleDateFormat("HH_mm");
                        String actualDate = sdf.format(c.getTime());
                        String actualTime = sdf1.format(c.getTime());

                        DBHelper dbHelper2 = new DBHelper(getActivity());
                        dbHelper2.dropLoadTable();


                        SharedPreferences dataSave = getActivity().getSharedPreferences("excel_data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = dataSave.edit();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                            String date = TextUtils.substring(postSnapshot.child("date_time").getValue(String.class),0,10);
                            String time = TextUtils.substring(postSnapshot.child("date_time").getValue(String.class),11,16);


                            editor.putString("oil_loaded", postSnapshot.child("oil_loaded").getValue(String.class));
                            editor.putString("location", postSnapshot.child("location").getValue(String.class));
                            editor.putString("state_name", postSnapshot.child("state_name").getValue(String.class));
                            editor.putString("next_stoppage", postSnapshot.child("next_stoppage").getValue(String.class));
                            editor.putString("gps_location", postSnapshot.child("gps_location").getValue(String.class));
                            editor.putString("date", date);
                            editor.putString("time", time);
                            editor.apply();

                            DBHelper dbHelper = new DBHelper(getActivity());
                            dbHelper.insertLoadData();

                            final Cursor cursor = dbHelper.getuser();


                            File sd = Environment.getExternalStorageDirectory();
                            String csvFile = userNameUID+" load "+actualDate+"_"+actualTime+".xls";

                            File directory = new File(sd.getAbsolutePath());
                            //create directory if not exist
                            if (!directory.isDirectory()) {
                                directory.mkdirs();
                            }

                            try {

                                //file path
                                File file = new File(directory, csvFile);
                                WorkbookSettings wbSettings = new WorkbookSettings();
                                wbSettings.setLocale(new Locale("en", "EN"));
                                WritableWorkbook workbook;
                                workbook = Workbook.createWorkbook(file, wbSettings);

                                //Excel sheet name. 0 represents first sheet
                                WritableSheet sheet = workbook.createSheet("Oil Loading Details", 0);

                                sheet.addCell(new Label(0, 0, "Oil Loaded"));
                                sheet.addCell(new Label(1, 0, "Location"));
                                sheet.addCell(new Label(2, 0, "State"));
                                sheet.addCell(new Label(3, 0, "Next Stoppage"));
                                sheet.addCell(new Label(4, 0, "Date"));
                                sheet.addCell(new Label(5, 0, "Time"));
                                sheet.addCell(new Label(6, 0, "GPS Location"));


                                if (cursor.moveToFirst()) {
                                    do {
                                        String oilLoaded = cursor.getString(cursor.getColumnIndex("oil_loaded"));
                                        String location = cursor.getString(cursor.getColumnIndex("location"));
                                        String stateName = cursor.getString(cursor.getColumnIndex("state_name"));
                                        String nextStoppage = cursor.getString(cursor.getColumnIndex("next_stoppage"));
                                        String date1 = cursor.getString(cursor.getColumnIndex("date"));
                                        String time1 = cursor.getString(cursor.getColumnIndex("time"));
                                        String gpsLocation = cursor.getString(cursor.getColumnIndex("gps_location"));


                                        int i = cursor.getPosition() + 1;


                                        sheet.addCell(new Label(6, i, gpsLocation));
                                        sheet.addCell(new Label(5, i, time1));
                                        sheet.addCell(new Label(4, i, date1));
                                        sheet.addCell(new Label(3, i, nextStoppage));
                                        sheet.addCell(new Label(2, i, stateName));
                                        sheet.addCell(new Label(1, i, location));
                                        sheet.addCell(new Label(0, i, oilLoaded));

                                    } while (cursor.moveToNext());
                                }
                                //closing cursor
                                cursor.close();
                                workbook.write();
                                workbook.close();



                            } catch(IOException | WriteException e){
                                e.printStackTrace();
                            }



                        }

                        Toast.makeText(getActivity(),
                                "Data Exported Successfully in a Excel Sheet", Toast.LENGTH_SHORT).show();
                        excelDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {


                    }
                });



            }
        });




        // export excel
        excel_export1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excelDialog.show();

                databaseReference = d_root.child("trip_details").child(contactUID_tx)
                        .child(key).child("unload");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {


                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
                        SimpleDateFormat sdf1 = new SimpleDateFormat("HH_mm");
                        String actualDate = sdf.format(c.getTime());
                        String actualTime = sdf1.format(c.getTime());

                        DBHelper dbHelper2 = new DBHelper(getActivity());
                        dbHelper2.dropunLoadTable();


                        SharedPreferences dataSave = getActivity().getSharedPreferences("excel_data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = dataSave.edit();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                            String date = TextUtils.substring(postSnapshot.child("date_time").getValue(String.class),0,10);
                            String time = TextUtils.substring(postSnapshot.child("date_time").getValue(String.class),11,16);


                            editor.putString("oil_unloaded", postSnapshot.child("oil_unloaded").getValue(String.class));
                            editor.putString("pump_name", postSnapshot.child("pump_name").getValue(String.class));
                            editor.putString("location", postSnapshot.child("location").getValue(String.class));
                            editor.putString("state_name", postSnapshot.child("state_name").getValue(String.class));
                            editor.putString("oil_left", postSnapshot.child("oil_left").getValue(String.class));
                            editor.putString("next_stoppage", postSnapshot.child("next_stoppage").getValue(String.class));
                            editor.putString("gps_location", postSnapshot.child("gps_location").getValue(String.class));
                            editor.putString("date", date);
                            editor.putString("time", time);
                            editor.apply();

                            DBHelper dbHelper = new DBHelper(getActivity());
                            dbHelper.insertunLoadData();

                            final Cursor cursor = dbHelper.getuser();


                            File sd = Environment.getExternalStorageDirectory();
                            String csvFile = userNameUID+" unload "+actualDate+"_"+actualTime+".xls";

                            File directory = new File(sd.getAbsolutePath());
                            //create directory if not exist
                            if (!directory.isDirectory()) {
                                directory.mkdirs();
                            }

                            try {

                                //file path
                                File file = new File(directory, csvFile);
                                WorkbookSettings wbSettings = new WorkbookSettings();
                                wbSettings.setLocale(new Locale("en", "EN"));
                                WritableWorkbook workbook;
                                workbook = Workbook.createWorkbook(file, wbSettings);

                                //Excel sheet name. 0 represents first sheet
                                WritableSheet sheet = workbook.createSheet("Oil Unloading Details", 0);

                                sheet.addCell(new Label(0, 0, "Oil unLoaded"));
                                sheet.addCell(new Label(1, 0, "Pump Name"));
                                sheet.addCell(new Label(2, 0, "Location"));
                                sheet.addCell(new Label(3, 0, "State"));
                                sheet.addCell(new Label(4, 0, "Oil Left"));
                                sheet.addCell(new Label(5, 0, "Next Stoppage"));
                                sheet.addCell(new Label(6, 0, "Date"));
                                sheet.addCell(new Label(7, 0, "Time"));
                                sheet.addCell(new Label(8, 0, "GPS Location"));


                                if (cursor.moveToFirst()) {
                                    do {
                                        String oilunLoaded = cursor.getString(cursor.getColumnIndex("oil_unloaded"));
                                        String pumpName = cursor.getString(cursor.getColumnIndex("pump_name"));
                                        String location = cursor.getString(cursor.getColumnIndex("location"));
                                        String stateName = cursor.getString(cursor.getColumnIndex("state_name"));
                                        String oilLeft = cursor.getString(cursor.getColumnIndex("oil_left"));
                                        String nextStoppage = cursor.getString(cursor.getColumnIndex("next_stoppage"));
                                        String date1 = cursor.getString(cursor.getColumnIndex("date"));
                                        String time1 = cursor.getString(cursor.getColumnIndex("time"));
                                        String gpsLocation = cursor.getString(cursor.getColumnIndex("gps_location"));


                                        int i = cursor.getPosition() + 1;


                                        sheet.addCell(new Label(8, i, gpsLocation));
                                        sheet.addCell(new Label(7, i, time1));
                                        sheet.addCell(new Label(6, i, date1));
                                        sheet.addCell(new Label(5, i, nextStoppage));
                                        sheet.addCell(new Label(4, i, oilLeft));
                                        sheet.addCell(new Label(3, i, stateName));
                                        sheet.addCell(new Label(2, i, location));
                                        sheet.addCell(new Label(1, i, pumpName));
                                        sheet.addCell(new Label(0, i, oilunLoaded));

                                    } while (cursor.moveToNext());
                                }
                                //closing cursor
                                cursor.close();
                                workbook.write();
                                workbook.close();



                            } catch(IOException | WriteException e){
                                e.printStackTrace();
                            }



                        }

                        Toast.makeText(getActivity(),
                                "Data Exported Successfully in a Excel Sheet", Toast.LENGTH_SHORT).show();
                        excelDialog.dismiss();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {


                    }
                });



            }
        });




        return view;
    }

}
