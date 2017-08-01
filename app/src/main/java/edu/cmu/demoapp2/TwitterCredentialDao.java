package edu.cmu.demoapp2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Yu-Lun Tsai on 01/08/2017.
 */

public class TwitterCredentialDao {

    private Context mContext;

    public TwitterCredentialDao(Context context){
        mContext = context;
    }

    public boolean checkTwitterCredential(){

        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.app_sp),
                Context.MODE_PRIVATE);
        String token = sharedPref.getString(mContext.getString(R.string.app_twitter_token), "");
        return !token.isEmpty();
    }

    public TwitterCredential getTwitterCredential(){

        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.app_sp),
                Context.MODE_PRIVATE);
        String token = sharedPref.getString(mContext.getString(R.string.app_twitter_token), "");
        String secret = sharedPref.getString(mContext.getString(R.string.app_twitter_secret), "");

        if(token.isEmpty()){
            return null;
        }
        else{
            return new TwitterCredential(token, secret);
        }
    }

    public void saveTwitterCredential(String token, String secret){

        SharedPreferences sharedPref = mContext.getSharedPreferences(
                mContext.getString(R.string.app_sp),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(mContext.getString(R.string.app_twitter_token), token);
        editor.putString(mContext.getString(R.string.app_twitter_secret), secret);
        editor.commit();
    }
}
