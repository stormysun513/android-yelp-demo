package edu.cmu.demoapp2;

import twitter4j.auth.AccessToken;

/**
 * Created by Yu-Lun Tsai on 27/07/2017.
 */

public interface GetTwitterTokenTaskListener {

    // these callback functions must be implemented in ui thread because it is
    // used to update the gui of an activity
    void onTokenTaskCompleted(AccessToken accessToken);
    void onTokenTaskFailed(String message);
}
