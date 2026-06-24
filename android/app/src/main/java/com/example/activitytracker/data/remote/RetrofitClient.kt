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
import com.example.activitytracker.data.remote.dto.RefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.net.HttpURLConnection

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

    private lateinit var authStorage: AuthStorage

    fun initialize(authStorage: AuthStorage) {
        this.authStorage = authStorage
    }

    private fun buildClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor(authStorage))
            .authenticator(TokenAuthenticator(authStorage))
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

    var accessToken: String? = null
        private set

    fun setToken(token: String?) {
        accessToken = token
    }

    internal val refreshApi: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(OkHttpClient.Builder().addInterceptor(logging).build())
            .build()
            .create(ApiService::class.java)
    }

    class TokenAuthenticator(private val authStorage: AuthStorage) : Authenticator {

        companion object {
            private val lock = Any()
        }

        override fun authenticate(route: Route?, response: Response): Request? {
            if (responseCount(response) >= 2) return null

            val failedToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            synchronized(lock) {
                val current = accessToken
                if (current != null && current != failedToken) {
                    return response.request.newBuilder()
                        .header("Authorization", "Bearer $current")
                        .build()
                }

                val refreshToken = runBlocking { authStorage.getRefreshToken() } ?: return null

                val refreshResponse = runBlocking {
                    try {
                        refreshApi.refreshToken(RefreshRequest(refreshToken))
                    } catch (_: Exception) {
                        null
                    }
                }

                if (refreshResponse == null) return null

                if (refreshResponse.code() == HttpURLConnection.HTTP_BAD_REQUEST ||
                    refreshResponse.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    authStorage.clearAll()
                    setToken(null)
                    return null
                }

                if (!refreshResponse.isSuccessful) return null

                val body = refreshResponse.body() ?: return null
                authStorage.saveAccessToken(body.accessToken)
                authStorage.saveRefreshToken(body.refreshToken)
                setToken(body.accessToken)

                return response.request.newBuilder()
                    .header("Authorization", "Bearer ${body.accessToken}")
                    .build()
            }
        }

        private fun responseCount(response: Response): Int {
            var count = 1
            var prior = response.priorResponse
            while (prior != null) {
                count++
                prior = prior.priorResponse
            }
            return count
        }
    }
}