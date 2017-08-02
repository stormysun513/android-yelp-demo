package edu.cmu.demoapp2;

import android.content.Context;
import android.os.AsyncTask;

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

public class SearchOnYelpTask extends AsyncTask<YelpSearchParameter, Void, Void> {

    private static final String TAG = "YELP_DEMO";

    private Context mContext;
    private static YelpFusionApiFactory sYelpApiFactory = null;
    private static YelpFusionApi sYelpApi = null;

    private String mMessage = "";
    private List<RestaurantInfoCell> mResults;

    public SearchOnYelpTask(Context context){
        mContext = context;
        if(sYelpApiFactory == null){
            sYelpApiFactory = new YelpFusionApiFactory();
        }
    }

    @Override
    protected Void doInBackground(YelpSearchParameter... params) {

        if(sYelpApi == null){
            try {
                sYelpApi = sYelpApiFactory.createAPI(BuildConfig.YELP_API_KEY,
                        BuildConfig.YELP_API_SECRET);
            } catch (IOException e) {
                e.printStackTrace();
                mMessage = e.getMessage();
                return null;
            }
        }

        try {
            if(params.length > 0){
                // TODO: prepare search params
                mResults = blockingSearch(params[0]);
            }
            else{
                mResults = blockingSearch(null);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            mMessage = e.getMessage();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if(mContext instanceof SearchOnYelpTaskListener) {
            SearchOnYelpTaskListener listener = (SearchOnYelpTaskListener) mContext;
            if (mMessage.isEmpty()) {
                listener.onSearchTaskCompleted(mResults);
            } else {
                listener.onSearchTaskFailed(mMessage);
            }
        }
    }

    private List<RestaurantInfoCell> blockingSearch(YelpSearchParameter param)
            throws InterruptedException {

        final List<RestaurantInfoCell> results = new ArrayList<>();
        final Object syncObject = new Object();

        Map<String, String> map = new HashMap<>();
        if(param == null){
            // default location is set around the campus
//            map.put("term", "indian");
//            map.put("latitude", "40.581140");
//            map.put("longitude", "-111.914184");
            map.put("latitude", "40.442570");
            map.put("longitude", "-79.945676");
            map.put("limit", "50");
        }
        else{
            map.put("term", "chinese");
            map.put("latitude", Double.toString(param.location.getLatitude()));
            map.put("longitude", Double.toString(param.location.getLongitude()));
            map.put("limit", "50");
        }

        Call<SearchResponse> call = sYelpApi.getBusinessSearch(map);
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {

            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                SearchResponse searchResponse = response.body();
                List<Business> restaurants = searchResponse.getBusinesses();
                for(Business business: restaurants){
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
