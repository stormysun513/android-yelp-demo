package edu.cmu.demoapp2;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Yu-Lun Tsai on 01/08/2017.
 */

public class CurrentLocationService implements LocationListener {

    private static final String TAG = "YELP_DEMO";

    private Location mLastLocation;
    private static final Object lockObj = new Object();

    public Location getLastLocation(){
        Location result = null;
        synchronized (lockObj){
            if(mLastLocation != null){
                result = new Location(mLastLocation);
            }
        }
        return result;
    }

    @Override
    public void onLocationChanged(Location location) {
        synchronized (lockObj){
            mLastLocation = new Location(location);
        }
        Log.i(TAG, String.format("Location: (%f, %f)", location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.i(TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.i(TAG, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.i(TAG, "onProviderDisabled");
    }
}
