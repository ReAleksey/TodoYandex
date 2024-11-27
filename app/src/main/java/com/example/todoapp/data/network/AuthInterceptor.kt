package com.example.todoapp.data.network

import okhttp3.Interceptor
import okhttp3.Response
import com.example.todoapp.BuildConfig


class AuthInterceptor : Interceptor {
    private val bearerToken = BuildConfig.BEARER_TOKEN
    private val oAuthToken = BuildConfig.OAUTH_TOKEN

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", oAuthToken)
            .build()
        return chain.proceed(request)
    }
}

