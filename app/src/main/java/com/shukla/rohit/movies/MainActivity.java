package com.shukla.rohit.movies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callto {

    private boolean mTwopane;
    private static final String MOVIEDETAIL_TAG = "DFTAG";
    Uri dataMovieUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Stetho.initializeWithDefaults(this);
        if(findViewById(R.id.fragmentDetail) != null)
        {
            mTwopane = true;

            if(savedInstanceState == null)
            {



                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentDetail,new MovieDetailsFragment(),MOVIEDETAIL_TAG).commit();
            }
        }
        else
        {
            mTwopane = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(Uri dateUri) {

        if(mTwopane)
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable(MovieDetailsFragment.DETAILED_URI,dateUri);

            MovieDetailsFragment details = new MovieDetailsFragment();
            details.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentDetail,details,MOVIEDETAIL_TAG).commit();

        }
        else
        {
            Intent intent = new Intent(this,MovieDetails.class)
                    .setData(dateUri);
            startActivity(intent);
        }

    }


}
