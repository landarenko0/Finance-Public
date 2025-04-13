package com.example.finance.ui.screens.operation

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.finance.domain.entities.Operation
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.usecases.AccountInteractor
import com.example.finance.domain.usecases.CategoryInteractor
import com.example.finance.domain.usecases.OperationInteractor
import com.example.finance.domain.usecases.SubcategoryInteractor
import com.example.finance.ui.navigation.OperationScreen
import com.example.finance.utils.toLocalDate
import com.example.finance.utils.toMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OperationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val operationInteractor: OperationInteractor,
    private val accountInteractor: AccountInteractor,
    private val categoryInteractor: CategoryInteractor,
    private val subcategoryInteractor: SubcategoryInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(OperationUiState())
    val uiState: StateFlow<OperationUiState> = _uiState.asStateFlow()

    private val _event = Channel<OperationEvent>()
    val event = _event.receiveAsFlow()

    private val operationScreenStateHandle = savedStateHandle.toRoute<OperationScreen>()

    private val operationId = operationScreenStateHandle.operationId
    private val accountId = operationScreenStateHandle.accountId
    private val categoryId = operationScreenStateHandle.categoryId

    init {
        viewModelScope.launch {
            if (operationId != null) {
                operationInteractor.getOperationById(operationId).also { operation ->
                    _uiState.update {
                        it.copy(
                            operationSum = operation.sum.toString(),
                            sign = if (operation.category.type == OperationType.EXPENSES) "-" else "+",
                            selectedAccount = operation.account,
                            selectedCategory = operation.category,
                            selectedSubcategory = operation.subcategory,
                            subcategories = subcategoryInteractor.getCategorySubcategories(operation.category.id),
                            selectedDate = operation.date.toMillis(),
                            comment = operation.comment,
                            showSubcategoryPicker = true,
                            details = OperationDetails.EditOperation()
                        )
                    }
                }
            } else {
                _uiState.update { it.copy(details = OperationDetails.CreateOperation) }

                accountId?.let {
                    if (accountId != 0) {
                        launch {
                            _uiState.update {
                                it.copy(
                                    selectedAccount = accountInteractor.getAccountById(accountId)
                                )
                            }
                        }
                    }
                }

                categoryId?.let {
                    launch {
                        _uiState.update {
                            it.copy(
                                selectedCategory = categoryInteractor.getCategoryById(categoryId),
                                showSubcategoryPicker = true
                            )
                        }
                    }
                }
            }

            launch {
                val accounts = async { accountInteractor.getAllAccounts().first() }
                val incomeCategories = async { categoryInteractor.getCategoriesByType(OperationType.INCOME).first() }
                val expensesCategories = async { categoryInteractor.getCategoriesByType(OperationType.EXPENSES).first() }

                _uiState.update {
                    it.copy(
                        accounts = accounts.await(),
                        incomeCategories = incomeCategories.await(),
                        expensesCategories = expensesCategories.await()
                    )
                }
            }
        }
    }

    fun onUiEvent(uiEvent: OperationUiEvent) {
        when (uiEvent) {
            OperationUiEvent.OnAccountPickerClick -> {
                _uiState.update { it.copy(showAccountPickerDialog = true) }
            }

            OperationUiEvent.OnCategoryPickerClick -> {
                _uiState.update { it.copy(showCategoryPickerDialog = true) }
            }

            OperationUiEvent.OnDatePickerClick -> {
                _uiState.update { it.copy(showDatePickerDialog = true) }
            }

            OperationUiEvent.OnDeleteOperationConfirmed -> deleteOperation()

            OperationUiEvent.OnDeleteOperationIconClick -> {
                _uiState.update {
                    it.copy(
                        details = (it.details as OperationDetails.EditOperation).copy(
                            showDeleteOperationDialog = true
                        )
                    )
                }
            }

            OperationUiEvent.OnDialogDismiss -> {
                _uiState.update {
                    it.copy(
                        showSubcategoryPickerDialog = false,
                        showDatePickerDialog = false,
                        showCategoryPickerDialog = false,
                        showAccountPickerDialog = false
                    )
                }
            }

            OperationUiEvent.OnDeleteOperationDialogDismiss -> {
                _uiState.update {
                    it.copy(
                        details = (it.details as OperationDetails.EditOperation).copy(
                            showDeleteOperationDialog = false
                        )
                    )
                }
            }

            is OperationUiEvent.OnNewAccountSelected -> updateSelectedAccount(uiEvent.accountId)

            is OperationUiEvent.OnNewCategorySelected -> updateSelectedCategory(uiEvent.categoryId)

            is OperationUiEvent.OnNewDateSelected -> {
                _uiState.update {
                    it.copy(
                        selectedDate = uiEvent.date,
                        showDatePickerDialog = false
                    )
                }
            }

            is OperationUiEvent.OnNewSubcategorySelected -> {
                updateSelectedSubcategory(uiEvent.subcategoryId)
            }

            OperationUiEvent.OnSaveButtonClick -> saveOperation()

            OperationUiEvent.OnSubcategoryPickerClick -> {
                _uiState.update { it.copy(showSubcategoryPickerDialog = true) }
            }

            is OperationUiEvent.OnCommentChanged -> {
                if (uiEvent.comment.length <= 1000) {
                    _uiState.update { it.copy(comment = uiEvent.comment) }
                }
            }

            is OperationUiEvent.OnOperationSumChanged -> {
                if (uiEvent.operationSum.isDigitsOnly() && uiEvent.operationSum.length <= 10) {
                    _uiState.update {
                        it.copy(
                            operationSum = uiEvent.operationSum,
                            operationSumError = false
                        )
                    }
                }
            }

            OperationUiEvent.OnBackIconClick -> {
                viewModelScope.launch { _event.send(OperationEvent.CloseScreen) }
            }
        }
    }

    private fun updateSelectedAccount(newAccountId: Int) {
        if (_uiState.value.selectedAccount?.id != newAccountId) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        selectedAccount = accountInteractor.getAccountById(newAccountId),
                        showAccountPickerDialog = false
                    )
                }
            }
        } else {
            _uiState.update { it.copy(showAccountPickerDialog = false) }
        }
    }

    private fun updateSelectedCategory(newCategoryId: Int) {
        if (_uiState.value.selectedCategory?.id != newCategoryId) {
            viewModelScope.launch {
                val newCategoryWithSubcategories = categoryInteractor
                    .getCategoryWithSubcategoriesById(newCategoryId)
                    .filterNotNull()
                    .first()

                _uiState.update {
                    it.copy(
                        selectedCategory = newCategoryWithSubcategories.category,
                        selectedSubcategory = null,
                        subcategories = newCategoryWithSubcategories.subcategories,
                        showCategoryPickerDialog = false,
                        showSubcategoryPicker = true,
                        sign = if (newCategoryWithSubcategories.category.type == OperationType.EXPENSES) "-" else "+"
                    )
                }
            }
        } else {
            _uiState.update { it.copy(showCategoryPickerDialog = false) }
        }
    }

    private fun updateSelectedSubcategory(newSubcategoryId: Int) {
        if (_uiState.value.selectedSubcategory?.id != newSubcategoryId) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        selectedSubcategory = subcategoryInteractor.getSubcategoryById(newSubcategoryId),
                        showSubcategoryPickerDialog = false
                    )
                }
            }
        } else {
            _uiState.update { it.copy(showSubcategoryPickerDialog = false) }
        }
    }

    private fun deleteOperation() {
        viewModelScope.launch {
            operationId?.let {
                operationInteractor.getOperationById(operationId).also { oldOperation ->
                    when (oldOperation.category.type) {
                        OperationType.EXPENSES -> {
                            accountInteractor.addMoneyToAccount(oldOperation.account.id, oldOperation.sum)
                        }

                        else -> {
                            accountInteractor.addMoneyToAccount(oldOperation.account.id, -oldOperation.sum)
                        }
                    }

                    operationInteractor.deleteOperation(oldOperation)
                }

                _uiState.update {
                    it.copy(
                        details = (it.details as OperationDetails.EditOperation).copy(
                            showDeleteOperationDialog = false
                        )
                    )
                }

                _event.send(OperationEvent.CloseScreen)
            }
        }
    }

    private fun saveOperation() {
        if (!validateInput()) return

        val operationSum = _uiState.value.operationSum.toLong()
        val selectedAccount = _uiState.value.selectedAccount!!
        val selectedCategory = _uiState.value.selectedCategory!!
        val selectedSubcategory = _uiState.value.selectedSubcategory
        val selectedDate = _uiState.value.selectedDate.toLocalDate()
        val comment = _uiState.value.comment.trim()

        viewModelScope.launch {
            when (_uiState.value.details) {
                OperationDetails.CreateOperation -> {
                    operationInteractor.addOperation(
                        Operation(
                            id = 0,
                            category = selectedCategory,
                            subcategory = selectedSubcategory,
                            account = selectedAccount,
                            sum = operationSum,
                            date = selectedDate,
                            comment = comment
                        )
                    )
                }

                is OperationDetails.EditOperation -> {
                    operationId?.let {
                        val oldOperation = operationInteractor.getOperationById(operationId)

                        when (oldOperation.category.type) {
                            OperationType.EXPENSES -> {
                                accountInteractor.addMoneyToAccount(oldOperation.account.id, oldOperation.sum)
                            }

                            else -> {
                                accountInteractor.addMoneyToAccount(oldOperation.account.id, -oldOperation.sum)
                            }
                        }

                        operationInteractor.updateOperation(
                            oldOperation.copy(
                                category = selectedCategory,
                                subcategory = selectedSubcategory,
                                account = selectedAccount,
                                sum = operationSum,
                                date = selectedDate,
                                comment = comment
                            )
                        )
                    }
                }

                OperationDetails.Initial -> throw RuntimeException("OperationDetails must be not Initial")
            }

            when (selectedCategory.type) {
                OperationType.EXPENSES -> {
                    accountInteractor.addMoneyToAccount(selectedAccount.id, -operationSum)
                }

                else -> {
                    accountInteractor.addMoneyToAccount(selectedAccount.id, operationSum)
                }
            }

            _event.send(OperationEvent.CloseScreen)
        }
    }

    private fun validateInput(): Boolean {
        val operationSum = _uiState.value.operationSum
        val account = _uiState.value.selectedAccount
        val category = _uiState.value.selectedCategory

        when {
            operationSum.isEmpty() || operationSum.isBlank() || operationSum.toLongOrNull() == null || operationSum.toLongOrNull() == 0L -> {
                _uiState.update { it.copy(operationSumError = true) }
                viewModelScope.launch { _event.send(OperationEvent.RequestOperationSumFocus) }
            }

            account == null -> _uiState.update { it.copy(showAccountPickerDialog = true) }

            category == null -> _uiState.update { it.copy(showCategoryPickerDialog = true) }

            else -> return true
        }

        return false
    }
}