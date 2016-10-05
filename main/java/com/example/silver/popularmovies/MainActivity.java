package com.example.silver.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    //Presents movie posters

    private GridView mGridView;

    //Holds menu items

    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setOnItemClickListener(moviePosterClickListener);

        if (savedInstanceState == null) {
            //Gets data from the Internet
            getMoviesFromTMDb(getSortMethod());
        } else {
            //Gets data from local resources
            Parcelable[] parcelable = savedInstanceState.getParcelableArray(getString(R.string. parcel_movie));

            if (parcelable != null) {
                int numMovieObjects = parcelable.length;
                Moviez[] moviez = new Moviez[numMovieObjects];
                for (int i=0; i < numMovieObjects; i++) {
                    moviez[i] = (Moviez) parcelable[i];
                }

                //Load movie objects into view
                mGridView.setAdapter(new ImageAdapter(this, moviez));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflating the menu, adding the action bar
        getMenuInflater().inflate(R.menu.main, mMenu);

        mMenu = menu;

        //Adding menu items
        //MAYBE PUT IT IN MENU FOLDER?!!
        mMenu.add(Menu.NONE, // No group
                R.string.pref_sort_pop_desc_key, //ID
                Menu.NONE, // Sort order isn't relevant
                null) // No text to display
                .setVisible(false)
                .setIcon(R.drawable.ic_action_whatshot) // ADD SOME ICON HERE
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        mMenu.add(Menu.NONE,
                R.string.pref_sort_vote_avg_desc_key, Menu.NONE, null)
                .setVisible(false)
                .setIcon(R.drawable.ic_action_poll) // ADD SOME ICON HERE
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        updateMenu();

        return true;
    }


    @Override
    protected void onSaveInstanceState (Bundle outState) {

        int numMovieObjects = mGridView.getCount();
        if (numMovieObjects > 0) {
            //Get Movie objects from gridview
            Moviez[] moviez = new Moviez[numMovieObjects];
            for (int i=0; i< numMovieObjects; i++) {
                moviez [i] = (Moviez) mGridView.getItemAtPosition(i);
            }

            //Saves Movie objects to bundle
            outState.putParcelableArray(getString(R.string.parcel_movie), moviez);
        }
    }

    //Handles action bar clicks

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.string.pref_sort_pop_desc_key:
                updateMenu();
                updateSharedPrefs(getString(R.string.tmdb_sort_pop_desc));
                getMoviesFromTMDb(getSortMethod());
                return true;
            case R.string.pref_sort_vote_avg_desc_key:
                updateMenu();
                updateSharedPrefs(getString(R.string.tmdb_sort_vote_avg_desc));
                getMoviesFromTMDb(getSortMethod());
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    //Listener for clicks on movie posters in GridView

    private final GridView.OnItemClickListener moviePosterClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
            Moviez moviez = (Moviez) parent.getItemAtPosition(position);

            Intent intent = new Intent (getApplicationContext(), DetailActivity.class);
            intent.putExtra(getResources().getString(R.string.parcel_movie), moviez);

            startActivity(intent);
        }
    };

    private void getMoviesFromTMDb(String sortMethod) {
        if (isNetworkAvailable()){
            //Sets the API key for accessing TMDb data
            String apiKey = getString(R.string.apikey_tmdb);

            //Listener for when AsynckTask is ready to update UI
            OnTaskCompleted taskCompleted = new OnTaskCompleted () {
                @Override
                public void onMoviezTaskCompleted(Moviez[]moviez){
                    mGridView.setAdapter(new ImageAdapter(getApplicationContext(), moviez));
                }
            };

            //Execute task
            MovieAsyncTask movieAsyncTask = new MovieAsyncTask(taskCompleted, apiKey);
            movieAsyncTask.execute(sortMethod);
        } else {
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_LONG).show();
        }
    }

    //Checks if user has Internet access. Based on StackOverflow snippet.

    private boolean isNetworkAvailable () {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //Updates the menu based on the method set in SharedPreferences.

    private void updateMenu() {
        String sortMethod = getSortMethod();

        if (sortMethod.equals(getString(R.string.tmdb_sort_pop_desc))) {
            mMenu.findItem(R.string.pref_sort_pop_desc_key).setVisible(false);
            mMenu.findItem(R.string.pref_sort_vote_avg_desc_key).setVisible(true);
        } else {
            mMenu.findItem(R.string.pref_sort_vote_avg_desc_key).setVisible(false);
            mMenu.findItem(R.string.pref_sort_pop_desc_key).setVisible(true);
        }
    }

    //Gets the sort method set by the user from SharedPreferences. Default is set to sort by popularity.

    private String getSortMethod(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        return pref.getString(getString(R.string.pref_sort_method_key), getString (R.string.tmdb_sort_pop_desc));
    }

    //Saves the selected sort method

    private void updateSharedPrefs(String sortMethod) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(getString(R.string.pref_sort_method_key), sortMethod);
        editor.apply();
    }
}
