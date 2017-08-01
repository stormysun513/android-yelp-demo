package edu.cmu.demoapp2;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean res = super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items, menu);
        return res;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_tweet:
                // generate a dialogue for twitter post input
                getInputFromUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadUIComponents(){
        mListView = (ListView) findViewById(R.id.listView);
    }

    private void getInputFromUser(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        final EditText edittext = new EditText(this);
        alert.setMessage("Enter your message");
        alert.setTitle("Post A Tweet");
        alert.setIcon(R.drawable.twitter_logo_blue);
        alert.setView(edittext);

        alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                String input = edittext.getText().toString();
                Log.i(TAG, String.format("Input: %s", input));
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });
        alert.show();
    }

    @Override
    public void onTaskCompleted(List<RestaurantInfoCell> results) {

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
