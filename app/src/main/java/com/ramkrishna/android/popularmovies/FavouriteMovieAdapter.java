package com.ramkrishna.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ramkrishna.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by ramkr on 19-Aug-16.
 */
public class FavouriteMovieAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    private boolean useTwoPaneLayout = false;

    public FavouriteMovieAdapter(Context context, Cursor c, boolean autoRequery, boolean useTwoPaneLayout) {
        super(context, c, autoRequery);
        mInflater = LayoutInflater.from(context);
        this.useTwoPaneLayout = useTwoPaneLayout;

        if (useTwoPaneLayout && c.moveToFirst()) {
            //Local broadcast to initialize detail fragment with first movie in the list
            Intent intent = new Intent(Constants.INTENT_TWO_PANE_DETAIL_FRAGMENT);
            intent.putExtra(Constants.INTENT_MOVIE_OBJECT, createMovieObjectFromCursor(c));
            intent.putExtra(Constants.INTENT_FAVOURITE_MOVIE, Constants.INTENT_FAVOURITE_MOVIE);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.movie_card, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        final int position = cursor.getPosition();
        ImageView posterImage = (ImageView) view.findViewById(R.id.poster_image_view);
        TextView titleText = (TextView) view.findViewById(R.id.grid_movie_title);

        titleText.setText(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)));

        //Construct the URI to be used to fetch movie data
        Uri posterUri = Uri.parse(Constants.URI_BASE_IMAGE_URI).buildUpon()
                .appendPath(Constants.URI_IMAGE_SIZE)
                .appendEncodedPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH)))
                .build();

        //Reset the image in the ImageView, so that no previously used image is displayed when this View is recycled
        posterImage.setImageDrawable(null);
        //Load the image and set it in ImageView using the Picasso Library
        Picasso.with(context).load(posterUri)
                .placeholder(R.drawable.ic_photo_black_24dp)
                .error(R.drawable.ic_photo_black_24dp)
                .into(posterImage);

        //Set a click listener, so that the Movie Details Activity is launched upon clicking on this CardView
        CardView cardView = (CardView) view.findViewById(R.id.movie_card);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!useTwoPaneLayout) {
                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    cursor.moveToPosition(position);
                    intent.putExtra(Constants.INTENT_MOVIE_OBJECT, createMovieObjectFromCursor(cursor));
                    intent.putExtra(Constants.INTENT_FAVOURITE_MOVIE, Constants.INTENT_FAVOURITE_MOVIE);
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(Constants.INTENT_TWO_PANE_DETAIL_FRAGMENT);
                    cursor.moveToPosition(position);
                    intent.putExtra(Constants.INTENT_MOVIE_OBJECT, createMovieObjectFromCursor(cursor));
                    intent.putExtra(Constants.INTENT_FAVOURITE_MOVIE, Constants.INTENT_FAVOURITE_MOVIE);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        });
    }

    public MovieObject createMovieObjectFromCursor(Cursor cursor) {
        MovieObject movieObject = new MovieObject();
        movieObject.setId(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
        movieObject.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)));
        movieObject.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW)));
        movieObject.setAvgVote(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RATING)));
        movieObject.setPopularity(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY)));
        movieObject.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE)));
        movieObject.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH)));
        movieObject.setBackdropPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH)));
        return movieObject;
    }
}
