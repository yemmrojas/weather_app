package com.yei.dev.weather_app.di

import com.yei.dev.weather_app.di.qualifier.AppName
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @AppName
    fun provideAppName(): String = "WeatherApp"
}
