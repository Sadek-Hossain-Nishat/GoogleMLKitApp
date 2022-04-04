package com.example.googlemlkitapp.fragments;

import android.content.Intent;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.googlemlkitapp.R;
import com.example.googlemlkitapp.barcodescanning.IntermediumBarcodeActivity;
import com.example.googlemlkitapp.facedetection.FaceDetectionActivity;
import com.example.googlemlkitapp.textrecognizing.TextRecognizerActivity;
import com.example.googlemlkitapp.userauthentication.custommenthod.ItemListener;

import com.example.googlemlkitapp.customadapter.mladapter.MLServicesAdapter;
import com.example.googlemlkitapp.modeldata.mlservice.MLService;


import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements ItemListener {



    private RecyclerView recyclerView;
    private final int[] servicelogoids = new int[]{
            R.drawable.barcode_scannig,
            R.drawable.face_detection,
            R.drawable.text_recognizing
    };
    private RecyclerView.LayoutManager layoutManager;
    private MLServicesAdapter mlServicesAdapter;


    private String[] servicetitles = new String[]{
            "Barcode Scanning",
            "Face Detection",
            "Text Recognizing"

    };

    private List<MLService> mlServices;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerviewgooglemlservice);

        mlServices = new ArrayList<>();
        for (int i = 0; i < servicelogoids.length; i++) {
            mlServices.add(new MLService(servicelogoids[i], servicetitles[i]));
        }

        configureServicelist();

        return view;
    }



    private void configureServicelist() {
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(layoutManager);
        mlServicesAdapter = new MLServicesAdapter(getActivity(), (ArrayList<MLService>) mlServices, this);
        recyclerView.setAdapter(mlServicesAdapter);


    }


    @Override
    public void clickItem(int position) {


        switch (position) {
            case 0:
                requireActivity().startActivity(new Intent(requireActivity(), IntermediumBarcodeActivity.class));
                break;
            case 1:
                requireActivity().startActivity(new Intent(requireActivity(), FaceDetectionActivity.class));
                break;
            case 2:
                requireActivity().startActivity(new Intent(requireActivity(), TextRecognizerActivity.class));
                break;

        }


    }


}