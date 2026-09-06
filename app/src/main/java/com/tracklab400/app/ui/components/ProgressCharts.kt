package com.tracklab400.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tracklab400.app.data.model.TimeUtils
import com.tracklab400.app.data.stats.DistanceSeriesPoint

data class ProgressBarItem(
    val label: String,
    val value: Float,
    val caption: String,
)

/**
 * Zaman değerlerini (ms) çizen çizgi grafiği. Tek eksen: y = süre (ne kadar küçük o kadar iyi),
 * x = nokta sırası. Hedef (kesikli) ve mevcut baz (noktalı) referans çizgilerini destekler.
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun TrackLabLineChart(
    points: List<DistanceSeriesPoint>,
    baselineMs: Long?,
    targetMs: Long?,
    contentDescription: String,
    modifier: Modifier = Modifier,
    labelColor: Color,
    gridColor: Color,
    seriesColor: Color,
    targetColor: Color,
    baselineColor: Color,
) {
    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .semantics { this.contentDescription = contentDescription },
    ) {
        val left = 52.dp.toPx()
        val right = 12.dp.toPx()
        val top = 10.dp.toPx()
        val bottom = 14.dp.toPx()
        val chartW = size.width - left - right
        val chartH = size.height - top - bottom
        if (chartW <= 0f || chartH <= 0f) return@Canvas

        val all = points.map { it.bestMs } + listOfNotNull(baselineMs, targetMs)
        if (all.isEmpty()) return@Canvas

        var min = all.min()
        var max = all.max()
        if (min == max) {
            min -= 1_000L
            max += 1_000L
        }
        val span = (max - min).toFloat()
        val padMin = min - span * 0.12f
        val padMax = max + span * 0.12f

        fun yFor(ms: Long): Float {
            val ratio = ((ms - padMin) / (padMax - padMin)).toFloat()
            return top + chartH * (1f - ratio)
        }

        // Yatay kılavuz + eksen etiketleri (saniye)
        val gridCount = 4
        for (i in 0..gridCount) {
            val fraction = i / gridCount.toFloat()
            val ms = (padMin + (padMax - padMin) * fraction).toLong()
            val y = yFor(ms)
            if (i in 1 until gridCount) {
                drawLine(
                    color = gridColor,
                    start = Offset(left, y),
                    end = Offset(size.width - right, y),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            val label = TimeUtils.formatSeconds(ms)
            val measured = textMeasurer.measure(
                text = label,
                style = TextStyle(color = labelColor, fontSize = 10.sp),
            )
            drawText(
                textMeasurer = textMeasurer,
                text = label,
                topLeft = Offset(
                    x = left - measured.size.width - 6.dp.toPx(),
                    y = y - measured.size.height / 2f,
                ),
                style = TextStyle(color = labelColor, fontSize = 10.sp),
            )
        }

        // Referans çizgileri
        targetMs?.let {
            drawReferenceLine(fixedY = yFor(it), color = targetColor, dashed = true)
        }
        baselineMs?.let {
            drawReferenceLine(fixedY = yFor(it), color = baselineColor, dashed = false)
        }

        if (points.isEmpty()) return@Canvas

        // Veri çizgisi
        val n = points.size
        val xFor: (Int) -> Float = { index ->
            if (n == 1) left + chartW / 2f else left + chartW * index / (n - 1).toFloat()
        }
        val path = Path()
        points.forEachIndexed { index, point ->
            val x = xFor(index)
            val y = yFor(point.bestMs)
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        drawPath(
            path = path,
            color = seriesColor,
            style = Stroke(width = 3.dp.toPx()),
        )

        // Noktalar
        points.forEachIndexed { index, point ->
            val x = xFor(index)
            val y = yFor(point.bestMs)
            drawCircle(color = seriesColor, radius = 5.dp.toPx(), center = Offset(x, y))
            drawCircle(
                color = labelColor,
                radius = 2.dp.toPx(),
                center = Offset(x, y),
            )
        }
    }
}

/**
 * Kategorik çubuk grafiği. Çubuk yüksekliği `value`, üstünde `caption` etiketi, altında `label`.
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun TrackLabBarChart(
    items: List<ProgressBarItem>,
    contentDescription: String,
    modifier: Modifier = Modifier,
    labelColor: Color,
    barColor: Color,
    trackColor: Color,
    maxValue: Float? = null,
) {
    val textMeasurer = rememberTextMeasurer()
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .semantics { this.contentDescription = contentDescription },
    ) {
        if (items.isEmpty()) return@Canvas

        val left = 24.dp.toPx()
        val right = 24.dp.toPx()
        val top = 26.dp.toPx()
        val bottom = 26.dp.toPx()
        val chartW = size.width - left - right
        val chartH = size.height - top - bottom
        if (chartW <= 0f || chartH <= 0f) return@Canvas

        val peak = maxValue
            ?: items.maxOf { it.value }
        val scaledMax = if (peak <= 0f) 1f else peak * 1.15f

        val slotW = chartW / items.size
        val barW = minOf(44.dp.toPx(), slotW * 0.6f)
        val baselineY = top + chartH

        items.forEachIndexed { index, item ->
            val centerX = left + slotW * index + slotW / 2f
            val barH = chartH * (item.value / scaledMax).coerceIn(0f, 1f)
            val rect = Rect(
                left = centerX - barW / 2f,
                top = baselineY - barH,
                right = centerX + barW / 2f,
                bottom = baselineY,
            )
            drawRoundRect(
                color = barColor,
                topLeft = rect.topLeft,
                size = Size(rect.width, rect.height),
                cornerRadius = CornerRadius(6.dp.toPx()),
            )

            // Caption (değer) çubuğun üstünde
            val caption = item.caption
            val captionMeasured = textMeasurer.measure(
                text = caption,
                style = TextStyle(color = labelColor, fontSize = 11.sp),
            )
            drawText(
                textMeasurer = textMeasurer,
                text = caption,
                topLeft = Offset(
                    x = centerX - captionMeasured.size.width / 2f,
                    y = baselineY - barH - 22.dp.toPx(),
                ),
                style = TextStyle(color = labelColor, fontSize = 11.sp),
            )

            // Label çubuğun altında
            val label = item.label
            val labelMeasured = textMeasurer.measure(
                text = label,
                style = TextStyle(color = labelColor, fontSize = 11.sp),
            )
            drawText(
                textMeasurer = textMeasurer,
                text = label,
                topLeft = Offset(
                    x = centerX - labelMeasured.size.width / 2f,
                    y = baselineY + 8.dp.toPx(),
                ),
                style = TextStyle(color = labelColor, fontSize = 11.sp),
            )
        }

        // Zemin çizgisi
        drawLine(
            color = trackColor,
            start = Offset(left, baselineY),
            end = Offset(size.width - right, baselineY),
            strokeWidth = 1.dp.toPx(),
        )
    }
}

private fun DrawScope.drawReferenceLine(
    fixedY: Float,
    color: Color,
    dashed: Boolean,
) {
    val pathEffect = if (dashed) {
        PathEffect.dashPathEffect(
            intervals = floatArrayOf(10.dp.toPx(), 8.dp.toPx()),
        )
    } else {
        PathEffect.dashPathEffect(
            intervals = floatArrayOf(2.dp.toPx(), 7.dp.toPx()),
        )
    }
    drawLine(
        color = color,
        start = Offset(size.width * 0.02f, fixedY),
        end = Offset(size.width * 0.98f, fixedY),
        strokeWidth = 1.5.dp.toPx(),
        pathEffect = pathEffect,
    )
}