package com.example.finance.ui.screens.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finance.ui.theme.FinanceTheme
import kotlin.math.abs

@Composable
fun ExpensesToIncomesDonutChart(
    expensesSum: Long,
    incomesSum: Long,
    modifier: Modifier = Modifier
) {
    val totalSum = expensesSum + incomesSum

    val values = arrayOf(expensesSum.toFloat(), incomesSum.toFloat())
    var startAngle = -90f

    val colors = arrayOf(
        Color(0xFFFF4F4F),
        Color(0xFF31FF68)
    )

    val emptyDonutChartColor = MaterialTheme.colorScheme.onSurfaceVariant

    val textMeasurer = rememberTextMeasurer()

    val textStyle = TextStyle(
        color = MaterialTheme.colorScheme.onSurface,
        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
        fontWeight = MaterialTheme.typography.displayMedium.fontWeight,
        fontSize = MaterialTheme.typography.displayMedium.fontSize
    )

    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    }

    Canvas(modifier = modifier.requiredSize(200.dp)) {
        val diameter = size.minDimension
        val strokeWidth = 40f

        val radius = diameter / 2 - strokeWidth / 2

        val rect = Rect(
            offset = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2)
        )

        if (totalSum == 0L) {
            drawArc(
                color = emptyDonutChartColor,
                startAngle = startAngle,
                sweepAngle = 360f * animatedProgress.value,
                useCenter = false,
                style = Stroke(width = strokeWidth),
                topLeft = rect.topLeft,
                size = rect.size
            )

            val textSize = textMeasurer.measure(text = "Нет операций", style = textStyle).size

            drawText(
                textMeasurer = textMeasurer,
                text = "Нет операций",
                topLeft = Offset(center.x - textSize.width / 2, center.y - textSize.height / 2),
                style = textStyle
            )
        } else {
            values.zip(colors).forEach { (value, color) ->
                val sweepAngle = (value / totalSum) * 360f

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle * animatedProgress.value,
                    useCenter = false,
                    style = Stroke(width = strokeWidth),
                    topLeft = rect.topLeft,
                    size = rect.size
                )

                startAngle += sweepAngle

                val difference = abs(incomesSum - expensesSum)

                val text = when {
                    incomesSum > expensesSum -> "+ $difference ₽"
                    incomesSum < expensesSum -> "- $difference ₽"
                    else -> "$difference ₽"
                }

                val textSize = textMeasurer.measure(text = text, style = textStyle).size

                drawText(
                    textMeasurer = textMeasurer,
                    text = text,
                    topLeft = Offset(center.x - textSize.width / 2, center.y - textSize.height / 2),
                    style = textStyle
                )
            }
        }
    }
}

@Preview
@Composable
private fun ExpensesToIncomesDonutChartPreview(modifier: Modifier = Modifier) {
    FinanceTheme {
        ExpensesToIncomesDonutChart(
            expensesSum = 250,
            incomesSum = 750,
            modifier = Modifier.size(200.dp)
        )
    }
}