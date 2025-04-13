package com.example.finance.ui.screens.home.components

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.finance.ui.theme.FinanceTheme

@Composable
fun PeriodTabs(
    selectedTabIndex: Int,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("День", "Неделя", "Месяц", "Период")

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
private fun PeriodTabsPreview() {
    FinanceTheme {
        PeriodTabs(
            selectedTabIndex = 0,
            onTabClick = {}
        )
    }
}