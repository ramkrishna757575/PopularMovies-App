package com.ramkrishna.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by ramkr on 17-Jul-16.
 */
public class GetTrailersFromApi extends AsyncTask {

    Context mContext;
    private GetTrailersFromApiListener delegate;
    NetworkDataManager networkDataManager;
    String rawJSON, url;
    ArrayList<MovieTrailerOrReviewObject> movieTrailerObjects;
    ArrayList<String> movieTrailerNames;
    ArrayList<String> movieTrailerKeys;

    GetTrailersFromApi(Context context, GetTrailersFromApiListener listener, int movieId) {
        mContext = context;
        delegate = listener;
        Uri trailerListUri = Uri.parse(Constants.URI_BASE_MOVIE_API_URI).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(Constants.URI_PARAM_MOVIE_VIDEOS)
                .appendQueryParameter(Constants.URI_PARAM_API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                .build();
        url = trailerListUri.toString();
        movieTrailerNames = new ArrayList<>();
        movieTrailerKeys = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        networkDataManager = new NetworkDataManager(mContext, url);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        networkDataManager.fetchRawString();
        //Get the raw JSON data
        rawJSON = networkDataManager.getRawData();
        try {
            //Get an instance of the MovieJsonParser class
            MovieJsonParser movieJsonParser = new MovieJsonParser();
            //Add the list of items received from the parser to the current list
            movieTrailerObjects = movieJsonParser.parseTrailerJson(rawJSON);
            createTrailerNamesAndKeys();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        delegate.getTrailers(movieTrailerKeys, movieTrailerNames);
    }

    public interface GetTrailersFromApiListener {
        void getTrailers(ArrayList<String> movieTrailerKeys, ArrayList<String> movieTrailerNames);
    }

    private void createTrailerNamesAndKeys() {
        for (int i = 0; i < movieTrailerObjects.size(); i++) {
            movieTrailerNames.add(movieTrailerObjects.get(i).getNameOrAuthor());
            movieTrailerKeys.add(movieTrailerObjects.get(i).getKeyOrContent());
        }
    }
}
