package com.example.weatherappj;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteWeatherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_activity);

        List<String> favorites = loadFavorites(); // Giả sử bạn có hàm này để tải dữ liệu
        RecyclerView recyclerView = findViewById(R.id.recyclerViewFavorites);
        FavoriteAdapter adapter = new FavoriteAdapter(favorites);
        recyclerView.setAdapter(adapter);
    }

    private List<String> loadFavorites() {
        // Tải dữ liệu từ SharedPreferences hoặc Database
        return new ArrayList<>(); // Trả về danh sách yêu thích
    }
}

