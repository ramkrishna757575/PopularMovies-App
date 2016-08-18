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

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * <p/>
 * Fragment showing the details of the movie
 */
public class MovieDetailsFragment extends Fragment implements GetReviewsFromApi.GetReviewsFromApiListener, GetTrailersFromApi.GetTrailersFromApiListener {

    private static final String MOVIE_TRAILER_FRAGMENT_TAG = "TRAILER_FRAGMENT";
    private static final String MOVIE_REVIEW_FRAGMENT_TAG = "REVIEW_FRAGMENT";
    MovieObject movieObject;

    //variables to get the reviews(api has pagination implemented)
    int currentPage = 1, maxPage = -1;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        //Get the Extra Data from the intent
        final Bundle dataBundle = this.getArguments();
        movieObject = dataBundle.getParcelable(Constants.INTENT_MOVIE_OBJECT);

        //Load the poster image and display in ImageView
        ImageView backdropImageView = (ImageView) view.findViewById(R.id.poster_image_view);
        Uri posterUri = Uri.parse(Constants.URI_BASE_IMAGE_URI).buildUpon()
                .appendPath(Constants.URI_IMAGE_SIZE)
                .appendEncodedPath(movieObject.getPosterPath())
                .build();
        Picasso.with(getContext()).load(posterUri)
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
                args.putString(Constants.INTENT_FAVOURITE_MOVIE, dataBundle.getString(Constants.INTENT_FAVOURITE_MOVIE));
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
                args.putString(Constants.INTENT_FAVOURITE_MOVIE, dataBundle.getString(Constants.INTENT_FAVOURITE_MOVIE));
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
                    Uri movieUri = MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(movieObject.getId())).build();
                    getContext().getContentResolver().delete(
                            movieUri,
                            MovieProvider.sMovieDetailsSelection,
                            selectionArgs
                    );
                    Uri reviewsUri = MovieContract.ReviewEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(movieObject.getId())).build();
                    getContext().getContentResolver().delete(
                            reviewsUri,
                            MovieProvider.sMovieReviewsSelection,
                            selectionArgs
                    );
                    Uri trailerUri = MovieContract.TrailerEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(movieObject.getId())).build();
                    getContext().getContentResolver().delete(
                            trailerUri,
                            MovieProvider.sMovieTrailersSelection,
                            selectionArgs
                    );
                } else {
                    addToFavouritesButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                    getContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, createMovieRecord());
                    GetReviewsFromApi getReviewsFromApi = new GetReviewsFromApi(getContext(), MovieDetailsFragment.this, movieObject.getId());
                    getReviewsFromApi.execute();
                    GetTrailersFromApi getTrailersFromApi = new GetTrailersFromApi(getContext(), MovieDetailsFragment.this, movieObject.getId());
                    getTrailersFromApi.execute();
                }
            }
        });
        return view;
    }

    private ContentValues createMovieRecord() {
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

    /*Get the reviews for this movie to be added to the db*/
    @Override
    public void getReviews(ArrayList<String> author, ArrayList<String> content) {
        if (author.size() > 0 && content.size() > 0 && author.size() == content.size()) {
            ContentValues[] reviewsArray = new ContentValues[author.size()];
            for (int i = 0; i < author.size(); i++) {
                ContentValues reviewsData = new ContentValues();
                reviewsData.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieObject.getId());
                reviewsData.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, author.get(i));
                reviewsData.put(MovieContract.ReviewEntry.COLUMN_CONTENT, content.get(i));
                reviewsArray[i] = reviewsData;
            }
            Uri reviewsUri = MovieContract.ReviewEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(movieObject.getId())).build();
            getContext().getContentResolver().bulkInsert(reviewsUri, reviewsArray);
        }
        currentPage++;
        if (currentPage <= maxPage) {
            GetReviewsFromApi getReviewsFromApi = new GetReviewsFromApi(getContext(), MovieDetailsFragment.this, movieObject.getId());
            getReviewsFromApi.execute();
        }
    }

    @Override
    public void onMaxPageObtained(int maxPage) {
        this.maxPage = maxPage;
    }

    @Override
    public int currentPage() {
        return currentPage;
    }

    @Override
    public int maxPage() {
        return maxPage;
    }

    /*Get the trailers for this movie and add it to the DB*/
    @Override
    public void getTrailers(ArrayList<String> movieTrailerKeys, ArrayList<String> movieTrailerNames) {
        if (movieTrailerKeys.size() > 0 && movieTrailerNames.size() > 0 && movieTrailerKeys.size() == movieTrailerNames.size()) {
            ContentValues[] trailersArray = new ContentValues[movieTrailerKeys.size()];
            for (int i = 0; i < movieTrailerKeys.size(); i++) {
                ContentValues trailerData = new ContentValues();
                trailerData.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieObject.getId());
                trailerData.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, movieTrailerKeys.get(i));
                trailerData.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, movieTrailerNames.get(i));
                trailersArray[i] = trailerData;
            }
            Uri trailerUri = MovieContract.TrailerEntry.CONTENT_URI.buildUpon().appendPath(Integer.toString(movieObject.getId())).build();
            getContext().getContentResolver().bulkInsert(trailerUri, trailersArray);
        }
    }
}