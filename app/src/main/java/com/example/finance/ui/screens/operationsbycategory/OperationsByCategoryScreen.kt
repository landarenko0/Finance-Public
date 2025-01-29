package com.example.finance.ui.screens.operationsbycategory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finance.domain.entities.OperationType
import com.example.finance.ui.common.AddFloatingActionButton
import com.example.finance.ui.common.BackTopBar
import com.example.finance.ui.common.PeriodText
import com.example.finance.ui.screens.operationsbycategory.components.OperationList
import com.example.finance.ui.screens.operationsbycategory.components.OperationsByCategoryScreenTopBarTitle
import com.example.finance.ui.screens.operationsbycategory.components.TransferList

@Composable
fun OperationsByCategoryScreen(
    onBackIconClick: () -> Unit,
    onFloatingButtonClick: (OperationType, Int, Int?) -> Unit,
    onOperationClick: (Int) -> Unit,
    onTransferClick: (Int) -> Unit,
    viewModel: OperationsByCategoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            BackTopBar(
                title = {
                    OperationsByCategoryScreenTopBarTitle(
                        title = uiState.categoryName,
                        totalSum = uiState.transactionsSum
                    )
                },
                onBackIconClick = onBackIconClick
            )
        },
        floatingActionButton = {
            AddFloatingActionButton(
                onClick = {
                    onFloatingButtonClick(
                        uiState.operationType,
                        uiState.accountId,
                        uiState.categoryId
                    )
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding() + 12.dp)
                .padding(horizontal = 16.dp)
        ) {
            PeriodText(period = uiState.period)

            when (val details = uiState.details) {
                is OperationsByCategoryDetails.Operations -> {
                    OperationList(
                        operations = details.operations,
                        onOperationClick = onOperationClick
                    )
                }

                is OperationsByCategoryDetails.Transfers -> {
                    TransferList(
                        transfers = details.transfers,
                        operationType = uiState.operationType,
                        onTransferClick = onTransferClick
                    )
                }

                OperationsByCategoryDetails.Initial -> {}
            }
        }
    }
}