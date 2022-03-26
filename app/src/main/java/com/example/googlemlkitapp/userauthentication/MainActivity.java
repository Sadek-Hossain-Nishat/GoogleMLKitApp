package com.example.googlemlkitapp.userauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.googlemlkitapp.R;
import com.example.googlemlkitapp.barcodescanning.BarcodeScannerMLActivity;
import com.example.googlemlkitapp.facedetection.FaceDetectionActivity;
import com.example.googlemlkitapp.textrecognizing.TextRecognizerActivity;
import com.example.googlemlkitapp.userauthentication.custommenthod.ItemListener;
import com.example.googlemlkitapp.userauthentication.mladapter.MLServicesAdapter;
import com.example.googlemlkitapp.userauthentication.mlservice.MLService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemListener {
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private int[] servicelogoids=new int[]{
            R.drawable.barcode_scanning,
            R.drawable.face_detection,
            R.drawable.text_recognizing
    };
    private RecyclerView.LayoutManager layoutManager;
    private MLServicesAdapter mlServicesAdapter;


    private String[] servicetitles=new String[]{
            "Barcode Scanning",
            "Face Detection",
            "Text Recognizing"

    };

    private List<MLService> mlServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recyclerviewgooglemlservice);
        mAuth=FirebaseAuth.getInstance();
        mlServices=new ArrayList<>();
        for (int i=0;i<servicelogoids.length;i++){
            mlServices.add(new MLService(servicelogoids[i],servicetitles[i]));
        }

        configureServicelist();






    }

    private void configureServicelist() {
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mlServicesAdapter=new MLServicesAdapter(this, (ArrayList<MLService>) mlServices,this);
        recyclerView.setAdapter(mlServicesAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu,menu);
        if(menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            //noinspection RestrictedApi
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutid:
                logOut();
                break;
            default:
                break;
        }

        return true;
    }

    private void logOut() {
        mAuth.signOut();
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }

    @Override
    public void clickItem(int position) {
        switch (position){
            case 0:
                startActivity(new Intent(this, BarcodeScannerMLActivity.class));
                break;
            case 1:
                startActivity(new Intent(this, FaceDetectionActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, TextRecognizerActivity.class));
                break;

        }

    }
}