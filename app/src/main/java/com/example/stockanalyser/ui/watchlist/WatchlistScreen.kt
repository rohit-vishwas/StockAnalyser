package com.example.stockanalyser.ui.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stockanalyser.R
import com.example.stockanalyser.domain.model.ConnectionStatus
import com.example.stockanalyser.ui.theme.PriceUp
import com.example.stockanalyser.ui.theme.StatusPending
import org.koin.androidx.compose.koinViewModel

@Composable
fun WatchlistScreen(viewModel: WatchlistViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WatchlistContent(state = uiState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchlistContent(state: WatchlistUiState) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.watchlist_title)) },
                actions = { StatusBadge(status = state.status) }
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding
            ) {
                items(items = state.stocks, key = { it.symbol }) { stock ->
                    Column(modifier = Modifier.animateItem()) {
                        StockRow(stock = stock)
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: ConnectionStatus) {
    val color = if (status == ConnectionStatus.LIVE) PriceUp else StatusPending
    val label = when (status) {
        ConnectionStatus.CONNECTING -> R.string.status_connecting
        ConnectionStatus.LIVE -> R.string.status_live
        ConnectionStatus.RECONNECTING -> R.string.status_reconnecting
    }

    Row(
        modifier = Modifier.padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color = color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
