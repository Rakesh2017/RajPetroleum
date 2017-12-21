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
public class OtherFillingDetail extends Fragment {

    View view;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    List<OtherRecyclerInfo> list = new ArrayList<>();
    ProgressDialog progressDialog, excelDialog;
    DatabaseReference databaseReference;

    private String contactUID_tx, userNameUID;
    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    String key;
    ImageButton excel_export;



    public OtherFillingDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_other_filling_detail, container, false);

        SharedPreferences shared = getActivity().getSharedPreferences("driverContact", Context.MODE_PRIVATE);
        try{
            contactUID_tx = (shared.getString("contactUID", ""));
            key = (shared.getString("TripSuperKey", ""));
        }
        catch (NullPointerException e){
            contactUID_tx  = "";
        }

        recyclerView = view.findViewById(R.id.otherFilling_recyclerView);
        excel_export = view.findViewById(R.id.other_excel);
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
                        .child(key).child("other_filling");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(list!=null) {
                            list.clear();  // v v v v important (eliminate duplication of data)
                        }


                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                            OtherRecyclerInfo otherFillingRecyclerInfo = postSnapshot.getValue(OtherRecyclerInfo.class);

                            list.add(otherFillingRecyclerInfo);

                        }

                        adapter = new OtherRecyclerViewAdapter(getActivity(), list);


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





//                other filling

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
                        .child(key).child("other_filling");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {


                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
                        SimpleDateFormat sdf1 = new SimpleDateFormat("HH_mm");
                        String actualDate = sdf.format(c.getTime());
                        String actualTime = sdf1.format(c.getTime());

                        DBHelper dbHelper2 = new DBHelper(getActivity());
                        dbHelper2.dropOtherTable();


                        SharedPreferences dataSave = getActivity().getSharedPreferences("excel_data", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = dataSave.edit();
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                            String date = TextUtils.substring(postSnapshot.child("date_time").getValue(String.class),0,10);
                            String time = TextUtils.substring(postSnapshot.child("date_time").getValue(String.class),11,16);


                            editor.putString("filling_name", postSnapshot.child("filling_name").getValue(String.class));
                            editor.putString("quantity", postSnapshot.child("quantity").getValue(String.class));
                            editor.putString("description", postSnapshot.child("description").getValue(String.class));
                            editor.putString("money_paid", postSnapshot.child("money_paid").getValue(String.class));
                            editor.putString("gps_location", postSnapshot.child("gps_location").getValue(String.class));
                            editor.putString("date", date);
                            editor.putString("time", time);
                            editor.apply();

                            DBHelper dbHelper = new DBHelper(getActivity());
                            dbHelper.insertOtherData();

                            final Cursor cursor = dbHelper.getuser();


                            File sd = Environment.getExternalStorageDirectory();
                            String csvFile = userNameUID+" Other Filling "+actualDate+"_"+actualTime+".xls";

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
                                WritableSheet sheet = workbook.createSheet("Other Filling Details", 0);

                                sheet.addCell(new Label(0, 0, "Filling"));
                                sheet.addCell(new Label(1, 0, "Quantity Filled"));
                                sheet.addCell(new Label(2, 0, "Amount Paid"));
                                sheet.addCell(new Label(3, 0, "Description"));
                                sheet.addCell(new Label(4, 0, "Date"));
                                sheet.addCell(new Label(5, 0, "Time"));
                                sheet.addCell(new Label(6, 0, "GPS Location"));


                                if (cursor.moveToFirst()) {
                                    do {
                                        String fillingName = cursor.getString(cursor.getColumnIndex("filling_name"));
                                        String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                                        String amountPaid = cursor.getString(cursor.getColumnIndex("money_paid"));
                                        String description = cursor.getString(cursor.getColumnIndex("description"));
                                        String date1 = cursor.getString(cursor.getColumnIndex("date"));
                                        String time1 = cursor.getString(cursor.getColumnIndex("time"));
                                        String gpsLocation = cursor.getString(cursor.getColumnIndex("gps_location"));


                                        int i = cursor.getPosition() + 1;


                                        sheet.addCell(new Label(6, i, gpsLocation));
                                        sheet.addCell(new Label(5, i, time1));
                                        sheet.addCell(new Label(4, i, date1));
                                        sheet.addCell(new Label(3, i, description));
                                        sheet.addCell(new Label(2, i, amountPaid));
                                        sheet.addCell(new Label(1, i, quantity));
                                        sheet.addCell(new Label(0, i, fillingName));

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
