package com.example.finance.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.domain.entities.OperationType
import com.example.finance.ui.common.AccountPickerDialog
import com.example.finance.ui.common.AddFloatingActionButton
import com.example.finance.ui.common.AppNavigationDrawer
import com.example.finance.ui.common.MenuTopBar
import com.example.finance.ui.common.PastOrPresentSelectableDates
import com.example.finance.ui.screens.main.components.DateRangePickerModalDialog
import com.example.finance.ui.screens.main.components.ExpensesToIncomesDonutChart
import com.example.finance.ui.screens.main.components.GroupedCategoriesList
import com.example.finance.ui.screens.main.components.MainScreenTopBarTitle
import com.example.finance.ui.screens.main.components.PeriodTabs
import com.example.finance.ui.common.PeriodText
import kotlinx.coroutines.launch

const val CURRENT_DATE_TAB_INDEX = 0
const val CURRENT_WEEK_TAB_INDEX = 1
const val CURRENT_MONTH_TAB_INDEX = 2
const val PERIOD_TAB_INDEX = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    selectedNavigationItemIndex: Int,
    onNavigationItemClick: (Int) -> Unit,
    onFloatingButtonClick: (Int) -> Unit,
    onGroupedCategoryClick: (OperationType, Int, Int?, String) -> Unit,
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val datePickerState = rememberDateRangePickerState(selectableDates = PastOrPresentSelectableDates)

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.onUiEvent(MainUiEvent.OnComposition) }

    AppNavigationDrawer(
        selectedIndex = selectedNavigationItemIndex,
        drawerState = drawerState,
        onNavigationItemClick = onNavigationItemClick
    ) {
        Scaffold(
            topBar = {
                MenuTopBar(
                    title = {
                        MainScreenTopBarTitle(
                            selectedAccount = uiState.selectedAccount,
                            onPickerClick = { viewModel.onUiEvent(MainUiEvent.OnAccountPickerClick) }
                        )
                    },
                    onMenuIconClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            floatingActionButton = {
                AddFloatingActionButton(
                    onClick = { onFloatingButtonClick(uiState.selectedAccount.id) }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding() + 12.dp)
                    .padding(horizontal = 16.dp)
            ) {
                PeriodTabs(
                    selectedTabIndex = uiState.selectedTabIndex,
                    onTabClick = { viewModel.onUiEvent(MainUiEvent.OnTabSelected(it)) }
                )

                PeriodText(period = uiState.selectedPeriod)

                if (uiState.showDonutChart) {
                    ExpensesToIncomesDonutChart(
                        expensesSum = uiState.expensesSum,
                        incomesSum = uiState.incomeSum
                    )
                }

                GroupedCategoriesList(
                    groupedCategories = uiState.selectedAccountOperations,
                    onItemClick = { operationType, categoryId ->
                        onGroupedCategoryClick(
                            operationType,
                            uiState.selectedAccount.id,
                            categoryId,
                            uiState.periodString
                        )
                    }
                )
            }
        }
    }

    if (uiState.showAccountPickerDialog) {
        AccountPickerDialog(
            accounts = uiState.accounts,
            initialSelectedAccountId = uiState.selectedAccount.id,
            onConfirmButtonClick = { viewModel.onUiEvent(MainUiEvent.OnNewAccountSelected(it)) },
            onDismiss = { viewModel.onUiEvent(MainUiEvent.OnDialogDismiss) }
        )
    }

    if (uiState.showDateRangePickerDialog) {
        DateRangePickerModalDialog(
            onConfirm = {
                datePickerState.setSelection(it.first, it.second)
                viewModel.onUiEvent(MainUiEvent.OnNewDateRangeSelected(it))
            },
            onDismiss = { viewModel.onUiEvent(MainUiEvent.OnDialogDismiss) },
            initialSelectedStartDate = datePickerState.selectedStartDateMillis,
            initialSelectedEndDate = datePickerState.selectedEndDateMillis
        )
    }

    if (uiState.clearSelectedDateRange) {
        datePickerState.setSelection(null, null)
        viewModel.onUiEvent(MainUiEvent.OnDateRangeCleared)
    }
}