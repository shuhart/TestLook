package com.shuhart.testlook.modules.flight.search.airport

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import com.shuhart.testlook.api.network.Api
import com.shuhart.testlook.di.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import com.shuhart.testlook.di.ViewModelKey


@Module
abstract class SearchAirportModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchAirportViewModel::class)
    abstract fun bindShowDetailsActivityViewModel(viewModel: SearchAirportViewModel): ViewModel

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideSearchAirPortPresenter(activity: SearchAirportActivity,
                                          context: Context,
                                          viewModelFactory: ViewModelFactory): SearchAirportPresenter {
            val viewModel = ViewModelProviders.of(activity, viewModelFactory).get(SearchAirportViewModel::class.java)
            return SearchAirportPresenterImpl(viewModel, activity, context, activity)
        }

        @JvmStatic
        @Provides
        fun provideInteractor(api: Api): SearchAirportInteractor {
            return SearchAirportInteractorImpl(api)
        }
    }
}