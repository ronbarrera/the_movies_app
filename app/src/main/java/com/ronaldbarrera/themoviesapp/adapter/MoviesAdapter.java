package com.ronaldbarrera.themoviesapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.ronaldbarrera.themoviesapp.OnBottomReachedListener;
import com.ronaldbarrera.themoviesapp.R;
import com.ronaldbarrera.themoviesapp.model.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private List<Movie> mMovieList;
    private Context context;
    private final MoviesAdapterOnClickHandler mClickHandler;
    private static String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/w342";
    private OnBottomReachedListener onBottomReachedListener;


    public MoviesAdapter(Context context, MoviesAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.mClickHandler = clickHandler;
    }

    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public void clear() {
        if(getItemCount() > 0){
            mMovieList.clear();
            notifyDataSetChanged();
        }
    }

    public void addAll(List<Movie> list) {
        mMovieList.addAll(list);
        notifyDataSetChanged();
    }

    public void setMovies(List<Movie> movieEntries) {
        mMovieList = movieEntries;
        notifyDataSetChanged();
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener){
        this.onBottomReachedListener = onBottomReachedListener;
    }


    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView posterImage;
        ProgressBar imageProgressbar;

        MoviesAdapterViewHolder(View view) {
            super(view);
            posterImage = view.findViewById(R.id.moviePoster);
            imageProgressbar = view.findViewById(R.id.imageProgressbar);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMovieList.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.movie_poster_item, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MoviesAdapterViewHolder holder, int position) {

        if(onBottomReachedListener != null) {
            if (position > getItemCount() - 2)
                onBottomReachedListener.onBottomReached(position);
        }
        String imageURL =  BASE_IMAGE_URL + mMovieList.get(position).getPoster_path();
        Picasso.get()
                .load(imageURL)
                .into(holder.posterImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.imageProgressbar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null) {
            return 0;
        }
        return mMovieList.size();    }

}
