package com.example.todoapp.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object NetworkModule {
    private const val BASE_URL = "https://hive.mrdekk.ru/todo/"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val contentType = "application/json".toMediaType()
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            response
        }
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    val apiService: TodoApiService = retrofit.create(TodoApiService::class.java)
}
