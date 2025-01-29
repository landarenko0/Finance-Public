package com.example.finance.ui.screens.categorylist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finance.domain.entities.Category
import com.example.finance.domain.entities.OperationType
import com.example.finance.ui.theme.FinanceTheme

@Composable
fun CategoryList(
    categories: List<Category>,
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = modifier
    ) {
        items(
            items = categories,
            key = { it.id }
        ) { category ->
            CategoryItem(
                category = category,
                onClick = onCategoryClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClick(category.id) },
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.displayMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )
    }
}

@Preview
@Composable
private fun CategoryItemPreview() {
    FinanceTheme {
        CategoryItem(
            category = Category(id = 0, name = "Машина", type = OperationType.INCOME),
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}