package com.example.finance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.finance.data.local.entities.CategoryDb
import com.example.finance.data.local.entities.CategoryWithSubcategoriesDb
import com.example.finance.domain.entities.OperationType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao : BaseDao<CategoryDb> {

    @Query("SELECT * FROM categorydb ORDER BY id DESC")
    fun getAll(): Flow<List<CategoryDb>>

    @Query("SELECT * FROM categorydb WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Int): CategoryDb

    @Query("SELECT * FROM categorydb WHERE type = :type")
    fun getCategoriesByType(type: OperationType): Flow<List<CategoryDb>>

    @Transaction
    @Query("SELECT * FROM categorydb WHERE id = :categoryId")
    suspend fun getCategoryWithSubcategoriesById(categoryId: Int): CategoryWithSubcategoriesDb

    @Query("DELETE FROM categorydb WHERE id = :categoryId")
    suspend fun deleteCategoryById(categoryId: Int)
}