package com.ramkrishna.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by ramkr on 18-Jul-16.
 */
public class GetReviewsFromApi extends AsyncTask {

    Context mContext;
    private GetReviewsFromApiListener delegate;
    NetworkDataManager networkDataManager;
    String rawJSON, url;
    ArrayList<MovieTrailerOrReviewObject> movieTrailerObjects;
    ArrayList<String> reviewAuthors;
    ArrayList<String> reviewContents;

    GetReviewsFromApi(Context context, GetReviewsFromApiListener listener, int movieId) {
        mContext = context;
        delegate = listener;
        Uri trailerListUri = Uri.parse(Constants.URI_BASE_MOVIE_API_URI).buildUpon()
                .appendPath(Integer.toString(movieId))
                .appendPath(Constants.URI_PARAM_MOVIE_REVIEWS)
                .appendQueryParameter(Constants.URI_PARAM_API_KEY, BuildConfig.MOVIE_DB_API_KEY)
                .appendQueryParameter(Constants.URI_PARAM_CURRENT_PAGE, Integer.toString(delegate.currentPage()))
                .build();
        url = trailerListUri.toString();
        reviewAuthors = new ArrayList<>();
        reviewContents = new ArrayList<>();
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
            movieTrailerObjects = movieJsonParser.parseReviewJson(rawJSON);
            createAuthorsAndContents();
            //parse the max page count and set it
            if (delegate.maxPage() == -1) {
                int maxPages = movieJsonParser.getPageLimit(rawJSON);
                delegate.onMaxPageObtained(maxPages);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        delegate.getReviews(reviewAuthors, reviewContents);
    }

    public interface GetReviewsFromApiListener {
        void getReviews(ArrayList<String> author, ArrayList<String> content);

        void onMaxPageObtained(int maxPage);

        int currentPage();

        int maxPage();
    }

    private void createAuthorsAndContents() {
        for (int i = 0; i < movieTrailerObjects.size(); i++) {
            reviewAuthors.add(movieTrailerObjects.get(i).getNameOrAuthor());
            reviewContents.add(movieTrailerObjects.get(i).getKeyOrContent());
        }
    }
}
