package com.example.stockanalyser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.stockanalyser.ui.theme.StockAnalyserTheme
import com.example.stockanalyser.ui.watchlist.WatchlistScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StockAnalyserTheme {
                WatchlistScreen()
            }
        }
    }
}
