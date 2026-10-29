package com.example.stockanalyser.ui.watchlist

import androidx.compose.runtime.Immutable
import com.example.stockanalyser.domain.model.PriceDirection
import com.example.stockanalyser.domain.model.Stock
import java.util.Locale

@Immutable
data class StockUiModel(
    val symbol: String,
    val baseAsset: String,
    val name: String,
    val iconUrl: String,
    val price: Double,
    val priceText: String,
    val changeText: String,
    val isChangePositive: Boolean,
    val direction: PriceDirection
)

private const val ICON_BASE_URL = "https://cdn.jsdelivr.net/gh/spothq/cryptocurrency-icons@master/128/color"

fun Stock.toUiModel(): StockUiModel {
    val decimals = if (price >= 1) 2 else 4
    return StockUiModel(
        symbol = symbol,
        baseAsset = baseAsset,
        name = name,
        iconUrl = "$ICON_BASE_URL/${baseAsset.lowercase()}.png",
        price = price,
        priceText = "$" + String.format(Locale.US, "%,.${decimals}f", price),
        changeText = String.format(Locale.US, "%+.2f%%", changePercent),
        isChangePositive = changePercent >= 0,
        direction = direction
    )
}
