package com.example.silver.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Silver Hein on 13.09.2016.
 */

class ImageAdapter extends BaseAdapter {
    private final Context mContext;
    private final Moviez[] mMoviez;

    public ImageAdapter(Context c, Moviez[] moviez) {
        mContext = c;
        mMoviez = moviez;
    }

    @Override
    public int getCount() {
        if (mMoviez == null || mMoviez.length == 0) {
            return -1;
        }

        return mMoviez.length;
    }

    @Override
    public Moviez getItem(int position) {
        if (mMoviez == null || mMoviez.length == 0) {
            return null;
        }

        return mMoviez[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //Creates a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load(mMoviez[position].getPosterPath())
                .resize(mContext.getResources().getInteger(R.integer.tmdb_poster_w185_width),
                        mContext.getResources().getInteger(R.integer.tmdb_poster_w185_height))
                .error(R.drawable.not_found)
                .placeholder(R.drawable.searching)
                .into(imageView);
        return imageView;
    }
}