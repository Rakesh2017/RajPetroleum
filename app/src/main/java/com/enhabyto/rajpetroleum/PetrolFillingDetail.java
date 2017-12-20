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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;

    private String contactUID_tx;
    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    String key;

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

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Data...");

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




                // export excel
                excel_export.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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




                                SharedPreferences dataSave = getActivity().getSharedPreferences("excel_data", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = dataSave.edit();
                                for (DataSnapshot postSnapshot : snapshot.getChildren()) {


                                    editor.putString("pump_name", postSnapshot.child("name").getValue(String.class));
                                    editor.putString("petrol_filled", postSnapshot.child("petrol_filled").getValue(String.class));
                                    editor.putString("address", postSnapshot.child("address").getValue(String.class));
                                    editor.putString("money_paid", postSnapshot.child("money_paid").getValue(String.class));
                                    editor.putString("state", postSnapshot.child("state").getValue(String.class));
                                    editor.putString("token_number", postSnapshot.child("token_number").getValue(String.class));
                                    editor.putString("gps_location", postSnapshot.child("gps_location").getValue(String.class));
                                    editor.apply();

                                    DBHelper dbHelper = new DBHelper(getActivity());
                                    dbHelper.insertData();

                                    final Cursor cursor = dbHelper.getuser();

                                    File sd = Environment.getExternalStorageDirectory();
                                    String csvFile = contactUID_tx+"Petrol filling"+actualDate+actualTime+".xls";

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
                                        WritableSheet sheet = workbook.createSheet("userList", 0);

                                        sheet.addCell(new Label(0, 0, "UserName"));
                                        sheet.addCell(new Label(1, 0, "PhoneNumber"));

                                        if (cursor.moveToFirst()) {
                                            do {
                                                String name = cursor.getString(cursor.getColumnIndex("user_name"));
                                                String phoneNumber = cursor.getString(cursor.getColumnIndex("phone_number"));

                                                int i = cursor.getPosition() + 1;


                                                sheet.addCell(new Label(1, i, phoneNumber));
                                                sheet.addCell(new Label(0, i, name));

                                            } while (cursor.moveToNext());
                                        }
                                        //closing cursor
                                        cursor.close();
                                        workbook.write();
                                        workbook.close();
                                        Toast.makeText(getActivity(),
                                                "Data Exported in a Excel Sheet", Toast.LENGTH_SHORT).show();


                                    } catch(IOException | WriteException e){
                                        e.printStackTrace();
                                    }



                                }


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
