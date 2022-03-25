package com.example.googlemlkitapp.userauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemlkitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText loginEmailET, loginpasswordET;
    private Button signinButton;
    private TextView notaccountcreateaccount;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));


        loginEmailET = findViewById(R.id.signin_email);
        loginpasswordET = findViewById(R.id.signin_password);
        signinButton = findViewById(R.id.button_signin);
        notaccountcreateaccount = findViewById(R.id.notaccountregister);
        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

        signinButton.setOnClickListener(this);
        notaccountcreateaccount.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_signin:
                signInUser();
                break;
            case R.id.notaccountregister:
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
                break;
        }

    }

    private void signInUser() {

        String email = loginEmailET.getText().toString();
        String password = loginpasswordET.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {


            if (TextUtils.isEmpty(email)) {
                loginEmailET.setError("Field must be Filled up");

            }
            if (TextUtils.isEmpty(password)) {
                loginpasswordET.setError("Field must be Filled up");

            }

        }


        if (password.length() < 6) {
            loginpasswordET.setError("Password must be at least 6 characters");

        } else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        progressBar.setVisibility(View.GONE);
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(LoginActivity.this, "You are Successfully Registered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,LoginActivity.class));
                        finish();

                    }else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();

                    }



                }
            });


        }
    }


}
