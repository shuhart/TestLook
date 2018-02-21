package com.shuhart.testlook.modules.flight.search.airport

import android.content.Context
import com.shuhart.testlook.api.network.Api
import dagger.Binds
import dagger.Module
import dagger.Provides


@Module
abstract class SearchAirportModule {

    @Binds
    abstract fun bindSearchAirportView(activity: SearchAirportActivity): SearchAirportView

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideSearchAirPortPresenter(view: SearchAirportView,
                                          context: Context,
                                          api: Api): SearchAirportPresenter {
            return SearchAirportPresenterImpl(SearchAirportInteractorImpl(api), view, context)
        }
    }
}