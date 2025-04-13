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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.domain.entities.OperationType
import com.example.finance.ui.common.AccountPickerDialog
import com.example.finance.ui.common.BackTopBar
import com.example.finance.ui.common.ConfirmationDialog
import com.example.finance.ui.common.DatePickerModalDialog
import com.example.finance.ui.common.FinanceTextField
import com.example.finance.ui.common.PastOrPresentSelectableDates
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
    navigateBack: () -> Unit,
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

    val operationSumFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                OperationEvent.CloseScreen -> navigateBack()
                OperationEvent.RequestOperationSumFocus -> operationSumFocusRequester.requestFocus()
            }
        }
    }

    when (uiState.details) {
        OperationDetails.CreateOperation -> {
            CreateOperationScreen(
                onUiEvent = viewModel::onUiEvent,
                uiState = uiState,
                operationSumFocusRequester = operationSumFocusRequester,
                modifier = modifier
            )
        }

        is OperationDetails.EditOperation -> {
            EditOperationScreen(
                uiState = uiState,
                onUiEvent = viewModel::onUiEvent,
                operationSumFocusRequester = operationSumFocusRequester,
                modifier = modifier
            )
        }

        OperationDetails.Initial -> {}
    }
}

@Composable
private fun CreateOperationScreen(
    onUiEvent: (OperationUiEvent) -> Unit,
    uiState: OperationUiState,
    operationSumFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Создание операции",
                onBackIconClick = { onUiEvent(OperationUiEvent.OnBackIconClick) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Screen(
            onUiEvent = onUiEvent,
            uiState = uiState,
            operationSumFocusRequester = operationSumFocusRequester,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        )
    }
}

@Composable
private fun EditOperationScreen(
    onUiEvent: (OperationUiEvent) -> Unit,
    uiState: OperationUiState,
    operationSumFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val details = uiState.details as OperationDetails.EditOperation

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Редактирование операции",
                onBackIconClick = { onUiEvent(OperationUiEvent.OnBackIconClick) },
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
            onUiEvent = onUiEvent,
            uiState = uiState,
            operationSumFocusRequester = operationSumFocusRequester,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Screen(
    onUiEvent: (OperationUiEvent) -> Unit,
    uiState: OperationUiState,
    operationSumFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val date = Date(uiState.selectedDate)
    val formattedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(vertical = 12.dp, horizontal = 16.dp)
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
                onValueChange = { onUiEvent(OperationUiEvent.OnOperationSumChanged(it)) },
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
            onValueChange = { onUiEvent(OperationUiEvent.OnCommentChanged(it)) },
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
            onDismiss = { onUiEvent(OperationUiEvent.OnDialogDismiss) },
            selectableDates = PastOrPresentSelectableDates
        )
    }
}