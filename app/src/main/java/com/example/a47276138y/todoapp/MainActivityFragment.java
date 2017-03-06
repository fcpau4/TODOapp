package com.example.a47276138y.todoapp;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_TAKE_VIDEO = 2;
    private File f;
    private GridView gv;
    private FirebaseListAdapter firebaseListAdapter;
    private DatabaseReference ref;
    private String mAddressOutput;


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


        recVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchDoVideoIntent();
            }
        });

        gv = (GridView) view.findViewById(R.id.gv_images);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference();

        firebaseListAdapter = new FirebaseListAdapter<Media>(getActivity(), Media.class, R.layout.gv_image_square, ref) {
            @Override
            protected void populateView(View v, Media pic, int position) {

                ImageView img = (ImageView) v.findViewById(R.id.photo_saved);
                Glide.with(getContext())
                        .load(Uri.fromFile(new File(pic.getAbsolute())))
                        .centerCrop()
                        .into(img);

                TextView geoLoc = (TextView) v.findViewById(R.id.geo_localization);
                geoLoc.setText(pic.getLocation());
            }

        };

        gv.setAdapter(firebaseListAdapter);

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


    private void dispatchDoVideoIntent() {
        //  Create new intent
        Intent makeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (makeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create a file to save the video
            File videoFile = null;

            try {
                videoFile = Utils.createVideoFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (videoFile != null) {
                makeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));

                // Set the video image quality to high
                makeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                startActivityForResult(makeVideoIntent, REQUEST_TAKE_VIDEO);

                f = videoFile;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {

                    String absolute = f.getAbsolutePath();
                    String address = getCurrentLocation();

                    Media mediaInfo = new Media(absolute, address);
                    ref.push().setValue(mediaInfo);
                }
                break;

            case REQUEST_TAKE_VIDEO:
                if (resultCode == RESULT_OK) {

                    String absolute = f.getAbsolutePath();
                    String address = getCurrentLocation();

                    Media mediaInfo = new Media(absolute, address);
                    ref.push().setValue(mediaInfo);
                }
                break;
        }
    }


    public String getCurrentLocation(){
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        try {

            Location mLocation = locationManager.getLastKnownLocation(provider);
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

            List<Address> addresses = null;

            // We're going to get just a single address.

                addresses = geocoder.getFromLocation(
                        mLocation.getLatitude(),
                        mLocation.getLongitude(),
                        1);

            mAddressOutput = addresses.get(0).getAddressLine(0);

            Log.w("LOCATION",  mAddressOutput);

        return mAddressOutput;


        }catch(SecurityException e){
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /*@Override
    public void onStart() {
        super.onStart();
        if ((GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity())) == 0) {
            mGoogleApiClient.connect();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }



    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        List<Address> addresses = null;

        // We're going to get just a single address.
        try {
            addresses = geocoder.getFromLocation(
                    mLastLocation.getLatitude(),
                    mLastLocation.getLongitude(),
                    1);

            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }

            mAddressOutput = addressFragments.get(0);

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/
}


