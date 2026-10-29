package com.example.stockanalyser.data

data class CoinInfo(
    val symbol: String,
    val baseAsset: String,
    val name: String
)

object WatchlistConfig {

    val coins = listOf(
        CoinInfo("BTCUSDT", "BTC", "Bitcoin"),
        CoinInfo("ETHUSDT", "ETH", "Ethereum"),
        CoinInfo("BNBUSDT", "BNB", "BNB"),
        CoinInfo("SOLUSDT", "SOL", "Solana"),
        CoinInfo("XRPUSDT", "XRP", "XRP"),
        CoinInfo("ADAUSDT", "ADA", "Cardano"),
        CoinInfo("DOGEUSDT", "DOGE", "Dogecoin"),
        CoinInfo("LTCUSDT", "LTC", "Litecoin")
    )
}
