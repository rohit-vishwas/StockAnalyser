package com.example.stockanalyser.data.remote

import com.google.gson.annotations.SerializedName

data class StreamMessage(
    @SerializedName("stream") val stream: String? = null,
    @SerializedName("data") val data: TickerDto? = null
)

data class TickerDto(
    @SerializedName("s") val symbol: String? = null,
    @SerializedName("c") val closePrice: String? = null,
    @SerializedName("o") val openPrice: String? = null
)
