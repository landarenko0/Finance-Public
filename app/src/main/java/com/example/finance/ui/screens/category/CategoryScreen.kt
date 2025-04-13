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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.finance.ui.common.BackTopBar
import com.example.finance.ui.common.ConfirmationDialog
import com.example.finance.ui.common.FinanceTextField
import com.example.finance.ui.common.MessageToUserDialog
import com.example.finance.ui.common.SaveButton
import com.example.finance.ui.common.SelectableOperationTypeCard

@Composable
fun CategoryScreen(
    navigateBack: () -> Unit,
    navigateToCategoryListScreen: () -> Unit,
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

    val categoryNameFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                CategoryEvent.CloseScreen -> navigateBack()
                CategoryEvent.NavigateToCategoryListScreen -> navigateToCategoryListScreen()
                CategoryEvent.RequestCategoryNameFocus -> categoryNameFocusRequester.requestFocus()
            }
        }
    }

    when (uiState.details) {
        is CategoryDetails.CreateCategory -> {
            CreateCategoryScreen(
                onUiEvent = viewModel::onUiEvent,
                uiState = uiState,
                categoryNameFocusRequester = categoryNameFocusRequester,
                modifier = modifier
            )
        }

        is CategoryDetails.EditCategory -> {
            EditCategoryScreen(
                onUiEvent = viewModel::onUiEvent,
                uiState = uiState,
                categoryNameFocusRequester = categoryNameFocusRequester,
                modifier = modifier
            )
        }

        CategoryDetails.Initial -> {}
    }
}

@Composable
private fun CreateCategoryScreen(
    onUiEvent: (CategoryUiEvent) -> Unit,
    uiState: CategoryUiState,
    categoryNameFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Создание категории",
                onBackIconClick = { onUiEvent(CategoryUiEvent.OnBackIconClick) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Screen(
            onUiEvent = onUiEvent,
            selectableOperationTypeCardEnabled = true,
            uiState = uiState,
            categoryNameFocusRequester = categoryNameFocusRequester,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun EditCategoryScreen(
    onUiEvent: (CategoryUiEvent) -> Unit,
    uiState: CategoryUiState,
    categoryNameFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val details = uiState.details as CategoryDetails.EditCategory

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Редактирование категории",
                onBackIconClick = { onUiEvent(CategoryUiEvent.OnBackIconClick) },
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
            onUiEvent = onUiEvent,
            selectableOperationTypeCardEnabled = false,
            uiState = uiState,
            categoryNameFocusRequester = categoryNameFocusRequester,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }

    if (details.showDeleteCategoryDialog) {
        ConfirmationDialog(
            text = "Вы действительно хотите удалить категорию ${details.selectedCategoryName}? Все связанные с ней операции и подкатегории будут удалены!",
            onConfirm = { onUiEvent(CategoryUiEvent.OnConfirmDeleteCategoryDialog) },
            onDismiss = { onUiEvent(CategoryUiEvent.OnDialogDismiss) }
        )
    }
}

@Composable
private fun Screen(
    onUiEvent: (CategoryUiEvent) -> Unit,
    selectableOperationTypeCardEnabled: Boolean,
    uiState: CategoryUiState,
    categoryNameFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        SelectableOperationTypeCard(
            selectedOperationType = uiState.selectedOperationType,
            onOperationTypeClick = { onUiEvent(CategoryUiEvent.OnOperationTypeChanged(it)) },
            enabled = selectableOperationTypeCardEnabled,
            modifier = Modifier.fillMaxWidth()
        )

        FinanceTextField(
            value = uiState.categoryName,
            onValueChange = { onUiEvent(CategoryUiEvent.OnCategoryNameChanged(it)) },
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
                message = "Категория с таким типом и названием уже существует",
                onConfirm = { onUiEvent(CategoryUiEvent.OnConfirmCategoryNameCollisionDialog) }
            )
        }
    }
}