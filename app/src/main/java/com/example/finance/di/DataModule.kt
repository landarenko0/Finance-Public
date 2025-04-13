package com.example.finance.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.work.WorkManager
import com.example.finance.data.datastore.AccountIdRepositoryImpl
import com.example.finance.data.local.dao.AccountDao
import com.example.finance.data.local.dao.CategoryDao
import com.example.finance.data.local.dao.OperationDao
import com.example.finance.data.local.dao.ReminderDao
import com.example.finance.data.local.dao.SubcategoryDao
import com.example.finance.data.local.dao.TransferDao
import com.example.finance.data.local.database.FinanceDatabase
import com.example.finance.data.repository.AccountRepositoryImpl
import com.example.finance.data.repository.CategoryRepositoryImpl
import com.example.finance.data.repository.OperationRepositoryImpl
import com.example.finance.data.repository.ReminderRepositoryImpl
import com.example.finance.data.repository.SubcategoryRepositoryImpl
import com.example.finance.data.repository.TransferRepositoryImpl
import com.example.finance.data.scheduler.ReminderSchedulerImpl
import com.example.finance.domain.datastore.AccountIdRepository
import com.example.finance.domain.repository.AccountRepository
import com.example.finance.domain.repository.CategoryRepository
import com.example.finance.domain.repository.OperationRepository
import com.example.finance.domain.repository.ReminderRepository
import com.example.finance.domain.repository.SubcategoryRepository
import com.example.finance.domain.repository.TransferRepository
import com.example.finance.domain.scheduler.ReminderScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

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

    @Singleton
    @Provides
    fun provideAccountRepository(accountDao: AccountDao): AccountRepository =
        AccountRepositoryImpl(accountDao, Dispatchers.IO)

    @Singleton
    @Provides
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository =
        CategoryRepositoryImpl(categoryDao, Dispatchers.IO)

    @Singleton
    @Provides
    fun provideOperationRepository(operationDao: OperationDao): OperationRepository =
        OperationRepositoryImpl(operationDao, Dispatchers.IO)

    @Singleton
    @Provides
    fun provideReminderRepository(reminderDao: ReminderDao): ReminderRepository =
        ReminderRepositoryImpl(reminderDao, Dispatchers.IO)

    @Singleton
    @Provides
    fun provideSubcategoryRepository(subcategoryDao: SubcategoryDao): SubcategoryRepository =
        SubcategoryRepositoryImpl(subcategoryDao, Dispatchers.IO)

    @Singleton
    @Provides
    fun provideTransferRepository(transferDao: TransferDao): TransferRepository =
        TransferRepositoryImpl(transferDao, Dispatchers.IO)

    @Singleton
    @Provides
    fun provideReminderScheduler(workManager: WorkManager): ReminderScheduler =
        ReminderSchedulerImpl(workManager)

    @Singleton
    @Provides
    fun provideAccountIdRepository(dataStore: DataStore<Preferences>): AccountIdRepository =
        AccountIdRepositoryImpl(dataStore, Dispatchers.IO)
}