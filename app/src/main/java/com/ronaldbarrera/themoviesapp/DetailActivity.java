package com.ronaldbarrera.themoviesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.ronaldbarrera.themoviesapp.adapter.ReviewsAdapter;
import com.ronaldbarrera.themoviesapp.adapter.VideosAdapter;
import com.ronaldbarrera.themoviesapp.database.AppDatabase;
import com.ronaldbarrera.themoviesapp.database.MovieEntry;
import com.ronaldbarrera.themoviesapp.databinding.ActivityDetailBinding;
import com.ronaldbarrera.themoviesapp.model.Movie;
import com.ronaldbarrera.themoviesapp.model.Review;
import com.ronaldbarrera.themoviesapp.model.ReviewsModel;
import com.ronaldbarrera.themoviesapp.model.Video;
import com.ronaldbarrera.themoviesapp.model.VideosModel;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements  VideosAdapter.VideosAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private String POSTER_IMGAGE_SIZE = "w342";
    private String BACKDROP_IMGAGE_SIZE = "w342";
    private static String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";

    boolean isFavorite;
    Movie movie = new Movie();
    List<Review> reviewsList;
    List<Video> videosList;

    private ActivityDetailBinding mDetailBinding;
    private AppDatabase mDb;

    private static final int DEFAULT_MOVIE_ID = -1;
    private int mMovieId = DEFAULT_MOVIE_ID;

    MovieEntry movieDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mDb = AppDatabase.getInstance(getApplicationContext());

        Gson gson = new Gson();
        String strObj = getIntent().getStringExtra("movie");
        movie = gson.fromJson(strObj, Movie.class);

        mDetailBinding.titleBackdrop.setText(movie.getTitle());
        mDetailBinding.primaryInfo.originalTitleTextview.setText(movie.getOriginal_title());
        mDetailBinding.primaryInfo.releaseDateTextview.setText(formatDate(movie.getRelease_date()));
        String vote_average = (int)(movie.getVote_average() * 10) + getString(R.string.percentage);
        mDetailBinding.primaryInfo.voteAverageTextview.setText(vote_average);
        mDetailBinding.extraDetails.overviewTextview.setText(movie.getOverview());

        Picasso.get()
                .load(  BASE_IMAGE_URL + BACKDROP_IMGAGE_SIZE + movie.getBackdrop_path())
                .into(mDetailBinding.backdropImage,  new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        mDetailBinding.imageProgressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
        Picasso.get()
                .load(  BASE_IMAGE_URL + POSTER_IMGAGE_SIZE + movie.getPoster_path())
                .into(mDetailBinding.primaryInfo.moviePosterImageview);


        mMovieId = movie.getId();
        fetchVideos(mMovieId);
        fetchReviews(mMovieId);

        AppExecutors.getInstance().diskIO().execute(() -> {
            movieDb = mDb.movieDao().loadMovieById(mMovieId);
            runOnUiThread(() -> {
                if(movieDb != null) {
                    isFavorite = true;
                    invalidateOptionsMenu();
                }
            });
        });
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_favorite);
        if(isFavorite)
            item.setIcon(R.drawable.ic_favorite_filled);
        else
            item.setIcon(R.drawable.ic_favorite_unfilled);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                onFavorite(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onFavorite(MenuItem favorite) {
        if(isFavorite) {
            isFavorite = false;
            unmarkAsFavorite();
            favorite.setIcon(R.drawable.ic_favorite_unfilled);
        } else {
            isFavorite = true;
            markAsFavorite();
            favorite.setIcon(R.drawable.ic_favorite_filled);
        }
    }

    private void unmarkAsFavorite() {
        AppExecutors.getInstance().diskIO().execute(() -> mDb.movieDao().deleteMovie(movieDb));
        Snackbar.make(mDetailBinding.detailLayout, getString(R.string.snackbar_moview_removed), Snackbar.LENGTH_LONG).show();
    }

    private void markAsFavorite() {
        movieDb = new MovieEntry(movie.getId(),
                movie.getPoster_path(),
                movie.getBackdrop_path(),
                movie.getOriginal_title(),
                movie.getTitle(),
                movie.getVote_average(),
                movie.getOverview(),
                movie.getRelease_date(),
                new Date());
        AppExecutors.getInstance().diskIO().execute(() -> { mDb.movieDao().insertMovie(movieDb); });
        Snackbar.make(mDetailBinding.detailLayout, getString(R.string.snackbar_movie_added), Snackbar.LENGTH_LONG).show();
    }

    private void fetchVideos(int movie_id) {
        MoviesApiInterface apiInterface = MoviesApiClient.getClient().create(MoviesApiInterface.class);
        Call<VideosModel> call = apiInterface.getMovieVideos(movie_id, getString(R.string.moviesApiKey));

        call.enqueue(new Callback<VideosModel>() {
            @Override
            public void onResponse(Call<VideosModel> call, Response<VideosModel> response) {
                if (response.body() != null) {
                    videosList = response.body().getVideos();
                    generateVideosDataList(videosList);
                }
            }

            @Override
            public void onFailure(Call<VideosModel> call, Throwable t) {
                showErrorUI();
            }
        });
    }

    private void fetchReviews(int movie_id) {

        MoviesApiInterface apiInterface = MoviesApiClient.getClient().create(MoviesApiInterface.class);
        Call<ReviewsModel> call = apiInterface.getMovieReviews(movie_id, getString(R.string.moviesApiKey));

        call.enqueue(new Callback<ReviewsModel>() {
            @Override
            public void onResponse(Call<ReviewsModel> call, Response<ReviewsModel> response) {
                if (response.body() != null) {
                    reviewsList = response.body().getReviews();
                    generateReviewsDataList(reviewsList);
                }
            }

            @Override
            public void onFailure(Call<ReviewsModel> call, Throwable t) {
                showErrorUI();
            }
        });
    }

    private void showErrorUI() {
        View view = findViewById(R.id.detail_layout);
        Snackbar snackbar = Snackbar
                .make(view, getString(R.string.no_internet_error), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void generateVideosDataList(List<Video> videosList) {
        if(videosList.size() > 0)
            mDetailBinding.extraDetails.videosLabelTextview.setVisibility(View.VISIBLE);

        VideosAdapter adapter = new VideosAdapter(this, videosList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this, RecyclerView.VERTICAL, false);
        mDetailBinding.extraDetails.videosRecyclerview.setLayoutManager(layoutManager);
        mDetailBinding.extraDetails.videosRecyclerview.setAdapter(adapter);
    }

    private void generateReviewsDataList(List<Review> reviewsList) {
        if(reviewsList.size() > 0)
            mDetailBinding.extraDetails.reviewsLabelTextview.setVisibility(View.VISIBLE);

        ReviewsAdapter adapter = new ReviewsAdapter(this, reviewsList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this, RecyclerView.VERTICAL, false);
        mDetailBinding.extraDetails.reviewsRecyclerview.setLayoutManager(layoutManager);
        mDetailBinding.extraDetails.reviewsRecyclerview.setAdapter(adapter);
    }

    @Override
    public void onClick(Video video) {
        Uri webpage = Uri.parse(video.getYouTubeUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public static String formatDate(String inputDate){
        String inputFormat = "yyyy-MM-dd";
        String outputFormat = "MMM dd, yyyy";
        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDate;
    }
}
