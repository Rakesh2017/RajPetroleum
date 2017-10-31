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
import android.widget.Toast;
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

    private EditText email_et, password_et;
    private String email_tx, password_tx;
    private FancyButton forgotPass_btn;

    private SharedPreferences.Editor editor1;
    private AlertDialog dialog;

    private DatabaseReference d_root = FirebaseDatabase.getInstance().getReference();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_et = findViewById(R.id.main_emailEditText);
        password_et = findViewById(R.id.main_passwordEditText);
        FancyButton login_btn = findViewById(R.id.main_loginButton);
        forgotPass_btn = findViewById(R.id.main_ForgotButton);
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
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    DatabaseReference d_refAdminProfile;
                                    assert user != null;
                                    d_refAdminProfile = d_root.child("admin").child(user.getUid()).child("profile");



                                    d_refAdminProfile.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String name_tx = dataSnapshot.child("name").getValue(String.class);
                                            String designation_tx = dataSnapshot.child("designation").getValue(String.class);
                                            String contact_tx = dataSnapshot.child("contact").getValue(String.class);
                                            String age_tx = dataSnapshot.child("age").getValue(String.class);


                                            if (TextUtils.isEmpty(name_tx) || TextUtils.isEmpty(contact_tx) || TextUtils.isEmpty(designation_tx) || TextUtils.isEmpty(age_tx)){
                                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_Login,new BasicInformation()).addToBackStack(null).commit();

                                            }
                                            else {

                                                Intent intent = new Intent(Login.this, DashBoard.class);
                                                startActivity(intent);
                                            }


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });



                                   // getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_Login,new BasicInformation()).addToBackStack(null).commit();

                                    editor1.putString("A",email_tx).apply();
                                    dialog.dismiss();

                                } else {
                                    // If sign in fails, display a message to the user.

                                    forgotPass_btn.setVisibility(View.VISIBLE);
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

                                }
                                dialog.dismiss();

                                // ...
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

    public void onStart(){
        super.onStart();


        SharedPreferences dataSave = getSharedPreferences("firstLog", 0);

        if(dataSave.getString("LaunchApplication","keys").equals("DashBoard")){
            Intent intent = new Intent(Login.this, DashBoard.class);
            startActivity(intent);
        }


    }

}//end
