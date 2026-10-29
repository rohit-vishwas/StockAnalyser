package com.example.stockanalyser.ui.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockanalyser.domain.usecase.ObserveWatchlistUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

class WatchlistViewModel(observeWatchlist: ObserveWatchlistUseCase) : ViewModel() {

    val uiState: StateFlow<WatchlistUiState> =
        combine(observeWatchlist(), observeWatchlist.connectionStatus) { stocks, status ->
            WatchlistUiState(
                isLoading = stocks.isEmpty(),
                stocks = stocks.map { it.toUiModel() },
                status = status
            )
        }
            .flowOn(Dispatchers.Default)
            .conflate()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = WatchlistUiState()
            )
}
