package edu.cmu.demoapp2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.List;


public class MainActivity extends AppCompatActivity implements SearchOnYelpTaskListener {

    private static final String TAG = "YELP_DEMO";

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadUIComponents();
        new SearchOnYelpTask(this).execute();
    }

    private void loadUIComponents(){
        mListView = (ListView) findViewById(R.id.listView);
    }

    @Override
    public void onTaskCompleted(List<RestaurantInfoCell> results) {
        Log.i(TAG, "yelp search is completed");

        final RestaurantInfoListAdapter adapter = new RestaurantInfoListAdapter(this, results);

        // Attach the adapter to a ListView
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskFailed(String message) {
        Log.i(TAG, "yelp search failed");
    }
}
