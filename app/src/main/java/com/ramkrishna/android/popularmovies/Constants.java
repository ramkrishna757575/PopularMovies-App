package com.ramkrishna.android.popularmovies;

/**
 * Created by ramkr on 10-May-16.
 *
 * This class holds global constants used throughout the application
 */
public class Constants {
    public static final String JSON_PARSER_TAG_RESULTS_ARRAY = "results";
    public static final String JSON_PARSER_TAG_MOVIE_ID = "id";
    public static final String JSON_PARSER_TAG_MOVIE_POSTER_PATH = "poster_path";
    public static final String JSON_PARSER_TAG_MOVIE_IS_ADULT = "adult";
    public static final String JSON_PARSER_TAG_MOVIE_OVERVIEW = "overview";
    public static final String JSON_PARSER_TAG_MOVIE_RELEASE_DATE = "release_date";
    public static final String JSON_PARSER_TAG_MOVIE_TITLE = "title";
    public static final String JSON_PARSER_TAG_MOVIE_BACKDROP_PATH = "backdrop_path";
    public static final String JSON_PARSER_TAG_MOVIE_POPULARITY = "popularity";
    public static final String JSON_PARSER_TAG_MOVIE_AVERAGE_VOTE = "vote_average";
    public static final String URI_BASE_IMAGE_URI = "http://image.tmdb.org/t/p/";
    public static final String URI_IMAGE_SIZE = "w342";
    public static final String JSON_PARSER_TAG_PAGE_LIMIT = "total_pages";

    public static final String INTENT_MOVIE_OBJECT = "MovieObject";

    public static final String URI_BASE_MOVIE_API_URI = "http://api.themoviedb.org/3/movie/";
    public static final String URI_PARAM_API_KEY = "api_key";
    public static final String URI_PARAM_CURRENT_PAGE = "page";
    public static final String URI_PARAM_SORT_ORDER_RATING = "top_rated";
    public static final String URI_PARAM_SORT_ORDER_POPULARITY = "popular";
    public static final String URI_PARAM_MOVIE_VIDEOS = "videos";
    public static final String URI_PARAM_MOVIE_REVIEWS = "reviews";

    public static final String JSON_PARSER_TAG_TRAILER_NAME = "name";
    public static final String JSON_PARSER_TAG_TRAILER_KEY = "key";
    public static final String JSON_PARSER_TAG_REVIEW_AUTHOR = "author";
    public static final String JSON_PARSER_TAG_REVIEW_CONTENT = "content";
    public static final String JSON_PARSER_TAG_VIDEO_TYPE = "type";
    public static final String JSON_PARSER_TAG_VIDEO_TYPE_TRAILER = "Trailer";
    public static final String URI_BASE_YOU_TUBE_URI = "https://www.youtube.com/watch";
    public static final String URI_PARAM_YOU_TUBE_VIDEO = "v";

    public static final int START_PAGE_NUMBER = 1;
}
