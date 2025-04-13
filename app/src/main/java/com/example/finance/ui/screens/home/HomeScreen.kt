package com.example.finance.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.domain.entities.OperationType
import com.example.finance.ui.common.AccountPickerDialog
import com.example.finance.ui.common.AddFloatingActionButton
import com.example.finance.ui.common.MenuTopBar
import com.example.finance.ui.common.PastOrPresentSelectableDates
import com.example.finance.ui.common.PeriodText
import com.example.finance.ui.screens.home.components.DateRangePickerModalDialog
import com.example.finance.ui.screens.home.components.ExpensesToIncomesDonutChart
import com.example.finance.ui.screens.home.components.GroupedCategoriesList
import com.example.finance.ui.screens.home.components.MainScreenTopBarTitle
import com.example.finance.ui.screens.home.components.PeriodTabs

const val CURRENT_DATE_TAB_INDEX = 0
const val CURRENT_WEEK_TAB_INDEX = 1
const val CURRENT_MONTH_TAB_INDEX = 2
const val PERIOD_TAB_INDEX = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToCreateOperationScreen: (Int) -> Unit,
    navigateToOperationsByCategoryScreen: (OperationType, Int, Int?, String) -> Unit,
    openNavigationDrawer: () -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val datePickerState = rememberDateRangePickerState(selectableDates = PastOrPresentSelectableDates)

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                HomeEvent.ClearDateRange -> datePickerState.setSelection(null, null)

                is HomeEvent.NavigateToCreateOperationScreen -> {
                    navigateToCreateOperationScreen(event.accountId)
                }

                is HomeEvent.NavigateToOperationsByCategoryScreen -> {
                    navigateToOperationsByCategoryScreen(
                        event.operationType,
                        event.accountId,
                        event.categoryId,
                        event.period
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            MenuTopBar(
                title = {
                    MainScreenTopBarTitle(
                        selectedAccount = uiState.selectedAccount,
                        onPickerClick = { viewModel.onUiEvent(HomeUiEvent.OnAccountPickerClick) }
                    )
                },
                onMenuIconClick = openNavigationDrawer,
                modifier = Modifier.fillMaxWidth()
            )
        },
        floatingActionButton = {
            AddFloatingActionButton(
                onClick = { viewModel.onUiEvent(HomeUiEvent.OnFloatingButtonClick) }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(paddingValues)
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            PeriodTabs(
                selectedTabIndex = uiState.selectedTabIndex,
                onTabClick = { viewModel.onUiEvent(HomeUiEvent.OnTabSelected(it)) }
            )

            PeriodText(period = uiState.selectedPeriod)

            if (uiState.showDonutChart) {
                ExpensesToIncomesDonutChart(
                    expensesSum = uiState.expensesSum,
                    incomesSum = uiState.incomeSum
                )
            }

            GroupedCategoriesList(
                groupedCategories = uiState.accountOperations,
                onItemClick = { operationType, categoryId ->
                    viewModel.onUiEvent(HomeUiEvent.OnGroupedCategoryClick(operationType, categoryId))
                }
            )
        }
    }

    if (uiState.showAccountPickerDialog) {
        AccountPickerDialog(
            accounts = uiState.accounts,
            initialSelectedAccountId = uiState.selectedAccount.id,
            onConfirmButtonClick = { viewModel.onUiEvent(HomeUiEvent.OnNewAccountSelected(it)) },
            onDismiss = { viewModel.onUiEvent(HomeUiEvent.OnDialogDismiss) }
        )
    }

    if (uiState.showDateRangePickerDialog) {
        DateRangePickerModalDialog(
            onConfirm = {
                datePickerState.setSelection(it.first, it.second)
                viewModel.onUiEvent(HomeUiEvent.OnNewDateRangeSelected(it))
            },
            onDismiss = { viewModel.onUiEvent(HomeUiEvent.OnDialogDismiss) },
            initialSelectedStartDate = datePickerState.selectedStartDateMillis,
            initialSelectedEndDate = datePickerState.selectedEndDateMillis
        )
    }
}