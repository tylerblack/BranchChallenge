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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    final String TAG = "BranchChallenge";
    final String BASE_URL = "https://api.bitcoinaverage.com/ticker/global/";
    final int NUM_CURRENCIES = 4;

    //Timer duration increased to 20 from 10 specified to reduce 429 errors
    final int REFRESH_DELAY = 20;

    private String[] currencyCodes;

    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        mTimer = new Timer();
        currencyCodes = new String[NUM_CURRENCIES];
        currencyCodes[0] = getResources().getString(R.string.us_code);
        currencyCodes[1] = getResources().getString(R.string.kenyan_code);
        currencyCodes[2] = getResources().getString(R.string.tanzanian_code);
        currencyCodes[3] = getResources().getString(R.string.ugandan_code);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);

        TimerTask requestTimer = new TimerTask() {
            @Override
            public void run() {
                for (String code : currencyCodes) {
                    // Request a string response from the provided URL.
                    Uri.Builder builder = new Uri.Builder();
                    Uri url = builder.path(BASE_URL).appendPath(code).build();
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url.getPath(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d(TAG, response);
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

        mTimer.schedule(requestTimer, 0,  REFRESH_DELAY*1000);

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
