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
public class PetrolFillingDetail extends Fragment {

    View view;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerView.Adapter adapter ;
    List<PetrolFillingRecyclerInfo> list = new ArrayList<>();
    ProgressDialog progressDialog, excelDialog;
    DatabaseReference databaseReference;

    private String contactUID_tx;
    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    String key, userNameUID;

    ImageButton excel_export;



    public PetrolFillingDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_petrol_filling_detail, container, false);

        SharedPreferences shared = getActivity().getSharedPreferences("driverContact", Context.MODE_PRIVATE);
        try{
            contactUID_tx = (shared.getString("contactUID", ""));
            key = (shared.getString("TripSuperKey", ""));
        }
        catch (NullPointerException e){
            contactUID_tx  = "";
        }

        excel_export = view.findViewById(R.id.petrol_excel);

        recyclerView = view.findViewById(R.id.petrolFilling_recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.isDuplicateParentStateEnabled();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(mLayoutManager);

        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(getContext());
        excelDialog = new ProgressDialog(getContext());

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Data...");
        excelDialog.setMessage("Exporting Data into Excel...");

        // Showing progress dialog.
        progressDialog.show();





                databaseReference = d_root.child("trip_details").child(contactUID_tx)
                        .child(key).child("petrol_filled");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(list!=null) {
                            list.clear();  // v v v v important (eliminate duplication of data)
                        }


                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                            PetrolFillingRecyclerInfo petrolFillingRecyclerInfo = postSnapshot.getValue(PetrolFillingRecyclerInfo.class);

                            list.add(petrolFillingRecyclerInfo);
                        }

                        adapter = new PetrolRecyclerViewAdapter(getActivity(), list);
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




                // export excel
                excel_export.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        excelDialog.show();

                        databaseReference = d_root.child("trip_details").child(contactUID_tx)
                                .child(key).child("petrol_filled");

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {


                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
                                SimpleDateFormat sdf1 = new SimpleDateFormat("HH_mm");
                                String actualDate = sdf.format(c.getTime());
                                String actualTime = sdf1.format(c.getTime());

                                DBHelper dbHelper2 = new DBHelper(getActivity());
                                dbHelper2.dropPetrolTable();


                                SharedPreferences dataSave = getActivity().getSharedPreferences("excel_data", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = dataSave.edit();
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                                    String date = TextUtils.substring(postSnapshot.child("date_time").getValue(String.class),0,10);
                                    String time = TextUtils.substring(postSnapshot.child("date_time").getValue(String.class),11,16);


                                    editor.putString("pump_name", postSnapshot.child("name").getValue(String.class));
                                    editor.putString("petrol_filled", postSnapshot.child("petrol_filled").getValue(String.class));
                                    editor.putString("address", postSnapshot.child("address").getValue(String.class));
                                    editor.putString("money_paid", postSnapshot.child("money_paid").getValue(String.class));
                                    editor.putString("state", postSnapshot.child("state").getValue(String.class));
                                    editor.putString("token_number", postSnapshot.child("token_number").getValue(String.class));
                                    editor.putString("pump_fuel_rate", postSnapshot.child("pump_fuel_rate").getValue(String.class));
                                    editor.putString("pump_fuel_rate_date", postSnapshot.child("pump_fuel_rate_date").getValue(String.class));
                                    editor.putString("gps_location", postSnapshot.child("gps_location").getValue(String.class));
                                    editor.putString("date", date);
                                    editor.putString("time", time);
                                    editor.apply();

                                    DBHelper dbHelper = new DBHelper(getActivity());
                                    dbHelper.insertPetrolData();

                                    final Cursor cursor = dbHelper.getuser();


                                    File sd = Environment.getExternalStorageDirectory();
                                    String csvFile = userNameUID+" Petrol filling "+actualDate+"_"+actualTime+".xls";

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
                                        WritableSheet sheet = workbook.createSheet("Petrol Filling Details", 0);

                                        sheet.addCell(new Label(0, 0, "Pump Name"));
                                        sheet.addCell(new Label(1, 0, "Petrol Filled"));
                                        sheet.addCell(new Label(2, 0, "Address"));
                                        sheet.addCell(new Label(3, 0, "Date"));
                                        sheet.addCell(new Label(4, 0, "Time"));
                                        sheet.addCell(new Label(5, 0, "GPS Location"));
                                        sheet.addCell(new Label(6, 0, "Token"));
                                        sheet.addCell(new Label(7, 0, "Money Paid"));
                                        sheet.addCell(new Label(8, 0, "State"));
                                        sheet.addCell(new Label(9, 0, "Pump Fuel Rate"));
                                        sheet.addCell(new Label(10, 0, "Pump Fuel Date"));
                                        sheet.addCell(new Label(11, 0, ""));

                                        if (cursor.moveToFirst()) {
                                            do {
                                                String pumpName = cursor.getString(cursor.getColumnIndex("pump_name"));
                                                String petrolFilled = cursor.getString(cursor.getColumnIndex("petrol_filled"));
                                                String address = cursor.getString(cursor.getColumnIndex("address"));
                                                String date1 = cursor.getString(cursor.getColumnIndex("date"));
                                                String time1 = cursor.getString(cursor.getColumnIndex("time"));
                                                String gpsLocation = cursor.getString(cursor.getColumnIndex("gps_location"));
                                                String tokenNumber = cursor.getString(cursor.getColumnIndex("token_number"));
                                                String moneyPaid = cursor.getString(cursor.getColumnIndex("money_paid"));
                                                String state = cursor.getString(cursor.getColumnIndex("state"));
                                                String pumpFuelRate = cursor.getString(cursor.getColumnIndex("pump_fuel_rate"));
                                                String pumpFuelRateDate = cursor.getString(cursor.getColumnIndex("pump_fuel_rate_date"));

                                                int i = cursor.getPosition() + 1;

                                                sheet.addCell(new Label(11, i, ""));
                                                sheet.addCell(new Label(10, i, pumpFuelRateDate));
                                                sheet.addCell(new Label(9, i, pumpFuelRate));
                                                sheet.addCell(new Label(8, i, state));
                                                sheet.addCell(new Label(7, i, moneyPaid));
                                                sheet.addCell(new Label(6, i, tokenNumber));
                                                sheet.addCell(new Label(5, i, gpsLocation));
                                                sheet.addCell(new Label(4, i, time1));
                                                sheet.addCell(new Label(3, i, date1));
                                                sheet.addCell(new Label(2, i, address));
                                                sheet.addCell(new Label(1, i, petrolFilled));
                                                sheet.addCell(new Label(0, i, pumpName));

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
