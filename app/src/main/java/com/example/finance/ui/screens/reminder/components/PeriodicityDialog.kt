package com.example.finance.ui.screens.reminder.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finance.domain.entities.Periodicity

@Composable
fun PeriodicityDialog(
    onConfirmButtonClick: (Periodicity) -> Unit,
    onDismiss: () -> Unit,
    initialSelectedPeriodicity: Periodicity,
    modifier: Modifier = Modifier
) {
    var selectedPeriodicity by remember { mutableStateOf(initialSelectedPeriodicity) }

    val periodicityItems = listOf(
        Periodicity.ONCE to "Один раз",
        Periodicity.DAY to "Каждый день",
        Periodicity.WEEK to "Каждую неделю",
        Periodicity.MONTH to "Каждый месяц",
        Periodicity.YEAR to "Каждый год",
    )

    AlertDialog(
        title = { Text(text = "Выберите периодичность") },
        confirmButton = {
            TextButton(onClick = { onConfirmButtonClick(selectedPeriodicity) }) {
                Text(text = "Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Отмена")
            }
        },
        text = {
            LazyColumn {
                items(
                    items = periodicityItems,
                    key = { it.first }
                ) { periodicity ->
                    PeriodicityRadioButton(
                        periodicityPair = periodicity,
                        onClick = { selectedPeriodicity = it },
                        selected = selectedPeriodicity == periodicity.first,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        onDismissRequest = onDismiss,
        modifier = modifier
    )
}

@Composable
private fun PeriodicityRadioButton(
    periodicityPair: Pair<Periodicity, String>,
    onClick: (Periodicity) -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.clickable { onClick(periodicityPair.first) }
    ) {
        RadioButton(
            selected = selected,
            onClick = { onClick(periodicityPair.first) }
        )

        Text(text = periodicityPair.second)
    }
}