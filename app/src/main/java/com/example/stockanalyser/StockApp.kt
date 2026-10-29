package com.example.stockanalyser

import android.app.Application
import com.example.stockanalyser.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class StockApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StockApp)
            modules(appModule)
        }
    }
}
