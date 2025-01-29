package com.example.finance.ui.screens.categorywithsubcategories.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finance.domain.entities.Subcategory
import com.example.finance.ui.theme.FinanceTheme

@Composable
fun SubcategoriesList(
    subcategories: List<Subcategory>,
    onSubcategoryClick: (Int) -> Unit,
    onLongSubcategoryClick: (Int) -> Unit,
    deleteSubcategoriesEnabled: Boolean,
    selectedSubcategoriesIds: List<Int>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = modifier
    ) {
        items(
            items = subcategories,
            key = { it.id }
        ) { subcategory ->
            SubcategoryItem(
                subcategory = subcategory,
                onClick = onSubcategoryClick,
                onLongClick = onLongSubcategoryClick,
                showCheckBox = deleteSubcategoriesEnabled,
                isChecked = subcategory.id in selectedSubcategoriesIds,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SubcategoryItem(
    subcategory: Subcategory,
    onClick: (Int) -> Unit,
    onLongClick: (Int) -> Unit,
    showCheckBox: Boolean,
    isChecked: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = modifier.combinedClickable(
            onClick = { onClick(subcategory.id) },
            onLongClick = { onLongClick(subcategory.id) }
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = subcategory.name,
                style = MaterialTheme.typography.displayMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (showCheckBox) {
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { onClick(subcategory.id) },
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun SubcategoryItemPreview() {
    FinanceTheme {
        SubcategoryItem(
            subcategory = Subcategory(id = 0, categoryId = 0, name = "Техническое обслуживание"),
            onClick = {},
            onLongClick = {},
            showCheckBox = true,
            isChecked = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}