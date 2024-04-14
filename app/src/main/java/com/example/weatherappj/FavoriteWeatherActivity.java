package com.example.weatherappj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteWeatherActivity extends AppCompatActivity implements FavoriteAdapter.FavoriteItemListener {
    private SharedPreferences prefs;
    private Set<String> favoritesSet;
    private RecyclerView recyclerView;
    private void removeLocationFromFavorites(String location) {
        // Retrieve the current favorites set
        Set<String> currentFavorites = prefs.getStringSet("FavoriteLocations", new HashSet<>());
        // Create a new set from the current one (important for SharedPreferences)
        Set<String> newFavorites = new HashSet<>(currentFavorites);
        // Remove the favorite
        newFavorites.remove(location);
        // Save the new set
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("FavoriteLocations", newFavorites);
        editor.apply(); // or editor.commit(); if you want to know the result
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_activity);

        prefs = getSharedPreferences("FAVORITES", MODE_PRIVATE);
        List<String> favorites = new ArrayList<>(loadFavorites()); // Convert Set to List

        RecyclerView recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Set the LayoutManager
        FavoriteAdapter adapter = new FavoriteAdapter(this, favorites,this);
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onFavoriteIconClicked(String location) {
        removeLocationFromFavorites(location);
        // Update the list and adapter after removing the item
        List<String> updatedFavorites = new ArrayList<>(loadFavorites());
        recyclerView.setAdapter(new FavoriteAdapter(this, updatedFavorites, this));
    }

    @Override
    public void onCityClicked(String location) {
        // When a city is clicked, return to the MainActivity and perform a search
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("CITY_NAME", location);
        startActivity(intent);
        finish(); // Finish this activity so you don't return to it when pressing back from MainActivity
    }
    private Set<String> loadFavorites() {
        // Load the favorites from SharedPreferences
        favoritesSet = prefs.getStringSet("FavoriteLocations", new HashSet<>());
        return favoritesSet; // Return the Set of favorites
    }
}
