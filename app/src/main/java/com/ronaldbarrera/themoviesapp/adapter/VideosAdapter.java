package com.ronaldbarrera.themoviesapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ronaldbarrera.themoviesapp.R;
import com.ronaldbarrera.themoviesapp.model.Video;

import java.util.List;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosAdapterViewHolder>  {

    private List<Video> videosList;
    private Context context;
    private final VideosAdapter.VideosAdapterOnClickHandler mClickHandler;

    public VideosAdapter(Context context, List<Video> videosList, VideosAdapterOnClickHandler clickHandler){
        this.context = context;
        this.videosList = videosList;
        mClickHandler = clickHandler;

        }

    public interface VideosAdapterOnClickHandler {
        void onClick(Video article);
    }

    public void clear() {
        videosList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Video> list) {
        videosList.addAll(list);
        notifyDataSetChanged();
    }


    public class VideosAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView videoTitle;
        private TextView videoDescription;


        VideosAdapterViewHolder(View view) {
            super(view);
            videoTitle = view.findViewById(R.id.video_title);
            videoDescription = view.findViewById(R.id.video_description);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Video video = videosList.get(adapterPosition);
            mClickHandler.onClick(video);
        }
    }

    @Override
    public VideosAdapter.VideosAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.video_item, parent, false);
        return new VideosAdapter.VideosAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideosAdapter.VideosAdapterViewHolder holder, int position) {
        holder.videoTitle.setText(videosList.get(position).getName());
        String description = videosList.get(position).getType() + context.getResources().getString(R.string.dot) + videosList.get(position).getSite();
        holder.videoDescription.setText(description);
    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }


}
