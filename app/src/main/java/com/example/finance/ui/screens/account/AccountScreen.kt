package com.example.finance.ui.screens.account

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.ui.common.BackTopBar
import com.example.finance.ui.common.ConfirmationDialog
import com.example.finance.ui.common.FinanceTextField
import com.example.finance.ui.common.MessageToUserDialog
import com.example.finance.ui.common.SaveButton
import com.example.finance.ui.common.AccountPickerDialog
import com.example.finance.ui.common.SumTextField
import com.example.finance.ui.screens.account.components.TransferAccountBalanceDialog

@Composable
fun AccountScreen(
    onBackIconClick: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
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
        is AccountDetails.CreateAccount -> {
            CreateAccountScreen(
                onBackIconClick = onBackIconClick,
                onAccountSumChanged = viewModel::updateAccountSum,
                onAccountNameChanged = viewModel::updateAccountName,
                onUiEvent = viewModel::onUiEvent,
                uiState = uiState,
                modifier = modifier
            )
        }

        is AccountDetails.EditAccount -> {
            EditAccountScreen(
                onBackIconClick = onBackIconClick,
                onAccountSumChanged = viewModel::updateAccountSum,
                onAccountNameChanged = viewModel::updateAccountName,
                onUiEvent = viewModel::onUiEvent,
                uiState = uiState,
                modifier = modifier
            )
        }

        AccountDetails.Initial-> {}
    }
}

@Composable
private fun CreateAccountScreen(
    onBackIconClick: () -> Unit,
    onAccountSumChanged: (String) -> Unit,
    onAccountNameChanged: (String) -> Unit,
    onUiEvent: (AccountUiEvent) -> Unit,
    uiState: AccountUiState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Создание счета",
                onBackIconClick = onBackIconClick,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Screen(
            onBackIconClick = onBackIconClick,
            onAccountSumChanged = onAccountSumChanged,
            onAccountNameChanged = onAccountNameChanged,
            onUiEvent = onUiEvent,
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
private fun EditAccountScreen(
    onBackIconClick: () -> Unit,
    onAccountSumChanged: (String) -> Unit,
    onAccountNameChanged: (String) -> Unit,
    onUiEvent: (AccountUiEvent) -> Unit,
    uiState: AccountUiState,
    modifier: Modifier = Modifier
) {
    val details = uiState.details as AccountDetails.EditAccount

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Редактирование счета",
                onBackIconClick = onBackIconClick,
                actions = {
                    IconButton(onClick = { onUiEvent(AccountUiEvent.OnDeleteIconClick) }) {
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
            onAccountSumChanged = onAccountSumChanged,
            onAccountNameChanged = onAccountNameChanged,
            onUiEvent = onUiEvent,
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

    if (details.showDeleteAccountDialog) {
        ConfirmationDialog(
            text = "Вы действительно хотите удалить счет? При удалении все операции по счету также будут удалены",
            onConfirm = { onUiEvent(AccountUiEvent.OnConfirmDeleteAccountDialog) },
            onDismiss = { onUiEvent(AccountUiEvent.OnDismissDialog) }
        )
    }

    if (details.showTransferAccountBalanceDialog) {
        TransferAccountBalanceDialog(
            accountSum = details.accountSum,
            onConfirmButtonClick = { onUiEvent(AccountUiEvent.OnConfirmTransferAccountBalanceDialog) },
            onDismissButtonClick = { onUiEvent(AccountUiEvent.OnDismissTransferAccountBalanceDialog) },
            onDismissRequest = { onUiEvent(AccountUiEvent.OnDismissDialog) }
        )
    }

    if (details.showSelectAccountDialog) {
        AccountPickerDialog(
            accounts = uiState.accounts,
            initialSelectedAccountId = -1,
            onConfirmButtonClick = { onUiEvent(AccountUiEvent.OnTransferAccountSelected(it)) },
            onDismiss = { onUiEvent(AccountUiEvent.OnDismissDialog) }
        )
    }
}

@Composable
private fun Screen(
    onBackIconClick: () -> Unit,
    onAccountSumChanged: (String) -> Unit,
    onAccountNameChanged: (String) -> Unit,
    onUiEvent: (AccountUiEvent) -> Unit,
    uiState: AccountUiState,
    modifier: Modifier
) {
    val accountSumFocusRequester = remember { FocusRequester() }
    val accountNameFocusRequester = remember { FocusRequester() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        SumTextField(
            sum = uiState.accountSum,
            onValueChange = { onAccountSumChanged(it) },
            isError = uiState.accountSumError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.focusRequester(accountSumFocusRequester)
        )

        FinanceTextField(
            value = uiState.accountName,
            onValueChange = { onAccountNameChanged(it) },
            label = "Название",
            isError = uiState.accountNameError,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(accountNameFocusRequester)
        )

        Spacer(Modifier.weight(1f))

        SaveButton(
            onClick = { onUiEvent(AccountUiEvent.OnSaveButtonClick) },
            modifier = Modifier.fillMaxWidth()
        )
    }

    if (uiState.showAccountNameCollisionDialog) {
        MessageToUserDialog(
            title = "Выберите другое название счета",
            message = "Счет с таким названием уже существует",
            onConfirm = { onUiEvent(AccountUiEvent.OnConfirmAccountNameCollisionDialog) }
        )
    }

    if (uiState.requestAccountSumFocus) {
        accountSumFocusRequester.requestFocus()
        onUiEvent(AccountUiEvent.OnFocusRequested)
    }

    if (uiState.requestAccountNameFocus) {
        accountNameFocusRequester.requestFocus()
        onUiEvent(AccountUiEvent.OnFocusRequested)
    }

    if (uiState.closeScreen) onBackIconClick()
}