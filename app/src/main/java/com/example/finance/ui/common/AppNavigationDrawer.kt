package com.example.finance.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.finance.ui.navigation.AppScreens
import kotlinx.coroutines.launch

@Composable
fun AppNavigationDrawer(
    currentScreens: AppScreens,
    drawerState: DrawerState,
    onNavigationItemClick: (AppScreens) -> Unit,
    gesturesEnabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                NavigationItems.forEach { item ->
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.displaySmall
                            )
                        },
                        selected = item.screen == currentScreens,
                        onClick = {
                            onNavigationItemClick(item.screen)
                            scope.launch { drawerState.close() }
                        },
                        icon = {
                            Icon(
                                imageVector = if (item.screen == currentScreens) item.selectedIcon else item.unselectedIcon,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        content = content,
        modifier = modifier
    )
}