package com.example.finance.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.finance.data.local.entities.SubcategoryDb
import kotlinx.coroutines.flow.Flow

@Dao
interface SubcategoryDao : BaseDao<SubcategoryDb> {

    @Query("SELECT * FROM subcategorydb")
    fun getAll(): Flow<List<SubcategoryDb>>

    @Query("SELECT * FROM subcategorydb WHERE id = :subcategoryId")
    suspend fun getSubcategoryById(subcategoryId: Int): SubcategoryDb

    @Query("DELETE FROM subcategorydb WHERE id IN (:subcategoriesIds)")
    suspend fun deleteSubcategoriesByIds(subcategoriesIds: List<Int>)

    @Query("SELECT * FROM subcategorydb WHERE categoryId = :categoryId")
    suspend fun getCategorySubcategories(categoryId: Int): List<SubcategoryDb>
}