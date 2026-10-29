package com.example.stockanalyser.ui.watchlist

import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.stockanalyser.domain.model.PriceDirection
import com.example.stockanalyser.ui.theme.PriceDown
import com.example.stockanalyser.ui.theme.PriceNeutral
import com.example.stockanalyser.ui.theme.PriceUp

private const val FLASH_DURATION_MS = 600
private const val FLASH_ALPHA = 0.25f

@Composable
fun StockRow(
    stock: StockUiModel,
    modifier: Modifier = Modifier
) {
    val flashColor = remember { Animatable(Color.Transparent) }

    LaunchedEffect(stock.price) {
        val tint = when (stock.direction) {
            PriceDirection.UP -> PriceUp
            PriceDirection.DOWN -> PriceDown
            PriceDirection.NONE -> return@LaunchedEffect
        }
        flashColor.snapTo(tint.copy(alpha = FLASH_ALPHA))
        flashColor.animateTo(tint.copy(alpha = 0f), tween(FLASH_DURATION_MS))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind { drawRect(flashColor.value) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoinIcon(url = stock.iconUrl, name = stock.name)

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stock.baseAsset,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stock.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = stock.priceText,
                style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
                fontWeight = FontWeight.SemiBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                DirectionArrow(direction = stock.direction)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stock.changeText,
                    style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum"),
                    color = if (stock.isChangePositive) PriceUp else PriceDown
                )
            }
        }
    }
}

@Composable
private fun CoinIcon(url: String, name: String) {
    val placeholderColor = MaterialTheme.colorScheme.surfaceVariant
    val placeholder = remember(placeholderColor) { ColorPainter(placeholderColor) }

    AsyncImage(
        model = url,
        contentDescription = name,
        placeholder = placeholder,
        error = placeholder,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
    )
}

@Composable
private fun DirectionArrow(direction: PriceDirection) {
    val rotation by animateFloatAsState(
        targetValue = if (direction == PriceDirection.DOWN) 180f else 0f,
        label = "arrowRotation"
    )
    val color by animateColorAsState(
        targetValue = when (direction) {
            PriceDirection.UP -> PriceUp
            PriceDirection.DOWN -> PriceDown
            PriceDirection.NONE -> PriceNeutral
        },
        label = "arrowColor"
    )

    Canvas(
        modifier = Modifier
            .size(10.dp)
            .graphicsLayer { rotationZ = rotation }
    ) {
        val arrow = Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path = arrow, color = color)
    }
}
