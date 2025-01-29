package com.example.finance.ui.screens.operation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finance.domain.entities.Category
import com.example.finance.domain.entities.OperationType
import com.example.finance.ui.common.SelectableOperationTypeCard
import com.example.finance.ui.theme.FinanceTheme

@Composable
fun CategoryPickerDialog(
    incomeCategories: List<Category>,
    expensesCategories: List<Category>,
    initialSelectedOperationType: OperationType,
    initialSelectedCategoryId: Int,
    onConfirmButtonClick: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryId by remember { mutableIntStateOf(initialSelectedCategoryId) }
    var selectedOperationType by remember { mutableStateOf(initialSelectedOperationType) }

    AlertDialog(
        title = { Text("Выберите категорию") },
        confirmButton = {
            TextButton(
                onClick = { onConfirmButtonClick(selectedCategoryId) },
                enabled = selectedCategoryId != -1
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
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SelectableOperationTypeCard(
                    selectedOperationType = selectedOperationType,
                    onOperationTypeClick = { selectedOperationType = it }
                )

                LazyColumn {
                    when (selectedOperationType) {
                        OperationType.EXPENSES -> {
                            if (expensesCategories.isNotEmpty()) {
                                items(
                                    items = expensesCategories,
                                    key = { it.id }
                                ) { category ->
                                    CategoryRadioButton(
                                        category = category,
                                        selected = category.id == selectedCategoryId,
                                        onClick = { selectedCategoryId = it },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            } else {
                                item {
                                    Text(text = "Нет доступных категорий")
                                }
                            }
                        }

                        else -> {
                            if (incomeCategories.isNotEmpty()) {
                                items(
                                    items = incomeCategories,
                                    key = { it.id }
                                ) { category ->
                                    CategoryRadioButton(
                                        category = category,
                                        selected = category.id == selectedCategoryId,
                                        onClick = { selectedCategoryId = it },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            } else {
                                item {
                                    Text(text = "Нет доступных категорий")
                                }
                            }
                        }
                    }
                }
            }
        },
        onDismissRequest = onDismiss,
        modifier = modifier.sizeIn(maxHeight = 400.dp)
    )
}

@Composable
private fun CategoryRadioButton(
    category: Category,
    selected: Boolean,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.clickable { onClick(category.id) }
    ) {
        RadioButton(
            selected = selected,
            onClick = { onClick(category.id) }
        )

        Text(text = category.name)
    }
}

@Preview
@Composable
private fun CategoryPickerDialogPreview() {
    FinanceTheme {
        CategoryPickerDialog(
            incomeCategories = emptyList(),
            expensesCategories = emptyList(),
            initialSelectedOperationType = OperationType.EXPENSES,
            initialSelectedCategoryId = -1,
            onConfirmButtonClick = {},
            onDismiss = {}
        )
    }
}