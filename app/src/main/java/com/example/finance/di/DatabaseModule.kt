package com.example.finance.di

import android.content.Context
import com.example.finance.data.local.dao.AccountDao
import com.example.finance.data.local.dao.CategoryDao
import com.example.finance.data.local.dao.OperationDao
import com.example.finance.data.local.dao.ReminderDao
import com.example.finance.data.local.dao.SubcategoryDao
import com.example.finance.data.local.dao.TransferDao
import com.example.finance.data.local.database.FinanceDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): FinanceDatabase =
        FinanceDatabase.getInstance(context)

    @Singleton
    @Provides
    fun provideAccountDao(db: FinanceDatabase): AccountDao = db.accountDao()

    @Singleton
    @Provides
    fun provideCategoryDao(db: FinanceDatabase): CategoryDao = db.categoryDao()

    @Singleton
    @Provides
    fun provideOperationDao(db: FinanceDatabase): OperationDao = db.operationDao()

    @Singleton
    @Provides
    fun provideReminderDao(db: FinanceDatabase): ReminderDao = db.reminderDao()

    @Singleton
    @Provides
    fun provideSubcategoryDao(db: FinanceDatabase): SubcategoryDao = db.subcategoryDao()

    @Singleton
    @Provides
    fun provideTransferDao(db: FinanceDatabase): TransferDao = db.transferDao()
}