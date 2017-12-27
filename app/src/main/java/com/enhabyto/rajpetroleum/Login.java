package com.enhabyto.rajpetroleum;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tapadoo.alerter.Alerter;
import dmax.dialog.SpotsDialog;
import mehdi.sakout.fancybuttons.FancyButton;


public class Login extends AppCompatActivity {

    private EditText email_et, password_et, contact_et, sub_password_et;
    private String email_tx, password_tx, contact_tx, sub_password_tx, match_contact_tx, match_password_tx;


    private SharedPreferences.Editor editor1;


    private AlertDialog dialog;

    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();
    DatabaseReference d_subAdminCredentials;


    private FirebaseAuth mAuth;
    DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference().child("checkNetwork").child("isConnected");
    String connected;

    //SharedPreferences preferencesSession = getSharedPreferences("session", Context.MODE_PRIVATE);
    //SharedPreferences.Editor editor2 = preferencesSession.edit();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_et = findViewById(R.id.main_emailEditText);
        password_et = findViewById(R.id.main_passwordEditText);
        contact_et = findViewById(R.id.main_sub_admin_contactEditText);
        sub_password_et = findViewById(R.id.main_sub_admin_passwordEditText);
        FancyButton login_btn = findViewById(R.id.main_loginButton);
        FancyButton sub_login_btn = findViewById(R.id.main_sub_admin_loginButton);
        dialog = new SpotsDialog(Login.this, R.style.logging);

        mAuth = FirebaseAuth.getInstance();


        // Shared Preferences
        SharedPreferences sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        editor1 = sharedPreferences.edit();

        email_tx= sharedPreferences.getString("A","");
        email_et.setText(email_tx);




        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

                email_tx = email_et.getText().toString().trim();
                password_tx = password_et.getText().toString().trim();


                if (!isNetworkAvailable()){
                    Alerter.create(Login.this)
                            .setTitle("No Internet Connection!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.blackFifty)
                            .setIcon(R.drawable.no_internet)
                            .show();
                    dialog.dismiss();
                    return;
                }


                if (!isValidEmail(email_tx)){
                    Alerter.create(Login.this)
                            .setTitle("Invalid Email Format!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.blackFifty)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog.dismiss();
                    return;
                }

                if (password_tx.length()<5){
                    Alerter.create(Login.this)
                            .setTitle("Length of Password should be greater than 5")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.blackFifty)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog.dismiss();
                    return;
                }

                        mAuth.signInWithEmailAndPassword(email_tx, password_tx)
                                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            //   Log.d("123", "signInWithEmail:success");

                                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_Login,new BasicInformation()).addToBackStack(null).commit();


                                            dialog.dismiss();

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            SharedPreferences dataSave = getSharedPreferences("firstLog", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = dataSave.edit();
                                            editor.putString("LaunchApplication", "Login");
                                            editor.apply();

                                            String message = "";
                                            try {
                                                message = task.getException().getMessage();
                                            }
                                            catch (NullPointerException e){
                                                e.printStackTrace();
                                            }
                                            Alerter.create(Login.this)
                                                    .setTitle("Authentication Failed!")
                                                    .setText(message)
                                                    .setContentGravity(1)
                                                    .setBackgroundColorRes(R.color.blackFifty)
                                                    .setIcon(R.drawable.error)
                                                    .show();
                                            dialog.dismiss();
                                        }


                                        // ...
                                    }
                                });


            }
        });


        sub_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();

                contact_tx = contact_et.getText().toString().trim();
                sub_password_tx = sub_password_et.getText().toString().trim();



                if (!isNetworkAvailable()){
                    Alerter.create(Login.this)
                            .setTitle("No Internet Connection!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.blackFifty)
                            .setIcon(R.drawable.no_internet)
                            .show();
                    dialog.dismiss();
                    return;
                }


                if (contact_tx.length() < 10){
                    Alerter.create(Login.this)
                            .setTitle("Invalid Contact Number!")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.blackFifty)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog.dismiss();
                    return;
                }

                if (sub_password_tx.length() < 6){
                    Alerter.create(Login.this)
                            .setTitle("Length of Password should be greater than 5")
                            .setContentGravity(1)
                            .setBackgroundColorRes(R.color.blackFifty)
                            .setIcon(R.drawable.error)
                            .show();
                    dialog.dismiss();
                    return;
                }
                connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        connected = dataSnapshot.getValue(String.class);
                        if (!TextUtils.equals(connected, "connected")){
                            Alerter.create(Login.this)
                                    .setTitle("Unable to Connect to Server!")
                                    .setContentGravity(1)
                                    .setBackgroundColorRes(R.color.black)
                                    .setIcon(R.drawable.no_internet)
                                    .show();
                            //    Log.w("123", connected);
                            dialog.dismiss();
                            return;
                        }

                        d_subAdminCredentials = d_root.child("sub_admin_credentials").child(contact_tx);

                        d_subAdminCredentials.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                match_contact_tx = dataSnapshot.child("identity").getValue(String.class);
                                match_password_tx = dataSnapshot.child("password").getValue(String.class);

                                if (TextUtils.equals(match_contact_tx, contact_tx) && TextUtils.equals(match_password_tx, sub_password_tx)){
                                    SharedPreferences dataSave = getSharedPreferences("firstLog", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = dataSave.edit();
                                    editor.putString("LaunchApplication", "DashBoard");
                                    editor.putString("user_designation", "subAdmin");
                                    editor.putString("subAdmin_contact", contact_tx);
                                    editor.apply();


                                    // editor2.putString("user_designation", "subAdmin");
                                    // editor2.commit();

                                    sub_password_et.setText("");

                                    Intent intent = new Intent(Login.this, DashBoard.class);
                                    startActivity(intent);
                                    Login.this.finish();
                                    dialog.dismiss();
                                }
                                else {
                                    Alerter.create(Login.this)
                                            .setTitle("Invalid Login Credentials")
                                            .setText("Contact Owner/Admin for more information!")
                                            .setContentGravity(1)
                                            .setBackgroundColorRes(R.color.black)
                                            .setIcon(R.drawable.no_internet)
                                            .show();
                                    //    Log.w("123", connected);
                                    dialog.dismiss();
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


        });


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

 /*   public void onStart(){
        super.onStart();


        SharedPreferences dataSave = getSharedPreferences("firstLog", 0);

        if(dataSave.getString("LaunchApplication","").equals("DashBoard")){
            Intent intent = new Intent(Login.this, DashBoard.class);
            startActivity(intent);
        }

    }*/

}//end
