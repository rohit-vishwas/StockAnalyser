# StockAnalyser

A small Android app that shows a live watchlist of 8 instruments. Prices are streamed over a WebSocket and every row flashes green or red when its price moves up or down.

## Running the app

1. Open the project in Android Studio (Narwhal or newer).
2. Let Gradle sync.
3. Run the `app` configuration on an emulator or device (minSdk 24).

Or from the command line:

```
./gradlew assembleDebug
```

No API key or extra setup is needed.

## Data source

The app uses the public Binance market data stream:

```
wss://data-stream.binance.vision/stream?streams=btcusdt@miniTicker/ethusdt@miniTicker/...
```

I went with Binance instead of a stock feed like Finnhub because:

- it does not need an API key, so anyone can clone and run it
- crypto trades 24/7, so the screen is always moving (US stock feeds are idle outside market hours)
- `data-stream.binance.vision` is the market-data-only endpoint and is available in more regions than `stream.binance.com`

The `miniTicker` stream gives the last price and the 24h open price, which is enough for the price and the 24h change %.

Watchlist: BTC, ETH, BNB, SOL, XRP, ADA, DOGE, LTC (all against USDT).

## Architecture

MVVM with Clean Architecture style layers, all in a single `app` module split by package.

```
data/
  remote/      BinanceSocketClient, DTOs, SocketEvent
  repository/  StockRepositoryImpl
  WatchlistConfig
domain/
  model/       Stock, PriceDirection, ConnectionStatus
  repository/  StockRepository
  usecase/     ObserveWatchlistUseCase
ui/
  watchlist/   WatchlistViewModel, WatchlistUiState, StockUiModel, WatchlistScreen, StockRow
  theme/
di/            Koin module
```

Flow of data:

1. `BinanceSocketClient` wraps the OkHttp WebSocket in a `callbackFlow`. The socket is closed in `awaitClose`, so it lives only as long as someone is collecting.
2. `StockRepositoryImpl` parses each message with Gson, keeps the latest `Stock` per symbol and works out the `PriceDirection` by comparing with the previous price. On failure it waits 3 seconds and reconnects (`retryWhen`). Connection state is exposed as a `StateFlow<ConnectionStatus>`.
3. `ObserveWatchlistUseCase` exposes the stock list and the connection status to the presentation layer.
4. `WatchlistViewModel` combines both, maps to `StockUiModel` (already formatted strings) on `Dispatchers.Default`, applies `conflate()` and exposes a `StateFlow` with `stateIn(WhileSubscribed(5000))`.
5. `WatchlistScreen` collects with `collectAsStateWithLifecycle`, so when the app goes to background the flow stops and the socket is closed. It reconnects when the app comes back.

Libraries: Jetpack Compose (Material 3), Kotlin Coroutines + Flow, Koin for DI, OkHttp for the WebSocket, Gson for parsing and Coil for coin icons.

## Keeping recomposition low

- `StockUiModel` is an `@Immutable` data class with only primitive / String / enum fields. When one symbol ticks, the other rows get equal models and Compose skips them.
- `LazyColumn` items use `key = { it.symbol }`, so rows keep their identity and state across updates, and `animateItem()` smooths out the moment a new row is inserted.
- The green/red flash is an `Animatable<Color>` that is only read inside `Modifier.drawBehind`. The animation runs in the draw phase, so the 600 ms fade does not trigger a recomposition on every frame.
- The direction arrow uses `animateFloatAsState` for rotation and `animateColorAsState` for colour, both read inside `graphicsLayer` / `Canvas` draw blocks for the same reason.
- Price and percent strings are formatted in the ViewModel mapping, not inside composables.
- `conflate()` drops intermediate states if the UI is slower than the stream, so updates never pile up.
- Prices use tabular figures (`tnum`) so the text width does not jump around on every tick.

## Trade-offs

- **Crypto instead of stocks.** Free stock WebSocket feeds need an API key and only move during market hours. The UI and architecture would be the same for a stock feed; only `BinanceSocketClient` and the DTO would change.
- **Single module.** Layers are separated by package rather than Gradle modules. For a single screen this keeps the build simple; splitting into modules would be easy later.
- **No local cache.** The last prices are kept in memory only. After process death the list loads again from the stream (usually within a second).
- **Gson instead of kotlinx.serialization.** Fewer moving parts (no compiler plugin) for three fields.
- **Rows appear as data arrives.** `miniTicker` only pushes a symbol when it changes, so a quiet pair can show up a second or two after the others. The row is inserted in its fixed position with `animateItem()` instead of waiting for all symbols or adding a REST call for an initial snapshot.
- **Fixed watchlist.** The symbols are hard-coded in `WatchlistConfig`, as adding/removing items was out of scope.
