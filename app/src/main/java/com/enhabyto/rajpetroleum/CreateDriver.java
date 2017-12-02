package com.enhabyto.rajpetroleum;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tapadoo.alerter.Alerter;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import mehdi.sakout.fancybuttons.FancyButton;
import util.android.textviews.FontTextView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateDriver extends Fragment implements View.OnClickListener{

    View view;
    FancyButton openUpdateProfile_btn, openCreateDriver_btn, createDriver_btn, updateProfile_btn
            , checkInfo_btn, chooseLis_btn, uploadLis_btn, chooseHazLis_btn, uploadHazLis_btn
            , chooseProfileImage_btn, uploadProfileImage_btn;

    FontTextView fontTextView1,fontTextView2;
    ImageView drivingLicenceImage, hazardousDrivingLicenceImage, driverImage;

    DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    DatabaseReference d_driverCredentials = d_root.child("driver_credentials");
    DatabaseReference d_driverProfiles = d_root.child("driver_profiles");
    DatabaseReference d_parent = FirebaseDatabase.getInstance().getReference().child("checkNetwork").child("isConnected");
    String connected;


    String contact_tx, password_tx, firebase_identity;
    String contact1_tx, name_tx, address_tx, birth_tx, drivingLis_tx, drivingLisValid_tx, hazardousDrivingLis_tx, hazardousDrivingLisValid_tx, education_tx, marital_tx, document_tx;
    EditText  name_et, address_et, birth_et, drivingLis_et, drivingLisValid_et, hazardousDrivingLis_et, hazardousDrivingLisValid_et, education_et, marital_et, document_et;

    EditText contact_et, password_et;
    String myFormatBirth;

    AlertDialog dialog, dialog_loading_data, dialog_updating_data, dialog_uploadingLicenceImage
            , dialog_uploadingHazardousLicenceImage, dialog_uploadProfileImage;


    AutoCompleteTextView contact1_et;
    Spinner spinner;

    //creating reference to firebase storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://rajpetroleum-4d3fa.appspot.com/");

    int PICK_IMAGE_REQUEST_L = 111;
    int PICK_IMAGE_REQUEST_H = 112;
    int PICK_IMAGE_REQUEST_P = 113;

    Uri LicenceImageFilePath;
    Uri HazardousLicenceImageFilePath;
    Uri ProfileImageFilePath;
    Calendar myCalendarLisValid = Calendar.getInstance();
    Calendar myCalendarHazLisValid = Calendar.getInstance();

    Calendar myCalendarBirth = Calendar.getInstance();


    public CreateDriver() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_driver, container, false);

        fontTextView1 = view.findViewById(R.id.cd_text);
        fontTextView2 = view.findViewById(R.id.cd_text1);
        openCreateDriver_btn = view.findViewById(R.id.cd_OpenCreateDriverButton);
        openUpdateProfile_btn = view.findViewById(R.id.cd_OpenUpdateProfileButton);
        createDriver_btn = view.findViewById(R.id.cd_CreateDriverButton);
        checkInfo_btn = view.findViewById(R.id.cd_checkInfoButton);
        updateProfile_btn = view.findViewById(R.id.cd_SubmitButton);
        chooseLis_btn = view.findViewById(R.id.cd_selectlis);
        uploadLis_btn = view.findViewById(R.id.cd_uploadlis);
        chooseHazLis_btn = view.findViewById(R.id.cd_Hazselectlis);
        chooseProfileImage_btn = view.findViewById(R.id.cd_selectProfileImage);
        uploadProfileImage_btn = view.findViewById(R.id.cd_uploadProfileImage);
        uploadHazLis_btn = view.findViewById(R.id.cd_Hazuploadlis);
        spinner = view.findViewById(R.id.cd_spinner);

        contact_et = view.findViewById(R.id.cd_ContactEditText);
        password_et = view.findViewById(R.id.cd_PasswordEditText);

        contact1_et = view.findViewById(R.id.cd_Contact1EditText);
        name_et = view.findViewById(R.id.cd_NameEditText);
        address_et = view.findViewById(R.id.cd_AddressEditText);
        birth_et = view.findViewById(R.id.cd_BirthEditText);
        drivingLis_et = view.findViewById(R.id.cd_LisEditText);
        drivingLisValid_et = view.findViewById(R.id.cd_LisValidToEditText);
        hazardousDrivingLis_et = view.findViewById(R.id.cd_HazLisEditText);
        hazardousDrivingLisValid_et = view.findViewById(R.id.cd_HazLisValidToEditText);
        education_et = view.findViewById(R.id.cd_EducationEditText);
        marital_et = view.findViewById(R.id.cd_MaritalStatusEditText);
        document_et = view.findViewById(R.id.cd_OtherDocEditText);

        birth_et.setKeyListener(null);
        drivingLisValid_et.setKeyListener(null);
        hazardousDrivingLisValid_et.setKeyListener(null);


        openUpdateProfile_btn.setOnClickListener(this);
        openCreateDriver_btn.setOnClickListener(this);
        createDriver_btn.setOnClickListener(this);
        updateProfile_btn.setOnClickListener(this);
        checkInfo_btn.setOnClickListener(this);
        chooseLis_btn.setOnClickListener(this);
        uploadLis_btn.setOnClickListener(this);
        chooseHazLis_btn.setOnClickListener(this);
        chooseProfileImage_btn.setOnClickListener(this);
        uploadHazLis_btn.setOnClickListener(this);
        uploadProfileImage_btn.setOnClickListener(this);
        birth_et.setOnClickListener(this);
        drivingLisValid_et.setOnClickListener(this);
        hazardousDrivingLisValid_et.setOnClickListener(this);

        dialog = new SpotsDialog(getActivity(),R.style.creatingDriverAccount);
        dialog_loading_data = new SpotsDialog(getActivity(),R.style.loadingData);
        dialog_updating_data = new SpotsDialog(getActivity(),R.style.Updating);
        dialog_uploadingLicenceImage = new SpotsDialog(getActivity(),R.style.dialog_uploadingLicence);
        dialog_uploadingHazardousLicenceImage = new SpotsDialog(getActivity(),R.style.dialog_uploadingHazardousLicence);
        dialog_uploadProfileImage = new SpotsDialog(getActivity(),R.style.dialog_uploadingProfile);


        drivingLicenceImage = view.findViewById(R.id.cd_drivingLicenceImage);
        hazardousDrivingLicenceImage = view.findViewById(R.id.cd_HazardousdrivingLicenceImage);
        driverImage = view.findViewById(R.id.cd_driverImage);


        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {

                            String selected = spinner.getSelectedItem().toString();
                            if (!TextUtils.equals(selected,"Select Contact")){
                                contact1_et.setText(selected);
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
    public void onClick(View v) {

        int id = v.getId();

        switch (id){

            case R.id.cd_OpenCreateDriverButton:

                view.findViewById(R.id.cd_profileRelativeLayout).setVisibility(View.VISIBLE);
              //  view.findViewById(R.id.cd_InfoRelativeLayout).setVisibility(View.GONE);
                YoYo.with(Techniques.FadeInLeft)
                        .duration(1000)
                        .repeat(0)
                        .playOn( view.findViewById(R.id.cd_profileRelativeLayout));
                YoYo.with(Techniques.FadeOutRight)
                        .duration(400)
                        .repeat(0)
                        .playOn( view.findViewById(R.id.cd_LoadInfoRelativeLayout));
                YoYo.with(Techniques.FadeOutRight)
                        .duration(400)
                        .repeat(0)
                        .playOn( view.findViewById(R.id.cd_driverDataRelativeLayout));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        contact1_et.setText("");
                        view.findViewById(R.id.cd_LoadInfoRelativeLayout).setVisibility(View.GONE);
                        view.findViewById(R.id.cd_driverDataRelativeLayout).setVisibility(View.GONE);
                    }
                },400);
                break;

            case R.id.cd_OpenUpdateProfileButton:

               // view.findViewById(R.id.cd_profileRelativeLayout).setVisibility(View.GONE);
                view.findViewById(R.id.cd_LoadInfoRelativeLayout).setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInRight)
                        .duration(1000)
                        .repeat(0)
                        .playOn( view.findViewById(R.id.cd_LoadInfoRelativeLayout));
                YoYo.with(Techniques.FadeOutLeft)
                        .duration(500)
                        .repeat(0)
                        .playOn( view.findViewById(R.id.cd_profileRelativeLayout));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.findViewById(R.id.cd_profileRelativeLayout).setVisibility(View.GONE);
                    }
                },400);

                break;


            case R.id.cd_CreateDriverButton:
                dialog.show();
                contact_tx = contact_et.getText().toString().trim();
                password_tx = password_et.getText().toString().trim();


                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                    dialog.dismiss();
                    return;
                }
                if (!TextUtils.isDigitsOnly(contact_tx)){
                    Alerter.create(getActivity())
                            .setTitle("Only digits are allowed!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog.dismiss();
                    return;
                }

                if (contact_tx.length()!=10){
                    Alerter.create(getActivity())
                            .setTitle("Invalid Mobile number!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog.dismiss();

                    return;
                }

                if (password_tx.length()<6){
                    Alerter.create(getActivity())
                            .setTitle("Length of password should be at least 6")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog.dismiss();

                    return;
                }

                d_parent.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        connected = dataSnapshot.getValue(String.class);

                        if (!TextUtils.equals(connected, "connected")){
                            Alerter.create(getActivity())
                                    .setTitle("Unable to Connect to Server!")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.no_internet)
                                    .show();
                            //    Log.w("123", connected);
                            dialog.dismiss();
                            return;
                        }


                        d_driverCredentials.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                firebase_identity = dataSnapshot.child(contact_tx).child("identity").getValue(String.class);


                                if (contact_tx.equals(firebase_identity)){
                                    Alerter.create(getActivity())
                                            .setTitle("Driver Account with "+firebase_identity+ " already exist!")
                                            .setContentGravity(1)
                                            .setBackgroundColorRes(R.color.black)
                                            .setIcon(R.drawable.error)
                                            .show();
                                    dialog.dismiss();
                                    return;
                                }
                                DatabaseReference d_driverProfile = d_root.child("driver_profiles").child(contact_tx);


                                d_driverCredentials.child(contact_tx).child("identity").setValue(contact_tx);
                                d_driverCredentials.child(contact_tx).child("password").setValue(password_tx);
                              //  d_driverProfile.child("contact").setValue(contact_tx);



                                Alerter.create(getActivity())
                                        .setTitle("Driver Account Created")
                                        .setContentGravity(1)
                                        .setBackgroundColorRes(R.color.black)
                                        .setIcon(R.drawable.success_icon)
                                        .show();
                                dialog.dismiss();

                                view.findViewById(R.id.cd_LoadInfoRelativeLayout).setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.FadeInRight)
                                        .duration(700)
                                        .repeat(0)
                                        .playOn( view.findViewById(R.id.cd_LoadInfoRelativeLayout));
                                YoYo.with(Techniques.FadeOutLeft)
                                        .duration(500)
                                        .repeat(0)
                                        .playOn( view.findViewById(R.id.cd_profileRelativeLayout));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.findViewById(R.id.cd_profileRelativeLayout).setVisibility(View.GONE);
                                    }
                                },400);
                                contact1_et.setText(contact_tx);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;


//                infoButton

            case R.id.cd_checkInfoButton:

                dialog_loading_data.show();
                contact1_tx = contact1_et.getText().toString().trim();

                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                    dialog_loading_data.dismiss();
                    return;
                }
                if (!TextUtils.isDigitsOnly(contact1_tx)){
                    Alerter.create(getActivity())
                            .setTitle("Only digits are allowed!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog_loading_data.dismiss();
                    return;
                }

                if (contact1_tx.length()!=10){
                    Alerter.create(getActivity())
                            .setTitle("Invalid Mobile number!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog_loading_data.dismiss();

                    return;
                }
                d_parent.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        connected = dataSnapshot.getValue(String.class);

                        if (!TextUtils.equals(connected, "connected")){
                            Alerter.create(getActivity())
                                    .setTitle("Unable to Connect to Server!")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.no_internet)
                                    .show();

                            dialog_loading_data.dismiss();
                            return;
                        }

                        d_driverCredentials.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                firebase_identity = dataSnapshot.child(contact1_tx).child("identity").getValue(String.class);

                                try {

                                    if (TextUtils.isEmpty(firebase_identity) && !contact1_tx.equals(firebase_identity)){
                                        Alerter.create(getActivity())
                                                .setTitle("Driver Account with "+contact1_tx+ " does not exist!")
                                                .setContentGravity(1)
                                                .setBackgroundColorRes(R.color.black)
                                                .setIcon(R.drawable.error)
                                                .show();
                                        dialog_loading_data.dismiss();
                                        view.findViewById(R.id.cd_driverDataRelativeLayout).setVisibility(View.GONE);
                                        return;

                                    }
                                    else {
                                        view.findViewById(R.id.cd_driverDataRelativeLayout).setVisibility(View.VISIBLE);
                                        YoYo.with(Techniques.FadeIn)
                                                .duration(1000)
                                                .repeat(0)
                                                .playOn( view.findViewById(R.id.cd_driverDataRelativeLayout));
                                    }




                                    dialog_loading_data.dismiss();


                                }
                                catch (NullPointerException e){
                                    e.printStackTrace();
                                }



                                d_driverProfiles.child(contact1_tx).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        name_tx = dataSnapshot.child("name").getValue(String.class);
                                        address_tx = dataSnapshot.child("address").getValue(String.class);
                                        birth_tx = dataSnapshot.child("birth").getValue(String.class);
                                        drivingLis_tx = dataSnapshot.child("driving_licence").getValue(String.class);
                                        drivingLisValid_tx = dataSnapshot.child("driving_licence_valid").getValue(String.class);
                                        hazardousDrivingLis_tx = dataSnapshot.child("hazardous_driving_licence").getValue(String.class);
                                        hazardousDrivingLisValid_tx = dataSnapshot.child("hazardous_driving_licence_valid").getValue(String.class);
                                        education_tx = dataSnapshot.child("education").getValue(String.class);
                                        marital_tx = dataSnapshot.child("marital").getValue(String.class);
                                        //  document_tx = dataSnapshot.child("document").getValue(String.class);

                                        name_et.setText(name_tx);
                                        address_et.setText(address_tx);
                                        birth_et.setText(birth_tx);
                                        drivingLis_et.setText(drivingLis_tx);
                                        drivingLisValid_et.setText(drivingLisValid_tx);
                                        hazardousDrivingLis_et.setText(hazardousDrivingLis_tx);
                                        hazardousDrivingLisValid_et.setText(hazardousDrivingLisValid_tx);
                                        education_et.setText(education_tx);
                                        marital_et.setText(marital_tx);
                                        try {
                                            int YearBirth = Integer.parseInt(TextUtils.substring(birth_tx, 6,10));
                                            int monthBirth = Integer.parseInt(TextUtils.substring(birth_tx, 3,5));
                                            int dateBirth = Integer.parseInt(TextUtils.substring(birth_tx, 0,2));
                                            //   Toast.makeText(getContext(), ""+YearBirth+" "+monthBirth+" "+dateBirth, Toast.LENGTH_SHORT).show();
                                            myCalendarBirth.set(Calendar.YEAR, YearBirth);
                                            myCalendarBirth.set(Calendar.MONTH, monthBirth-1);
                                            myCalendarBirth.set(Calendar.DAY_OF_MONTH, dateBirth);


                                            int YearLisValid = Integer.parseInt(TextUtils.substring(drivingLisValid_tx, 6,10));
                                            int monthLisValid = Integer.parseInt(TextUtils.substring(drivingLisValid_tx, 3,5));
                                            int dateLisValid = Integer.parseInt(TextUtils.substring(drivingLisValid_tx, 0,2));
                                            ///    Toast.makeText(getContext(), ""+YearLisValid+" "+monthLisValid+" "+dateLisValid, Toast.LENGTH_SHORT).show();
                                            myCalendarLisValid.set(Calendar.YEAR, YearLisValid);
                                            myCalendarLisValid.set(Calendar.MONTH, monthLisValid-1);
                                            myCalendarLisValid.set(Calendar.DAY_OF_MONTH, dateLisValid);

                                            int YearHazLisValid = Integer.parseInt(TextUtils.substring(hazardousDrivingLisValid_tx, 6,10));
                                            int monthHazLisValid = Integer.parseInt(TextUtils.substring(hazardousDrivingLisValid_tx, 3,5));
                                            int dateHazLisValid = Integer.parseInt(TextUtils.substring(hazardousDrivingLisValid_tx, 0,2));
                                            //  Toast.makeText(getContext(), ""+YearLisValid+" "+monthLisValid+" "+dateLisValid, Toast.LENGTH_SHORT).show();
                                            myCalendarHazLisValid.set(Calendar.YEAR, YearHazLisValid);
                                            myCalendarHazLisValid.set(Calendar.MONTH, monthHazLisValid-1);
                                            myCalendarHazLisValid.set(Calendar.DAY_OF_MONTH, dateHazLisValid);

                                        }
                                        catch (NullPointerException e){
                                            e.printStackTrace();
                                        }


                                        StorageReference licenceRef = storageRef.child("driver_profiles/").child(contact1_tx).child("/licence_image/").child("licence.jpg");
                                        StorageReference hazardousLicenceRef = storageRef.child("driver_profiles/").child(contact1_tx).child("/hazardous_licence_image/").child("hazardous_licence.jpg");
                                        StorageReference driverImageref = storageRef.child("driver_profiles/").child(contact1_tx).child("/profile_image/").child("image.jpg");

                                        try {

                                            licenceRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    if (uri != null){
                                                        Glide.with(getActivity())
                                                                .load(uri)
                                                                .fitCenter()
                                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                                .crossFade(1200)
                                                                .into(drivingLicenceImage);
                                                        drivingLicenceImage.setVisibility(View.VISIBLE);
                                                    }

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {

                                                }
                                            });


                                            hazardousLicenceRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    if (uri != null){
                                                        Glide.with(getActivity())
                                                                .load(uri)
                                                                .fitCenter()
                                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                                .crossFade(1200)
                                                                .into(hazardousDrivingLicenceImage);
                                                        hazardousDrivingLicenceImage.setVisibility(View.VISIBLE);
                                                    }

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {

                                                }
                                            });


                                            driverImageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    if (uri != null){
                                                        Glide.with(getActivity())
                                                                .load(uri)
                                                                .fitCenter()
                                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                                .crossFade(1200)
                                                                .into(driverImage);
                                                        driverImage.setVisibility(View.VISIBLE);
                                                    }

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {

                                                }
                                            });




                                        }
                                        catch (IllegalArgumentException e){
                                            e.printStackTrace();
                                        }





                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
         break;


//                SUBMIT BUTTON

            case R.id.cd_SubmitButton:
                dialog_updating_data.show();
                contact1_tx = contact1_et.getText().toString().trim();
                name_tx = name_et.getText().toString().trim();
                address_tx = address_et.getText().toString().trim();
                birth_tx = birth_et.getText().toString().trim();
                drivingLis_tx = drivingLis_et.getText().toString().trim();
                drivingLisValid_tx = drivingLisValid_et.getText().toString().trim();
                hazardousDrivingLis_tx = hazardousDrivingLis_et.getText().toString().trim();
                hazardousDrivingLisValid_tx = hazardousDrivingLisValid_et.getText().toString().trim();
                education_tx = education_et.getText().toString().trim();
                marital_tx  = marital_et.getText().toString().trim();
                document_tx = document_et.getText().toString().trim();

                if (!isNetworkAvailable()){
                    Alerter.create(getActivity())
                            .setTitle("No Internet Connection!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.no_internet)
                            .show();
                    dialog_updating_data.dismiss();
                    return;
                }

                Calendar minAge = Calendar.getInstance();
                minAge.add(Calendar.YEAR, -18);


                if (minAge.compareTo(myCalendarBirth) <0 ){
                    Alerter.create(getActivity())
                            .setTitle("Driver Should be 18 years old!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog_updating_data.dismiss();
                    return;
                }

                Calendar minDate = Calendar.getInstance();
                if (myCalendarLisValid.compareTo(minDate) < 0 ){
                    Alerter.create(getActivity())
                            .setTitle("Licence Valid Date Should be greater than current Date!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog_updating_data.dismiss();
                    return;
                }

                if (myCalendarHazLisValid.compareTo(minDate) < 0 ){
                    Alerter.create(getActivity())
                            .setTitle("Hazardous Licence Valid Date Should be greater than current Date!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog_updating_data.dismiss();
                    return;
                }


                d_parent.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        connected = dataSnapshot.getValue(String.class);

                        if (!TextUtils.equals(connected, "connected")){
                            Alerter.create(getActivity())
                                    .setTitle("Unable to Connect to Server!")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.no_internet)
                                    .show();
                            dialog_updating_data.dismiss();
                            return;
                        }

                        DatabaseReference d_driverProfile = d_root.child("driver_profiles").child(contact1_tx);
                        //  Toast.makeText(getContext(), ""+contact1_tx, Toast.LENGTH_SHORT).show();

                        d_driverProfile.child("contact").setValue(contact1_tx);
                        d_driverProfile.child("name").setValue(name_et.getText().toString());
                        d_driverProfile.child("address").setValue(address_tx);
                        d_driverProfile.child("birth").setValue(birth_tx);
                        d_driverProfile.child("driving_licence").setValue(drivingLis_tx);
                        d_driverProfile.child("driving_licence_valid").setValue(drivingLisValid_tx);
                        d_driverProfile.child("hazardous_driving_licence").setValue(hazardousDrivingLis_tx);
                        d_driverProfile.child("hazardous_driving_licence_valid").setValue(hazardousDrivingLisValid_tx);
                        d_driverProfile.child("education").setValue(education_tx);
                        d_driverProfile.child("marital").setValue(marital_tx);
                        //  d_driverProfile.child(document_tx).setValue(document_tx);

                        Alerter.create(getActivity())
                                .setTitle("Profile Updated")
                                .setContentGravity(1)
                                .setBackgroundColorRes(R.color.black)
                                .setIcon(R.drawable.success_icon)
                                .show();

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_DashBoard, new CreateDriver()).addToBackStack("AdminFragment").commit();

                        dialog_updating_data.dismiss();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        dialog_updating_data.dismiss();
                    }
                });


                break;



            // choose licence image

            case R.id.cd_selectlis:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST_L);
                break;



            case R.id.cd_uploadlis:
                if(LicenceImageFilePath != null) {
                   dialog_uploadingLicenceImage.show();
                    contact1_tx = contact1_et.getText().toString().trim();

                    StorageReference childRef = storageRef.child("driver_profiles").child(contact1_tx).child("licence_image/").child("licence.jpg");

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(LicenceImageFilePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog_uploadingLicenceImage.dismiss();
                            Alerter.create(getActivity())
                                    .setTitle("Driving Licence image successfully uploaded")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.success_icon)
                                    .show();
                            chooseLis_btn.setText("select\nimage");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog_uploadingLicenceImage.dismiss();
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
                    dialog_uploadingLicenceImage.dismiss();
                    Alerter.create(getActivity())
                            .setTitle("First Select Image")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                }

                break;


//                choose hazardous licence image

            case R.id.cd_Hazselectlis:
                Intent intentHazLis = new Intent();
                intentHazLis.setType("image/*");
                intentHazLis.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentHazLis, "Select Image"), PICK_IMAGE_REQUEST_H);
                break;


            case R.id.cd_Hazuploadlis:
                if(HazardousLicenceImageFilePath != null) {
                    dialog_uploadingHazardousLicenceImage.show();
                    contact1_tx = contact1_et.getText().toString().trim();

                    StorageReference childRef = storageRef.child("driver_profiles/").child(contact1_tx).child("hazardous_licence_image/").child("hazardous_licence.jpg");

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(HazardousLicenceImageFilePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog_uploadingHazardousLicenceImage.dismiss();
                            Alerter.create(getActivity())
                                    .setTitle("Hazardous Driving Licence image successfully uploaded")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.success_icon)
                                    .show();
                            chooseHazLis_btn.setText("select\nimage");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog_uploadingHazardousLicenceImage.dismiss();
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
                    dialog_uploadingHazardousLicenceImage.dismiss();
                    Alerter.create(getActivity())
                            .setTitle("First Select Image")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                }

                break;


            // choose licence image

            case R.id.cd_selectProfileImage:
                Intent intentProfile = new Intent();
                intentProfile.setType("image/*");
                intentProfile.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentProfile, "Select Image"), PICK_IMAGE_REQUEST_P);
                break;

                //upload profile image

            case R.id.cd_uploadProfileImage:
                if(ProfileImageFilePath != null) {
                    dialog_uploadProfileImage.show();
                    contact1_tx = contact1_et.getText().toString().trim();

                    StorageReference childRef = storageRef.child("driver_profiles/").child(contact1_tx).child("profile_image/").child("image.jpg");

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(ProfileImageFilePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            dialog_uploadProfileImage.dismiss();
                            Alerter.create(getActivity())
                                    .setTitle("Driver Image successfully uploaded")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.success_icon)
                                    .show();
                            chooseProfileImage_btn.setText("select image");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog_uploadProfileImage.dismiss();
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
                    dialog_uploadProfileImage.dismiss();
                    Alerter.create(getActivity())
                            .setTitle("First Select Image")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.black)
                            .setIcon(R.drawable.error)
                            .show();
                }

                break;



            //birth

            case R.id.cd_BirthEditText:

                DatePickerDialog.OnDateSetListener date_birth = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendarBirth.set(Calendar.YEAR, year);
                        myCalendarBirth.set(Calendar.MONTH, monthOfYear);
                        myCalendarBirth.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        myFormatBirth = "dd/MM/yyyy"; // your format
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormatBirth, Locale.getDefault());

                        birth_et.setText(sdf.format(myCalendarBirth.getTime()));


                    }


                };
                new DatePickerDialog(getContext(), date_birth, myCalendarBirth.get(Calendar.YEAR), myCalendarBirth.get(Calendar.MONTH), myCalendarBirth.get(Calendar.DAY_OF_MONTH)).show();
               break;


              // Valid Lis Date
            case R.id.cd_LisValidToEditText:

                DatePickerDialog.OnDateSetListener date_validLis = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendarLisValid.set(Calendar.YEAR, year);
                        myCalendarLisValid.set(Calendar.MONTH, monthOfYear);
                        myCalendarLisValid.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MM/yyyy"; // your format
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                        drivingLisValid_et.setText(sdf.format(myCalendarLisValid.getTime()));
                    }

                };
                new DatePickerDialog(getContext(), date_validLis, myCalendarLisValid.get(Calendar.YEAR), myCalendarLisValid.get(Calendar.MONTH), myCalendarLisValid.get(Calendar.DAY_OF_MONTH)).show();
                break;


            // Valid Lis Date
            case R.id.cd_HazLisValidToEditText:

                DatePickerDialog.OnDateSetListener date_validHazLis = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendarHazLisValid.set(Calendar.YEAR, year);
                        myCalendarHazLisValid.set(Calendar.MONTH, monthOfYear);
                        myCalendarHazLisValid.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MM/yyyy"; // your format
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                        hazardousDrivingLisValid_et.setText(sdf.format(myCalendarHazLisValid.getTime()));
                    }

                };
                new DatePickerDialog(getContext(), date_validHazLis, myCalendarHazLisValid.get(Calendar.YEAR), myCalendarHazLisValid.get(Calendar.MONTH), myCalendarHazLisValid.get(Calendar.DAY_OF_MONTH)).show();
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

        if (requestCode == PICK_IMAGE_REQUEST_L && resultCode == RESULT_OK && data != null && data.getData() != null) {
            LicenceImageFilePath = data.getData();
            chooseLis_btn.setText("Selected");
        }

        if (requestCode == PICK_IMAGE_REQUEST_H && resultCode == RESULT_OK && data != null && data.getData() != null) {
            HazardousLicenceImageFilePath = data.getData();
            chooseHazLis_btn.setText("Selected");
        }

        if (requestCode == PICK_IMAGE_REQUEST_P && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ProfileImageFilePath = data.getData();
            chooseProfileImage_btn.setText("Selected");
        }
    }

    public void onStart(){
        super.onStart();


        DatabaseReference dataRef_spinner = FirebaseDatabase.getInstance().getReference();
        dataRef_spinner.child("driver_credentials").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    final List<String> areas = new ArrayList<>();
                    areas.add("Select Contact");
                    for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                        String pump_name = areaSnapshot.child("identity").getValue(String.class);
                        areas.add(pump_name);

                    }

                    ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, areas);

                    areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(areasAdapter);
                    contact1_et.setAdapter(areasAdapter);

                } catch (NullPointerException e) {
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
