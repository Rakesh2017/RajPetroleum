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
public class BreakDownDetail extends Fragment {

    View view;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    List<BrokenRecyclerInfo> list = new ArrayList<>();
    ProgressDialog progressDialog, excelDialog;
    DatabaseReference databaseReference;

    private String contactUID_tx, userNameUID;
    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    String key;

    ImageButton excel_export;



    public BreakDownDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_break_down_detail, container, false);


        SharedPreferences shared = getActivity().getSharedPreferences("driverContact", Context.MODE_PRIVATE);
        try{
            contactUID_tx = (shared.getString("contactUID", ""));
            key = (shared.getString("TripSuperKey", ""));
        }
        catch (NullPointerException e){
            contactUID_tx  = "";
        }


        recyclerView = view.findViewById(R.id.break_recyclerView);
        excel_export = view.findViewById(R.id.break_excel);
        recyclerView.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(getContext());

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Data...");

        excelDialog = new ProgressDialog(getContext());
        excelDialog.setMessage("Exporting Data into Excel...");

        // Showing progress dialog.
        progressDialog.show();



                databaseReference = d_root.child("trip_details").child(contactUID_tx)
                        .child(key).child("failure");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(list!=null) {
                            list.clear();  // v v v v important (eliminate duplication of data)
                        }


                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                            BrokenRecyclerInfo breakDownRecyclerInfo = postSnapshot.getValue(BrokenRecyclerInfo.class);

                            list.add(breakDownRecyclerInfo);
                        }

                        adapter = new BrokenRecyclerViewAdapter(getActivity(), list);
                        //   Collections.reverse(list);
                        adapter.notifyDataSetChanged();
                        recyclerView.setAdapter(adapter);

                        // Hiding the progress dialog.
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        // Hiding the progress dialog.
                        progressDialog.dismiss();

                    }
                });


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


        excel_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excelDialog.show();

                databaseReference = d_root.child("trip_details").child(contactUID_tx)
                        .child(key).child("failure");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {


                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
                        SimpleDateFormat sdf1 = new SimpleDateFormat("HH_mm");
                        String actualDate = sdf.format(c.getTime());
                        String actualTime = sdf1.format(c.getTime());

                        DBHelper dbHelper2 = new DBHelper(getActivity());
                        dbHelper2.dropBreakTable();


                        SharedPreferences dataSave = getActivity().getSharedPreferences("excel_data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = dataSave.edit();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                            String date = TextUtils.substring(postSnapshot.child("date_time").getValue(String.class),0,10);
                            String time = TextUtils.substring(postSnapshot.child("date_time").getValue(String.class),11,16);


                            editor.putString("failure_name", postSnapshot.child("failure_name").getValue(String.class));
                            editor.putString("address", postSnapshot.child("address").getValue(String.class));
                            editor.putString("state_name", postSnapshot.child("state_name").getValue(String.class));

                            editor.putString("resource_name1", postSnapshot.child("resource_name1").getValue(String.class));
                            editor.putString("resource_price1", postSnapshot.child("resource_price1").getValue(String.class));
                            editor.putString("resource_quantity1", postSnapshot.child("resource_quantity1").getValue(String.class));

                            editor.putString("resource_name2", postSnapshot.child("resource_name2").getValue(String.class));
                            editor.putString("resource_price2", postSnapshot.child("resource_price2").getValue(String.class));
                            editor.putString("resource_quantity2", postSnapshot.child("resource_quantity2").getValue(String.class));

                            editor.putString("resource_name3", postSnapshot.child("resource_name3").getValue(String.class));
                            editor.putString("resource_price3", postSnapshot.child("resource_price3").getValue(String.class));
                            editor.putString("resource_quantity3", postSnapshot.child("resource_quantity3").getValue(String.class));

                            editor.putString("resource_name4", postSnapshot.child("resource_name4").getValue(String.class));
                            editor.putString("resource_price4", postSnapshot.child("resource_price4").getValue(String.class));
                            editor.putString("resource_quantity4", postSnapshot.child("resource_quantity4").getValue(String.class));

                            editor.putString("resource_name5", postSnapshot.child("resource_name5").getValue(String.class));
                            editor.putString("resource_price5", postSnapshot.child("resource_price5").getValue(String.class));
                            editor.putString("resource_quantity5", postSnapshot.child("resource_quantity5").getValue(String.class));

                            editor.putString("bill_paid1", postSnapshot.child("bill_paid1").getValue(String.class));
                            editor.putString("bill_paid2", postSnapshot.child("bill_paid2").getValue(String.class));
                            editor.putString("bill_paid3", postSnapshot.child("bill_paid3").getValue(String.class));

                            editor.putString("gps_location", postSnapshot.child("gps_location").getValue(String.class));
                            editor.putString("date", date);
                            editor.putString("time", time);
                            editor.apply();

                            DBHelper dbHelper = new DBHelper(getActivity());
                            dbHelper.insertBreakData();

                            final Cursor cursor = dbHelper.getuser();


                            File sd = Environment.getExternalStorageDirectory();
                            String csvFile = userNameUID+" Breakdown "+actualDate+"_"+actualTime+".xls";

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
                                WritableSheet sheet = workbook.createSheet("BreakDown Details", 0);

                                sheet.addCell(new Label(0, 0, "Failure Name"));
                                sheet.addCell(new Label(1, 0, "Address"));
                                sheet.addCell(new Label(2, 0, "State Name"));

                                sheet.addCell(new Label(3, 0, "Resource Used1"));
                                sheet.addCell(new Label(4, 0, "Resource Price1"));
                                sheet.addCell(new Label(5, 0, "Resource Quantity1"));

                                sheet.addCell(new Label(6, 0, "Resource Used2"));
                                sheet.addCell(new Label(7, 0, "Resource Price2"));
                                sheet.addCell(new Label(8, 0, "Resource Quantity2"));

                                sheet.addCell(new Label(9, 0, "Resource Used3"));
                                sheet.addCell(new Label(10, 0, "Resource Price3"));
                                sheet.addCell(new Label(11, 0, "Resource Quantity3"));

                                sheet.addCell(new Label(12, 0, "Resource Used4"));
                                sheet.addCell(new Label(13, 0, "Resource Price4"));
                                sheet.addCell(new Label(14, 0, "Resource Quantity4"));

                                sheet.addCell(new Label(15, 0, "Resource Used5"));
                                sheet.addCell(new Label(16, 0, "Resource Price5"));
                                sheet.addCell(new Label(17, 0, "Resource Quantity5"));

                                sheet.addCell(new Label(18, 0, "Bill Paid1"));
                                sheet.addCell(new Label(19, 0, "Bill Paid2"));
                                sheet.addCell(new Label(20, 0, "Bill Paid3"));


                                sheet.addCell(new Label(21, 0, "Date"));
                                sheet.addCell(new Label(22, 0, "Time"));
                                sheet.addCell(new Label(23, 0, "GPS Location"));


                                if (cursor.moveToFirst()) {
                                    do {
                                        String failureName = cursor.getString(cursor.getColumnIndex("failure_name"));
                                        String address = cursor.getString(cursor.getColumnIndex("address"));
                                        String stateName = cursor.getString(cursor.getColumnIndex("state_name"));

                                        String resourceUser1 = cursor.getString(cursor.getColumnIndex("resource_name1"));
                                        String resourcePrice1 = cursor.getString(cursor.getColumnIndex("resource_price1"));
                                        String resourceQuantity1 = cursor.getString(cursor.getColumnIndex("resource_quantity1"));


                                        String resourceUser2 = cursor.getString(cursor.getColumnIndex("resource_name2"));
                                        String resourcePrice2 = cursor.getString(cursor.getColumnIndex("resource_price2"));
                                        String resourceQuantity2 = cursor.getString(cursor.getColumnIndex("resource_quantity2"));


                                        String resourceUser3 = cursor.getString(cursor.getColumnIndex("resource_name3"));
                                        String resourcePrice3 = cursor.getString(cursor.getColumnIndex("resource_price3"));
                                        String resourceQuantity3 = cursor.getString(cursor.getColumnIndex("resource_quantity3"));


                                        String resourceUser4 = cursor.getString(cursor.getColumnIndex("resource_name4"));
                                        String resourcePrice4 = cursor.getString(cursor.getColumnIndex("resource_price4"));
                                        String resourceQuantity4 = cursor.getString(cursor.getColumnIndex("resource_quantity4"));


                                        String resourceUser5 = cursor.getString(cursor.getColumnIndex("resource_name5"));
                                        String resourcePrice5 = cursor.getString(cursor.getColumnIndex("resource_price5"));
                                        String resourceQuantity5 = cursor.getString(cursor.getColumnIndex("resource_quantity5"));

                                        String billPaid1 = cursor.getString(cursor.getColumnIndex("bill_paid1"));
                                        String billPaid2 = cursor.getString(cursor.getColumnIndex("bill_paid2"));
                                        String billPaid3 = cursor.getString(cursor.getColumnIndex("bill_paid3"));


                                        String date1 = cursor.getString(cursor.getColumnIndex("date"));
                                        String time1 = cursor.getString(cursor.getColumnIndex("time"));
                                        String gpsLocation = cursor.getString(cursor.getColumnIndex("gps_location"));


                                        int i = cursor.getPosition() + 1;


                                        sheet.addCell(new Label(23, i, gpsLocation));
                                        sheet.addCell(new Label(22, i, time1));
                                        sheet.addCell(new Label(21, i, date1));

                                        sheet.addCell(new Label(20, i, billPaid3));
                                        sheet.addCell(new Label(19, i, billPaid2));
                                        sheet.addCell(new Label(18, i, billPaid1));

                                        sheet.addCell(new Label(17, i, resourceQuantity5));
                                        sheet.addCell(new Label(16, i, resourcePrice5));
                                        sheet.addCell(new Label(15, i, resourceUser5));

                                        sheet.addCell(new Label(14, i, resourceQuantity4));
                                        sheet.addCell(new Label(13, i, resourcePrice4));
                                        sheet.addCell(new Label(12, i, resourceUser4));

                                        sheet.addCell(new Label(11, i, resourceQuantity3));
                                        sheet.addCell(new Label(10, i, resourcePrice3));
                                        sheet.addCell(new Label(9, i, resourceUser3));

                                        sheet.addCell(new Label(8, i, resourceQuantity2));
                                        sheet.addCell(new Label(7, i, resourcePrice2));
                                        sheet.addCell(new Label(6, i, resourceUser2));

                                        sheet.addCell(new Label(5, i, resourceQuantity1));
                                        sheet.addCell(new Label(4, i, resourcePrice1));
                                        sheet.addCell(new Label(3, i, resourceUser1));

                                        sheet.addCell(new Label(2, i, stateName));
                                        sheet.addCell(new Label(1, i, address));
                                        sheet.addCell(new Label(0, i, failureName));

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