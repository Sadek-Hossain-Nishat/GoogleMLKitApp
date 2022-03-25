package com.example.googlemlkitapp.userauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText userEmailET,userPasswordET,userRetypePasswordET;
    private TextView haveaccountSignin;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

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
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.white));


        userEmailET=findViewById(R.id.profile_email);

        userPasswordET=findViewById(R.id.profile_password);

        userRetypePasswordET=findViewById(R.id.profile_retypepassword);

        haveaccountSignin=findViewById(R.id.haveaccountsignin);

        registerButton=findViewById(R.id.button_register);
        progressBar=findViewById(R.id.progressBar);

        mAuth=FirebaseAuth.getInstance();



        haveaccountSignin.setOnClickListener(this);
        registerButton.setOnClickListener(this);











    }


    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser!=null){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }





    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.haveaccountsignin:
                startActivity(new Intent(this,LoginActivity.class));
                finish();
                break;
            case R.id.button_register:
                createAccount();
                break;

        }

    }

    private void createAccount() {

        String email=userEmailET.getText().toString();
        String password=userPasswordET.getText().toString();
        String retypepassword=userRetypePasswordET.getText().toString();

        if (TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(retypepassword)){



            if (TextUtils.isEmpty(email)){
                userEmailET.setError("Field must be Filled up");

            }
            if (TextUtils.isEmpty(password)){
                userPasswordET.setError("Field must be Filled up");

            }
            if (TextUtils.isEmpty(retypepassword)){
                userRetypePasswordET.setError("Field must be Filled up");

            }
        }
        else {
            if (password.length()<6){
                userPasswordET.setError("Password must be at least 6 characters");

            }else {
                if (!password.equals(retypepassword)){
                    userRetypePasswordET.setError("Retype password didn't match with password");

                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {


                                    if (task.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(RegisterActivity.this, "You are Successfully Registered", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                        finish();

                                    }else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(RegisterActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();

                                    }




                                    
                                }
                            });








                }

            }

        }




    }
}