package com.example.a47276138y.todoapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.os.ResultReceiver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener {

    static final int REQUEST_TAKE_PHOTO = 1;
    private File f;
    private GridView gv;
    private FirebaseListAdapter firebaseListAdapter;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private ResultReceiver mResultReceiver;

    private Location mLastLocation;
    private String mLongitudeText;
    private String mLatitudeText;
    private GoogleApiClient mGoogleApiClient;
    private boolean addressRequested;
    private String mAddressOutput;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create an instance of GoogleAPIClient to get LastLocation.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

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

        firebaseListAdapter = new FirebaseListAdapter<PhotoData>(getActivity(), PhotoData.class, R.layout.gv_image_square, ref) {
            @Override
            protected void populateView(View v, PhotoData pic, int position) {

                ImageView img = (ImageView) v.findViewById(R.id.photo_saved);
                Glide.with(getContext()).load(Uri.fromFile(new File(pic.getAbsolute())))
                        .centerCrop()
                        .crossFade()
                        .into(img);

                TextView geoLoc = (TextView) v.findViewById(R.id.geo_localization);
                geoLoc.setText(pic.getLocation());
            }

        };

        gv.setAdapter(firebaseListAdapter);

        return view;
    }


    protected void startIntentService() {
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        getActivity().startService(intent);
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Get the address string
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

        }
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

            if (mGoogleApiClient.isConnected() && mLastLocation != null) {
                startIntentService();
            }

            // If GoogleApiClient isn't connected, process the user's request by
            // setting addressRequested to true. Later, when GoogleApiClient connects,
            // launch the service to fetch the address. As far as the user is
            // concerned, pressing the Fetch Address button
            // immediately kicks off the process of getting the address.
            addressRequested = true;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                    PhotoData picInfo = new PhotoData(f.getAbsolutePath(), mAddressOutput);
                    ref.push().setValue(picInfo);
                }
        }
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

        if(mLastLocation != null){
            mLatitudeText = String.valueOf(mLastLocation.getLatitude());
            mLongitudeText = String.valueOf(mLastLocation.getLongitude());
        }

        if(addressRequested){
            startIntentService();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
