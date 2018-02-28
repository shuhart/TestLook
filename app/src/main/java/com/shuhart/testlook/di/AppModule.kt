package com.shuhart.testlook.di

import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.shuhart.testlook.api.network.Api
import com.shuhart.testlook.api.network.Rest
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
abstract class AppModule {

    @Binds
    abstract fun provideContext(application: Application): Context

    @Module
    companion object {
        @JvmStatic
        @Provides
        @Singleton
        fun provideApi(): Api = Rest().create()
    }

}