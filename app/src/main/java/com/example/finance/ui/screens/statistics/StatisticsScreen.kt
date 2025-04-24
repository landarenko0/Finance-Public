package com.example.finance.ui.screens.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.ui.common.AccountPickerDialog
import com.example.finance.ui.common.AccountPickerWithBalanceTopBarTitle
import com.example.finance.ui.common.FinanceTabRow
import com.example.finance.ui.common.MenuTopBar
import io.github.dautovicharis.charts.LineChart
import io.github.dautovicharis.charts.model.toMultiChartDataSet
import io.github.dautovicharis.charts.style.LineChartDefaults

const val EXPENSES_AND_INCOMES_TAB_INDEX = 0
const val EXPENSES_TAB_INDEX = 1
const val INCOMES_TAB_INDEX = 2

const val CURRENT_DATE_TAB_INDEX = 0
const val CURRENT_WEEK_TAB_INDEX = 1
const val CURRENT_MONTH_TAB_INDEX = 2
const val PERIOD_TAB_INDEX = 3

@Composable
fun StatisticsScreen(
    openNavigationDrawer: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val operationTypeTabs = remember { listOf("Расходы и доходы", "Расходы", "Доходы") }
    val periodTabs = remember { listOf("День", "Неделя", "Месяц", "Период") }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            MenuTopBar(
                title = {
                    AccountPickerWithBalanceTopBarTitle(
                        selectedAccount = uiState.selectedAccount,
                        onPickerClick = {
                            viewModel.onUiEvent(StatisticsUiEvent.OnAccountPickerClick)
                        }
                    )
                },
                onMenuIconClick = openNavigationDrawer,
                modifier = Modifier.fillMaxWidth()
            )
        }
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            FinanceTabRow(
                tabs = operationTypeTabs,
                selectedTabIndex = uiState.selectedOperationTypeTabIndex,
                onTabClick = { viewModel.onUiEvent(StatisticsUiEvent.OnOperationTypeTabSelected(it)) }
            )

            if (uiState.showPeriodTab) {
                FinanceTabRow(
                    tabs = periodTabs,
                    selectedTabIndex = uiState.selectedPeriodTabIndex,
                    onTabClick = { viewModel.onUiEvent(StatisticsUiEvent.OnPeriodTabSelected(it)) }
                )
            }

            Column(Modifier.verticalScroll(scrollState)) {
                when (val details = uiState.details) {
                    is StatisticsDetails.ExpensesAndIncomes -> ExpensesAndIncomesChart(
                        expenses = details.expenses,
                        incomes = details.incomes,
                        months = details.months
                    )

                    is StatisticsDetails.ExpensesOrIncomes -> {}

                    StatisticsDetails.Idle -> {}
                }
            }
        }

        if (uiState.showAccountPickerDialog) {
            AccountPickerDialog(
                accounts = uiState.accounts,
                initialSelectedAccountId = uiState.selectedAccount.id,
                onConfirmButtonClick = {
                    viewModel.onUiEvent(StatisticsUiEvent.OnAccountSelected(it))
                },
                onDismiss = { viewModel.onUiEvent(StatisticsUiEvent.OnDialogDismiss) }
            )
        }
    }
}

@Composable
private fun ExpensesAndIncomesChart(
    expenses: List<Double>,
    incomes: List<Double>,
    months: List<String>
) {
    val items = listOf(
        "Расходы" to expenses,
        "Доходы" to incomes
    )

    val dataset = items.toMultiChartDataSet(
        title = "График расходов и доходов за год",
        categories = months
    )

    LineChart(
        dataSet = dataset,
        style = LineChartDefaults.style(
            lineColors = listOf(Color(0xFFFF4F4F), Color(0xFF31FF68))
        )
    )
}