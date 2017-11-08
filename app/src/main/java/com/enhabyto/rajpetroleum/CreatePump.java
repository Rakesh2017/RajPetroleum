package com.enhabyto.rajpetroleum;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tapadoo.alerter.Alerter;


import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import mehdi.sakout.fancybuttons.FancyButton;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreatePump extends Fragment implements View.OnClickListener{

    private View view;

    EditText pumpName1_et, pumpName2_et, contactName_et, contactNumber_et, contactApp_et
            , landLine_et, sap_et, address_et, gst_et, tin_et , company_et;

    String pumpName1_tx, pumpName2_tx, contactName_tx, contactNumber_tx, contactApp_tx
            , landLine_tx, sap_tx, address_tx, gst_tx, tin_tx , company_tx;

    FancyButton loadData_btn, submit_btn, selectImage_btn, uploadImage_btn;

    DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dataRef_pumpDetails;
    DatabaseReference dataRef_spinner = d_root.child("pump_details");
    AlertDialog dialog_updatePump, dialog_loading, dialog_uploadingPump;

    DatabaseReference d_parent = FirebaseDatabase.getInstance().getReference().child("checkNetwork").child("isConnected");
    String connected;

    int PICK_IMAGE_REQUEST = 111;
    Uri ImageFilePath;
    Spinner spinner;
    String selected_val;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://rajpetroleum-4d3fa.appspot.com/");

    public CreatePump() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_pump, container, false);

        pumpName1_et = view.findViewById(R.id.cp_pumpNameEditText1);
        pumpName2_et = view.findViewById(R.id.cp_pumpNameEditText2);
        contactName_et = view.findViewById(R.id.cp_contactNameEditText);
        contactApp_et = view.findViewById(R.id.cp_contactAppEditText);
        contactNumber_et = view.findViewById(R.id.cp_contactEditText);
        landLine_et = view.findViewById(R.id.cp_landlineEditText);
        sap_et = view.findViewById(R.id.cp_sapEditText);
        address_et = view.findViewById(R.id.cp_addressEditText);
        gst_et = view.findViewById(R.id.cp_gstEditText);
        tin_et = view.findViewById(R.id.cp_tinEditText);
        company_et = view.findViewById(R.id.cp_companyEditText);
        spinner = view.findViewById(R.id.cp_spinner);

        pumpName2_et.setKeyListener(null);

        selectImage_btn = view.findViewById(R.id.cp_selectPumpImage);
        uploadImage_btn = view.findViewById(R.id.cp_uploadPumpImage);

        loadData_btn = view.findViewById(R.id.ct_openCreatePumpButton);
        submit_btn = view.findViewById(R.id.ct_submitPumpButton);

        loadData_btn.setOnClickListener(this);
        submit_btn.setOnClickListener(this);
        selectImage_btn.setOnClickListener(this);
        uploadImage_btn.setOnClickListener(this);

        dialog_updatePump = new SpotsDialog(getActivity(),R.style.dialog_updatingPump);
        dialog_loading = new SpotsDialog(getActivity(),R.style.loadingData);
        dialog_uploadingPump = new SpotsDialog(getActivity(),R.style.dialog_uploadingPumpImage);

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                   spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                       @Override
                       public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {

                               selected_val = spinner.getSelectedItem().toString();
                           if (!TextUtils.equals(selected_val,"Select Pump")){
                               pumpName1_et.setText(selected_val);

                           }


                               //YOUR CODE HERE


                           // Toast.makeText(getApplicationContext(), selected_val ,
                           //      Toast.LENGTH_SHORT).show();
                       }

                       @Override
                       public void onNothingSelected(AdapterView<?> arg0) {
                           // TODO Auto-generated method stub

                       }
                   });

                }
                return false;
            }
        });







        return view;
    }

    @Override
    public void onClick(final View v) {
        int id = v.getId();
        switch (id){

            case R.id.ct_openCreatePumpButton:

                dialog_loading.show();

                pumpName1_tx = pumpName1_et.getText().toString().trim();
                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                    dialog_loading.dismiss();
                    return;
                }

                if (pumpName1_tx.length()<2){
                    Alerter.create(getActivity())
                            .setTitle("Pump Name too short!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog_loading.dismiss();
                    return;
                }

                d_parent.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        connected = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (!TextUtils.equals(connected, "connected")){
                            Alerter.create(getActivity())
                                    .setTitle("Unable to Connect to Server!")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.no_internet)
                                    .show();
                            //    Log.w("123", connected);
                            dialog_loading.dismiss();
                            return;
                        }

                        dataRef_pumpDetails = d_root.child("pump_details").child(pumpName1_tx);

                        dataRef_pumpDetails.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                view.findViewById(R.id.cp_relativeLayout2).setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.FadeIn)
                                        .duration(700)
                                        .repeat(0)
                                        .playOn(view.findViewById(R.id.cp_relativeLayout2));

                                contactNumber_tx = dataSnapshot.child("contact_number").getValue(String.class);
                                contactName_tx = dataSnapshot.child("contact_name").getValue(String.class);
                                contactApp_tx = dataSnapshot.child("contact_whatsapp").getValue(String.class);
                                landLine_tx = dataSnapshot.child("landline").getValue(String.class);
                                sap_tx = dataSnapshot.child("sap_code").getValue(String.class);
                                address_tx = dataSnapshot.child("address").getValue(String.class);
                                gst_tx = dataSnapshot.child("gst").getValue(String.class);
                                tin_tx = dataSnapshot.child("tin").getValue(String.class);
                                company_tx = dataSnapshot.child("company_name").getValue(String.class);

                                pumpName2_et.setText(pumpName1_tx);
                                company_et.setText(company_tx);
                                contactApp_et.setText(contactApp_tx);
                                landLine_et.setText(landLine_tx);
                                sap_et.setText(sap_tx);
                                address_et.setText(address_tx);
                                gst_et.setText(gst_tx);
                                tin_et.setText(tin_tx);
                                contactNumber_et.setText(contactNumber_tx);
                                contactName_et.setText(contactName_tx);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        dialog_loading.dismiss();

                    }
                },1500);




                break;


            case R.id.ct_submitPumpButton:
                dialog_updatePump.show();

                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                    dialog_updatePump.dismiss();
                    return;
                }


                d_parent.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        connected = dataSnapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        if (!TextUtils.equals(connected, "connected")){
                            Alerter.create(getActivity())
                                    .setTitle("Unable to Connect to Server!")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.no_internet)
                                    .show();
                            //    Log.w("123", connected);
                            dialog_updatePump.dismiss();
                            return;
                        }

                        pumpName2_tx = pumpName2_et.getText().toString().trim();
                        dataRef_pumpDetails = d_root.child("pump_details").child(pumpName2_tx);

                        contactName_tx = contactName_et.getText().toString().trim();
                        contactNumber_tx = contactNumber_et.getText().toString().trim();
                        company_tx = company_et.getText().toString().trim();
                        contactApp_tx = contactApp_et.getText().toString().trim();
                        landLine_tx = landLine_et.getText().toString().trim();
                        sap_tx = sap_et.getText().toString().trim();
                        gst_tx = gst_et.getText().toString().trim();
                        tin_tx = tin_et.getText().toString().trim();
                        address_tx = address_et.getText().toString().trim();

                        dataRef_pumpDetails.child("contact_name").setValue(contactName_tx);
                        dataRef_pumpDetails.child("contact_number").setValue(contactNumber_tx);
                        dataRef_pumpDetails.child("company_name").setValue(company_tx);
                        dataRef_pumpDetails.child("contact_whatsapp").setValue(contactApp_tx);
                        dataRef_pumpDetails.child("landline").setValue(landLine_tx);
                        dataRef_pumpDetails.child("sap_code").setValue(sap_tx);
                        dataRef_pumpDetails.child("address").setValue(address_tx);
                        dataRef_pumpDetails.child("gst").setValue(gst_tx);
                        dataRef_pumpDetails.child("tin").setValue(tin_tx);
                        dataRef_pumpDetails.child("pump_name").setValue(pumpName2_tx);
                        dataRef_pumpDetails.child("address").setValue(address_tx);

                        Alerter.create(getActivity())
                                .setTitle("Pump Details Updated")
                                .setContentGravity(1)
                                .setBackgroundColorRes(R.color.black)
                                .setIcon(R.drawable.success_icon)
                                .show();

                        dialog_updatePump.dismiss();



                    }
                },1500);

                break;


            case R.id.cp_selectPumpImage:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

                break;


            case R.id.cp_uploadPumpImage:
                if(ImageFilePath != null) {
                    dialog_uploadingPump.show();
                    String pumpName = pumpName1_et.getText().toString().trim();
                    StorageReference childRef = storageRef.child("pump_details/").child(pumpName).child("/pump_image.jpg");

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(ImageFilePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog_uploadingPump.dismiss();
                            Alerter.create(getActivity())
                                    .setTitle("Image Successfully uploaded")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.success_icon)
                                    .show();
                            selectImage_btn.setText("select image");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           dialog_uploadingPump.dismiss();
                            Alerter.create(getActivity())
                                    .setTitle("Upload Failed"+e.getMessage())
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.error)
                                    .show();
                        }
                    });
                }
                else {
                 dialog_uploadingPump.dismiss();
                    Alerter.create(getActivity())
                            .setTitle("First Select Image")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                }

                break;

        }


    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImageFilePath = data.getData();
            selectImage_btn.setText("Selected");
        }
    }

    public void onStart(){
        super.onStart();


                dataRef_spinner.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Is better to use a List, because you don't know the size
                        // of the iterator returned by dataSnapshot.getChildren() to
                        // initialize the array
                        try {
                            final List<String> areas = new ArrayList<String>();
                            areas.add("Select Pump");
                            for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                                String areaName = areaSnapshot.child("pump_name").getValue(String.class);
                                areas.add(areaName);


                            }


                            ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areas);
                            areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(areasAdapter);
                        }
                        catch (NullPointerException e){
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        throw databaseError.toException();
                    }
                });

    }

    //end
}

