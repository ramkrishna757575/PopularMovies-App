package com.ramkrishna.android.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ramkr on 10-May-16.
 * <p/>
 * JSON Parser that takes in raw JSON String and returns a list of MovieObjects and also the Total Pages from the API
 */
public class MovieJsonParser {

    //Returns MovieObjects list
    public ArrayList<MovieObject> parseMovieJson(String rawJsonString) throws JSONException {
        if (rawJsonString == null || rawJsonString.isEmpty()) {
            return null;
        }
        ArrayList<MovieObject> movieObjectList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(rawJsonString);
        JSONArray movieDetailsArray = jsonObject.getJSONArray(Constants.JSON_PARSER_TAG_RESULTS_ARRAY);

        for (int i = 0; i < movieDetailsArray.length(); i++) {
            MovieObject movieObject = new MovieObject();
            JSONObject movie = movieDetailsArray.getJSONObject(i);
            movieObject.setId(movie.getInt(Constants.JSON_PARSER_TAG_MOVIE_ID));
            movieObject.setPosterPath(movie.getString(Constants.JSON_PARSER_TAG_MOVIE_POSTER_PATH));
            movieObject.setAdult(movie.getBoolean(Constants.JSON_PARSER_TAG_MOVIE_IS_ADULT));
            movieObject.setOverview(movie.getString(Constants.JSON_PARSER_TAG_MOVIE_OVERVIEW));
            movieObject.setReleaseDate(movie.getString(Constants.JSON_PARSER_TAG_MOVIE_RELEASE_DATE));
            movieObject.setTitle(movie.getString(Constants.JSON_PARSER_TAG_MOVIE_TITLE));
            movieObject.setBackdropPath(movie.getString(Constants.JSON_PARSER_TAG_MOVIE_BACKDROP_PATH));
            movieObject.setPopularity(movie.getDouble(Constants.JSON_PARSER_TAG_MOVIE_POPULARITY));
            movieObject.setAvgVote(movie.getDouble(Constants.JSON_PARSER_TAG_MOVIE_AVERAGE_VOTE));
            movieObjectList.add(movieObject);
        }
        return movieObjectList;
    }

    //Return Total Pages
    public int getPageLimit(String rawJsonString) throws JSONException {
        if (rawJsonString == null || rawJsonString.isEmpty()) {
            return 0;
        } else {
            JSONObject jsonObject = new JSONObject(rawJsonString);
            return jsonObject.getInt(Constants.JSON_PARSER_TAG_PAGE_LIMIT);
        }
    }

    public ArrayList<MovieTrailerOrReviewObject> parseTrailerJson(String rawJsonString) throws JSONException {
        if (rawJsonString == null || rawJsonString.isEmpty()) {
            return null;
        }
        ArrayList<MovieTrailerOrReviewObject> movieTrailerList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(rawJsonString);
        JSONArray trailerDetailsArray = jsonObject.getJSONArray(Constants.JSON_PARSER_TAG_RESULTS_ARRAY);

        for (int i = 0; i < trailerDetailsArray.length(); i++) {
            JSONObject trailerObject = trailerDetailsArray.getJSONObject(i);
            String videoType = trailerObject.getString(Constants.JSON_PARSER_TAG_VIDEO_TYPE);

            if (!videoType.equals(Constants.JSON_PARSER_TAG_VIDEO_TYPE_TRAILER)) {
                continue;
            } else {
                MovieTrailerOrReviewObject movieTrailerObject = new MovieTrailerOrReviewObject();
                movieTrailerObject.setNameOrAuthor(trailerObject.getString(Constants.JSON_PARSER_TAG_TRAILER_NAME));
                movieTrailerObject.setKeyOrContent(trailerObject.getString(Constants.JSON_PARSER_TAG_TRAILER_KEY));
                movieTrailerList.add(movieTrailerObject);
            }
        }
        return movieTrailerList;
    }

    public ArrayList<MovieTrailerOrReviewObject> parseReviewJson(String rawJsonString) throws JSONException {
        if (rawJsonString == null || rawJsonString.isEmpty()) {
            return null;
        }
        ArrayList<MovieTrailerOrReviewObject> movieReviewsList = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(rawJsonString);
        JSONArray reviewDetailsArray = jsonObject.getJSONArray(Constants.JSON_PARSER_TAG_RESULTS_ARRAY);

        for (int i = 0; i < reviewDetailsArray.length(); i++) {
            JSONObject reviewObject = reviewDetailsArray.getJSONObject(i);
            MovieTrailerOrReviewObject movieReviewObject = new MovieTrailerOrReviewObject();
            movieReviewObject.setNameOrAuthor(reviewObject.getString(Constants.JSON_PARSER_TAG_REVIEW_AUTHOR));
            movieReviewObject.setKeyOrContent(reviewObject.getString(Constants.JSON_PARSER_TAG_REVIEW_CONTENT));
            movieReviewsList.add(movieReviewObject);
        }
        return movieReviewsList;
    }
}
