package com.example.weatherforecast.mappers

import com.example.data.entity.Coordinates
import com.example.data.entity.SettingsState
import com.example.data.entity.WeatherEntity
import com.example.weatherforecast.utils.FORMAT_EEE_HH_mm
import com.example.weatherforecast.utils.FORMAT_EEE_d_MMMM_HH_mm
import com.example.weatherforecast.model.SideEffect
import com.example.weatherforecast.model.WeatherState
import com.example.weatherforecast.model.WeatherType
import com.example.weatherforecast.utils.format
import com.example.weatherforecast.model.Units
import kotlin.math.roundToInt

internal fun WeatherEntity.toWeatherState(
    userMessage: SideEffect<Int?>,
    isLoading: Boolean,
    emptyCityError: Boolean
): WeatherState = WeatherState(
    isFavorites = isFavorites,
    id = id,
    coordinates = Coordinates(
        lat = lat,
        lon = lon
    ),
    city = city,
    country = country,
    temperature = "${temperature.roundToInt()}${Units.getUnits(settingsState.selectedUnits)}",
    icon = WeatherType.find(icon),
    description = description,
    feelsLike = "${feelsLike.roundToInt()}°",
    humidity = "$humidity %",
    pressure = "$pressure hPa",
    windSpeed = "${windSpeed.roundToInt()} m/s",
    data = data.format(FORMAT_EEE_d_MMMM_HH_mm, timezone),
    timezone = timezone,
    forecastState = forecast.toForecastState(FORMAT_EEE_HH_mm, timezone),
    airState = air.toAirState(),
    userMessage = userMessage,
    isLoading = isLoading,
    emptyCityError = emptyCityError
)