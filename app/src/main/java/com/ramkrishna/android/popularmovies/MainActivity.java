package com.ramkrishna.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/*
* The MainActivity class. Also the launcher activity
* */
public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPrefs;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    private static final String MOVIE_GRID_FRAGMENT_TAG = "MOVIE_GRID_FRAGMENT";
    private static final String FAVOURITE_MOVIE_FRAGMENT_TAG = "FAVOURITE_MOVIE_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Set the default preference value for the sort order of the movies
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                setFragmentByPrefs();
            }
        };

        //Register the listener for SharedPrefs change behaviour
        sharedPrefs.registerOnSharedPreferenceChangeListener(listener);

        if (savedInstanceState == null) {
            setFragmentByPrefs();
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

    private void setFragmentByPrefs() {
        String setPrefs = sharedPrefs.getString(this.getString(R.string.pref_sort_order_key), this.getString(R.string.settings_order_by_default_val));
        String favouritePref = getResources().getStringArray(R.array.order_by_values)[2];
        if (setPrefs.equals(favouritePref)) {
            //Add the FavouriteMovies fragment
            Log.d("TODO", "Add the FavouriteMovies fragment");
        } else {
            //Add the MovieGrid fragment
            // Create a new Fragment to be placed in the activity layout
            MovieGridFragment movieGridFragment = new MovieGridFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            movieGridFragment.setArguments(getIntent().getExtras());
            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, movieGridFragment, MOVIE_GRID_FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        }
    }
}
