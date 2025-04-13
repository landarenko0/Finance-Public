package com.example.finance.ui.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.finance.domain.entities.OperationType
import com.example.finance.ui.common.AppNavigationDrawer
import com.example.finance.ui.screens.account.AccountScreen
import com.example.finance.ui.screens.accountlist.AccountListScreen
import com.example.finance.ui.screens.category.CategoryScreen
import com.example.finance.ui.screens.categorylist.CategoryListScreen
import com.example.finance.ui.screens.categorywithsubcategories.CategoryWithSubcategoriesScreen
import com.example.finance.ui.screens.home.HomeScreen
import com.example.finance.ui.screens.operation.OperationScreen
import com.example.finance.ui.screens.operationsbycategory.OperationsByCategoryScreen
import com.example.finance.ui.screens.reminder.ReminderScreen
import com.example.finance.ui.screens.reminderlist.ReminderListScreen
import com.example.finance.ui.screens.transfer.TransferScreen
import kotlinx.coroutines.launch

@Composable
fun FinanceApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentScreen = AppScreens.getScreenByRoute(backStackEntry?.destination?.route)

    val onNavigationItemClick = { screen: AppScreens ->
        if (currentScreen != screen) {
            navController.navigate(
                when (screen) {
                    AppScreens.HOME -> HomeScreen
                    AppScreens.ACCOUNT_LIST -> AccountListScreen
                    AppScreens.CATEGORY_LIST -> CategoryListScreen
                    AppScreens.STATISTICS -> StatisticsScreen
                    AppScreens.REMINDER_LIST -> ReminderListScreen
                    else -> throw IllegalArgumentException("Unknown navigation item screen")
                }
            ) {
                popUpTo(HomeScreen)
                launchSingleTop = true
            }
        }
    }

    val gesturesEnabled = when (currentScreen) {
        AppScreens.HOME,
        AppScreens.ACCOUNT_LIST,
        AppScreens.CATEGORY_LIST,
        AppScreens.STATISTICS,
        AppScreens.REMINDER_LIST -> true

        else -> false
    }

    AppNavigationDrawer(
        currentScreens = currentScreen,
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        onNavigationItemClick = onNavigationItemClick
    ) {
        NavHost(
            navController = navController,
            startDestination = HomeScreen
        ) {
            composable<HomeScreen> {
                HomeScreen(
                    navigateToCreateOperationScreen = { accountId ->
                        navController.navigate(OperationScreen(accountId = accountId))
                    },
                    navigateToOperationsByCategoryScreen = { operationType, accountId, categoryId, period ->
                        navController.navigate(
                            OperationsByCategoryScreen(
                                operationType = operationType,
                                accountId = accountId,
                                categoryId = categoryId,
                                period = period
                            )
                        )
                    },
                    openNavigationDrawer = { scope.launch { drawerState.open() } },
                )
            }

            composable<AccountListScreen> {
                AccountListScreen(
                    navigateToCreateAccountScreen = {
                        navController.navigate(AccountScreen())
                    },
                    navigateToCreateTransferScreen = {
                        navController.navigate(TransferScreen())
                    },
                    navigateToEditAccountScreen = { accountId ->
                        navController.navigate(AccountScreen(accountId))
                    },
                    openNavigationDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable<CategoryListScreen> {
                CategoryListScreen(
                    navigateToCreateCategoryScreen = {
                        navController.navigate(
                            CategoryScreen(initialSelectedOperationType = it)
                        )
                    },
                    navigateToCategoryWithSubcategoriesScreen = {
                        navController.navigate(CategoryWithSubcategoriesScreen(it))
                    },
                    openNavigationDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable<StatisticsScreen> { }

            composable<ReminderListScreen> {
                ReminderListScreen(
                    navigateToCreateReminderScreen = { navController.navigate(ReminderScreen()) },
                    navigateToEditReminderScreen = { navController.navigate(ReminderScreen(it)) },
                    openNavigationDrawer = { scope.launch { drawerState.open() } }
                )
            }

            composable<ReminderScreen> {
                ReminderScreen(
                    navigateBack = { navController.navigateUp() }
                )
            }

            composable<AccountScreen> {
                AccountScreen(
                    navigateBack = { navController.navigateUp() }
                )
            }

            composable<TransferScreen> {
                TransferScreen(
                    navigateBack = { navController.navigateUp() }
                )
            }

            composable<CategoryScreen> {
                CategoryScreen(
                    navigateBack = { navController.navigateUp() },
                    navigateToCategoryListScreen = {
                        navController.popBackStack(
                            route = CategoryListScreen,
                            inclusive = false
                        )
                    },
                )
            }

            composable<CategoryWithSubcategoriesScreen> {
                CategoryWithSubcategoriesScreen(
                    navigateBack = { navController.navigateUp() },
                    navigateToEditCategoryScreen = {
                        navController.navigate(CategoryScreen(categoryId = it))
                    }
                )
            }

            composable<OperationScreen> {
                OperationScreen(
                    navigateBack = { navController.navigateUp() }
                )
            }

            composable<OperationsByCategoryScreen> {
                OperationsByCategoryScreen(
                    navigateBack = { navController.navigateUp() },
                    navigateToCreateOperationOrTransferScreen = { operationType, accountId, categoryId ->
                        when (operationType) {
                            OperationType.EXPENSES, OperationType.INCOME -> {
                                navController.navigate(
                                    OperationScreen(
                                        accountId = accountId,
                                        categoryId = categoryId
                                    )
                                )
                            }

                            OperationType.OUTCOME_TRANSFER,
                            OperationType.INCOME_TRANSFER,
                            OperationType.TRANSFER -> {
                                navController.navigate(TransferScreen())
                            }
                        }
                    },
                    navigateToEditOperationScreen = {
                        navController.navigate(OperationScreen(operationId = it))
                    },
                    navigateToEditTransferScreen = {
                        navController.navigate(TransferScreen(it))
                    }
                )
            }
        }
    }
}