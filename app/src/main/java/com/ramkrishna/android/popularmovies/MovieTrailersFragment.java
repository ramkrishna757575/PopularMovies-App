package com.ramkrishna.android.popularmovies;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieTrailersFragment extends Fragment implements GetTrailersFromApi.GetTrailersFromApiListener {

    ListView trailerListView;
    ArrayAdapter<String> trailerAdapter;
    ArrayList<String> movieTrailerKeys;
    ArrayList<String> movieTrailerNames;
    public static String BUNDLE_ARGUMENT_KEY = "MovieId";

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
        //Fetch the trailers for the movie and show them in the listview
        movieTrailerKeys = new ArrayList<>();
        movieTrailerNames = new ArrayList<>();
        trailerListView = (ListView) view.findViewById(R.id.trailers_list_view);

        GetTrailersFromApi getTrailersFromApi = new GetTrailersFromApi(getContext(), MovieTrailersFragment.this, movieId);
        getTrailersFromApi.execute();

        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri videoUri = Uri.parse(Constants.URI_BASE_YOU_TUBE_URI).buildUpon()
                        .appendQueryParameter(Constants.URI_PARAM_YOU_TUBE_VIDEO, movieTrailerKeys.get(position))
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
