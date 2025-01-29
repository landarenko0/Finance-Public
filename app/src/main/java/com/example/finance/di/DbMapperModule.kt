package com.example.finance.di

import com.example.finance.data.local.entities.mappers.AccountDomainToDbMapper
import com.example.finance.data.local.entities.mappers.CategoryDomainToDbMapper
import com.example.finance.data.local.entities.mappers.OperationDomainToDbMapper
import com.example.finance.data.local.entities.mappers.ReminderDomainToDbMapper
import com.example.finance.data.local.entities.mappers.SubcategoryDomainToDbMapper
import com.example.finance.data.local.entities.mappers.TransferDomainToDbMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbMapperModule {

    @Singleton
    @Provides
    fun provideAccountDomainToDbMapper(): AccountDomainToDbMapper = AccountDomainToDbMapper()

    @Singleton
    @Provides
    fun provideCategoryDomainToDbMapper(): CategoryDomainToDbMapper = CategoryDomainToDbMapper()

    @Singleton
    @Provides
    fun provideOperationDomainToDbMapper(): OperationDomainToDbMapper = OperationDomainToDbMapper()

    @Singleton
    @Provides
    fun provideReminderDomainToDbMapper(): ReminderDomainToDbMapper = ReminderDomainToDbMapper()

    @Singleton
    @Provides
    fun provideSubcategoryDomainToDbMapper(): SubcategoryDomainToDbMapper =
        SubcategoryDomainToDbMapper()

    @Singleton
    @Provides
    fun provideTransferDomainToDbMapper(): TransferDomainToDbMapper = TransferDomainToDbMapper()
}