package com.ramkrishna.android.popularmovies;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * <p/>
 * The fragment that displays the list of movies in a GridView
 */
public class MovieGridFragment extends Fragment implements GetMoviesFromApi.GetMoviesFromApiListener {

    GridView gridView; //Reference to the GridView in the fragment
    ArrayList<MovieObject> movieObjects = new ArrayList<>(); //List to hold the MovieObjects
    MovieAdapter movieAdapter; //Custom Adapter for the GridView
    private int currentPage = Constants.START_PAGE_NUMBER; //The Current Page that will be used to fetch data from the API
    private int maxPages = -1; //The Max pages that the API has
    private boolean isLoading = true; //Indicates if data is loading from the API

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = getView();
        if (view == null) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_movie_grid, container, false);
            //Get the GridView from the Layout
            gridView = (GridView) view.findViewById(R.id.movie_grid_view);

            //Fetch the movies data from API for the first time, when Application is launched
            GetMoviesFromApi getMoviesFromAPI = new GetMoviesFromApi(getContext(), this);
            getMoviesFromAPI.execute();

            //Set a scroll listener that monitors the scrolling of the GridView
            gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    //DO NOTHING
                }

                //
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    /*
                    *Fetch the Next Page Data if :
                    * 1)nothing is already loading
                    * 2)the list now needs more data to show further movies
                    * */
                    if (!isLoading && (firstVisibleItem + visibleItemCount >= totalItemCount) && (maxPages > 0 && currentPage + 1 <= maxPages)) {
                        currentPage++;
                        GetMoviesFromApi getMoviesFromAPI = new GetMoviesFromApi(getContext(), MovieGridFragment.this);
                        getMoviesFromAPI.execute();
                    }
                }
            });
        }
        return view;
    }

    @Override
    public int setCurrentPage() {
        return currentPage;
    }

    @Override
    public void getIsLoading(boolean loading) {
        isLoading = loading;
    }

    @Override
    public void getMaxPagesCount(int maxpages) {
        maxPages = maxpages;
    }

    @Override
    public int setMaxPagesCount() {
        return maxPages;
    }

    @Override
    public void notifyForDataChange(ArrayList<MovieObject> newMoviesList) {
        //Reset the adapter to a new one if it isn't already created or if Preference has changed
        movieObjects.addAll(newMoviesList);
        if (movieAdapter == null) {
            movieAdapter = new MovieAdapter(getContext(), movieObjects);
            gridView.setAdapter(movieAdapter);
        } else {
            //Notify the adapter that the data has changed, so that the GridView can be updated accordingly
            movieAdapter.notifyDataSetChanged();
        }
    }
}