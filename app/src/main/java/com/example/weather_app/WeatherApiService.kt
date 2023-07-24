package com.example.weather_app


import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast.json")
    fun getWeatherForecast(
        @Query("key") apiKey: String,
        @Query("q") location: String,
    ): Call<WeatherResponse>
   }