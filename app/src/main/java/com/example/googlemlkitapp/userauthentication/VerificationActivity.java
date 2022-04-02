package com.example.googlemlkitapp.userauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemlkitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationActivity extends AppCompatActivity {
    private static final String TAG = "verify";
    FirebaseAuth mAuth;
    TextView textView2, textView3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        mAuth = FirebaseAuth.getInstance();

    }


    @Override
    protected void onStart() {
       super.onStart();

       FirebaseUser currentUser=mAuth.getCurrentUser();


        if (currentUser!=null){

            currentUser.reload();

            if (currentUser.isEmailVerified()) {

                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

        }





    }


    public void resendMail(View view) {
        textView2.setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);


        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            textView2.setVisibility(View.VISIBLE);
                            textView3.setVisibility(View.VISIBLE);
                        } else {
                            textView2.setVisibility(View.GONE);
                            textView3.setVisibility(View.GONE);

                        }
                    }
                });


    }
}