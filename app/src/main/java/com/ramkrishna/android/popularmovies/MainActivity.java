package com.ramkrishna.android.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/*
* The MainActivity class. Also the launcher activity
* */
public class MainActivity extends AppCompatActivity {

    private boolean mTwoPane = false;
    private static final String MOVIE_GRID_FRAGMENT = "MOVIE_GRID_FRAGMENT";
    private static final String MOVIE_DETAILS_FRAGMENT = "MOVIE_DETAILS_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Set the default preference value for the sort order of the movies
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        if (findViewById(R.id.movie_details_fragment_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                MovieGridFragment movieGridFragment = new MovieGridFragment();
                Bundle args = new Bundle();
                args.putString(Constants.INTENT_TWO_PANE_DETAIL_FRAGMENT, Constants.INTENT_TWO_PANE_DETAIL_FRAGMENT);
                movieGridFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_grid_fragment_container, movieGridFragment, MOVIE_GRID_FRAGMENT)
                        .commit();
            }
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.INTENT_TWO_PANE_DETAIL_FRAGMENT));
        } else {
            mTwoPane = false;
            if (savedInstanceState == null) {
                MovieGridFragment movieGridFragment = new MovieGridFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_fragment_container, movieGridFragment, MOVIE_GRID_FRAGMENT)
                        .commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fragmentmain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //Start the Settings Activity if the settings menu is clicked
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if (mTwoPane) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Constants.INTENT_TWO_PANE_DETAIL_FRAGMENT));
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mTwoPane) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        }
        super.onPause();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
            movieDetailsFragment.setArguments(intent.getExtras());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_fragment_container, movieDetailsFragment, MOVIE_DETAILS_FRAGMENT)
                    .commit();
        }
    };
}
