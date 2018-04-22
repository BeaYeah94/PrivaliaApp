package com.beayeah.privalia.privaliaapp;

import android.app.Activity;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<MovieResponse.Movie> list;

    public MovieAdapter(Activity context, ArrayList<MovieResponse.Movie> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        MovieResponse.Movie m = list.get(position);

        Resources res = context.getResources();
        String newTitle = String.format(res.getString(R.string.film_title), m.getTitle(), getYear(m.getReleaseDate()));
        holder.title.setText(newTitle);

        if (m.getOverview() == null || m.getOverview().isEmpty())
            holder.overview.setVisibility(View.GONE);
        else {
            holder.overview.setText(m.getOverview());
            holder.overview.setVisibility(View.VISIBLE);
        }

        if (m.getPoster_path() == null || m.getPoster_path().isEmpty())
            holder.img.setVisibility(View.GONE);
        else {
            Uri uri = Uri.parse("https://image.tmdb.org/t/p/w500/" + m.getPoster_path());
            holder.img.setImageURI(uri);
            holder.img.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(MovieResponse.Movie movie) {
        list.add(movie);
        notifyItemInserted(getItemCount());
    }

    public ArrayList<MovieResponse.Movie> getList() {
        return list;
    }

    private String getYear(String date) {
        return date.split("-")[0];
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView title;
        @BindView(R.id.film_overview)
        TextView overview;
        @BindView(R.id.movie_poster)
        SimpleDraweeView img;

        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
