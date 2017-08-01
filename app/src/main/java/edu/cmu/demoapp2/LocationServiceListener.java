package edu.cmu.demoapp2;

import android.location.Location;

/**
 * Created by Yu-Lun Tsai on 01/08/2017.
 */

public interface LocationServiceListener {
    void onLocationChange(Location location);
}
