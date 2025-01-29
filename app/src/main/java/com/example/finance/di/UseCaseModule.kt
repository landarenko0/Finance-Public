package com.example.finance.di

import com.example.finance.domain.repository.AccountRepository
import com.example.finance.domain.repository.CategoryRepository
import com.example.finance.domain.repository.OperationRepository
import com.example.finance.domain.repository.ReminderRepository
import com.example.finance.domain.repository.SubcategoryRepository
import com.example.finance.domain.repository.TransferRepository
import com.example.finance.domain.usecases.AccountInteractor
import com.example.finance.domain.usecases.CategoryInteractor
import com.example.finance.domain.usecases.DeleteUseCase
import com.example.finance.domain.usecases.DeleteObjectByIdUseCase
import com.example.finance.domain.usecases.DeleteOperationsByAccountIdUseCase
import com.example.finance.domain.usecases.DeleteSubcategoriesByIdsUseCase
import com.example.finance.domain.usecases.GetAccountsTotalSumUseCase
import com.example.finance.domain.usecases.GetAllGroupedCategoriesUseCase
import com.example.finance.domain.usecases.GetAllUseCase
import com.example.finance.domain.usecases.GetCategoriesByTypeUseCase
import com.example.finance.domain.usecases.GetCategoryWithSubcategoriesByIdUseCase
import com.example.finance.domain.usecases.GetGroupedCategoriesUseCase
import com.example.finance.domain.usecases.GetObjectByIdUseCase
import com.example.finance.domain.usecases.GetOperationsByCategoryIdUseCase
import com.example.finance.domain.usecases.GetTransfersByAccountsIdAndPeriodUseCase
import com.example.finance.domain.usecases.InsertAllUseCase
import com.example.finance.domain.usecases.InsertUseCase
import com.example.finance.domain.usecases.OperationInteractor
import com.example.finance.domain.usecases.ReminderInteractor
import com.example.finance.domain.usecases.SubcategoryInteractor
import com.example.finance.domain.usecases.TransferInteractor
import com.example.finance.domain.usecases.UpdateUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideAccountInteractor(accountRepository: AccountRepository): AccountInteractor =
        AccountInteractor(
            getAllAccounts = GetAllUseCase(accountRepository),
            getAccountById = GetObjectByIdUseCase(accountRepository),
            addAccount = InsertUseCase(accountRepository),
            addAllAccounts = InsertAllUseCase(accountRepository),
            updateAccount = UpdateUseCase(accountRepository),
            deleteAccount = DeleteUseCase(accountRepository),
            deleteAccountById = DeleteObjectByIdUseCase(accountRepository),
            getAccountsTotalSum = GetAccountsTotalSumUseCase(accountRepository)
        )

    @Singleton
    @Provides
    fun provideCategoryInteractor(categoryRepository: CategoryRepository): CategoryInteractor =
        CategoryInteractor(
            getAllCategories = GetAllUseCase(categoryRepository),
            getCategoryById = GetObjectByIdUseCase(categoryRepository),
            addCategory = InsertUseCase(categoryRepository),
            addAllCategories = InsertAllUseCase(categoryRepository),
            updateCategory = UpdateUseCase(categoryRepository),
            deleteCategory = DeleteUseCase(categoryRepository),
            deleteCategoryById = DeleteObjectByIdUseCase(categoryRepository),
            getCategoriesByType = GetCategoriesByTypeUseCase(categoryRepository),
            getCategoryWithSubcategoriesById = GetCategoryWithSubcategoriesByIdUseCase(
                categoryRepository
            )
        )

    @Singleton
    @Provides
    fun provideOperationInteractor(operationRepository: OperationRepository): OperationInteractor =
        OperationInteractor(
            getAllOperations = GetAllUseCase(operationRepository),
            getOperationById = GetObjectByIdUseCase(operationRepository),
            addOperation = InsertUseCase(operationRepository),
            addAllOperations = InsertAllUseCase(operationRepository),
            updateOperation = UpdateUseCase(operationRepository),
            deleteOperation = DeleteUseCase(operationRepository),
            deleteOperationById = DeleteObjectByIdUseCase(operationRepository),
            getGroupedCategories = GetGroupedCategoriesUseCase(operationRepository),
            getOperationsByCategoryId = GetOperationsByCategoryIdUseCase(operationRepository),
            deleteOperationsByAccountId = DeleteOperationsByAccountIdUseCase(operationRepository),
            getAllGroupedCategories = GetAllGroupedCategoriesUseCase(operationRepository)
        )

    @Singleton
    @Provides
    fun provideReminderInteractor(reminderRepository: ReminderRepository): ReminderInteractor =
        ReminderInteractor(
            getAllReminders = GetAllUseCase(reminderRepository),
            getReminderById = GetObjectByIdUseCase(reminderRepository),
            addReminder = InsertUseCase(reminderRepository),
            addAllReminders = InsertAllUseCase(reminderRepository),
            updateReminder = UpdateUseCase(reminderRepository),
            deleteReminder = DeleteUseCase(reminderRepository),
            deleteReminderById = DeleteObjectByIdUseCase(reminderRepository)
        )

    @Singleton
    @Provides
    fun provideSubcategoryInteractor(
        subcategoryRepository: SubcategoryRepository
    ): SubcategoryInteractor = SubcategoryInteractor(
        getAllSubcategories = GetAllUseCase(subcategoryRepository),
        getSubcategoryById = GetObjectByIdUseCase(subcategoryRepository),
        addSubcategory = InsertUseCase(subcategoryRepository),
        addAllSubcategories = InsertAllUseCase(subcategoryRepository),
        updateSubcategory = UpdateUseCase(subcategoryRepository),
        deleteSubcategory = DeleteUseCase(subcategoryRepository),
        deleteSubcategoryById = DeleteObjectByIdUseCase(subcategoryRepository),
        deleteSubcategoriesByIds = DeleteSubcategoriesByIdsUseCase(subcategoryRepository)
    )

    @Singleton
    @Provides
    fun provideTransferInteractor(transferRepository: TransferRepository): TransferInteractor =
        TransferInteractor(
            getAllTransfers = GetAllUseCase(transferRepository),
            getTransferById = GetObjectByIdUseCase(transferRepository),
            addTransfer = InsertUseCase(transferRepository),
            addAllTransfers = InsertAllUseCase(transferRepository),
            updateTransfer = UpdateUseCase(transferRepository),
            deleteTransfer = DeleteUseCase(transferRepository),
            deleteTransferById = DeleteObjectByIdUseCase(transferRepository),
            getTransfersByAccountsIdAndPeriod = GetTransfersByAccountsIdAndPeriodUseCase(
                transferRepository
            )
        )
}