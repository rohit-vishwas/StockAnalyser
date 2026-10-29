package com.example.stockanalyser.domain.usecase

import com.example.stockanalyser.domain.model.ConnectionStatus
import com.example.stockanalyser.domain.model.Stock
import com.example.stockanalyser.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class ObserveWatchlistUseCase(private val repository: StockRepository) {

    val connectionStatus: StateFlow<ConnectionStatus>
        get() = repository.connectionStatus

    operator fun invoke(): Flow<List<Stock>> = repository.observeStocks()
}
