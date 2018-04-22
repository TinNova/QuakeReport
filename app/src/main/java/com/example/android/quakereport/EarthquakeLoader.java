package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTash to perform the
 * network request to the given URL
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {

    /** Tag for log messages */
    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    /** Query URL*/
    private String mUrl;

    /**
     * Constructs a new {@link EarthquakeLoader}
     *
     * @param context of the activity
     * @param url url of the USGS API
     */
    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    /** This onStartLoading method and forceLoad is required */
    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: EarthquakeLoader, onStartLoading() called");

        forceLoad();
    }

    /**
     * This is on a background thread.
     * @return
     */
    @Override
    public List<Earthquake> loadInBackground() {
        Log.i(LOG_TAG, "TEST: EarthquakeLoader, loadInBackground() called");
        if (mUrl == null) {
            return null;
        } // else

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;
    }
}
