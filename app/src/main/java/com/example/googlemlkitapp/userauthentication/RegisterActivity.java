package com.example.googlemlkitapp.userauthentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.googlemlkitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "register";
    private EditText userEmailET, userPasswordET, userRetypePasswordET, userFullNameET;
    private TextView haveaccountSignin;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));


        userFullNameET = findViewById(R.id.user_fullnameid);


        userEmailET = findViewById(R.id.profile_email);

        userPasswordET = findViewById(R.id.profile_password);

        userRetypePasswordET = findViewById(R.id.profile_retypepassword);

        haveaccountSignin = findViewById(R.id.haveaccountsignin);

        registerButton = findViewById(R.id.button_register);
        progressBar = findViewById(R.id.progressBar);
        Log.i(TAG, "onCreate: ");

        mAuth = FirebaseAuth.getInstance();


        firestore = FirebaseFirestore.getInstance();


        haveaccountSignin.setOnClickListener(this);
        registerButton.setOnClickListener(this);


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.haveaccountsignin:
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.button_register:
                createAccount();
                break;

        }


    }


    private void createAccount() {

        String email = userEmailET.getText().toString();
        String password = userPasswordET.getText().toString();
        String retypepassword = userRetypePasswordET.getText().toString();
        String fullname=userFullNameET.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(retypepassword)) {


            if (TextUtils.isEmpty(email)) {
                userEmailET.setError("Field must be Filled up");

            }
            if (TextUtils.isEmpty(password)) {
                userPasswordET.setError("Field must be Filled up");

            }
            if (TextUtils.isEmpty(retypepassword)) {
                userRetypePasswordET.setError("Field must be Filled up");

            }
        } else {
            if (password.length() < 6) {
                userPasswordET.setError("Password must be at least 6 characters");

            } else {
                if (!password.equals(retypepassword)) {
                    userRetypePasswordET.setError("Retype password didn't match with password");

                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {


                                    if (task.isSuccessful()) {
                                        progressBar.setVisibility(View.GONE);
                                        // Sign in success, update UI with the signed-in user's information

                                        Map<String,Object> userinfo=new HashMap<>();
                                        userinfo.put("fullname",fullname);
                                        userinfo.put("email",email);


                                        FirebaseUser user = mAuth.getCurrentUser();

                                        if (user != null) {
                                            firestore.collection("users").document(user.getUid())
                                                    .set(userinfo)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.i(TAG, "DocumentSnapshot successfully written!");

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.i(TAG, "Error writing document", e);

                                                        }
                                                    });


                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(RegisterActivity.this, "You are Successfully Registered", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(RegisterActivity.this, VerificationActivity.class));
                                                                finish();
                                                            } else {
                                                                progressBar.setVisibility(View.GONE);
                                                                Toast.makeText(RegisterActivity.this, "" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    });


                                        }


                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(RegisterActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    }


                                }
                            });


                }

            }

        }


    }


}