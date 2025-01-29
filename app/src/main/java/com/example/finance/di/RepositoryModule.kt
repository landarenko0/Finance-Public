package com.example.finance.di

import com.example.finance.data.local.dao.AccountDao
import com.example.finance.data.local.dao.CategoryDao
import com.example.finance.data.local.dao.OperationDao
import com.example.finance.data.local.dao.ReminderDao
import com.example.finance.data.local.dao.SubcategoryDao
import com.example.finance.data.local.dao.TransferDao
import com.example.finance.data.local.entities.mappers.AccountDomainToDbMapper
import com.example.finance.data.local.entities.mappers.CategoryDomainToDbMapper
import com.example.finance.data.local.entities.mappers.OperationDomainToDbMapper
import com.example.finance.data.local.entities.mappers.ReminderDomainToDbMapper
import com.example.finance.data.local.entities.mappers.SubcategoryDomainToDbMapper
import com.example.finance.data.local.entities.mappers.TransferDomainToDbMapper
import com.example.finance.data.repository.AccountRepositoryImpl
import com.example.finance.data.repository.CategoryRepositoryImpl
import com.example.finance.data.repository.OperationRepositoryImpl
import com.example.finance.data.repository.ReminderRepositoryImpl
import com.example.finance.data.repository.SubcategoryRepositoryImpl
import com.example.finance.data.repository.TransferRepositoryImpl
import com.example.finance.domain.entities.mappers.AccountDbToDomainMapper
import com.example.finance.domain.entities.mappers.CategoryDbToDomainMapper
import com.example.finance.domain.entities.mappers.CategoryWithSubcategoriesDbToDomainMapper
import com.example.finance.domain.entities.mappers.OperationDbExtendedToDomainMapper
import com.example.finance.domain.entities.mappers.ReminderDbToDomainMapper
import com.example.finance.domain.entities.mappers.SubcategoryDbToDomainMapper
import com.example.finance.domain.entities.mappers.TransferDbExtendedToDomainMapper
import com.example.finance.domain.repository.AccountRepository
import com.example.finance.domain.repository.CategoryRepository
import com.example.finance.domain.repository.OperationRepository
import com.example.finance.domain.repository.ReminderRepository
import com.example.finance.domain.repository.SubcategoryRepository
import com.example.finance.domain.repository.TransferRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAccountRepository(
        accountDao: AccountDao,
        accountDbToDomainMapper: AccountDbToDomainMapper,
        accountDomainToDbMapper: AccountDomainToDbMapper
    ): AccountRepository =
        AccountRepositoryImpl(accountDao, accountDbToDomainMapper, accountDomainToDbMapper)

    @Singleton
    @Provides
    fun provideCategoryRepository(
        categoryDao: CategoryDao,
        categoryDbToDomainMapper: CategoryDbToDomainMapper,
        categoryDomainToDbMapper: CategoryDomainToDbMapper,
        categoryWithSubcategoriesDbToDomainMapper: CategoryWithSubcategoriesDbToDomainMapper
    ): CategoryRepository = CategoryRepositoryImpl(
        categoryDao,
        categoryDbToDomainMapper,
        categoryDomainToDbMapper,
        categoryWithSubcategoriesDbToDomainMapper
    )

    @Singleton
    @Provides
    fun provideOperationRepository(
        operationDao: OperationDao,
        operationDomainToDbMapper: OperationDomainToDbMapper,
        operationDbExtendedToDomainMapper: OperationDbExtendedToDomainMapper
    ): OperationRepository = OperationRepositoryImpl(
        operationDao,
        operationDomainToDbMapper,
        operationDbExtendedToDomainMapper
    )

    @Singleton
    @Provides
    fun provideReminderRepository(
        reminderDao: ReminderDao,
        reminderDbToDomainMapper: ReminderDbToDomainMapper,
        reminderDomainToDbMapper: ReminderDomainToDbMapper
    ): ReminderRepository =
        ReminderRepositoryImpl(reminderDao, reminderDbToDomainMapper, reminderDomainToDbMapper)

    @Singleton
    @Provides
    fun provideSubcategoryRepository(
        subcategoryDao: SubcategoryDao,
        subcategoryDbToDomainMapper: SubcategoryDbToDomainMapper,
        subcategoryDomainToDbMapper: SubcategoryDomainToDbMapper
    ): SubcategoryRepository = SubcategoryRepositoryImpl(
        subcategoryDao,
        subcategoryDbToDomainMapper,
        subcategoryDomainToDbMapper
    )

    @Singleton
    @Provides
    fun provideTransferRepository(
        transferDao: TransferDao,
        transferDomainToDbMapper: TransferDomainToDbMapper,
        transferDbExtendedToDomainMapper: TransferDbExtendedToDomainMapper
    ): TransferRepository = TransferRepositoryImpl(
        transferDao,
        transferDomainToDbMapper,
        transferDbExtendedToDomainMapper
    )
}