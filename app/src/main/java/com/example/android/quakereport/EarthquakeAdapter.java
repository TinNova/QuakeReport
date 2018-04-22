package com.example.android.quakereport;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Tin on 09/05/2017.
 */

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param earthquakes A List of AndroidFlavor objects to display in a list
     */
    public EarthquakeAdapter(Activity context, ArrayList<Earthquake> earthquakes) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for three TextViews, the adapter is not going to
        // use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, earthquakes);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link Earthquake} object located at this position in the list
        Earthquake currentEarthquake = getItem(position);


        /** FORMATING THE MAGNITUDE TO A SINGLE DECIMAL
         * Assign the current Earthquake magnitude to a double "magnitude"
         * Use method DecimalFormat to format the value to one decimal place
         */

        // Find the TextView in the list_item.xml layout with the Magnitude strength
        TextView magnitudeTextView = (TextView) listItemView.findViewById(R.id.magnitude);

        double magnitude = currentEarthquake.getMagnitude();

        DecimalFormat formatter = new DecimalFormat("0.0");
        String magnitudeSingleDecimal = formatter.format(magnitude);

        // Assign the formatted magnitude to the mangitudeTextView
        magnitudeTextView.setText(magnitudeSingleDecimal);

        // Find the TextView in the list_item.xml layout with the primaryLocation and locationOffset
        TextView locationOffsetTextView = (TextView) listItemView.findViewById(R.id.locationOffset);
        TextView primaryLocationTextView = (TextView) listItemView.findViewById(R.id.primaryLocation);


        /** APPLYING COLOUR TO THE MAGNITUDE CIRCLE ELEMENT
         * Set the proper background color on the magnitude circle.
         * Fetch the background from the TextView, which is a GradientDrawable.
         */
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        // Colour is generated from the getMagnitudeColour helper method
        int magnitudeColor = getMagnitudeColour(currentEarthquake.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);


        /** SPLITTING THE LOCATION INTO TWO STRINGS, THE OFFSET LOCATION AND THE PRIMARY LOCATION
         * In this code, we will split the location text into a location offset (“74km NW of “)
         * and a primary location (“Rumoi, Japan”) and display the 2 Strings in 2 separate TextViews.
         * If there’s no location offset, use “Near the”, along with the primary location (“Pacific-Antarctic Ridge”).
         *
         * 1st: Put the currentEarthquake location into a String that we can edit "rawLocation"
         * 2nd: if Statement: if rawLocation contains "of" split rawLocation into two Strings, "part1" & "part2"
         * 3rd: else: if rawLocation doesn't contain "of", locationOffsetTextView.setText("Near the") and primaryLocationTextView.setText(rawLocation)
         */
        String rawLocation = currentEarthquake.getLocation();

        if (rawLocation.contains("of")) {

            // (?<=of) means to add the of to "part1"
            String[] parts = rawLocation.split("(?<=of)");
            String part1 = parts[0];
            String part2 = parts[1];

            // part2 contains the space after "of ", .trim removes the spaces before and after part2
            String part2SpacesTrimmedFromBeginningAndEnd = part2.trim();

            // Here we assign part1 & part2 to their TextViews
            locationOffsetTextView.setText(part1);
            primaryLocationTextView.setText(part2SpacesTrimmedFromBeginningAndEnd);

        } else { //It doesn't contain "of"

            locationOffsetTextView.setText("Near the");
            primaryLocationTextView.setText(rawLocation);
        }


        /** CONVERTING UNIX TIMEINMILLISECONDS INTO A READABLE DATE
         * Here we convert the current Earthquake timeInMilliseconds into a Date object using the Date constructor
         * and we name it dataObject.
         * The dataObject is then converted into a date and time format using the formatDate and formatTime methods
         * created below this class.
         */
        Date dateObject = new Date(currentEarthquake.getTimeInMilliseconds());

        /** Here we format the dateObject into a readable date format using the formatDate method created below this class */
        String formattedDate = formatDate(dateObject);

        // Find the TextView in the list_item.xml layout with the Date
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date);
        // Get the Date from the current Earthquake object and
        // set this text on the Location TextView
        dateTextView.setText(formattedDate);


        /** Here we format the dateObject into a readable time format using the formatTime method created below this class */
        String formattedTime = formatTime(dateObject);

        // Find the TextView in the list_item.xml layout with the Date
        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time);
        // Get the Date from the current Earthquake object and
        // set this text on the Location TextView
        timeTextView.setText(formattedTime);


        // Return the whole list item layout (containing 4 TextViews, Magnitude, Location, Date & Time)
        // so that it can be shown in the ListView
        return listItemView;
    }

    /** These two methods are created to help format the unix timeInMilliseconds into a readable format */
    /**
     * Return the formatted date string (i.e. "3 Mar, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLL, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);

    }

    /**
     * getMagnitudeColour helper method to get the magnitude colour for the relevant mangitude
     * @param magnitude
     * @return
     */
    private int getMagnitudeColour(double magnitude){

        // Here we create a variable that will hold the magnitude colour resource Id
        int magnitudeColourResourceId;

        // Here we convert the magnitude double into and int, and ensure we always round down to the nearest whole number (int)
        int magnitudeFloor = (int)Math.floor(magnitude);

        switch(magnitudeFloor){
            case 0:

            case 1:
                    magnitudeColourResourceId = R.color.magnitude1;
                    break;
            case 2:
                    magnitudeColourResourceId = R.color.magnitude2;
                    break;
            case 3:
                    magnitudeColourResourceId = R.color.magnitude3;
                    break;
            case 4:
                    magnitudeColourResourceId = R.color.magnitude4;
                    break;
            case 5:
                    magnitudeColourResourceId = R.color.magnitude5;
                    break;
            case 6:
                    magnitudeColourResourceId = R.color.magnitude6;
                    break;
            case 7:
                    magnitudeColourResourceId = R.color.magnitude7;
                    break;
            case 8:
                    magnitudeColourResourceId = R.color.magnitude8;
                    break;
            case 9:
                    magnitudeColourResourceId = R.color.magnitude9;
                    break;
            default:
                    magnitudeColourResourceId = R.color.magnitude10plus;
                    break;
        }

        return ContextCompat.getColor(getContext(), magnitudeColourResourceId);
    }

}
