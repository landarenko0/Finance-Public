package com.example.finance.ui.screens.operation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.domain.entities.OperationType
import com.example.finance.ui.common.AccountPickerDialog
import com.example.finance.ui.common.BackTopBar
import com.example.finance.ui.common.ConfirmationDialog
import com.example.finance.ui.common.DatePickerModalDialog
import com.example.finance.ui.common.FinanceTextField
import com.example.finance.ui.common.PickerWithTitle
import com.example.finance.ui.common.SaveButton
import com.example.finance.ui.common.SumTextField
import com.example.finance.ui.screens.operation.components.CategoryPickerDialog
import com.example.finance.ui.screens.operation.components.SubcategoryPickerDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OperationScreen(
    onBackIconClick: () -> Unit,
    viewModel: OperationViewModel = hiltViewModel()
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
        OperationDetails.CreateOperation -> {
            CreateOperationScreen(
                onBackIconClick = onBackIconClick,
                onOperationSumChanged = viewModel::onOperationSumChanged,
                onCommentChanged = viewModel::onCommentChanged,
                onUiEvent = viewModel::onUiEvent,
                uiState = uiState,
                modifier = modifier
            )
        }

        is OperationDetails.EditOperation -> {
            EditOperationScreen(
                onBackIconClick = onBackIconClick,
                onOperationSumChanged = viewModel::onOperationSumChanged,
                onCommentChanged = viewModel::onCommentChanged,
                uiState = uiState,
                onUiEvent = viewModel::onUiEvent,
                modifier = modifier
            )
        }

        OperationDetails.Initial -> {}
    }
}

@Composable
private fun CreateOperationScreen(
    onBackIconClick: () -> Unit,
    onOperationSumChanged: (String) -> Unit,
    onCommentChanged: (String) -> Unit,
    onUiEvent: (OperationUiEvent) -> Unit,
    uiState: OperationUiState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Создание операции",
                onBackIconClick = onBackIconClick,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Screen(
            onBackIconClick = onBackIconClick,
            onOperationSumChanged = onOperationSumChanged,
            onCommentChanged = onCommentChanged,
            onUiEvent = onUiEvent,
            uiState = uiState,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding() + 12.dp,
                    bottom = 24.dp
                )
                .padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun EditOperationScreen(
    onBackIconClick: () -> Unit,
    onOperationSumChanged: (String) -> Unit,
    onCommentChanged: (String) -> Unit,
    onUiEvent: (OperationUiEvent) -> Unit,
    uiState: OperationUiState,
    modifier: Modifier = Modifier
) {
    val details = uiState.details as OperationDetails.EditOperation

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Редактирование операции",
                onBackIconClick = onBackIconClick,
                actions = {
                    IconButton(onClick = { onUiEvent(OperationUiEvent.OnDeleteOperationIconClick) }) {
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
            onOperationSumChanged = onOperationSumChanged,
            onCommentChanged = onCommentChanged,
            onUiEvent = onUiEvent,
            uiState = uiState,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding() + 12.dp,
                    bottom = 24.dp
                )
                .padding(horizontal = 16.dp),
        )
    }

    if (details.showDeleteOperationDialog) {
        ConfirmationDialog(
            text = "Вы действительно хотите удалить операцию? Деньги будут возвращены на счет",
            onConfirm = { onUiEvent(OperationUiEvent.OnDeleteOperationConfirmed) },
            onDismiss = { onUiEvent(OperationUiEvent.OnDeleteOperationDialogDismiss) }
        )
    }
}

@Composable
private fun Screen(
    onBackIconClick: () -> Unit,
    onOperationSumChanged: (String) -> Unit,
    onCommentChanged: (String) -> Unit,
    onUiEvent: (OperationUiEvent) -> Unit,
    uiState: OperationUiState,
    modifier: Modifier = Modifier
) {
    val operationSumFocusRequester = remember { FocusRequester() }

    val date = Date(uiState.selectedDate)
    val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.sign.isNotEmpty()) {
                Text(
                    text = uiState.sign,
                    style = MaterialTheme.typography.displayLarge,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }

            SumTextField(
                sum = uiState.operationSum,
                onValueChange = onOperationSumChanged,
                isError = uiState.operationSumError,
                modifier = Modifier.focusRequester(operationSumFocusRequester)
            )
        }

        PickerWithTitle(
            title = "Счет",
            pickerText = uiState.selectedAccount?.name ?: "Не выбрано",
            onPickerClick = { onUiEvent(OperationUiEvent.OnAccountPickerClick) }
        )

        PickerWithTitle(
            title = "Категория",
            pickerText = uiState.selectedCategory?.name ?: "Не выбрано",
            onPickerClick = { onUiEvent(OperationUiEvent.OnCategoryPickerClick) }
        )

        if (uiState.showSubcategoryPicker) {
            PickerWithTitle(
                title = "Подкатегория - необязательно",
                pickerText = uiState.selectedSubcategory?.name ?: "Не выбрано",
                onPickerClick = { onUiEvent(OperationUiEvent.OnSubcategoryPickerClick) }
            )
        }

        PickerWithTitle(
            title = "Дата",
            pickerText = formattedDate,
            onPickerClick = { onUiEvent(OperationUiEvent.OnDatePickerClick) }
        )

        FinanceTextField(
            value = uiState.comment,
            onValueChange = onCommentChanged,
            label = "Комментарий",
            placeholder = "Напишите что-нибудь",
            maxLines = 5,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        SaveButton(
            onClick = { onUiEvent(OperationUiEvent.OnSaveButtonClick) },
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (uiState.showAccountPickerDialog) {
        AccountPickerDialog(
            accounts = uiState.accounts,
            initialSelectedAccountId = uiState.selectedAccount?.id ?: -1,
            onConfirmButtonClick = { onUiEvent(OperationUiEvent.OnNewAccountSelected(it)) },
            onDismiss = { onUiEvent(OperationUiEvent.OnDialogDismiss) }
        )
    }

    if (uiState.showCategoryPickerDialog) {
        CategoryPickerDialog(
            incomeCategories = uiState.incomeCategories,
            expensesCategories = uiState.expensesCategories,
            initialSelectedOperationType = uiState.selectedCategory?.type ?: OperationType.EXPENSES,
            initialSelectedCategoryId = uiState.selectedCategory?.id ?: -1,
            onConfirmButtonClick = { onUiEvent(OperationUiEvent.OnNewCategorySelected(it)) },
            onDismiss = { onUiEvent(OperationUiEvent.OnDialogDismiss) }
        )
    }

    if (uiState.showSubcategoryPickerDialog) {
        SubcategoryPickerDialog(
            subcategories = uiState.subcategories,
            initialSelectedSubcategoryId = uiState.selectedSubcategory?.id ?: -1,
            onConfirmButtonClick = { onUiEvent(OperationUiEvent.OnNewSubcategorySelected(it)) },
            onDismiss = { onUiEvent(OperationUiEvent.OnDialogDismiss) }
        )
    }

    if (uiState.showDatePickerDialog) {
        DatePickerModalDialog(
            initialSelectedDate = uiState.selectedDate,
            onConfirmButtonClick = { onUiEvent(OperationUiEvent.OnNewDateSelected(it)) },
            onDismiss = { onUiEvent(OperationUiEvent.OnDialogDismiss) }
        )
    }

    if (uiState.closeScreen) onBackIconClick()

    if (uiState.requestAccountSumFocus) {
        operationSumFocusRequester.requestFocus()
        onUiEvent(OperationUiEvent.OnFocusRequested)
    }
}