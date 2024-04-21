package com.example.weatherappj;

import org.json.JSONObject;

public class SearchData {
    String textFind;



    String time;

    public String getForecastday() {
        return forecastday;
    }

    public void setForecastday(String forecastday) {
        this.forecastday = forecastday;
    }

    String forecastday;
    public SearchData(){}
    public SearchData(String textFind, String forecastday){
        this.textFind =textFind;
        this.forecastday = forecastday;
    }

    public String getTextFind() {
        return textFind;
    }

    public void setTextFind(String textFind) {
        this.textFind = textFind;
    }
}
