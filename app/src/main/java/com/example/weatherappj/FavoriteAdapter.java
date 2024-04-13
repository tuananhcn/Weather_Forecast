package com.example.weatherappj;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private List<String> mFavorites; // Danh sách các khu vực

    public FavoriteAdapter(List<String> favorites) {
        mFavorites = favorites;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String favorite = mFavorites.get(position);
        holder.textViewFavorite.setText(favorite);
    }

    @Override
    public int getItemCount() {
        return mFavorites.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewFavorite;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewFavorite = (TextView) itemView.findViewById(R.id.textViewFavorite);
        }
    }
}

