package com.example.finance.ui.common

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.finance.ui.theme.FinanceTheme

@Composable
fun FinanceTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = { onTabClick(index) },
                text = { Text(text = tab) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FinanceTabRowPreview() {
    FinanceTheme {
        FinanceTabRow(
            tabs = listOf("День", "Неделя", "Месяц", "Период"),
            selectedTabIndex = 0,
            onTabClick = {}
        )
    }
}