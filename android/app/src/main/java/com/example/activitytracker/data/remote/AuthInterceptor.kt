package com.example.activitytracker.data.remote

import com.example.activitytracker.data.local.storage.AuthStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val authStorage: AuthStorage?) : Interceptor {
    private val noAuthPaths = listOf(
        "/api/users/login",
        "/api/users/register",
        "/api/users/refresh",
        "/api/users/google",
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val path = chain.request().url.encodedPath

        if (noAuthPaths.any { path.endsWith(it) }) {
            return chain.proceed(chain.request())
        }

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