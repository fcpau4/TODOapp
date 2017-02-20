package com.example.a47276138y.todoapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    static final int REQUEST_TAKE_PHOTO = 1;
    private File f;
    private GridView gv;
    private FirebaseListAdapter firebaseListAdapter;




    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);


        Button btTakeImage = (Button) view.findViewById(R.id.bt_take_img);
        Button recVideo = (Button) view.findViewById(R.id.bt_rec_video);

        btTakeImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        gv = (GridView) view.findViewById(R.id.gv_images);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        firebaseListAdapter = new FirebaseListAdapter<String>(getActivity(), String.class, R.layout.gv_image_square, ref) {
            @Override
            protected void populateView(View v, String model, int position) {


                    ImageView img = (ImageView) v.findViewById(R.id.photo_saved);
                    Glide.with(getContext()).load(Uri.fromFile(new File(model)))
                            .centerCrop()
                            .crossFade()
                            .into(img);
                }

        };



        gv.setAdapter(firebaseListAdapter);




        /*recVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        return view;

    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = Utils.createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.getMessage();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                f = photoFile;
            }
        }


    }

    /*private void dispatchTakeVideoIntent(){

        Intent takeVideoIntent = new

    }*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    ref.push().setValue(f.getAbsolutePath());
                }

        }
    }



   /* private void setupAuth() {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            Log.d("Current user", String.valueOf(auth.getCurrentUser()));

        } else {
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
                            )
                            .build(),
                    RC_SIGN_IN);}
    }*/
}
