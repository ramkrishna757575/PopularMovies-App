package com.ramkrishna.android.popularmovies;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ramkrishna.android.popularmovies.data.MovieContract;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieTrailersFragment extends Fragment implements GetTrailersFromApi.GetTrailersFromApiListener {

    ListView trailerListView;
    ArrayAdapter<String> trailerAdapter;
    Cursor cursor;
    ArrayList<String> movieTrailerKeys;
    ArrayList<String> movieTrailerNames;
    public static String BUNDLE_ARGUMENT_KEY = "MovieId";
    String isFavouriteMovie = null;

    public MovieTrailersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_trailers, container, false);
        //Get the Extra Data from the intent
        int movieId = getArguments().getInt(BUNDLE_ARGUMENT_KEY);
        isFavouriteMovie = getArguments().getString(Constants.INTENT_FAVOURITE_MOVIE);
        //Fetch the trailers for the movie and show them in the listview
        movieTrailerKeys = new ArrayList<>();
        movieTrailerNames = new ArrayList<>();
        trailerListView = (ListView) view.findViewById(R.id.trailers_list_view);

        if (isFavouriteMovie != null && isFavouriteMovie.equals(Constants.INTENT_FAVOURITE_MOVIE)) {
            Uri trailerUri = MovieContract.TrailerEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(movieId)).build();
            cursor = getContext().getContentResolver().query(trailerUri, null, null, null, null);
            String[] from = new String[]{MovieContract.TrailerEntry.COLUMN_TRAILER_NAME};
            int[] to = new int[]{android.R.id.text1};
            SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(getContext(), android.R.layout.simple_list_item_1, cursor, from, to, Adapter.NO_SELECTION);
            trailerListView.setAdapter(simpleCursorAdapter);
        } else {
            GetTrailersFromApi getTrailersFromApi = new GetTrailersFromApi(getContext(), MovieTrailersFragment.this, movieId);
            getTrailersFromApi.execute();
        }
        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String trailerKey = null;
                if (isFavouriteMovie != null && isFavouriteMovie.equals(Constants.INTENT_FAVOURITE_MOVIE)) {
                    cursor.moveToPosition(position);
                    trailerKey = cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY));
                } else {
                    trailerKey = movieTrailerKeys.get(position);
                }
                Uri videoUri = Uri.parse(Constants.URI_BASE_YOU_TUBE_URI).buildUpon()
                        .appendQueryParameter(Constants.URI_PARAM_YOU_TUBE_VIDEO, trailerKey)
                        .build();
                startActivity(new Intent(Intent.ACTION_VIEW, videoUri));
            }
        });
        return view;
    }

    @Override
    public void getTrailers(ArrayList<String> movieTrailerKeys, ArrayList<String> trailerNames) {
        this.movieTrailerKeys = movieTrailerKeys;
        movieTrailerNames = trailerNames;
        trailerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, movieTrailerNames);
        trailerListView.setAdapter(trailerAdapter);
    }
}
