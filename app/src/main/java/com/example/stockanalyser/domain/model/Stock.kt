package com.example.stockanalyser.domain.model

data class Stock(
    val symbol: String,
    val baseAsset: String,
    val name: String,
    val price: Double,
    val changePercent: Double,
    val direction: PriceDirection
)
