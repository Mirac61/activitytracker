package com.example.activitytracker.data.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("/api/weather")
    fun getWeather(@Query("lat") lat: Double, @Query("lon") lon: Double): Call<ResponseBody>
}