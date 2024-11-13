package com.example.todoapp.data.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    private val bearerToken = "Bearer Lalaith"
    private val oAuthToken = "OAuth y0_AgAAAAAkCDffAARC0QAAAAEYab1ZAAAjDkLLqB1ByKzR2RvUhcyYsgSnPQ"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", oAuthToken)
            .build()
        return chain.proceed(request)
    }
}
