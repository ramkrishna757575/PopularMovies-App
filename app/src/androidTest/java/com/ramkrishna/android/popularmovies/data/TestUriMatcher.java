/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ramkrishna.android.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/*
    Uncomment this class when you are ready to test your UriMatcher.  Note that this class utilizes
    constants that are declared with package protection inside of the UriMatcher, which is why
    the test must be in the same data package as the Android app code.  Doing the test this way is
    a nice compromise between data hiding and testability.
 */
public class TestUriMatcher extends AndroidTestCase {

    private static final long MOVIE_ID = 5;
    private static final Uri TEST_MOVIES = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_ID = MovieContract.MovieEntry.buildMovieUri(MOVIE_ID);
    private static final Uri TEST_REVIEWS = MovieContract.ReviewEntry.CONTENT_URI;
    private static final Uri TEST_REVIEWS_WITH_MOVIE_ID = MovieContract.ReviewEntry.buildReviewUri(MOVIE_ID);
    private static final Uri TEST_TRAILERS = MovieContract.TrailerEntry.CONTENT_URI;
    private static final Uri TEST_TRAILERS_WITH_MOVIE_ID = MovieContract.TrailerEntry.buildTrailerUri(MOVIE_ID);

    /*
        Students: This function tests that your UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.  Uncomment this when you are
        ready to test your UriMatcher.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();
        assertEquals("Error: The MOVIES URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIES), MovieProvider.MOVIES);
        assertEquals("Error: The MOVIE_WITH_ID URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_ID), MovieProvider.MOVIE_WITH_ID);
        assertEquals("Error: The REVIEWS URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEWS), MovieProvider.REVIEWS);
        assertEquals("Error: The REVIEWS_WITH_MOVIE_ID URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEWS_WITH_MOVIE_ID), MovieProvider.REVIEWS_WITH_MOVIE_ID);
        assertEquals("Error: The TRAILERS URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILERS), MovieProvider.TRAILERS);
        assertEquals("Error: The TRAILERS_WITH_MOVIE_ID URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILERS_WITH_MOVIE_ID), MovieProvider.TRAILERS_WITH_MOVIE_ID);
    }
}