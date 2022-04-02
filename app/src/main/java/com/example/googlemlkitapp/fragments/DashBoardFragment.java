package com.example.googlemlkitapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.googlemlkitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class DashBoardFragment extends Fragment implements View.OnClickListener {

    // alternative for onActivityforResults
    ActivityResultLauncher<Intent> gallerylauncher;
    FirebaseStorage storage;
    StorageReference storageReference;



    public DashBoardFragment() {


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        // create a path for uploading the image

        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        StorageReference reference=storageReference.child("profileimages/"+
                user.getUid()+"/profile.jpg");


        View view = inflater.inflate(R.layout.fragment_dash_board, container, false);
        TextView profilename = view.findViewById(R.id.profilenamedashboardeid);
        TextView profileemail = view.findViewById(R.id.profileemaildashboardid);

        CircleImageView profileimage=view.findViewById(R.id.profileimageid);
        ImageView editoption=view.findViewById(R.id.editoptionid);

        //showing the image on fragment onCreateView method is called
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileimage);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        //intent result -> alternative for onActivityforResults method

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




                            reference.putFile(selectedImageUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Picasso.get().load(uri).into(profileimage);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });








                        }catch (Exception e){
                            e.printStackTrace();

                        }



                    }
                });






        profileimage.setOnClickListener(this);
        editoption.setOnClickListener(this);

        // fetch data from cloud firestore database by documentReferenece
        DocumentReference documentReference = firestore.collection("users")
                .document(user.getUid());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    profilename.setText(document.getString("fullname"));
                    profileemail.setText(document.getString("email"));

                } else {

                }
            }
        });


        return view;
    }

    // edit profile image

    @Override
    public void onClick(View v) {

        Intent galleryintent=new Intent();
        galleryintent.setType("image/*");
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);

        gallerylauncher.launch(galleryintent);


    }




}