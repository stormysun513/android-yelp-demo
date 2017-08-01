package edu.cmu.demoapp2;

/**
 * Created by Yu-Lun Tsai on 01/08/2017.
 */

public interface PostTwitterTaskListener {
    void onPostTaskCompleted();
    void onPostTaskFailed(String message);
}
