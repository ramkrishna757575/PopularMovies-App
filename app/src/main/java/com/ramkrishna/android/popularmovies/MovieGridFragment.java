package com.ramkrishna.android.popularmovies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * The fragment that displays the list of movies in a GridView
 */
public class MovieGridFragment extends Fragment {

    GridView gridView; //Reference to the GridView in the fragment
    ArrayList<MovieObject> movieObjects = null; //List to hold the MovieObjects
    MovieAdapter movieAdapter; //Custom Adapter for the GridView
    private int currentPage = Constants.START_PAGE_NUMBER; //The Current Page that will be used to fetch data from the API
    private int maxPages = -1; //The Max pages that the API has
    private boolean isLoading = true; //Indicates if data is loading from the API
    private boolean isPrefsChanged = false; //Indicates if user changed the SortOrder Preference
    SharedPreferences sharedPrefs; //Reference to hold the SharedPreferences
    SharedPreferences.OnSharedPreferenceChangeListener listener; //Reference to a listener that monitors change in SharedPreferences

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = getView();
        if (view == null)
        {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_movie_grid, container, false);
            //Get the GridView from the Layout
            gridView = (GridView) view.findViewById(R.id.movie_grid_view);
            //Initialize List of MovieObjects with empty data in it
            movieObjects = new ArrayList<>();

            //Get the SharedPreference in the current application
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //Set a listener to listent to changes in SharedPreferences
            listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
                {
                    //If SharedPrefs changed then, empty the movie list, reset currentPage and the maxPages
                    movieObjects.clear();
                    currentPage = 1;
                    maxPages = -1;
                    //SharedPrefs changed
                    isPrefsChanged = true;
                    //Fetch New data form the API for the changed Preference
                    GetMoviesFromAPI getMoviesFromAPI = new GetMoviesFromAPI();
                    getMoviesFromAPI.execute();
                }
            };
            //Register the listener for SharedPrefs change behaviour
            sharedPrefs.registerOnSharedPreferenceChangeListener(listener);

            //Fetch the movies data from API for the first time, when Application is launched
            GetMoviesFromAPI getMoviesFromAPI = new GetMoviesFromAPI();
            getMoviesFromAPI.execute();

            //Set a scroll listener that monitors the scrolling of the GridView
            gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState)
                {
                    //DO NOTHING
                }

                //
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                {
                    /*
                    *Fetch the Next Page Data if :
                    * 1)nothing is already loading
                    * 2)the list now needs more data to show further movies
                    * */
                    if (!isLoading && (firstVisibleItem + visibleItemCount >= totalItemCount) && (maxPages > 0 && currentPage + 1 <= maxPages))
                    {
                        currentPage++;
                        GetMoviesFromAPI getMoviesFromAPI = new GetMoviesFromAPI();
                        getMoviesFromAPI.execute();
                    }
                }
            });
        }
        return view;
    }

    //The internal class that handles the data fetching from the API
    private class GetMoviesFromAPI extends AsyncTask {

        //Reference to hold the NetworkDataManager Object
        NetworkDataManager networkDataManager;
        String rawJSON, url;

        //Constructor that initializes the URL to be used to fetch the data
        GetMoviesFromAPI()
        {
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
        protected void onPreExecute()
        {
            super.onPreExecute();
            isLoading = true; //Set to true to indicate that data loading is going to start
            networkDataManager = new NetworkDataManager(getContext(), url);
        }

        //Tasks to be done in background thread. Here we are fetching data from API using NetworkDataManager Object
        @Override
        protected Object doInBackground(Object[] params)
        {
            networkDataManager.fetchRawString();
            return null;
        }

        //Tasks to be done after data loading finished in background thread
        @Override
        protected void onPostExecute(Object o)
        {
            super.onPostExecute(o);
            //Get the raw JSON data
            rawJSON = networkDataManager.getRawData();
            //If we received raw JSON String, then do further processing
            if (rawJSON != null && !rawJSON.isEmpty())
            {
                try
                {
                    //Get an instance of the MovieJsonParser class
                    MovieJsonParser movieJsonParser = new MovieJsonParser();
                    //Add the list of items received from the parser to the current list
                    movieObjects.addAll(movieJsonParser.parseMovieJson(rawJSON));

                    //set the Max Page Limit if not set already
                    if (maxPages < 1)
                    {
                        maxPages = movieJsonParser.getPageLimit(rawJSON);
                    }

                    //Reset the adapter to a new one if it isn't already created or if Preference has changed
                    if (movieAdapter == null || isPrefsChanged)
                    {
                        movieAdapter = new MovieAdapter(getContext(), movieObjects);
                        gridView.setAdapter(movieAdapter);
                        isPrefsChanged = false; //Now task related to changed Prefs done. So reset isPrefsChanged back to false
                    } else
                    {
                        //Notify the adapter that the data has changed, so that the GridView can be updated accordingly
                        movieAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            isLoading = false;  //Finally data loading and GridView Updation done. So reset isLoading back to false
        }
    }
}