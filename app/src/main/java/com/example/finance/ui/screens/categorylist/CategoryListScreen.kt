package com.example.finance.ui.screens.categorylist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.domain.entities.OperationType
import com.example.finance.ui.common.AddFloatingActionButton
import com.example.finance.ui.common.AppNavigationDrawer
import com.example.finance.ui.common.MenuTopBar
import com.example.finance.ui.common.SelectableOperationTypeCard
import com.example.finance.ui.screens.categorylist.components.CategoryList
import kotlinx.coroutines.launch

@Composable
fun CategoryListScreen(
    selectedNavigationItemIndex: Int,
    onNavigationItemClick: (Int) -> Unit,
    onFloatingButtonClick: (OperationType) -> Unit,
    onCategoryClick: (Int) -> Unit,
    viewModel: CategoryListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    AppNavigationDrawer(
        selectedIndex = selectedNavigationItemIndex,
        drawerState = drawerState,
        onNavigationItemClick = onNavigationItemClick
    ) {
        Scaffold(
            topBar = {
                MenuTopBar(
                    title = { Text(text = "Категории") },
                    onMenuIconClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            floatingActionButton = {
                AddFloatingActionButton(
                    onClick = { onFloatingButtonClick(uiState.selectedOperationType) }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding() + 12.dp)
                    .padding(horizontal = 16.dp)
            ) {
                SelectableOperationTypeCard(
                    selectedOperationType = uiState.selectedOperationType,
                    onOperationTypeClick = viewModel::updateOperationType,
                    modifier = Modifier.fillMaxWidth()
                )

                CategoryList(
                    categories = when (uiState.selectedOperationType) {
                        OperationType.EXPENSES -> uiState.expensesCategories
                        else -> uiState.incomeCategories
                    },
                    onCategoryClick = onCategoryClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}