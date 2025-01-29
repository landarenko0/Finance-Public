package com.example.finance.ui.screens.operation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.finance.domain.entities.Subcategory

@Composable
fun SubcategoryPickerDialog(
    subcategories: List<Subcategory>,
    initialSelectedSubcategoryId: Int,
    onConfirmButtonClick: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubcategoryId by remember { mutableIntStateOf(initialSelectedSubcategoryId) }

    AlertDialog(
        title = { Text(text = "Выберите подкатегорию") },
        confirmButton = {
            TextButton(
                onClick = { onConfirmButtonClick(selectedSubcategoryId) },
                enabled = selectedSubcategoryId != -1
            ) {
                Text(text = "Выбрать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Отмена")
            }
        },
        text = {
            LazyColumn {
                if (subcategories.isNotEmpty()) {
                    items(
                        items = subcategories,
                        key = { it.id }
                    ) { subcategory ->
                        SubcategoryRadioButton(
                            subcategory = subcategory,
                            selected = subcategory.id == selectedSubcategoryId,
                            onClick = { selectedSubcategoryId = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    item {
                        Text(text = "Нет доступных подкатегорий")
                    }
                }
            }
        },
        onDismissRequest = onDismiss,
        modifier = modifier.sizeIn(maxHeight = 400.dp)
    )
}

@Composable
private fun SubcategoryRadioButton(
    subcategory: Subcategory,
    selected: Boolean,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.clickable { onClick(subcategory.id) }
    ) {
        RadioButton(
            selected = selected,
            onClick = { onClick(subcategory.id) }
        )

        Text(text = subcategory.name)
    }
}