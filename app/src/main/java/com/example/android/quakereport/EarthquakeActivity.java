/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.R.attr.data;
import static android.R.attr.visibility;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();

    /** URL for earthquake data from the USGS API */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query";

    /** Adapter for the list of earthquakes */
    private EarthquakeAdapter mAdapter;

    /** ID FOR THE LOADER WHICH HANDLES THE URL CONNECTION
     * We need to specify an ID for our loader. This is only really relevant if we have multiple
     * loaders in the same activity. We can choose any integer, so we will choose the number 1
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;

    // TextView that displays message "No Earthquakes Found" when the API doesn't have any data
    private TextView noDataOrNoInternetTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        Log.i(LOG_TAG, "TEST: Earthquake Activity, onCreate() called");

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // TextView that displays message "No Earthquakes Found" when the API doesn't have any data
        // We set it to be empty until so that the message doesn't appear before the API has been checked
        noDataOrNoInternetTextView = (TextView) findViewById(R.id.noDataOrNoInternet);
        earthquakeListView.setEmptyView(noDataOrNoInternetTextView);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mAdapter);


        /** CLICK EARTHQUAKE TO GO TO USGS WEBSITE */
        // Here we have a special type onClickListener designed for lists that we attached to the earthquakeListView
        // The OnItemClickListener has a parameter called position which knows which row a user clicked on
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long l) {

                // Find the current earthquake that was clicked on. (i.e if they click on the 3rd row of the list it returns the 3rd Word object)
                Earthquake currentEarthquake = mAdapter.getItem(position);

                // Create a Uri variable called earthquakeUri and assign the website to it using the getWebsite method
                Uri earthquakeUri = Uri.parse(currentEarthquake.getWebsite());

                // This intent sends the user to their web browser and opens the earthquakeUri
                Intent i = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                startActivity(i);

            }
        });

        /** CHECK INTERNET CONNECTION BEFORE DOWNLOADING DATA */
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active defailt data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // if there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            /** LOADER STARTS HERE
             * Get a reference to the LoadManager, in order to interact with the loader*/
            LoaderManager loaderManager = getLoaderManager();

            /**
             * Initialise the loader. Pass in the int ID constant that was defined globally.
             * Two parameters are required, for the Bundle pass in "null"
             * for the LoaderCallbacks  pass in "this" (aka this activity, which is valid
             * because this activity implements the LoaderCallbacks interface).
             *
             * This will start the onCreateLoader, which you can see requires an int and Bundle
             * as it's parameters, which we have established here
             */
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            // Otherwise, display a no Internet Connection message
            // First, hide loading indicator,
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(ProgressBar.GONE);
            noDataOrNoInternetTextView.setText(R.string.no_internet_connection);
        }
    }


    /**
     * We need onCreateLoader() for when the LoaderManager has determined that the loader
     * with our specified ID isn't running, therefore we should create a new one
     */
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "TEST: Earthquake Activity, onCreateLoader() called");

        // Create a new loader for the given URL
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", "time");

        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        Log.i(LOG_TAG, "TEST: Earthquake Activity, onLoadFinished() called");

        // Set TextView to display "No Earthquakes Found..."
        noDataOrNoInternetTextView.setText(R.string.no_earthquakes);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.GONE);

        // Clear the adapter of previous earthquake data, so it doesn't take up memory
        mAdapter.clear();

        // If earthquakes IS NOT null and IS NOT Empty, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        Log.i(LOG_TAG, "TEST: Earthquake Activity, onLoaderReset() called");
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }




    /** CREATING THE MENU BUTTON **
     * First we inflate the menu we created in res.menu.main.xml (which contains the menu
     * icon image) */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /** Here we establish an Intent to go to the SettingsActivity when the menu is clicked on
     * by the user */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // If the settings_menu_button we just inflated in clicked by the user, start the Intent
        if (id == R.id.settings_menu_button) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
