package com.example.finance.domain.repository

import com.example.finance.domain.entities.Category
import com.example.finance.domain.entities.CategoryWithSubcategories
import com.example.finance.domain.entities.OperationType
import kotlinx.coroutines.flow.Flow

interface CategoryRepository : BaseRepository<Category> {

    fun getCategoriesByType(type: OperationType): Flow<List<Category>>

    fun getCategoryWithSubcategoriesById(categoryId: Int): Flow<CategoryWithSubcategories?>
}