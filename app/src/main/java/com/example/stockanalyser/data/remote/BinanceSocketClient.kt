package com.example.stockanalyser.data.remote

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.IOException

class BinanceSocketClient(private val client: OkHttpClient) {

    fun observe(symbols: List<String>): Flow<SocketEvent> = callbackFlow {
        val streams = symbols.joinToString("/") { "${it.lowercase()}@miniTicker" }
        val request = Request.Builder()
            .url("$BASE_URL?streams=$streams")
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                trySend(SocketEvent.Connected)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                trySend(SocketEvent.Message(text))
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(NORMAL_CLOSE, null)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                close(IOException("Socket closed by server: $code $reason"))
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                close(t)
            }
        }

        val socket = client.newWebSocket(request, listener)

        awaitClose {
            socket.close(NORMAL_CLOSE, null)
        }
    }

    companion object {
        private const val BASE_URL = "wss://data-stream.binance.vision/stream"
        private const val NORMAL_CLOSE = 1000
    }
}
