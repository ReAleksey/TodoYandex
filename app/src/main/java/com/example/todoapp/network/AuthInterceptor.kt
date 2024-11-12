package com.example.todoapp.network

import okhttp3.Interceptor
import okhttp3.Response
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets

class AuthInterceptor : Interceptor {
    private val token = "Bearer Lalaith"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", token)
            .build()
        return chain.proceed(request)
    }
}
