package com.ramkrishna.android.popularmovies;


import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramkrishna.android.popularmovies.data.MovieContract;
import com.ramkrishna.android.popularmovies.data.MovieProvider;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * <p>
 * Fragment showing the details of the movie
 */
public class MovieDetailsFragment extends Fragment {

    private static final String MOVIE_TRAILER_FRAGMENT_TAG = "TRAILER_FRAGMENT";
    private static final String MOVIE_REVIEW_FRAGMENT_TAG = "REVIEW_FRAGMENT";
    MovieObject movieObject;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        //get the movie trailer button and set the sction to be performed when it is clicked
        Button trailerButton = (Button) view.findViewById(R.id.view_trailers_button);
        trailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create fragment and give it an argument specifying the trailers it should show
                MovieTrailersFragment movieTrailersFragment = new MovieTrailersFragment();
                Bundle args = new Bundle();
                args.putInt(MovieTrailersFragment.BUNDLE_ARGUMENT_KEY, movieObject.getId());
                movieTrailersFragment.setArguments(args);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.movie_details_fragment_container, movieTrailersFragment, MOVIE_TRAILER_FRAGMENT_TAG);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        //get the movie trailer button and set the sction to be performed when it is clicked
        Button reviewButton = (Button) view.findViewById(R.id.view_review_button);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create fragment and give it an argument specifying the trailers it should show
                MovieReviewsFragment movieReviewsFragment = new MovieReviewsFragment();
                Bundle args = new Bundle();
                args.putInt(MovieReviewsFragment.BUNDLE_ARGUMENT_KEY, movieObject.getId());
                movieReviewsFragment.setArguments(args);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.movie_details_fragment_container, movieReviewsFragment, MOVIE_REVIEW_FRAGMENT_TAG);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        final ImageButton addToFavouritesButton = (ImageButton) view.findViewById(R.id.mark_favourite_button);
        if (isFavouriteMovie()) {
            addToFavouritesButton.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            addToFavouritesButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
        addToFavouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavouriteMovie()) {
                    addToFavouritesButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    String[] selectionArgs = new String[]{Integer.toString(movieObject.getId())};
                    getContext().getContentResolver().delete(
                            MovieContract.MovieEntry.CONTENT_URI,
                            MovieProvider.sMovieDetailsSelection,
                            selectionArgs);
                } else {
                    addToFavouritesButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                    getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, createContentValues());
                }
            }
        });
        return view;
    }

    private ContentValues createContentValues() {
        ContentValues movieData = new ContentValues();
        movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieObject.getId());
        movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieObject.getTitle());
        movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movieObject.getOverview());
        movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movieObject.getReleaseDate());
        movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY, movieObject.getPopularity());
        movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, movieObject.getAvgVote());
        movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movieObject.getPosterPath());
        movieData.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, movieObject.getBackdropPath());
        return movieData;
    }

    private boolean isFavouriteMovie() {
        Uri movieUri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(movieObject.getId())).build();
        Cursor favMoviesCursor = getContext().getContentResolver().query(
                movieUri,
                null,
                null,
                null,
                null
        );
        boolean isFavourite = favMoviesCursor.moveToFirst();
        favMoviesCursor.close();
        return isFavourite;
    }
}