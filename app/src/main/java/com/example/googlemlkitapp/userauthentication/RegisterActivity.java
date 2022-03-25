package com.example.googlemlkitapp.userauthentication;

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

import com.example.googlemlkitapp.R;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText userEmailET,userPasswordET,userRetypePasswordET;
    private TextView haveaccountSignin;
    private Button registerButton;
    private ProgressBar progressBar;

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



        haveaccountSignin.setOnClickListener(this);
        registerButton.setOnClickListener(this);











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
        progressBar.setVisibility(View.VISIBLE);
        String email=userEmailET.getText().toString();
        String password=userPasswordET.getText().toString();
        String retypepassword=userRetypePasswordET.getText().toString();

        if (TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(retypepassword)){
            userEmailET.setError("Field must be Filled up");
            userPasswordET.setError("Field must be Filled up");
            userRetypePasswordET.setError("Field must be Filled up");
        }
        else {

        }




    }
}