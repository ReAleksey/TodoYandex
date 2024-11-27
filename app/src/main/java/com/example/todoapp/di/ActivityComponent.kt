package com.example.todoapp.di

import com.example.todoapp.ui.view.MainActivity
import dagger.Subcomponent

@Subcomponent(
    modules = [
        ViewModelModule::class
    ]
)
interface ActivityComponent {

    fun inject(activity: MainActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(): ActivityComponent
    }
}