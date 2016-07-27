package com.ramkrishna.android.popularmovies;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieReviewsFragment extends Fragment implements GetReviewsFromApi.GetReviewsFromApiListener {

    Button moreReviewsButton;
    ListView reviewsListView;
    ArrayAdapter reviewsAdapter;
    ArrayList<String> reviewAuthors = new ArrayList<>();
    ArrayList<String> reviewContents = new ArrayList<>();
    int currentPage = 1, maxPage = -1, movieId;
    public static final String BUNDLE_ARGUMENT_KEY = "MovieId";

    public MovieReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_reviews, container, false);
        //Get the Extra Data from the intent
        movieId = getArguments().getInt(BUNDLE_ARGUMENT_KEY);
        //Fetch the trailers for the movie and show them in the listview
        reviewsListView = (ListView) view.findViewById(R.id.reviews_list_view);

        GetReviewsFromApi getReviewsFromApi = new GetReviewsFromApi(getContext(), MovieReviewsFragment.this, movieId);
        getReviewsFromApi.execute();

        reviewsAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, reviewAuthors) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(reviewAuthors.get(position));
                text1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                text2.setText(reviewContents.get(position));
                return view;
            }
        };

        reviewsListView.setAdapter(reviewsAdapter);

        moreReviewsButton = (Button) view.findViewById(R.id.more_reviews_button);
        moreReviewsButton.setEnabled(false);
        moreReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage++;
                GetReviewsFromApi getReviewsFromApi = new GetReviewsFromApi(getContext(), MovieReviewsFragment.this, movieId);
                getReviewsFromApi.execute();
                if (currentPage >= maxPage)
                    moreReviewsButton.setEnabled(false);
                else
                    moreReviewsButton.setEnabled(true);
            }
        });

        return view;
    }

    @Override
    public void getReviews(ArrayList<String> authors, ArrayList<String> contents) {
        reviewAuthors.addAll(authors);
        reviewContents.addAll(contents);
        reviewsAdapter.notifyDataSetChanged();
        if (currentPage >= maxPage)
            moreReviewsButton.setEnabled(false);
        else
            moreReviewsButton.setEnabled(true);
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
}
