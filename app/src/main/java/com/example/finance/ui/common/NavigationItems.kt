package com.example.finance.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.DonutLarge
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.finance.ui.navigation.AppScreens

val NavigationItems = listOf(
    NavigationItem(
        title = "Главная",
        screen = AppScreens.HOME,
        selectedIcon = Icons.Filled.DonutLarge,
        unselectedIcon = Icons.Outlined.DonutLarge
    ),
    NavigationItem(
        title = "Счета",
        screen = AppScreens.ACCOUNT_LIST,
        selectedIcon = Icons.Filled.AccountBalanceWallet,
        unselectedIcon = Icons.Outlined.AccountBalanceWallet
    ),
    NavigationItem(
        title = "Категории",
        screen = AppScreens.CATEGORY_LIST,
        selectedIcon = Icons.AutoMirrored.Filled.List,
        unselectedIcon = Icons.AutoMirrored.Outlined.List
    ),
    NavigationItem(
        title = "Статистика",
        screen = AppScreens.STATISTICS,
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    ),
    NavigationItem(
        title = "Напоминания",
        screen = AppScreens.REMINDER_LIST,
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications
    )
)

data class NavigationItem(
    val title: String,
    val screen: AppScreens,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
