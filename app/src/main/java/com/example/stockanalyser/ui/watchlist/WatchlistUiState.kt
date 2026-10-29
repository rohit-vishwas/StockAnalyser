package com.example.stockanalyser.ui.watchlist

import androidx.compose.runtime.Immutable
import com.example.stockanalyser.domain.model.ConnectionStatus

@Immutable
data class WatchlistUiState(
    val isLoading: Boolean = true,
    val stocks: List<StockUiModel> = emptyList(),
    val status: ConnectionStatus = ConnectionStatus.CONNECTING
)
