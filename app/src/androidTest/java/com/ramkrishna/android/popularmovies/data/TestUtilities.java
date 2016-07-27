package com.ramkrishna.android.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.ramkrishna.android.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your WeatherContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            switch (valueCursor.getType(idx)) {
                case Cursor.FIELD_TYPE_FLOAT:
                    float fdata = valueCursor.getFloat(idx);
                    assertEquals("Value '" + expectedValue +
                            "' did not match the expected value '" +
                            fdata + "'. " + error, Float.parseFloat(expectedValue), fdata);
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    String sdata = valueCursor.getString(idx);
                    assertEquals("Value '" + expectedValue +
                            "' did not match the expected value '" +
                            sdata + "'. " + error, expectedValue, sdata);
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    Integer idata = valueCursor.getInt(idx);
                    assertEquals("Value '" + expectedValue +
                            "' did not match the expected value '" +
                            idata + "'. " + error, expectedValue, Integer.toString(idata));
                    break;
                default:
                    assertTrue("Unknown value", true);
            }
        }
    }

    /*
        Students: You can uncomment this helper function once you have finished creating the
        LocationEntry part of the WeatherContract.
     */
    static ContentValues createMovieRecord() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 209112);
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "Batman v Superman: Dawn of Justice");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, "Fearing the actions of a god-like Super Hero left unchecked, Gotham City’s own formidable, forceful vigilante takes on Metropolis’s most revered, modern-day savior, while the world wrestles with what sort of hero it really needs. And with Batman and Superman at war with one another, a new threat quickly arises, putting mankind in greater danger than it’s ever known before.");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, "2016-03-23");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY, 36.542516);
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, 5.57);
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, "/cGOPbv9wA5gEejkUN892JrveARt.jpg");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, "/vsjBeMPZtyB7yNsYY56XYxifaQZ.jpg");

        return testValues;
    }

    /*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the WeatherContract as well as the WeatherDbHelper.
     */
    static long insertMovieRecord(Context context) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieRecord();

        long locationRowId;
        locationRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);
        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);

        return locationRowId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }

    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
