package edu.cmu.demoapp2;

import java.util.List;

/**
 * Created by Yu-Lun Tsai on 31/07/2017.
 */

public interface SearchOnYelpTaskListener {
    void onSearchTaskCompleted(List<RestaurantInfoCell> results);
    void onSearchTaskFailed(String message);
}
