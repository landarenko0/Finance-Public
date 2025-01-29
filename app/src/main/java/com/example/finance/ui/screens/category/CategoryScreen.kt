package com.example.finance.ui.screens.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.domain.entities.OperationType
import com.example.finance.ui.common.BackTopBar
import com.example.finance.ui.common.ConfirmationDialog
import com.example.finance.ui.common.FinanceTextField
import com.example.finance.ui.common.MessageToUserDialog
import com.example.finance.ui.common.SaveButton
import com.example.finance.ui.common.SelectableOperationTypeCard

@Composable
fun CategoryScreen(
    onBackIconClick: () -> Unit,
    onCategoryDelete: () -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val modifier = Modifier
        .fillMaxSize()
        .clickable(
            onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
            },
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        )

    when (uiState.details) {
        is CategoryDetails.CreateCategory -> {
            CreateCategoryScreen(
                onBackIconClick = onBackIconClick,
                onCategoryNameChanged = viewModel::updateCategoryName,
                onOperationTypeChanged = viewModel::updateOperationType,
                onUiEvent = viewModel::onUiEvent,
                uiState = uiState,
                modifier = modifier
            )
        }

        is CategoryDetails.EditCategory -> {
            EditCategoryScreen(
                onBackIconClick = onBackIconClick,
                onCategoryNameChanged = viewModel::updateCategoryName,
                onUiEvent = viewModel::onUiEvent,
                onCategoryDelete = onCategoryDelete,
                uiState = uiState,
                modifier = modifier
            )
        }

        CategoryDetails.Initial -> {}
    }
}

@Composable
private fun CreateCategoryScreen(
    onBackIconClick: () -> Unit,
    onCategoryNameChanged: (String) -> Unit,
    onOperationTypeChanged: (OperationType) -> Unit,
    onUiEvent: (CategoryUiEvent) -> Unit,
    uiState: CategoryUiState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Создание категории",
                onBackIconClick = onBackIconClick,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Screen(
            onBackIconClick = onBackIconClick,
            onCategoryNameChanged = onCategoryNameChanged,
            onOperationTypeChanged = onOperationTypeChanged,
            onUiEvent = onUiEvent,
            selectableOperationTypeCardEnabled = true,
            uiState = uiState,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding() + 12.dp,
                    bottom = 24.dp
                )
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun EditCategoryScreen(
    onBackIconClick: () -> Unit,
    onCategoryNameChanged: (String) -> Unit,
    onCategoryDelete: () -> Unit,
    onUiEvent: (CategoryUiEvent) -> Unit,
    uiState: CategoryUiState,
    modifier: Modifier = Modifier
) {
    val details = uiState.details as CategoryDetails.EditCategory

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Редактирование категории",
                onBackIconClick = onBackIconClick,
                actions = {
                    IconButton(onClick = { onUiEvent(CategoryUiEvent.OnDeleteIconClick) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Screen(
            onBackIconClick = onBackIconClick,
            onCategoryNameChanged = onCategoryNameChanged,
            onOperationTypeChanged = {},
            onUiEvent = onUiEvent,
            selectableOperationTypeCardEnabled = false,
            uiState = uiState,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding() + 12.dp,
                    bottom = 24.dp
                )
                .padding(horizontal = 16.dp)
        )
    }

    if (details.showDeleteCategoryDialog) {
        ConfirmationDialog(
            text = "Вы действительно хотите удалить категорию ${details.selectedCategoryName}? Все связанные с ней операции и подкатегории будут удалены!",
            onConfirm = { onUiEvent(CategoryUiEvent.OnConfirmDeleteCategoryDialog) },
            onDismiss = { onUiEvent(CategoryUiEvent.OnDialogDismiss) }
        )
    }

    if (details.categoryDeleted) onCategoryDelete()
}

@Composable
private fun Screen(
    onBackIconClick: () -> Unit,
    onCategoryNameChanged: (String) -> Unit,
    onOperationTypeChanged: (OperationType) -> Unit,
    onUiEvent: (CategoryUiEvent) -> Unit,
    selectableOperationTypeCardEnabled: Boolean,
    uiState: CategoryUiState,
    modifier: Modifier = Modifier
) {
    val categoryNameFocusRequester = remember { FocusRequester() }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        SelectableOperationTypeCard(
            selectedOperationType = uiState.selectedOperationType,
            onOperationTypeClick = onOperationTypeChanged,
            enabled = selectableOperationTypeCardEnabled,
            modifier = Modifier.fillMaxWidth()
        )

        FinanceTextField(
            value = uiState.categoryName,
            onValueChange = onCategoryNameChanged,
            label = "Название",
            isError = uiState.categoryNameError,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(categoryNameFocusRequester)
        )

        Spacer(Modifier.weight(1f))

        SaveButton(
            onClick = { onUiEvent(CategoryUiEvent.OnSaveButtonCLick) },
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.showCategoryNameCollisionDialog) {
            MessageToUserDialog(
                title = "Выберите другое название категории",
                message = "Категория с таким названием уже существует",
                onConfirm = { onUiEvent(CategoryUiEvent.OnConfirmCategoryNameCollisionDialog) }
            )
        }

        if (uiState.closeScreen) onBackIconClick()

        if (uiState.requestCategoryNameFocus) {
            categoryNameFocusRequester.requestFocus()
            onUiEvent(CategoryUiEvent.OnFocusRequested)
        }
    }
}