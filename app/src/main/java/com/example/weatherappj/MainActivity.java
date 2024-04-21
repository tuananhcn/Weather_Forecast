package com.example.weatherappj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.weatherappj.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private RelativeLayout homeRL;
    private Intent ChartIntent;
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
    public static final String[] languages ={"Select Language","English","Vietnamese","Hindi"};
    ActivityMainBinding binding;
    String searchText;
    FirebaseDatabase db;
    DatabaseReference reference;
    public void setLocal(Activity activity, String langCode) {
        Locale locale = new Locale(langCode);
        locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Transparent Toolbar
        toolbar.getBackground().setAlpha(0);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


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
        favoriteIV = findViewById(R.id.iv_favorite);

        reference = FirebaseDatabase.getInstance().getReference("SearchData");

//        searchIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.
                ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }
        else {
            getLocationAndWeatherInfo(); // Permissions already granted, proceed to get location and weather info
        }
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ;
//        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.
//                ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
//
//            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        if (location != null){cityName = getCityName(location.getLongitude(),location.getLatitude());
//            getWeatherInfo(cityName);
////            updateFavoriteIcon(cityName, favoriteIV);
//        } else {
//              cityName = "Ranchi";
//             getWeatherInfo(cityName);
////            updateFavoriteIcon(cityName, favoriteIV);
//
//            // Toast.makeText(this, "Invalid User's Location", Toast.LENGTH_SHORT).show();
//
//        }

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityName = cityEDT.getText().toString(); //city
                if (cityName.isEmpty()){ //city
                    Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
                }else {
                    cityNameTV.setText(cityName);
                    getWeatherInfo(cityName); // city
                    refreshFavoritesAndUI();
                }
            }
        });
        prefs = getSharedPreferences("FAVORITES", MODE_PRIVATE);
        favorites = prefs.getStringSet("FavoriteLocations", new HashSet<>());
        Log.d("Test Favorite", "Favorite: " + favorites);
//        updateFavoriteIcon(cityName, favoriteIV);
//        final ImageView favoriteIV = findViewById(R.id.iv_favorite);
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
                favoriteIV.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshFavoritesAndUI();
                    }
                });
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        // Handle navigation view item clicks here.
                        int id = item.getItemId();

                        if (id == R.id.nav_favorites) {
                            Intent intent = new Intent(MainActivity.this, FavoriteWeatherActivity.class);
                            startActivity(intent);
                        } else if (id == R.id.nav_share) {
                            if (temperatureTV.getText() != null && conditionTv.getText() != null) {
                                // Lấy thông tin từ các TextView
                                String cityName = cityNameTV.getText().toString();
                                String temperature = temperatureTV.getText().toString();
                                String condition = conditionTv.getText().toString();

                                String dataToShare = "City: " + cityName + "\n"
                                        + "Temperature: " + temperature + "\n"
                                        + "Condition: " + condition;
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, dataToShare);
                                startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
                            } else {
                                Toast.makeText(getApplicationContext(), "Không thể chia sẻ do thiếu thông tin", Toast.LENGTH_SHORT).show();
                            }
                        } else if (id == R.id.lang_english) {
                            setLocal(MainActivity.this,"en");
                            finish();
                            startActivity(getIntent());
                        } else if (id == R.id.lang_hindi) {
                            setLocal(MainActivity.this, "hi");
                            finish();
                            startActivity(getIntent());
                        } else if (id == R.id.lang_vietnamese) {
                            setLocal(MainActivity.this, "vi");
                            finish();
                            startActivity(getIntent());
                        } else if (id == R.id.nav_chart) {
                            startActivity(ChartIntent);
                        }
                        // Add other language handling as necessary

                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                        return true;
                    }
                });
    }
    private void getLocationAndWeatherInfo() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                cityName = getCityName(location.getLongitude(), location.getLatitude());
                getWeatherInfo(cityName);
            } else {
                cityName = "Ho Chi Minh"; // Default city or let the user choose one
                getWeatherInfo(cityName);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission granted....", Toast.LENGTH_SHORT).show();
                getLocationAndWeatherInfo();
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
//                        Toast.makeText(this, "User city not found..", Toast.LENGTH_SHORT).show();
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
                    displayWeatherNotification(temperature, condition, conditionIcon);
                    if (isDay==1){
                        //morning
                        Picasso.get().load("https://images.unsplash.com/photo-1444041103143-1d0586b9c0b8?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1374&q=80").into(backIV);
                    }else {
                        Picasso.get().load("https://images.unsplash.com/photo-1507400492013-162706c8c05e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1559&q=80").into(backIV);
                    }
                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forcastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forcastO.getJSONArray("hour");
                    ArrayList<Float> hourlyTemperatures = new ArrayList<>();
                    ArrayList<Float> hourlyRain = new ArrayList<>();
                    ArrayList<Float> hourlyUVIndex = new ArrayList<>();
                    ArrayList<Float> hourlyWindSpeed = new ArrayList<>();
                    ArrayList<Float> hourlyHumidity = new ArrayList<>();
                    for(int i=0; i<hourArray.length(); i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRvModelArrayList.add(new WeatherRvModel(time, temper, img, wind));
                        hourlyTemperatures.add((float) hourObj.getDouble("temp_c"));
                        hourlyRain.add((float) hourObj.getDouble("precip_mm"));
                        hourlyUVIndex.add((float) hourObj.getDouble("uv"));
                        hourlyWindSpeed.add((float) hourObj.getDouble("wind_kph"));
                        hourlyHumidity.add((float) hourObj.getDouble("humidity"));
                    }
                    ChartIntent = new Intent(MainActivity.this, ChartActivity.class);
                    ChartIntent.putExtra("HourlyTemperatures", hourlyTemperatures);
                    ChartIntent.putExtra("HourlyRain", hourlyRain);
                    ChartIntent.putExtra("HourlyUVIndex", hourlyUVIndex);
                    ChartIntent.putExtra("HourlyWindSpeed", hourlyWindSpeed);
                    ChartIntent.putExtra("HourlyHumidity", hourlyHumidity);
                    weatherRVAdapter.notifyDataSetChanged();
                    Date currentDate = new Date();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    String currentDateTime = dateFormat.format(currentDate);
                    String forecastDay = forcastO.toString();
                    String data = cityName;
                    String currentTime= currentDateTime.toString();
                    String childTitle = data + " " + currentTime;
                    Log.d("childTitle", data);
                    SearchData searchData = new SearchData(data,forecastDay);

                    reference.child(childTitle).setValue(searchData);


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
        Set<String> currentFavorites = prefs.getStringSet("FavoriteLocations", new HashSet<>());
        // Create a new set from the current one (important for SharedPreferences)
        Set<String> newFavorites = new HashSet<>(currentFavorites);
        // Add the new favorite
        newFavorites.add(location);
        // Save the new set
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("FavoriteLocations", newFavorites);
        editor.commit(); // or editor.commit(); if you want to know the result
    }
    private void displayWeatherNotification(String temperature, String condition, String iconUrl) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return; // Exit if NotificationManager not available
        }

        String channelId = "weather"; // The id of the channel.

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Weather Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = null;
        int pendingIntentFlag = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M ?
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

        pendingIntent = PendingIntent.getActivity(this, 0, intent, pendingIntentFlag);
        Notification.Builder notificationBuilder = new Notification.Builder(this, "weather")
                .setContentTitle(String.format(getString(R.string.current_weather), cityName))
                .setContentText(String.format(getString(R.string.temperature_condition), temperature, condition))
                .setSmallIcon(R.drawable.cloudy) // Đặt một biểu tượng cho thông báo
//                .setLargeIcon(Picasso.get().load(iconUrl).get()) // Lấy hình ảnh từ Picasso
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        // Gửi thông báo
        notificationManager.notify(1, notificationBuilder.build());
    }

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
        editor.commit(); // or edl.itor.commit(); if you want to know the result
    }

    private boolean isLocationFavorite(String location) {
        boolean isFavorite = favorites.contains(location);
        Log.d("MainActivity", "isLocationFavorite: " + location + " - " + isFavorite);
        return isFavorite;
    }

    private void updateFavoriteIcon(String location, ImageView favoriteIcon) {
        if (isLocationFavorite(location)) {
            favoriteIcon.setImageResource(R.drawable.filled_favorite);
        } else {
            favoriteIcon.setImageResource(R.drawable.favorite_icon);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Handle the intent from FavoriteWeatherActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("CITY_NAME")) {
            String city = intent.getStringExtra("CITY_NAME");
            if (city != null && !city.isEmpty()) {
                cityNameTV.setText(city);
                getWeatherInfo(city); // Perform the search
                refreshFavoritesAndUI();
                updateFavoriteIcon(city, favoriteIV); // Update the favorite icon
            }
        }
    }
private void refreshFavoritesAndUI() {
    // Always get the latest set of favorites from SharedPreferences
    prefs = getSharedPreferences("FAVORITES", MODE_PRIVATE);
    favorites = new HashSet<>(prefs.getStringSet("FavoriteLocations", new HashSet<>()));

    // Refresh the UI with the updated favorite icon
    if (cityName != null && !cityName.isEmpty()) {
        updateFavoriteIcon(cityName, favoriteIV);
    }
}

    @Override
    protected void onResume() {
        super.onResume();
//        refreshFavoritesAndUI();
    }
    @Override
    public void onBackPressed() {
        // Close the drawer if it's open when the back button is pressed
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
