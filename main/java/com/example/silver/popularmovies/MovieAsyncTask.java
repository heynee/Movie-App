package com.example.silver.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Silver Hein on 14.09.2016.
 */

//Fetches data from the Internet

public class MovieAsyncTask extends AsyncTask<String, Void, Moviez[]> {

    private final String LOG_TAG = MovieAsyncTask.class.getSimpleName();

    private final String mApiKey;

    private final OnTaskCompleted mListener;

    public MovieAsyncTask(OnTaskCompleted listener, String apiKey){
        super();

        mListener = listener;
        mApiKey = apiKey;
    }

    @Override
    protected Moviez [] doInBackground (String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviezJsonStr = null;

        try {
            URL url = getApiUrl (params);

            //Connecting to get JSON
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();

            if (inputStream == null) {
                return null;
            }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }


                //If no data found, do nothing
                if (builder.length() == 0) {
                    return null;
                }

                moviezJsonStr = builder.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getMoviezDataFromJson(moviezJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    //Extracts data from the JSON object and return an Array of movie objects.

    private Moviez[] getMoviezDataFromJson (String moviezJsonStr) throws JSONException {
        final String TAG_RESULTS = "results";
        final String TAG_ORIGINAL_TITLE = "original_title";
        final String TAG_POSTER_PATH = "poster_path";
        final String TAG_OVERVIEW = "overview";
        final String TAG_VOTE_AVERAGE = "vote_average";
        final String TAG_RELEASE_DATE = "release_date";


        //Gets the array containing the movies found
        JSONObject moviezJson = new JSONObject(moviezJsonStr);
        JSONArray resultsArray = moviezJson.getJSONArray(TAG_RESULTS);

        //Create array of Movie objects that store data from the JSON string
        Moviez[] moviez = new Moviez[resultsArray.length()];

        //Go through movies one by one and get data

        for (int i = 0; i < resultsArray.length(); i++) {
            //Initalize each object before it can be used
            moviez[i] = new Moviez();

            //Object contains all tags we're looking for
            JSONObject moviezInfo = resultsArray.getJSONObject(i);

            //Store data in movie object
            moviez[i].setOriginalTitle(moviezInfo.getString(TAG_ORIGINAL_TITLE));
            moviez[i].setPosterPath(moviezInfo.getString(TAG_POSTER_PATH));
            moviez[i].setOverview(moviezInfo.getString(TAG_OVERVIEW));
            moviez[i].setVoteAverage(moviezInfo.getDouble(TAG_VOTE_AVERAGE));
            moviez[i].setReleaseDate(moviezInfo.getString(TAG_RELEASE_DATE));
        }

        return moviez;
    }

    private URL getApiUrl (String[] parameters) throws MalformedURLException {
        final String TMDB_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
        final String SORT_BY_PARAM = "sort_by";
        final String API_KEY_PARAM = "api_key";

        Uri builtUri = Uri.parse (TMDB_BASE_URL).buildUpon()
                .appendQueryParameter(SORT_BY_PARAM, parameters[0])
                .appendQueryParameter(API_KEY_PARAM, mApiKey)
                .build();

        return new URL (builtUri.toString());
    }

    @Override
    protected void onPostExecute (Moviez[] moviez) {
        super.onPostExecute(moviez);

        //Notifies the UI
        mListener.onMoviezTaskCompleted(moviez);
    }
}

