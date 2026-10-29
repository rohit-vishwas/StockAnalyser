package com.example.stockanalyser.data.repository

import com.example.stockanalyser.data.WatchlistConfig
import com.example.stockanalyser.data.remote.BinanceSocketClient
import com.example.stockanalyser.data.remote.SocketEvent
import com.example.stockanalyser.data.remote.StreamMessage
import com.example.stockanalyser.data.remote.TickerDto
import com.example.stockanalyser.domain.model.ConnectionStatus
import com.example.stockanalyser.domain.model.PriceDirection
import com.example.stockanalyser.domain.model.Stock
import com.example.stockanalyser.domain.repository.StockRepository
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen

class StockRepositoryImpl(
    private val socketClient: BinanceSocketClient,
    private val gson: Gson
) : StockRepository {

    private val coins = WatchlistConfig.coins
    private val coinsBySymbol = coins.associateBy { it.symbol }

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.CONNECTING)
    override val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private var latestStocks = emptyMap<String, Stock>()

    override fun observeStocks(): Flow<List<Stock>> = flow {
        _connectionStatus.value = ConnectionStatus.CONNECTING
        emit(orderedStocks())

        socketEvents()
            .filterIsInstance<SocketEvent.Message>()
            .mapNotNull { parseTicker(it.text) }
            .collect { ticker ->
                val updated = toStock(ticker) ?: return@collect
                latestStocks = latestStocks + (updated.symbol to updated)
                emit(orderedStocks())
            }
    }

    private fun socketEvents(): Flow<SocketEvent> =
        socketClient.observe(coins.map { it.symbol })
            .onEach { event ->
                if (event is SocketEvent.Connected) {
                    _connectionStatus.value = ConnectionStatus.LIVE
                }
            }
            .retryWhen { _, _ ->
                _connectionStatus.value = ConnectionStatus.RECONNECTING
                delay(RETRY_DELAY_MS)
                true
            }

    private fun parseTicker(text: String): TickerDto? {
        return try {
            gson.fromJson(text, StreamMessage::class.java)?.data
        } catch (e: JsonSyntaxException) {
            null
        }
    }

    private fun toStock(ticker: TickerDto): Stock? {
        val coin = coinsBySymbol[ticker.symbol] ?: return null
        val price = ticker.closePrice?.toDoubleOrNull() ?: return null
        val openPrice = ticker.openPrice?.toDoubleOrNull() ?: price
        val previous = latestStocks[coin.symbol]

        val direction = when {
            previous == null -> PriceDirection.NONE
            price > previous.price -> PriceDirection.UP
            price < previous.price -> PriceDirection.DOWN
            else -> previous.direction
        }

        val changePercent = if (openPrice != 0.0) (price - openPrice) / openPrice * 100 else 0.0

        return Stock(
            symbol = coin.symbol,
            baseAsset = coin.baseAsset,
            name = coin.name,
            price = price,
            changePercent = changePercent,
            direction = direction
        )
    }

    private fun orderedStocks(): List<Stock> = coins.mapNotNull { latestStocks[it.symbol] }

    companion object {
        private const val RETRY_DELAY_MS = 3000L
    }
}
