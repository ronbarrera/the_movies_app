package com.ronaldbarrera.themoviesapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.ronaldbarrera.themoviesapp.R;
import com.ronaldbarrera.themoviesapp.model.Review;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder>  {

    private List<Review> reviewList;
    private Context context;

    public ReviewsAdapter(Context context, List<Review> videosList){
        this.context = context;
        this.reviewList = videosList;
    }

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {

        private TextView author_review_textview;
        private ExpandableTextView expand_text_view;

        ReviewsAdapterViewHolder(View view) {
            super(view);
            author_review_textview = view.findViewById(R.id.author_textview);
            expand_text_view = view.findViewById(R.id.expand_text_view);
        }
    }

    @Override
    public ReviewsAdapter.ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.review_item, parent, false);
        return new ReviewsAdapter.ReviewsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewsAdapter.ReviewsAdapterViewHolder holder, int position) {
        holder.author_review_textview.setText(reviewList.get(position).getAuthor());
        holder.expand_text_view.setText(reviewList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

}
