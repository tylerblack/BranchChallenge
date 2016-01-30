package com.example.tyler.branchchallenge;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    final String TAG = "BranchChallenge";
    final String BASE_URL = "https://api.bitcoinaverage.com/ticker/global/";
    final int NUM_CURRENCIES = 4;

    //Timer duration increased to 20 from 10 specified to reduce 429 errors
    final int REFRESH_DELAY = 20;

    private String[] mCurrencyCodes;
    private BitcoinJson[] mRecentResponses;

    TextView mCurrencyName;
    TextView mCurrencyAmount;
    TextView mTimestamp;

    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCurrencyName = (TextView) findViewById(R.id.currency_name);
        mCurrencyAmount = (TextView) findViewById(R.id.currency_amount);
        mTimestamp = (TextView) findViewById(R.id.currency_timestamp);

    }

    @Override
    protected void onStart(){
        super.onStart();
        mTimer = new Timer();
        mRecentResponses = new BitcoinJson[NUM_CURRENCIES];
        mCurrencyCodes = new String[NUM_CURRENCIES];
        mCurrencyCodes[0] = getResources().getString(R.string.us_code);
        mCurrencyCodes[1] = getResources().getString(R.string.kenyan_code);
        mCurrencyCodes[2] = getResources().getString(R.string.tanzanian_code);
        mCurrencyCodes[3] = getResources().getString(R.string.ugandan_code);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);

        TimerTask requestTimer = new TimerTask() {
            @Override
            public void run() {
                for(int i = 0; i < mCurrencyCodes.length; i++){
                    String code = mCurrencyCodes[i];
                    // Request a string response from the provided URL.
                    Uri.Builder builder = new Uri.Builder();
                    Uri url = builder.path(BASE_URL).appendPath(code).build();
                    final int finalI = i;
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url.getPath(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Gson gson = new Gson();
                                    BitcoinJson newResponse = gson.fromJson(response, BitcoinJson.class);
                                    mRecentResponses[finalI] = newResponse;

                                    mCurrencyAmount.setText(String.valueOf(mRecentResponses[0].ask));
                                    mTimestamp.setText( String.valueOf(mRecentResponses[0].timestamp));
                                    mCurrencyName.setText(mCurrencyCodes[0]);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            //TODO: display error based on response
                            Log.d(TAG, "code is not valid");
                        }

                    });
                    // Add the request to the RequestQueue.
                    queue.add(stringRequest);
                }
            }
        };

        mTimer.schedule(requestTimer, 0, REFRESH_DELAY * 1000);


    }

    @Override
    protected void onPause(){
        super.onPause();
        mTimer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
