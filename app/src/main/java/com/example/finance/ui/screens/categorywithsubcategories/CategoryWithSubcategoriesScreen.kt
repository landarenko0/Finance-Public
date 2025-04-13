package com.example.finance.ui.screens.categorywithsubcategories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.finance.ui.common.ConfirmationDialog
import com.example.finance.ui.common.MessageToUserDialog
import com.example.finance.ui.screens.categorywithsubcategories.components.SubcategoriesList
import com.example.finance.ui.screens.categorywithsubcategories.components.SubcategoriesScreenTopBar
import com.example.finance.ui.screens.categorywithsubcategories.components.SubcategoryDialog

@Composable
fun CategoryWithSubcategoriesScreen(
    navigateBack: () -> Unit,
    navigateToEditCategoryScreen: (Int) -> Unit,
    viewModel: CategoryWithSubcategoriesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val operationType = if (uiState.category.type == OperationType.EXPENSES) "Расход" else "Доход"

    Scaffold(
        topBar = {
            SubcategoriesScreenTopBar(
                title = "${uiState.category.name} - $operationType",
                onBackIconClick = navigateBack,
                onCloseIconClick = { viewModel.onUiEvent(CategoryWithSubcategoriesUiEvent.OnCloseIconClick) },
                deleteSubcategoriesEnabled = uiState.deleteSubcategoriesEnabled,
                actions = {
                    IconButton(
                        onClick = { navigateToEditCategoryScreen(uiState.category.id) },
                        enabled = !uiState.deleteSubcategoriesEnabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    }

                    if (uiState.deleteSubcategoriesEnabled) {
                        IconButton(
                            onClick = { viewModel.onUiEvent(CategoryWithSubcategoriesUiEvent.OnDeleteIconClick) },
                            enabled = uiState.selectedSubcategories.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        floatingActionButton = {
            AddFloatingActionButton(
                onClick = { viewModel.onUiEvent(CategoryWithSubcategoriesUiEvent.OnFloatingButtonClick) }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            if (uiState.subcategories.isNotEmpty()) {
                Text(text = "Подкатегории")

                SubcategoriesList(
                    subcategories = uiState.subcategories,
                    onSubcategoryClick = {
                        viewModel.onUiEvent(CategoryWithSubcategoriesUiEvent.OnSubcategoryClick(it))
                    },
                    onLongSubcategoryClick = {
                        viewModel.onUiEvent(
                            CategoryWithSubcategoriesUiEvent.OnLongSubcategoryClick(it)
                        )
                    },
                    deleteSubcategoriesEnabled = uiState.deleteSubcategoriesEnabled,
                    selectedSubcategoriesIds = uiState.selectedSubcategories,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "Нет подкатегорий")
                }
            }
        }
    }

    if (uiState.showSubcategoryDialog) {
        SubcategoryDialog(
            initialText = uiState.selectedSubcategory?.name,
            onConfirm = { viewModel.onUiEvent(CategoryWithSubcategoriesUiEvent.OnSaveButtonClick(it)) },
            onDismiss = { viewModel.onUiEvent(CategoryWithSubcategoriesUiEvent.OnDialogDismiss) }
        )
    }

    if (uiState.showSubcategoryNameCollisionDialog) {
        MessageToUserDialog(
            title = "Выберите другое название подкатегории",
            message = "Подкатегория с таким названием уже существует в категории ${uiState.category.name}",
            onConfirm = { viewModel.onUiEvent(CategoryWithSubcategoriesUiEvent.OnConfirmSubcategoryNameCollision) }
        )
    }

    if (uiState.showDeleteSubcategoriesDialog) {
        ConfirmationDialog(
            text = "Вы действительно хотите удалить выбранные подкатегории? Связанные с ними операции не будут удалены",
            onConfirm = { viewModel.onUiEvent(CategoryWithSubcategoriesUiEvent.OnConfirmDeleteSubcategoriesDialog) },
            onDismiss = { viewModel.onUiEvent(CategoryWithSubcategoriesUiEvent.OnDialogDismiss) }
        )
    }
}