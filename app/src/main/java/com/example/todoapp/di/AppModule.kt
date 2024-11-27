package com.example.todoapp.di


import android.content.Context
import com.example.todoapp.data.local.RevisionStorage
import com.example.todoapp.data.network.NetworkStatusProvider
import com.example.todoapp.data.network.NetworkStatusTracker
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Binds
    abstract fun bindNetworkStatusProvider(
        networkStatusTracker: NetworkStatusTracker
    ): NetworkStatusProvider

    companion object {

        @Singleton
        @Provides
        fun provideRevisionStorage(context: Context): RevisionStorage {
            return RevisionStorage(context)
        }
    }
}