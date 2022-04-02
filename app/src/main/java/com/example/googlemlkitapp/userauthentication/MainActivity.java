package com.example.googlemlkitapp.userauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.googlemlkitapp.R;
import com.example.googlemlkitapp.barcodescanning.BarcodeScannerMLActivity;
import com.example.googlemlkitapp.facedetection.FaceDetectionActivity;
import com.example.googlemlkitapp.fragments.DashBoardFragment;
import com.example.googlemlkitapp.fragments.HistoryFragment;
import com.example.googlemlkitapp.fragments.HomeFragment;
import com.example.googlemlkitapp.textrecognizing.TextRecognizerActivity;
import com.example.googlemlkitapp.userauthentication.custommenthod.ItemListener;
import com.example.googlemlkitapp.userauthentication.mladapter.MLServicesAdapter;
import com.example.googlemlkitapp.userauthentication.mlservice.MLService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity  {
    private FirebaseAuth mAuth;

    private BottomNavigationView bottomNavigationView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView=findViewById(R.id.bottom_nav_mainactivityid);

        mAuth=FirebaseAuth.getInstance();

        //setting home fragment as a main fragment
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
        getSupportFragmentManager().beginTransaction().replace(R.id.containermainactivityid,new HomeFragment()).commit();




        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.homeid:
                        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
                        getSupportFragmentManager().beginTransaction().replace(R.id.containermainactivityid,new HomeFragment()).commit();
                        break;
                    case R.id.dashboardid:
                        Objects.requireNonNull(getSupportActionBar()).setTitle("Dashboard");
                        getSupportFragmentManager().beginTransaction().replace(R.id.containermainactivityid,new DashBoardFragment()).commit();
                        break;
                    case R.id.historyid:
                        Objects.requireNonNull(getSupportActionBar()).setTitle("History");
                        getSupportFragmentManager().beginTransaction().replace(R.id.containermainactivityid,new HistoryFragment()).commit();
                        break;

                }

                return true;
            }
        });









    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            //noinspection RestrictedApi
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutid:
                logOut();
                break;
            case R.id.changepasswordid:
                changePassword();
                break;
            case R.id.deleteaccountdid:
                deleteAccount();
                break;
        }

        return true;
    }

    private void deleteAccount() {
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser!=null){

            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        mAuth.signOut();
                        Toast.makeText(MainActivity.this, "Your account has been deleted successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                        finish();
                    }
                    else {

                        Toast.makeText(MainActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });



        }




    }

    private void changePassword() {
        FirebaseUser currentUser=mAuth.getCurrentUser();
        EditText newpasswordET=new EditText(this);
        newpasswordET.requestFocus(); // to blink the cursor
        newpasswordET.setHint("Enter your new password here");
        newpasswordET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("Change Password");
        builder.setMessage("Do you want to change your password?\nPlease, enter your new password");
        builder.setView(newpasswordET);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(newpasswordET.getText().toString())){
                    Toast.makeText(MainActivity.this, "Empty field is not allowed for this operation", Toast.LENGTH_SHORT).show();

                }
                if ( newpasswordET.getText().toString().length()<6){
                    Toast.makeText(MainActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();

                }
                else {

                    if (currentUser!=null){


                        currentUser.updatePassword(newpasswordET.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(MainActivity.this, "Password has been changed successfully", Toast.LENGTH_SHORT).show();
                                            mAuth.signOut();
                                            startActivity(new Intent(MainActivity.this,LoginActivity.class));
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(MainActivity.this, ""+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });


                    }

                }




            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.setCancelable(false);

        builder.create().show();



    }




    private void logOut() {
        mAuth.signOut();
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }


}