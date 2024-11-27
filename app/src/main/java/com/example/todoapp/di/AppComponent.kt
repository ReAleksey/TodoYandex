package com.example.todoapp.di


import android.content.Context
import com.example.todoapp.ui.view.TodoApp
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent {

    fun inject(app: TodoApp)

    fun activityComponent(): ActivityComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}