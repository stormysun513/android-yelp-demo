package edu.cmu.demoapp2;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by Yu-Lun Tsai on 07/06/2017.
 */

public class GetTwitterTokenTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "DEMO";

    // GUI objects
    private Context mContext;
    private String mOAuthURL, mVerifier;
    private Dialog mDialog;
    private ProgressDialog mProgressBar;

    // Twitter variables
    private Twitter mTwitter;
    private RequestToken mRequestToken;
    private AccessToken mAccessToken;

    public GetTwitterTokenTask(Context context) {
        this.mContext = context;
        mTwitter = new TwitterFactory().getInstance();
        mTwitter.setOAuthConsumer(BuildConfig.TWITTER_API_KEY, BuildConfig.TWITTER_API_SECRET);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //showing a progress mDialog
        mProgressBar = new ProgressDialog(mContext);
        mProgressBar.setMessage("Connecting...");
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressBar.setCancelable(false);

        //prevent system ui from showing
        mProgressBar.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        mProgressBar.show();

        //Set the dialog to immersive
        mProgressBar.getWindow().getDecorView().setSystemUiVisibility(
                ((Activity)mContext).getWindow().getDecorView().getSystemUiVisibility());

        //Clear the not focusable flag from the window
        mProgressBar.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            mRequestToken = mTwitter.getOAuthRequestToken();
            mOAuthURL = mRequestToken.getAuthorizationURL();
        } catch (TwitterException e) {
            Log.e(TAG, e.toString());
        }
        return mOAuthURL;
    }

    @Override
    protected void onPostExecute(String oauthUrl) {

        if (oauthUrl != null) {

            mDialog = new Dialog(mContext);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(R.layout.dialog_oauth);

            WebView webView = mDialog.findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(oauthUrl);
            webView.setWebViewClient(new WebViewClient() {

                boolean authComplete = false;

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    if (url.contains("oauth_verifier") && !authComplete) {
                        authComplete = true;
                        Uri uri = Uri.parse(url);
                        mVerifier = uri.getQueryParameter("oauth_verifier");
                        mDialog.dismiss();

                        // evoke access token asynctask
                        new AccessTokenGetTask().execute();

                    } else if (url.contains("denied")) {
                        mDialog.dismiss();
                        if(mContext instanceof GetTwitterTokenTaskListener) {
                            ((GetTwitterTokenTaskListener) mContext).onTaskFailed("permission denied");
                        }
                    }
                }
            });

            // prevent system ui from showing
            mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            mDialog.show();

            //Set the dialog to immersive
            mDialog.getWindow().getDecorView().setSystemUiVisibility(
                    ((Activity)mContext).getWindow().getDecorView().getSystemUiVisibility());

            //Clear the not focusable flag from the window
            mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            //make the dialog cancelable
            mDialog.setCancelable(true);

            //dismiss progress mDialog when task finished.
            mProgressBar.dismiss();

        } else {
            if(mContext instanceof GetTwitterTokenTaskListener) {
                ((GetTwitterTokenTaskListener) mContext).onTaskFailed("network error");
            }
        }
    }

    private class AccessTokenGetTask extends AsyncTask<String, String, AccessToken> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar = new ProgressDialog(mContext);
            mProgressBar.setMessage("Fetching Data ...");
            mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressBar.setCancelable(false);

            //prevent system ui from showing
            mProgressBar.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

            mProgressBar.show();

            //Set the dialog to immersive
            mProgressBar.getWindow().getDecorView().setSystemUiVisibility(
                    ((Activity)mContext).getWindow().getDecorView().getSystemUiVisibility());

            //Clear the not focusable flag from the window
            mProgressBar.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }

        @Override
        protected AccessToken doInBackground(String... args) {
            try {
                mAccessToken = mTwitter.getOAuthAccessToken(mRequestToken, mVerifier);
                Log.i(TAG, String.format("TOKEN: %s, SECRET: %s, ID: %s",
                        mAccessToken.getToken(),
                        mAccessToken.getTokenSecret(),
                        mAccessToken.getScreenName()));
            } catch (TwitterException e) {
                Log.e(TAG, String.format("error code: %d, message: %s",
                        e.getErrorCode(),
                        e.getErrorMessage()));
            }
            return mAccessToken;
        }

        @Override
        protected void onPostExecute(AccessToken response) {
            mProgressBar.dismiss();
            if(mAccessToken != null){
                if(mContext instanceof GetTwitterTokenTaskListener) {
                    ((GetTwitterTokenTaskListener) mContext).onTaskCompleted(mAccessToken);
                }
            }
            else{
                if(mContext instanceof GetTwitterTokenTaskListener) {
                    ((GetTwitterTokenTaskListener) mContext).onTaskFailed("twitter server error");
                }
            }
        }
    }
}
