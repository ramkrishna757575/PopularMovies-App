package com.ramkrishna.android.popularmovies;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 *
 * Fragment showing the details of the movie
 */
public class MovieDetailsFragment extends Fragment {

    MovieObject movieObject;

    public MovieDetailsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        //Get the Extra Data from the intent
        movieObject = getActivity().getIntent().getExtras().getParcelable(Constants.INTENT_MOVIE_OBJECT);

        //Load the poster image and display in ImageView
        ImageView backdropImageView = (ImageView) view.findViewById(R.id.poster_image_view);
        Uri posterUri = Uri.parse(Constants.URI_BASE_IMAGE_URI).buildUpon()
                .appendPath(Constants.URI_IMAGE_SIZE)
                .appendEncodedPath(movieObject.getPosterPath())
                .build();
        Picasso.with(getContext()).load(posterUri)
                .placeholder(R.drawable.ic_photo_black_24dp)
                .error(R.drawable.ic_photo_black_24dp)
                .into(backdropImageView);

        //Set the Title in the Title TextView
        TextView movieTitle = (TextView) view.findViewById(R.id.movie_title_text_view);
        movieTitle.setText(movieObject.getTitle());

        //Set the Release Date in the ReleaseDate TextView
        TextView releaseDate = (TextView) view.findViewById(R.id.release_date_text_view);
        releaseDate.setText(movieObject.getReleaseDate());

        //Set the Rating in the Rating TextView
        TextView rating = (TextView) view.findViewById(R.id.rating_text_view);
        rating.setText(Double.toString(movieObject.getAvgVote()) + "/10");

        //Set the Overview in the Overview TextView
        TextView movieOverview = (TextView) view.findViewById(R.id.movie_overview_text_view);
        movieOverview.setText(movieObject.getOverview());

        return view;
    }

}
