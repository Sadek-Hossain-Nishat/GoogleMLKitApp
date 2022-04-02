package com.example.googlemlkitapp.textrecognizing;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.googlemlkitapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class TextRecognizerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView detectText,showtext;
    private Button btnpickimage,btndetecttext;
    private ImageView imageView;
    private Bitmap bitmap;


    // alternative for onActivityforResults
    ActivityResultLauncher<Intent> gallerylauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognizer);

        // initialize the views
        detectText=findViewById(R.id.detect_text);
        btnpickimage=findViewById(R.id.button_pick_image);
        btndetecttext=findViewById(R.id.button_detect_text);
        imageView=findViewById(R.id.imageView);
        showtext=findViewById(R.id.textView_show);

        gallerylauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {

                            //handle the result
                            Intent data=result.getData();
                            // Get the url of the image from data
                            assert data != null;
                            Uri selectedImageUri = data.getData();
                            bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),selectedImageUri);
                            imageView.setImageBitmap(bitmap);




                        }catch (Exception e){
                            e.printStackTrace();

                        }



                    }
                });

        btnpickimage.setOnClickListener(this);
        btndetecttext.setOnClickListener(this);






    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_pick_image:
                showtext.setVisibility(View.GONE);
                pickImage();
                break;
            case R.id.button_detect_text:

                detectText();

                break;


        }

    }

    // detect text from image

    private void detectText() {
        if (bitmap==null){
            Toast.makeText(this, "No image is selected", Toast.LENGTH_SHORT).show();
        }
        else {

            InputImage inputImage=InputImage.fromBitmap(bitmap,0);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            recognizer.process(inputImage)
                    .addOnSuccessListener(
                            new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text texts) {

                                    processTextRecognitionResult(texts);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception

                                    e.printStackTrace();
                                }
                            });



        }
    }

    private void processTextRecognitionResult(Text texts) {
        detectText.setText(texts.getText().toString());

//        for (Text.TextBlock block : texts.getTextBlocks()) {
//            String blockText = block.getText();
//            detectText.setText(blockText.toString());
//
//        }




    }

    //pick image from gallery

    private void pickImage() {

        Intent galleryintent=new Intent();
        galleryintent.setType("image/*");
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);

        gallerylauncher.launch(galleryintent);








    }
}