package com.example.stockanalyser.data.remote

sealed interface SocketEvent {
    data object Connected : SocketEvent
    data class Message(val text: String) : SocketEvent
}
