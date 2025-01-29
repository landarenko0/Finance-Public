package com.example.finance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.finance.data.local.entities.SubcategoryDb
import kotlinx.coroutines.flow.Flow

@Dao
interface SubcategoryDao : BaseDao<SubcategoryDb> {

    @Query("SELECT * FROM subcategorydb ORDER BY id DESC")
    fun getAll(): Flow<List<SubcategoryDb>>

    @Query("SELECT * FROM subcategorydb WHERE id = :subcategoryId")
    suspend fun getSubcategoryById(subcategoryId: Int): SubcategoryDb

    @Query("DELETE FROM subcategorydb WHERE id = :subcategoryId")
    suspend fun deleteSubCategoryById(subcategoryId: Int)

    @Query("DELETE FROM subcategorydb WHERE id IN (:subcategoriesIds)")
    suspend fun deleteSubcategoriesByIds(subcategoriesIds: List<Int>)
}