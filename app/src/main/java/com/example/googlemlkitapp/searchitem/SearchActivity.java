package com.example.googlemlkitapp.searchitem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.os.Bundle;

import com.example.googlemlkitapp.R;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView=findViewById(R.id.id_searchview);
        searchView.requestFocus();


    }
}