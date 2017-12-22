package com.enhabyto.rajpetroleum;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.tapadoo.alerter.Alerter;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import id.zelory.compressor.Compressor;
import mehdi.sakout.fancybuttons.FancyButton;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreatePump extends Fragment implements View.OnClickListener{

    private View view;

    EditText  pumpName2_et, contactName_et, contactNumber_et, contactApp_et
            , landLine_et, sap_et, address_et, gst_et, tin_et , company_et;

    String pumpName1_tx, pumpName2_tx, contactName_tx, contactNumber_tx, contactApp_tx
            , landLine_tx, sap_tx, address_tx, gst_tx, tin_tx , company_tx;

    FancyButton loadData_btn, submit_btn, selectImage_btn, uploadImage_btn, remove_pump;

    List<String> areas = new ArrayList<>();

    DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    DatabaseReference dataRef_pumpDetails;
    DatabaseReference dataRef_spinner = d_root.child("pump_details");
    AlertDialog dialog_updatePump, dialog_loading, dialog_uploadingPump, dialog_removing_truck;

    AutoCompleteTextView pumpName1_et;

    int PICK_IMAGE_REQUEST = 111;
    Uri ImageFilePath;
    Spinner spinner;
    String selected_val;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://rajpetroleum-4d3fa.appspot.com/");

    ImageView imageView;
    String url;

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
        imageView = view.findViewById(R.id.cp_pumpImage);
        remove_pump = view.findViewById(R.id.cp_removePumpButton);


        selectImage_btn = view.findViewById(R.id.cp_selectPumpImage);
        uploadImage_btn = view.findViewById(R.id.cp_uploadPumpImage);

        loadData_btn = view.findViewById(R.id.cp_openCreatePumpButton);
        submit_btn = view.findViewById(R.id.cp_submitPumpButton);

        loadData_btn.setOnClickListener(this);
        submit_btn.setOnClickListener(this);
        selectImage_btn.setOnClickListener(this);
        uploadImage_btn.setOnClickListener(this);
        remove_pump.setOnClickListener(this);

        dialog_updatePump = new SpotsDialog(getActivity(),R.style.dialog_updatingPump);
        dialog_loading = new SpotsDialog(getActivity(),R.style.loadingData);
        dialog_uploadingPump = new SpotsDialog(getActivity(),R.style.dialog_uploadingPumpImage);
        dialog_removing_truck = new SpotsDialog(getActivity(),R.style.dialog_removing);

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

            case R.id.cp_openCreatePumpButton:

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

                FirebaseDatabase.getInstance().getReference(".info/connected")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean connect = dataSnapshot.getValue(Boolean.class);
                                if (connect) {


                                    dataRef_pumpDetails = d_root.child("pump_details").child(pumpName1_tx);

                                    dataRef_pumpDetails.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                            url = dataSnapshot.child("pump_image").child("imageURL").getValue(String.class);

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

                                            if (url != null) {

                                                imageView.setVisibility(View.VISIBLE);
                                                Glide.with(getActivity())
                                                        .load(url)
                                                        .crossFade(1200)
                                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                        .into(imageView);

                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    dialog_loading.dismiss();

                                }
                                else {
                                    Alerter.create(getActivity())
                                            .setTitle("No Internet Connection!")
                                            .setText("There is no internet Connection! please check your internet connection")
                                            .setContentGravity(1)
                                            .setBackgroundColorRes(R.color.black)
                                            .setIcon(R.drawable.no_internet)
                                            .show();
                                    dialog_loading.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });



                break;


            case R.id.cp_submitPumpButton:
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

                FirebaseDatabase.getInstance().getReference(".info/connected")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean connect = dataSnapshot.getValue(Boolean.class);
                                if (connect) {

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
                                            .setTitle(pumpName1_tx + " Details Updated")
                                            .setContentGravity(1)
                                            .setBackgroundColorRes(R.color.black)
                                            .setIcon(R.drawable.success_icon)
                                            .show();

                                  //  getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard, new CreatePump()).addToBackStack("AdminFragment").commit();


                                    dialog_updatePump.dismiss();
                                }

                        else {
                                        Alerter.create(getActivity())
                                                .setTitle("No Internet Connection!")
                                                .setText("There is no internet Connection! please check your internet connection")
                                                .setContentGravity(1)
                                                .setBackgroundColorRes(R.color.black)
                                                .setIcon(R.drawable.no_internet)
                                                .show();
                                        dialog_loading.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });





                break;


            case R.id.cp_selectPumpImage:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

                break;


            case R.id.cp_uploadPumpImage:
                dialog_uploadingPump.show();
                UploadImageFileToFirebaseStorage();


                break;


            case R.id.cp_removePumpButton:

                pumpName2_tx = pumpName2_et.getText().toString().trim();
                new MaterialDialog.Builder(getActivity())

                        .title("Are You Sure to Remove "+pumpName2_tx)
                        .content("This cannot be undone, so be very sure.")
                        .positiveText("Yes")
                        .positiveColor(getResources().getColor(R.color.lightRed))
                        .negativeText("No")
                        .negativeColor(getResources().getColor(R.color.lightGreen))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog_removing_truck.show();

                                if(!isNetworkAvailable()){
                                    Alerter.create(getActivity())
                                            .setTitle("No Internet Connection!")
                                            .setContentGravity(1)
                                            .setBackgroundColorRes(R.color.black)
                                            .setIcon(R.drawable.no_internet)
                                            .show();
                                    return;
                                }

                                d_root.child("pump_details").child(pumpName2_tx).setValue(null);

                                view.findViewById(R.id.cp_relativeLayout2).setVisibility(View.GONE);
                                Alerter.create(getActivity())
                                        .setTitle("Pump Removed!")
                                        .setText("Pump "+pumpName2_tx+" is successfully removed.")
                                        .setContentGravity(1)
                                        .setBackgroundColorRes(R.color.black)
                                        .setIcon(R.drawable.no_internet)
                                        .show();


                                pumpName1_et.setText("");
                                areas.remove(pumpName2_tx);
                                dialog_removing_truck.dismiss();

                                // TODO
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // TODO

                            }
                        }) .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // TODO
                    }
                })
                        .show();

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

        FirebaseDatabase.getInstance().getReference(".info/connected")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean connect = dataSnapshot.getValue(Boolean.class);
                        if (connect){

                            dataRef_spinner.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    try {

                                        areas.add("Select Pump");
                                        for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                                            String areaName = areaSnapshot.child("pump_name").getValue(String.class);
                                            areas.add(areaName);


                                        }


                                        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areas);
                                        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        spinner.setAdapter(areasAdapter);
                                        pumpName1_et.setAdapter(areasAdapter);
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

                        else {
                            Alerter.create(getActivity())
                                    .setTitle("No Internet Connection!")
                                    .setText("There is no internet Connection! please check your internet connection")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.no_internet)
                                    .show();
                            dialog_loading.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }

    public void UploadImageFileToFirebaseStorage() {

        if (ImageFilePath != null) {
            String pumpName = pumpName2_et.getText().toString().trim();

            File actualImage = FileUtils.getFile(getActivity(), ImageFilePath);
            try {

                File compressedImageFile = new Compressor(getActivity())
                        .setQuality(20)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .compressToFile(actualImage);
                ImageFilePath = Uri.fromFile(compressedImageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            StorageReference childRef = storageRef.child("pump_details/").child(pumpName).child("/pump_image.jpg");
            childRef.putFile(ImageFilePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            @SuppressWarnings("VisibleForTests")
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(taskSnapshot.getDownloadUrl().toString());

                            String pumpName = pumpName2_et.getText().toString().trim();

                            d_root.child("pump_details").child(pumpName).child("pump_image").setValue(imageUploadInfo);
                            dialog_uploadingPump.dismiss();
                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            dialog_uploadingPump.dismiss();
                            // Showing exception erro message.
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                        }
                    });
        }
        else {
            dialog_uploadingPump.dismiss();
            Toast.makeText(getActivity(), "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }
    }

    //end
}

