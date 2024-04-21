package com.example.weatherappj;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_activity);

        chart = findViewById(R.id.chart);

        // Lấy dữ liệu được truyền từ MainActivity
        ArrayList<Float> hourlyTemperatures = (ArrayList<Float>) getIntent().getSerializableExtra("HourlyTemperatures");
        ArrayList<Float> hourlyRain = (ArrayList<Float>) getIntent().getSerializableExtra("HourlyRain");
        ArrayList<Float> hourlyUVIndex = (ArrayList<Float>) getIntent().getSerializableExtra("HourlyUVIndex");
        ArrayList<Float> hourlyWindSpeed = (ArrayList<Float>) getIntent().getSerializableExtra("HourlyWindSpeed");
        ArrayList<Float> hourlyHumidity = (ArrayList<Float>) getIntent().getSerializableExtra("HourlyHumidity");

        // Setup the chart with these data
        setupChart(hourlyTemperatures, hourlyRain, hourlyUVIndex, hourlyWindSpeed, hourlyHumidity);
    }

    private void setupChart(ArrayList<Float> temperatures, ArrayList<Float> rain,
                            ArrayList<Float> uvIndex, ArrayList<Float> windSpeed,
                            ArrayList<Float> humidity) {
        List<Entry> temperatureEntries = new ArrayList<>();
        List<Entry> rainEntries = new ArrayList<>();
        List<Entry> uvEntries = new ArrayList<>();
        List<Entry> windEntries = new ArrayList<>();
        List<Entry> humidityEntries = new ArrayList<>();

        for (int i = 0; i < temperatures.size(); i++) {
            temperatureEntries.add(new Entry(i, temperatures.get(i)));
            rainEntries.add(new Entry(i, rain.get(i)));
            uvEntries.add(new Entry(i, uvIndex.get(i)));
            windEntries.add(new Entry(i, windSpeed.get(i)));
            humidityEntries.add(new Entry(i, humidity.get(i)));
        }

        LineDataSet temperatureDataSet = new LineDataSet(temperatureEntries, "Temperature");
        temperatureDataSet.setColor(Color.RED);
        temperatureDataSet.setValueTextColor(Color.WHITE);

        LineDataSet rainDataSet = new LineDataSet(rainEntries, "Rainfall");
        rainDataSet.setColor(Color.BLUE);
        rainDataSet.setValueTextColor(Color.WHITE);

        LineDataSet uvDataSet = new LineDataSet(uvEntries, "UV Index");
        uvDataSet.setColor(Color.YELLOW);
        uvDataSet.setValueTextColor(Color.WHITE);

        LineDataSet windDataSet = new LineDataSet(windEntries, "Wind Speed");
        windDataSet.setColor(Color.GREEN);
        windDataSet.setValueTextColor(Color.WHITE);

        LineDataSet humidityDataSet = new LineDataSet(humidityEntries, "Humidity");
        humidityDataSet.setColor(Color.CYAN);
        humidityDataSet.setValueTextColor(Color.WHITE);

        // Combine all LineDataSets into LineData object
        LineData data = new LineData(temperatureDataSet, rainDataSet, uvDataSet, windDataSet, humidityDataSet);
        chart.setData(data);
        chart.invalidate(); // refreshes the chart
    }
}
