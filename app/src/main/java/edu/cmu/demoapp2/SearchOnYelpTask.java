package edu.cmu.demoapp2;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.yelp.fusion.client.connection.YelpFusionApi;
import com.yelp.fusion.client.connection.YelpFusionApiFactory;
import com.yelp.fusion.client.models.Business;
import com.yelp.fusion.client.models.SearchResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Yu-Lun Tsai on 31/07/2017.
 */

public class SearchOnYelpTask extends AsyncTask<String, Void, Void> {

    private static final String TAG = "YELP_DEMO";

    private Context mContext;

    private static YelpFusionApiFactory apiFactory = null;
    private static YelpFusionApi yelpFusionApi = null;

    private String mMessage = "";
    private List<RestaurantInfoCell> mResults;

    public SearchOnYelpTask(Context context){
        mContext = context;
        if(apiFactory == null){
            apiFactory = new YelpFusionApiFactory();
        }
    }

    @Override
    protected Void doInBackground(String... strings) {

        if(yelpFusionApi == null){
            try {
                yelpFusionApi = apiFactory.createAPI(BuildConfig.YELP_API_KEY,
                        BuildConfig.YELP_API_SECRET);
            } catch (IOException e) {
                e.printStackTrace();
                mMessage = e.getMessage();
                return null;
            }
        }

        if(strings.length > 0){
            // TODO: prepare search params
        }
        else{
            try {
                mResults = blockingSearch();
            } catch (InterruptedException e) {
                e.printStackTrace();
                mMessage = e.getMessage();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(mContext instanceof SearchOnYelpTaskListener) {
            SearchOnYelpTaskListener listener = (SearchOnYelpTaskListener) mContext;
            if (mMessage.isEmpty()) {
                listener.onTaskCompleted(mResults);
            } else {
                listener.onTaskFailed(mMessage);
            }
        }
    }

    private List<RestaurantInfoCell> blockingSearch() throws InterruptedException {

        final List<RestaurantInfoCell> results = new ArrayList<>();
        final Object syncObject = new Object();

        Map<String, String> params = new HashMap<>();
        params.put("term", "indian food");
        params.put("latitude", "40.581140");
        params.put("longitude", "-111.914184");
        params.put("limit", "50");

        Call<SearchResponse> call = yelpFusionApi.getBusinessSearch(params);
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {

            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
                List<Business> restaurants = searchResponse.getBusinesses();
                for(Business business: restaurants){
//                    Log.d(TAG, String.format("name: %s, url: %s", business.getName(), business.getImageUrl()));
                    results.add(new RestaurantInfoCell(
                            business.getName(),
                            business.getLocation().getAddress1(),
                            business.getImageUrl())
                    );
                }
                synchronized (syncObject){
                    syncObject.notify();
                }
            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                mMessage = t.getMessage();
                synchronized (syncObject){
                    syncObject.notify();
                }
            }
        };

        call.enqueue(callback);
        synchronized (syncObject){
            syncObject.wait();
        }
        return results;
    }
}
