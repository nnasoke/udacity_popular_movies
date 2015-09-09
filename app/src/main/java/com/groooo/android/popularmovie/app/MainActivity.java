package com.groooo.android.popularmovie.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.groooo.android.popularmovie.R;
import com.groooo.android.popularmovie.helper.Utility;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_MOVIES_FRAGMENT = "MVFLG";

    private String mSortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSortBy = Utility.getSharedPreferenceSortOrder(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movies_list_container, new MovieFragment(), TAG_MOVIES_FRAGMENT)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMoviesList(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_refresh) {
            updateMoviesList(true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMoviesList(boolean force) {
        final String sortBy = Utility.getSharedPreferenceSortOrder(this);
        if (force || !sortBy.equals(mSortBy)) {
            MovieFragment fragment = (MovieFragment)getSupportFragmentManager().findFragmentByTag(TAG_MOVIES_FRAGMENT);
            fragment.onSortChange(sortBy);
        }
    }
}
