package com.example.silver.popularmovies;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Silver Hein on 13.09.2016.
 */
public class DetailActivity extends AppCompatActivity{

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        TextView tvOriginalTitle = (TextView) findViewById(R.id.textview_original_title);
        ImageView ivPoster = (ImageView) findViewById(R.id.imageview_poster);
        TextView tvOverView = (TextView) findViewById(R.id.textview_overview);
        TextView tvVoteAverage = (TextView) findViewById(R.id.textview_vote_average);
        TextView tvReleaseDate = (TextView) findViewById(R.id.textview_release_date);

        Intent intent = getIntent();
        Moviez moviez = intent.getParcelableExtra(getString(R.string.parcel_movie));

        tvOriginalTitle.setText(moviez.getOriginalTitle());

        Picasso.with(this)
                .load(moviez.getPosterPath())
                .resize(getResources().getInteger(R.integer.tmdb_poster_w185_width),
                        getResources().getInteger(R.integer.tmdb_poster_w185_height))
                .error(R.drawable.not_found)
                .placeholder(R.drawable.searching)
                .into(ivPoster);

        String overView = moviez.getOverview();
        if (overView == null) {
            tvOverView.setTypeface(null, Typeface.ITALIC);
            overView = getResources().getString(R.string.overview_no_summary);
        }
        tvOverView.setText(overView);
        tvVoteAverage.setText(moviez.getDetailedVoteAverage());

        String releaseDate = moviez.getReleaseDate();
        tvReleaseDate.setText(moviez.getReleaseDate());
    }
}
