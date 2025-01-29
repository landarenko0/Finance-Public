package com.example.finance.ui.navigation

import com.example.finance.domain.entities.OperationType
import kotlinx.serialization.Serializable

object AppScreens {

    @Serializable
    data object MainScreen

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
    data object ReminderScreen
}