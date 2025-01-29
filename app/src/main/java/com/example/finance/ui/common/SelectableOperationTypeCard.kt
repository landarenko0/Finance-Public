package com.example.finance.ui.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finance.domain.entities.OperationType
import com.example.finance.ui.theme.FinanceTheme

@Composable
fun SelectableOperationTypeCard(
    selectedOperationType: OperationType,
    onOperationTypeClick: (OperationType) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Card(
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
        ) {
            OperationTypeCard(
                text = "Расходы",
                isSelected = selectedOperationType == OperationType.EXPENSES,
                onClick = { if (enabled) onOperationTypeClick(OperationType.EXPENSES) },
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )

            OperationTypeCard(
                text = "Доходы",
                isSelected = selectedOperationType == OperationType.INCOME,
                onClick = { if (enabled) onOperationTypeClick(OperationType.INCOME) },
                enabled = enabled,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun OperationTypeCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedColor by animateColorAsState(
        targetValue = when {
            isSelected -> {
                if (enabled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline
            }

            else -> Color.Transparent
        },
        animationSpec = tween(
            durationMillis = 200,
            easing = FastOutLinearInEasing
        ),
        label = "OperationTypeCardColorAnimation"
    )

    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = animatedColor),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                color = when {
                    isSelected -> {
                        if (enabled) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.outlineVariant
                    }

                    else -> Color.Unspecified
                },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
private fun OperationTypeSelectableButtonPreview() {
    FinanceTheme {
        SelectableOperationTypeCard(
            selectedOperationType = OperationType.EXPENSES,
            onOperationTypeClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun OperationTypeCardPreview() {
    FinanceTheme {
        OperationTypeCard(
            text = "Расходы",
            isSelected = true,
            enabled = true,
            onClick = {},
            modifier = Modifier.height(42.dp)
        )
    }
}