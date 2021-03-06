package com.shukla.rohit.movies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MovieDetails extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {

            Bundle argument = new Bundle();
            argument.putParcelable(MovieDetailsFragment.DETAILED_URI,getIntent().getData());
            MovieDetailsFragment detailsFragment = new MovieDetailsFragment();
            detailsFragment.setArguments(argument);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentDetail, detailsFragment)
                    .commit();
        }

    }

}
