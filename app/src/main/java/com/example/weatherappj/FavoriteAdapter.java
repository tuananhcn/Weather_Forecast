package com.example.weatherappj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private List<String> mFavorites; // This list should contain the cities
    private LayoutInflater mInflater;
    private Context mContext;
    public interface FavoriteItemListener {
        void onFavoriteIconClicked(String location);
        void onCityClicked(String location);
    }
    private FavoriteItemListener listener;
    public FavoriteAdapter(Context context, List<String> favorites, FavoriteItemListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.mFavorites = favorites;
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.favorite_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String city = mFavorites.get(position);
        holder.textViewCityName.setText(city);
        boolean isFavorite = checkIsFavorite(city);
        holder.imageViewFavorite.setImageResource(isFavorite ? R.drawable.filled_favorite : R.drawable.favorite_icon);
    }
    private boolean checkIsFavorite(String city) {
        // This method should check if a city is in the favorites
        // For example, if you have a way to access the current favorites from here
        return mFavorites.contains(city);
    }
    @Override
    public int getItemCount() {
        return mFavorites.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCityName;
        public ImageView imageViewFavorite;
        private RelativeLayout itemLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewCityName = itemView.findViewById(R.id.textViewCityName);
            imageViewFavorite = itemView.findViewById(R.id.imageViewFavorite);
            itemLayout = itemView.findViewById(R.id.relativeLayoutItem);
            // Set up the click listener for the favorite icon
            imageViewFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String city = mFavorites.get(position);
                        listener.onFavoriteIconClicked(city);
                    }
                }

            });
            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        String city = mFavorites.get(position);
                        listener.onCityClicked(city);
                    }
                }
            });
        }
    }
    public void updateFavoritesList(List<String> newFavorites) {
        mFavorites.clear();
        mFavorites.addAll(newFavorites);
        notifyDataSetChanged(); // This will refresh the RecyclerView with the new favorites list
    }
    // Additional methods for the adapter...
}
