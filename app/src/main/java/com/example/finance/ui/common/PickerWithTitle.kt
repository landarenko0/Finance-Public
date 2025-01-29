package com.example.finance.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finance.ui.theme.FinanceTheme

@Composable
fun PickerWithTitle(
    title: String,
    pickerText: String,
    onPickerClick: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Text(
            text = title,
            color = if (isError) MaterialTheme.colorScheme.error else Color.Unspecified
        )

        Picker(
            text = pickerText,
            onClick = onPickerClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PickerWithTitlePreview() {
    FinanceTheme {
        PickerWithTitle(
            title = "Перевод со счета",
            pickerText = "Не выбрано",
            onPickerClick = {}
        )
    }
}