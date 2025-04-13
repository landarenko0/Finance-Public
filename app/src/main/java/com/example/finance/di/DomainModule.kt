package com.example.finance.di

import com.example.finance.domain.datastore.AccountIdRepository
import com.example.finance.domain.repository.AccountRepository
import com.example.finance.domain.repository.CategoryRepository
import com.example.finance.domain.repository.OperationRepository
import com.example.finance.domain.repository.ReminderRepository
import com.example.finance.domain.repository.SubcategoryRepository
import com.example.finance.domain.repository.TransferRepository
import com.example.finance.domain.scheduler.ReminderScheduler
import com.example.finance.domain.usecases.AccountInteractor
import com.example.finance.domain.usecases.AddMoneyToAccountUseCase
import com.example.finance.domain.usecases.CancelReminderUseCase
import com.example.finance.domain.usecases.CategoryInteractor
import com.example.finance.domain.usecases.CheckAccountNameCollisionExceptUseCase
import com.example.finance.domain.usecases.CheckAccountNameCollisionUseCase
import com.example.finance.domain.usecases.CheckCategoryNameCollisionExceptUseCase
import com.example.finance.domain.usecases.CheckCategoryNameCollisionUseCase
import com.example.finance.domain.usecases.CheckReminderNameCollisionExceptUseCase
import com.example.finance.domain.usecases.CheckReminderNameCollisionUseCase
import com.example.finance.domain.usecases.CheckSubcategoryNameCollisionExceptUseCase
import com.example.finance.domain.usecases.CheckSubcategoryNameCollisionUseCase
import com.example.finance.domain.usecases.DeleteSubcategoriesByIdsUseCase
import com.example.finance.domain.usecases.DeleteUseCase
import com.example.finance.domain.usecases.FlowAccountByIdUseCase
import com.example.finance.domain.usecases.FlowTotalAccountUseCase
import com.example.finance.domain.usecases.GetAccountsTotalSumUseCase
import com.example.finance.domain.usecases.GetAllGroupedCategoriesUseCase
import com.example.finance.domain.usecases.GetAllUseCase
import com.example.finance.domain.usecases.GetCategoriesByTypeUseCase
import com.example.finance.domain.usecases.GetCategorySubcategoriesUseCase
import com.example.finance.domain.usecases.GetCategoryWithSubcategoriesByIdUseCase
import com.example.finance.domain.usecases.GetGroupedCategoriesUseCase
import com.example.finance.domain.usecases.GetObjectByIdUseCase
import com.example.finance.domain.usecases.GetOperationsByCategoryAndAccountUseCase
import com.example.finance.domain.usecases.GetOperationsByCategoryUseCase
import com.example.finance.domain.usecases.GetSavedAccountIdUseCase
import com.example.finance.domain.usecases.GetTransferByPeriodUseCase
import com.example.finance.domain.usecases.GetTransfersByAccountsIdAndPeriodUseCase
import com.example.finance.domain.usecases.InsertUseCase
import com.example.finance.domain.usecases.OperationInteractor
import com.example.finance.domain.usecases.ReminderInteractor
import com.example.finance.domain.usecases.ScheduleReminderUseCase
import com.example.finance.domain.usecases.SubcategoryInteractor
import com.example.finance.domain.usecases.TransferInteractor
import com.example.finance.domain.usecases.TransferMoneyFromOneAccountToAnotherUseCase
import com.example.finance.domain.usecases.UpdateSavedAccountIdUseCase
import com.example.finance.domain.usecases.UpdateUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Singleton
    @Provides
    fun provideAccountInteractor(
        accountRepository: AccountRepository,
        accountIdRepository: AccountIdRepository
    ): AccountInteractor =
        AccountInteractor(
            getAllAccounts = GetAllUseCase(accountRepository),
            getAccountById = GetObjectByIdUseCase(accountRepository),
            addAccount = InsertUseCase(accountRepository),
            updateAccount = UpdateUseCase(accountRepository),
            deleteAccount = DeleteUseCase(accountRepository),
            getAccountsTotalSum = GetAccountsTotalSumUseCase(accountRepository),
            transferMoneyFromOneAccountToAnother = TransferMoneyFromOneAccountToAnotherUseCase(
                accountRepository
            ),
            checkAccountNameCollision = CheckAccountNameCollisionUseCase(accountRepository),
            checkAccountNameCollisionExcept = CheckAccountNameCollisionExceptUseCase(
                accountRepository
            ),
            addMoneyToAccount = AddMoneyToAccountUseCase(accountRepository),
            getSavedAccountId = GetSavedAccountIdUseCase(accountIdRepository),
            updateSavedAccountId = UpdateSavedAccountIdUseCase(accountIdRepository),
            flowAccountById = FlowAccountByIdUseCase(accountRepository),
            flowTotalAccount = FlowTotalAccountUseCase(accountRepository)
        )

    @Singleton
    @Provides
    fun provideCategoryInteractor(categoryRepository: CategoryRepository): CategoryInteractor =
        CategoryInteractor(
            getCategoryById = GetObjectByIdUseCase(categoryRepository),
            addCategory = InsertUseCase(categoryRepository),
            updateCategory = UpdateUseCase(categoryRepository),
            deleteCategory = DeleteUseCase(categoryRepository),
            getCategoriesByType = GetCategoriesByTypeUseCase(categoryRepository),
            getCategoryWithSubcategoriesById = GetCategoryWithSubcategoriesByIdUseCase(
                categoryRepository
            ),
            checkCategoryNameCollision = CheckCategoryNameCollisionUseCase(categoryRepository),
            checkCategoryNameCollisionExcept = CheckCategoryNameCollisionExceptUseCase(
                categoryRepository
            )
        )

    @Singleton
    @Provides
    fun provideOperationInteractor(operationRepository: OperationRepository): OperationInteractor =
        OperationInteractor(
            getOperationById = GetObjectByIdUseCase(operationRepository),
            addOperation = InsertUseCase(operationRepository),
            updateOperation = UpdateUseCase(operationRepository),
            deleteOperation = DeleteUseCase(operationRepository),
            getGroupedCategories = GetGroupedCategoriesUseCase(operationRepository),
            getOperationsByCategoryAndAccount = GetOperationsByCategoryAndAccountUseCase(
                operationRepository
            ),
            getOperationsByCategory = GetOperationsByCategoryUseCase(operationRepository),
            getAllGroupedCategories = GetAllGroupedCategoriesUseCase(operationRepository)
        )

    @Singleton
    @Provides
    fun provideReminderInteractor(
        reminderRepository: ReminderRepository,
        reminderScheduler: ReminderScheduler
    ): ReminderInteractor =
        ReminderInteractor(
            getAllReminders = GetAllUseCase(reminderRepository),
            getReminderById = GetObjectByIdUseCase(reminderRepository),
            addReminder = InsertUseCase(reminderRepository),
            updateReminder = UpdateUseCase(reminderRepository),
            deleteReminder = DeleteUseCase(reminderRepository),
            checkReminderNameCollision = CheckReminderNameCollisionUseCase(reminderRepository),
            checkReminderNameCollisionExcept = CheckReminderNameCollisionExceptUseCase(
                reminderRepository
            ),
            scheduleReminder = ScheduleReminderUseCase(
                reminderRepository,
                reminderScheduler
            ),
            cancelReminder = CancelReminderUseCase(
                reminderRepository,
                reminderScheduler
            )
        )

    @Singleton
    @Provides
    fun provideSubcategoryInteractor(
        subcategoryRepository: SubcategoryRepository
    ): SubcategoryInteractor = SubcategoryInteractor(
        getSubcategoryById = GetObjectByIdUseCase(subcategoryRepository),
        addSubcategory = InsertUseCase(subcategoryRepository),
        updateSubcategory = UpdateUseCase(subcategoryRepository),
        deleteSubcategoriesByIds = DeleteSubcategoriesByIdsUseCase(subcategoryRepository),
        checkSubcategoryNameCollision = CheckSubcategoryNameCollisionUseCase(subcategoryRepository),
        checkSubcategoryNameCollisionExcept = CheckSubcategoryNameCollisionExceptUseCase(
            subcategoryRepository
        ),
        getCategorySubcategories = GetCategorySubcategoriesUseCase(subcategoryRepository)
    )

    @Singleton
    @Provides
    fun provideTransferInteractor(transferRepository: TransferRepository): TransferInteractor =
        TransferInteractor(
            getTransferById = GetObjectByIdUseCase(transferRepository),
            addTransfer = InsertUseCase(transferRepository),
            updateTransfer = UpdateUseCase(transferRepository),
            deleteTransfer = DeleteUseCase(transferRepository),
            getTransfersByAccountAndPeriod = GetTransfersByAccountsIdAndPeriodUseCase(
                transferRepository
            ),
            getTransfersByPeriod = GetTransferByPeriodUseCase(transferRepository)
        )
}