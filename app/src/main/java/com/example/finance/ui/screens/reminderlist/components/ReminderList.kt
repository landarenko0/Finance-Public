package com.example.finance.ui.screens.reminderlist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finance.domain.entities.Periodicity
import com.example.finance.domain.entities.Reminder
import com.example.finance.ui.theme.FinanceTheme
import java.time.LocalDateTime

@Composable
fun ReminderList(
    reminders: List<Reminder>,
    onReminderClick: (Int) -> Unit,
    onReminderSwitchClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = modifier
    ) {
        items(
            items = reminders,
            key = { it.id }
        ) { reminder ->
            ReminderItem(
                reminder = reminder,
                onClick = onReminderClick,
                onSwitchClick = onReminderSwitchClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ReminderItem(
    reminder: Reminder,
    onClick: (Int) -> Unit,
    onSwitchClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClick(reminder.id) },
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = reminder.name,
                style = MaterialTheme.typography.displayMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = reminder.isActive,
                onCheckedChange = { onSwitchClick(reminder.id) },
                modifier = Modifier.size(height = 20.dp, width = 48.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ReminderItemPreview() {
    FinanceTheme {
        ReminderItem(
            reminder = Reminder(0, "Подписка", Periodicity.DAY, LocalDateTime.now(), "", true, null),
            onClick = {},
            onSwitchClick = {}
        )
    }
}