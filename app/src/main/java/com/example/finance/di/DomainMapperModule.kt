package com.example.finance.di

import com.example.finance.domain.entities.mappers.AccountDbToDomainMapper
import com.example.finance.domain.entities.mappers.CategoryDbToDomainMapper
import com.example.finance.domain.entities.mappers.CategoryWithSubcategoriesDbToDomainMapper
import com.example.finance.domain.entities.mappers.OperationDbExtendedToDomainMapper
import com.example.finance.domain.entities.mappers.ReminderDbToDomainMapper
import com.example.finance.domain.entities.mappers.SubcategoryDbToDomainMapper
import com.example.finance.domain.entities.mappers.TransferDbExtendedToDomainMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainMapperModule {

    @Singleton
    @Provides
    fun provideAccountDbToDomainMapper(): AccountDbToDomainMapper = AccountDbToDomainMapper()

    @Singleton
    @Provides
    fun provideCategoryDbToDomainMapper(): CategoryDbToDomainMapper = CategoryDbToDomainMapper()

    @Singleton
    @Provides
    fun provideCategoryWithSubcategoriesDbToDomainMapper(
        categoryDbToDomainMapper: CategoryDbToDomainMapper,
        subcategoryDbToDomainMapper: SubcategoryDbToDomainMapper
    ): CategoryWithSubcategoriesDbToDomainMapper = CategoryWithSubcategoriesDbToDomainMapper(
        categoryDbToDomainMapper,
        subcategoryDbToDomainMapper
    )

    @Singleton
    @Provides
    fun provideOperationDbExtendedToDomainMapper(
        categoryDbToDomainMapper: CategoryDbToDomainMapper,
        subcategoryDbToDomainMapper: SubcategoryDbToDomainMapper,
        accountDbToDomainMapper: AccountDbToDomainMapper
    ): OperationDbExtendedToDomainMapper =
        OperationDbExtendedToDomainMapper(
            categoryDbToDomainMapper,
            subcategoryDbToDomainMapper,
            accountDbToDomainMapper
        )

    @Singleton
    @Provides
    fun provideReminderDbToDomainMapper(): ReminderDbToDomainMapper = ReminderDbToDomainMapper()

    @Singleton
    @Provides
    fun provideSubcategoryDbToDomainMapper(): SubcategoryDbToDomainMapper =
        SubcategoryDbToDomainMapper()

    @Singleton
    @Provides
    fun provideTransferDbExtendedToDomainMapper(
        accountDbToDomainMapper: AccountDbToDomainMapper
    ): TransferDbExtendedToDomainMapper = TransferDbExtendedToDomainMapper(accountDbToDomainMapper)
}