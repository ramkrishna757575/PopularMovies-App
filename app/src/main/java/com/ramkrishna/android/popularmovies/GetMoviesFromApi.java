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
    private GetMoviesFromApiListener delegate;
    //Reference to hold the NetworkDataManager Object
    NetworkDataManager networkDataManager;
    String rawJSON, url;
    SharedPreferences sharedPrefs;
    ArrayList<MovieObject> movieObjects = null;

    //Constructor that initializes the URL to be used to fetch the data
    GetMoviesFromApi(Context context, GetMoviesFromApiListener listener) {
        this.context = context;
        delegate = listener;
        //Get the SharedPreference in the current application
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrderPrefs = sharedPrefs.getString(context.getString(R.string.pref_sort_order_key), context.getString(R.string.settings_order_by_default_val));
        String sortOrder = sortOrderPrefs.equals(context.getString(R.string.settings_order_by_default_val)) ? Constants.URI_PARAM_SORT_ORDER_POPULARITY : Constants.URI_PARAM_SORT_ORDER_RATING;
        Uri movieListUri = Uri.parse(Constants.URI_BASE_MOVIE_API_URI).buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(Constants.URI_PARAM_API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                .appendQueryParameter(Constants.URI_PARAM_CURRENT_PAGE, Integer.toString(listener.setCurrentPage()))
                .build();
        url = movieListUri.toString();
        movieObjects = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        delegate.getIsLoading(true); //Set to true to indicate that data loading is going to start
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
                if (delegate.setMaxPagesCount() < 1) {
                    delegate.getMaxPagesCount(movieJsonParser.getPageLimit(rawJSON));
                }
                // notify the delegate's adapter for the data change
                delegate.notifyForDataChange(movieObjects);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        delegate.getIsLoading(false); //Set to false to indicate that data loading is finished
    }

    public interface GetMoviesFromApiListener {
        int setCurrentPage();

        void getIsLoading(boolean isLoading);

        void getMaxPagesCount(int maxPages);

        int setMaxPagesCount();

        void notifyForDataChange(ArrayList<MovieObject> newMoviesList);
    }
}