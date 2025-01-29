package com.example.finance.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.finance.data.local.dao.AccountDao
import com.example.finance.data.local.dao.CategoryDao
import com.example.finance.data.local.dao.OperationDao
import com.example.finance.data.local.dao.ReminderDao
import com.example.finance.data.local.dao.SubcategoryDao
import com.example.finance.data.local.dao.TransferDao
import com.example.finance.data.local.database.utils.LocalDateConverter
import com.example.finance.data.local.database.utils.LocalDateTimeConverter
import com.example.finance.data.local.entities.AccountDb
import com.example.finance.data.local.entities.CategoryDb
import com.example.finance.data.local.entities.OperationDb
import com.example.finance.data.local.entities.ReminderDb
import com.example.finance.data.local.entities.SubcategoryDb
import com.example.finance.data.local.entities.TransferDb
import com.example.finance.domain.entities.OperationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Database(
    entities = [AccountDb::class, CategoryDb::class, OperationDb::class, ReminderDb::class, SubcategoryDb::class, TransferDb::class],
    version = 1
)
@TypeConverters(LocalDateConverter::class, LocalDateTimeConverter::class)
abstract class FinanceDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao

    abstract fun categoryDao(): CategoryDao

    abstract fun operationDao(): OperationDao

    abstract fun reminderDao(): ReminderDao

    abstract fun subcategoryDao(): SubcategoryDao

    abstract fun transferDao(): TransferDao

    companion object {
        private const val DATABASE_NAME = "finance.db"

        @Volatile
        private var INSTANCE: FinanceDatabase? = null

        fun getInstance(context: Context): FinanceDatabase =
            INSTANCE ?: synchronized(this) {
                buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context = context,
            klass = FinanceDatabase::class.java,
            name = DATABASE_NAME
        ).addCallback(object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                val prepopulatedAccount = AccountDb(name = "Основной", sum = 0)

                val prepopulatedCategories = listOf(
                    CategoryDb(
                        name = "Продукты",
                        type = OperationType.EXPENSES
                    ),
                    CategoryDb(
                        name = "Развлечения",
                        type = OperationType.EXPENSES
                    ),
                    CategoryDb(
                        name = "Транспорт",
                        type = OperationType.EXPENSES
                    ),
                    CategoryDb(
                        name = "Другое",
                        type = OperationType.EXPENSES
                    ),
                    CategoryDb(
                        name = "Зарплата",
                        type = OperationType.INCOME
                    ),
                    CategoryDb(
                        name = "Другое",
                        type = OperationType.INCOME
                    )
                )

                CoroutineScope(Job() + Dispatchers.IO).launch {
                    val database = getInstance(context)
                    database.accountDao().insert(prepopulatedAccount)
                    database.categoryDao().insertAll(prepopulatedCategories)
                }
            }
        }).build()
    }
}