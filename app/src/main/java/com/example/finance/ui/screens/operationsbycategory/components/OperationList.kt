package com.example.finance.ui.screens.operationsbycategory.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.finance.domain.entities.Operation
import com.example.finance.domain.entities.OperationType
import com.example.finance.ui.common.LocalDateText
import java.time.LocalDate

@Composable
fun OperationList(
    operations: Map<LocalDate, List<Operation>>,
    onOperationClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = modifier
    ) {
        if (operations.isNotEmpty()) {
            operations.forEach { (date, operations) ->
                item { LocalDateText(date) }

                items(
                    items = operations,
                    key = { it.id }
                ) { operation ->
                    OperationItem(
                        operation = operation,
                        onClick = onOperationClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item { Spacer(Modifier.height(4.dp)) }
            }
        } else {
            item { Text(text = "Нет операций") }
        }
    }
}

@Composable
private fun OperationItem(
    operation: Operation,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClick(operation.id) },
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = operation.subcategory?.name ?: operation.category.name,
                style = MaterialTheme.typography.displayMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            val sign = when (operation.category.type) {
                OperationType.EXPENSES -> "-"
                OperationType.INCOME -> "+"
                else -> ""
            }

            Text(
                text = "$sign ${operation.sum} ₽",
                style = MaterialTheme.typography.displayMedium
            )
        }
    }
}