package com.example.activitytracker.data.remote

import com.example.activitytracker.BuildConfig
import com.example.activitytracker.data.local.storage.AuthStorage
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDate
import java.time.OffsetDateTime

object RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, JsonSerializer<LocalDate> { src, _, _ ->
            JsonPrimitive(src.toString())
        })
        .registerTypeAdapter(OffsetDateTime::class.java, JsonSerializer<OffsetDateTime> { src, _, _ ->
            JsonPrimitive(src.toString())
        })
        .create()

    // ✅ wird in ActivityApplication gesetzt
    private lateinit var authStorage: AuthStorage

    fun initialize(authStorage: AuthStorage) {
        this.authStorage = authStorage
    }

    // ✅ baut den Client frisch mit Auth Interceptor
    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(authStorage))
            .build()
    }

    private fun buildRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(buildClient())
            .build()
    }

    val api: ApiService by lazy { buildRetrofit().create(ApiService::class.java) }

    val weatherApi: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(buildClient())
            .build()
            .create(ApiService::class.java)
    }

    val friendApi: FriendApiService by lazy {
        buildRetrofit().create(FriendApiService::class.java)
    }
}