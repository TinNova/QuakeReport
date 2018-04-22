package com.example.android.quakereport;

/**
 * Created by Tin on 09/05/2017.
 */

public class Earthquake {

    /** The magnitude level of the earthquake */
    private double mMagnitude;

    /** The location or city the earthquake occurred in */
    private String mLocation;

    /** The time of the earthquake in unix seconds */
    private long mTimeInMilliseconds;

    /** The URL that leads to the website with more detail about the earthquake*/
    private String mWebsite;

    /**
     * Create a new Earthquake Object made up of the three variable we want to display
     *
     * @param eMagnitude e represents Earthquake, eMagnitude is the magnitude of the earthquake
     * @param eLocation e represents Earthquake, eLocation is the locaiton of the earthquake
     * @param eTimeInMilliseconds e represents Earthquake, eDate is the date of the earthquake
     * @param eWebsite e represents Earthquake, eWebsite is URL that contains more information about the earthquake
     */
    public Earthquake(double eMagnitude, String eLocation, long eTimeInMilliseconds, String eWebsite){

        mMagnitude = eMagnitude;
        mLocation = eLocation;
        mTimeInMilliseconds = eTimeInMilliseconds;
        mWebsite = eWebsite;

    }

    /**
     *
     * Method that returns the Magnitude
     */
    public double getMagnitude() {
        return mMagnitude;
    }

    /**
     *
     * Method that returns the Location
     */
    public String getLocation() {
        return mLocation;
    }

    /**
     *
     * Method that returns the TimeInMilliseconds
     */
    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    /**
     *
     * Method that returns the Website
     */
    public String getWebsite() {
        return mWebsite;
    }


}
