package com.ronaldbarrera.themoviesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.ronaldbarrera.themoviesapp.adapter.MoviesAdapter;
import com.ronaldbarrera.themoviesapp.database.AppDatabase;
import com.ronaldbarrera.themoviesapp.database.MovieEntry;
import com.ronaldbarrera.themoviesapp.model.Movie;
import com.ronaldbarrera.themoviesapp.model.MoviesModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private MoviesAdapter mAdapter;
    private TextView mSelectionLabel;

    private int page = 1;

    private AppDatabase mDb;

    public static final String FILTER_BY_POPULAR = "popular";
    public static final String FILTER_BY_TOP_RATED = "top_rated";
    public static final String FILTER_BY_FAVORITES = "favorites";
    private String CURRENT_FILTER_BY = null;

    private final static String MENU_SELECTED = "selected";
    MenuItem menuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDb = AppDatabase.getInstance(getApplicationContext());

        mSelectionLabel = findViewById(R.id.option_selected_label);
        mRecyclerView = findViewById(R.id.movies_recyclerview);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MoviesAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState != null){
            Log.d(TAG, "savedInstanceState != null");
            CURRENT_FILTER_BY = savedInstanceState.getString(MENU_SELECTED);
            if(!CURRENT_FILTER_BY.equals(FILTER_BY_FAVORITES))
                fetchMovies(CURRENT_FILTER_BY, page);
        } else {
            fetchMovies(CURRENT_FILTER_BY = FILTER_BY_POPULAR, page);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(MENU_SELECTED, CURRENT_FILTER_BY);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(!item.isChecked()) {
            item.setChecked(true);
            if (item.getTitle() != null) {
                mSelectionLabel.setText(item.getTitle());
                switch (item.getItemId()) {
                    case R.id.filter_by_popular:
                        fetchMovies(CURRENT_FILTER_BY = FILTER_BY_POPULAR, page = 1);
                        break;
                    case R.id.filter_by_top_rated:
                        fetchMovies(CURRENT_FILTER_BY = FILTER_BY_TOP_RATED, page = 1);
                        break;
                    case R.id.filter_by_favorite:
                        CURRENT_FILTER_BY = FILTER_BY_FAVORITES;
                        setupFavoriteViewModel();
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid view type, value of " + item.getItemId());
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupFavoriteViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this, movieEntries -> {

            List<Movie> favoriteMovies = new ArrayList<>();
            for(MovieEntry entry : movieEntries) {
                Movie movie = new Movie(entry.getPoster_path(),
                        entry.getId(), entry.getBackdrop_path(),
                        entry.getOriginal_title(),
                        entry.getTitle(),
                        entry.getVote_average(),
                        entry.getOverview(),
                        entry.getRelease_date());
                favoriteMovies.add(movie);
            }
            if(CURRENT_FILTER_BY.equals(FILTER_BY_FAVORITES)) {
                mAdapter.setMovies(favoriteMovies);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (CURRENT_FILTER_BY == null)
            return true;

        switch (CURRENT_FILTER_BY){
            case FILTER_BY_POPULAR:
                menuItem = menu.findItem(R.id.filter_by_popular);
                mSelectionLabel.setText(menuItem.getTitle());
                fetchMovies(FILTER_BY_POPULAR, page);
                menuItem.setChecked(true);
                break;

            case FILTER_BY_TOP_RATED:
                menuItem = menu.findItem(R.id.filter_by_top_rated);
                mSelectionLabel.setText(menuItem.getTitle());
                fetchMovies(FILTER_BY_FAVORITES, page);
                menuItem.setChecked(true);
                break;

            case FILTER_BY_FAVORITES:
                menuItem = menu.findItem(R.id.filter_by_favorite);
                mSelectionLabel.setText(menuItem.getTitle());
                setupFavoriteViewModel();
                menuItem.setChecked(true);
                break;
        }
        return true;
    }

    private void fetchMovies(String filter_by, final int page) {
        MoviesApiInterface apiInterface = MoviesApiClient.getClient().create(MoviesApiInterface.class);
        Call<MoviesModel> call = apiInterface.getMovies(filter_by, page, getString(R.string.moviesApiKey));

        call.enqueue(new Callback<MoviesModel>() {
            @Override
            public void onResponse(Call<MoviesModel> call, Response<MoviesModel> response) {

                if(response.body() != null) {
                    if(page > 1) {
                        List<Movie> nextMovieList = response.body().getMovies();
                        mAdapter.addAll(nextMovieList);
                    }
                    else {
                        updateUI(response.body().getMovies());
                    }
                }
            }

            @Override
            public void onFailure(Call<MoviesModel> call, Throwable t) {
                View view = findViewById(R.id.main_layout);
                Snackbar snackbar = Snackbar
                        .make(view, getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);

                snackbar.show();
            }
        });
    }

    private void updateUI(List<Movie> mMovieList) {
        mAdapter.clear();
        mAdapter.setMovies(mMovieList);
        mAdapter.setOnBottomReachedListener(position -> {
            Log.d(TAG, "setOnBottomReachedListener");

            if(!CURRENT_FILTER_BY.equals(FILTER_BY_FAVORITES)) {
                fetchMovies(CURRENT_FILTER_BY, ++page);
            }
        });
    }

    @Override
    public void onClick(Movie article) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);

        Gson gson = new Gson();
        intentToStartDetailActivity.putExtra("movie", gson.toJson(article));
        startActivity(intentToStartDetailActivity);
    }
}
