package com.example.activitytracker.data.remote

import com.example.activitytracker.BuildConfig
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.OffsetDateTime

object RetrofitClient {

    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, JsonSerializer<LocalDate> { src, _, _ ->
            JsonPrimitive(src.toString())
        })
        .registerTypeAdapter(OffsetDateTime::class.java, JsonSerializer<OffsetDateTime> { src, _, _ ->
            JsonPrimitive(src.toString())
        })
        .create()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}