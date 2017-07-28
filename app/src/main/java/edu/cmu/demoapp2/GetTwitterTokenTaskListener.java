package edu.cmu.demoapp2;

import twitter4j.auth.AccessToken;

/**
 * Created by Yu-Lun Tsai on 27/07/2017.
 */

public interface GetTwitterTokenTaskListener {

    void onTaskCompleted(AccessToken accessToken);
    void onTaskFailed(String message);
}
