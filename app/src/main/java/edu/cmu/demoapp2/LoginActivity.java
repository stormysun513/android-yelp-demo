package edu.cmu.demoapp2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import twitter4j.auth.AccessToken;

public class LoginActivity extends AppCompatActivity implements GetTwitterTokenTaskListener{

    private static final String TAG = "YELP_DEMO";

    private TwitterCredentialDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        hideSystemUI();

        dao = new TwitterCredentialDao(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    public void onClickFacebookSignInButton(View view){
        startMainActivity();
    }

    public void onClickTwitterSignInButton(View view){

        if(dao.checkTwitterCredential()){
            startMainActivity();
        }
        else{
            new GetTwitterTokenTask(this).execute();
        }
    }

    @Override
    public void onTokenTaskCompleted(AccessToken accessToken) {
        Log.i(TAG, "successfully sign in with twitter account");
        dao.saveTwitterCredential(accessToken.getToken(), accessToken.getTokenSecret());
        startMainActivity();
    }

    @Override
    public void onTokenTaskFailed(String message) {

    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
