package com.example.activitytracker.data.remote

import com.example.activitytracker.data.local.storage.AuthStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authStorage: AuthStorage?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenFromRetrofit = RetrofitClient.accessToken
        val tokenFromStorage = runBlocking { authStorage?.getAccessToken() }
        val token = tokenFromRetrofit ?: tokenFromStorage

        android.util.Log.d("AuthInterceptor", "tokenFromRetrofit: $tokenFromRetrofit")
        android.util.Log.d("AuthInterceptor", "tokenFromStorage: ${tokenFromStorage?.take(20)}")

        val request = chain.request().newBuilder()
            .apply {
                if (token != null) {
                    addHeader("Authorization", "Bearer $token")
                }
            }
            .build()

        return chain.proceed(request)
    }
}