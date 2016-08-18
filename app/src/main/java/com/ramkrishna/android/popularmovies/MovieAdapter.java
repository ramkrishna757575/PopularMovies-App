package com.ramkrishna.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ramkr on 10-May-16.
 * <p/>
 * The Custom Adapter for GridView to show list of movies in a grid
 */
public class MovieAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<MovieObject> movieObjects;
    private LayoutInflater mInflater;

    private boolean useTwoPaneLayout = false;

    //Constructor for initialization
    public MovieAdapter(Context c, ArrayList<MovieObject> movieList, boolean useTwoPaneLayout) {
        mContext = c;
        movieObjects = movieList;
        mInflater = LayoutInflater.from(c);
        this.useTwoPaneLayout = useTwoPaneLayout;
        //Local broadcast to initialize detail fragment with first movie in the list
        if (useTwoPaneLayout) {
            Intent intent = new Intent(Constants.INTENT_TWO_PANE_DETAIL_FRAGMENT);
            intent.putExtra(Constants.INTENT_MOVIE_OBJECT, movieObjects.get(0));
            LocalBroadcastManager.getInstance(c).sendBroadcast(intent);
        }
    }

    //Return the size of the Movie List
    @Override
    public int getCount() {
        return movieObjects.size();
    }

    //Returns the item at position 'position'
    @Override
    public Object getItem(int position) {
        return movieObjects.get(position);
    }

    //Item id same as position of the item in the list
    @Override
    public long getItemId(int position) {
        return position;
    }

    //Loading of the poster image in each CardView of the GridView
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.movie_card, parent, false);
        }

        //Get the ImageView of each CardView to display poster image of the corresponding movie
        ImageView posterImage = (ImageView) convertView.findViewById(R.id.poster_image_view);
        TextView titleText = (TextView) convertView.findViewById(R.id.grid_movie_title);

        titleText.setText(movieObjects.get(position).getTitle());

        //Construct the URI to be used to fetch movie data
        Uri posterUri = Uri.parse(Constants.URI_BASE_IMAGE_URI).buildUpon()
                .appendPath(Constants.URI_IMAGE_SIZE)
                .appendEncodedPath(movieObjects.get(position).getPosterPath())
                .build();

        //Reset the image in the ImageView, so that no previously used image is displayed when this View is recycled
        posterImage.setImageDrawable(null);
        //Load the image and set it in ImageView using the Picasso Library
        Picasso.with(mContext).load(posterUri)
                .placeholder(R.drawable.ic_photo_black_24dp)
                .error(R.drawable.ic_photo_black_24dp)
                .into(posterImage);

        //Set a click listener, so that the Movie Details Activity is launched upon clicking on this CardView
        CardView cardView = (CardView) convertView.findViewById(R.id.movie_card);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!useTwoPaneLayout) {
                    Intent intent = new Intent(mContext, MovieDetailsActivity.class);
                    intent.putExtra(Constants.INTENT_MOVIE_OBJECT, movieObjects.get(position));
                    mContext.startActivity(intent);
                }else{
                    Intent intent = new Intent(Constants.INTENT_TWO_PANE_DETAIL_FRAGMENT);
                    intent.putExtra(Constants.INTENT_MOVIE_OBJECT, movieObjects.get(position));
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
            }
        });

        return convertView;
    }
}
