package com.example.stockanalyser.di

import com.example.stockanalyser.data.remote.BinanceSocketClient
import com.example.stockanalyser.data.repository.StockRepositoryImpl
import com.example.stockanalyser.domain.repository.StockRepository
import com.example.stockanalyser.domain.usecase.ObserveWatchlistUseCase
import com.example.stockanalyser.ui.watchlist.WatchlistViewModel
import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val appModule = module {

    single {
        OkHttpClient.Builder()
            .pingInterval(20, TimeUnit.SECONDS)
            .build()
    }

    single { Gson() }

    single { BinanceSocketClient(get()) }

    single<StockRepository> { StockRepositoryImpl(get(), get()) }

    factory { ObserveWatchlistUseCase(get()) }

    viewModel { WatchlistViewModel(get()) }
}
