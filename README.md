**StockAnalyser**

Real-time crypto watchlist built with Jetpack Compose. Streams live prices over WebSockets and highlights rows green/red as prices move up or down.

Setup & Run
Open in Android Studio (Narwhal+)

Let Gradle sync

Run on device/emulator (minSdk 24)

CLI:

Bash
./gradlew assembleDebug
No API keys or config needed.

Why Binance data instead of stocks?
I used Binance's public stream (wss://data-stream.binance.vision/...) instead of Finnhub or Alpha Vantage because:

No API keys needed to run the project.

Crypto runs 24/7, so the UI actually moves while reviewing (stock markets sleep on weekends/evenings).

data-stream.binance.vision avoids regional blocks on regular Binance endpoints.

Pairs tracked: BTC, ETH, BNB, SOL, XRP, ADA, DOGE, LTC (USDT pairs).

Architecture
Single-module MVVM + Clean Architecture:

data: BinanceSocketClient (OkHttp WebSocket wrapped in callbackFlow), parsing with Gson, and retry logic on drop.

domain: Models (Stock, PriceDirection) and repository interface.

ui: Compose screen, WatchlistViewModel with WhileSubscribed(5000) so the socket disconnects when the app is backgrounded.

di: Koin.

Keeping Compose fast
Fast ticker updates can easily cause jank, so a few optimizations are in place:

Modifier.drawBehind: Flash color animations run directly in the draw phase, skipping unnecessary row recompositions.

@Immutable UI models: Compose skips re-rendering rows whose prices haven't changed.

conflate(): Drops backpressured updates if the UI is still drawing a previous tick.

Tabular figures (tnum): Keeps numbers monospaced so prices don't jitter horizontally on every tick.

Trade-offs & Notes
Crypto over stocks: Done purely for zero-config review and 24/7 live data.

In-memory only: No Room/DB cache since ticker data becomes useless after a few seconds anyway.

Single module: Overkill to split modules for a small demo screen.

Row loading: miniTicker only emits on trades, so less liquid pairs show up a couple of seconds later once they tick.
