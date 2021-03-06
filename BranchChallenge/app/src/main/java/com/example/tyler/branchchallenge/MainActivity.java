package com.example.tyler.branchchallenge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    TabLayout mTabLayout;

    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mCurrencyAmount = (TextView) findViewById(R.id.currency_amount);
        mCurrencyName = (TextView) findViewById(R.id.currency_name);
        mTimestamp = (TextView) findViewById(R.id.currency_timestamp);

    }

    @Override
    protected void onStart(){
        super.onStart();
        mTimer = new Timer();
        mRecentResponses = new BitcoinJson[NUM_CURRENCIES];
        mCurrencyCodes = new String[NUM_CURRENCIES];

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String c0 = prefs.getString("code_1", getResources().getString(R.string.us_code));
        String c1 = prefs.getString("code_2", getResources().getString(R.string.kenyan_code));
        String c2 = prefs.getString("code_3", getResources().getString(R.string.tanzanian_code));
        String c3 = prefs.getString("code_4", getResources().getString(R.string.ugandan_code));
        mCurrencyCodes[0] = c0.toUpperCase();
        mTabLayout.addTab(mTabLayout.newTab().setText(c0));
        mCurrencyCodes[1] = c1.toUpperCase();
        mTabLayout.addTab(mTabLayout.newTab().setText(c1));
        mCurrencyCodes[2] = c2.toUpperCase();
        mTabLayout.addTab(mTabLayout.newTab().setText(c2));
        mCurrencyCodes[3] = c3.toUpperCase();
        mTabLayout.addTab(mTabLayout.newTab().setText(c3));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               updateInformation();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);
        updateInformation();

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
                                    updateInformation();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if(error.networkResponse.statusCode == 404){
                                BitcoinJson newResponse = new BitcoinJson();
                                newResponse.ask = 0;
                                newResponse.timestamp = "Country not found";
                                mRecentResponses[finalI] = newResponse;
                                updateInformation();
                            }
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
        mTabLayout.removeAllTabs();
    }

    public void updateInformation(){
        int position =  mTabLayout.getSelectedTabPosition();
        if( position >= 0 && position < mRecentResponses.length && mRecentResponses[position] != null) {
            mCurrencyAmount.setText(String.valueOf(mRecentResponses[position].ask));
            mCurrencyName.setText(mCurrencyCodes[position]);
            mTimestamp.setText(mRecentResponses[position].timestamp);
        }
        else{
            mCurrencyName.setText(getResources().getString(R.string.error));
            mCurrencyAmount.setText(getResources().getString(R.string.error));
            mTimestamp.setText(getResources().getString(R.string.error));
        }
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
            Intent intent = new Intent(this, PreferenceWithHeaders.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
