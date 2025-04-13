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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.ui.common.AccountPickerDialog
import com.example.finance.ui.common.BackTopBar
import com.example.finance.ui.common.ConfirmationDialog
import com.example.finance.ui.common.FinanceTextField
import com.example.finance.ui.common.MessageToUserDialog
import com.example.finance.ui.common.SaveButton
import com.example.finance.ui.common.SumTextField
import com.example.finance.ui.screens.account.components.TransferAccountBalanceDialog

@Composable
fun AccountScreen(
    navigateBack: () -> Unit,
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

    val accountBalanceFocusRequester = remember { FocusRequester() }
    val accountNameFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                AccountEvent.CloseScreen -> navigateBack()
                AccountEvent.RequestAccountBalanceFocus -> accountBalanceFocusRequester.requestFocus()
                AccountEvent.RequestAccountNameFocus -> accountNameFocusRequester.requestFocus()
            }
        }
    }

    when (uiState.details) {
        is AccountDetails.CreateAccount -> {
            CreateAccountScreen(
                onUiEvent = viewModel::onUiEvent,
                uiState = uiState,
                accountBalanceFocusRequester = accountBalanceFocusRequester,
                accountNameFocusRequester = accountNameFocusRequester,
                modifier = modifier
            )
        }

        is AccountDetails.EditAccount -> {
            EditAccountScreen(
                onUiEvent = viewModel::onUiEvent,
                uiState = uiState,
                accountBalanceFocusRequester = accountBalanceFocusRequester,
                accountNameFocusRequester = accountNameFocusRequester,
                modifier = modifier
            )
        }

        AccountDetails.Initial -> {}
    }
}

@Composable
private fun CreateAccountScreen(
    onUiEvent: (AccountUiEvent) -> Unit,
    uiState: AccountUiState,
    accountBalanceFocusRequester: FocusRequester,
    accountNameFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            BackTopBar(
                title = "Создание счета",
                onBackIconClick = { onUiEvent(AccountUiEvent.OnBackIconClick) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Screen(
            onUiEvent = onUiEvent,
            uiState = uiState,
            accountBalanceFocusRequester = accountBalanceFocusRequester,
            accountNameFocusRequester = accountNameFocusRequester,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun EditAccountScreen(
    onUiEvent: (AccountUiEvent) -> Unit,
    uiState: AccountUiState,
    accountBalanceFocusRequester: FocusRequester,
    accountNameFocusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val details = uiState.details as AccountDetails.EditAccount

    Scaffold(
        topBar = {
            BackTopBar(
                title = "Редактирование счета",
                onBackIconClick = { onUiEvent(AccountUiEvent.OnBackIconClick) },
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
            onUiEvent = onUiEvent,
            uiState = uiState,
            accountBalanceFocusRequester = accountBalanceFocusRequester,
            accountNameFocusRequester = accountNameFocusRequester,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
            accountBalance = details.accountBalance,
            onConfirmButtonClick = { onUiEvent(AccountUiEvent.OnConfirmTransferAccountBalanceDialog) },
            onDismissButtonClick = { onUiEvent(AccountUiEvent.OnDismissTransferAccountBalanceDialog) },
            onDismissRequest = { onUiEvent(AccountUiEvent.OnDismissDialog) }
        )
    }

    if (details.showSelectAccountDialog) {
        AccountPickerDialog(
            accounts = details.otherAccounts,
            initialSelectedAccountId = -1,
            onConfirmButtonClick = { onUiEvent(AccountUiEvent.OnTransferAccountSelected(it)) },
            onDismiss = { onUiEvent(AccountUiEvent.OnDismissDialog) }
        )
    }
}

@Composable
private fun Screen(
    onUiEvent: (AccountUiEvent) -> Unit,
    uiState: AccountUiState,
    accountBalanceFocusRequester: FocusRequester,
    accountNameFocusRequester: FocusRequester,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        SumTextField(
            sum = uiState.accountBalance,
            onValueChange = { onUiEvent(AccountUiEvent.OnAccountBalanceChanged(it)) },
            isError = uiState.accountBalanceError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.focusRequester(accountBalanceFocusRequester)
        )

        FinanceTextField(
            value = uiState.accountName,
            onValueChange = { onUiEvent(AccountUiEvent.OnAccountNameChanged(it)) },
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
}