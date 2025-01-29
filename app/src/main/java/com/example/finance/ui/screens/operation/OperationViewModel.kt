package com.example.finance.ui.screens.operation

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.finance.domain.entities.Account
import com.example.finance.domain.entities.Operation
import com.example.finance.domain.entities.OperationType
import com.example.finance.domain.usecases.AccountInteractor
import com.example.finance.domain.usecases.CategoryInteractor
import com.example.finance.domain.usecases.OperationInteractor
import com.example.finance.domain.usecases.SubcategoryInteractor
import com.example.finance.ui.navigation.AppScreens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
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

    private val operationScreenStateHandle = savedStateHandle.toRoute<AppScreens.OperationScreen>()

    private val operationId = operationScreenStateHandle.operationId
    private val accountId = operationScreenStateHandle.accountId
    private val categoryId = operationScreenStateHandle.categoryId

    init {
        viewModelScope.launch {
            if (operationId != null) {
                _uiState.value = _uiState.value.copy(details = OperationDetails.EditOperation())

                operationInteractor.getOperationById(operationId).also { operation ->
                    _uiState.update {
                        it.copy(
                            operationSum = operation.sum.toString(),
                            sign = if (operation.category.type == OperationType.EXPENSES) "-" else "+",
                            selectedAccount = operation.account,
                            selectedCategory = operation.category,
                            selectedSubcategory = operation.subcategory,
                            selectedDate = operation.date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                            comment = operation.comment,
                            showSubcategoryPicker = true
                        )
                    }
                }
            } else {
                _uiState.value = _uiState.value.copy(details = OperationDetails.CreateOperation)

                accountId?.let {
                    if (accountId != 0) {
                        _uiState.update {
                            it.copy(selectedAccount = accountInteractor.getAccountById(accountId))
                        }
                    }
                }

                categoryId?.let {
                    _uiState.update {
                        it.copy(selectedCategory = categoryInteractor.getCategoryById(categoryId))
                    }
                }
            }

            val categories = categoryInteractor.getAllCategories().first().reversed()

            _uiState.update { state ->
                state.copy(
                    accounts = accountInteractor.getAllAccounts().first().reversed(),
                    incomeCategories = categories.filter { it.type == OperationType.INCOME },
                    expensesCategories = categories.filter { it.type == OperationType.EXPENSES },
                    subcategories = subcategoryInteractor.getAllSubcategories().first()
                )
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
                        details = (it.details as OperationDetails.EditOperation).copy(showDeleteOperationDialog = false)
                    )
                }
            }

            OperationUiEvent.OnFocusRequested -> {
                _uiState.update { it.copy(requestAccountSumFocus = false) }
            }

            is OperationUiEvent.OnNewAccountSelected -> updateSelectedAccount(uiEvent.newAccountId)

            is OperationUiEvent.OnNewCategorySelected -> updateSelectedCategory(uiEvent.newCategoryId)

            is OperationUiEvent.OnNewDateSelected -> {
                _uiState.update {
                    it.copy(
                        selectedDate = uiEvent.newDate,
                        showDatePickerDialog = false
                    )
                }
            }

            is OperationUiEvent.OnNewSubcategorySelected -> updateSelectedSubcategory(uiEvent.newSubcategoryId)

            OperationUiEvent.OnSaveButtonClick -> saveCategory()

            OperationUiEvent.OnSubcategoryPickerClick -> {
                _uiState.update { it.copy(showSubcategoryPickerDialog = true) }
            }
        }
    }

    fun onOperationSumChanged(operationSum: String) {
        if (operationSum.isDigitsOnly() && operationSum.length <= 10) {
            _uiState.update {
                it.copy(
                    operationSum = operationSum,
                    operationSumError = false
                )
            }
        }
    }

    fun onCommentChanged(comment: String) {
        if (comment.length <= 1000) _uiState.update { it.copy(comment = comment) }
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
                val newCategoryWithSubcategories = categoryInteractor.getCategoryWithSubcategoriesById(newCategoryId)

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
                operationInteractor.deleteOperation(
                    operationInteractor.getOperationById(operationId)
                )

                _uiState.update {
                    it.copy(
                        closeScreen = true,
                        details = (it.details as OperationDetails.EditOperation).copy(
                            showDeleteOperationDialog = false
                        )
                    )
                }
            }
        }
    }

    private fun saveCategory() {
        if (!validateOperation()) return

        val operationSum = _uiState.value.operationSum.toLong()
        val selectedAccount = _uiState.value.selectedAccount!!
        val selectedCategory = _uiState.value.selectedCategory!!
        val selectedSubcategory = _uiState.value.selectedSubcategory
        val selectedDate = Instant.ofEpochMilli(_uiState.value.selectedDate).atZone(ZoneId.systemDefault()).toLocalDate()
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
                        val account = accountInteractor.getAccountById(oldOperation.account.id)

                        when (oldOperation.category.type) {
                            OperationType.EXPENSES -> addMoneyToAccount(account, oldOperation.sum)

                            else -> addMoneyToAccount(account, -oldOperation.sum)
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
                OperationType.EXPENSES -> addMoneyToAccount(selectedAccount, -operationSum)

                else -> addMoneyToAccount(selectedAccount, operationSum)
            }

            _uiState.update { it.copy(closeScreen = true) }
        }
    }

    private fun validateOperation(): Boolean {
        val operationSum = _uiState.value.operationSum
        val account = _uiState.value.selectedAccount
        val category = _uiState.value.selectedCategory

        when {
            operationSum.toLongOrNull() == null || operationSum.isEmpty() || operationSum.isBlank() || operationSum.toLongOrNull() == 0L -> {
                _uiState.update {
                    it.copy(
                        operationSumError = true,
                        requestAccountSumFocus = true
                    )
                }
            }

            account == null -> _uiState.update { it.copy(showAccountPickerDialog = true) }

            category == null -> _uiState.update { it.copy(showCategoryPickerDialog = true) }

            else -> return true
        }

        return false
    }

    private suspend fun addMoneyToAccount(account: Account, sum: Long) {
        accountInteractor.updateAccount(account.copy(sum = account.sum + sum))
    }
}