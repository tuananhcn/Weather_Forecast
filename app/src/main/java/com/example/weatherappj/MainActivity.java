package com.example.weatherappj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private String cityName;
    private ProgressBar loadingPB;
    private TextView cityNameTV, temperatureTV, conditionTv;
    private TextInputEditText cityEDT;
    private ImageView backIV, iconIV, searchIV;
    private RecyclerView weatherRv;
    private ArrayList<WeatherRvModel> weatherRvModelArrayList;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private ImageView favoriteIV;
    private Set<String> favorites;
    private SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        homeRL = findViewById(R.id.IdRlHome);
        loadingPB = findViewById(R.id.IdPbLoading);
        cityNameTV = findViewById(R.id.IdTvCityName);
        temperatureTV = findViewById(R.id.IdTvTempearture);
        conditionTv = findViewById(R.id.IDtVCondition);
        weatherRv = findViewById(R.id.Idrvweather);
        cityEDT = findViewById(R.id.Ideditcity);
        backIV = findViewById(R.id.Idivback);
        iconIV = findViewById(R.id.IdIvIcon);
        searchIV = findViewById(R.id.IdIvSearch);
        weatherRvModelArrayList = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRvModelArrayList);
        weatherRv.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ;
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.
                ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null){cityName = getCityName(location.getLongitude(),location.getLatitude());
            getWeatherInfo(cityName);
//            updateFavoriteIcon(cityName, favoriteIV);
        } else {
              cityName = "Ranchi";
             getWeatherInfo(cityName);
//            updateFavoriteIcon(cityName, favoriteIV);

            // Toast.makeText(this, "Invalid User's Location", Toast.LENGTH_SHORT).show();

        }

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEDT.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
                }else {
                    cityNameTV.setText(cityName);
                    getWeatherInfo(city);
//                    updateFavoriteIcon(cityName, favoriteIV);

                }
            }
        });
        Button btnFavWeather = findViewById(R.id.btnFavWeather);
        btnFavWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FavoriteWeatherActivity.class);
                startActivity(intent);
            }
        });
//        favoriteIV = findViewById(R.id.iv_favorite);
        prefs = getSharedPreferences("FAVORITES", MODE_PRIVATE);
        favorites = prefs.getStringSet("FavoriteLocations", new HashSet<>());
        cityName = getCityName(location.getLongitude(), location.getLatitude());
//        updateFavoriteIcon(cityName, favoriteIV);
        final ImageView favoriteIV = findViewById(R.id.iv_favorite);
        favoriteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Refresh the favorites set from SharedPreferences
                favorites = new HashSet<>(prefs.getStringSet("FavoriteLocations", new HashSet<>()));

                if (favorites.contains(cityName)) {
                    removeLocationFromFavorites(cityName);
                    Toast.makeText(MainActivity.this, cityName + " removed from favorites!", Toast.LENGTH_SHORT).show();
                } else {
                    addLocationToFavorites(cityName);
                    Toast.makeText(MainActivity.this, cityName + " added to favorites!", Toast.LENGTH_SHORT).show();
                }
                updateFavoriteIcon(cityName, favoriteIV);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted....", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Please Provide the permission...", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude,10);

            for (Address adr : addresses){
                if (adr != null){
                    String city = adr.getLocality();
                    if (city != null && !city.equals("")){
                        cityName = city;
                    }else {
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this, "User city not found..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }
    private void getWeatherInfo(String cityName){
        //http://api.weatherapi.com/v1/forecast.json?key=03dd97a21d0e43f6bd550527232905&q=delhi&days=1&aqi=yes&alerts=yes
        String url = "http://api.weatherapi.com/v1/forecast.json?key=03dd97a21d0e43f6bd550527232905&q=" + cityName + "&days=1&aqi=yes&alerts=yes";
        cityNameTV.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRvModelArrayList.clear();

                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature+"°C");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTv.setText(condition);
                    if (isDay==1){
                        //morning
                        Picasso.get().load("https://images.unsplash.com/photo-1444041103143-1d0586b9c0b8?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1374&q=80").into(backIV);
                    }else {
                        Picasso.get().load("https://images.unsplash.com/photo-1507400492013-162706c8c05e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1559&q=80").into(backIV);
                    }
                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forcastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forcastO.getJSONArray("hour");

                    for(int i=0; i<hourArray.length(); i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRvModelArrayList.add(new WeatherRvModel(time, temper, img, wind));
                    }
                    weatherRVAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    private void addLocationToFavorites(String location) {
        Set<String> newFavorites = new HashSet<>(favorites);
        newFavorites.add(location);
        prefs.edit().putStringSet("FavoriteLocations", newFavorites).commit(); // Commit the changes
        // Update the favorites set
        favorites = newFavorites;
    }

    private void removeLocationFromFavorites(String location) {
        Set<String> newFavorites = new HashSet<>(favorites);
        newFavorites.remove(location);
        prefs.edit().putStringSet("FavoriteLocations", newFavorites).commit(); // Commit the changes
        // Update the favorites set
        favorites = newFavorites;
    }

    private boolean isLocationFavorite(String location) {
        return favorites.contains(location);
    }

    private void updateFavoriteIcon(String location, ImageView favoriteIcon) {
        if (isLocationFavorite(location)) {
            favoriteIcon.setImageResource(R.drawable.filled_favorite);
        } else {
            favoriteIcon.setImageResource(R.drawable.favorite_icon);
        }
    }
}