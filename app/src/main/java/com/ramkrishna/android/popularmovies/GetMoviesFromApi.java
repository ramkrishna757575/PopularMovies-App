package com.ramkrishna.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by ramkr on 16-Jul-16.
 */
public class GetMoviesFromApi extends AsyncTask {

    private Context context;
    private GetMoviesFromApiListener listener;
    //Reference to hold the NetworkDataManager Object
    NetworkDataManager networkDataManager;
    String rawJSON, url;
    SharedPreferences sharedPrefs;
    ArrayList<MovieObject> movieObjects = null;

    //Constructor that initializes the URL to be used to fetch the data
    GetMoviesFromApi(Context context, GetMoviesFromApiListener listener) {
        this.context = context;
        this.listener = listener;
        //Get the SharedPreference in the current application
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrderPrefs = sharedPrefs.getString(context.getString(R.string.pref_sort_order_key), context.getString(R.string.settings_order_by_default_val));
        String sortOrder = sortOrderPrefs.equals(context.getString(R.string.settings_order_by_default_val)) ? Constants.URI_PARAM_SORT_ORDER_POPULARITY : Constants.URI_PARAM_SORT_ORDER_RATING;
        Uri movieListUri = Uri.parse(Constants.URI_BASE_MOVIE_API_URI).buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(Constants.URI_PARAM_API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                .appendQueryParameter(Constants.URI_PARAM_CURRENT_PAGE, Integer.toString(listener.getListenerCurrentPage()))
                .build();
        url = movieListUri.toString();
        movieObjects = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.setListenerIsLoading(true); //Set to true to indicate that data loading is going to start
        networkDataManager = new NetworkDataManager(context, url);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        networkDataManager.fetchRawString();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        //Get the raw JSON data
        rawJSON = networkDataManager.getRawData();
        //If we received raw JSON String, then do further processing
        if (rawJSON != null && !rawJSON.isEmpty()) {
            try {
                //Get an instance of the MovieJsonParser class
                MovieJsonParser movieJsonParser = new MovieJsonParser();
                //Add the list of items received from the parser to the current list
                movieObjects = movieJsonParser.parseMovieJson(rawJSON);

                //set the Max Page Limit if not set already
                if (listener.getListenerMaxPagesCount() < 1) {
                    listener.setListenerMaxPagesCount(movieJsonParser.getPageLimit(rawJSON));
                }
                // notify the listener's adapter for the data change
                listener.notifyForDataChange(movieObjects);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        listener.setListenerIsLoading(false); //Set to false to indicate that data loading is finished
    }

    public interface GetMoviesFromApiListener {
        int getListenerCurrentPage();

        void setListenerIsLoading(boolean isLoading);

        void setListenerMaxPagesCount(int maxPages);

        int getListenerMaxPagesCount();

        void notifyForDataChange(ArrayList<MovieObject> newMoviesList);
    }
}

//The previously used internal class that handled the data fetching from the API
/*private class GetMoviesFromAPI extends AsyncTask {

    //Reference to hold the NetworkDataManager Object
    NetworkDataManager networkDataManager;
    String rawJSON, url;

    //Constructor that initializes the URL to be used to fetch the data
    GetMoviesFromAPI() {
        String sortOrderPrefs = sharedPrefs.getString(getString(R.string.pref_sort_order_key), getString(R.string.settings_order_by_default_val));
        String sortOrder = sortOrderPrefs.equals(getString(R.string.settings_order_by_default_val)) ? Constants.URI_PARAM_SORT_ORDER_POPULARITY : Constants.URI_PARAM_SORT_ORDER_RATING;
        Uri movieListUri = Uri.parse(Constants.URI_BASE_MOVIE_API_URI).buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(Constants.URI_PARAM_API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                .appendQueryParameter(Constants.URI_PARAM_CURRENT_PAGE, Integer.toString(currentPage))
                .build();
        url = movieListUri.toString();
    }

    //Tasks to be done before background thread is created to fetch data
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isLoading = true; //Set to true to indicate that data loading is going to start
        networkDataManager = new NetworkDataManager(getContext(), url);
    }

    //Tasks to be done in background thread. Here we are fetching data from API using NetworkDataManager Object
    @Override
    protected Object doInBackground(Object[] params) {
        networkDataManager.fetchRawString();
        return null;
    }

    //Tasks to be done after data loading finished in background thread
    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        //Get the raw JSON data
        rawJSON = networkDataManager.getRawData();
        //If we received raw JSON String, then do further processing
        if (rawJSON != null && !rawJSON.isEmpty()) {
            try {
                //Get an instance of the MovieJsonParser class
                MovieJsonParser movieJsonParser = new MovieJsonParser();
                //Add the list of items received from the parser to the current list
                movieObjects.addAll(movieJsonParser.parseMovieJson(rawJSON));

                //set the Max Page Limit if not set already
                if (maxPages < 1) {
                    maxPages = movieJsonParser.getPageLimit(rawJSON);
                }

                //Reset the adapter to a new one if it isn't already created or if Preference has changed
                if (movieAdapter == null || isPrefsChanged) {
                    movieAdapter = new MovieAdapter(getContext(), movieObjects);
                    gridView.setAdapter(movieAdapter);
                    isPrefsChanged = false; //Now task related to changed Prefs done. So reset isPrefsChanged back to false
                } else {
                    //Notify the adapter that the data has changed, so that the GridView can be updated accordingly
                    movieAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        isLoading = false;  //Finally data loading and GridView Updation done. So reset isLoading back to false
    }
}*/