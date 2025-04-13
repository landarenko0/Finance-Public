package com.example.finance.ui.navigation

import com.example.finance.domain.entities.OperationType
import kotlinx.serialization.Serializable

enum class AppScreens {
    HOME,
    OPERATIONS_BY_CATEGORY,
    OPERATION,
    ACCOUNT_LIST,
    ACCOUNT,
    TRANSFER,
    CATEGORY_LIST,
    CATEGORY_WITH_SUBCATEGORIES,
    CATEGORY,
    STATISTICS,
    REMINDER_LIST,
    REMINDER;

    companion object {
        fun getScreenByRoute(route: String?): AppScreens {
            return when {
                route == null || route.contains("HomeScreen") -> HOME
                route.contains("OperationsByCategoryScreen") -> OPERATIONS_BY_CATEGORY
                route.contains("OperationScreen") -> OPERATION
                route.contains("AccountListScreen") -> ACCOUNT_LIST
                route.contains("AccountScreen") -> ACCOUNT
                route.contains("TransferScreen") -> TRANSFER
                route.contains("CategoryListScreen") -> CATEGORY_LIST
                route.contains("CategoryWithSubcategoriesScreen") -> CATEGORY_WITH_SUBCATEGORIES
                route.contains("CategoryScreen") -> CATEGORY
                route.contains("StatisticsScreen") -> STATISTICS
                route.contains("ReminderListScreen") -> REMINDER_LIST
                route.contains("ReminderScreen") -> REMINDER
                else -> throw IllegalArgumentException("Unknown screen route")
            }
        }
    }
}

@Serializable
data object HomeScreen

@Serializable
data class OperationsByCategoryScreen(
    val operationType: OperationType,
    val accountId: Int,
    val categoryId: Int? = null,
    val period: String
)

@Serializable
data class OperationScreen(
    val operationId: Int? = null,
    val accountId: Int? = null,
    val categoryId: Int? = null
)

@Serializable
data object AccountListScreen

@Serializable
data class AccountScreen(val accountId: Int? = null)

@Serializable
data class TransferScreen(val transferId: Int? = null)

@Serializable
data object CategoryListScreen

@Serializable
data class CategoryWithSubcategoriesScreen(val categoryId: Int)

@Serializable
data class CategoryScreen(
    val initialSelectedOperationType: OperationType = OperationType.EXPENSES,
    val categoryId: Int? = null
)

@Serializable
data object StatisticsScreen

@Serializable
data object ReminderListScreen

@Serializable
data class ReminderScreen(val reminderId: Int? = null)