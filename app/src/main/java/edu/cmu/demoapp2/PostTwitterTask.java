package edu.cmu.demoapp2;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * Created by Yu-Lun Tsai on 31/07/2017.
 */

public class PostTwitterTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = "YELP_DEMO";

    private Context mContext;
    static private Twitter mTwitter = null;
    private String mMessage = "";

    public PostTwitterTask(Context context) {
        mContext = context;

        if(mTwitter == null){
            mTwitter = new TwitterFactory().getInstance();
            mTwitter.setOAuthConsumer(BuildConfig.TWITTER_API_KEY, BuildConfig.TWITTER_API_SECRET);

            TwitterCredentialDao dao = new TwitterCredentialDao(mContext);
            TwitterCredential credential = dao.getTwitterCredential();
            mTwitter.setOAuthAccessToken(new AccessToken(credential.accessToken, credential.accessSecret));
        }
    }

    @Override
    protected Void doInBackground(String... strings) {
        if(strings.length > 0){
            String latestStatus = strings[0];
            try {
                twitter4j.Status status = mTwitter.updateStatus(latestStatus);
                Log.i(TAG, String.format("Tweet is posted at %s", status.getCreatedAt()));
            } catch (TwitterException e) {
                e.printStackTrace();
                mMessage = e.getMessage();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(mContext instanceof PostTwitterTaskListener){
            PostTwitterTaskListener listener = (PostTwitterTaskListener)mContext;
            if(mMessage.isEmpty()){
                listener.onPostTaskCompleted();
            }
            else{
                listener.onPostTaskFailed(mMessage);
            }
        }
    }
}
