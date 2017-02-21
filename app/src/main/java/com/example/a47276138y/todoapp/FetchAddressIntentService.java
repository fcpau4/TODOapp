package com.example.a47276138y.todoapp;

import android.app.IntentService;
import android.content.Intent;
import android.location.Geocoder;

import java.util.Locale;

/**
 * Created by 47276138y on 21/02/17.
 */

public class FetchAddressIntentService extends IntentService {

    //https://developer.android.com/training/location/display-address.html#fetch-address

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchAddressIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
    }
}
