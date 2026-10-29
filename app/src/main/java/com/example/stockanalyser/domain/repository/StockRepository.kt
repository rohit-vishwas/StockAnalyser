package com.example.stockanalyser.domain.repository

import com.example.stockanalyser.domain.model.ConnectionStatus
import com.example.stockanalyser.domain.model.Stock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface StockRepository {

    val connectionStatus: StateFlow<ConnectionStatus>

    fun observeStocks(): Flow<List<Stock>>
}
