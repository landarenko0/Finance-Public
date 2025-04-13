package com.example.finance.ui.screens.categorylist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.domain.entities.OperationType
import com.example.finance.ui.common.AddFloatingActionButton
import com.example.finance.ui.common.MenuTopBar
import com.example.finance.ui.common.SelectableOperationTypeCard
import com.example.finance.ui.screens.categorylist.components.CategoryList

@Composable
fun CategoryListScreen(
    navigateToCreateCategoryScreen: (OperationType) -> Unit,
    navigateToCategoryWithSubcategoriesScreen: (Int) -> Unit,
    openNavigationDrawer: () -> Unit,
    viewModel: CategoryListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MenuTopBar(
                title = { Text(text = "Категории") },
                onMenuIconClick = openNavigationDrawer,
                modifier = Modifier.fillMaxWidth()
            )
        },
        floatingActionButton = {
            AddFloatingActionButton(
                onClick = { navigateToCreateCategoryScreen(uiState.selectedOperationType) }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            SelectableOperationTypeCard(
                selectedOperationType = uiState.selectedOperationType,
                onOperationTypeClick = viewModel::updateOperationType,
                modifier = Modifier.fillMaxWidth()
            )

            when (uiState.selectedOperationType) {
                OperationType.EXPENSES -> {
                    if (uiState.expensesCategories.isNotEmpty()) {
                        CategoryList(
                            categories = uiState.expensesCategories,
                            onCategoryClick = navigateToCategoryWithSubcategoriesScreen,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(text = "Нет категорий расходов")
                        }
                    }
                }

                else -> {
                    if (uiState.incomeCategories.isNotEmpty()) {
                        CategoryList(
                            categories = uiState.incomeCategories,
                            onCategoryClick = navigateToCategoryWithSubcategoriesScreen,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(text = "Нет категорий доходов")
                        }
                    }
                }
            }
        }
    }
}