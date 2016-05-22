package com.ramkrishna.android.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ramkr on 10-May-16.
 *
 * JSON Parser that takes in raw JSON String and returns a list of MovieObjects and also the Total Pages from the API
 */
public class MovieJsonParser {

    //Returns MovieObjects list
    public ArrayList<MovieObject> parseMovieJson(String rawJsonString) throws JSONException
    {
        if (rawJsonString == null || rawJsonString.isEmpty())
        {
            return null;
        }
        ArrayList<MovieObject> movieObjectList = new ArrayList<MovieObject>();

        JSONObject jsonObject = new JSONObject(rawJsonString);
        JSONArray movieDetailsArray = jsonObject.getJSONArray(Constants.JSON_PARSER_TAG_RESULTS_ARRAY);

        for (int i = 0; i < movieDetailsArray.length(); i++)
        {
            MovieObject movieObject = new MovieObject();
            JSONObject movie = movieDetailsArray.getJSONObject(i);
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
    public int getPageLimit(String rawJsonString) throws JSONException
    {
        if (rawJsonString == null || rawJsonString.isEmpty())
        {
            return 0;
        }else{
            JSONObject jsonObject = new JSONObject(rawJsonString);
            return jsonObject.getInt(Constants.JSON_PARSER_TAG_PAGE_LIMIT);
        }
    }
}
