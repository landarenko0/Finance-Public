package com.example.finance.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.finance.domain.entities.OperationType
import com.example.finance.ui.screens.account.AccountScreen
import com.example.finance.ui.screens.accountlist.AccountListScreen
import com.example.finance.ui.screens.category.CategoryScreen
import com.example.finance.ui.screens.categorylist.CategoryListScreen
import com.example.finance.ui.screens.categorywithsubcategories.CategoryWithSubcategoriesScreen
import com.example.finance.ui.screens.main.MainScreen
import com.example.finance.ui.screens.operation.OperationScreen
import com.example.finance.ui.screens.operationsbycategory.OperationsByCategoryScreen
import com.example.finance.ui.screens.transfer.TransferScreen

private const val MAIN_SCREEN_INDEX = 0
private const val ACCOUNT_LIST_SCREEN_INDEX = 1
private const val CATEGORY_LIST_SCREEN_INDEX = 2
private const val STATISTICS_SCREEN_INDEX = 3
private const val REMINDER_LIST_SCREEN_INDEX = 4

@Composable
fun FinanceNavGraph() {
    val navController = rememberNavController()

    var selectedIndex by remember { mutableIntStateOf(MAIN_SCREEN_INDEX) }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            backStackEntry.destination.route?.let {
                selectedIndex = when {
                    it.endsWith("MainScreen") -> MAIN_SCREEN_INDEX
                    it.endsWith("AccountListScreen") -> ACCOUNT_LIST_SCREEN_INDEX
                    it.endsWith("CategoryListScreen") -> CATEGORY_LIST_SCREEN_INDEX
                    it.endsWith("StatisticsScreen") -> STATISTICS_SCREEN_INDEX
                    it.endsWith("ReminderListScreen") -> REMINDER_LIST_SCREEN_INDEX
                    else -> selectedIndex
                }
            }
        }
    }

    val onNavigationItemClick = { index: Int ->
        if (selectedIndex != index) {
            navController.navigate(
                when (index) {
                    MAIN_SCREEN_INDEX -> AppScreens.MainScreen
                    ACCOUNT_LIST_SCREEN_INDEX -> AppScreens.AccountListScreen
                    CATEGORY_LIST_SCREEN_INDEX -> AppScreens.CategoryListScreen
                    STATISTICS_SCREEN_INDEX -> AppScreens.StatisticsScreen
                    REMINDER_LIST_SCREEN_INDEX -> AppScreens.ReminderListScreen
                    else -> throw IllegalArgumentException("Unknown navigation item index")
                }
            ) {
                popUpTo(AppScreens.MainScreen)
                launchSingleTop = true
            }

            selectedIndex = index
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppScreens.MainScreen
    ) {
        composable<AppScreens.MainScreen> {
            MainScreen(
                selectedNavigationItemIndex = selectedIndex,
                onNavigationItemClick = onNavigationItemClick,
                onFloatingButtonClick = { accountId ->
                    navController.navigate(AppScreens.OperationScreen(accountId = accountId))
                },
                onGroupedCategoryClick = { operationType, accountId, categoryId, period ->
                    navController.navigate(
                        AppScreens.OperationsByCategoryScreen(
                            operationType = operationType,
                            accountId = accountId,
                            categoryId = categoryId,
                            period = period
                        )
                    )
                }
            )
        }

        composable<AppScreens.AccountListScreen> {
            AccountListScreen(
                selectedNavigationItemIndex = selectedIndex,
                onNavigationItemClick = onNavigationItemClick,
                onFloatingButtonClick = {
                    navController.navigate(AppScreens.AccountScreen())
                },
                onTransferIconClick = {
                    navController.navigate(AppScreens.TransferScreen())
                },
                onAccountItemClick = { accountId ->
                    navController.navigate(AppScreens.AccountScreen(accountId))
                }
            )
        }

        composable<AppScreens.CategoryListScreen> {
            CategoryListScreen(
                selectedNavigationItemIndex = selectedIndex,
                onNavigationItemClick = onNavigationItemClick,
                onFloatingButtonClick = {
                    navController.navigate(
                        AppScreens.CategoryScreen(initialSelectedOperationType = it)
                    )
                },
                onCategoryClick = {
                    navController.navigate(AppScreens.CategoryWithSubcategoriesScreen(it))
                }
            )
        }

        composable<AppScreens.StatisticsScreen> { }

        composable<AppScreens.ReminderListScreen> { }

        composable<AppScreens.AccountScreen> {
            AccountScreen(
                onBackIconClick = { navController.popBackStack() }
            )
        }

        composable<AppScreens.TransferScreen> {
            TransferScreen(
                onBackIconClick = { navController.popBackStack() }
            )
        }

        composable<AppScreens.CategoryScreen> {
            CategoryScreen(
                onBackIconClick = { navController.popBackStack() },
                onCategoryDelete = {
                    navController.popBackStack(
                        route = AppScreens.CategoryListScreen,
                        inclusive = false
                    )
                },
            )
        }

        composable<AppScreens.CategoryWithSubcategoriesScreen> {
            CategoryWithSubcategoriesScreen(
                onBackIconClick = { navController.popBackStack() },
                onEditButtonClick = {
                    navController.navigate(AppScreens.CategoryScreen(categoryId = it))
                }
            )
        }

        composable<AppScreens.OperationScreen> {
            OperationScreen(
                onBackIconClick = { navController.popBackStack() }
            )
        }

        composable<AppScreens.OperationsByCategoryScreen> {
            OperationsByCategoryScreen(
                onBackIconClick = { navController.popBackStack() },
                onFloatingButtonClick = { operationType, accountId, categoryId ->
                    when (operationType) {
                        OperationType.EXPENSES, OperationType.INCOME -> {
                            navController.navigate(
                                AppScreens.OperationScreen(
                                    accountId = accountId,
                                    categoryId = categoryId
                                )
                            )
                        }

                        OperationType.OUTCOME_TRANSFER,
                        OperationType.INCOME_TRANSFER,
                        OperationType.TRANSFER -> {
                            navController.navigate(AppScreens.TransferScreen())
                        }
                    }
                },
                onOperationClick = {
                    navController.navigate(AppScreens.OperationScreen(operationId = it))
                },
                onTransferClick = {
                    navController.navigate(AppScreens.TransferScreen(it))
                }
            )
        }
    }
}