package com.shuhart.testlook.di

import com.shuhart.testlook.modules.flight.search.airport.SearchAirportActivity
import com.shuhart.testlook.modules.flight.search.airport.SearchAirportModule
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [(SearchAirportModule::class)])
    internal abstract fun contributeSearchAiportInjector(): SearchAirportActivity

}