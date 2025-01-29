package com.example.finance.ui.screens.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.finance.domain.entities.GroupedCategories
import com.example.finance.domain.entities.OperationType

@Composable
fun GroupedCategoriesList(
    groupedCategories: List<GroupedCategories>,
    onItemClick: (OperationType, Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = modifier
    ) {
        items(groupedCategories) { groupedCategory ->
            GroupedCategoryItem(
                groupedCategory = groupedCategory,
                onClick = onItemClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GroupedCategoryItem(
    groupedCategory: GroupedCategories,
    onClick: (OperationType, Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.small,
        onClick = { onClick(groupedCategory.operationType, groupedCategory.categoryId) },
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
                text = groupedCategory.categoryName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            val signOfSum = when (groupedCategory.operationType) {
                OperationType.INCOME, OperationType.INCOME_TRANSFER -> "+"
                OperationType.EXPENSES, OperationType.OUTCOME_TRANSFER -> "-"
                else -> ""
            }

            Text(text = "$signOfSum ${groupedCategory.totalSum} ₽")
        }
    }
}